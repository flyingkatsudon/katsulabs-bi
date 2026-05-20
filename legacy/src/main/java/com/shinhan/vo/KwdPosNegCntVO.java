package com.shinhan.vo;

import java.util.Date;

public class KwdPosNegCntVO {

	private String kwd;
	private Date docDate;
	private Long timestamp;
	private String categoryCode;
	private String cId;
	private int cnt = 0;
	private int posCnt = 0;
	private int negCnt = 0;
	private Date loadDate;
	private Date updateDate;
	
	private String channel; 
	private int freqBoth = 0;
	
	public int getFreqBoth() {
		return freqBoth;
	}
	public void setFreqBoth(int freqBoth) {
		this.freqBoth = freqBoth;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	private String kwdA;
	private String kwdB;

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
	private String site; // SNS 채널명 e.g, twitter, instagram..
	
	public String getKwd() {
		return kwd;
	}
	public void setKwd(String kwd) {
		this.kwd = kwd;
	}
	public Date getDocDate() {
		return docDate;
	}
	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
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
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
}
