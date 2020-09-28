package Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// SERVER : Single Server
// TIPE : One-Way Communication (Client to Server)
// DESCRIPTION :
// A simple server that will accept a single client connection and display everything the client says on the screen.
// If the client user types "exit", the client and the server will both quit.
public class Server {

    //private int port = 8081;
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private BufferedInputStream bis = null;
    private DataInputStream dis = null;

    public Server(int port, int size) {

        int opCount = 0;
        long startTime = System.currentTimeMillis();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + serverSocket.getLocalPort() + "...");
            System.out.println("Waiting for client...");

            socket = serverSocket.accept();
            System.out.println("Client " + socket.getRemoteSocketAddress() + " connected to server...");

            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);

            int i = 1000;
            while (true) {
                try {
                    //String messageFromClient = new String(dis.readNBytes(size)) ;
                    byte[] data = dis.readNBytes(size) ;
                    opCount++;
                    if (data.length < 1) {
                        break;
                    }
                    if (opCount % i == 0 ) {
                        System.out.println("Client [" + socket.getRemoteSocketAddress() + "] : " + opCount);
                    }
                } catch (IOException e) {
                    break;
                }
            }
            dis.close();
            socket.close();
            System.out.println("Client " + socket.getRemoteSocketAddress() + " disconnect from server...");
        } catch (IOException e) {
            System.out.println("Error : " + e);
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("IOPS : " + opCount * 1000 / (endTime - startTime) );
        }
    }

    public static void main(String args[]) {
        int serverPort = 8081;
        int recvSize = 64;
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            recvSize = Integer.parseInt(args[1]);
        }
        Server server = new Server(serverPort, recvSize);
    }
}
