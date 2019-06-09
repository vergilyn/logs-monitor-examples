# elk-gelf-examples

参考:
  - [log4j2 和logstash整合](https://segmentfault.com/a/1190000010138070)
  - [ELK入门02—Logstash+Log4j2+ES](https://segmentfault.com/a/1190000016192394)
  
[github logstash-gelf](https://github.com/mp911de/logstash-gelf)
log4j --> logstash --> elasticsearch

application 通过中间件 logstash-gelf.jar 将log传递到 logstash，然后logstash ->es 


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
    index => "logs-2019"
  } 
  
}
```