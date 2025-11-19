package com.example.demo.chat.agent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;

import java.time.Duration;

@Configuration
public class BedrockAgentConfig {

    @Value("${spring.ai.bedrock.aws.region}")
    private Region region;

    @Bean
    public BedrockAgentRuntimeAsyncClient bedrockAgentRuntimeAsyncClient() {
        // 타임아웃 설정: API 호출 전체 타임아웃 2분, 시도당 타임아웃 1분
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofMinutes(1))
                .build();

        return BedrockAgentRuntimeAsyncClient.builder()
                .region(region)
                .overrideConfiguration(overrideConfig)
                .build();
    }
}
