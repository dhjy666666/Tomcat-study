#本项目用于Tomcat容器的学习

###项目环境

本项目基于Idea和Maven；

###项目如何运行

如下图所示配置，然后直接点击run按钮，本地访问http://127.0.0.1:8080/ 即可

![Idea调试配置](/IdeaDebugConfig.jpg)

其中，VM options:

-Dcatalina.home=catalina-home 
-Dcatalina.base=catalina-home 
-Djava.endorsed.dirs=catalina-home/endorsed 
-Djava.io.tmpdir=catalina-home/temp 
-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager 
-Djava.util.logging.config.file=catalina-home/conf/logging.properties


