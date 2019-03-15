package com.cu.servlet;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.xml.sax.SAXException;

import com.cu.data.*;
import com.cu.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//验证用户是否登录
		
		HttpSession session = request.getSession();
//		String staff_id = null;
//		if (session != null){
//			staff_id = (String)session.getAttribute("staff_id");
//			if (staff_id == null || staff_id == ""){
//				//response.sendRedirect("login.jsp");
//				if( request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest") ){
//					PrintWriter printWriter = response.getWriter(); 
//					printWriter.write("{\"code\":\"-1\"}"); 
//					printWriter.flush(); 
//					printWriter.close();
//				}else{
//					response.sendRedirect("login.jsp");
//				}
//				return;
//			}
//		}else{
//			if( request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest") ){
//				PrintWriter printWriter = response.getWriter(); 
//				printWriter.write("{\"code\":\"-1\"}"); 
//				printWriter.flush(); 
//				printWriter.close();
//			}else{
//				response.sendRedirect("login.jsp");
//			}
//			return;
//		}
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		JSONObject json = new JSONObject();
		String action = request.getParameter("action");
//		int columns_num = Integer.valueOf(session.getAttribute("columns_num").toString());
		if ("get_table_columns".equals(action)){
			String table_name = request.getParameter("table_name");
			System.out.println(Func.getDatenow() + "----" + table_name);
			List<Table_Columns> list = Table_Columns.query(table_name);
			int num = 0;
			if(list.size() > 0) {
				num = 200;
			}
			session.setAttribute("columns_num", list.size());
			JSONArray jsona = JSONArray.fromObject(list);
			json.put("code", num);
			json.put("list", jsona);
			//System.out.println(json.toString());
		}else if("update_table".equals(action)) {
			
			try {
				String table_name = request.getParameter("table_name");
				String filePath = request.getParameter("filePath");
				String database_name = request.getParameter("database_name");
				int columns_num = Integer.parseInt(request.getParameter("columns_num"));
//				Iterator<Entry<String, String>> it = read_big_excel(filePath);
				ReadBigExcel rExcelUtils = new ReadBigExcel();
				rExcelUtils.process(filePath);
				List<List<String>> list = rExcelUtils.getList();
				System.out.println("List行数："+list.size());
				Map<String, String> map = Table_Columns.insert(database_name, table_name, list,columns_num);
				//System.out.println(map.toString());
				json.put("code", map.get("code"));
				json.put("message", map.get("result"));
			} catch (OpenXML4JException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
			
		}
		PrintWriter out = response.getWriter();
		out.write(json.toString());
		out.flush();
		out.close();
	}
	
	public static Iterator<Entry<String, String>> read_big_excel(String filePath){
		Iterator<Entry<String, String>> it = null;
		try {
	        LargeExcelFileReadUtil example = new LargeExcelFileReadUtil();
	        example.processOneSheet(filePath);
	        LinkedHashMap<String, String>  map=example.getRowContents();
	        it= map.entrySet().iterator();
//	        int count=0;
//	        String prePos="";
//	        while (it.hasNext()){
//	            Map.Entry<String, String> entry=(Map.Entry<String, String>)it.next();
//	            String pos=entry.getKey();
//	            if(!pos.substring(1).equals(prePos)){
//	                prePos=pos.substring(1);
//	                count++;
//	            }
//	            //System.out.println(pos+";"+entry.getValue());
//	        }
//	        System.out.println("解析数据"+count+"条;耗时"+(endtime-time)/1000+"秒");
//	        
//        	File file1 =new File(filePath);
//			FileInputStream is = new FileInputStream(file1);
//			Workbook workbook = WorkbookFactory.create(is);
//			Sheet sheet = workbook.getSheetAt(0);//读取第一个表
//			int rowCount = sheet.getPhysicalNumberOfRows();//获取总行数
//			int colCount = sheet.getRow(0).getPhysicalNumberOfCells();//获取列数
//			System.out.println("导入文件总行数："+rowCount);
//			System.out.println("导入文件总列数："+colCount);
//			for (int i = 0;i < rowCount; i++){	
//				Row row = sheet.getRow(i);//获取当前行
//				JSONObject json1 = new JSONObject();
//				for(int j = 1;j <=colCount; j++) {
//					json1.put("A"+j, row.getCell(j-1)!=null?getValue(row.getCell(j-1)):"");
//				}
//				jsona.add(json1);
//			}
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return it;
	}
	
	
	public static JSONArray read_excel(String filePath){
		JSONArray jsona = new JSONArray();
		try {  
        	File file1 =new File(filePath);
			FileInputStream is = new FileInputStream(file1);
			Workbook workbook = WorkbookFactory.create(is);
			Sheet sheet = workbook.getSheetAt(0);//读取第一个表
			int rowCount = sheet.getPhysicalNumberOfRows();//获取总行数
			int colCount = sheet.getRow(0).getPhysicalNumberOfCells();//获取列数
			System.out.println("导入文件总行数："+rowCount);
			System.out.println("导入文件总列数："+colCount);
			for (int i = 0;i < rowCount; i++){	
				Row row = sheet.getRow(i);//获取当前行
				JSONObject json1 = new JSONObject();
				for(int j = 1;j <=colCount; j++) {
					json1.put("A"+j, row.getCell(j-1)!=null?getValue(row.getCell(j-1)):"");
				}
				jsona.add(json1);
			}
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return jsona;
	}
	
	private static String getValue(Cell cell) {
		String cellValue = "";
		switch (cell.getCellTypeEnum()) {
		    case STRING:
		    	cellValue = cell.getRichStringCellValue().getString();
		        break;
		    case NUMERIC:
		    	if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式 
		            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            cellValue =  sdf1.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
		          } else if (cell.getCellStyle().getDataFormat() == 58) { 
		            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58) 
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		            double value = cell.getNumericCellValue(); 
		            Date date = org.apache.poi.ss.usermodel.DateUtil 
		                .getJavaDate(value); 
		            cellValue = sdf.format(date); 
		          } else {
		            double value = cell.getNumericCellValue(); 
		            CellStyle style = cell.getCellStyle(); 
		            DecimalFormat format = new DecimalFormat(); 
		            String temp = style.getDataFormatString(); 
		            // 单元格设置成常规 
		            if (temp.equals("General")) { 
		              format.applyPattern("#"); 
		            } 
		            cellValue = format.format(value); 
		          } 
		        break;
		    case BOOLEAN:
		    	cellValue = String.valueOf(cell.getBooleanCellValue());
		        break;
		    case FORMULA:
		    	cellValue = String.valueOf(cell.getCellFormula());
		        break;
		    case BLANK:
		    	cellValue = "";
		        break;
		    default:
		    	cellValue = "";
		}
		return cellValue.replace("\"", "");
	}
}
