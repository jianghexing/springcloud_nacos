# springcloud整合nacos做配置中心和注册中心demo

### springcloud整合nacos

```
nacos 快速开始：https://nacos.io/zh-cn/docs/quick-start.html
Nacos Spring Cloud 快速开始:https://nacos.io/zh-cn/docs/quick-start-spring-cloud.html
```

1、 使用springcloud整合nacos 可以参考我写的demo,这里聊聊我踩的坑

```
1） 使用nacos做配置中心，一直想着完全去除配置文件，让所有的配置文件交个nacos 管理;理论上是ok的。整合springboot的nacos 使用springboot的@EnableNacosConfig 注解将我们的配置中心地址和dataId指定。 这里的话比较使用于项目复杂度相对少的
@NacosPropertySource(dataId = "springboot_nacos", autoRefreshed = true)
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "192.168.25.207:8848,192.168.25.209:8848,192.168.25.210:8848"))
```

```
2) 使用springcloud整合nacos做配置中心，项目的配置文件名称改成bootstrap.properties（官方推荐）没改可能会遇到问题；
指定nacos-server 地址
spring.cloud.nacos.config.server-addr=192.168.25.210:8848,192.168.25.207:8848,192.168.25.210:8848        
spring.cloud.nacos.config.prefix=${spring.application.name}
spring.profiles.active=dev
spring.application.name=client-devspring.cloud.nacos.config.file-extension=properties
spring.cloud.nacos.config.group=DEFAULT_GROUP

修改配置 指定dataID 默认情况下，会加载Data ID=${spring.application.name}.properties，Group=DEFAULT_GROUP的配置。(官方加载顺序)
```
聊坑
```
1）nacos集群搭建：https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html
是集群不是多配置地址，所以要注意nacos-server配置文件的配置
2）可能你在测试配置中心的时候会去改数据库的配置信息（我就干过）.会发现坑的好惨，改了数据库的配置信息，发现nacos的缓存配置信息并没有改变。切记不要乱改数据库，改了重新启动一下nacos-server
```

第一次分享：有什么疑问我能帮上的可发我邮箱共同探讨
```
email:jiang_java@163.com
```



