package com.aio.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AIOServer {
    public static void main(String[] args) throws Exception {
        //打开AIO服务的通道,一个异步的通道
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        //绑定ip和端口
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",9999));

        //监听客户端请求
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            //如果要是监听到客户端请求,进入这个方法
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {

                //接收客户端请求
                serverSocketChannel.accept(null,this);

                //设置缓冲区,用来读取客户端发来的消息
                ByteBuffer readbuf = ByteBuffer.allocate(1024 * 4);

                //读取客户端请求的内容
                clientChannel.read(readbuf, readbuf, new CompletionHandler<Integer, ByteBuffer>() {

                    //具体读取客户端请求的方法
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        //客户端消息请求现在已经在attachment缓冲区中了,要把attachment改为读的模式
                        attachment.flip();

                        byte [] bytes = new byte[attachment.remaining()];

                        //把attachment的消息读到bytes
                        attachment.get(bytes);

                        System.out.println("客户端 :" +    new String(bytes));

                        String msg = "回复消息:"+clientChannel;

                        //把msg放到缓冲区
                        ByteBuffer writebuf = ByteBuffer.allocate(msg.getBytes().length);
                        writebuf.put(msg.getBytes());

                        //把writebuf切换到读
                        writebuf.flip();

                        //服务端返回给客户端消息
                        clientChannel.write(writebuf, writebuf, new CompletionHandler<Integer, ByteBuffer>() {
                            //具体返回消息的方法
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                            if(attachment.hasRemaining()){
                                clientChannel.write(attachment,attachment,this);
                                }
                            }

                            //服务端返回客户端消息失败
                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {

                            }
                        });

                    }

                    //读取客户端发送信息失败
                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {

                    }
                });

            }
            //接收客户端请求失败
            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });

        /**
         * AIO是异步非阻塞
         * 如果使用main方法运行AIO
         * 在main方法最后使用无线循环让main方法的主线程不要停
         */

        while(true){
        Thread.sleep(1000);
        }
    }


}
