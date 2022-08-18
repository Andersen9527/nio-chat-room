package com.andersen;

import java.io.IOException;

/**
 * @Author : Andersen
 * @Date : 2022-08-15 17:20
 * @Description :A客户端
 **/
public class AClient {
    public static void main(String[] args) throws IOException {
        NioClient client=new NioClient();
        client.start("A客户端");
    }
}
