package com.andersen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @Author : Andersen
 * @Date : 2022-08-15 10:54
 * @Description :NIO客户端
 **/
public class NioClient {

    /**
     * 启动方法
     */
    public void start(String name) throws IOException {
        /**
         * 建立与服务器端的连接
         */
        SocketChannel socketChannel = SocketChannel.open
                (new InetSocketAddress("127.0.0.1", 8000));

        System.out.println("客户端启动成功！");

        /**
         * 接收服务器端的响应
         */
        Selector selector= Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        /**
         * 向服务端发送请求
         */
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNextLine()){
            String request=scanner.nextLine();
            if(request!=null && request.length()>0){
                socketChannel.write(Charset.forName("UTF-8").encode(name+":"+request));
            }
        }


    }

}
