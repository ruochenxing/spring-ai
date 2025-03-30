package org.example.springmcpclient;

import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.Resource;
import org.example.springmcpclient.service.McpClientService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.test.mockmvc.print=none",
    "spring.test.mockmvc.printOnlyOnFailure=false"
})
class SpringAiClientApplicationTests {

    @Resource
    private ChatModel chatModel;
    //    @Resource
//    private ChatClient chatClient;
    @Resource
    private McpClientService mcpClientService;

    @Test
    void contextLoads() {
    }

//
//    @Test
//    public void test(){
//        String resp = chatClient.prompt("乌鲁木齐今天气温多少度").call().content();
//        System.out.println(resp);
//    }

    @Test
    public void test2(){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        List<SyncMcpToolCallback> allCallbacks = new ArrayList<>();
        for (McpSyncClient client : McpClientService.clients){
            allCallbacks.addAll(mcpClientService.mcpToolCallbacks(client));
        }

        ChatClient chatClient = builder
                .defaultFunctions(allCallbacks.toArray(new SyncMcpToolCallback[0]))
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();

        Scanner scanner = new Scanner(System.in);
        System.out.println("开始聊天，输入 'exit' 退出");
        while (true) {
            System.out.print("你: ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("聊天结束");
                break;
            }

            try{
                String response = chatClient.prompt(userInput).call().content();
                System.out.println("AI: " + response);
            }catch (Exception e){
                System.out.println("发生异常了， " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
        for (McpSyncClient client : McpClientService.clients){
            client.closeGracefully();
        }
    }
//    @Test
//    public void testInteractiveChat() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("开始聊天，输入 'exit' 退出");
//
//        while (true) {
//            System.out.print("你: ");
//            String userInput = scanner.nextLine();
//
//            if ("exit".equalsIgnoreCase(userInput)) {
//                System.out.println("聊天结束");
//                break;
//            }
//
//            String response = chatClient.prompt(userInput).call().content();
//            System.out.println("AI: " + response);
//        }
//
//        scanner.close();
//    }
}
