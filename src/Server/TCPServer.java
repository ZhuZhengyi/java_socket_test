/*
 * TCPServer.java
 * Copyright (C) 2020 zhuzhengyi <zhuzhengyi@ZBMAC-C02VQ7GQ7.local>
 *
 * Distributed under terms of the MIT license.
 */


package Server;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.UUID;
class TCPServer {
    private static int LOCAL_PORT = 3001;
    private static ClientListenHandle clientListenHandle;
    private static ArrayList<ClientHandler> clientHandlerList = new ArrayList<ClientHandler>();
    static void accept() throws IOException {
        //创建服务器端套接字
        ServerSocket serverSocket = createSocket();
        InitSocket(serverSocket);
        System.out.println("服务器准备就绪 addr: " + Inet4Address.getLocalHost() + "  /port: " + LOCAL_PORT);
        System.out.println("开始监听客户端连接...");
        //创建线程监听客户端请求
        clientListenHandle = new ClientListenHandle(serverSocket);
        clientListenHandle.start();
    }
    static void stop() throws IOException {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.socketClose();
        }
        clientHandlerList.clear();
        clientListenHandle.exit();
    }
    private static ServerSocket createSocket() throws IOException {
        ServerSocket socket = new ServerSocket(LOCAL_PORT, 50);
        return socket;
    }
    private static void InitSocket(ServerSocket socket) throws SocketException {
        // 是否复用未完全关闭的地址端口
        socket.setReuseAddress(true);
        // 等效Socket#setReceiveBufferSize
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        // 设置serverSocket#accept超时时间，不设置即永久等待
        // serverSocket.setSoTimeout(2000);
        // 设置性能参数：短链接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1, 1, 1);
    }
    static void broadcast(String msg) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.write(msg);
        }
    }
    /**
     * 监听客户端请求的线程
     */
    static class ClientListenHandle extends Thread {
        private final ServerSocket serverSocket;
        private Boolean done = false;
        ClientListenHandle(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            super.run();
            try {
                do {
                    Socket client;
                    try {
                        client = serverSocket.accept();
                    } catch (Exception e) {
                        continue;//某一个客户端连接失败，要保证其它客户端能正常连接
                    }
                    String uuid = UUID.randomUUID().toString();//为客户端生成唯一标识
                    System.out.println("已接受连接client："+uuid+" /Addr:"+client.getInetAddress()+" /Port:"+client.getPort());
                    //为该客户端实例化一个ClientHandler对象，注入对象删除操作的lambda表达式
                    ClientHandler clientHandle = new ClientHandler(client, handler -> clientHandlerList.remove(handler), uuid);
                    clientHandle.read();
                    clientHandlerList.add(clientHandle);
                } while (!done);
            } catch (Exception e) {
                if (!done) {
                    System.out.println("异常退出！");
                }
            }
        }
        void exit() throws IOException {
            done = true;
            serverSocket.close();
        }
    }
}

