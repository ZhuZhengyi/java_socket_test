package Client;
import Client.bean.ServerInfo;
import java.io.*;
import java.net.*;
class TCPClient {
    static void connect(ServerInfo serverInfo) throws IOException {
        Socket clientSocket = createSocket();//建立套接字
        InitSocket(clientSocket);//初始化套接字
        //连接远程服务器
        clientSocket.connect(new InetSocketAddress(serverInfo.getAddress(), serverInfo.getPort()), 3000);
        System.out.println("已连接server");
        try {
            //输入流线程
            ReadHandle readHandle = new ReadHandle(clientSocket.getInputStream());
            readHandle.start();
            //输出流
            write(clientSocket);
            //当输出流结束时，关闭输入流
            readHandle.exit();
        } catch (Exception e) {
            System.out.println("出现异常！");
        } finally {
            clientSocket.close();
            System.out.println("客户端结束");
        }
    }
    private static Socket createSocket() throws IOException {
        Socket socket = new Socket();
        return socket;
    }
    private static void InitSocket(Socket socket) throws SocketException {
        // 设置读取超时时间为2秒，超过2秒未获得数据时readline报超时异常；不设置即进行永久等待
        //socket.setSoTimeout(2000);
        // 是否复用未完全关闭的Socket地址，对于指定bind操作后的套接字有效
        socket.setReuseAddress(true);
        // 是否开启Nagle算法
        socket.setTcpNoDelay(true);
        // 是否需要在长时无数据响应时发送确认数据（类似心跳包），时间大约为2小时
        socket.setKeepAlive(true);
        // 对于close关闭操作行为进行怎样的处理；默认为false，0
        // false、0：默认情况，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        // true、0：关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对方，并无需经过2MSL等待
        // true、200：关闭时最长阻塞200毫秒，随后按第二情况处理
        socket.setSoLinger(true, 20);
        // 是否让紧急数据内敛，默认false；紧急数据通过 socket.sendUrgentData(1);发送
        socket.setOOBInline(true);
        // 设置接收发送缓冲器大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);
        // 设置性能参数：短链接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1, 1, 1);
    }
    /**
     * 输出流方法
     */
    private static void write(Socket socket) throws IOException {
        //构建键盘输入流
        InputStream in = System.in;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        //得到socket输出流并转化为打印流
        OutputStream outputStream = socket.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        for(;;){
            String str = bufferedReader.readLine();//从键盘输入获取内容
            printStream.println(str);//通过打印流输出
            if(str.equalsIgnoreCase("exit")){
                break;
            }
        }
        printStream.close();
        System.out.println("输出流关闭");
    }
    /**
     * 输入流线程
     */
    static class ReadHandle extends Thread {
        private final InputStream inputStream;
        private Boolean done = false;
        ReadHandle(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            super.run();
            try {
                //获取输入流
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));
                do {
                    String str;
                    str = socketInput.readLine();
                    if (str==null) {
                        break;
                    }
                    System.out.println("From server: "+ str);
                } while (!done);
            } catch (Exception e) {
                if (!done) {
                    System.out.println("异常断开，或者输入异常");
                }
            }
        }
        void exit() {
            done = true;
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                System.out.println("输入流关闭");
            }
        }
    }
}


