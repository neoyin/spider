package com.test;

/**
 * Created by neoyin on 15/11/11.
 */
public class StringTest {


    public static void main(String[] args) {
        String url ="20151111/993262.shtml";

        String tempUrl = url.substring(0,url.indexOf("?"));

        System.out.println(tempUrl);
    }
}
