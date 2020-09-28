/*
 * Server.java
 * Copyright (C) 2020 zhuzhengyi <zhuzhengyi@ZBMAC-C02VQ7GQ7.local>
 *
 * Distributed under terms of the MIT license.
 */


package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Date;

public class Server {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
    public static void main(String[] args){
        try {
            TCPServer.accept();
            new Timer("Timer").schedule(new TimerTask() {
                @Override
                public void run() {
                    TCPServer.broadcast(df.format(new Date()));
                }
            }, 1000,5000);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String str;
            //因为ClientListen是异步线程，使用键盘输入流将主线程阻塞住，保证跟ClientListen线程同步，同时可控制ClientListen服务的退出
            do{
                str = bufferedReader.readLine();
            }while (str.equalsIgnoreCase("serverExit"));
        }catch (Exception e){
            System.out.println("监听请求过程中异常退出");
        }
        try {
            TCPServer.stop();
        } catch (IOException e) {
            System.out.println("关闭套接字过程中出现异常");
        } finally {
            System.out.println("服务器端套接字已关闭！");
        }
    }
}

