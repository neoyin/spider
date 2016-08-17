package com.lifeix.pintimes.spider.service;


import com.lifeix.pintimes.spider.bean.SpiderConfig;
import com.lifeix.pintimes.spider.util.BerkeleyDBUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by neoyin on 15/11/9.
 */
public class SpiderInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderInit.class);

    @Value("${config.path}")String configPath;


    @Autowired
    TaskScheduler taskScheduler;

    @Value("${db.path}")String dbPath;

    @Value("${db.size}")String sizeStr;

    public void startTask(){

        BerkeleyDBUtil.getInstance.setUp(dbPath,Long.valueOf(sizeStr));

        Set<SpiderConfig> configSet = initConfig();

        for (SpiderConfig sc:configSet){
            SpiderTask task = new SpiderTask(sc);
            taskScheduler.schedule(task,new CronTrigger(sc.getCronTigger()));
        }
        LOGGER.info("task number is "+configSet.size());
    }


    public Set<SpiderConfig> initConfig(){

        Set<File> files = getConfigFiles();
        Set<SpiderConfig> configSet = new HashSet<SpiderConfig>();
        for (File f:files){
            try {
                SpiderConfig config = parseConfig(f);
                configSet.add(config);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return configSet;
    }

    private Set<File> getConfigFiles(){

        Set<File> files = new HashSet<File>();
        File file = new File(configPath);
        if (!file.isDirectory())return files;
        File[] temps = file.listFiles();
        if (temps==null||temps.length<1)return files;
        for (File f:temps){
            if (f.getName().endsWith(".properties")){
                files.add(f);
            }
        }
        return files;
    }


    private SpiderConfig parseConfig(File file) throws ConfigurationException {
        PropertiesConfiguration p = new PropertiesConfiguration(file);
        SpiderConfig config = new SpiderConfig();
        config.setAuthorId(p.getLong("authorId",0));
        config.setUsername(p.getString("username"));
        config.setPasswd(p.getString("password"));
        config.setCategoryId(p.getLong("categoryId",0));
        config.setWeight(p.getDouble("weight",0));
        config.setCharSet(p.getString("charSet","UTF-8"));
        config.setContentsOffset(p.getString("contentsOffset"));
        config.setCronTigger(p.getString("cronTigger"));
        config.setDescOffset(p.getString("descOffset"));
        config.setDescDetal(p.getString("descDetal"));
        List<String> fOffset = (p.getProperty("filterOffset")==null||p.getString("filterOffset").length()<1)?null:(List<String>)p.getProperty("filterOffset");
        config.setFilterOffset(fOffset);
        config.setHomePage(p.getString("homePage"));
        config.setImageOffset(p.getString("imageOffset"));
        config.setPageOffset(p.getString("pageOffset"));
        config.setTitleOffset(p.getString("titleOffset"));
        config.setImagePath(p.getString("imagePath"));
        config.setParentUrl(p.getString("parentUrl"));
        config.setTimeSec(p.getLong("timeSec",5));

        List<String> fStr = (p.getProperty("filterStr")==null||p.getString("filterStr").length()<1)?null:(List<String>)p.getProperty("filterStr");
        config.setFilterStr(fStr);

        return config;

    }

}
