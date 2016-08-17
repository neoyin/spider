package com.lifeix.pintimes.spider.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


public class SpiderUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpiderUtils.class);


	private static String USER_AGENT[] ={"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3",
		"Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3",
		//"Mozilla/5.0 (iPhone Simulator; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7",
		};
	
	/**
	 * URL 得到 文本信息
	 * @param url
	 * @return
	 */
	public static String parseResponseStr(String url,String charactor,boolean proxyFlag){
		if (charactor==null) {
			charactor = "UTF-8";
		}

		HttpClient client =  new HttpClient();

		client.getParams().setParameter(  
			      HttpMethodParams.HTTP_CONTENT_CHARSET, charactor);  
		
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,USER_AGENT[new Random().nextInt(USER_AGENT.length-1)]);
		
		//client.getHostConfiguration().setProxy(t[0], Integer.valueOf(t[1])); 
		client.getHttpConnectionManager().getParams().setConnectionTimeout(60*1000);
		GetMethod method = new GetMethod(url);
		method.addRequestHeader("Referer", url);
		try {
			client.executeMethod(method);
			
			if (method.getStatusCode()==200) {
				String response = method.getResponseBodyAsString();
				return response;
			}else {
				LOGGER.info(" error status code is "+method.getStatusCode());
				return null;
			}
		} catch (HttpException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUrlPrefix(String url){

		String temp = url.replace("http://", "");
		temp = temp.substring(0,temp.indexOf("/")+1) ;
		return temp;
	}
	/**
	 * 解析url 列表
	 * @param offset
	 * @return
	 */
	public static List<String> parseLink(String url,String html,String offset,String chatSet){

		String parseUrl =url;

		String response ="";
		if (html==null||html.length()<1) {
			LOGGER.info("ready to get url list : start parse url :"+parseUrl);
			response = parseResponseStr(parseUrl, chatSet, false);
		}else {
			response = html;
		}

		List<String> linkList = new ArrayList<String>();
		try {
			Document document = Jsoup.parseBodyFragment(response);
			if (document==null||offset==null) {
				LOGGER.info(" url get document is null :"+parseUrl+"==="+offset);
				return linkList;
			}
			Elements elements = document.select(offset);

			String temp = getUrlPrefix(parseUrl);

			for (Element element : elements) {
				String link = element.attr("href");
				if (!link.startsWith("http://")&&!link.startsWith(temp)) {

					if (!link.startsWith("/")) {
						if (parseUrl.endsWith("/")) {
							link = parseUrl+link;
						}else {

							int stemp =link.indexOf("/");
							if (stemp<1)stemp=link.indexOf("?");
							if (stemp<1){
								link = parseUrl+"/"+link;
							}else {
								String tempUrl = link.substring(0,stemp);
								if (parseUrl.contains(tempUrl)){
									link = "http://"+temp+link;
								}else {
									link = parseUrl+"/"+link;
								}

							}

							//String tempUrl = link.substring(0,);
							//if (tempUrl)



						}
					}else {
						/*String temp = spiderconfig.getSpiderUrl().replace("http://", "");
						temp = temp.substring(0,temp.indexOf("/")+1) ;*/
						String tempSrc= link.replace("http://","");
						tempSrc = tempSrc.replace(temp, "");
						if (temp.endsWith("/")&&tempSrc.startsWith("/")) {
							link ="http://"+temp+tempSrc.substring(1);
						}else {
							link ="http://"+temp+tempSrc;
						}
					}
				}
				linkList.add(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return linkList;
	}

	public static List removeDuplicate(List list){
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		list.clear();
		list.addAll(newList);

		return newList;
	}


	public static void main(String[] args) {
		List<String> urlList = parseLink("http://www.sinovision.net/portal.php?mod=center&mobile=yes",null,"#newlist li.cl a","UTF-8");
		//parseResponseStr()
		for (String s:urlList){
			System.out.println(s);
		}

	}

}
