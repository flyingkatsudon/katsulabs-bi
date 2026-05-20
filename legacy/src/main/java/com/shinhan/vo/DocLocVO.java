package com.shinhan.vo;

import java.util.Date;

public class DocLocVO {

	private String docUid;
	private String sentUid;
	private String categoryCode;
	
	private Date docDate;
	private String location;
	private String markerType;
	private int marker_idx;
	private int loc_idx;
	
	private double lat;
	private double lon;
	
	private Date loadDate;
	private Date updateDate;
	
	private String docContent;

	public String getDocUid() {
		return docUid;
	}

	public void setDocUid(String docUid) {
		this.docUid = docUid;
	}

	public String getSentUid() {
		return sentUid;
	}

	public void setSentUid(String sentUid) {
		this.sentUid = sentUid;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	public int getMarker_idx() {
		return marker_idx;
	}

	public void setMarker_idx(int marker_idx) {
		this.marker_idx = marker_idx;
	}

	public int getLoc_idx() {
		return loc_idx;
	}

	public void setLoc_idx(int loc_idx) {
		this.loc_idx = loc_idx;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
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

	public String getDocContent() {
		return docContent;
	}

	public void setDocContent(String docContent) {
		this.docContent = docContent;
	}
}
