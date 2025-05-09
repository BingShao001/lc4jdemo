package com.lc.lc4jdemo.aiservice.functioncall;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class WeatherTools {

    // 定义天气查询工具，自动绑定到模型
    @Tool("查询指定城市的天气预报")
    public String getWeather(@P("城市名称") String city, @P(value = "温度单位,支持C（摄氏度）或F（华氏度）") String unit) {
        log.info("查询 {} 的天气，单位：{}", city, unit);
        // 实际调用天气API的逻辑（此处模拟返回）
        return String.format("【%s】当前温度：25°C，天气晴朗", city);
    }

    // 定义辅助工具（如日期计算）
    @Tool("返回明天的日期")
    public LocalDate getTomorrow() {
        log.info("获取明天的日期");
        return LocalDate.now().plusDays(1);
    }
}
