package com.alibaba.datax.plugin.writer.rocketmqwriter;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 阿里云版RocketMQWriter处理类
 * @Auther: wenqi
 * @Date: 2020/11/30 14:47
 */
@Slf4j
public class RocketMQWriter extends Writer {

    private final static String WRITE_COLUMNS = "write_columns";

    public static class Job extends Writer.Job {

        private Configuration conf = null;

        /**
         * 获取json配置参数
         */
        @Override
        public void init() {
            this.conf = super.getPluginJobConf();
        }

        @Override
        public List<Configuration> split(int mandatoryNumber) {
            List<Configuration> configurations = new ArrayList<Configuration>(mandatoryNumber);
            for (int i = 0; i < mandatoryNumber; i++) {
                configurations.add(conf);
            }
            return configurations;
        }

        @Override
        public void destroy() {

        }
    }

    public static class Task extends Writer.Task {

        private Configuration conf = null;

        private Producer producer = null;

        private List<MQFieldType> typeList;

        private List<MQColumn> columnList;

        /**
         * 构造RocketMQ生产者
         */
        @Override
        public void init(){
            //获取json配置参数
            this.conf = super.getPluginJobConf();

            //获取属性映射
            this.getMapping();

            Properties properties = new Properties();
            // 鉴权用 AccessKey，在阿里云服务器管理控制台创建
            properties.put(PropertyKeyConst.AccessKey, Key.getAccessKey(conf));
            // 鉴权用 SecretKey，在阿里云服务器管理控制台创建
            properties.put(PropertyKeyConst.SecretKey, Key.getSecretKey(conf));
            // 设置 TCP 接入域名，进入控制台的实例管理页面，在页面上方选择实例后，在实例信息中的“获取接入点信息”区域查看
            properties.put(PropertyKeyConst.NAMESRV_ADDR,Key.getNameServer(conf));
            //设置发送超时时间，单位毫秒
            properties.put(PropertyKeyConst.SendMsgTimeoutMillis, 5000L);

            producer = ONSFactory.createProducer(properties);
            // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
            producer.start();

            log.info(">>>>>>>>>>>>>>> producer start success <<<<<<<<<<<<<<<<<<<");
            //防止 MQClientException: No route info of this topic 问题
            try {TimeUnit.SECONDS.sleep(3);} catch (Exception e) {}
        }

        @Override
        public void startWrite(RecordReceiver lineReceiver) {
            List<Record> writerBuffer = new ArrayList<Record>();
            Record record = null;
            while ((record = lineReceiver.getFromReader()) != null) {
                writerBuffer.add(record);
                if(writerBuffer.size() >= Key.getBatchSize(conf)){
                    sendMessage(writerBuffer);
                    //清空推送完成的集合
                    writerBuffer.clear();
                }
            }

            if (!writerBuffer.isEmpty()) {
                sendMessage(writerBuffer);
                //清空推送完成的集合
                writerBuffer.clear();
            }

        }

        /**
         * 释放资源
         */
        @Override
        public void destroy() {
            if(Objects.nonNull(producer)){
                producer.shutdown();
            }
        }

        /**
         * 按照批次推送MQ消息
         * @param writerBuffer
         */
        private void sendMessage(List<Record> writerBuffer){
            List<Map<String, Object>> list = doAssembleData(writerBuffer);
            Message message = new Message();
            message.setTopic(Key.getTopic(conf));
            message.setTag(Key.getTags(conf));
            message.setBody(JSON.toJSONString(list).getBytes());
            //发送消息
            SendResult sendResult = producer.send(message);
            log.info("按照批次推送MQ消息条数:{},结果:{}",list.size(),sendResult);

        }

        /**
         * 按照接收数据组装数据
         * @param writerBuffer
         */
        private List<Map<String, Object>> doAssembleData(List<Record> writerBuffer) {
            List<Map<String, Object>> list = new ArrayList<>(writerBuffer.size());
            Map<String, Object> data = null;
            for (Record record : writerBuffer) {
                data = new HashMap<String, Object>();
                for (int i = 0; i < record.getColumnNumber(); i++) {
                    Column column = record.getColumn(i);
                    String columnName = columnList.get(i).getName();
//                    MQFieldType columnType = typeList.get(i);
                    data.put(columnName, column.asString());
                }
                list.add(data);
            }
            return list;
        }

        /**
         * 获取属性映射
         */
        private void getMapping() {
            List column = conf.getList("column");
            if (column != null) {
                columnList = new ArrayList<>();
                for (Object col : column) {
                    JSONObject jo = JSONObject.parseObject(col.toString());
                    String colName = jo.getString("name");
                    String colTypeStr = jo.getString("type");
                    if (colTypeStr == null) {
//                        throw DataXException.asDataXException(ESWriterErrorCode.BAD_CONFIG_VALUE, col.toString() + " column must have type");
                    }
                    MQFieldType colType = MQFieldType.getESFieldType(colTypeStr);
                    if (colType == null) {
//                        throw DataXException.asDataXException(ESWriterErrorCode.BAD_CONFIG_VALUE, col.toString() + " unsupported type");
                    }

                    MQColumn columnItem = new MQColumn();

                    columnItem.setName(colName);
                    columnItem.setType(colTypeStr);

                    columnList.add(columnItem);
                }
            }

            typeList = new ArrayList<MQFieldType>();

            for (MQColumn col : columnList) {
                typeList.add(MQFieldType.getESFieldType(col.getType()));
            }
        }


    }
}
