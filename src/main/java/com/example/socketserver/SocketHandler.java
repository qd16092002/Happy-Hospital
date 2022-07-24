package com.example.socketserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.example.entity.Constant;
import com.example.Scene;

public class SocketHandler extends Thread {
    public Socket socket;
    public Scene scene;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        this.scene = new Scene();
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            handshake(in, out);
            handleMessage(in, out);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void handshake(InputStream in, OutputStream out) throws Exception {
        String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);
        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(
                            MessageDigest
                                    .getInstance("SHA-1")
                                    .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                            .getBytes("UTF-8")))
                    + "\r\n\r\n")
                    .getBytes("UTF-8");

            out.write(response, 0, response.length);

        }
    }

    public void handleMessage(InputStream in, OutputStream out) {
        try {

            while (true) {
                var cs = 6;
                byte[] buff = new byte[100024];
                int lineData = in.read(buff);
                if (buff[1] == -2)
                    cs = 8;
                for (int i = 0; i < lineData - cs; i++) {
                    buff[i + cs] = (byte) (buff[i % 4 + cs - 4] ^ buff[i + cs]);
                }

                String mes = new String(buff, cs, lineData - cs, "UTF-8");
                var jsonArray = new JSONArray();
                var jsonObject = new JSONObject();

                if (mes.contains("setNumAgents")) {
                    mes = mes.replaceFirst("setNumAgents", "");
                    scene.setMaxAgent(Integer.parseInt(mes));
                    mes = "";
                } else if (mes.contains("ClickSaveButton")) {
                    scene.handleClickSaveButton();
                    jsonObject.put("ID", "Save");
                    mes = "";
                } else if (mes.contains("ClickLoadButton")) {
                    mes = mes.replaceFirst("ClickLoadButton", "");
                    scene.handleClickLoadButton(mes);
                    jsonObject.put("ID", "Load");
                    mes = "";
                }
                if (!jsonObject.isEmpty()) jsonArray.add(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("ID", "timeTable");
                jsonObject.put("data", scene.timeTable);
                jsonArray.add(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("ID", "harmfullness");
                jsonObject.put("data", scene.harmfullness);
                jsonArray.add(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("ID", "time");
                jsonObject.put("data", Constant.secondsToHMS(Constant.getTime() / 1000));
                jsonArray.add(jsonObject);

                String line = scene.update(mes, jsonArray);
                int length = line.getBytes("UTF-8").length;
                byte[] sendHead = new byte[4];
                if (length < 126) {
                    sendHead[0] = buff[0];
                    sendHead[1] = (byte) length;
                    out.write(sendHead[0]);
                    out.write(sendHead[1]);
                } else {
                    sendHead[0] = buff[0];
                    sendHead[1] = (byte) 126;
                    sendHead[2] = (byte) (length >> 8);
                    sendHead[3] = (byte) (length & 255);
                    out.write(sendHead);
                }
                out.write(line.getBytes("UTF-8"));
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
