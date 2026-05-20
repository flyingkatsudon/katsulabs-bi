package com.shinhan.vo;

import java.util.Date;

public class StockExpertReportVO {

	private String isuCd;
	private String rptCmpn;
	private String isuNm;
	private Date rptDate;
	private String opinion;
	private double curPr;
	private double tgtPr;
	
	public String getIsuCd() {
		return isuCd;
	}
	public void setIsuCd(String isuCd) {
		this.isuCd = isuCd;
	}
	public String getRptCmpn() {
		return rptCmpn;
	}
	public void setRptCmpn(String rptCmpn) {
		this.rptCmpn = rptCmpn;
	}
	public String getIsuNm() {
		return isuNm;
	}
	public void setIsuNm(String isuNm) {
		this.isuNm = isuNm;
	}
	public Date getRptDate() {
		return rptDate;
	}
	public void setRptDate(Date rptDate) {
		this.rptDate = rptDate;
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public double getCurPr() {
		return curPr;
	}
	public void setCurPr(double curPr) {
		this.curPr = curPr;
	}
	public double getTgtPr() {
		return tgtPr;
	}
	public void setTgtPr(double tgtPr) {
		this.tgtPr = tgtPr;
	}
}
