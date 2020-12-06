package com.alibaba.datax.plugin.writer.ons_rocketmqwriter;

// TODO 待完善
public enum MQFieldType {
    ID,
    STRING,
    TEXT,
    KEYWORD,
    LONG,
    INTEGER,
    SHORT,
    BYTE,
    DOUBLE,
    FLOAT,
    DATE,
    BOOLEAN,
    BINARY,
    INTEGER_RANGE,
    FLOAT_RANGE,
    LONG_RANGE,
    DOUBLE_RANGE,
    DATE_RANGE,
    GEO_POINT,
    GEO_SHAPE,

    IP,
    COMPLETION,
    TOKEN_COUNT,

    ARRAY,
    OBJECT,
    NESTED;

    public static MQFieldType getESFieldType(String type) {
        if (type == null) {
            return null;
        }
        for (MQFieldType f : MQFieldType.values()) {
            if (f.name().compareTo(type.toUpperCase()) == 0) {
                return f;
            }
        }
        return null;
    }
}
