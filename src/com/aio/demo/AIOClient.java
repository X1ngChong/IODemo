package com.aio.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AIOClient {
    public static void main(String[] args) throws  Exception{
     //俩个用户并发
        for (int i =0;i<2;i++){
            client();
        }
        while(true){
            Thread.sleep(1000);
        }
    }


    public static void client() throws  Exception{
        //开启通道
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        //请求连接服务端
        client.connect(new InetSocketAddress("127.0.0.1", 9999),null, new CompletionHandler<Void, Void>() {
            //连接成功进入这个方法
            @Override
            public void completed(Void result, Void attachment) {

                ByteBuffer writebuf = ByteBuffer.wrap((client+"说:你好").getBytes());
                client.write(writebuf, writebuf, new CompletionHandler<Integer, ByteBuffer>() {
                    //具体的写
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {

                        if(attachment.hasRemaining()){
                            client.write(attachment,attachment,this);

                        }

                        ByteBuffer readbuf = ByteBuffer.allocate(1024 * 4);

                        //读取服务端返回给客户端的消息
                        client.read(readbuf,readbuf, new CompletionHandler<Integer, ByteBuffer>() {
                            //具体的读取
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                //现在服务端返回的给客户端的消息已经在attachment中,需要把attachment切换位读的模式
                                attachment.flip();

                                byte [] bytes = new byte[attachment.remaining()];

                                //把缓冲区的内容读到bytes中
                                attachment.get(bytes);

                                System.out.println(client+"接收服务端消息:"+new String(bytes));


                            }

                            //读取失败
                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {

                            }
                        });

                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {

                    }
                });

            }

            //连接失败进入这个方法
            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });
    }

}
