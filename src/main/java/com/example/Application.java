package com.example;

import com.example.http.RunHttpServer;
import com.example.socketserver.RunSocket;

public class Application {

    public static void main(String[] args) {
        
        RunHttpServer httpServer = new RunHttpServer();
        httpServer.start();
        RunSocket webSocket = new RunSocket();
    }
}
