package com.cu.util;

import javax.imageio.ImageIO;  
import java.awt.*;  
import java.awt.image.BufferedImage;  
import java.io.IOException;  
import java.io.OutputStream;  
import java.util.Random; 

/** 
 * 生成验证码图片 
 */  
public class MakeCertPic {  
    private static final Color Color = null;
    
//    private char mapTable[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',  
//            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',  
//            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',  
//            '9',  };  
    private char mapTable[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',  
            'j', 'k' , 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v',  
            'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8',  
            '9',  }; 
  
    /** 
     * 功能:生成彩色验证码图片 参数width为生成图片的宽度,参数height为生成图片的高度,参数os为页面的输出流 
     */  
  
    public String getCerPic(int width, int height, OutputStream os) {  
        if (width < 60) {  
            width = 60;  
        }  
        if (height <= 0) {  
            height = 20;  
        }  
        BufferedImage image = new BufferedImage(width, height,  
                BufferedImage.TYPE_3BYTE_BGR);  
        // 获取图形上下文  
        Graphics graphics = image.getGraphics();  
        // 设定背景颜色  
        graphics.setColor(new Color(0xDCDCDC));  
        graphics.fillRect(0, 0, width, height);  
        // 边框  
        graphics.setColor(Color.black);  
        graphics.drawRect(0, 0, width - 1, height - 1);  
        // 随机产生验证码  
        String strEnsure = "";  
        // 4代表4位验证码  
        for (int i = 1; i <= 4; i++) {  
            strEnsure += mapTable[(int) (mapTable.length * Math.random())];  
        }  
        // 将图形验证码显示在图片中  
        graphics.setColor(Color.black);  
        graphics.setFont(new Font("Atlantic Inline", Font.PLAIN, 20));  
        String str = strEnsure.substring(0, 1);  
        graphics.drawString(str, 8, 17);//8:左右距离,17:上下距离  
        str = strEnsure.substring(1, 2);  
        graphics.drawString(str, 20, 15);  
        str = strEnsure.substring(2, 3);  
        graphics.drawString(str, 35, 18);  
        str = strEnsure.substring(3, 4);  
        graphics.drawString(str, 45, 15);  
        // 随机产生10个干扰点  
        Random random = new Random();  
        for (int i = 0; i <= 10; i++) {  
            int x = random.nextInt(width);  
            int y = random.nextInt(height);  
            graphics.drawOval(x, y, 1, 1);  
        }  
        // 释放图形上下文  
        graphics.dispose();  
  
        try {  
            ImageIO.write(image, "JPEG", os);  
        } catch (IOException e) {  
            e.printStackTrace();  
            return "";  
        }  
        return strEnsure;  
  
    }  
}  