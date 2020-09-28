package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Client for Server1, Server2, Server3
public class Client {

    int opCount = 0;
    private Socket socket = null;
    private DataOutputStream dos = null;

    long startTime = System.currentTimeMillis();
    public Client(String serverName, int serverPort, String message) {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client started on port " + socket.getLocalPort()+"...");
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    dos.writeBytes(message);
                    dos.flush();
                    opCount++;
                } catch (IOException e) {
                    break;
                }
            }
            dos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("IOPS : " + opCount * 1000 / (endTime - startTime) );
        }
    }

    public static void main(String args[]) {
        String serverName = "localhost";
        int serverPort = 8081;
        String message = "xx" +
                        "0123456789" +
                        "0123456789" +
                        "0123456789" +
                        "0123456789" +
                        "0123456789" +
                        "0123456789" +
                        "xx" ;
        if (args.length > 0) {
            serverName = args[0];
        }
        if (args.length > 1) {
            serverPort = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            message = args[2];
        }
        Client client = new Client(serverName, serverPort, message);
    }
}
