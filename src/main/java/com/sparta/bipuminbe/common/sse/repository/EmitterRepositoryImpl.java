package com.sparta.bipuminbe.common.sse.repository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
@NoArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository{
    //DB에 저장하지 않고, Map에 저장하고 꺼내는 방식
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    //save - Emitter를 저장한다. Emitter = 이벤트를 생성하는 메소드
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);

        for(Map.Entry<String, SseEmitter> entrySet : emitters.entrySet()){
            log.info(entrySet.getKey() + " : " + entrySet.getValue());
        }
        return sseEmitter;
    }

    //saveEventCache - 이벤트를 저장한다. Cache = 첫 요청 시 특정 위치에 복사본을 저장하는 것
    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    //회원과 관련된 모든 Emitter를 찾아온다.
    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //회원과 관련된 모든 이벤트를 찾는다.
    @Override
    public Map<String, Object> findAllEventCacheStartWithByUserId(String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //Emitter를 삭제한다
    @Override
    public void deleteById(String id) {
        log.info(id + " 삭제");
        emitters.remove(id);
    }
    //회원과 관련된 모든 Emitter를 지운다
    @Override
    public void deleteAllEmitterStartWithId(String userId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(userId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    //회원과 관련된 모든 이벤트를 지운다.
    @Override
    public void deleteAllEventCacheStartWithId(String userId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(userId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
