package com.lifeix.pintimes.spider;

import com.lifeix.bed.client.BedClientUtils;
import com.lifeix.dovebox.client.DoveboxClientUtils;
import com.lifeix.pintimes.spider.service.SpiderInit;

/**
 * Created by neoyin on 15/11/10.
 */
public class SpiderStartup {




    public static void main(String[] args) {
        ContextManager.init();
        //DoveboxClientUtils.init("http://dbapi.l99.com/");
        BedClientUtils.init("http://spider.nyx.l99.com/");
        SpiderInit spriderInit = (SpiderInit) ContextManager.getContext().getBean("spriderInit");

        spriderInit.startTask();
    }

}
