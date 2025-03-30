package org.example.springmcpclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class McpClientService {

    public static final List<McpSyncClient> clients = new ArrayList<>();

    /**
     * {
    *                           "mcpServers": {
    *                             "mysql_mcp_server": {
    *                                 "command": "uv",
    *                                 "args": [
    *                                   "run --python /Users/ruochenxing/Documents/cursor_workspace/mysql_mcp_server/.venv/bin/python mcp run /Users/ruochenxing/Documents/cursor_workspace/mysql_mcp_server/src/mysql_mcp_server/server.py"
    *                                 ]
    *                             },
    *                             "server-name": {
    *                               "url": "http://localhost:8888/sse",
    *                               "env": {
    *                                 "API_KEY": "value"
    *                               }
    *                             }
    *                           }
    *                       }
     * */
    @PostConstruct
    public void init() throws JsonProcessingException {
        String json = """
                        {
                          "mcpServers": {
                            "weatherServer": {
                                "url": "http://localhost:8888",
                                "env": {
                                    "API_KEY": "value"
                                }
                            }
                          }
                      }
                """;
        // parse the json
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, Map.class);
        Map<String, Object> mcpServers = (Map<String, Object>) map.get("mcpServers");
        for (Map.Entry<String, Object> entry : mcpServers.entrySet()) {
            String serverName = entry.getKey();
            Map<String, Object> serverConfig = (Map<String, Object>) entry.getValue();
            if (serverConfig.containsKey("command")) {// stdio server服务可以自动启动
                String command = (String) serverConfig.get("command");
                List<String> args = (List<String>) serverConfig.get("args");
                String[] argsArray = args.toArray(new String[0]);

                var stdioParams = ServerParameters.builder(command).args(argsArray).build();
                var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                        .requestTimeout(Duration.ofSeconds(20)).build();
                var init = mcpClient.initialize();
                System.out.println("MCP Initialized: " + init);
                clients.add(mcpClient);
            } else if (serverConfig.containsKey("url")) {// sse http服务则需要手动启动好
                String url = (String) serverConfig.get("url");
                String apiKey = (String) serverConfig.get("apiKey");
                var mcpClient = McpClient.sync(new HttpClientSseClientTransport(url)).build();
                var init = mcpClient.initialize();
                System.out.println("MCP Initialized: " + init);
                clients.add(mcpClient);
            }
        }
    }

    public List<SyncMcpToolCallback> mcpToolCallbacks(McpSyncClient mcpClient) {
        List<SyncMcpToolCallback> functionCallbacks = mcpClient.listTools(null)
                .tools()
                .stream()
                .map(tool -> new SyncMcpToolCallback(mcpClient, tool))
                .toList();
        return functionCallbacks;
    }


}
