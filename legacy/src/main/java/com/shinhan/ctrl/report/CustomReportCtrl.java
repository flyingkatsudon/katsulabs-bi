package com.shinhan.ctrl.report;

import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinhan.biz.cus.kwd.CusKwdChart;
import com.shinhan.biz.cus.trend.CusTrendChart;
import com.shinhan.util.SetData;
import com.shinhan.vo.ParamVO;

@Controller
@RequestMapping("/cus")
public class CustomReportCtrl {
	
	@Autowired
	private CusTrendChart cusTrendChart;
	
	@Autowired
	private CusKwdChart cusKwdChart;
	
	private SetData setData = new SetData();

	/* NEW */

	// 다음레포트 트렌드차트
	@PostMapping("/get_trd")
	@ResponseBody
	public Map<String, Object> getTrendV2(@RequestBody ParamVO param) throws ParseException {
		return cusTrendChart.getTrendV2(setData.setParam(param));
	}
	
	// Bar 차트
	@PostMapping("/get_kwd")
	@ResponseBody
	public Map<String, Object> getKwd(@RequestBody ParamVO param) throws ParseException {
		return cusKwdChart.getKwd(setData.setParam(param));
	}

	// 주차별 키워드의 연관어 (다음레포트에서 가져옴)
	@PostMapping("/get_kwd_asso_v2")
	@ResponseBody
	public Map<String, Object> getKwdAssoV2(@RequestBody ParamVO param) throws ParseException {
		return cusKwdChart.getKwdAssoV2(setData.setParam(param));
	}
	
	// 세션정보
	@PostMapping("/get_user_info")
	@ResponseBody
	public Map<String, Object> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
		return cusKwdChart.getUserInfo(request.getSession());
	}
}
