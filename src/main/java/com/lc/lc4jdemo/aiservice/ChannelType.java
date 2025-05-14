package com.lc.lc4jdemo.aiservice;

/**
 * Represents different shopping platforms available for recommendations
 * 
 * @author bing
 * @version 1.0
 */
public enum ChannelType {
    /** Xiaohongshu shopping platform */
    XIAOHONGSHU("小红书"),
    
    /** Taobao shopping platform */
    TAOBAO("淘宝"),
    
    /** JD.com shopping platform */
    JINGDONG("京东"),
    
    /** Pinduoduo shopping platform */
    PINDUODUO("拼多多"),
    
    /** Vipshop shopping platform */
    WEIPINHUI("唯品会");

    private final String displayName;

    /**
     * Constructor for ChannelType
     * 
     * @param displayName the Chinese display name of the shopping platform
     */
    ChannelType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Finds a ChannelType by name (case-insensitive) or display name
     * 
     * @param name the name or display name to search for
     * @return the matching ChannelType
     * @throws IllegalArgumentException if no matching channel is found
     */
    public static ChannelType fromName(String name) {
        for (ChannelType ct : values()) {
            if (ct.name().equalsIgnoreCase(name) || ct.displayName.equals(name)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown channel: " + name);
    }

    /**
     * Gets the Chinese display name of the shopping platform
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}

