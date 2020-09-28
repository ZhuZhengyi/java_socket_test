/*
 * ClientHandler.java
 * Copyright (C) 2020 zhuzhengyi <zhuzhengyi@ZBMAC-C02VQ7GQ7.local>
 *
 * Distributed under terms of the MIT license.
 */


package Server;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private final Socket client;
    private final ReadHandler readHandler;
    private final WriteHandle writeHandler;
    private final Removable removable;
    private final String uid;
    ClientHandler(Socket socket, Removable removable, String uid) throws IOException {
        this.client = socket;
        this.readHandler = new ReadHandler(socket.getInputStream());
        this.writeHandler = new WriteHandle(socket.getOutputStream());
        this.removable = removable;
        this.uid = uid;
    }
    void read() {
        readHandler.start();
    }
    void write(String msg) {
        System.out.println("Server -->> " + uid + " : " + msg);
        writeHandler.write(msg);
    }
    /**
     * 把输入输出流和套接字都关闭
     */
    void socketClose(){
        try {
            readHandler.exit();
            writeHandler.exit();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("客户端："+uid+" 套接字连接已关闭");
        }
    }
    /**
     * 把自身从对象列表中清除掉，具体方法是使用lambda表达式来注入的
     */
    void removeClientHandler() {
        removable.removeClientHandle(this);
    }
    /**
     * 定义一个接口，接收lambda表达式
     */
    interface Removable {
        void removeClientHandle(ClientHandler clientHandler);
    }
    /**
     * 输入流操作线程
     */
    class ReadHandler extends Thread {
        private final InputStream inputStream;
        private Boolean flag = true;
        ReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            super.run();
            BufferedReader socketInput = null;
            try {
                socketInput = new BufferedReader(new InputStreamReader(inputStream));
                do {
                    String str = socketInput.readLine();
                    //不知道为什么，客户端关闭时，这里直接报异常，获取不到null
                    if (str.equalsIgnoreCase("exit")) {
                        System.out.println("已无法读取客户端数据！");
                        throw new Exception();
                    }
                    System.out.println(uid + " -->> server : " + str);
                } while (flag);
            } catch (Exception e) {
                if (flag) {
                    System.out.println("读取客户端过程中异常退出");
                    ClientHandler.this.removeClientHandler();
                    ClientHandler.this.socketClose();
                }
            }
        }
        void exit() throws IOException {
            flag = false;
            inputStream.close();
        }
    }
    /**
     * 输出流操作线程，使用单例线程池，可以自动等待任务并处理，无需人工添加阻塞操作
     */
    class WriteHandle {
        private final OutputStream outputStream;
        private final ExecutorService executorService;
        WriteHandle(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.executorService = Executors.newSingleThreadExecutor();
        }
        private void write(String msg){
            executorService.execute(new WriteRunnable(msg,outputStream));
        }
        void exit() throws IOException{
            outputStream.close();
            executorService.shutdown();
        }
        class WriteRunnable implements Runnable{
            private final String msg;
            private final PrintStream printStream;
            WriteRunnable(String msg, OutputStream outputStream) {
                this.msg = msg;
                this.printStream = new PrintStream(outputStream);
            }
            @Override
            public void run() {
                try {
                    printStream.println(msg);
                } catch (Exception e) {
                    System.out.println("打印输出异常！");
                }
            }
        }
    }
}


