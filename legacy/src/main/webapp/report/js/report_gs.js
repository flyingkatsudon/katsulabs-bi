/*
 * report.js
 * - insight report base script
 *
 * 
 18.09
 *
 * JayKeem
 *
 * jaykeem@weni.co.kr
*/

(function() {
	
sf.onDrawSchPop = function() {
	$(".sch_popup_back").css("min-height", $(document).innerHeight() + "px").show();
	$(".sch_popup").css("top", $(document).scrollTop() + 100).show();
	$("body").css("overflow", "hidden");
	//var allPickr    = $(".flatpickr");
	var schPickrFrm = $(".flatpickr.sch_pickr_frm");
	var schPickrTo  = $(".flatpickr.sch_pickr_to");
	schPickrFrm.get(0)._flatpickr.setDate($("input[name=date_from]").val());
	schPickrTo.get(0)._flatpickr.setDate($("input[name=date_to]").val());
}

sf.onCloseSchPop = function() {
	$(".sch_popup_back").hide();
	$(".sch_popup").hide();
	$("body").css("overflow", "visible");	
}

sf.onDrawStkPop = function() {
	$(".sch_popup_back").css("min-height", $(document).innerHeight() + "px").show();
	$(".stock_popup").css("top", $(document).scrollTop() + 100).show();
	$("body").css("overflow", "hidden");
}

sf.onCloseStkPop = function() {
	$(".sch_popup_back").hide();
	$(".stock_popup").hide();
	$("body").css("overflow", "visible");
}

sf.onDrawExpertOpinion = function(isuNm) {

	$("body").loading();
	$(".stock_popup_title").text("전문가 의견 : " + isuNm);
	$(".stock_popup_cont_wrap").html("");

	var param = {
		//date: $("input[name=date_to]").val(),
		startDate: $("input[name=date_from]").val(),
		endDate:  $("input[name=date_to]").val(),
		kwd: isuNm
	};
 
	$.ajax({
	    type: "post",
	  	contentType: 'application/json; charset=utf-8',
		dataType: 'json',
	    url: SDII.Url + "/report/get_expert_opinion",
	    data: JSON.stringify(param),

	    success: function (data) {

	    	$("body").loading("stop");
	
			if (!data.isSuccess) {
				alert("데이터 요청에 실패하였습니다.");	        
				return false;
			}
	
			if (!data.data || data.data.length === 0) {
				$(".stock_popup_cont_wrap").html("데이터가 조회되지 않습니다.");
				return false;
			}

			var d = data.data;
			console.log(d);
			var table = $("<table class='table disclosure_tb'></table>");
			table.append(
				"<tr>" +
					"<th>투자의견</th>" +
					"<th>증권사</th> "  +
					"<th>현재가</th>"   +
					"<th>목표가</th>"   +
					"<th>작성일시</th>" +
				"</tr>"
	    	);
			
			for (var i = 0; i < d.length; i++) {
				table.append(
		      		"<tr>" +
		      			"<td>" + d[i].opinion + "</td>" +
		      			"<td>" + d[i].rptCmpn + "</td>" +
		      			"<td>" + sf.addComma(d[i].curPr)  + "원</td>" +
		      			"<td>" + sf.addComma(d[i].tgtPr)  + "원</td>" +
		      			"<td>" + moment(d[i].rptDate).format("YYYY-MM-DD kk:mm:ss") + "</td>" +
		      		"</tr>"
				)
			}

			$(".stock_popup_cont_wrap").append(table);
      
			return true;
	    },

	    error: function (request, status, error) {
	    	$("body").loading("stop");
		    console.log('code: '+request.status+"\n"+'message: '+request.responseText+"\n"+'error: '+error);
		    alert("데이터 요청에 실패하였습니다..");
		    return false;
	  	}
	});

}

sf.onDrawDisclosure = function(isuNm) {

	$("body").loading();
	$(".stock_popup_title").text("공시 : " + isuNm);
	$(".stock_popup_cont_wrap").html("");

	var param = {
		date: $("input[name=date_to]").val(),
		kwd: isuNm,
		cnt: 999
	};

	$.ajax({
	    type: "post",
	  	contentType: 'application/json; charset=utf-8',
		dataType: 'json',
	    url: SDII.Url + "/report/get_disclosure_info",
	    data: JSON.stringify(param),

	    success: function (data) {

	    	$("body").loading("stop");

	    	if (!data.isSuccess) {
		        alert("데이터 요청에 실패하였습니다.");	        
		        return false;
	    	}

	    	if (!data.data || data.data.length === 0) {
		      	$(".stock_popup_cont_wrap").html("데이터가 조회되지 않습니다.");
		      	return false;
	    	}

	    	var d = data.data;
	    	var table = $("<table class='table disclosure_tb'></table>");
	    	table.append(
		      	"<tr>" +
		      		"<th>발표시간</th>" +
		      		"<th>공시유형</th>" +
		      		"<th>링크</th>" +
		    		"</tr>"
	    	);
	    	
	    	for (var i = 0; i < d.length; i++) {
	    		table.append(
		      		"<tr>" +
		      			"<td>" + moment(d[i].isuDcsDate).format("YYYY-MM-DD kk:mm:ss") + "</td>" +
		      			"<td>" + d[i].dcsNm + "</td>" +
		      			"<td><a class='dc_link' url='" +  d[i].link  + "'>" + d[i].link  + "</a></td>" +
		      		"</tr>"
	    		);
	    	}

	    	$(".stock_popup_cont_wrap").append(table);
	    	$(".dc_link").on("click", function() {
	    		var url = $(this).attr("url");
	    		if (!url) return false;
	    		window.open("http://dart.fss.or.kr/" + url, "", "width=1200,height=600");
	    		return true;
	    	});
	    	
	    	return true;
	    },

	    error: function (request, status, error) {
	    	$("body").loading("stop");
		    console.log('code: '+request.status+"\n"+'message: '+request.responseText+"\n"+'error: '+error);
		    alert("데이터 요청에 실패하였습니다..");
		    return false;
	  	}
	});
}

sf.onDrawBoardLink = function(isuNm, isuNum) {

	$(".stock_popup_title").text("투자자의견 : " + isuNm);
	$(".stock_popup_cont_wrap").html("");

	var table = $("<table class='table disclosure_tb'></table>");
	table.append(
	  	"<tr>" +
	  		"<th>명칭</th>" +
	  		"<th>링크</th>" +
			"</tr>"
		);
	
	for (var i = 0; i < sv.links.length / 2; i++) {

	  	var nm = sv.links[i * 2];
		var ln = sv.links[i * 2 + 1] + isuNum;

		table.append(
			"<tr>" +
				"<td>" + nm + "</td>" +
				"<td><a class='dc_link' url='" + ln  + "'>" + ln + "</a></td>" +
			"</tr>"
		);
	}

	$(".stock_popup_cont_wrap").append(table);
	
	$(".dc_link").on("click", function() {
	  	var url = $(this).attr("url");
	  	if (!url) return false;
	  	window.open(url, "", "width=1200,height=600");
	  	return true;
	});
}

sf.onReqKospiChart = function() {

	var period = "day";

	$.ajax({
	  	url: SDII.Url + "/report/get_trd_v2",
	    type: "post",
		contentType: "application/json; charset=utf-8",
		dataType: "JSON",
		data: JSON.stringify({
    	businessCode: sv.groupCd,
    	fid : sv.reqMap[sv.category].kospi,
    	period: period,
    	startDate: $(".flatpickr")[0]._flatpickr.input.value,
    	endDate: $(".flatpickr")[1]._flatpickr.input.value,
    }),
   	success: function (data) {
      if (!data.isSuccess) {
        alert("데이터 요청에 실패하였습니다.");
        return false;
      }
    	sf.onDrawKospiChart(data, period);
    	return true;
    },
    error: sf.onErrorComm

	});
}

sf.onDrawKospiChart = function(data, period) {

	$("body").loading("stop");

	if (!data.isSuccess) {
	  alert("데이터 요청에 실패하였습니다.");
    	return false;
  	}

  	if (!data.data || data.data.length === 0) {
  		alert("코스피/코스닥에 조회되는 데이터가 없습니다.");
  		$("#chart_kospi_sub").html("조회되는 데이터가 없습니다.");
  		return false;
  	}

    $("#chart_kospi_sub").html("");
	$("#chart_kospi_sub").append(
		"<div class='chart_kospi_sub_title_wrap d-flex justify-content-between'>" +
			"<div class='chart_kospi_sub_title'>코스피/코스닥 추이</div>" +
			"<div class='chart_kospi_sub_title_time' style='padding-right:30px;font-size:15px'></div>" +
		"</div>"
	);

	$("#chart_kospi_sub").append("<div id='chart_kospi_sub_area'></div>");
	
	var d     = data.data, 
	  	tempD = {};

	var kospiDataTb = new google.visualization.DataTable();
	kospiDataTb.addColumn("datetime", "Date");
	kospiDataTb.addColumn("number",  "코스피");
	kospiDataTb.addColumn({
		type: "string",
		role: "tooltip",
		p: {html: true}
	});
	kospiDataTb.addColumn("number",  "코스닥");
	kospiDataTb.addColumn({
		type: "string",
		role: "tooltip",
		p: {html: true}
	});
	
	var seriesData = [];
	var maxPr      = 0;
	var minPr      = 100000000;
	var nms        = data.cnames;  
	var rCnt       = 0;

	for (var i = 0; i < d.length; i++) {

	  	var tStamp = moment(d[i].docDate).format("YYYY-MM-DD kk:mm");
	  	var sStamp = tStamp.split(" ");
	  	var dates  = sStamp[0].split("-");
	  	var times  = sStamp[1].split(":");
	  	var fDate  = new Date(dates[0], parseInt(dates[1]) - 1, dates[2], times[0], times[1], 0);

	  	if (!d[i].docCntBoth || d[i].docCntBoth <= 0) continue;

	  	if (!tempD[tStamp]) tempD[tStamp] = [fDate];

		var idxPos = nms.indexOf(d[i].cId);
		console.log(idxPos);
		if (idxPos === -1) {
			alert("Invalid Case"); continue;
		}

	  	var tooltipText = "" +
	  	"<div class='sch_tooltip_wrap'>" + 
	  		"<div class='sch_tooltip_isunm'>" + d[i].channel + "</div>" + 
	  		"<div class='sch_tooltip_date'>" + tStamp + "</div>" + 
	  		"<div class='d-flex sch_tooltip_cont'>" + 
	  			"<div class='stock_price'>" + (d[i].docCntBoth) + "</div>" +
				"</div>" + 
	  	"</div>";

		tempD[tStamp][idxPos * 2 + 1] = d[i].docCntBoth;
		tempD[tStamp][idxPos * 2 + 2] = tooltipText;
		
		if (maxPr < parseInt(d[i].docCntBoth)) maxPr = d[i].docCntBoth;
		if (minPr > parseInt(d[i].docCntBoth)) minPr = d[i].docCntBoth;
		
		if (i === d.length - 1) $(".chart_kospi_sub_title_time").text("(" + tStamp + " 기준)");
	}
	
	for (var el in tempD) seriesData.push(tempD[el]);
  
	kospiDataTb.addRows(seriesData);

	var options = {
	  	theme: {
	    	chartArea: {margin:0, width: "85%", height: "80%"},
	    },
	   	curveType: 'function',
	    legend: "none",
	    lineWidth: 1,
	    pointSize: 1,
	    hAxis: {
	    	gridlines: {
	      	color:  "none"
	      },
	    	format: "dd일 aa kk시"
	    },
	    vAxis: { 
	      viewWindow:{
		      min: minPr * 0.8,
		      max: maxPr * 1.2
	      }
	    },
	    crosshair: {
	    	color: '#bbb',
	    	opacity: 1,
					trigger: 'both',
	      orientation: 'both'
		},
		tooltip: {
			trigger: "both",
			isHtml : true
		},
		maxRangeSize: 86400000 * 3,
		minRangeSize: 86400000
	};

	if (period === "time") {

	  	if (stockData.length > 20) delete options.pointSize;
	  	options.vAxis.viewWindow = { 
	      min: minPr * 0.8,
	      max: maxPr * 1.2
	    };

	} else {
		options.pointSize = 2;
		options.hAxis.format = "yyyy-MM-dd";

	}

	var chart = new google.visualization.LineChart(document.getElementById("chart_kospi_sub_area"));
	chart.draw(kospiDataTb, options);
	return true;
}

$(document).ready(function() {
	
	$(".combo_list_show_wrap").hide();
	$(".dashboard_trend_isu_sch").hide();

	$(".stock_pop_mini").hide();
	$(".stock_popup").hide();
	$(".sch_popup").hide();
	$(".sch_popup_back").hide();

	$(".dashboard_trend_isu_sch").on("click", function() {
		sf.onDrawSchPop();
	});

	$(".title_main_close").on("click", function() {
		sf.onCloseSchPop();
	});

	$(".stock_popup_close").on("click", function() {
		sf.onCloseStkPop();
	});

	$(".sch_ctrl_box").on("click", function() {

		var d = $(this).attr("data");
		if (!d) return false;

		var frmDate = $("input[name=sch_from]").val();
		var toDate  = moment(new Date());

		if (d === "day" ) {
			frmDate = toDate.subtract(1, "days").format("YYYY-MM-DD");
		} else if (d === "7day") {
			frmDate = toDate.subtract(7, "days").format("YYYY-MM-DD");;
		} else if (d === "month") {
			frmDate = toDate.subtract(1, "months").format("YYYY-MM-DD");;
		} else if (d === "3month") {
			frmDate = toDate.subtract(3, "months").format("YYYY-MM-DD");;
		}
		$(".flatpickr.sch_pickr_frm").get(0)._flatpickr.setDate(frmDate);
		$(".flatpickr.sch_pickr_to").get(0)._flatpickr.setDate(moment(new Date).format("YYYY-MM-DD"));
		
		return true;

	});

	//종목검색차트
	$(".sch_popup_btn").on("click", function() {
		var inpText = $(".sch_inp input").val();
		inpText = inpText.replace(/\s+/gi, "");

		if (!inpText) {
			alert("검색 종목을 입력해주세요");
			return false;
		}

		var frmDate = $("input[name=sch_from]").val();
		var toDate  = $("input[name=sch_to]").val();
		var param   = {
			kwd: inpText,
			startDate: frmDate, //frmDate,
			endDate: toDate, //toDate,
			period: $(".sch_ctrl_sel select").val()
		}
		
		if (param.period === "time") {
			param.startDate += " 00:00:00";
			param.endDate   += " 23:59:59";
		}

		$(".sch_pop_chart_title").html("");
		$(".sch_pop_chart").html("");
		$("body").loading();

		$.ajax({
			type: "post",
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			url: SDII.Url + "/report/get_stock_info",
			data: JSON.stringify(param),

			success: function (data) {

				$("body").loading("stop");

		    	if (!data.isSuccess) {
			        alert("데이터 요청에 실패하였습니다.");
			        return false;
		    	}
	
		    	if (!data.data || data.data.length === 0) {
			      	alert('조회되는 데이터가 없습니다.');
			      	$(".sch_pop_chart_title").html("조회되는 데이터가 없습니다.");
			      	return false;
		    	}
	
		    	var d     = data.data, 
	      			tempD = [];
	
		    	var stockDataTb = new google.visualization.DataTable();
			    stockDataTb.addColumn("datetime", "Date");
			    stockDataTb.addColumn("number",  "count");
			    stockDataTb.addColumn({
					type: "string",
		    		role: "tooltip",
		    		p: {html: true}
		    	});
	
			    var stockData = [];
			    var maxPr     = 0;
			    var minPr     = 100000000;
			    for (var i = 0; i < d.length; i++) {
			    	var tStamp = moment(d[i].isuCurDate).format("YYYY-MM-DD kk:mm");
			    	var sStamp = tStamp.split(" ");
			    	var dates  = sStamp[0].split("-");
			    	var times  = sStamp[1].split(":");
			    	var fDate  = new Date(dates[0], parseInt(dates[1]) - 1, dates[2] ,times[0], times[1], 0);

			    	if (!d[i].curPr || d[i].curPr <= 0) continue;

			    	var updnColor = d[i].flucTpCd === 1 ? "up" : "down";
			    	var updnShape = d[i].flucTpCd === 1 ? "▲" : "▼";

			    	if (d[i].prvDdCmpr === 0) {
			    		updnShape = " -&nbsp;";
			    		updnColor = "";
			    	}

			    	var tooltipText = "" +
				    	"<div class='sch_tooltip_wrap'>" + 
				    		"<div class='sch_tooltip_isunm'>" + d[i].isuNm + "</div>" + 
				    		"<div class='sch_tooltip_date'>" + tStamp + "</div>" + 
				    		"<div class='d-flex sch_tooltip_cont'>" + 
				    			"<div class='stock_price'>" + sf.addComma(d[i].curPr) + "원</div>" +
				    			"<div class='d-flex stock_updn_wrap'>" +
				    				"<div>(</div>" +
				    				"<div class='stock_updn_"   + updnColor + "'>" + updnShape + "</div>" +
				    				"<div class='stock_value'>" + sf.addComma(d[i].prvDdCmpr) + ")</div>" +
				    			"</div>" +
			    			"</div>" + 
				    	"</div>";
			    	stockData.push([fDate, d[i].curPr, tooltipText]);
			    	if (maxPr < parseInt(d[i].curPr)) maxPr = d[i].curPr;
			    	if (minPr > parseInt(d[i].curPr)) minPr = d[i].curPr;
	      	
			    	if (d.length - 1 === i) $(".sch_pop_chart_title").text("(" + tStamp + " 기준)");
			    }
	
			    stockDataTb.addRows(stockData);
	
		        var options = {
		        	theme: {
		        		chartArea: {margin:0, width: "85%", height: "80%"},
				    },
		         	curveType: 'function',
		         	legend: "none",
		         	lineWidth: 1,
		         	pointSize: 1,
				    hAxis: {
				    	gridlines: {
				      	color:  "none"
				      },
				    	format: "dd일 aa kk시"
				    },
				    vAxis: { 
				    	viewWindow:{
				    		min: minPr * 0.95,
				    		max: maxPr * 1.05
				      }
				    },
				    crosshair: {
			    		color: '#bbb',
			    		opacity: 1,
		 				trigger: 'both',
		 				orientation: 'both'
					},
					tooltip: {
						trigger: "both",
						isHtml : true
					},
					//maxRangeSize: 86400000 * 3,
					//minRangeSize: 86400000
		        };
		
		        if (param.period === "time") {
		        	if (stockData.length > 20) delete options.pointSize;
		        	options.vAxis.viewWindow = { 
					      min: minPr * 0.99,
					      max: maxPr * 1.005
				    };
		        } else {
		        	options.pointSize = 2;
		      		options.hAxis.format = "yyyy-MM-dd";
		        }
	
	        	var chart = new google.visualization.LineChart(document.getElementById("sch_pop_chart"));
	        	chart.draw(stockDataTb, options);
	        	return true;
		    },

		    error: function (request, status, error) {
		    	$("body").loading("stop");
			    console.log('code: '+request.status+"\n"+'message: '+request.responseText+"\n"+'error: '+error);
			    alert("데이터 요청에 실패하였습니다..");
			    return false;
		  	}
		});
		return true;
	}); // end of sch_popup_btn
});

})();
