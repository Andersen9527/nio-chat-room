package com.andersen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author : Andersen
 * @Date : 2022-08-15 10:54
 * @Description :  NIO服务器端
 **/
public class NioServer {
    /**
     * 启动方法
     */
    public void start() throws IOException {
        /**
         * 1. 创建selector
         */
        Selector selector = Selector.open();
        /**
         * 2. 通过ServerSocketChannel创建channel通道
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        /**
         * 3. 为channel通道绑定监听端口
         */
        serverSocketChannel.bind(new InetSocketAddress(8000));
        /**
         * 4. 设置channel为非阻塞模式
         */
        serverSocketChannel.configureBlocking(false);

        /**
         * 5. 注册channel到selector上，监听连接事件
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功！");

        /**
         * 6. 循环等待新接入的连接（遍历select方法，找到可用channel个数）
         */
        for(;;){
            /**
             * TODO 获取可用channel数量
             */
            int readyChannels = selector.select();

            /**
             * TODO ??
             */
            if(readyChannels==0) {continue;}

            /**
             * 获取可用channel集合
             */
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();


            while (iterator.hasNext()){
                /**
                 * SelectionKey实例
                 */
                SelectionKey selectionKey = iterator.next();

                /**
                 * 移除Set中的当前selectionKey
                 */
                iterator.remove();

                /**
                 * 7. 根据channel就绪状态，调用对应方法处理业务逻辑
                 */

                /**
                 * 如果是  接入事件
                 */
                if (selectionKey.isAcceptable()){
                    acceptHandler(serverSocketChannel,selector);
                }

                /**
                 * 如果是  可读事件
                 */
                if (selectionKey.isReadable()){
                    readHandler(selectionKey,selector);
                }
            }

        }
    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel,Selector selector)
            throws IOException {
        /**
         * 如果是接入事件，创建socketChannel
         */
        SocketChannel socketChannel = serverSocketChannel.accept();

        /**
         * 将socketChannel设置为非阻塞工作模式
         */
        socketChannel.configureBlocking(false);

        /**
         * 将socketChannel注册到selector中，监听可读事件
         */
        socketChannel.register(selector,SelectionKey.OP_READ);

        /**
         * 响应客户端提示信息
         */
        socketChannel.write(Charset.forName("UTF-8")
                .encode("您与聊点室其他人都不是朋友关系，请注意隐私安全！"));

    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
        /**
         * 要从 selectionKey中获取channel
         */
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        /**
         * 创建buffer
         */
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        /**
         * 循环读取客户端请求信息
         */
        String requst="";
        while (socketChannel.read(byteBuffer)>0){
            /**
             * 切换为读模式
             */
            byteBuffer.flip();

            /**
             * 读取buffer中的内容
             */
            requst+=Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 将channel再次注册到selector中，监听其他可读事件
         */
        socketChannel.register(selector,SelectionKey.OP_READ);

        /**
         * 将客户端发送的请求信息 广播给其他客户端
         */
        if(requst.length()>0){
            broadCast(selector,socketChannel,requst);
        }
    }

    /**
     * 广播给其他客户端
     */
    private void broadCast(Selector selector,
                           SocketChannel socketChannel, String request){
        /**
         * 获取所有已接入到服务端的客户端channel
         */
        Set<SelectionKey> keys = selector.keys();
        /**
         * 循环向所有channel广播信息
         */
        keys.forEach(selectionKey ->{
            Channel targetChannel =selectionKey.channel();
            /**
             * 剔除发消息的客户端
             */
            if(targetChannel != socketChannel && targetChannel instanceof  SocketChannel){
                try {
                    //将信息发送到targetChannel客户端
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * 主方法
     * @param args
     */
    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
