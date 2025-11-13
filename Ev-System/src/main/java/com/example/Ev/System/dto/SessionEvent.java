package com.example.Ev.System.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionEvent {
    // CREATED | CLOSED
    private String type;
    private String sessionId;
    private long timestamp;

    private String customerHint;

    private ChatMessage initialMessage;
}