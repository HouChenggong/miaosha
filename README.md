## 从零开始打造简易秒杀系统

该项目为基于SpringBoot的简易秒杀系统实战代码

### 对应教程

[【秒杀系统】零基础上手秒杀系统（一）：防止超卖](https://mp.weixin.qq.com/s?__biz=MzU1NTA0NTEwMg==&mid=2247484174&idx=1&sn=235af7ead49a7d33e7fab52e05d5021f&lang=zh_CN#rd)

[【秒杀系统】零基础上手秒杀系统（二）：令牌桶限流 + 再谈超卖](https://mp.weixin.qq.com/s?__biz=MzU1NTA0NTEwMg==&mid=2247484178&idx=1&sn=f4d8072b5408b08f983cae26a6ce1cf5&lang=zh_CN#rd)

[【秒杀系统】零基础上手秒杀系统（三）：抢购接口隐藏 + 单用户限制频率](https://mp.weixin.qq.com/s?__biz=MzU1NTA0NTEwMg==&mid=2247484184&idx=1&sn=8b878e9e730a6e4da27ed336c8201c92&lang=zh_CN#rd)

[【秒杀系统】四：令牌桶+AOP注解实现不同接口不同速率限流](https://mp.weixin.qq.com/s/yfZHnZZCKbpQjueJ9MUJYQ)
[用RabbitMq实现秒杀成功之后的通知](https://github.com/HouChenggong/miaosha/blob/master/md/1%E7%94%A8MQ%E5%8F%91%E9%80%81%E6%B6%88%E6%81%AF%E5%B9%B6%E9%AA%8C%E8%AF%81.md)
[用RabbitMQ实现异步发送邮件或者短信](https://github.com/HouChenggong/miaosha/blob/master/md/rabbitMQSendMsg.md)

### 项目使用简介

项目是SpringBoot工程，并且是父子工程，直接导入IDEA，Eclipse即可使用。

1. 导入miaosha.sql文件到你的MySQL数据库

2. 配置application.properties文件，修改为你的数据库链接地址

3. mvn clean install最外层的父工程（pom.xml）

4. 运行miaosha-web，在POSTMAN或者浏览器直接访问请求链接即可。


## About me


