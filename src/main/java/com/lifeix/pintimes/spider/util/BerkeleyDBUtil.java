package com.lifeix.pintimes.spider.util;

import com.sleepycat.je.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by neoyin on 15/11/10.
 */
public class BerkeleyDBUtil {

    public static DatabaseConfig defaultDBConfig = createDefaultDBConfig();

    private Database db;
    private Environment env;
    private BerkeleyDBUtil() {

    }
    public static BerkeleyDBUtil getInstance = LazyClassHolder.INSTANCE;

    private static class LazyClassHolder{
        private static final BerkeleyDBUtil INSTANCE = new BerkeleyDBUtil();
    }


    public static DatabaseConfig createDefaultDBConfig() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setDeferredWrite(true);
        databaseConfig.setSortedDuplicates(false);
        return databaseConfig;
    }

    public void setUp(String path,long cacheSize){
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setCacheSize(cacheSize*1024*1024);
        env = new Environment(new File(path),envConfig);
    }

    public void openDB(String dbname){
        DatabaseConfig dbConfig = createDefaultDBConfig();
        db = env.openDatabase(null,dbname,dbConfig);
    }

    public void closeDB(){
        if (db!=null)db.close();
        if (env!=null){
            env.cleanLog();
            env.close();
        }
    }

    public void flush(){
        if (db!=null)db.close();
    }

    public String get(String key) {
        try {
            DatabaseEntry queryKey = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            queryKey.setData(key.getBytes("UTF-8"));
            OperationStatus status = db.get(null, queryKey, value, LockMode.DEFAULT);
            if (status == OperationStatus.SUCCESS) {
                return new String(value.getData(),"UTF-8");
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean put(String key, String value) {

        try {
            byte[] theKey = key.getBytes("UTF-8");
            byte[] theValue = value.getBytes("UTF-8");
            /**
             * Berkeley DB中的记录包括两个字段，就是键和值，
             * 并且这些键和值都必须是com.sleepycat.je.DatabaseEntry类的实例。
             */
            OperationStatus status = db.put(null, new DatabaseEntry(theKey),
                    new DatabaseEntry(theValue));
            if (status == OperationStatus.SUCCESS) {
                return true;

            }

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean del(String key) throws Exception{
        DatabaseEntry queryKey = new DatabaseEntry();
        queryKey.setData(key.getBytes("UTF-8"));
        OperationStatus status=db.delete(null, queryKey);
        if(status==OperationStatus.SUCCESS){
            return true;
        }
        return false;
    }

    public boolean isExist(String dbName,String seed){
        openDB(dbName);
        if(get(seed)!=null)return true;

        return false;
    }

    public void inject(String dbName,String seed) {
        openDB(dbName);
        put(seed,String.valueOf(true));
        db.sync();
        db.close();
    }

    public void inject(String dbName,ArrayList<String> seeds) {
        openDB(dbName);
        Iterator i$ = seeds.iterator();
        while(i$.hasNext()) {
            String seed = (String)i$.next();
            put(seed, String.valueOf(true));
        }

        db.sync();
        db.close();
    }



    public static void main(String[] args) throws Exception {

//        BerkeleyDBUtil db = new BerkeleyDBUtil();
//        db.setUp("/tmp/tt",1024*1024);
//
//        db.openDB("test");
//        String key ="www.l99.com";
//        db.put(key, "www.l99.com");
//        //db.put(key,"www.l99.com");
//        System.out.println(System.currentTimeMillis());
//        System.out.println(db.get(key));
//        System.out.println(System.currentTimeMillis());
//        db.closeDB();
        BerkeleyDBUtil db = BerkeleyDBUtil.getInstance;
        db.setUp("/tmp/tt", 100);
        //db.flush();

        String api = "api.l99.com";
        String www = "www.l99.com";
        db.openDB(www);


        db.put(www, www);

       // System.out.println(db.get(key));

        db.openDB(api);

        db.put(api,api);

        db.flush();


        db.openDB(api);
        System.out.println(db.get(api));

    }


}
