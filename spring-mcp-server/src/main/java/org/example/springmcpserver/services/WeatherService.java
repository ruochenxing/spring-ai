package org.example.springmcpserver.services;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class WeatherService {

    @Tool(description = "通过城市名字获取气温")
    public String getWeather(String cityName) throws UnsupportedEncodingException {
//        cityName = new String(cityName.getBytes(), StandardCharsets.UTF_8);
        double v = cityName.length() * 8 + 0.6;
        // stdio模式要注释掉下面这行
//        System.out.println(v);
        return cityName + "今天的温度是" + v;
    }


}
