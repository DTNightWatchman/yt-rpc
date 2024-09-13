package com.yt.ytrpccore.protocol;

/**
 * 协议常量
 *
 */
public interface ProtocolConstant {

    /**
     * 消息头长度
     */
    int MESSAGE_LENGTH = 17;

    /**
     * 协议魔数
     */
    int PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
