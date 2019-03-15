package com.cu.util;

import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;

public class Func {
	
	/**
	 * 转大小写，true：转大写， false：转小写
	 * @param str
	 * @param beginUp
	 * @return
	 */
	public static String convertString(String str, Boolean beginUp){  
	    char[] ch = str.toCharArray();  
	    StringBuffer sbf = new StringBuffer();  
	    if(beginUp) {
	    	for(int i=0; i< ch.length; i++){  
	    		sbf.append(charToUpperCase(ch[i]));  
	    	}  
	    }else{
	    	for(int i=0; i< ch.length; i++){  
	    		sbf.append(charToLowerCase(ch[i]));  
	    	}
	    }
	    return sbf.toString();  
	}  
	      
	/**转大写**/  
	public static char charToUpperCase(char ch){  
	    if(ch <= 122 && ch >= 97){  
	        ch -= 32;  
	    }  
	    return ch;  
	}  
	/***转小写**/  
	public static char charToLowerCase(char ch){  
	    if(ch <= 90 && ch >= 65){  
	        ch += 32;  
	    }  
	    return ch;  
	} 
	
	/**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost_q(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！URL="+url+"; Param="+param);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
	
	/**
	 * 生成随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { //length表示生成字符串的长度  
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 }
	
	/**
	 * 取当前时间几分钟后的时间
	 * @param minute
	 * @return
	 */
	public static String getTimeByMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(calendar.getTime());

    }
	
	/**
	 * 取当前时间几天后的时间
	 * @param minute
	 * @return
	 */
	public static String getTimeByDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(calendar.getTime());

    }
	/**
	 * 根据开始日期和天数计算结束日期
	 * @param startDate
	 * @param days
	 * @return
	 */
	public static String DateAddDay(String startDate,int days){
		String format = "yyyyMMdd";
		if (startDate.contains("-")){
			format = "yyyy-MM-dd";
		}
		String endDate = "";
		Date fDate=new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);  
		    fDate = sdf.parse(startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(fDate);
		aCalendar.add(Calendar.DATE, days);
		SimpleDateFormat s=new SimpleDateFormat(format);
		endDate=s.format(aCalendar.getTime());
		return endDate;
	}
	/**
	 * 计算天数差
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	public static int daysOfTwo(String Date1, String Date2) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");  
        Calendar cal = Calendar.getInstance();    
        try {
			cal.setTime(sdf.parse(Date1));
		} catch (ParseException e) {
			e.printStackTrace();
		}    
        long time1 = cal.getTimeInMillis();                 
        try {
			cal.setTime(sdf.parse(Date2));
		} catch (ParseException e) {
			e.printStackTrace();
		}    
        long time2 = cal.getTimeInMillis();         
        long between_days = (time1-time2)/(1000*3600*24) ;
        return Integer.parseInt(String.valueOf(between_days));
	}
	
	
	
	/**
	 * 根据格式获取几天后的时间
	 * @param startDate
	 * @param days
	 * @param format
	 * @return
	 */
	public static String DateAddDay(String startDate,int days,String format){
		String endDate = "";
		Date fDate=new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);  
		    fDate = sdf.parse(startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(fDate);
		aCalendar.add(Calendar.DATE, days);
		SimpleDateFormat s=new SimpleDateFormat(format);
		endDate=s.format(aCalendar.getTime());
		return endDate;
	}

	public static boolean isMobileNO(String mobiles) {

		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		// System.out.println(m.matches() + "---");
		return m.matches();
	}
	
	/**
	 * Luhn iccid校验
	 * @param iccid 共计20位，完整iccid
	 * @return result 返回是否为正确iccid
	 */
	public static boolean luhn_check(String iccid){
		boolean result = false;
		int oldsum = 0, doubelsum = 0;
		boolean ifoop = true;
		
		for ( int i=iccid.length()-1; i>=0; i--){
			
			int num_this = Integer.parseInt(iccid.substring(i, i+1));
			if (ifoop) {
				oldsum += num_this;
				ifoop = false;
			} else {
				num_this = num_this*2;
				if (num_this>9) {
					num_this = num_this-9;
				}
				doubelsum +=num_this;
				ifoop = true;
			}
			
		}
		
		int sum = oldsum+doubelsum;
		System.out.println("iccid校验："+sum+"="+oldsum+"+"+doubelsum);
		if ((sum % 10) == 0) {
			result = true;
		}
		return result;
	}
	
	
	
	/**
	 * Luhn 算法计算iccid校验位
	 * @param iccid 19位数字加补位0，共计20位
	 * @return 返回20位正确iccid
	 */
	public static String luhn_iccid(String iccid){
		String iccid1=iccid+"0";
		int result = 0;
		int oldsum = 0, doubelsum = 0;
		boolean ifoop = true;
		
		for ( int i=iccid1.length()-1; i>=0; i--){
			
			int num_this = Integer.parseInt(iccid1.substring(i, i+1));
			if (ifoop) {
				oldsum += num_this;
				ifoop = false;
			} else {
				num_this = num_this*2;
				if (num_this>9) {
					num_this = num_this-9;
				}
				doubelsum +=num_this;
				ifoop = true;
			}
			
		}
		
		int sum = oldsum+doubelsum;
		//System.out.println(sum+"="+oldsum+"+"+doubelsum);
		result = sum % 10;
		if (result == 0) {
			result = 0;
		}else{
			result = 10 -result;
		}
		return iccid+String.valueOf(result);
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
	
	/*
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
	
	/*
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
	
	
	/*
     * 获取ip地址
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
	
	/*
     * 
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
    
    /*
	 * base64字符串转化成图片  
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
    
    /*
     * 计算文件大小 流方式
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
}
