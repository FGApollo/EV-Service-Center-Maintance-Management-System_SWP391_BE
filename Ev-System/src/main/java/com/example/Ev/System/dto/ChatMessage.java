package com.example.Ev.System.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    // ID phiên chat (UUID string do FE tạo mỗi lần mở chat)
    private String sessionId;

    // "CUSTOMER" hoặc "STAFF" (chỉ để hiển thị, không auth)
    private String sender;

    private String content;
    private long timestamp;
}