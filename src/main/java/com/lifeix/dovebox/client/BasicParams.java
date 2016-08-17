package com.lifeix.dovebox.client;

/**
 * 此表单为客户端传值
 * @author neoyin
 *
 */
public class BasicParams {
	/**
	 * 纬度
	 */
	private double lat;
	/**
	 * 经度
	 */
	private double lng;
	/**
	 * 语言
	 */
	private String language ="zh_CN" ;
	/**
	 * 版本号
	 */
	private String version ="1.0";
	/**
	 * source
	 */
	private String source ="key:DoveboxForiPhone";
	/**
	 * 最大数值
	 */
	private int limit=19;
	/**
	 * 设备ＩＤ
	 */
	private String deviceId;
	/**
	 * token值
	 */
	private String token;
	/**
	 * 时间戳
	 */
	private long timestamp;
	/**
	 * 数据格式
	 */
	private String format;
	
	private Long sessId;
	
	public Long getSessId() {
		return sessId;
	}
	public void setSessId(Long sessId) {
		this.sessId = sessId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public BasicParams(float lat, float lng, String language, String version,
			String source, int limit,String deviceId,long timestamp,String token,String format,Long sessId) {
		this.lat = lat;
		this.lng = lng;
		this.language = language;
		this.version = version;
		this.source = source;
		this.limit = limit;
		this.deviceId =deviceId;
		this.timestamp = timestamp;
		this.token =token;
		this.format = format;
		this.sessId = sessId;
	}
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public BasicParams(){
		
	}
	@Override
	public String toString() {
		return "BasicParams [lat=" + lat + ", lng=" + lng + ", language="
				+ language + ", version=" + version + ", source=" + source
				+ ", limit=" + limit + ", deviceId=" + deviceId + ", token="
				+ token + ", timestamp=" + timestamp + ", format=" + format
				+ ", sessId=" + sessId + "]";
	}


}
