package com.lifeix.pintimes.spider.service;

import com.lifeix.pintimes.dao.post.PostContentDao;
import com.lifeix.pintimes.dao.term.TermRelationshipsDao;
import com.lifeix.pintimes.dto.post.PostContent;
import com.lifeix.pintimes.dto.term.TermRelationships;
import com.lifeix.pintimes.spider.ContextManager;
import com.lifeix.pintimes.spider.bean.SpiderContent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * Created by neoyin on 15/11/11.
 */
public class SpiderStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderStore.class);

    private static PostContentDao postContentDao;

    private static TermRelationshipsDao termRelationshipsDao;




    public static void saveContent(SpiderContent data){

        if (postContentDao==null){
            postContentDao = (PostContentDao) ContextManager.getContext().getBean("postContentDao");
        }
        if (termRelationshipsDao == null){
            termRelationshipsDao = (TermRelationshipsDao)ContextManager.getContext().getBean("termRelationshipsDao");
        }

        if (data==null|| StringUtils.isEmpty(data.getPost_content())||data.getPost_content().length()<100){
            LOGGER.warn("get data is excption "+data);
            return ;
        }


        PostContent content = new PostContent();
        BeanUtils.copyProperties(data,content);
        System.out.println("=============================");

        PostContent p = postContentDao.insertContent(content);
        if (data.getCategoryId()!=null&&data.getCategoryId().longValue()>0){

            if (p.getId()!=null&&p.getId().longValue()>0){
                TermRelationships ships = new TermRelationships(p.getId(), data.getCategoryId(), 0L);
                termRelationshipsDao.insert(ships);
            }
        }
        LOGGER.info(printData(content));
    }


    private static String printData(PostContent content){
        return "PostContent{" +
                "id=" + content.getId() +
                ", post_author=" + content.getPost_author() +
                ", post_content length ='" + content.getPost_content().length() + '\'' +
                ", post_title='" + content.getPost_title() + '\'' +
                ", post_content_filtered='" + content.getPost_content_filtered() + '\'' +
                '}';
    }

}
