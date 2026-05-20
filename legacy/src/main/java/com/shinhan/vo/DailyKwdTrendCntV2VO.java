package com.shinhan.vo;

import java.util.Date;

public class DailyKwdTrendCntV2VO {
	
	private int rn = 0;
		
	private String jobId;
	private String fid;
	
	private String businessCode;
	private String categoryCode;
	private String cId;
	
	private Date docDate;
	
	private String kwdA;
	private String kwdB;
	
	private int docCntA = 0;
	private int docCntB = 0;
	private int docCntBoth = 0;
	
	private int posCntBoth = 0;
	private int negCntBoth = 0;
	private int neuCntBoth = 0;

	private String posPercent = "0";
	private String negPercent = "0";
	private String neuPercent = "0";
	
	private Date loadDate;
	private Date updateDate;
	
	private String channel;
	private String site = "";
	
	private String year;
	private String month;
	private String week;
	private String quarter;
	
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public int getNeuCntBoth() {
		return neuCntBoth;
	}
	public void setNeuCntBoth(int neuCntBoth) {
		this.neuCntBoth = neuCntBoth;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getQuarter() {
		return quarter;
	}
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public int getRn() {
		return rn;
	}
	public void setRn(int rn) {
		this.rn = rn;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	public Date getDocDate() {
		return docDate;
	}
	public void setDocDate(Date docDate) {
		this.docDate = docDate;
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
	public int getDocCntA() {
		return docCntA;
	}
	public void setDocCntA(int docCntA) {
		this.docCntA = docCntA;
	}
	public int getDocCntB() {
		return docCntB;
	}
	public void setDocCntB(int docCntB) {
		this.docCntB = docCntB;
	}
	public int getDocCntBoth() {
		return docCntBoth;
	}
	public void setDocCntBoth(int docCntBoth) {
		this.docCntBoth = docCntBoth;
	}
	public int getPosCntBoth() {
		return posCntBoth;
	}
	public void setPosCntBoth(int posCntBoth) {
		this.posCntBoth = posCntBoth;
	}
	public int getNegCntBoth() {
		return negCntBoth;
	}
	public void setNegCntBoth(int negCntBoth) {
		this.negCntBoth = negCntBoth;
	}
	public Date getLoadDate() {
		return loadDate;
	}
	public void setLoadDate(Date loadDate) {
		this.loadDate = loadDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getPosPercent() {
		return posPercent;
	}
	public void setPosPercent(String posPercent) {
		this.posPercent = posPercent;
	}
	public String getNegPercent() {
		return negPercent;
	}
	public void setNegPercent(String negPercent) {
		this.negPercent = negPercent;
	}
	public String getNeuPercent() {
		return neuPercent;
	}
	public void setNeuPercent(String neuPercent) {
		this.neuPercent = neuPercent;
	}
	
}
