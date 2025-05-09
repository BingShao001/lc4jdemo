package com.lc.lc4jdemo.redis.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.data.message.Content;

import java.util.List;

public abstract class SystemMessageMixin {
    @JsonCreator
    public SystemMessageMixin(@JsonProperty("text") String text) {
        // 注意：这个构造签名要匹配 UserMessage 中存在的构造函数
    }
}
