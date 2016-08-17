package com.lifeix.pintimes.spider.imgstore;

import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.dovebox.client.AuthorizationData;
import com.lifeix.dovebox.client.BasicParams;
import com.lifeix.dovebox.client.DoveboxClientUtils;
import com.lifeix.pintimes.spider.bean.SpiderConfig;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoyin on 16/8/17.
 */
@Service("doveboxImgService")
public class ImageDoveboxServiceImpl extends ImgService {


    public static final Logger LOGGER  = LoggerFactory.getLogger(ImageDoveboxServiceImpl.class);

    @Value("${db.image.path}")
    private String tempImgPath;
    @Value("${imgPrefix}")
    private String prefixImg;

    @Override
    public Map<String, String> convertImgPath(SpiderConfig config,List<String> urls) {
        Map<String,String> imgMap = new HashMap<>();
        for (String u:urls){
            String localUrl = downloadPic(tempImgPath,u,config.getHomePage());
            try {
                LOGGER.debug("download img success "+localUrl+"---- start upload img");
                String onlineUrl = uploadImg(config,localUrl);
                if (onlineUrl==null){
                    imgMap.put(u,u);
                }else{
                    onlineUrl =  prefixImg+onlineUrl;
                    LOGGER.debug("upload img success "+onlineUrl);
                    imgMap.put(u,onlineUrl);
                }
            } catch (IOException e) {
                imgMap.put(u,u);
            }
        }
        return imgMap;
    }

    @Override
    public String uploadImg(SpiderConfig config, String filePath) throws IOException {
        if (filePath==null){
            return null;
        }
        File file = new File(filePath);
        try {
            AuthorizationData authorizationData = new AuthorizationData(config.getUsername(),config.getPasswd(),DoveboxClientUtils.client_version);
            return DoveboxClientUtils.uploadAndGetPath(file,file.getName(),new BasicParams(),authorizationData);
        } catch (L99IllegalOperateException e) {
            LOGGER.debug(e.getMessage(),e);
        } catch (JSONException e) {
            LOGGER.debug(e.getMessage(),e);
        }
        return null;
    }
}
