package com.lc.lc4jdemo.redis.jackson;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.data.message.TextContent;

public abstract class TextContentMixin {

    @JsonCreator
    public TextContentMixin(@JsonProperty("text") String text) {
    }
}

