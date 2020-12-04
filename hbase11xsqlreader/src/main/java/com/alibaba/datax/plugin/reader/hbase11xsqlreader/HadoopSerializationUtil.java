package com.alibaba.datax.plugin.reader.hbase11xsqlreader;


import org.apache.hadoop.io.Writable;

import java.io.*;

public class HadoopSerializationUtil {

    public static byte[] serialize(Writable writable) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataout = new DataOutputStream(out);
        writable.write(dataout);
        dataout.close();
        return out.toByteArray();
    }

    public static void deserialize(Writable writable, byte[] bytes) throws Exception {

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        DataInputStream datain = new DataInputStream(in);
        writable.readFields(datain);
        datain.close();
    }


}