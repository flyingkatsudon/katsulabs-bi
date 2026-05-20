package com.shinhan.vo;

import java.util.Date;

public class ScriptRelDocsStdInfoVO {

	private int cnt = 0;
	private String categoryCode = "";
	
	private String scriptUid = "";
	private Date scriptPeriodSd;
	private Date scriptPeriodEd;
	private Date scriptDate;
	private String scriptTitle = "";
	private double scriptWeight = 0.0;
	private int scriptRank = 0;
	
	private String relDocUid = "";
	private String relDocSummary = "";
		
	private int docRank;
	private int relDocRank = 0;
	
	public int getRelDocRank() {
		return relDocRank;
	}
	public void setRelDocRank(int relDocRank) {
		this.relDocRank = relDocRank;
	}
	private Date loadDate;
	private Date updateDate;
	
	private String cId;

	private double sumRate = 0.0;
	
	private String scriptLabelL1 = "";
	private String scriptLabelL2 = "";
	private String scriptLabelL3 = "";
	private String scriptLabelL4 = "";
	
	public String getScriptLabelL1() {
		return scriptLabelL1;
	}
	public void setScriptLabelL1(String scriptLabelL1) {
		this.scriptLabelL1 = scriptLabelL1;
	}
	public String getScriptLabelL2() {
		return scriptLabelL2;
	}
	public void setScriptLabelL2(String scriptLabelL2) {
		this.scriptLabelL2 = scriptLabelL2;
	}
	public String getScriptLabelL3() {
		return scriptLabelL3;
	}
	public void setScriptLabelL3(String scriptLabelL3) {
		this.scriptLabelL3 = scriptLabelL3;
	}
	public String getScriptLabelL4() {
		return scriptLabelL4;
	}
	public void setScriptLabelL4(String scriptLabelL4) {
		this.scriptLabelL4 = scriptLabelL4;
	}
	public double getSumRate() {
		return sumRate;
	}
	public void setSumRate(double sumRate) {
		this.sumRate = sumRate;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getScriptUid() {
		return scriptUid;
	}
	public void setScriptUid(String scriptUid) {
		this.scriptUid = scriptUid;
	}
	public Date getScriptPeriodSd() {
		return scriptPeriodSd;
	}
	public void setScriptPeriodSd(Date scriptPeriodSd) {
		this.scriptPeriodSd = scriptPeriodSd;
	}
	public Date getScriptPeriodEd() {
		return scriptPeriodEd;
	}
	public void setScriptPeriodEd(Date scriptPeriodEd) {
		this.scriptPeriodEd = scriptPeriodEd;
	}
	public Date getScriptDate() {
		return scriptDate;
	}
	public void setScriptDate(Date scriptDate) {
		this.scriptDate = scriptDate;
	}
	public String getScriptTitle() {
		return scriptTitle;
	}
	public void setScriptTitle(String scriptTitle) {
		this.scriptTitle = scriptTitle;
	}
	public double getScriptWeight() {
		return scriptWeight;
	}
	public void setScriptWeight(double scriptWeight) {
		this.scriptWeight = scriptWeight;
	}
	public int getScriptRank() {
		return scriptRank;
	}
	public void setScriptRank(int scriptRank) {
		this.scriptRank = scriptRank;
	}
	public String getRelDocUid() {
		return relDocUid;
	}
	public void setRelDocUid(String relDocUid) {
		this.relDocUid = relDocUid;
	}
	public String getRelDocSummary() {
		return relDocSummary;
	}
	public void setRelDocSummary(String relDocSummary) {
		this.relDocSummary = relDocSummary;
	}
	public int getDocRank() {
		return docRank;
	}
	public void setDocRank(int docRank) {
		this.docRank = docRank;
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
	public String getCId() {
		return cId;
	}
	public void setCId(String cId) {
		this.cId = cId;
	}
}
