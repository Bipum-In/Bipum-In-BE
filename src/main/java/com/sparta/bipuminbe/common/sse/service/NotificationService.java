package com.sparta.bipuminbe.common.sse.service;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseDto;
import com.sparta.bipuminbe.common.sse.entity.Notification;
import com.sparta.bipuminbe.common.sse.repository.EmitterRepository;
import com.sparta.bipuminbe.common.sse.repository.NotificationRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    // 연결 유지시간 20분. 만료 시 재연결
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 20;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final RequestsRepository requestsRepository;

    //시간이 포함된 아이디 생성. SseEmitter 구분을 위함
    @Transactional
    public SseEmitter subscribe(Long userId, String lastEventId) {


        String emitterId = makeTimeIncludeId(userId);
        // lastEventId가 있을 경우, userId와 비교해서 유실된 데이터일 경우 재전송할 수 있다.
        log.info("save 전");
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("save 후");
        log.info("userId : " + userId);
        log.info("emitterId " + emitterId);
        log.info("lastEventId : " + lastEventId);

        //시간이 만료된 경우 자동으로 레포지토리에서 삭제하고 클라이언트에서 재요청을 보낸다.
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        log.info("onCompletion 후");
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        log.info("Timeout 후");
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        //Dummy 데이터를 보내 503에러 방지. (SseEmitter 유효시간 동안 어느 데이터도 전송되지 않으면 503에러 발생)
        String eventId = makeTimeIncludeId(userId);
        log.info("subscribe5");
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");
        log.info("subscribe6");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방한다.
        if (hasLostData(lastEventId)) {
            log.info("subscribe7");
            sendLostData(lastEventId, userId, emitterId, emitter);
            log.info("subscribe8");
        }
        log.info("subscribe9");
        return emitter;
    }

    private String makeTimeIncludeId(Long userId){
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

    @Transactional
//    public void send(User receiver, String content, String url) {
    public void send(Long requestsId, AcceptResult isAccepted, String uri) {
        Requests request = requestsRepository.findById(requestsId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest)
        );

        User receiver = request.getUser();

        String content = createMessage(request, receiver, isAccepted);

        uri = uri + requestsId;
        Notification notification = notificationRepository.save(createNotification(receiver, content, uri));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);

        for(String key : emitters.keySet()){
            SseEmitter emitter = emitters.get(key);
            log.info("============");
            log.info("emitter : " + emitter);
            log.info("key : " + key);
            log.info("============");
        }
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    log.info("emitters 안쪽 eventId : " + eventId);
                    sendNotification(emitter, eventId, key, NotificationResponseDto.of(notification).getContent());
                }
        );
    }

    private Notification createNotification(User receiver, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }

    private String createMessage(Requests request, User receiver, AcceptResult isAccepted) {
        String categoryName = request.getCategory() == null ?
                request.getSupply().getCategory().getCategoryName() :
                request.getCategory().getCategoryName();

        // 승인 건
        if (isAccepted.name().equals("ACCEPT")) {
            return receiver.getEmpName() + " 님의 "
                    + categoryName + " "
                    + request.getRequestType().getKorean() + " 이 승인되었습니다.";
        }
        // 거부 건
        if (isAccepted.name().equals("DECLINE")) {
            return receiver.getEmpName() + " 님의 "
                    + categoryName + " "
                    + request.getRequestType().getKorean() + " 이 반려되었습니다.";
        }
        // 수리 요청 >> 폐기 처리 건
        return receiver.getEmpName() + " 님의 "
                + request.getSupply().getModelName() + " "
                + categoryName
                + " 수리 요청 건이 폐기 승인되었습니다.";
    }
}
