package com.example.socketserver;
import java.net.ServerSocket;
import java.net.Socket;


public class RunSocket {
    public ServerSocket server;

    public RunSocket() {
        try {
            server = new ServerSocket(8080);

            System.out.print(server.getLocalPort());
            while (true) {
                try {
                    Socket socket = server.accept();
                    SocketHandler socketHandler = new SocketHandler(socket);
                    socketHandler.start();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}