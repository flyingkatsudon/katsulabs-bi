package com.shinhan.vo;

import java.util.Date;

public class StockDisclosureLinkVO {

	private String isuCd;
	private String isuNm;
	private Date isuDcsDate;

	private String dcsCd;
	private String dcsNm;
	private String link;
	
	public String getDcsNm() {
		return dcsNm;
	}
	public void setDcsNm(String dcsNm) {
		this.dcsNm = dcsNm;
	}
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
	public Date getIsuDcsDate() {
		return isuDcsDate;
	}
	public void setIsuDcsDate(Date isuDcsDate) {
		this.isuDcsDate = isuDcsDate;
	}
	public String getDcsCd() {
		return dcsCd;
	}
	public void setDcsCd(String dcsCd) {
		this.dcsCd = dcsCd;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
}
