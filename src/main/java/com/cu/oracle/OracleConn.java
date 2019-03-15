package com.cu.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cu.util.Config;

public class OracleConn {

	public Connection getConn() {

		 Connection con = null;
		 	String url = Config.ORACLE_JDBCURL;
			String user = Config.ORACLE_USER;
			String password = Config.ORACLE_PASSWORD;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
	        con = DriverManager.getConnection(url, user, password);// 获取连接
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public static void releaseResource(Connection conn, PreparedStatement ps,
			ResultSet rs) {
		try {
			if (null != rs) {
				rs.close();
			}
			if (null != ps) {
				ps.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseResource(Connection conn, PreparedStatement ps) {
		try {
			if (null != ps) {
				ps.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void releaseResource(Connection conn, CallableStatement statement) {
		try {
			if (null != statement) {
				statement.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
