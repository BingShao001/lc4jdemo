package com.lc.lc4jdemo.aiservice.functioncall;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class BookTools {

    // 定义天气查询工具，自动绑定到模型
    @Tool("预定机票")
    public String bookFlight(@P("航班") String flight, @P(value = "姓名") String name,@P("日期") String  date) {
        log.info("开始调用订票API，航班 {} ，姓名：{}，日期：{}", flight, name,date);
        // 实际调用天气API的逻辑（此处模拟返回）
        return String.format("你好，【%s】先生，你预定的【%s】【%s】号航班已成功！", name,date,flight);
    }

    // 定义辅助工具（如日期计算）
    @Tool("返回明天的日期")
    public LocalDate getTomorrow() {
        log.info("获取明天的日期");
        return LocalDate.now().plusDays(1);
    }
}
