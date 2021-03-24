# nacos-1x-mysql-single

| image | tags                     | size      | history                   |
|:------|:-------------------------|:----------|:--------------------------|
| mysql | mysql:8.0.23             | 546.12 MB | mysql                     |
| nacos | nacos/nacos-server:1.3.2 | 914.28 MB | openJDK-1.8, nacos-server |

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
