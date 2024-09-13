package com.yt.ytrpccore.protocol;

import lombok.Getter;

/**
 * 协议消息的类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(0), // 请求
    RESPONSE(1), // 响应
    HEART_BEAT(2), // heart_beat 心跳检测
    OTHERS(3);


    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    /**
     * 根据 value 获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByValue(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }
}
