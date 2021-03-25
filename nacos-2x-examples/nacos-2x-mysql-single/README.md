# nacos-2x-mysql-single

**PS: 与nacos-1x完全一样**

| image | tags                     | size      | history                   |
|:------|:-------------------------|:----------|:--------------------------|
| mysql | mysql:8.0.23             | 546.12 MB | mysql                     |
| nacos | nacos/nacos-server:2.0.0 | 974.66 MB | openJDK-1.8, nacos-server |

docker-mysql:
+ <https://hub.docker.com/_/mysql>
+ <https://dev.mysql.com/doc/refman/5.7/en/environment-variables.html>
+ <https://github.com/docker-library/postgres>

docker-nacos-server:
+ <https://hub.docker.com/r/nacos/nacos-server>
+ <https://github.com/nacos-group/nacos-docker/tree/1.3.2>

**NOTE:**
1. docker-nacos 的 `application.properties` 与非docker `application.properties.example` 内容是不一样的。

2. nacos 启动log `D:/docker-volumes/nacos-1x-mysql/nacos/logs/start.out`

3. docker-mysql init-sql order?
+ [issues#744, Database initialization scripts exec order](https://github.com/docker-library/postgres/issues/744)
```
# 根据 目录`/docker-entrypoint-initdb.d/`下文件的自然顺序（字母）
# 所以，可以 通过重命名文件来达到 强顺序执行sql
[001]nacos-mysql.sql  
[002]1.4.0-ipv6_support-update.sql
```

## nacos-1.x VS nacos-2.x
1. `nacos-mysql.sql`
- [1.4.0-ipv6_support-update.sql](mysql/%5B002%5D1.4.0-ipv6_support-update.sql)
1.x: `src_ip varchar(20)`  
2.x: `src_ip varchar(50)`

2. TODO  
docker `application.properties` 沿用的是 `1.4.1`，并未 `2.0.0`的配置。

