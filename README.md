[TOC]

# 项目介绍

[项目地址](https://github.com/dianping/cat)
介绍：

> cat是大众点评开源的一套基于java的实时应用监控平台，主要应用于服务中间件框架（MVC 框架、RPC 框架、持久层框架、分布式缓存框架）的监控，为开发和运维提供各项性能指标、健康检查、自动报警等可视化服务。

# 实战
> 关于项目介绍的文章已经很多了，现在想办法把服务搭建起来，看看项目效果如何，是否能达到自己的项目需求。未来方便起见，使用docker部署。

机器配置说明：
* 1c2g
* Centos 7
* Docker version 18.09.0, build 4d60db4

## 1. Cat服务器部署

### 1.1容器构建
先把项目从github拉取下来，进入到 `docker`目录，启动容器。(暂时只考虑单机部署), 这个构建过程可能需要20分钟左右。 

```
git clone https://github.com/dianping/cat.git
cd ./cat/docker
docker-compose up
```
安装过程遇见的问题：
1.数据库并没有启动起来
```
cat-mysql | Initializing database
cat-mysql | mysqld: Can't create/write to file '/var/lib/mysql/is_writable' (Errcode: 13 - Permission denied)
cat-mysql | 2019-01-06T08:15:45.931202Z 0 [Warning] TIMESTAMP with implicit DEFAULT value is deprecated. Please use --explicit_defaults_for_timestamp server option (see documentation for more details).
cat-mysql | 2019-01-06T08:15:45.932753Z 0 [ERROR] --initialize specified but the data directory exists and is not writable. Aborting.
cat-mysql | 2019-01-06T08:15:45.932773Z 0 [ERROR] Aborting
```

网上查了好多方法，试了下，都没怎么解决，最后修改`docker-compose.yml`文件，把`mysql`换成了`mariadb`数据库。
```
 mysql:
    container_name: cat-mysql
    image: mariadb:10.2
    # expose 33306 to client (navicat)
    # user: "1000:50"
    # user: "1000"
    ports:
       - 33306:3306
    volumes:
      # change './docker/mysql/volume' to your own path
      # WARNING: without this line, your data will be lost.
      - "./mysql/volume:/var/lib/mysql/data"
      # 第一次启动，可以通过命令创建数据库表 ：
      # docker exec 容器id bash -c "mysql -uroot -Dcat < /init.sql"
      - "../script/CatApplication.sql:/init.sql"
    # command: mysqld -uroot --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --init-connect='SET NAMES utf8mb4' --innodb-flush-log-at-trx-commit=0
    command: mysqld -uroot --character-set-server=utf8mb4
    environment:
      - MYSQL_ALLOW_EMPTY_PASSWORD=yes
      - MYSQL_DATABASE=cat
      - MYSQL_USER=root
      - MYSQL_PASSWORD=

```
启动成功后通过`docker ps`会有两个容器启动
```
 ➜  docker git:(master) ✗ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                            NAMES
869de049e824        docker_cat          "/bin/sh -c 'chmod +…"   30 hours ago        Up 30 hours         0.0.0.0:2280->2280/tcp, 0.0.0.0:8080->8080/tcp   cat
695b3745e4b3        mariadb:10.2        "docker-entrypoint.s…"   30 hours ago        Up 30 hours         0.0.0.0:33306->3306/tcp                          cat-mysql
```


### 1.2 创建表
```
# 进入mariadb 容器里面，查init.sql 文件是否存在
➜  docker git:(master) ✗ docker exec -it 695b3745e4b3  /bin/bash
root@695b3745e4b3:/# ls -l
-rw-rw-r--   1 1000 1000 15120 Jan  5 05:02 init.sql
# 退出容器，创建数据库表
root@695b3745e4b3:/# exit
➜  docker git:(master) ✗ docker exec 695b3745e4b3 bash -c "mysql -uroot -Dcat < /init.sql"
```
### 1.3 配置 
* 登录：http://211.159.147.25:8080/cat/s/login ，用户名：admin 密码：admin
* 通过浏览器配置(进入到`Configs` -> `全局系统配置` -> `客户端路由`)
	- 1 客户端路由路由配置(**27.0.0.1 替换为你本机的实际的内网IP，比如说192.168.1.1**)
	-  服务端配置 (`控制台`,`消费机`,`告警端`,`任务机`)



## 2. 创建SpringBoot 项目
### 2.1 通过maven添加依赖(另一种方式是直接下载cat-client-3.0.0.jar包)
> 通过[mvnrepository  cat-client](https://mvnrepository.com/artifact/com.dianping.cat/cat-client)只有`3.0.0`版本，引用的过程发现找不到这个包，`又去看了cat源码的pom.xml文件`，发现是增加了`repositories`依赖。
```xml
<dependency>
    <groupId>com.dianping.cat</groupId>
    <artifactId>cat-client</artifactId>
    <version>${cat.version}</version>
</dependency>

 <repositories>
    <repository>
      <id>central</id>
      <name>Maven2 Central Repository</name>
      <layout>default</layout>
      <url>http://repo1.maven.org/maven2</url>
    </repository>
    <repository>
      <id>unidal.releases</id>
      <url>http://unidal.org/nexus/content/repositories/releases/</url>
    </repository>
  </repositories>
  <pluginRepositories>
    <pluginRepository>
      <id>central</id>
      <url>http://repo1.maven.org/maven2</url>
    </pluginRepository>
    <pluginRepository>
      <id>unidal.releases</id>
      <url>http://unidal.org/nexus/content/repositories/releases/</url>
    </pluginRepository>
  </pluginRepositories>

```
### 2.2 准备工作
### 准备工作1
> `cat-client ` 要去` /data/appdatas/cat/client.xml` 找服务，进行数据上报。线上环境，应该由运维人员专人负责。
> mac 开发环境下，需要先修改写的权限，`chmod -R 777 /data`

* 创建 /data/appdatas/cat 目录，具有读写权限。
* 创建 /data/applogs/cat 目录 (可选)
* 创建 /data/appdatas/cat/client.xml
```
<?xml version="1.0" encoding="utf-8"?>
<config xmlns:xsi="http://www.w3.org/2001/XMLSchema" xsi:noNamespaceSchemaLocation="config.xsd">
    <servers>
        <server ip="<cat server ip address>" port="2280" http-port="8080" />
    </servers>
</config>
<!-- 不要忘记把 `<cat server IP address>`替换成你自己的服务器地址哦！ -->
```

### 准备工作2
项目中创建 `src/main/resources/META-INF/app.properties`文件, 通过`app.name` 在 搜索对应的项目，这个也是必须得，在启动的时候会报错。
```
app.name={appkey}
```

> 对于项目启动的过程中出现的问题，可以通过`tail -f /data/applogs/cat/cat_client_.log ` 查看对应日志，可以方便定位出现的问题。


##  3. API list
* Transaction
如果不用`URL集成组件`，手动添加埋点，模板代码如下
```
// 创建一个Transaction, `HelloController`看做一个类，一个类下面 `hello`具体方法
Transaction transaction = Cat.newTransaction("HelloController", "hello");

try {
    // 具体业务代码
    t.setStatus(Transaction.SUCCESS);
} catch (Exception e) {
    t.setStatus(e);
    Cat.logError(e);
} finally {
    t.complete();
}
```
![transaction.png](https://i.loli.net/2019/01/09/5c35cf5ad016d.png)


* Event  记录事件
```
# 成功事件
Cat.logEvent("URL.Server", "serverIp");
Cat.logEvent("URL.Server", "serverIp", Event.SUCCESS, "ip=${serverIp}");
# 失败事件
Cat.logError(e);
```
* Metric (记录业务指标)
```

```


* 

## 4. demo中使用到的 [集成组件](https://github.com/dianping/cat/tree/master/integration) 

>  这里面有一些集成的组件，可参考使用

* [URL监控埋点](https://github.com/dianping/cat/tree/master/integration/URL) : http请求过来会自动打点，记录所有的HTTP请求，即使错误的请求也会记录，
  - 对 shop/1 shop/2 都会自动归类到 /shop/{num}
  - shop/v1 shop/v2 等不会自动到/shop/v{num}

* [Log4j2配置](https://github.com/dianping/cat/tree/master/integration/log4j2)



## CAT报表
* [Transaction报表](https://github.com/dianping/cat/blob/master/cat-doc/posts/ch1-report/transaction.md): 监控一段代码 `运行次数`，`OPS`，`错误次数`，`失败率`，`响应时间统计（平均影响时间、Tp分位值）`等等。
![Transaction报表.png](https://i.loli.net/2019/01/09/5c35c973b1059.png)

* [Event报表](https://github.com/dianping/cat/blob/master/cat-doc/posts/ch1-report/event.md): 同样监听一段代码的运行次数，和Transaction表表功能类似，缺少相应时间的统计。
* [Problem报表](https://github.com/dianping/cat/blob/master/cat-doc/posts/ch1-report/problem.md) 对一些异常，错误，响应时间过长（如：慢查询，网络延迟高），系统性能排查有用。
![Problem报表.png](https://i.loli.net/2019/01/09/5c35cacd8fe5b.png)
* [Heartbeat报表](https://github.com/dianping/cat/blob/master/cat-doc/posts/ch1-report/heartbeat.md): 机器信息，JVM状态信息
![Heartbeat报表.png](https://i.loli.net/2019/01/09/5c35caae87645.png)

* [Business报表](https://github.com/dianping/cat/blob/master/cat-doc/posts/ch1-report/business.md)： 偏重`业务指标`, 如`订单数`，`新注册用户数`
![](https://i.loli.net/2019/01/09/5c35ca8877e80.png)

# 参考
1. [Githup 介绍](https://github.com/dianping/cat)
2. [Docker 启动部分参考](https://github.com/dianping/cat/blob/master/cat-doc/posts/ch4-server/README.md)
2. [大众点评Cat监控系统的部署与操作说明](https://my.oschina.net/yanyimin/blog/1517724)
3. [MySQL docker 5.7.6 and later fails to initialize database](https://github.com/docker-library/mysql/issues/69)
4. [美团开源监控框架CAT-Spring Boot Demo](https://blog.csdn.net/chentravelling/article/details/84780086)
5. [深度剖析开源分布式监控CAT](https://tech.meituan.com/CAT_in_Depth_Java_Application_Monitoring.html)