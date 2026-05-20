package com.shinhan.biz.insight.trend;

import java.util.Map;

import com.shinhan.vo.ParamVO;

public interface TrendChart {

	// 트렌드차트 new
	public Map<String, Object> getKwdTrendV2(ParamVO param);
	
	// 채널별 확산 new
	public Map<String, Object> getTrendV2(ParamVO param);

	// 종목브리핑 - 연관종목
	public Map<String, Object> getRelStock(ParamVO param);

	// 금투 - 종목브리핑 - 종목검색
	public Map<String, Object> getStockInfo(ParamVO param);
}
