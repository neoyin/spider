package com.lifeix.pintimes.spider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by neoyin on 15/10/22.
 */
public class ContextManager {

    private static ClassPathXmlApplicationContext context;

    public static void init(){
        // 初始化
        context = new ClassPathXmlApplicationContext(new String[] { "conf/applicationContext.xml"});
        context.start();

    }

    public static ClassPathXmlApplicationContext getContext(){
        return context;
    }

}
