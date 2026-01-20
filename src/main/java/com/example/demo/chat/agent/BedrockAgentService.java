package com.example.demo.chat.agent;

import com.example.demo.chat.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeAgentRequest;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeAgentResponseHandler;
import software.amazon.awssdk.services.bedrockagentruntime.model.PayloadPart;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedrockAgentService {

    @Value("${chat.agent.id}")
    private String agentId;

    @Value("${chat.agent.alias}")
    private String agentAlias;

    private final BedrockAgentRuntimeAsyncClient bedrockAgentRuntimeAsyncClient;

    private static final int FUTURE_TIMEOUT_SECONDS = 30;

    public String chatWithAgent(ChatRequest request) {
        String sessionId = UUID.randomUUID().toString();

        InvokeAgentRequest invokeAgentRequest = InvokeAgentRequest.builder()
                .agentId(agentId)
                .agentAliasId(agentAlias)
                .sessionId("session-" + sessionId) // TODO: 대화 session을 유지하려면 별도 관리 필요
                .inputText(request.message())
                .build();

        StringBuilder responseBuilder = new StringBuilder();

        CompletableFuture<Void> future = bedrockAgentRuntimeAsyncClient.invokeAgent(
                invokeAgentRequest,
                InvokeAgentResponseHandler.builder()
                        .onResponse(response -> log.info("Agent response received. Status: {}", response.sdkHttpResponse().statusCode()))
                        .onEventStream(publisher -> publisher.subscribe(event -> {
                            if (event instanceof PayloadPart payloadPart) {
                                String part = payloadPart.bytes().asUtf8String();
                                responseBuilder.append(part);
                            }
                        }))
                        .build()
        );

        try {
            // @see BedrockAgentConfig
            future.get(FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return responseBuilder.toString();
        } catch (TimeoutException e) {
            log.error("Timeout while waiting for Bedrock Agent response", e);
            // 중요: 타임아웃 발생 시 SDK 요청도 취소하여 리소스 낭비 방지
            future.cancel(true);
            return "죄송합니다. 응답 시간이 초과되었습니다. 잠시 후 다시 시도해 주세요.";
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error invoking Bedrock Agent", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to chat with agent", e);
        }
    }
}
