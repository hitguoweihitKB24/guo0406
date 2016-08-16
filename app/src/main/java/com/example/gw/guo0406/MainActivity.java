package com.example.gw.guo0406;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable {
    private Thread thread;	//声明线程对象
    int i;	//循环变量
    int t=1,b=0,a=0;
    boolean suspended = false,p=true;
    android.widget.ImageView image;// 用于显示接收到的图片的ImageView对象
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton startButton=(ImageButton)findViewById(R.id.button1);	//获取“开始”按钮
        textView = (TextView) findViewById(R.id.textView);
        new Thread(new ThreadShow()).start();
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                i=0;
                thread=new Thread(MainActivity.this);	//创建一个线程
                thread.start();	//开启线程
            }
        });
        ImageButton stopButton=(ImageButton)findViewById(R.id.button2);	//获取“停止”按钮
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p)
                {
                    if(thread!=null){
                        suspend();	//中断线程
                        //thread=null;
                    }
                    Log.i("提示：","中断暂停");
                    p=false;
                }else
                {
                    resume();
                    Log.i("提示：", "中断恢复");
                    p=true;
                }
            }
        });
        ImageButton recordButton=(ImageButton)findViewById(R.id.button3);	//获取“功能”按钮
        recordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new ThreadShowA()).start();
//                try {
//                UDPSend udp= new UDPSend();
//                    udp.udpsendA();
//                    System.out.println("阿大声道");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            }
        });
    }
    class ThreadShowA implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub

                try {
                UDPSend udp= new UDPSend();
                    udp.udpsendA();

                }catch (Exception e){
                    e.printStackTrace();
                }

        }
    }

    @Override
    protected void onDestroy() {
        if(thread!=null){
            thread.interrupt();	//中断线程
            thread=null;
        }
        super.onDestroy();
    }
    protected void suspend() {
        suspended = true;
    }

    synchronized void resume() {
        suspended = false;
        notify();
    }
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    // System.out.println("send...");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("thread error...");
                }
            }
        }
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                b = t - a;
                textView.setText("每秒" + Integer.toString(b) + "帧");
                a = t;
               // System.out.println("每秒" + b + "帧");
            }
        }
    };
    @Override
    public void run(){
        try {
            image = (android.widget.ImageView) findViewById(R.id.image);
            // 创建与服务器的连结
            ServerSocket serverSocket=null;
            Socket clientsocket=null;
            try {
                serverSocket = new ServerSocket(1900);
            }catch(IOException e)
            {
                e.printStackTrace();
            }
            while (true) {
                try {
                    clientsocket = serverSocket.accept();
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
                if (clientsocket!=null){
                    break;
                }
            }
            // 获得界面显示图片的ImageView对象
            // 获得套接字的输入流并包装成基本数据输入流
            InputStream inputStream=clientsocket.getInputStream();
            while (true) {
                byte[] buffer = new byte[512*256];
                int count = 0;
                int sum=0;
                while(true)
                {
                //    System.out.println("AAA");
                    count=inputStream.read(buffer,sum,256*512-sum);
                    System.out.println("count="+count);
                    if (count ==0)
                    {
                        break;
                    }
                    if(sum+count==256*512)
                    {
                        break;
                    }
                    sum=sum+count;
                //    System.out.println("buffer="+buffer[100]);
                }
                System.out.println("totalsum="+sum);
                final  Bitmap bitmap = MyBitmapFactory.createMyBitmap(buffer, 512, 256);
                t=t+1;
                synchronized(this)
                {
                    while(suspended)
                    {
                        wait();
                    }
                }
                image.post(new Runnable() {
                    public void run() {
                        image.setImageBitmap(bitmap);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
