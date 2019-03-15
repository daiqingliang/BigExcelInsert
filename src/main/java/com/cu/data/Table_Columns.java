package com.cu.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cu.oracle.*;
import com.cu.util.Func;

public class Table_Columns {
	private String column_name;
	private String data_type;
	private String data_length;
	private String comments;
	public String getColumn_name() {
		return column_name;
	}
	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	public String getData_type() {
		return data_type;
	}
	public void setData_type(String data_type) {
		this.data_type = data_type;
	}
	public String getData_length() {
		return data_length;
	}
	public void setData_length(String data_length) {
		this.data_length = data_length;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public static List<Table_Columns> query(String table_name) {
		List<Table_Columns> list = new ArrayList<Table_Columns>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			conn = new OracleConn().getConn();
			String sql = "select b.column_name,b.data_type,b.data_length,a.comments "
					+" from user_col_comments a,all_tab_columns b "
					+" where a.table_name = b.TABLE_NAME "
					+" and a.column_name = b.COLUMN_NAME "
					+" and a.table_name = ? "
					+" order by b.COLUMN_ID ";
			String table_name_upper = Func.convertString(table_name,true);
//			System.out.println(table_name_upper);
			ps = conn.prepareStatement(sql);
			ps.setString(1, table_name_upper);
			rs = ps.executeQuery();
			while(rs.next()) {
				Table_Columns p = new Table_Columns();
				p.setColumn_name(rs.getString("column_name"));
				p.setData_type(rs.getString("data_type"));
				p.setData_length(rs.getString("data_length"));
				p.setComments(rs.getString("comments"));
				list.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OracleConn.releaseResource(conn, ps, rs);
		}
		return list;
	}
	
	public static Map<String, String> insert(String database_name,String table_name,List<List<String>> list_1,int columns_num) {
		Map<String, String> map = new HashMap<>();
		String result = "";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql_str = "";
			List<Table_Columns> list = Table_Columns.query(table_name);
			for(int k = 0;k<list.size();k++){
				if(list.get(k).getData_type().equals("NUMBER")){
					sql_str += "to_number(?),";
				}else if(list.get(k).getData_type().equals("DATE")){
					sql_str += "to_date(?,'yyyy-mm-dd hh24:mi:ss'),";
				}else{
					sql_str += "?,";
				}
			}

			conn = new OracleConn().getConn();
			String sql = " insert into "+table_name+" values (" + sql_str.substring(0, sql_str.length()-1)+")";
			
//			System.out.println(sql);
			conn.setAutoCommit(false);//关闭事物自动提交
			long startTime = System.currentTimeMillis();
			ps = conn.prepareStatement(sql);
	        for(int i = 0; i< list_1.size();i++) {
				List<String> list2 = list_1.get(i);
				for(int j = 0; j < list2.size();j++){
					ps.setString(j+1, list2.get(j));
				}
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			Long endTime = System.currentTimeMillis();
			result = "共计"+list_1.size()+"行数据导入，用时："+(endTime-startTime)+"毫秒";
			map.put("code", "200");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
			map.put("code", "0");
		} finally {
			OracleConn.releaseResource(conn, ps);
		}
		map.put("result", result);
		return map;
	}
}
