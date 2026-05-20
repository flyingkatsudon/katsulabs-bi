package com.shinhan.dao;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.shinhan.vo.ParamVO;

@Repository
public interface CusTrendChartDAO {
	
	// 다음레포트 트렌드차트 new
	ArrayList<Object> getTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getWeeklyTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getMonthlyTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getQuarterlyTrendV2(@Param("param") ParamVO param);

	ArrayList<Object> getYearlyTrendV2(@Param("param") ParamVO param);

}
