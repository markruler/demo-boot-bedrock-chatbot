package com.example.demo.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public String chat(
            @RequestBody ChatRequest request
    ) {
        return chatService.chat(request);
    }

}
