# elk-gelf-examples

logstash:
  - [logstash-plugin-input beats](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-beats.html)
  - [logstash-plugin-filter mutate](https://www.elastic.co/guide/en/logstash-versioned-plugins/current/v3.3.3-plugins-filters-mutate.html)

参考:
  - [log4j2 和logstash整合](https://segmentfault.com/a/1190000010138070)
  - [ELK入门02—Logstash+Log4j2+ES](https://segmentfault.com/a/1190000016192394)
  - [ELK实战-Logstash：监控日志文件](https://blog.csdn.net/K_Zombie/article/details/51156299)
  - [logstash获取nginx日志 两种方法](https://blog.csdn.net/ywmack/article/details/83819058)
  - [把nginx日志写入到logstash中](https://blog.csdn.net/genglei1022/article/details/82349573)


1. 应用日志采集：logstash-input-gelf

- [github logstash-gelf](https://github.com/mp911de/logstash-gelf)
- [logstash-plugins-input gelf](https://www.elastic.co/guide/en/logstash-versioned-plugins/current/v3.1.1-plugins-inputs-gelf.html)

需要在项目中引入依赖`logstash-gelf`，并配置`log4j.xml / log4j2.xml`（或其它log插件）。
（个人：log4j配置中不知道如何实现 `index_name`，所以才改用log4j2）

总体来说，这种实现应该是最简单的。但因为侵入了项目

maven依赖：
```
<dependency>
    <groupId>biz.paluch.logging</groupId>
    <artifactId>logstash-gelf</artifactId>
    <version>x.y.z</version>
</dependency>
```

``` xml
 <!-- log4j2.xml 的gelf主要配置 -->
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

```conf
# logstash 启动 conf
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


2. nginx/tomcat等日志采集：logstash-input-file

无需依赖其余插件/中间件。但需要很好的理解`logstash-input-file`的使用。
例如，Read Mode，Reading from remote network volumes等。
- [logstash-plugin-input file](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-file.html)


缺点：
  1. `path`对远程读取支持不一定理想（**并未找到如何配置远程监听**）。see: [Reading from remote network volumes](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-file.html#_reading_from_remote_network_volumes)
  2. 可能每台需要采集日志的机器都需要安装logstash，并且单独配置。（因为 远程监听 不一定理想）
  3. 采集的日志格式不一样。（这个相对可以接受，也可以通过自定义达到相同日志格式）

```
input {
 file {
    # windows 环境下分隔符必须是`/`，必须是绝对路径，支持正则或类似`logs-{yyyy-MM}.log`
    path => ["D:/VergiLyn/Workspace/Workspace Git/logs-monitor-examples/logs/logs-elk-file.log"]
    start_position => "beginning"
  }
}
output {
  stdout { 
    codec => rubydebug 
  }
  elasticsearch {
    hosts => ["127.0.0.1:9200"]
    index => "logs-elk-file"
  } 
}

```

**总结：**
`logstash-gelf + logstash-input-file` 貌似已经可以满足最简单实现的日志采集。
其实只用 `logstash-input-file` 去监听所有的log文件也可以实现。但是`logstash-gelf`对java应用的日志采集信息更友好。

其次，暂时未测试或未了解其性能及吞吐量瓶颈。