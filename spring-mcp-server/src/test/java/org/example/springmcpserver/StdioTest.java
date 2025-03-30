package org.example.springmcpserver;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StdioTest {

    private static String jarFilePath;
    private McpSyncClient mcpClient;

    @BeforeAll
    public static void init() throws URISyntaxException {
        // find jar file path

        URL resource = StdioTest.class.getClassLoader().getResource("locate.properties");
        Assertions.assertNotNull(resource, "load junit classLoader context fail");

        Path targetPath = Paths.get(resource.toURI()).getParent().getParent();

        File files = targetPath.toFile();
        File[] files1 = files.listFiles(file -> (file.getName().endsWith(".jar")));

        Assertions.assertNotNull(files1, "Can not find the generated jar file under [target] directory");
        Assertions.assertEquals(1, files1.length, "Find more then one jar file under [target] directory");

        jarFilePath = files1[0].getAbsolutePath();
    }

    @BeforeEach
    public void initClient() {
        // -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8
        ServerParameters serverParameters = ServerParameters.builder("java")
                .args("-jar", "-Dspring.ai.mcp.server.stdio=true", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8", jarFilePath).build();
        mcpClient = McpClient.sync(new StdioClientTransport(serverParameters)).build();
		mcpClient.initialize();
        // make sure program started
        Assertions.assertDoesNotThrow(() -> TimeUnit.SECONDS.sleep(5));
    }

    @AfterEach
    public void close() {
        if (mcpClient != null) {
            mcpClient.closeGracefully();
        }
    }

    @Test
    public void testWeatherMCP() {

        var weather = quickCall("getWeather", Map.of("cityName", "乌鲁木齐"));

        List<McpSchema.Content> contentList = weather.content();
        String res = easyTextContent(contentList);
        System.out.println(res);
        Assertions.assertEquals("\"乌鲁木齐今天的温度是32.6\"", res);
    }

    @Test
    public void testNumMCP() {

        var num = quickCall("judgeIfOdd", Map.of("num", "1"));

        List<McpSchema.Content> contentList = num.content();
        String res = easyTextContent(contentList);
        System.out.println(res);
        Assertions.assertEquals("\"1不是双数\"", res);
    }

    private McpSchema.CallToolResult quickCall(String name, Map<String, Object> arguments) {
        return mcpClient.callTool(new McpSchema.CallToolRequest(name, arguments));
    }

    private String easyTextContent(List<McpSchema.Content> contentList) {
        assert contentList != null && contentList.size() == 1;
        McpSchema.TextContent textContent = (McpSchema.TextContent) contentList.get(0);
        return textContent.text();
    }
}
