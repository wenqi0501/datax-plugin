package com.alibaba.datax.plugin.writer.ons_rocketmqwriter;

import com.alibaba.datax.common.spi.ErrorCode;

// TODO 待完善
public enum MQWriterErrorCode implements ErrorCode {
    BAD_CONFIG_VALUE("ESWriter-00", "您配置的值不合法."),
    ;

    private final String code;
    private final String description;

    MQWriterErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Description:[%s]. ", this.code,
                this.description);
    }
}