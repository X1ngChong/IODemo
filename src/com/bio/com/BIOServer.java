package com.bio.com;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);//创建服务端，绑定9999端口

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

        while(true){
            Socket socket = server.accept();//监听连接服务端的客户端

            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try{

                        InputStream in = socket.getInputStream();//获取客户端发送的数据

                        BufferedReader br = new BufferedReader(new InputStreamReader(in));

                        String line = br.readLine();

                        System.out.println("服务端接收到客户端的请求:" + line);

                        OutputStream out = socket.getOutputStream();//服务端的输出流，用来响应客户端的请求

                        Thread.sleep(4000);

                        out.write((Thread.currentThread() + "我已经收到了你的的请求").getBytes());

                        socket.shutdownOutput();//关闭输出流
                        socket.shutdownInput();//关闭输入流

                        socket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

//            new Thread(){
//                @Override
//                public void run() {
//
//                }
//            }.start();

        }

//        server.close();
    }
}

