package com.lifeix.pintimes.spider.service;

import com.l99.common.utils.JsoupUtil;
import com.l99.common.utils.ResolveTextObject;
import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.pintimes.spider.bean.SpiderConfig;
import com.lifeix.pintimes.spider.bean.SpiderContent;
import com.lifeix.pintimes.spider.util.BerkeleyDBUtil;
import com.lifeix.pintimes.spider.util.SpiderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoyin on 15/11/10.
 */
public class SpiderTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderTask.class);

    private SpiderConfig config;

    private static Map<String,Boolean> taskStarting = new HashMap<String, Boolean>();

    public SpiderTask(SpiderConfig config){
        this.config = config;
    }

    public void start(){

        try  {
            //synchronized(taskStarting){
                //if (taskStarting.get(config.getHomePage())==null|| !taskStarting.get(config.getHomePage()).booleanValue()){
                    //taskStarting.put(config.getHomePage(),true);
                    List<String> urlList = SpiderUtils.parseLink(config.getParentUrl(),null,config.getContentsOffset(),config.getCharSet());
                    LOGGER.info("spider url list size : "+urlList.size());
                    for (String url:urlList){

                        if (!BerkeleyDBUtil.getInstance.isExist(config.getHomePage(),url)){
                            BerkeleyDBUtil.getInstance.inject(config.getHomePage(),url);
                            spiderPage(url);
                        }else {
                            LOGGER.debug("this url had spider: " +url );
                        }

                        try {
                            Thread.sleep(1000*config.getTimeSec());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


//                }else {
//                    System.out.println("task is running ");
//                }
            //}
        }finally{
            taskStarting.remove(config.getHomePage());
            System.out.println(config.getHomePage()+"--"+Thread.currentThread()+"--"+ new Date()+"-gone-"+taskStarting);
        }
    }


    public void spiderPage(String url){
        SpiderParse parse = new SpiderParse(config);
        SpiderContent data = parse.productionLine(url);
        System.out.println(data+"\n"+config);
        SpiderDoveStore store = new SpiderDoveStore(data,config);
        try {
            store.saveContent();
        } catch (L99IllegalOperateException e) {
           LOGGER.debug(e.getMessage(),e);
        }
        System.out.println("===========");


    }


    public void run() {

        System.out.println(config.getHomePage()+"--"+Thread.currentThread()+"--"+ new Date());
        //BerkeleyDBUtil.getInstance.openDB(config.getHomePage());
        start();
        //BerkeleyDBUtil.getInstance.flush();

    }
}
