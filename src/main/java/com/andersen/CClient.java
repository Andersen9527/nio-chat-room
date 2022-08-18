package com.andersen;

import java.io.IOException;

/**
 * @Author : Andersen
 * @Date : 2022-08-15 17:20
 * @Description :C客户端
 **/
public class CClient {
    public static void main(String[] args) throws IOException {
        NioClient client=new NioClient();
        client.start("C客户端");
    }
}
