package com.shinhan.ctrl.report;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinhan.biz.insight.docs.DocTable;
import com.shinhan.biz.insight.kwd.KwdChart;
import com.shinhan.biz.insight.trend.TrendChart;
import com.shinhan.util.SetData;
import com.shinhan.vo.ParamVO;

@Controller
@RequestMapping("/report")
public class ReportCtrl {

	@Autowired
	private DocTable docTable;

	@Autowired
	private TrendChart trendChart;

	@Autowired
	private KwdChart kwdChart;
	
	private SetData setData = new SetData();

	/* NEW */
	// 요약 스크립트
	@PostMapping("/get_abs_src")
	@ResponseBody
	public Map<String, Object> getAbsScript(@RequestBody ParamVO param) throws ParseException {
		return docTable.getAbsScript(setData.setParam(param));
	}
	
	// 요약문 목록 및 원문보기
	@PostMapping("/get_smmry_extv")
	@ResponseBody
	public Map<String, Object> getSmmryExtv(@RequestBody ParamVO param) {
		return docTable.getSmmryExtv(setData.setParam(param));
	}
	
	// 트렌드차트
	@PostMapping("/get_kwd_trd_v2")
	@ResponseBody
	public Map<String, Object> getKwdTrendV2(@RequestBody ParamVO param) throws ParseException {
		
		// 금투-연관종목
		ArrayList<String> fidList = new ArrayList<String>(); // 키워드 리스트 String을 list로 구분하여 저장
		if (param.getFid() != null && param.getFid().contains(",")) { // fid에 콤마가 포함이면
			String[] arr = param.getFid().split(","); // 파싱
	
			for (int i = 0; i < arr.length; i++) {
				fidList.add(arr[i].trim());
			}
	
			param.setFidList(fidList);
		}
		
		// fid가 여러개 들어왔을 때
		if (fidList.contains("BDPC04030305")) return trendChart.getRelStock(setData.setParam(param));
		return trendChart.getKwdTrendV2(setData.setParam(param));
	}
	
	// 채널별 확산
	@PostMapping("/get_trd_v2")
	@ResponseBody
	public Map<String, Object> getTrendV2(@RequestBody ParamVO param) throws ParseException {
		// 시장브리핑 - 코스피/코스닥 지수추이
		/*if(param.getFid().equals("BDPC04030205")) {
			return trendChart.getMarketTrend(setData.setParam(param));
		} else {
			return trendChart.getTrendV2(setData.setParam(param));
		}*/
		return trendChart.getTrendV2(setData.setParam(param));
	}

	// 기간별 연관어 차트
	@PostMapping("/get_kwd_asso")
	@ResponseBody
	public Map<String, Object> getKwdAsso(@RequestBody ParamVO param) throws ParseException {
		return kwdChart.getKwdAsso(setData.setParam(param));
	}
	
	// 주차별 연관어 차트
	@PostMapping("/get_kwd_asso_v2")
	@ResponseBody
	public Map<String, Object> getKwdAssoV2(@RequestBody ParamVO param) throws ParseException {
		return kwdChart.getKwdAssoV2(setData.setParam(param));
	}

	// 긍부정차트
	@PostMapping("/get_emo_asso")
	@ResponseBody
	public Map<String, Object> getEmoAsso(@RequestBody ParamVO param) throws Throwable {
		return kwdChart.getEmoAsso(setData.setParam(param));
	}
	
	// 금투-종목검색
	@PostMapping("/get_stock_info")
	@ResponseBody
	public Map<String, Object> getStockInfo(@RequestBody ParamVO param) throws Throwable {
		return trendChart.getStockInfo(setData.setParam(param));
	}
	
	// 금투-공시
	@PostMapping("/get_disclosure_info")
	@ResponseBody
	public Map<String, Object> getDisclosureInfo(@RequestBody ParamVO param) throws Throwable {
		return kwdChart.getDisclosureInfo(setData.setParam(param));
	}
	
	// 금투 - 전문가 의견
	@PostMapping("/get_expert_opinion")
	@ResponseBody
	public Map<String, Object> getExpertOpinion(@RequestBody ParamVO param) throws Throwable {
		return kwdChart.getExpertOpinion(setData.setParam(param));
	}

	/* 검색엔진 사용 */
	// 키워드 네트워크 원문조회
	@PostMapping("/get_full_src")
	@ResponseBody
	public Map<String, Object> getFullSrc(@RequestBody ParamVO param) throws Throwable {
		return docTable.getFullSrc(setData.setParam(param));
	}
}