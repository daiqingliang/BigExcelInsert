package com.cu.util;

public class Config {

	public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String ORACLE_JDBCURL = "jdbc:oracle:thin:@127.0.0.1:1521:qdjf";
	public static final String ORACLE_USER = "username"; //Oracle数据库用户名
	public static final String ORACLE_PASSWORD = "password"; //Oracle数据库密码
	
	//windows
	public static final String EXCEL_UPLOAD_PATH = "D:/BigExcelInsert/upload/";
	public static final String UPLOAD_TEMP_PATH = "D:/BigExcelInsert/temp/";
	public static final String EXCEL_SAVE_PATH = "D:/BigExcelInsert/download/";
	
	//linux or mac
//	public static final String EXCEL_UPLOAD_PATH = "/usr/local/user/upload/";
//	public static final String UPLOAD_TEMP_PATH = "/usr/local/user/temp/";
//	public static final String EXCEL_SAVE_PATH = "/usr/local/user/download/";
	
	public static final String MYSQL_JDBCURL = "";
	public static final String MYSQL_Driver = "";
	public static final String MYSQL_USER = "";
	public static final String MYSQL_PASSWORD = "";
}
