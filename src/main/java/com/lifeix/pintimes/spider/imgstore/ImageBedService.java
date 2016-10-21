package com.lifeix.pintimes.spider.imgstore;

import com.lifeix.pintimes.spider.bean.SpiderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoyin on 16/9/26.
 */
@Service("bedImageService")
public class ImageBedService extends ImgService {


    @Value("${db.image.path}")
    private String tempImgPath;
    @Value("${imgPrefix}")
    private String prefixImg;


    @Override
    public Map<String, String> convertImgPath(SpiderConfig config, List<String> urls) {
        Map<String,String> imgMap = new HashMap<>();
        for (String u:urls){
            String localUrl = downloadPic(tempImgPath,u,config.getHomePage());
            if (localUrl==null){
                localUrl = u;
            }
            imgMap.put(u,localUrl);

        }
        return imgMap;

    }

    @Override
    public String uploadImg(SpiderConfig config, String filePath) throws IOException {
        return null;
    }
}
