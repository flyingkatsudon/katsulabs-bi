package com.shinhan.biz.cus.trend.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.shinhan.biz.cus.trend.CusTrendChart;
import com.shinhan.dao.CusTrendChartDAO;
import com.shinhan.vo.DailyKwdTrendCntV2VO;
import com.shinhan.vo.ParamVO;

public class CusTrendChartImpl implements CusTrendChart {

	@Autowired
	private CusTrendChartDAO cusTrendChartDAO;

	// 다음레포트 트렌드차트 new
	@Override
	public Map<String, Object> getTrendV2(ParamVO param) {

		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Object> list = new ArrayList<Object>();
		
		switch(param.getPeriod()) {
		case "day":
			list = cusTrendChartDAO.getTrendV2(param);
			break;
		case "week":
			list = cusTrendChartDAO.getWeeklyTrendV2(param);
			break;
		case "mon":
			list = cusTrendChartDAO.getMonthlyTrendV2(param);
			break;
		case "quarter":
			list = cusTrendChartDAO.getQuarterlyTrendV2(param);
			break;
		case "year":
			list = cusTrendChartDAO.getYearlyTrendV2(param);
			break;
		default:
			list = cusTrendChartDAO.getTrendV2(param);
		}
		
		try {
			String[] tmpCnames = {"NEWS", "SNS", "COMMUNITY", "BLOG", "ETC"};
			
			map.put("data", setCusTrend(list));
			map.put("cnames", tmpCnames);

			map.put("isSuccess", true);

		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}

		map.put("requestDate", new Timestamp(System.currentTimeMillis()));

		return map;
	}

	public ArrayList<Map<String, Object>> setCusTrend(ArrayList<Object> list) {

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if(list != null && list.size() > 0) {
			for (Object o : list) {
				DailyKwdTrendCntV2VO vo = (DailyKwdTrendCntV2VO) o;
				Map<String, Object> map = new HashMap<String, Object>();
	
				if (vo.getDocDate() != null) map.put("docDate", vo.getDocDate());
				if (vo.getCategoryCode() != null) map.put("categoryCode", vo.getCategoryCode());
				if (vo.getChannel() != null) map.put("channel", vo.getChannel());
				
				map.put("docCntBoth", vo.getDocCntBoth());
				
				result.add(map);
			}
		}

		return result;
	}
}
