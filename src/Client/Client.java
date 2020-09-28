/*
 * Client.java
 * Copyright (C) 2020 zhuzhengyi <zhuzhengyi@ZBMAC-C02VQ7GQ7.local>
 *
 * Distributed under terms of the MIT license.
 */


package Client;
import java.io.*;
import java.util.UUID;
import Client.bean.ServerInfo;
public class Client {
    public static void main(String[] args)throws IOException {
        ServerInfo serverInfo = new ServerInfo(UUID.randomUUID().toString(),"127.0.2.16",3001);
        System.out.println("准备发起服务器连接...");
        System.out.println("服务器信息：Addr:"+serverInfo.getAddress()+" /Port:"+serverInfo.getPort());
        try {
            TCPClient.connect(serverInfo);
        }catch (Exception e){
            System.out.println("连接失败，退出");
        }
    }
}


