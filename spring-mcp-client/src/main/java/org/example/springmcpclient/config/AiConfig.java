package org.example.springmcpclient.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatModel chatModel(){
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl("https://dashscope.aliyuncs.com/compatible-mode")
                .apiKey("sk-5b6ec961b2e140e6b5239582d1f47cf3")
                .build();
        OpenAiChatOptions openAiChatOptions = new OpenAiChatOptions();
        openAiChatOptions.setModel("qwen-plus");
        return OpenAiChatModel.builder().defaultOptions(openAiChatOptions).openAiApi(openAiApi).build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return builder
//                .defaultFunctions(functionCallbacks.toArray(new SyncMcpToolCallback[0]))
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

//    @Bean(destroyMethod = "close")
//    public McpSyncClient mcpClient() {
//        return McpClient.sync(new HttpClientSseClientTransport("http://127.0.0.1:8888")).build();
//    }
//
//    @Bean
//    public List<SyncMcpToolCallback> functionCallbacks(McpSyncClient mcpClient) {
//
//        return mcpClient.listTools(null)
//                .tools()
//                .stream()
//                .map(tool -> new SyncMcpToolCallback(mcpClient, tool))
//                .toList();
//    }
}
