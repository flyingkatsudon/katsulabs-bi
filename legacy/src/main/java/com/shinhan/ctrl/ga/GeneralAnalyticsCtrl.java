package com.shinhan.ctrl.ga;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinhan.biz.ga.GeneralAnalyticsBiz;
import com.shinhan.vo.ParamGaVO;

@Controller
@RequestMapping("/ga")
public class GeneralAnalyticsCtrl {

	@Autowired
	private GeneralAnalyticsBiz generalAnalyticsBiz;
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralAnalyticsCtrl.class);

	public ParamGaVO setParam(ParamGaVO vo) {

		ParamGaVO param = new ParamGaVO();
		
		if (vo != null) {
			
			if (vo.getSearchPattern() == null && vo.getSearchPattern().equals("")) {
				param.setSearchPattern(null);
			} else {
				param.setSearchPattern(vo.getSearchPattern());
			}
			
			if (vo.getStartDate() == null || vo.getStartDate().equals("")) param.setStartDate(null);
			else param.setStartDate(vo.getStartDate());
			
			if (vo.getEndDate() == null || vo.getEndDate().equals("")) param.setEndDate(null);
			else param.setEndDate(vo.getEndDate());
			
			/** 
			 * startDate, endDate 중 어느 하나만 있으면: 동일한 날짜
			 * 아무 값도 없다면: 오늘 날짜
			*/
			String defDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String d  = param.getDate();
			String sd = param.getStartDate();
			String ed = param.getEndDate();
			
			if (d == null) {
				if (ed == null) {
					param.setDate(defDate);
					param.setEndDate(defDate);
				} else {
					param.setDate(ed);
				}
				d = param.getDate();
				ed = param.getEndDate();
			}
			
			if (sd != null && ed == null) param.setEndDate(sd);
			if (ed != null && sd == null) param.setStartDate(ed);
			
			if (vo.getSearchData() == null && vo.getSearchData().equals("")) {
				param.setSearchData(null);
			} else {
				param.setSearchData(vo.getSearchData());
			}
			
			if (vo.getCategoryCode() == null || vo.getCategoryCode().equals("")) {
				param.setCategoryCode(null);
			} else {
				param.setCategoryCode(vo.getCategoryCode());
			}
			
			if (vo.getSection() == null || vo.getSection().equals("")) {
				param.setSection(null);
			} else {
				param.setSection(vo.getSection());
			}
			
			if (vo.getAnalyticsCode() == null || vo.getAnalyticsCode().equals("")) {
				param.setAnalyticsCode(null);
			} else {
				param.setAnalyticsCode(vo.getAnalyticsCode());
			}
			
			if (vo.getLimitCnt() == null || vo.getLimitCnt().equals("")) {
				param.setLimitCnt(null);
			} else {
				param.setLimitCnt(vo.getLimitCnt());
			}
			
			if (vo.getPageCnt() == null || vo.getPageCnt().equals("")) {
				param.setPageCnt(null);
			} else {
				param.setPageCnt(vo.getPageCnt());
			}
			
			if (vo.getCurrentPage() == null || vo.getCurrentPage().equals("")) {
				param.setCurrentPage(null);
			} else {
				param.setCurrentPage(vo.getCurrentPage());
			}
			
			if (vo.getBusinessCode() == null || vo.getBusinessCode().equals("")) {
				param.setBusinessCode("U");
			} else {
				param.setBusinessCode(vo.getBusinessCode());
			}

			if (vo.getPress() == null || vo.getPress().equals("")) {
				param.setPress(null);
			} else {
				param.setPress(vo.getBusinessCode());
			}
			
			if (vo.getVersion() == null || vo.getVersion().equals("")) {
				param.setVersion("0.1");
			} else {
				param.setVersion(vo.getVersion());
			}
		}
		
		logger.info("ANALYTICS CODE: " + param.getAnalyticsCode());
		logger.info("searchPattern: " + param.getSearchPattern());
		logger.info("searchData: " + param.getSearchData());
		logger.info("startDate: " + param.getStartDate());
		logger.info("endDate: " + param.getEndDate());
		logger.info("categoryCode: " + param.getCategoryCode());
		logger.info("limitCnt: " + param.getLimitCnt());
		logger.info("pageCnt: " + param.getPageCnt());
		logger.info("currendPage: " + param.getCurrentPage());
		logger.info("section: " + param.getSection());
		logger.info("businessCode: " + param.getBusinessCode());
		logger.info("press: " + param.getPress());
		logger.info("version: " + param.getVersion() + "\n");
		
		logger.info("jobId: " + param.getJobId());
		logger.info("cnt: " + param.getCnt());
		logger.info("kwd: " + param.getKwd());
		logger.info("query: " + param.getQuery());
		logger.info("domainOpt: " + param.getDomainOpt());
		logger.info("businessCode: " + param.getBusinessCode());
		logger.info("flag: " + param.getFlag());
		logger.info("fid: " + param.getFid() + "\n");
		
		return param;
	}

	/* 검색엔진 사용 */
	// 일반분석환경 - 상세검색
	@PostMapping("/call_general_analytics_api")
	@ResponseBody
	public Map<String, Object> getAnalyticsDocs(@RequestBody ParamGaVO param) throws Throwable {
		return generalAnalyticsBiz.getAnalyticsDocs(setParam(param));
	}

	/* 검색엔진 사용 */
	@PostMapping("/call_general_analytics_excel")
	@ResponseBody
	public void getXlsx(@ModelAttribute ParamGaVO param, HttpServletResponse res) throws Exception {
		String oriKwd = param.getSearchPattern();
		String section = param.getSection();
		param.setSearchPattern(URLDecoder.decode(oriKwd, "UTF-8"));
		if (section != null)
			param.setSection(URLDecoder.decode(section, "UTF-8"));

		try {
			generalAnalyticsBiz.getAnalyticsXlsx(setParam(param), res);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
