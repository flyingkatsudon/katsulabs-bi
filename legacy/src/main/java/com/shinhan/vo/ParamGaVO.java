package com.shinhan.vo;

public class ParamGaVO extends ParamVO {
	
	private String searchPattern;
	private String startDate;
	private String endDate;
	private String searchData;
	private String categoryCode;
	private String section;
	private String analyticsCode; 
	
	private String limitCnt;
	private String pageCnt;
	private String currentPage;
	
	private String businessCode;
	private String version;
	
	private String press;
	
	public String getPress() {
		return press;
	}
	public void setPress(String press) {
		this.press = press;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	public String getLimitCnt() {
		return limitCnt;
	}
	public void setLimitCnt(String limitCnt) {
		this.limitCnt = limitCnt;
	}
	public String getPageCnt() {
		return pageCnt;
	}
	public void setPageCnt(String pageCnt) {
		this.pageCnt = pageCnt;
	}
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	public String getAnalyticsCode() {
		return analyticsCode;
	}
	public void setAnalyticsCode(String analyticsCode) {
		this.analyticsCode = analyticsCode;
	}
	public String getSearchPattern() {
		return searchPattern;
	}
	public void setSearchPattern(String searchPattern) {
		this.searchPattern = searchPattern;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getSearchData() {
		return searchData;
	}
	public void setSearchData(String searchData) {
		this.searchData = searchData;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	
	@Override
	public String toString() {
		return "ParamGaVO [searchPattern=" + searchPattern + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", searchData=" + searchData + ", categoryCode=" + categoryCode + ", section=" + section
				+ ", analyticsCode=" + analyticsCode + ", limitCnt=" + limitCnt + ", pageCnt=" + pageCnt
				+ ", currentPage=" + currentPage + ", businessCode=" + businessCode + ", version=" + version
				+ ", press=" + press + "]";
	}
}
