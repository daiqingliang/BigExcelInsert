package com.cu.servlet;

import java.io.*;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.cu.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
       
    public DownloadServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//doGet(request, response);
		//验证用户是否登录
		HttpSession session = request.getSession(false);
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
		
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		int columns_num = Integer.valueOf(session.getAttribute("columns_num").toString());
//		System.out.println("columns_num = :"+columns_num);
		if (action.equals("excel_upload")){ //上门激活订单导入，对应MyTask
			JSONObject json = new JSONObject();
			String code = "200";
			// 检测是否为多媒体上传
			if (!ServletFileUpload.isMultipartContent(request)) {
			    // 如果不是则停止
//			    System.out.println("Error: 表单必须包含 enctype=multipart/form-data");
			    return;
			}
			String uploadPath = Config.EXCEL_UPLOAD_PATH;
			String tempPath = Config.UPLOAD_TEMP_PATH; // 临时文件目录
			int sizeMax = 500;
			//String[] fileType = new String[]{".jpg",".gif",".bmp",".png",".jpeg",".ico"};
			String[] fileType = new String[]{".xls","xlsx"};
			 //Servlet初始化时执行,如果上传文件目录不存在则自动创建  
	        if(!new File(uploadPath).isDirectory()){  
	            new File(uploadPath).mkdirs();  
	        }  
	        if(!new File(tempPath).isDirectory()){  
	            new File(tempPath).mkdirs();  
	        }  
	  
	        DiskFileItemFactory factory = new DiskFileItemFactory(); 
	        factory.setSizeThreshold(500*1024); //最大缓存  
	        factory.setRepository(new File(tempPath));//临时文件目录  
	          
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        upload.setSizeMax(sizeMax*1024*1024);//文件最大上限  
	          
	        String filePath = null;  
	        try {  
	            List<FileItem> items = upload.parseRequest(request);//获取所有文件列表  
	            for (FileItem item : items) {  
	                //获得文件名，这个文件名包括路径  
	                if(!item.isFormField()){
	                    //文件名  
	                    String fileName = item.getName().toLowerCase();  
	                    if(fileName.endsWith(fileType[0]) || fileName.endsWith(fileType[1])){  
	                        String uuid = UUID.randomUUID().toString();  
	                        filePath = uploadPath+uuid+fileName.substring(fileName.lastIndexOf("."));  
	                        item.write(new File(filePath));
	                        int rowCount = 0;
	                        JSONArray jsona = new JSONArray();
	                        try {  
//	                        	System.out.println("导入"+fileName+"文件成功！");
	                			Long time=System.currentTimeMillis();
	                			ReadBigExcel h = new ReadBigExcel();
	                			h.process(filePath);
	                			List<List<String>> result_list = h.getList();
	                	        Long endtime=System.currentTimeMillis();
	                	        
	                	        rowCount = result_list.size();
	                	        int excel_col_num = 0;
	                	        //当表的字段个数大于Excel列数报错--解决
	                	        if (rowCount > 0) {
	                	        	excel_col_num = result_list.get(0).size();
	                	        } else {
	                	        	code = "0";//excel读取失败
	                	        }
	                	        if ((columns_num - excel_col_num) != 0 ) {
	                	        	code = "1";//表字段数和Excel列数不一样
//	                	        	System.out.println("表字段数和Excel列数不一样");
	                	        } else {
	                	        	JSONObject json1 = new JSONObject();
		                	        for (int i = 0; i < 10; i++) {
		                	        	for (int col = 0; col < columns_num; col++) {
			                	            json1.put("A"+(col+1),result_list.get(i).get(col));
		                	        	}
		                	        	jsona.add(JSONObject.fromObject(json1.toString()));
	                	            	json1.clear();
		                	        }
//		                	        System.out.println("解析Excel"+filePath+";耗时"+(endtime-time)/1000+"秒");
	                	        }
	                	        
	                	        
	                        } catch (Exception e) {  
	                            e.printStackTrace();  
	                        }
	                        PrintWriter out = response.getWriter();
	            			//out.print("<script language='javascript'>alert('上传成功！');location.href='smjh_insert.jsp';</script>");
	                        json.put("code", code);
	                        json.put("list", jsona.toString());
	                        json.put("filePath", filePath);
	                        json.put("rowCount", rowCount);
//	                        System.out.println(json.toString());
	                        out.write(json.toString());
	            			out.flush();
	            			out.close();
	                    }else{
//	                    	System.out.println("文件格式不正确");
	                    	json.put("code", "-1");
	                        PrintWriter out = response.getWriter();
	            			//out.write("<script language='javascript'>alert('上传失败,请确认上传的文件存在并且类型是EXCEL文件!');location.href='index.jsp';</script>");
	            			out.write(json.toString());
	                        out.flush();
	            			out.close();
	                    }  
	                }  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();
	            PrintWriter out = response.getWriter();
    			out.print("<script language='javascript'>alert('上传失败,请确认上传的文件大小不能超过"+sizeMax+"M!');location.href='number_modify.jsp';</script>");
    			out.flush();
    			out.close();
	        }  
		}
	}
}
