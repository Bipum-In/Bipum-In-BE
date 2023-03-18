package com.sparta.bipuminbe.common.sse.service;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseDto;
import com.sparta.bipuminbe.common.sse.entity.Notification;
import com.sparta.bipuminbe.common.sse.repository.EmitterRepository;
import com.sparta.bipuminbe.common.sse.repository.NotificationRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    // 연결 유지시간 20분. 만료 시 재연결
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 20;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final RequestsRepository requestsRepository;

    //시간이 포함된 아이디 생성. SseEmitter 구분을 위함
    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        // lastEventId가 있을 경우, userId와 비교해서 유실된 데이터일 경우 재전송할 수 있다.

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        //시간이 만료된 경우 자동으로 레포지토리에서 삭제하고 클라이언트에서 재요청을 보낸다.
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        //Dummy 데이터를 보내 503에러 방지. (SseEmitter 유효시간 동안 어느 데이터도 전송되지 않으면 503에러 발생)
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방한다.
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(Long userId){
        return userId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
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

    //알림을 구성하고, 알림에 대한 이벤트를 발생시킴. 유저이름. 요청 카테고리. 요청 uri
    // content는 send하는 곳에서 만들어서 전달함.
    // ~님이 ~를 요청하셨습니다. (관리자) or ~님의 ~ 요청이 처리되었습니다. (유저)
    @Transactional
//    public void send(User receiver, String content, String url) {
    public void send(Long requestsId, Boolean isAccepted, String url) {
        Requests request = requestsRepository.findById(requestsId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundRequest)
        );
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 어떤 요청이 들어오는지 알 수 없음.. 방법 찾아야함 각 수리, 반납, 비품
        // Request의 id명이 같음. 구별해서 받을 수 있어야함. isAccepted 값을 기준으로? 메시지를 작성한다.
        User receiver = request.getUser();
        String content = "";

        Notification notification = notificationRepository.save(createNotification(receiver, content, url));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDto.of(notification));
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
}
