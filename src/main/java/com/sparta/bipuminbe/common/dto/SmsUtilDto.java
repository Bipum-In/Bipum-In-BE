package com.sparta.bipuminbe.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class SmsUtilDto {
    private String type;
    private String from;
    private String content;
    private List<SmsTo> messages;

    public static SmsUtilDto of(String content, String fromPhone, List<String> toPhoneList) {
        List<SmsTo> messages = new ArrayList<>();
        for (String toPhone : toPhoneList) {
            messages.add(SmsTo.of(toPhone));
        }

        return SmsUtilDto.builder()
                .type("SMS")
                .from(fromPhone)
                .content(content)
                .messages(messages)
                .build();
    }
}
