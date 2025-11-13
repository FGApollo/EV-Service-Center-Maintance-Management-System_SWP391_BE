package com.example.Ev.System.controller;

import com.example.Ev.System.chat.ActiveChatRegistry;
import com.example.Ev.System.dto.ChatMessage;
import com.example.Ev.System.dto.SessionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ActiveChatRegistry registry;

    @MessageMapping("/chat.send")
    public void send(ChatMessage msg) {
        if (msg == null || msg.getSessionId() == null || msg.getSessionId().isBlank()) {
            return;
        }

        boolean isNew = registry.registerIfAbsent(msg.getSessionId());
        registry.touch(msg.getSessionId());

        if (isNew) {
            // Phát event CREATED kèm initialMessage để tất cả staff tạo chatbox + hiển thị luôn tin đầu tiên
            SessionEvent created = SessionEvent.builder()
                    .type("CREATED")
                    .sessionId(msg.getSessionId())
                    .timestamp(System.currentTimeMillis())
                    .customerHint("Guest")
                    .initialMessage(msg)
                    .build();
            messagingTemplate.convertAndSend("/topic/staff/sessions", created);

            // LƯU Ý: Không gửi lại msg vào /topic/chat/{sessionId} ở lần đầu,
            // để tránh staff miss vì subscribe trễ. Staff sẽ lấy initialMessage từ event trên.
            // Customer phải tự hiển thị tin của mình (optimistic) ở FE.
            return;
        }

        // Các tin nhắn tiếp theo: gửi vào topic phòng như bình thường
        messagingTemplate.convertAndSend("/topic/chat/" + msg.getSessionId(), msg);
    }

    @MessageMapping("/chat.close")
    public void close(ChatMessage msg) {
        if (msg == null || msg.getSessionId() == null || msg.getSessionId().isBlank()) return;

        boolean removed = registry.close(msg.getSessionId());
        if (removed) {
            SessionEvent closed = SessionEvent.builder()
                    .type("CLOSED")
                    .sessionId(msg.getSessionId())
                    .timestamp(System.currentTimeMillis())
                    .build();
            messagingTemplate.convertAndSend("/topic/staff/sessions", closed);
        }
    }
}