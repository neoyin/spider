package com.lifeix.com.lifeix.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FileUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	
	public static String readFile(String path){
		StringBuffer buffer = new StringBuffer();
		try {
			String a = "";
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			int index =0;
			while ((a = br.readLine()) != null) {
				buffer.append(a+"\n");
			}
			//System.out.println(str.toString()+"------------"+index);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	/**
	 * 写入文件
	 * @param path
	 * @param str
	 * @return
	 */
	public static boolean writeFile(String path,String str){
		
		PrintStream ps = null;
		FileOutputStream out = null;
		try {
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			out = new FileOutputStream(file,true);
			ps = new PrintStream(out);
			ps.println(str);
			out.flush();
			
		} catch (Exception e) {
		}finally{
			try {
				out.close();
				ps.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * down load pic 
	 * @param path
	 * @param url
	 * @return
	 */
	public static String downLoadPic(String path, String url,String referer){
		try {
			
			URI uri = new URI(url);
			
			String suffix = url.substring(url.lastIndexOf("/")+1);
			url = url.replaceAll(" ", "%20");
			if (uri.getQuery()!=null) {
				url = url.replaceAll("\\?"+uri.getQuery(), "");
			}
			URL picUrl = new URL(url);
			Proxy proxy =null;

			System.out.println("开始download pic :"+url);
			
			HttpURLConnection conn = null;
			if (proxy!=null) {
				 conn = (HttpURLConnection) picUrl.openConnection(proxy);
			}else {
				conn = (HttpURLConnection) picUrl.openConnection();
			}
			conn.setRequestProperty("referer", referer);
			conn.setConnectTimeout(30000);
			
			InputStream inputStream = conn.getInputStream();
			DataInputStream in = new DataInputStream(inputStream);
			String fileName = path+"/"+ UUID.randomUUID()+"_"+suffix;
			DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
			byte[] buff = new byte[8996];
			int count = 0;
			while((count =in.read(buff))>0){
				out.write(buff,0,count);
			}
			out.close();
			in.close();
			conn.disconnect();
			
			return fileName;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			LOGGER.debug("download pic error "+ url+e.getMessage());
		} catch (IOException e) {
			LOGGER.debug("download pic error "+ url+e.getMessage());
		} catch (URISyntaxException e) {
			LOGGER.debug("download pic error "+ url+e.getMessage());
		}
		return null;
	}
	/**
	 * 转化成数组
	 * @param path
	 * @return
	 */
	public static List<String> paserFileToStrArr(String path){
		String str = readFile(path);
		List<String> strArr = new ArrayList<String>();
		if (str==null||str.length()<1) {
			return strArr;
		}
		String[] temp = str.split("\n");
		if (temp!=null&&temp.length>0) {
			for (String s : temp) {
				if (s!=null&&s.length()>0) {//去除空行
					strArr.add(s);
				}
			}
			//return Arrays.asList(temp);
		}
		return strArr;
	}
	
	public static void main(String[] args) {
		String url ="http://www.baby729.com/attach.php?r=MjAxMi0wOC0yMi8xMzQ1NjM2MjgwcTc2eS5qcGcsMCwwLDEsMTQzYzIw";
		
		//http://www.chunluoli.com/
		
		
	}
	
	
}
