# BigExcelInsert
Read big excel and insert into Oracle database fast!

本项目是基于maven的Web项目，下载后再Eclipse或者Idea中打开即可。
在日常工作中，经常使用oracle数据库的同事在把Excel导入Oracle时，用PLSQL导入会很慢，使用此项目导入4.1W行数据（3列），仅需要1.2s，速度比较快。

## 使用说明：
## 1、下载项目后，修改src/main/java/com/cu/util/Config.java，修改对应Oracle地址，用户名，密码。

```
public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
public static final String ORACLE_JDBCURL = "jdbc:oracle:thin:@127.0.0.1:1521:database";
public static final String ORACLE_USER = "username"; //Oracle数据库用户名
public static final String ORACLE_PASSWORD = "password"; //Oracle数据库密码
```
  
## 2、修改src/main/java/com/cu/util/Config.java，根据系统设置对应的保存Excel的文件夹
  
  **windows:(在D盘新建对应目录即可)**
```
public static final String EXCEL_UPLOAD_PATH = "D:/BigExcelInsert/upload/";
public static final String UPLOAD_TEMP_PATH = "D:/BigExcelInsert/temp/";
public static final String EXCEL_SAVE_PATH = "D:/BigExcelInsert/download/";
```
  
  **Linux/Mac:(新建对应目录)**
```
public static final String EXCEL_UPLOAD_PATH = "/usr/local/user/upload/";
public static final String UPLOAD_TEMP_PATH = "/usr/local/user/temp/";
public static final String EXCEL_SAVE_PATH = "/usr/local/user/download/";
```
  
  //Mysql 数据库连接无需配置

## 3、系统中安装java环境，下载tomcat8（或者tomcat9），将项目打包成BigExcelInsert.war包后，放到 tomcat/webapps目录下，启动tomcat,
访问 http://localhost:8080/BigExcelInsert/index.jsp即可。

## 4、根据需要导入的Excel表列数，使用PLSQl在数据库中新建需要导入表，例如：

Excel中数据：
姓名	年龄	性别
张三	30	男
李四	31	女
…	…	…

建表：
```
  create table person_test (
    name varchar2(50),
    age varchar2(50),
    sex varchar2(50)
  )
```
  
## 5、在导入到表：中输入 person_test 点击查询，会出来列的展示。
## 6、查询列名成功后，点击选择文件选中需要导入的Excel，点击上传，等待数据读取完成后，会返回对应导入后的示例，检查无误后，点击提交，等待完成即可。


  
 
