package com.shinhan.vo;

import java.util.Date;

public class DocSentimentVO {

	private String kwdA;
	private String kwdB;
	private Date docDate;
	private String kwd;
	private String sentFlag;
	private String sentiFlag;
	private double kwdSentiValue = 0;
	
	public String getSentFlag() {
		return sentFlag;
	}
	public void setSentFlag(String sentFlag) {
		this.sentFlag = sentFlag;
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
	public Date getDocDate() {
		return docDate;
	}
	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}
	public String getKwd() {
		return kwd;
	}
	public void setKwd(String kwd) {
		this.kwd = kwd;
	}
	public double getKwdSentiValue() {
		return kwdSentiValue;
	}
	public void setKwdSentiValue(double kwdSentiValue) {
		this.kwdSentiValue = kwdSentiValue;
	}
	public String getSentiFlag() {
		return sentiFlag;
	}
	public void setSentiFlag(String sentiFlag) {
		this.sentiFlag = sentiFlag;
	}
	
}
