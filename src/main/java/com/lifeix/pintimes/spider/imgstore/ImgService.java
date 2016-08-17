package com.lifeix.pintimes.spider.imgstore;

import com.lifeix.com.lifeix.utils.FileUtils;
import com.lifeix.pintimes.spider.bean.SpiderConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by neoyin on 16/8/17.
 */
public abstract class ImgService {

    public abstract Map<String, String> convertImgPath(SpiderConfig config,List<String> urls);

    public String downloadPic(String path,String picUrl,String ref){
        return FileUtils.downLoadPic(path,picUrl,ref);
    }

    public abstract String uploadImg(SpiderConfig config, String filePath) throws IOException;
}
