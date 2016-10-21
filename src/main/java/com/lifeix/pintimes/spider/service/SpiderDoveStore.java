package com.lifeix.pintimes.spider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.l99.common.utils.JsoupUtil;
import com.l99.common.utils.ResolveTextObject;
import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.bed.client.BedClientUtils;
import com.lifeix.bed.client.BedImagePO;
import com.lifeix.com.lifeix.utils.JSONUtils;
import com.lifeix.pintimes.spider.ContextManager;
import com.lifeix.pintimes.spider.bean.SpiderConfig;
import com.lifeix.pintimes.spider.bean.SpiderContent;
import com.lifeix.pintimes.spider.imgstore.ImageBedService;
import com.lifeix.pintimes.spider.imgstore.ImgService;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoyin on 16/8/17.
 */
public class SpiderDoveStore {

    private SpiderConfig config;
    private SpiderContent data;

    private static ImgService imgService = (ImageBedService) ContextManager.getContext().getBean("bedImageService");

    //private AuthorizationData authorizationData ;

    SpiderDoveStore(SpiderContent data, SpiderConfig config) {
        this.data = data;
        this.config = config;


    }

    public void saveContent() throws L99IllegalOperateException {

        ResolveTextObject textObject = JsoupUtil.resolveText(data.getPost_content());
        if (textObject == null) return ;
        String content = textObject.getTextContent();
        List<String> imgList = textObject.getTextImages();

        System.out.println(content);
        System.out.println(imgList);

        String params = "";
        if (imgList!=null&&imgList.size()>0){

            Map<String,String> imgMap = imgService.convertImgPath(config,imgList);


            Map<String,BedImagePO> imagePOMap = new HashMap<>();

            for (Map.Entry<String,String> entry:imgMap.entrySet()){
                BedImagePO po =null;
                try {
                    po = BedClientUtils.uploadImg(new File(entry.getValue()));
                } catch (JSONException e) {
                    System.out.println("upload img error "+entry.getKey());
                } catch (IOException e) {
                    System.out.println("upload img error "+entry.getKey());
                }
                if (po!=null)imagePOMap.put(entry.getKey(),po);
            }
            List<BedImagePO> poList = new ArrayList<>();
            for (String img:imgList){
                BedImagePO po = imagePOMap.get(img);
                if (po!=null){
                    poList.add(po);
                }else{
                    po = new BedImagePO("0","0",img);
                }
            }
            try {
               params =  JSONUtils.obj2json(poList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        String response = BedClientUtils.sendArticle(data.getPost_title(),content,params,1);


            //System.out.println(content);
            //System.out.println(imgList.size());

        //authorizationData = new AuthorizationData(config.getUsername(),config.getPasswd(),"1.2.0");
        //DoveboxClientUtils.textDove(0l,data.getPost_content(),"192.168.50.110",null,new BasicParams(),authorizationData);

    }

}
