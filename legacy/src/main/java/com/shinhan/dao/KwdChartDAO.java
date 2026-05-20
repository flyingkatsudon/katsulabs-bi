package com.shinhan.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shinhan.vo.ParamVO;

@Repository
public interface KwdChartDAO {
	
	ArrayList<Object> getKwdAsso(@Param("param") ParamVO param);
	
	ArrayList<Object> getWeeklyKwdAsso(@Param("param") ParamVO param);
	
	ArrayList<Object> getMonthlyKwdAsso(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyKwdAsso(@Param("param") ParamVO param);
	
	ArrayList<Object> getYearlyKwdAsso(@Param("param") ParamVO param);
	
	// 다음레포트  기간별 연관어 차트
	//ArrayList<Object> getDailyKwdAssoV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getWeeklyKwdAssoV2(@Param("param") ParamVO param);
	
	//ArrayList<Object> getMonthlyKwdAssoV2(@Param("param") ParamVO param);
	
	//ArrayList<Object> getQuarterlyKwdAssoV2(@Param("param") ParamVO param);
	
	//ArrayList<Object> getYearlyKwdAssoV2(@Param("param") ParamVO param);

	// 긍부정차트
	ArrayList<Object> getEmoAsso(@Param("param") ParamVO param);
	
	ArrayList<Object> getEmoAssoV2(@Param("param") ParamVO param);
	
	ArrayList<Object> getWeeklyEmoAsso(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyEmoAsso(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyEmoAsso(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyEmoAsso(@Param("param") ParamVO param);

	ArrayList<Object> getWeeklyEmoAssoV2(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyEmoAssoV2(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyEmoAssoV2(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyEmoAssoV2(@Param("param") ParamVO param);
	
	// 금투-공시
	ArrayList<Object> getDisclosureInfo(@Param("param") ParamVO param);

	ArrayList<Object> getExpertOpinion(@Param("param") ParamVO param);
}
