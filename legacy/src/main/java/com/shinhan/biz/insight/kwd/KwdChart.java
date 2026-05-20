package com.shinhan.biz.insight.kwd;

import java.util.Map;

import com.shinhan.vo.ParamVO;

public interface KwdChart {

	// 키워드 네트워크
	public Map<String, Object> getKwdAsso(ParamVO param);

	// 기간별 연관어 차트
	public Map<String, Object> getKwdAssoV2(ParamVO param);
	
	// 긍부정차트
	public Map<String, Object> getEmoAsso(ParamVO param);
	
	// 금투 - 공시
	public Map<String, Object> getDisclosureInfo(ParamVO param);
	
	// 금투 - 전문가 의견
	public Map<String, Object> getExpertOpinion(ParamVO param);
}
