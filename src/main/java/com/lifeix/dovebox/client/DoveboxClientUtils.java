package com.lifeix.dovebox.client;

import com.l99.exception.service.L99IllegalDataException;
import com.lifeix.common.utils.CipherUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DoveboxClientUtils {


	
	private static final Logger LOGGER = LoggerFactory.getLogger(DoveboxClientUtils.class);
	
	/**
	 * 飞鸽上传图片
	 */
	private final static String PHOTO_PATH ="dovebox/post/photo/upload" ;
	/**
	 * 发表照片飞鸽
	 */
	private final static String PHOTO_SAVE ="dovebox/post/photo/save";
	/**
	 * 发表随笔飞鸽
	 */
	private final static String TEXT_PATH ="dovebox/post/text/save";
	/**
	 * 通过飞鸽IDS查找飞鸽
	 */
	private final static String DOVE_LIST ="dovebox/content/ids";
	/**
	 * 发表评论 带图片
	 */
	private final static String COMMENT_PATH_WIHTPHOTO ="dovebox/comment/sendwithupload";
	/**
	 * 不带图片
	 */
	private final static String COMMENT_PATH ="dovebox/comment/send";
	/**
	 * 回复评论 带图片
	 */
	private final static String REPLY_WITHPHOTO ="dovebox/comment/replywithupload";
	/**
	 * 回复评论  不带图片
	 */
	private final static String REPLY_PATH ="dovebox/comment/reply";
	
	
	
	private static String DOVEBOX_PATH ="";
	
	
	public final static String client_version = "1.5";
	
	
	public static void init(String path){
		DOVEBOX_PATH =  path;
//		ClientConfig cc = new DefaultClientConfig();
//		cc.getClasses().add(MultiPartWriter.class);
//		client = Client.create(cc);
	}
	
	private static Client getClient(){
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		Client client = Client.create(cc);
		return client;
	}
	
	public static Client addFilter(AuthorizationData authData,boolean filter){
		Client client = getClient();
		if (filter) {
			String username = CipherUtil.getInstance().aesEncode(authData.getUsername());
			String password = CipherUtil.getInstance().aesEncode(authData.getPassword() + ":" + authData.getVersion());
			client.addFilter(new HTTPBasicAuthFilter(username, password));
		}
		return client;
	}
	
	/**
	 * 通过IDs 得到飞鸽
	 * @param ids
	 * @param basicParams
	 * @param authData
	 * @return
	 */
	public static DoveBoxResponse doveList(List<Long> ids,BasicParams basicParams,AuthorizationData authData,boolean validata){
		Client client = addFilter(authData,validata);
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		String temp = arrayToStr(ids);
		/*for (Long dashboardId : ids) {
			params.add("dashboard_ids",String.valueOf(dashboardId));
		}*/
		params.add("dashboard_ids",temp);
		params.add("client", basicParams.getSource());
		//params.add("ipaddress",authData.getIpAddress());
		params.add("lng", String.valueOf(basicParams.getLng()));
		params.add("lat",String.valueOf(basicParams.getLat()));
		WebResource webResource = client.resource(DOVEBOX_PATH+DOVE_LIST);
		String response= webResource.queryParams(params).type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).get(String.class);
		
		DoveBoxResponse tResponse = new DoveBoxResponse();
		tResponse.setResponse(response);
		return tResponse;
	}
	
	/**
	 * 发表随笔飞鸽
	 * @param localId
	 * @param content
	 * @param ipAddress
	 * @param target_ids
	 * @param basicParams
	 * @param authData
	 * @return
	 */
	public static DoveBoxResponse textDove(long localId,String content,String ipAddress,List<Long> target_ids,BasicParams basicParams,AuthorizationData authData){
		
		Form form =  new Form(); 
		form.add("local_id",localId);
		form.add("permission_type", 40);
		form.add("ipaddress",ipAddress);
		form.add("lng", basicParams.getLng());
		form.add("lat",basicParams.getLat());
		form.add("client",basicParams.getSource());
		form.add("text_content", content);
		form.add("html_flag",true);
		
		String temp = arrayToStr(target_ids);
		form.add("target_ids", temp);
		
		Client client = addFilter(authData,true);
		WebResource webResource = client.resource(DOVEBOX_PATH+TEXT_PATH);
		String response= webResource.type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class,form);
		DoveBoxResponse tResponse = new DoveBoxResponse();
		tResponse.setResponse(response);
		try {
			if (!response.isEmpty()) {
				
				JSONObject object = new JSONObject(response);
				
				if (object.has("code")) {
					tResponse.setCode(object.getInt("code"));
				}
				if (object.has("status")) {
					tResponse.setStatus(object.getInt("status"));
				}
				
				if (object.has("data")) {
					JSONObject dataJson = object.getJSONObject("data");
					if (dataJson!=null&&dataJson.has("dashboard")) {
						JSONObject dashboardJson = dataJson.getJSONObject("dashboard");
						//需要添加 with 信息
						
						if (dashboardJson!=null&&dashboardJson.has("dashboard_id")) {
							long dashboardId = dashboardJson.getLong("dashboard_id");
							tResponse.setDashboardId(dashboardId);
							int dashboardType = dashboardJson.getInt("dashboard_type");
							tResponse.setDashboardType(dashboardType);
							long dashboardData = dashboardJson.getLong("dashboard_data");
							tResponse.setDashboardData(dashboardData);
						}
					}
				}
				
			}
		} catch (JSONException e) {
			LOGGER.debug(e.getMessage(),e);
		}
		return tResponse;
	}
	
	/**
	 * 发表照片飞鸽
	 * @param localId
	 * @param photoDesc
	 * @param basicParams
	 * @return
	 * @throws L99IllegalDataException 
	 */
	public static DoveBoxResponse photoDove(long localId,List<File> files,String formName,String photoDesc,int permissionType,String title,String tags,BasicParams basicParams,AuthorizationData authData) throws L99IllegalDataException{
		//上传照片
		try {
			
			List<Long> photoIds = new ArrayList<Long>();
			for (File file : files) {
				long photoId = uploadAndGetId(file,formName,basicParams,authData);
				photoIds.add(photoId);
			}
			if (photoIds.size()<1) {
				throw new L99IllegalDataException("文件不匹配...");
			}
			Form form =  new Form();
			form.add("photo_ids", arrayToStr(photoIds));
			form.add("photo_desc", photoDesc);
			form.add("local_id",localId);
			form.add("permission_type", permissionType);
			form.add("lng", basicParams.getLng());
			form.add("lat",basicParams.getLat());
			form.add("client",basicParams.getSource());
			form.add("photo_title", title);
			form.add("dashboard_tags",tags);
			/*String temp = arrayToStr(target_ids);
			form.add("target_ids", temp);*/
			
			Client client = addFilter(authData,true);
			WebResource webResource = client.resource(DOVEBOX_PATH+PHOTO_SAVE);
			String response= webResource.type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class,form);
			DoveBoxResponse tResponse = new DoveBoxResponse();
			tResponse.setResponse(response);
			if (!response.isEmpty()) {
				JSONObject object = new JSONObject(response);
				if (object.has("code")) {
					tResponse.setCode(object.getInt("code"));
				}
				if (object.has("status")) {
					tResponse.setStatus(object.getInt("status"));
				}
				if (object.has("data")) {
					JSONObject dataJson = object.getJSONObject("data");
					if (dataJson!=null&&dataJson.has("dashboard")) {
						JSONObject dashboardJson = dataJson.getJSONObject("dashboard");
						if (dashboardJson!=null) {
							long dashboardId = dashboardJson.getLong("dashboard_id");
							tResponse.setDashboardId(dashboardId);
							int dashboardType = dashboardJson.getInt("dashboard_type");
							tResponse.setDashboardType(dashboardType);
							long dashboardData = dashboardJson.getLong("dashboard_data");
							tResponse.setDashboardData(dashboardData);
						}
					}
				}
			}
			LOGGER.info(tResponse.toString());
			return tResponse;
		} catch (JSONException e) {
			LOGGER.debug(e.getMessage(),e);
			throw new L99IllegalDataException();
		}
	}
	
	public static DoveBoxResponse photoDoveByIds(List<Long> photoIds,String formName,String photoDesc,int permissionType,String title,String tags,BasicParams basicParams,AuthorizationData authData){
		
		DoveBoxResponse tResponse = new DoveBoxResponse();
		try {
			
			if (photoIds==null||photoIds.size()<1) {
				//throw new L99IllegalDataException("文件不匹配...");
				LOGGER.info("photoIds is null");
			}
			Form form =  new Form();
			form.add("photo_ids", arrayToStr(photoIds));
			form.add("photo_desc", photoDesc);
			form.add("local_id","0");
			form.add("permission_type", permissionType);
			form.add("lng", basicParams.getLng());
			form.add("lat",basicParams.getLat());
			form.add("client",basicParams.getSource());
			form.add("photo_title", title);
			form.add("dashboard_tags",tags);
			/*String temp = arrayToStr(target_ids);
			form.add("target_ids", temp);*/
			
			Client client = addFilter(authData,true);
			WebResource webResource = client.resource(DOVEBOX_PATH+PHOTO_SAVE);
			String response= webResource.type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class,form);
			
			tResponse.setResponse(response);
			if (!response.isEmpty()) {
				JSONObject object = new JSONObject(response);
				if (object.has("code")) {
					tResponse.setCode(object.getInt("code"));
				}
				if (object.has("status")) {
					tResponse.setStatus(object.getInt("status"));
				}
				if (object.has("data")) {
					JSONObject dataJson = object.getJSONObject("data");
					if (dataJson!=null&&dataJson.has("dashboard")) {
						JSONObject dashboardJson = dataJson.getJSONObject("dashboard");
						if (dashboardJson!=null) {
							long dashboardId = dashboardJson.getLong("dashboard_id");
							tResponse.setDashboardId(dashboardId);
							int dashboardType = dashboardJson.getInt("dashboard_type");
							tResponse.setDashboardType(dashboardType);
							long dashboardData = dashboardJson.getLong("dashboard_data");
							tResponse.setDashboardData(dashboardData);
						}
					}
				}
			}
			LOGGER.info(tResponse.toString());
			return tResponse;
		} catch (JSONException e) {
			LOGGER.debug(e.getMessage(),e);
			return tResponse;
		}
	}
	
	
	/**
	 * 回复飞鸽评论 
	 * @param file
	 * @param commentId
	 * @param content
	 * @param targetIds
	 * @param params
	 * @param authData
	 * @return
	 */
	public static DoveBoxResponse reply(File file ,long commentId,String content,String targetIds,BasicParams params,AuthorizationData authData){
		Client client = addFilter(authData,true);
		String response= null;
		
		if (file!=null) {
			FormDataMultiPart part = new FormDataMultiPart();
			part.field("file", file, MediaType.MULTIPART_FORM_DATA_TYPE);
			part.bodyPart(new FormDataBodyPart("client", params.getSource()));
			part.bodyPart(new FormDataBodyPart("lng",String.valueOf(params.getLng())));
			part.bodyPart(new FormDataBodyPart("lat",String.valueOf(params.getLat())));
			part.bodyPart(new FormDataBodyPart("ipaddress",authData.getIpAddress()));
			if (targetIds!=null) {
				part.bodyPart(new FormDataBodyPart("target_ids", targetIds));
			}
			
			part.bodyPart(new FormDataBodyPart("comment_id", String.valueOf(commentId)));
			if (content!=null) {
				part.bodyPart(new FormDataBodyPart("comment_content", content));
			}
			WebResource webResource = client.resource(DOVEBOX_PATH+REPLY_WITHPHOTO);
			response =webResource.type(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).post(String.class,part);
		}else {
			Form form = new Form();
			form.add("client", params.getSource());
			form.add("lng",params.getLng());
			form.add("lat",params.getLat());
			form.add("ipaddress",authData.getIpAddress());
			if (targetIds!=null) {
				form.add("target_ids", targetIds);
			}
			form.add("comment_id", commentId);
			form.add("comment_content", content);
			WebResource webResource = client.resource(DOVEBOX_PATH+REPLY_PATH);
			response =webResource.type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class,form);
		}
		if (!response.isEmpty()) {
			DoveBoxResponse tResponse = new DoveBoxResponse();
			tResponse.setResponse(response);
			return tResponse;
		}
		return null;
	}
	
	
	
	/**
	 * 评论飞鸽
	 * @param file
	 * @param dashboardId
	 * @param dashboardType
	 * @param dashboardData
	 * @param content
	 * @param targetIds
	 * @param params
	 * @return
	 */
	public static DoveBoxResponse comment(File file,long dashboardId,int dashboardType,long dashboardData,String content,String targetIds,BasicParams params,AuthorizationData authData){
		
		Client client = addFilter(authData,true);
		String response= null;
		if (file!=null) {
			FormDataMultiPart part = new FormDataMultiPart();
			part.field("file", file, MediaType.MULTIPART_FORM_DATA_TYPE);
			part.bodyPart(new FormDataBodyPart("client", params.getSource()));
			part.bodyPart(new FormDataBodyPart("lng",String.valueOf(params.getLng())));
			part.bodyPart(new FormDataBodyPart("lat",String.valueOf(params.getLat())));
			part.bodyPart(new FormDataBodyPart("ipaddress",authData.getIpAddress()));
			if (targetIds!=null) {
				part.bodyPart(new FormDataBodyPart("target_ids", targetIds));
			}
			part.bodyPart(new FormDataBodyPart("dashboard_id", String.valueOf(dashboardId)));
			part.bodyPart(new FormDataBodyPart("dahsboard_type", String.valueOf(dashboardType)));
			part.bodyPart(new FormDataBodyPart("dashboard_data", String.valueOf(dashboardData)));
			if (content!=null) {
				part.bodyPart(new FormDataBodyPart("comment_content", content));
			}
			WebResource webResource = client.resource(DOVEBOX_PATH+COMMENT_PATH_WIHTPHOTO);
			response =webResource.type(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).post(String.class,part);
		}else {
			Form form = new Form();
			form.add("client", params.getSource());
			form.add("lng",params.getLng());
			form.add("lat",params.getLat());
			form.add("ipaddress",authData.getIpAddress());
			if (targetIds!=null) {
				form.add("target_ids", targetIds);
			}
			if(dashboardId > 0){
				form.add("dashboard_id", dashboardId);
			}
			form.add("dahsboard_type", dashboardType);
			form.add("dashboard_data", dashboardData);
			form.add("comment_content", content);
			WebResource webResource = client.resource(DOVEBOX_PATH+COMMENT_PATH);
			response =webResource.type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class,form);
		}
		if (!response.isEmpty()) {
			DoveBoxResponse tResponse = new DoveBoxResponse();
			tResponse.setResponse(response);
			return tResponse;
		}
		return null;
	}
	
	
	/**
	 * 上传照片
	 * @param file
	 * @param formName
	 * @param basicParams
	 * @param authData
	 * @return
	 */
	public static String upload( File file, String formName,BasicParams basicParams,AuthorizationData authData){
		FormDataMultiPart part = new FormDataMultiPart();
		//part.field("file", file, MediaType.MULTIPART_FORM_DATA_TYPE);
		part.bodyPart(new FileDataBodyPart("file", file, MediaType.MULTIPART_FORM_DATA_TYPE));
		   
		part.bodyPart(new FormDataBodyPart("client", basicParams.getSource()));
		part.bodyPart(new FormDataBodyPart("lng",String.valueOf(basicParams.getLng())));
		part.bodyPart(new FormDataBodyPart("lat",String.valueOf(basicParams.getLat())));
		Client client = addFilter(authData,true);
		WebResource webResource = client.resource(DOVEBOX_PATH+PHOTO_PATH);
		String response= webResource.type(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).post(String.class,part);
		return response;
	}
	
	
	/**
	 * 上传照片并得到照片路径
	 * @param file
	 * @param formName
	 * @param basicParams
	 * @param authData
	 * @return
	 * @throws JSONException
	 */
	public static String uploadAndGetPath(File file, String formName,BasicParams basicParams,AuthorizationData authData) throws JSONException {
		String response= upload(file, formName, basicParams, authData);
		return getImageFromDoveResponse(response);
	}
	
	/**
	 * 向飞鸽 API请求.(上传图片)
	 * @param formName
	 * @throws JSONException
	 */
	public static long uploadAndGetId( File file, String formName,BasicParams basicParams,AuthorizationData authData) throws JSONException {
		
		String response= upload(file, formName, basicParams, authData);
		if (!response.isEmpty()) {
			JSONObject object =  new JSONObject(response);
			if (object.has("data")) {
				JSONObject dataJson = object.getJSONObject("data");
				if (dataJson!=null&&dataJson.has("dashboard")) {
					JSONObject dashboardJson = dataJson.getJSONObject("dashboard");
					if (dashboardJson!=null&&dashboardJson.has("photo_id")) {
						long photoId = dashboardJson.getLong("photo_id");
						return photoId;
					}
				}
			}
		}
		//上传图片失败
		LOGGER.debug(" upload photo error "+response);
		
		return 0;
	}
	
	/**
	 * 从飞鸽API中解析得到image url
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	private static String getImageFromDoveResponse(String response) throws JSONException {
			JSONObject object = new JSONObject(response);
			JSONObject data = object.getJSONObject("data");
			if (data.has("dashboard")) {
				JSONObject comment = data.getJSONObject("dashboard") ;
				if (comment.has("big_path")) {
					return comment.getString("big_path");
				}
			}
			return null;
	}
	
	
	/**
	 * list 转化成字符串
	 * @param ids
	 * @return
	 */
	public static String arrayToStr(List<Long> ids){
		if (ids==null||ids.size()<1)return "";
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < ids.size(); i++) {
			if (i==(ids.size()-1)) {
				temp.append(ids.get(i));
			}else {
				temp.append(ids.get(i)+",");
			}
		}
		return temp.toString();
	}
	/**
	 * 数组字符串参数 转换成数组
	 * @param ids
	 * @return
	 */
	public static List<Long> strToArray(String ids){
		if (ids==null||ids.equals("")) {
			return null;
		}
		String[]temps = ids.split(",");
		if (temps==null||temps.length<1) {
			return null;
		}
		List<Long> arrIds = new ArrayList<Long>();
		for (String strId: temps) {
			Long id = Long.valueOf(strId);
			arrIds.add(id);
		}
		return arrIds;
	}
	
	public static List<String> phonesToArray(String phones){
		if (phones==null||phones.equals("")) {
			return null;
		}
		String[]temps = phones.split(",");
		if (temps==null||temps.length<1) {
			return null;
		}
		List<String> arrIds = new ArrayList<String>();
		for (String strId: temps) {
			arrIds.add(strId);
		}
		return arrIds;
	}
	
	
	
}
