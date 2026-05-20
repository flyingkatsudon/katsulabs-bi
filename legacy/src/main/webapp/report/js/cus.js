(function() {

window.onProcess = function(from, to) {

	console.log(from, to);

	var $inpText = $(".search_box_inp").val();
	
	if (!$inpText) $inpText = "";
	$inpText = $inpText.replace(/\s+/gi, "");
	
	if (!$inpText || $inpText.length < 2) {
		alert("검색어를 2글자이상 입력해주세요.");
		$("body").loading("stop");
		return false;
	}

	$(".temp_wrap").show();
	$(".cus_kwd_asso_time_table_wrap").show();
	$(".cus_kwd_asso_time_kwd_search").hide();

	$(".daum_container_title").html("로딩중입니다");
	$(".cus_kwd_asso_time_table_title").html("로딩중입니다");
	$(".cus_network_chart_title").html("로딩중입니다");
 	$(".cus_bar_chart_title").html("로딩중입니다");
	
 	$("body").loading();

 	// 1. 트랜드차트
	var param = {
		kwd: $(".search_box_inp").val().trim(),
		startDate: from,
		endDate: to,
		businessCode: sv.groupCd, 
		period: sv.period
	};
	//param.fid = "BDPC04050102";
	
	$.ajax({
		url: SDII.Url + "/cus/get_trd",
		type: 'post',
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data: JSON.stringify(param),
		success: function(trendResult) {

			console.log(trendResult);
			
			if (!trendResult.isSuccess) {
		  		alert("데이터 요청에 실패하였습니다.");
		  		return false;
		  	}
	
		  	if (trendResult.data.length === 0) $("body").loading("stop");
	
		  	$(".daum_container_title").html("\""+$(".search_box_inp").val().trim()+"\" 일별 언급량");
		  	new SDII.Chart.cusTrendChart().setData(trendResult).onDrawChart();
		  	
		  	return true;
	    }
	});
	
	// 2. 네트워크 차트 + 동시출현빈도수
	//param.fid = "BDPC04050103";
	$.ajax({
		url: SDII.Url + "/cus/get_kwd",
		type: 'post',
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data: JSON.stringify(param),
		success: function(kwdResult) {
		  	if (!kwdResult.isSuccess) {
		  		alert("데이터 요청에 실패하였습니다.");
		  		return false;
		  	}
		  	
		  	//if (kwdResult.data.length === 0) $("body").loading("stop");
	
		  	$(".cus_network_chart_title").html("\""+$(".search_box_inp").val().trim()+"\" 연관어 네트워크");
		  	$(".cus_bar_chart_title").html("\""+$(".search_box_inp").val().trim()+"\" 연관어 동시출현빈도수");
		  	
		  	new SDII.Chart.cusKwdAssoNetwork().setData(kwdResult).onDrawChart();	        	
		  	new SDII.Chart.cusKwdAssoChart().setData(kwdResult).onDrawChart();
	
		  	$('#myScroll').css('overflow', 'hidden');
		  	
		  	return true;
	    }
	});
	
	// 기간별 연관어 상위25개 테이블
	var newParam = {
     kwd: $(".search_box_inp").val().trim(),
     startDate: moment(to).subtract(35, "days").format("YYYY-MM-DD"),
     endDate: to,
     period: "week",
     cnt: "100",
     //businessCode: "SH" //sv.groupCd,
     //fid: sv.reqMap[sv.category].assoKwd
  };
	//newParam.fid = "BDPC04050104";

	$.ajax({
		url: SDII.Url + "/cus/get_kwd_asso_v2",
		type: 'post',
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data:  JSON.stringify(newParam),
		success: function(kwdAssoTimeResult) {

			if (!kwdAssoTimeResult.isSuccess) {
		  		alert("데이터 요청에 실패하였습니다.");
		  		return false;
		  	}

		  	//if (kwdAssoTimeResult.data.length === 0) $("body").loading("stop");
	
		  	var kwd = $(".search_box_inp").val();
		  	if (!kwdAssoTimeResult.data || kwdAssoTimeResult.data.length === 0) {
		  		$(".cus_kwd_asso_time_table_wrap").html(
		  			"<div class='chart_trend_sub_title' style='padding:20px'>\"" +kwd + "\"에 대한 연관어 검색 결과를 찾을 수 없습니다.</div>");
		  		return false;
		  	}
	
		  	$(".cus_kwd_asso_time_table_wrap").html(
		  		"<div class='d-flex justify-content-between'>" +
		  			"<div class='chart_trend_sub_title' style='padding:20px'>[" + kwd + "] 연관어 기간별 Top25</div>" +
		  			"<div class='btn_search_kwd'>[선택한 키워드 원문검색]</div>" +
		  		"</div>" +
		  		"<div class='chart_trend_sub_container'></div>"
	  		);
		  	
		  	new SDII.Chart.kwdAssoTimeTable(newParam.cnt, $(".chart_trend_sub_container"), true).setData(kwdAssoTimeResult).onDrawChart();
		  	
		  	return true;
	    }		
	});
	return true;
}

$(document).ready(function() {

	$("body").loading({ message: '데이터 조회중 입니다'});

	function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
	}

	$(".flatpickr").flatpickr({locale : "ko", wrap: true});

	$(".range_wrap .btn_range").on("click", function() {
		$(this).parent().find("div").removeClass("btn_range_click");
		$(this).addClass("btn_range_click");
	});

	//$lastMon = new Date();
	//$lastMon.setMonth($lastMon.getMonth() - 1);
	//$(".flatpickr")[0]._flatpickr.setDate($lastMon);
	//$(".flatpickr")[1]._flatpickr.setDate(new Date());
	//$(".flatpickr")[0]._flatpickr.setDate(new Date(2018, 8, 1));
	//$(".flatpickr")[1]._flatpickr.setDate(new Date(2018, 9, 15));

	//$(".search_date").text("요약 조회기준일 : " + $(".flatpickr")[1]._flatpickr.input.value);
	$(".cus_kwd_asso_time_kwd_search").hide();

	$(".btn_range").click(function() {

		var $curVal = $(this).attr("data-value");

		if ($curVal === "day")          sv.period = "day";
		else if ($curVal === "week")    sv.period = "week";
		else if ($curVal === "mon")     sv.period = "mon";
		else if ($curVal === "quarter") sv.period = "quarter";
		else if ($curVal === "year")    sv.period = "year";

		console.log("Selected Peried : " + sv.period);
	});

	var tempDef = moment(new Date()),
			toDef   = "", fromDef = "";

	toDef   = tempDef.format("YYYY-MM-DD");
	fromDef = tempDef.subtract(3, "months").format("YYYY-MM-DD");

	var from = getUrlParameter("from") || fromDef;
	var to   = getUrlParameter("to")   || toDef;

	$(".flatpickr")[0]._flatpickr.setDate(from);
	$(".flatpickr")[1]._flatpickr.setDate(to);
	
	$(".btn_search_box").on("click", function() {

		var from = $("input[name=date_from]").val();
		var to   = $("input[name=date_to]").val();

		onProcess(from, to);
	});

	$(".btn_search_detl_box").on("click", function() {
		$(".control_bar_show_wrap").toggle();
	});

	$(".search_box_inp ").on("keydown", function() {
		if (event.keyCode === 13) {
			var from = $("input[name=date_from]").val();
			var to   = $("input[name=date_to]").val();
			onProcess(from, to); 
			
		}
		return false;
	});
	
	$(".btn_insight_report").on("click", function() {
		history.back(-1);
	});

	$(".go_starter_btn").on("click", function() {
		location.href = "/bdp/cboard/starter.jsp";
	});
	//INIT
	$(".control_bar_show_wrap").toggle();
	$(".search_box_inp").val("신한");
	onProcess(from, to);
}); //document ready end


})(); //function end
