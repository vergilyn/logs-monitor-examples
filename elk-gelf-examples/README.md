# elk-gelf-examples

参考:
  - [log4j2 和logstash整合](https://segmentfault.com/a/1190000010138070)
  - [ELK入门02—Logstash+Log4j2+ES](https://segmentfault.com/a/1190000016192394)
  
- [github logstash-gelf](https://github.com/mp911de/logstash-gelf)
- [logstash-plugin-input gelf](https://www.elastic.co/guide/en/logstash-versioned-plugins/current/v3.1.1-plugins-inputs-gelf.html)
- [logstash-plugin-filter mutate](https://www.elastic.co/guide/en/logstash-versioned-plugins/current/v3.3.3-plugins-filters-mutate.html)

```
logstash-6.4.1 >>>>
logstash-input-gelf-3.1.1
logstash-filter-mutate-3.3.3
```

log4j --> logstash --> elasticsearch
application 通过中间件 logstash-gelf.jar 将log传递到 logstash，然后logstash ->es 

[elk-logstash时区问题](https://www.cnblogs.com/wangpei886/p/8043021.html)

问题:
  假设input中存在{"updateTime": "yyyy-MM-dd HH:mm:ss", "createTime": "yyyy-MM-dd HH:mm:ss"}
  现在需要把`createTime = @timestamp`，这个没什么问题。
  但是，如何取类似`log-%{updateTime:yyyy-MM}`? `%{+yyyy-MM}`取的是`@timestamp`
  
  一种解决方式， 在input中加入新的字段`updateTimeFormat: yyyy-MM`，那么在logstash中直接引用`updateTimeFormat`。
 
```
# 省略的 log4j2.xml
<Gelf name="logstash-gelf" host="udp:127.0.0.1" port="18090" version="1.1" ignoreExceptions="true">
    <Field name="timestamp" pattern="%d{yyyy-MM-dd HH:mm:ss.SSS}" />
    <Field name="logger" pattern="%logger" />
    <Field name="level" pattern="%level" />
    <Field name="simpleClassName" pattern="%C{1}" />
    <Field name="className" pattern="%C" />
    <Field name="server" pattern="%host" />
    <Field name="index_name" pattern="%d{yyyy-MM}" />
</Gelf>
```

```
input {
  gelf {
	host => "127.0.0.1"
    port => 18090
  }
}
filter {

}
output {
  stdout { 
    codec => rubydebug 
  }
  elasticsearch {
    hosts => ["127.0.0.1:9200"]
    index => "logs-%{index_name}"
  } 
  
}
```

为什么 `index => "logs-%{index_name}"` 而不直接 `index => "logs-%{+yyyy-MM}"`？
原因：
  因为logstash默认使用的UTC时间，而我们是UTC+8。未找到类似`index => "logs-%{Time:yyyy-MM}"`的语法。
  现在的解决思路是，在logstash-input中直接引入一个新的字段`index_name`，然后直接引用。（缺点：多了一个filed）