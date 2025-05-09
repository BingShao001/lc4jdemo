package com.lc.lc4jdemo.redis.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ToolExecutionResultMessageMixin {
    @JsonCreator
    public ToolExecutionResultMessageMixin(@JsonProperty("id") String id, @JsonProperty("toolName") String toolName, @JsonProperty("text") String text) {
    }
}
