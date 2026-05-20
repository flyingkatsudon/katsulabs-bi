package com.shinhan.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shinhan.vo.ParamVO;

@Repository
public interface TrendChartDAO {

	// 트렌드차트 new
	ArrayList<Object> getKwdTrendV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getWeeklyKwdTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyKwdTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyKwdTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyKwdTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getKwdTrendV3(@Param("param") ParamVO param);
	
	ArrayList<Object> getWeeklyKwdTrendV3(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyKwdTrendV3(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyKwdTrendV3(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyKwdTrendV3(@Param("param") ParamVO param);
	
	// 질병 브리핑 트랜드 차트
	ArrayList<Object> getKwdDiseaseTrend(@Param("param") ParamVO param); 
	
	ArrayList<Object> getWeeklyKwdDiseaseTrend(@Param("param") ParamVO param); 
	
	ArrayList<Object> getMonthlyKwdDiseaseTrend(@Param("param") ParamVO param); 
	
	ArrayList<Object> getQuarterlyKwdDiseaseTrend(@Param("param") ParamVO param); 
	
	ArrayList<Object> getYearlyKwdDiseaseTrend(@Param("param") ParamVO param); 
	
	// 금투 - 종목브리핑 트렌드차트
	ArrayList<String> getRelStock(@Param("param") ParamVO param);
	
	ArrayList<String> getWeeklyRelStock(@Param("param") ParamVO param);
	
	ArrayList<String> getMonthlyRelStock(@Param("param") ParamVO param);
	
	ArrayList<String> getQuarterlyRelStock(@Param("param") ParamVO param);
	
	ArrayList<String> getYearlyRelStock(@Param("param") ParamVO param);

	// 금투 - 연관종목 트렌드차트
	ArrayList<Object> getStockKwdTrendV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getStockKwdTrendV3(@Param("param") ParamVO param);
	
	ArrayList<Object> getWeeklyStockKwdTrendV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getMonthlyStockKwdTrendV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getQuarterlyStockKwdTrendV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getYearlyStockKwdTrendV2(@Param("param") ParamVO param);
	
	// 금투 - 연관종목 top1 이슈종목 트렌드차트
	ArrayList<Object> getIsuRelKwds(@Param("param") ParamVO param);

	ArrayList<Object> getWeeklyIsuRelKwds(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyIsuRelKwds(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyIsuRelKwds(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyIsuRelKwds(@Param("param") ParamVO param);
	
	// 채널별 확산 new
	ArrayList<Object> getTrendV2(@Param("param") ParamVO param);
	
	// 시장브리핑 - 코스피/코스닥 지수추이
	ArrayList<Object> getNrtMarketTrend(@Param("param") ParamVO param);
	
	ArrayList<Object> getMarketTrend(@Param("param") ParamVO param);

	// 금투 - 종목브리핑 - 종목검색
	ArrayList<Object> getStockInfo(@Param("param") ParamVO param);
	
	ArrayList<Object> getNrtStockInfo(@Param("param") ParamVO param);
}
