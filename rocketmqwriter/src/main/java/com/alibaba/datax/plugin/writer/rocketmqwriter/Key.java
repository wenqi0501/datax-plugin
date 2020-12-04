package com.alibaba.datax.plugin.writer.rocketmqwriter;

import com.alibaba.datax.common.util.Configuration;

public final class Key {

    private static final String NAMESERVER = "nameServer";
    private static final String ACCESSKEY = "accessKey";
    private static final String SECRETKEY = "secretKey";
    private static final String TOPIC = "topic";
    private static final String TAGS = "tags";
    private static final String BATCHSIZE = "batchSize";

    /**
     * 根据json获取nameServer配置
     * @param conf
     * @return
     */
    public static String getNameServer(Configuration conf) {
        return conf.getString(NAMESERVER, "");
    }

    /**
     * 根据json获取accessKey配置
     * @param conf
     * @return
     */
    public static String getAccessKey(Configuration conf) {
        return conf.getString(ACCESSKEY, "");
    }

    /**
     * 根据json获取secretKey配置
     * @param conf
     * @return
     */
    public static String getSecretKey(Configuration conf) {
        return conf.getString(SECRETKEY, "");
    }

    /**
     * 根据json获取topic配置
     * @param conf
     * @return
     */
    public static String getTopic(Configuration conf) {
        return conf.getString(TOPIC, "");
    }

    /**
     * 根据json获取tags配置
     * @param conf
     * @return
     */
    public static String getTags(Configuration conf) {
        return conf.getString(TAGS, "");
    }

    /**
     * 根据json获取BatchSize多少条推送一次的配置
     * @param conf
     * @return
     */
    public static Integer getBatchSize(Configuration conf) {
        return conf.getInt(BATCHSIZE, 1000);
    }


}
