package com.example.gw.guo0406;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by gw on 2016/5/26.
 */
public  class UDPSend  {
    static public void udpsendA() throws Exception{

        DatagramSocket socket=new DatagramSocket();

        String str ="00";

        DatagramPacket pack=new DatagramPacket(str.getBytes(),0,str.length(),InetAddress.getByName("192.168.43.45"),1999);
        System.out.println("发送k");
        socket.send(pack);

        socket.close();
        System.out.println("发送完毕");

    }
}
