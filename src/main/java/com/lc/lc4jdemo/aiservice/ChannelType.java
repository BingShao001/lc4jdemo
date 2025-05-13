package com.lc.lc4jdemo.aiservice;

import jdk.jfr.Description;

public enum ChannelType {
    XIAOHONGSHU("小红书"),
    TAOBAO("淘宝"),
    JINGDONG("京东"),
    PINDUODUO("拼多多"),
    WEIPINHUI("唯品会");

    private final String displayName;

    ChannelType(String displayName) {
        this.displayName = displayName;
    }

    public static ChannelType fromName(String name) {
        for (ChannelType ct : values()) {
            if (ct.name().equalsIgnoreCase(name) || ct.displayName.equals(name)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown channel: " + name);
    }

    public String getDisplayName() {
        return displayName;
    }
}

