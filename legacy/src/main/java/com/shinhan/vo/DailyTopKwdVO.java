package com.shinhan.vo;

import java.util.Date;

public class DailyTopKwdVO {

	private int rn;
	private String kwdA;
	private String kwdB;
	private Date docDate;
	private int freqBoth;
	private int cnt;
	private int posCnt;
	private int negCnt;
	private int priority;
	private String categoryCode;
	private String cId;
	
	public int getRn() {
		return rn;
	}
	public void setRn(int rn) {
		this.rn = rn;
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
	public int getFreqBoth() {
		return freqBoth;
	}
	public void setFreqBoth(int freqBoth) {
		this.freqBoth = freqBoth;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public int getPosCnt() {
		return posCnt;
	}
	public void setPosCnt(int posCnt) {
		this.posCnt = posCnt;
	}
	public int getNegCnt() {
		return negCnt;
	}
	public void setNegCnt(int negCnt) {
		this.negCnt = negCnt;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
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
}
