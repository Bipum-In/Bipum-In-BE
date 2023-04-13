package com.sparta.bipuminbe.common.sse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.NotificationType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseDto;
import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.sse.repository.EmitterRepository;
import com.sparta.bipuminbe.common.sse.repository.NotificationRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    // 연결 유지시간 20분. 만료 시 재연결
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 20;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final RequestsRepository requestsRepository;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    //시간이 포함된 아이디 생성. SseEmitter 구분을 위함
    @Transactional
    public SseEmitter subscribe(Long userId, String lastEventId) {

        String emitterId = makeTimeIncludeId(userId);
        // lastEventId가 있을 경우, userId와 비교해서 유실된 데이터일 경우 재전송할 수 있다.

        emitterRepository.deleteAllEmitterStartWithId(String.valueOf(userId));

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> {
            log.info("SSE 연결 Complete");
            emitterRepository.deleteById(emitterId);
//            onClientDisconnect(emitter, "Compeletion");
        });
        //시간이 만료된 경우 자동으로 레포지토리에서 삭제하고 클라이언트에서 재요청을 보낸다.
        emitter.onTimeout(() -> {
            log.info("SSE 연결 Timeout");
            emitterRepository.deleteById(emitterId);
//            onClientDisconnect(emitter, "Timeout");
        });
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));
        //Dummy 데이터를 보내 503에러 방지. (SseEmitter 유효시간 동안 어느 데이터도 전송되지 않으면 503에러 발생)
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방한다.
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            log.info("eventId : " + eventId);
            log.info("data" + data);
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(String.valueOf(data)));

        } catch (IOException exception) {
            log.info("예외 발생해서 emitter 삭제됨");
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    // 유실된 데이터 다시 보내기
    private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(memberId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    // 관리자가 요청을 처리하면, 유저들에게 알림을 보낸다.
    @Transactional
    public void sendForUser(User sender, Long requestsId, AcceptResult isAccepted) {
        Requests request = requestsRepository.findById(requestsId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest)
        );
        // 강제 생성 시, sender는 관리자, receiver는 할당된 사람

        // 알림에 담을 내용
        User receiver = request.getUser();
        String content = createForUserMessage(request, receiver, isAccepted);

        // 관리자가 직접 유저를 배정한 건은 승인으로 간주함
        if (isAccepted == AcceptResult.ASSIGN) {
            isAccepted = AcceptResult.ACCEPT;
        }

        Notification notification = notificationRepository.save(createNotification(sender, receiver, content, request, NotificationType.PROCESSED, isAccepted));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();


        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);

//        for(String key : emitters.keySet()){
//            SseEmitter emitter = emitters.get(key);
//            log.info("============");
//            log.info("emitter : " + emitter);
//            log.info("key : " + key);
//            log.info("============");
//        }

        log.info("sender : " + sender.getUsername());
        // 관리자 사진, 메시지랑, 시간
        //이미지 바이트로 변환
        String finalJsonResult = convertToJson(sender, notification);

        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    log.info("emitters 안쪽 eventId : " + eventId);
                    sendNotification(emitter, eventId, key, finalJsonResult);
                }
        );
    }

    //     유저가 요청을 보내면 관리자들에게 알림을 보낸다.
    @Transactional
    public void sendForAdmin(Long requestsId, User sender) {
        Requests request = requestsRepository.findById(requestsId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest)
        );

        // 알림에 담을 내용
        String content = creatForAdminMessage(request, sender);

        // Role이 Admin인 유저를 조회한다.
        List<User> receiverList = userRepository.findByRoleAndAlarmAndDeletedFalse(UserRoleEnum.ADMIN, true);

        // 각 Admin 마다 알림을 전송한다.
        for (User receiver : receiverList) {
            Notification notification = notificationRepository.save(createNotification(sender, receiver, content, request, NotificationType.REQUEST, null));
            String receiverId = String.valueOf(receiver.getId());
            String eventId = receiverId + "_" + System.currentTimeMillis();

            // Emitter는 유저가 로그인하면 바로 구독할 것이다. 그럼 Admin 의 Emitter도 있을듯 본인 것만 보내기 때문에 노상관
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);

            String finalJsonResult = convertToJson(sender, notification);

            emitters.forEach(
                    (key, emitter) -> {
                        emitterRepository.saveEventCache(key, notification);
                        log.info("emitters 안쪽 eventId : " + eventId);
                        sendNotification(emitter, eventId, key, finalJsonResult);
                    }
            );
        }
    }

    private Notification createNotification(User sender, User receiver, String content,
                                            Requests request, NotificationType notificationType, AcceptResult isAccepted) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .isRead(false)
                .request(request)
                .notificationType(notificationType)
                .acceptResult(isAccepted)
                .build();
    }

    private String createForUserMessage(Requests request, User receiver, AcceptResult isAccepted) {
        String categoryName = getCategoryName(request);

        // 승인 건
        if (isAccepted.name().equals("ACCEPT")) {
            return receiver.getEmpName() + " 님의 "
                    + categoryName + " "
                    + request.getRequestType().getKorean() + "이 승인되었습니다.";
        }
        // 거부 건
        if (isAccepted.name().equals("DECLINE")) {
            return receiver.getEmpName() + " 님의 "
                    + categoryName + " "
                    + request.getRequestType().getKorean() + "이 반려되었습니다.";
        }

        // 관리자가 직접 비품의 유저를 설정한 건
        if (isAccepted.name().equals("ASSIGN") && request.getRequestType().name().equals("SUPPLY")) {
            return "관리자에 의해 " + request.getSupply().getModelName() + " "
                    + categoryName + " 비품이 배정되었습니다.";
        }

        // 관리자가 배정된 비품을 다른 사용자에게 지급할 때 기존사용자에게 보낼 메시지
        if (isAccepted.name().equals("ASSIGN") && request.getRequestType().name().equals("RETURN")) {
            return "관리자에 의해 " + request.getSupply().getModelName() + " "
                    + categoryName + " 비품이 반납되었습니다.";
        }

        // 관리자가 비품을 폐기할 때, 기존 사용자에게 보낼 메시지
        if (isAccepted.name().equals("ASSIGN") && request.getSupply().getDeleted()) {
            return "관리자에 의해 " + request.getSupply().getModelName() + " "
                    + categoryName + " 비품이 폐기되었습니다.";
        }
        // 수리 요청 >> 폐기 처리 건
        return receiver.getEmpName() + " 님의 "
                + request.getSupply().getModelName() + " "
                + categoryName
                + " 비품이 폐기처리되었습니다.";
    }

    private String creatForAdminMessage(Requests request, User sender) {
        String categoryName = getCategoryName(request);
        String requestType = request.getRequestType().getKorean();

        if (requestType.equals("보고서 결재")) {
            return sender.getEmpName() + " 님이 " + categoryName + " "
                    + requestType + "를 요청하셨습니다.";
        }

        // ~~ 님이 ~~카테고리 ~~를 요청하셨습니다.
        return sender.getEmpName() + " 님의 " +
                categoryName + " " + requestType + "이 등록되었습니다.";
    }

    private String getCategoryName(Requests request) {
        return request.getCategory() == null ?
                request.getSupply().getCategory().getCategoryName() :
                request.getCategory().getCategoryName();
    }

    private String convertToJson(User sender, Notification notification) {
        String jsonResult = "";

        NotificationResponseDto notificationResponseDto = NotificationResponseDto.of(notification, sender.getImage());

        try {
            jsonResult = objectMapper.writeValueAsString(notificationResponseDto);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JsonConvertError);
        }

        return jsonResult;
    }

    // 클라이언트 타임아웃 처리
    private void onClientDisconnect(SseEmitter emitter, String type) {
        try {
            emitter.send(SseEmitter.event().name(type).data("Client" + type).id(UUID.randomUUID().toString()));
            emitter.complete();
        } catch (IOException e) {
            log.error("Failed to send" + type + "event to client", e);
        }
    }

    // 처리 된 건이고, 읽은 알림은 삭제 (1일 경과한 데이터)
    @Transactional
    public void deleteOldNotification() {
        List<Notification> notifications = notificationRepository.findOldNotification();

        log.info("총 " + notifications.size() + " 건의 알림 삭제");
        for (Notification notification : notifications) {
            notificationRepository.deleteById(notification.getId());
        }
    }

    @Transactional
    public ResponseDto<String> deleteNotifications(User user) {
        notificationRepository.deleteAll(notificationRepository.findByReceiver(user));
        return ResponseDto.success("알림 삭제 완료.");
    }


    @Transactional(readOnly = true)
    public ResponseDto<Long> countNotifications(User user, UserRoleEnum role) {
        return ResponseDto.success(notificationRepository.countByReceiver_IdAndNotificationTypeAndIncludeCountTrue(user.getId(),
                role == UserRoleEnum.ADMIN ? NotificationType.REQUEST : NotificationType.PROCESSED));
    }


    @Transactional(readOnly = true)
    public ResponseDto<String> resetCount(User user, UserRoleEnum role) {
        List<Notification> notificationList = notificationRepository.findByReceiver_IdAndNotificationTypeAndIncludeCountTrue(user.getId(),
                role == UserRoleEnum.ADMIN ? NotificationType.REQUEST : NotificationType.PROCESSED);
        for (Notification notification : notificationList) {
            notification.notCount();
        }
        return ResponseDto.success("알림 카운트 초기화 완료.");
    }
}
