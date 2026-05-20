package com.shinhan.biz.ga;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.shinhan.vo.ParamGaVO;

public interface GeneralAnalyticsBiz {

	// 일반분석환경 - 상세검색
	public Map<String, Object> getAnalyticsDocs(ParamGaVO param) throws Throwable;
	
	public void getAnalyticsXlsx(ParamGaVO param, HttpServletResponse res) throws Exception;
}
