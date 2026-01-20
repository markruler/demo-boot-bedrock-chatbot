package com.example.demo.chat.agent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;

import java.time.Duration;

@Configuration
public class BedrockAgentConfig {

    @Value("${spring.ai.bedrock.aws.region}")
    private Region region;

    private static final int ATTEMPT_TIMEOUT_SECONDS = 10;

    @Bean
    public BedrockAgentRuntimeAsyncClient bedrockAgentRuntimeAsyncClient() {
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(ATTEMPT_TIMEOUT_SECONDS * 2 + 1L)) // API 호출 전체 타임아웃
                .apiCallAttemptTimeout(Duration.ofSeconds(ATTEMPT_TIMEOUT_SECONDS)) // 시도당 타임아웃
                .build();

        return BedrockAgentRuntimeAsyncClient.builder()
                // .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .overrideConfiguration(overrideConfig)
                .build();
    }
}
