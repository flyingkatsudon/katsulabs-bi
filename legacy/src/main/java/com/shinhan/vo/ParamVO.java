package com.shinhan.vo;

import java.util.ArrayList;

public class ParamVO {

	protected String cnt; // 조회 건수
	protected String kwd; // 키워드
	
	protected String kwdA; // 키워드
	protected String kwdB; // kwdA의 연관어
	
	protected String nerInfoA; // 질병 트랜드용 카테고리 연관어(?)
	
	protected String site; // SNS 채널명 e.g, twitter, instagram..
	
	protected String date; // 조회 일자
	protected String startDate; // 조회 시작 일자
	protected String endDate; // 조회 마감 일자
	protected String scriptUid; // 스크립트 고유 아이디
	protected ArrayList<String> kwdList = null; // 키워드 리스트 split 후 저장할 list
	
	protected String categoryCode; // 카테고리 코드
	
	protected String businessCode; // 그룹사 구분 코드
	protected String period; // 조회 단위 일, 주, 월, 분기, 년(00, 01, 02, 03, 04)
	
	protected String jobId;
	protected String fid;
	protected ArrayList<String> fidList = null; // 키워드 리스트 split 후 저장할 list
	
	protected String flag;
	
	protected String isuCd;
	
	public String getIsuCd() {
		return isuCd;
	}
	public void setIsuCd(String isuCd) {
		this.isuCd = isuCd;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public ArrayList<String> getFidList() {
		return fidList;
	}
	public void setFidList(ArrayList<String> fidList) {
		this.fidList = fidList;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	// 검색엔진 관련
	protected String query;
	protected String domainOpt; // news, instagram, twitter
    protected String Operator; //대소문자 가능 (and, AND, or, OR)
    protected String andQuery; //Operator가 AND일 경우, AND로 작동 (Query는 반드시 포함된 결과 도출)
    protected String orQuery; //Operator가 OR일 경우, OR로 작동 (Query는 반드시 포함된 결과 도출)
    protected String notQuery; //제외 키워드
    
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt;
	}
	public String getKwd() {
		return kwd;
	}
	public void setKwd(String kwd) {
		this.kwd = kwd;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public String getScriptUid() {
		return scriptUid;
	}
	public void setScriptUid(String scriptUid) {
		this.scriptUid = scriptUid;
	}
	public ArrayList<String> getKwdList() {
		return kwdList;
	}
	public void setKwdList(ArrayList<String> kwdList) {
		this.kwdList = kwdList;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getDomainOpt() {
		return domainOpt;
	}
	public void setDomainOpt(String domainOpt) {
		this.domainOpt = domainOpt;
	}
	public String getOperator() {
		return Operator;
	}
	public void setOperator(String operator) {
		Operator = operator;
	}
	public String getAndQuery() {
		return andQuery;
	}
	public void setAndQuery(String andQuery) {
		this.andQuery = andQuery;
	}
	public String getOrQuery() {
		return orQuery;
	}
	public void setOrQuery(String orQuery) {
		this.orQuery = orQuery;
	}
	public String getNotQuery() {
		return notQuery;
	}
	public void setNotQuery(String notQuery) {
		this.notQuery = notQuery;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getKwdA() {
		return kwdA;
	}
	public void setKwdA(String kwdA) {
		this.kwdA = kwdA;
	}
	public String getKwdB() {
		return kwdB;
	}
	public void setKwdB(String kwdB) {
		this.kwdB = kwdB;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getNerInfoA() {
		return nerInfoA;
	}
	public void setNerInfoA(String nerInfoA) {
		this.nerInfoA = nerInfoA;
	}
	
}
