package com.alibaba.datax.plugin.reader.otsreader.callable;

import com.aliyun.openservices.ots.OTSClient;
import com.aliyun.openservices.ots.model.DescribeTableRequest;
import com.aliyun.openservices.ots.model.DescribeTableResult;
import com.aliyun.openservices.ots.model.TableMeta;

import java.util.concurrent.Callable;

public class GetTableMetaCallable implements Callable<TableMeta>{

    private OTSClient ots = null;
    private String tableName = null;
    
    public GetTableMetaCallable(OTSClient ots, String tableName) {
        this.ots = ots;
        this.tableName = tableName;
    }
    
    @Override
    public TableMeta call() throws Exception {
        DescribeTableRequest describeTableRequest = new DescribeTableRequest();
        describeTableRequest.setTableName(tableName);
        DescribeTableResult result = ots.describeTable(describeTableRequest);
        TableMeta tableMeta = result.getTableMeta();
        return tableMeta;
    }

}
