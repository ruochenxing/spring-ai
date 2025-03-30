package org.example.springmcpserver.config;

import org.example.springmcpserver.services.MySqlService;
import org.example.springmcpserver.services.NumService;
import org.example.springmcpserver.services.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderConfig {
    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

    @Bean
    public ToolCallbackProvider numTools(NumService numService) {
        return MethodToolCallbackProvider.builder().toolObjects(numService).build();
    }

    @Bean
    public ToolCallbackProvider mysqlTools(MySqlService mySqlService) {
        return MethodToolCallbackProvider.builder().toolObjects(mySqlService).build();
    }
}
