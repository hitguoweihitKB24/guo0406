package com.example.gw.guo0406;

import android.graphics.Bitmap;

/**
 * Created by gw on 2016/4/9.
 */
public class MyBitmapFactory {
    static public Bitmap createMyBitmap(byte[] data, int width, int height){
        int []colors = convertByteToColor(data);
        if (colors == null){
            return null;
        }
        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(colors, 0, width, width, height,
                    Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
        return bmp;
    }
    /*
     * 将RGB数组转化为像素数组
     */
    private static int[] convertByteToColor(byte[] data){
        int size = data.length;
        int []color = new int[size];
      //  int red, green, blue;
       										//  正好是3的倍数
            for(int i = 0; i < color.length; ++i){

                color[i] = (data[i] << 16 & 0x00FF0000) |
                        (data[i] << 8 & 0x0000FF00 ) |
                        (data[i] & 0x000000FF ) |
                        0xFF000000;
            }
            color[color.length - 1] = 0xFF000000;					// 最后一个像素用黑色填充
        return color;
    }
}


