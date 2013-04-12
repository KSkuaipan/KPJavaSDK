#KPJavaSDK


快盘 Java sdk。


# 使用方法
## 1. 安装到本地maven仓库

    mvn clean install -Dmaven.test.skip=true

## 2. 安装到公司私有maven服务器

修改pom.xml 添加部署配置


    <distributionManagement>
        <repository>
            <id>your repe id</id>
            <name>your repo name</name>
            <url>your repo url</url>
        </repository>
    </distributionManagement>

部署到maven仓库
  
    mvn clean deploy -Dmaven.test.skip=true

  

