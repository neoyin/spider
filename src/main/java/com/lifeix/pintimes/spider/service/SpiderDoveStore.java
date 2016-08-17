package com.lifeix.pintimes.spider.service;

import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.dovebox.client.AuthorizationData;
import com.lifeix.dovebox.client.BasicParams;
import com.lifeix.dovebox.client.DoveboxClientUtils;
import com.lifeix.pintimes.spider.bean.SpiderConfig;
import com.lifeix.pintimes.spider.bean.SpiderContent;

/**
 * Created by neoyin on 16/8/17.
 */
public class SpiderDoveStore {

    private SpiderConfig config;
    private SpiderContent data;

    private AuthorizationData authorizationData ;

    SpiderDoveStore(SpiderContent data, SpiderConfig config) {
        this.data = data;
        this.config = config;


    }

    public void saveContent() throws L99IllegalOperateException {

        authorizationData = new AuthorizationData(config.getUsername(),config.getPasswd(),"1.2.0");

        DoveboxClientUtils.textDove(0l,data.getPost_content(),"192.168.50.110",null,new BasicParams(),authorizationData);

    }

}
