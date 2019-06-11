# logs-monitor-examples


[logstash sprintf-format](https://www.elastic.co/guide/en/logstash/7.1/event-dependent-configuration.html#sprintf)

  

spring-boot中使用log4j2：（log4j2 和 log4j maven依赖冲突导致）
[Logback configuration error detected的终极解决方案](https://blog.csdn.net/m0_37034294/article/details/82801294)  


```
# logstash 默认field
# logstash -e ""
{
       "message" => "hello world!\r",
      "@version" => "1",
          "type" => "stdin",
    "@timestamp" => 2019-06-11T01:35:36.891Z,
          "host" => "DESKTOP-OSO9VK9"
}
```

```
input { 
  stdin { 
    type => stdin 
	add_field => { 
	  "time" => "%{+yyyy-MM}"
	  "time2" => "2019-06-10 15:26:49"
	  "field1" => "field1, %{time2}"
	}
  } 
}
filter {
  #执行ruby程序，下面例子是将日期转化为字符串赋予daytag
  ruby {
	# code => "event['daytag'] = event.timestamp.time.localtime.strftime('%Y-%m-%d')"
    code => 'event.set("daytag", event.timestamp.time.localtime.strftime("%Y-%m-%d %H:%M:%S"))'
  }
}
output { 
  stdout { 
    codec => rubydebug 
  } 
}
```

## 总结

- [Logstash配置总结和实例](https://my.oschina.net/shawnplaying/blog/670217)
- [elk-logstash时区问题](https://www.cnblogs.com/wangpei886/p/8043021.html)

问题:
  假设input中存在{"updateTime": "yyyy-MM-dd HH:mm:ss", "createTime": "yyyy-MM-dd HH:mm:ss"}
  现在需要把`createTime = @timestamp`，这个没什么问题。
  但是，如何取类似`log-%{updateTime:yyyy-MM}`? `%{+yyyy-MM}`取的是`@timestamp`
  
  一种解决方式， 在input中加入新的字段`updateTimeFormat: yyyy-MM`，那么在logstash中直接引用`updateTimeFormat`。