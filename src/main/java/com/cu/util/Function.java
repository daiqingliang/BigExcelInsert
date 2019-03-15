package com.cu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;

public class Function {
	
	
	/**
	 * base64字符串转化成图片  
	 * @param imgStr
	 * @return
	 */
    public static boolean Base64ToImage(String imgStr)  
    {   //对字节数组字符串进行Base64解码并生成图片  
        if (imgStr == null) //图像数据为空  
            return false;  
        BASE64Decoder decoder = new BASE64Decoder();  
        try   
        {  
            //Base64解码  
            byte[] b = decoder.decodeBuffer(imgStr);  
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpeg图片  
            String imgFilePath = "d://222.jpg";//新生成的图片  
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b);  
            out.flush();  
            out.close();  
            return true;  
        }   
        catch (Exception e)   
        {  
            return false;  
        }  
    }
    
    /**
     * 计算文件大小 流方式
     * @param filePath
     * @return
     */
    public static long GetFileSizeByStream(String filePath){
        long fileSize=0l;
        FileChannel fc= null;  
        try {  
            File f= new File(filePath);  
            if (f.exists() && f.isFile()){  
                FileInputStream fis= new FileInputStream(f);  
                fc= fis.getChannel();  
                fileSize=fc.size()/1024;
                fis.close();
            }else{  
                //logger.info("file doesn't exist or is not a file");  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();
        } catch (IOException e) {  
        	e.printStackTrace();  
        } finally {  
            if (null!=fc){  
                try{  
                    fc.close(); 
                }catch(IOException e){  
                	e.printStackTrace();
                }  
            }   
        }
        return fileSize;
    }
    
    /*
     * 计算文件大小 io方式
     */
    public static int GetFileSizeByFile(String filePath) {
    	int fileSize=0;
        File f= new File(filePath);  
        if (f.exists() && f.isFile()){  
        	fileSize = (int)f.length()/1024;
        }
        return fileSize;
    }
    
    /**
     * 获取Mac地址
     * @param request
     * @return
     */
    public static String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
    
    /**
     * 获取Mac地址
     * @param ip
     * @return
     */
	public static String getMACAddress(String ip) {
		String str = "";
		String macAddress = "";
		try {
			Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader ir = new InputStreamReader(p.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("MAC Address") > 1) {
						macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return macAddress;
	}
	
	/**
	 * 获取当前时间，参数为需要的格式，例如：yyyy-MM-dd HH:mm:ss
	 * @param fString 返回时间格式
	 * @return String 当前时间
	 */
	public static String getDatenow(String fString) {
		String now="";
		if (fString.equals(null) || fString.equals("")){
			fString="yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat df= new SimpleDateFormat(fString);
		Date nowDate = new Date();
		now =df.format(nowDate);
		return now;
	}
	
	/**
	 * 直接获取当前时间，格式为yyyy-MM-dd HH:mm:ss
	 * @return String 当前时间
	 */
	public static String getDatenow() {
		String now="";
		String fString="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat df= new SimpleDateFormat(fString);
		Date nowDate = new Date();
		now =df.format(nowDate);
		return now;
	}
	
	/**
	 * 返回随机指定位数的随机数(1-9)
	 * @param num 返回随机数的位数
	 * @return String 随机数
	 */
	public static String rand(int num) {
		Random r = new Random();
		String verify = "";
		for( int i=1 ; i<=num ; i++){
			verify += String.valueOf(r.nextInt(9) + 1);
		}
		return verify;
	}
}
