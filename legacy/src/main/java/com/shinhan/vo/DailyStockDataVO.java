package com.shinhan.vo;

import java.util.Date;

public class DailyStockDataVO {

	private String isuCd;
	private String isuNm;
	private Date isuCurDate;

	private double curPr;
	private double flucTpCd;
	private double prvDdCmpr;
	private double updnRate;
	
	private Date loadDate;
	private Date updateDate;
	
	public String getIsuCd() {
		return isuCd;
	}
	public void setIsuCd(String isuCd) {
		this.isuCd = isuCd;
	}
	public String getIsuNm() {
		return isuNm;
	}
	public void setIsuNm(String isuNm) {
		this.isuNm = isuNm;
	}
	public Date getIsuCurDate() {
		return isuCurDate;
	}
	public void setIsuCurDate(Date isuCurDate) {
		this.isuCurDate = isuCurDate;
	}
	public double getCurPr() {
		return curPr;
	}
	public void setCurPr(double curPr) {
		this.curPr = curPr;
	}
	public double getFlucTpCd() {
		return flucTpCd;
	}
	public void setFlucTpCd(double flucTpCd) {
		this.flucTpCd = flucTpCd;
	}
	public double getPrvDdCmpr() {
		return prvDdCmpr;
	}
	public void setPrvDdCmpr(double prvDdCmpr) {
		this.prvDdCmpr = prvDdCmpr;
	}
	public double getUpdnRate() {
		return updnRate;
	}
	public void setUpdnRate(double updnRate) {
		this.updnRate = updnRate;
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
}
