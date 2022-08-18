package com.andersen;

import java.io.IOException;

/**
 * @Author : Andersen
 * @Date : 2022-08-15 17:20
 * @Description :B客户端
 **/
public class BClient {
    public static void main(String[] args) throws IOException {
        NioClient client=new NioClient();
        client.start("B客户端");
    }
}
