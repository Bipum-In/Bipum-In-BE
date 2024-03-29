package com.sparta.bipuminbe.common.sse.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.sse.dto.NotificationCountResponseDto;
import com.sparta.bipuminbe.common.sse.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "SSE 연결", description = "로그인 직후 SSE 연결해야합니다!")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.subscribe(userDetails.getUser().getId(), lastEventId);
    }

    @Scheduled(cron = "0 0 0 1/1 * ? *") // 매일 자정 실행한다.
    public void deleteOldNotification() {
        notificationService.deleteOldNotification();
    }

    @Operation(summary = "알림 전체 s삭제", description = "읽었든 안읽었든 삭제합니다.")
    @DeleteMapping(value = "/notification")
    public ResponseDto<String> deleteNotifications(@RequestParam UserRoleEnum role,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.deleteNotifications(userDetails.getUser(), role);
    }

    // role : ADMIN / USER
    @Operation(summary = "알림 카운트 갯수", description = "role = ADMIN / USER<br>" +
            "ADMIN/USER 페이지의 알림 카운트를 반환 해준다.")
    @GetMapping("/notification/count")
    public ResponseDto<List<NotificationCountResponseDto>> countUserNotifications(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.countNotifications(userDetails.getUser());
    }

    // role : ADMIN / USER
    @Operation(summary = "알림 카운트 갯수 초기화", description = "role = ADMIN / USER<br>" +
            "ADMIN/USER 페이지의 알림 카운트를 초기화 해준다.")
    @PutMapping("notification/count")
    public ResponseDto<String> resetUserCount(@RequestParam UserRoleEnum role,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.resetCount(userDetails.getUser(), role);
    }
}
