package com.example.http;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class RunHttpServer extends Thread {
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
            server.createContext("/", new ServerResourceHandler(System.getProperty("user.dir") + "/src", false, false));
            server.setExecutor(null); // creates a default executor
            server.start();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
