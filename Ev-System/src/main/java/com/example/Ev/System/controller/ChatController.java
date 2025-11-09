package com.example.Ev.System.controller;


import com.example.Ev.System.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    // Client gửi tới /app/chat.send
    @MessageMapping("/chat.send")
    public void relay(ChatMessage msg) {
        // Không lưu DB, chỉ forward tới room topic
        // Client subscribe: /topic/rooms/{roomId}
        String destination = "/topic/rooms/" + msg.getRoomId();
        messagingTemplate.convertAndSend(destination, msg);
    }
}