package com.lc.lc4jdemo.redis.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AiMessageMixin {

    @JsonCreator
    public AiMessageMixin(@JsonProperty("text") String text) {
    }
}
