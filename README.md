# 插件开发步骤
1. 下载datax源码，下载地址：https://github.com/alibaba/DataX

2. 按照已有插件的代码结构建立rocketmqwriter的代码目录如下：

   ![image-20201206132342266](/Users/wenqi/Library/Application Support/typora-user-images/image-20201206132342266.png)
   

   package.xml：定义了插件具体的打包路径

   ```xml
   <assembly
           xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0 http://maven.apache.org/xsd/assembly-1.1.0.xsd">
       <id></id>
       <formats>
           <format>dir</format>
       </formats>
       <includeBaseDirectory>false</includeBaseDirectory>
       <fileSets>
           <fileSet>
               <directory>src/main/resources</directory>
               <includes>
                   <include>plugin.json</include>
               </includes>
               <outputDirectory>plugin/writer/ons-rocketmqwriter</outputDirectory>
           </fileSet>
           <fileSet>
               <directory>target/</directory>
               <includes>
                   <include>ons-rocketmqwriter-0.0.1-SNAPSHOT.jar</include>
               </includes>
               <outputDirectory>plugin/writer/ons-rocketmqwriter</outputDirectory>
           </fileSet>
       </fileSets>
   
       <dependencySets>
           <dependencySet>
               <useProjectArtifact>false</useProjectArtifact>
               <outputDirectory>plugin/writer/ons-rocketmqwriter/libs</outputDirectory>
               <scope>runtime</scope>
           </dependencySet>
       </dependencySets>
   </assembly>
   ```

   Key类：获取配置文件json传递的参数；

   MQColumn类：获取属性column[]的值；

   MQFieldType类：获取属性column[]值的类型；

   MQWriterErrorCode类：定义MQ解析过程中的异常错误码；

   **ONS_RocketMQWriter类**：核心实现，数据解析以及MQ消息统一推送。

​      plugin.json：配置插件统一扫描文件实现类，即扫描ONS_RocketMQWriter类，配置如下：

```json
{
    "name": "ons-rocketmqwriter",
    "class": "com.alibaba.datax.plugin.writer.ons_rocketmqwriter.ONS_RocketMQWriter",
    "description": "适用于: 阿里云版RocketMQ",
    "developer": "wenqi"
}
```

​	  完成以上步骤，还需要修改Datax主工程配置文件package.xml，添加rocketmqwriter模块，代码如下：

```xml
<fileSet>
  <directory>ons-rocketmqwriter/target/datax/</directory>
  <includes>
    <include>**/*.*</include>
  </includes>
  <outputDirectory>datax</outputDirectory>
</fileSet>
```

3. 插件代码完成后，本地调试的话，需要修改core包下的Engine类，1）设置运行的datax的home目录；2）设置datax的运行脚本信息。截图如下：

   ![image-20201206133522793](/Users/wenqi/Library/Application Support/typora-user-images/image-20201206133522793.png)

4. 本地调试完成后进行maven打包，在idea的terminal中执行 mvn -U clean package assembly:assembly -Dmaven.test.skip=true

5. 编写json文件，mysql-mq.json

   ```json
   {
     "job": {
       "setting": {
             "speed": {
                 "channel": 1,
                 "record": -1,
                 "byte": -1
             }
         },
         "content": [{
             "reader": {
                 "name": "mysqlreader",
                 "parameter": {
                     "username": "root",
                     "password": "******",
                     "connection": [
                         {
                             "querySql": [
                                 "select id from table where id<7506000;"
                             ],
                             "jdbcUrl": [
                                 "jdbc:mysql://pc-xxx.rwlb.rds.aliyuncs.com:3306/db"
                             ]
                         }
                     ]
                 }
             },
             "writer": {
                 "name": "ons-rocketmqwriter",
                 "parameter": {
                     "nameServer": "http://xxx.mq-internet.aliyuncs.com:80",
                     "accessKey": "xxxxxx",
                     "secretKey": "xxxxxx",
                     "topic": "topic_name",
                     "tags": "*",
                     "batchSize":1000,
                     "column": [
                       {"name": "tableId", "type": "string"}
                     ]
                 }
             }
         }]
     }
   }
   ```

6. 执行脚本命令便可以完成mysql数据按照规则同步给RocketMQ了

   ``vim /job/mysql-mq.json``
