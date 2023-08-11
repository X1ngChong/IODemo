package com.nio.com;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOServer {
    public static void main(String[] args) {
        try {
            //打开socket服务通道
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            //设置通道为非阻塞
            serverSocket.configureBlocking(false);
            //给服务绑定ip和端口
            serverSocket.bind(new InetSocketAddress("127.0.0.1",9999));
            //打开多路复用器,相当于客服
            Selector selector = Selector.open();
            //把服务器注册到多路复用器,指定现在的通道可以接收客户端的连接请求
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            //轮询服务
            while(true){
                //多路复用器监听,选择准备好的事件.相当于客服上班了,开始看哪些客户需要沟通了
                selector.select();

                //获取已经选择的件集,相当于客服找到了哪些客户需要沟通了,给这些客户放一个小本本
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                //迭代处理集合中的每个事件
                while(it.hasNext()){
                    SelectionKey key = it.next();
                    //在件集中移除上面取到的key,相当于客服在小本本上拿到了一个客户,再把本上这个客户划掉,之后客服要沟通这个客户了
                    it.remove();

                    //判断当前的 SelectionKey是一个什么类型的事件
                    //接收到客户端的请求
                    if(key.isAcceptable()){
                        SocketChannel socket = serverSocket.accept();

                        socket.configureBlocking(false);
                        //注册读取客户端的请求内容
                        socket.register(selector,SelectionKey.OP_READ);

                        String msg = "连接成功,是第 "+ (selector.keys().size() - 1) +" 位用户";
                        //发送消息给客户端
                        socket.write(ByteBuffer.wrap(msg.getBytes()));

                    }
                    //客户端有信息发过来,服务端要读取
                    if(key.isReadable()){
                        //得到当前的事件对应的通道
                        SocketChannel socket = (SocketChannel) key.channel();

                        //设置缓冲区
                        ByteBuffer readbuf = ByteBuffer.allocate(1024 * 4);

                        int len=0;

                        byte [] res = new byte[1024 * 4];


                        /**
                         * 在客户端关闭后发送FIN报文,也会处罚read事件,但是连接已经关闭,次数read就会产生异常
                         */
                        try{
                            //socket.read(readbuf)把客户端发送的消息写道缓冲区
                            while((len = socket.read(readbuf)) != 0){
                                //把缓冲区从写切换成读
                                readbuf.flip();
                                //把缓冲区的内容读到byte数组中
                                readbuf.get(res,0,len);

                                System.out.println(socket+" 客户端: "+new String(res,0,len));
                                Thread.sleep(3000);

                                //清空缓冲区
                                readbuf.clear();
                            }

                        }catch (Exception e){
                            key.cancel();
                            socket.close();
                            System.out.println("客户端已经关闭");
                        }
                    }
                    //服务端要写信息给客户端
//                    if(key.isWritable()){
//
//                    }

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
