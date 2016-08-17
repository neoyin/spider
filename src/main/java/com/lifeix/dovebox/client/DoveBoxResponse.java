package com.lifeix.dovebox.client;

/**
 * 调用其它API 返回结果
 * @author neoyin
 *
 */
public class DoveBoxResponse {

	private String response;
	
	private int code;
	
	private int status;
	
	private String msg;
	/**
	 * 需要得到dashboardId
	 */
	private long dashboardId;
	
	private long dashboardData;
	
	private int dashboardType;
	
	
	

	public long getDashboardData() {
		return dashboardData;
	}

	public void setDashboardData(long dashboardData) {
		this.dashboardData = dashboardData;
	}

	public int getDashboardType() {
		return dashboardType;
	}

	public void setDashboardType(int dashboardType) {
		this.dashboardType = dashboardType;
	}

	public long getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public DoveBoxResponse(String response, int code, String msg) {
		this.response = response;
		this.code = code;
		this.msg = msg;
	}

	public DoveBoxResponse() {
	}

	@Override
	public String toString() {
		return "DoveBoxResponse [response=" + response + ", code=" + code
				+ ", status=" + status + ", msg=" + msg + ", dashboardId="
				+ dashboardId + ", dashboardData=" + dashboardData
				+ ", dashboardType=" + dashboardType + "]";
	}
	
}
