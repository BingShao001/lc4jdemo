package com.lc.lc4jdemo.redis.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.data.message.Content;

import java.util.List;

public abstract class UserMessageMixin {
    //@JsonProperty("name") String name,
    @JsonCreator
    public UserMessageMixin( @JsonProperty("contents") List<Content> contents) {
        // 注意：这个构造签名要匹配 UserMessage 中存在的构造函数
    }
}
