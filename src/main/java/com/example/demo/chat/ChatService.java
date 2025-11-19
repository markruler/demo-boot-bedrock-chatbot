package com.example.demo.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public String chat(ChatRequest request) {
        return chatClient
                // 프롬프트 시작
                .prompt()
                // 유저 메시지
                .user(request.message())
                // Bedrock 호출
                .call()
                // 모델이 생성한 텍스트만 추출
                .content();
    }

}
