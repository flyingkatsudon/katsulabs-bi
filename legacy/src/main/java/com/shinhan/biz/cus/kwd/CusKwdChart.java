package com.shinhan.biz.cus.kwd;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.shinhan.vo.ParamVO;

public interface CusKwdChart {

	// Bar 차트
	public Map<String, Object> getKwd(ParamVO param);

	// 주차별 키워드의 연관어 (다음레포트에서 가져옴)
	public Map<String, Object> getKwdAssoV2(ParamVO param);
	
	public Map<String, Object> getUserInfo(HttpSession session);
}
