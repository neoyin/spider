package com.lifeix.pintimes.spider.service;

import com.lifeix.pintimes.spider.ContextManager;
import com.lifeix.pintimes.spider.bean.SpiderConfig;
import com.lifeix.pintimes.spider.bean.SpiderContent;
import com.lifeix.pintimes.spider.imgstore.ImageBedService;
import com.lifeix.pintimes.spider.imgstore.ImageDoveboxServiceImpl;
import com.lifeix.pintimes.spider.imgstore.ImgService;
import com.lifeix.pintimes.spider.util.SpiderUtils;
import com.lifeix.pintimes.spider.util.WeightMathUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neoyin on 15/11/10.
 */
public class SpiderParse {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderParse.class);

    private static ImgService imgService = (ImageBedService) ContextManager.getContext().getBean("bedImageService");
    //private static ImgService imgService = (ImageDoveboxServiceImpl) ContextManager.getContext().getBean("doveboxImgService");


    static Whitelist list = Whitelist.basicWithImages();
    static {
        list.removeAttributes("a", "href");
        list.addAttributes("video",new String[]{"poster","src","controls"});
    }

    private SpiderConfig spiderconfig;

    public SpiderParse(SpiderConfig spiderconfig) {
        this.spiderconfig = spiderconfig;
    }

    public SpiderContent productionLine(String url){
        SpiderContent data = pageContent(url);
        //过滤内容
        data = filterContent(data);
        //将图片下载上传到服务器并替换image地址
        //data = replaceImgs(data);
        //提取摘要
        data = contentExtractor(data);
        double weight = WeightMathUtil.contentLengthWeight(data.getWeight(),data.getPost_content().length());
        data.setWeight(weight);
        return data;
    }


    public SpiderContent getContentFromUrl(String url){

        LOGGER.info(" start parse url :"+url);
        String image ="";
        String desc ="";String title ="";String tags ="";
        String pageUrl =url;
        String tempURL =pageUrl;

        String response = SpiderUtils.parseResponseStr(tempURL, spiderconfig.getCharSet(), false);
        if (response==null)return null;
        Document document = Jsoup.parseBodyFragment(response);
        Elements elements = document.select("meta");

        for (Element element : elements) {
            String name = element.attr("name");
            String content = element.attr("content");

            if (name.endsWith("keywords")) {
                tags = content;
                tags = tags.replaceAll(" ", ",");

            }
            if (name.endsWith("description")) {//如果没有获得内容则描述为内容
                desc = content;
            }
        }

        if (spiderconfig.getDescOffset()!=null&&spiderconfig.getDescOffset().length()>0) {
            Elements descEle = document.select(spiderconfig.getDescOffset());
            if (descEle!=null) {
                desc =descEle.html();
            }

        }
        title = document.title();
        if (spiderconfig.getTitleOffset()!=null&&spiderconfig.getTitleOffset().length()>0) {
            Element titleEle = document.select(spiderconfig.getTitleOffset()).first();
            if (titleEle!=null) {
                title =  titleEle.text();
            }
        }

//        if (spiderconfig.getTagsOffset()!=null&&spiderconfig.getTagsOffset().length()>0) {
//            Elements tagsEle = document.select(spiderconfig.getTagsOffset());
//            if (tagsEle!=null&&tagsEle.size()>0) {
//                tags ="";
//                for (int i = 0; i < tagsEle.size(); i++) {
//                    Element  element =  tagsEle.get(i);
//                    String t = element.text().replaceAll("&", ",");
//                    tags +=t+",";
//                }
//            }
//        }



        SpiderContent data = new SpiderContent(response,spiderconfig.getAuthorId(),spiderconfig.getCategoryId(),desc,title,url,spiderconfig.getWeight());
        data.setHtml(response);
        return data;
    }


    public SpiderContent pageContent(String url){

        SpiderContent data = getContentFromUrl(url);
        if (data ==null) return data;
        if (StringUtils.isEmpty(spiderconfig.getPageOffset()))return data;
        List<String> pageLinks =SpiderUtils.parseLink(url,data.getHtml(),spiderconfig.getPageOffset(),spiderconfig.getCharSet());
        pageLinks = SpiderUtils.removeDuplicate(pageLinks);
        for (String link:pageLinks){
            if (link.equals(data.getSource_url()))continue;
            SpiderContent temp = getContentFromUrl(link);
            if (temp!=null){
                data.setPost_content(data.getPost_content()+temp.getPost_content());
            }
        }
        return data;
    }



    /**
     * 替换成本站图片
     * @param data
     * @return
     */
    public SpiderContent replaceImgs(SpiderContent data){

        if (data==null||StringUtils.isEmpty(data.getPost_content()))return data;
        Document document = Jsoup.parseBodyFragment(data.getPost_content());
        Elements elements =  document.select(spiderconfig.getImageOffset());
        if (elements==null||elements.size()<1)return data;

        LOGGER.info("===start replace img number : "+elements.size());

        Map<String,Element> elementMap = new HashMap<String, Element>();
        List<String> imgList = new ArrayList<String>();
        for (Element e:elements){
            String imgUrl = e.attr("src");
            imgList.add(imgUrl);
            elementMap.put(imgUrl,e);
        }
        Map<String,String> imgMap = imgService.convertImgPath(spiderconfig,imgList);

        for (Element e:elements){
            String imgUrl = e.attr("src");
            String newImgUrl = imgMap.get(imgUrl);
            if (newImgUrl!=null){
                e.attr("src",newImgUrl);
            }
        }
        data.setPost_content(document.html());
        data.setWeight(data.getWeight()+imgList.size());
        return data;
    }




    public SpiderContent filterContent(SpiderContent data){


        Document document = Jsoup.parseBodyFragment(data.getPost_content());
        if (spiderconfig.getFilterOffset()!=null&&spiderconfig.getFilterOffset().size()>0) {
            for (String t : spiderconfig.getFilterOffset()) {
                Elements filtersEle = document.select(t);
                if (filtersEle!=null&&filtersEle.size()>0) {
                    filtersEle.remove();
                }
            }
            String desc = document.html();
            data.setPost_content(desc);
        }
        
        /*此段代码可修复博讯图片显示问题和两个来源问题*/
        if(spiderconfig.getHomePage().equals("www.boxun.com")){
        	Elements elements =  document.select(spiderconfig.getImageOffset());
        	if (elements!=null&&elements.size()>=1){
        		for (int i=0;i<elements.size();i++){
        			if (!elements.get(i).attr("src").contains("http")){
        				document.select(spiderconfig.getImageOffset()).get(i).attr("src","https://www.boxun.com"+elements.get(i).attr("src"));
        			}
        		}
        		data.setPost_content(document.html());
        	}
        	Elements s = document.select("body");
        	String clean = document.select("body").get(0).html();
        		if(s!=null && s.size()>=1){
        			if(s.get(0).html().substring(57, 60).equals("来源：")){
        				while(!clean.substring(57, 58).equals("\n"))
        				{
        					clean = clean.substring(0,57)+clean.substring(58);
        				}
        				document.select("body").get(0).html(clean);
        			}
        			else if(s.get(0).html().substring(86, 89).equals("来源：")){
        				while(!clean.substring(86, 87).equals("\n"))
        				{
        					clean = clean.substring(0,86)+clean.substring(87);
        				}
        				document.select("body").get(0).html(clean);
        			}
        			data.setPost_content(document.html());
        		}
        }
        /*此段代码可修复博讯图片显示问题和两个来源问题*/
        /*图片可以在网页中显示，但是仍然无法下载，错误代码为http 403*/

        String contentClean = Jsoup.clean(data.getPost_content(),list);
        data.setPost_content(contentClean);

        if (spiderconfig.getFilterStr()!=null&&spiderconfig.getFilterStr().size()>0) {
            LOGGER.info("过滤标题字符..."+spiderconfig.getFilterStr());
            for (String s:spiderconfig.getFilterStr()){
                String title = data.getPost_title().replaceAll(s, "");
                data.setPost_title(title);
            }
            LOGGER.info("过滤内容字符..."+spiderconfig.getFilterStr());
            for (String s:spiderconfig.getFilterStr()){
                String desc = data.getPost_content().replaceAll(s, "");
                data.setPost_content(desc);
            }
        }

        return data;
    }

    private SpiderContent contentExtractor(SpiderContent data){
        if (data==null||StringUtils.isEmpty(data.getPost_content()))return data;
        Document doc = Jsoup.parseBodyFragment(data.getPost_content());
        //处理摘要
        String text = doc.text();
        JSONObject obj = new JSONObject();
        if(!StringUtils.isBlank(text)){
            if(text.length() > 100){
                text = text.substring(0, 100);
            }
            try {
                obj.put("text", text);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        //处理图片
        Elements imgElements = doc.select("img");
        if(imgElements.size() > 0){
            JSONArray imgArr = new JSONArray();
            for(Element ele : imgElements){
                String imgSrc = ele.attr("src");
                imgArr.put(imgSrc);
            }
            try {
                obj.put("imgs", imgArr);
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        data.setPost_content_filtered(obj.toString());
        data.setPost_excerpt(text);
        return data;
    }





}
