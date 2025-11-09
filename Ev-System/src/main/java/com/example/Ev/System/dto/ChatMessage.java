package com.example.Ev.System.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String roomId;     // ví dụ: bookingId, orderId
    private String senderId;   // id người gửi
    private String senderRole; // CUSTOMER | STAFF
    private String content;    // nội dung tin nhắn
    private long timestamp;    // epoch millis
}