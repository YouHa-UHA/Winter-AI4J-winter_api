# Winter-AI4J
Winter-AI4J

## application.yml

```shell
server:
  port: 9090
  tomcat:
    connection-timeout: 180000
spring:
  mvc:
    async:
      request-timeout: 180000
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://121.41.34.23:3306/winter_ai4j?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root2024
    # 数据库连接池使用Druid，开启stat统计和wall防火墙，统计登录密码admin
    # 登录http://localhost:9090/druid/login.html查看
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      filters: stat,wall
      stat-view-servlet:
        login-username: admin
        login-password: admin
        enabled: true
  redis:
    host: 127.0.0.1
    port: 6379
    #    username: default
    password:
    lettuce:
      pool:
        max-active: 200
        max-wait: -1ms
        max-idle: 10
        min-idle: 0
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
minio:
  url: http://
  bucketName: bucket
  accessKey: bucket
  secretKey: bucket
knife4j:
  enable: true
winter:
  corePoolSize: 1000
  maxPoolSize: 1000
  queueCapacity: 20000
  keepAliveSeconds: 3000
sa-token:
  token-name: token
  timeout: 604800
  active-timeout: -1
  is-concurrent: false
  is-share: true
  token-style: uuid
  is-log: true


```

