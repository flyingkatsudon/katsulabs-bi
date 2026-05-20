package com.shinhan.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shinhan.vo.ParamVO;

@Repository
public interface CusKwdChartDAO {

	// Bar 차트
	ArrayList<Object> getKwd(@Param("param") ParamVO param);

	ArrayList<Object> getWeeklyKwd(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyKwd(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyKwd(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyKwd(@Param("param") ParamVO param);

	// 키워드 네트워크
	ArrayList<Object> getKwdAsso(@Param("param") ParamVO param);

	ArrayList<Object> getWeeklyKwdAsso(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyKwdAsso(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyKwdAsso(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyKwdAsso(@Param("param") ParamVO param);

	// 주차별 키워드의 연관어 (다음레포트에서 가져옴)
	ArrayList<Object> getWeeklyKwdAssoV2(@Param("param") ParamVO param);
}
