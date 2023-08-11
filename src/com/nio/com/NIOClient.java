package com.nio.com;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class NIOClient {
    public static void main(String[] args) throws Exception{

        String [] msgs = new String[]{"hello你好","world你好"};

        //模拟2次并发请求到服务端
        for(int i =0;i<2;i++){
                String msg = msgs[i];
            new Thread(){
                @Override
                public void run() {
                    try{
                        //客户端连接127.0.0.1的机器上端口号是9999的服务端
                        Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);

                        OutputStream out = socket.getOutputStream();//输出流，向服务端输出请求的内容

                        out.write((msg +"hello，服务端").getBytes());

                        socket.shutdownOutput();//关闭输出流

                        InputStream in = socket.getInputStream();//输入流，接收服务端响应的内容

                        BufferedReader br = new BufferedReader(new InputStreamReader(in));

                        String line = br.readLine();

                        System.out.println("服务端响应：" + line);

                        br.close();
                        in.close();
                        socket.close();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }
}
