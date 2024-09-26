package com.yt.ytrpccore.protocol;

/**
 * 协议常量
 *
 */
public interface ProtocolConstant {

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;

    /**
     * 信息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;
}
