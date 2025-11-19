package com.example.demo.chat;

import com.example.demo.chat.agent.BedrockAgentService;
import com.example.demo.chat.client.ChatClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatClientService chatClientService;
    private final BedrockAgentService bedrockAgentService;

    @PostMapping("/chat")
    public String chat(
            @Valid @RequestBody ChatRequest request
    ) {
        return chatClientService.chat(request);
    }

    @PostMapping("/agent")
    public String chatWithAgent(
            @Valid @RequestBody ChatRequest request
    ) {
        return bedrockAgentService.chatWithAgent(request);
    }

}
