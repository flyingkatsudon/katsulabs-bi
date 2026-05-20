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

// -- 공통 함수 설정 시작 -- 

sf.addComma = function(str) {
  if (!str) return "0";
  str = str + "";
  return str.replace(/[^0-9]/g,"").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

sf.clearTrend = function() {
	$("#chart_trend").html("Trend Chart 데이터를 찾을 수 없습니다.");
	$(".chart_trend_combo_wrap").hide();
	$("#chart_trend_combo").html("");
	$(".chart_trend_tooltip").hide();
}

sf.clearNetwork = function() {
	$("#chart_network_title").text("Trend Chart 데이터가 조회되지 않습니다.");
	$("#chart_network_chart").html("");
}

// Category ID 값에 맞는 카테고리 한글명 매핑
sf.getTrendChartName   = function() {
	var $prefix = "NEWS";
	if      (sv.category === "2") $prefix = "SNS";
	else if (sv.category === "4") $prefix = "시장";
	else if (sv.category === "5") $prefix = "종목";
	else if (sv.category === "6") $prefix = "상품";
	else if (sv.category === "7") $prefix = "건강";
	else if (sv.category === "8") $prefix = "질병";
	return $prefix;
}

// Loading Text
sf.getTrendLoadingText = function() {
	return sf.getTrendChartName() + " Trend 조회중 입니다.";
}

// Ajax 실패 시 공통 처리 
sf.onErrorComm = function(request, status, error) {
	$("body").loading("stop");
	$(".chart_trend_tooltip").hide();
  console.log("code: " + request.status + "\nerror: " + error);
  alert("데이터 요청에 실패하였습니다..");
}

// 요약 스크립트 Top5 요청 함수
sf.onReqAbsScrpt = function() {
	$.ajax({
  	url: SDII.Url + "/report/get_abs_src",
    type: "post",
		contentType: 'application/json; charset=utf-8',
		dataType: "JSON",
    data: JSON.stringify({
    	businessCode: sv.groupCd,
    	fid : sv.reqAbsCds,
    	date: $(".flatpickr")[1]._flatpickr.input.value,
    	cnt : 5
    }),
   	success: function (data) {
      if (!data.isSuccess) {
        alert("데이터 요청에 실패하였습니다.");
        return false;
      }
      sf.onDrawCard(data);
      return true;
    },
    error: function (request, status, error) {
    	console.log('code: '+request.status+"\n"+'message: '+request.responseText+"\n"+'error: '+error);
	    alert("데이터 요청에 실패하였습니다..");
	    return false;
  	}
	});
}

// Top5 리스트 그리는 함수
sf.onDrawCard = function (res) {

	$("div[card-data-type='1']").html("데이터가 없습니다.");
	$("div[card-data-type='2']").html("데이터가 없습니다.");
	$("div[card-data-type='4']").html("데이터가 없습니다.");
	$("div[card-data-type='5']").html("데이터가 없습니다.");
	$("div[card-data-type='6']").html("데이터가 없습니다.");
	$("div[card-data-type='7']").html("데이터가 없습니다.");
	$("div[card-data-type='8']").html("데이터가 없습니다.");

	if (!res.isSuccess || res.data.length === 0) {
		if (sv.category === "5") { // 종목 브리핑인경우 별도 메세지
			alert("종목이 존재하지않아 트렌드차트 조회가 되지 않습니다.");
			sf.clearTrend();
			$("body").loading("stop");
		}
		return false;
	}

	var d = res.data,
		  c = {};

	for (var i = 0; i < d.length; i++) {

		if (!sv.has_cate5_data && d[i].cId === "5")
			sv.has_cate5_data = true;

		//var cateId = d[i].categoryCode;
		var cateId = d[i].cId;
		if (!cateId) continue;

		var parent = $("div[card-data-type=" + cateId + "]");
		if (parent.length === 0) continue;

		if (!c[cateId]) {
			parent.html("");
			c[cateId] = 0;
		}
		if (c[cateId] === 5) continue;

		var cursrc = d[i].scriptTitle || "";

		if (!cursrc) {
			parent.append("데이터가 없습니다.")
			continue;
		}
		if (cursrc.length > 13) cursrc = d[i].scriptTitle.substr(0, 12) + "...";

		var $hoverCont = d[i].scriptLabelL2 ? d[i].scriptLabelL2 : d[i].scriptTitle;
		var $a_tag = $("<span class='content_num'>" +  d[i].cnt + " </span><a class='src_title'>" + cursrc + "</a>");
		$a_tag.attr("data-container", "body")
		  .attr("data-trigger", "hover")
			.attr("data-toggle", "popover")
			.attr("data-placement", "right")
			.attr("data-cid", d[i].cId)
			.attr("data-content", $hoverCont)
			.attr("data-ori", d[i].scriptTitle)
			.attr("uuid", d[i].scriptUid);

		//if (d[i].scriptLabelL2) $a_tag.popover();
		$a_tag.popover();
		
		c[cateId] += 1; 
		$a_tag.on("click", function() {

			if (!$(this).attr("uuid")) return false;

			$(".docs_smmry_wrap").css("height", ($(".side_container").height() - 20) + "px");
			$(".control_bar_show_wrap").slideUp();

			$(".news_wrap").hide();
			$(".docs_smmry_wrap").show();
			$(".docs_extv_result").html("'" + $(this).attr("data-ori") + "'에 대해 조회중 입니다. 잠시만 기다려주세요.");
			$(".docs_extv_cont").html("");

			$.ajax({
				url: SDII.Url + "/report/get_smmry_extv",
			    type: "post",
	        	contentType: 'application/json; charset=utf-8',
				dataType: "JSON",
		    	data: JSON.stringify({
		    	  	fid: sv.reqMap[$(this).attr('data-cid')].smmry,
			    	scriptUid: $(this).attr("uuid"),
			    	date: $(".flatpickr")[1]._flatpickr.input.value,
		    		cnt : 999
		    	}),
		   	success: function (data) {

		   		console.log(data);
		   		$(document).scrollTop(70);

		      if (!data.isSuccess) {
		        alert("데이터 요청에 실패하였습니다.");
		        return false;
		      }
		      
		      if (!data.data || data.data.length === 0) {
		      	$(".docs_extv_cont").html("<div>조회된 결과가 없습니다.</div>");
		      	return false;
		      }

		      $(".docs_extv_cont").html("");
		      $(".docs_smmry_wrap").scrollTop(0);
		      $(".docs_extv_result").text("검색결과 : " + data.data.length + "건 / 조회기준일 : " + $(".flatpickr")[1]._flatpickr.input.value);

		      var d = data.data;
		      for (var i = 0; i < d.length; i++) {
		      	
		      	var $title = $("<div class='smmry_title smmry_link' url='" + d[i].relDocUid + "'>" + d[i].scriptTitle	+ "</div>");
		      	var $smmry = $("<div class='smmry_cont'>" + d[i].relDocSummary	+ "</div>");
		      	var $row   = $("<div class='smmry_row_wrap'></div>");

		      	$row.append($title).append($smmry);//.append($url);
		      	$(".docs_extv_cont").append($row);
		      }

		      $(".smmry_link").on("click", function() {
		      	window.open($(this).attr("url"), "", "width=1200,height=800");
		      });
		      
		      return true;
		    },
		    error: function (request, status, error) {
		    	console.log('code: '+request.status+"\n"+'message: '+request.responseText+"\n"+'error: '+error);
			    alert("요약 스크립트 호출에 실패하였습니다.");
			    return false;
		  	}
			});
			
			return true;
		});

		if (cateId !== "5") {
			parent.append($a_tag).append("<div class='content_bdr'></div>");	
		} else {
			var wrapDiv = $("<div class='d-flex justify-content-between link_wrap'></div>");
			var rankDiv = $("<div class='rank_wrap'></div>");
			var popDiv  = $("<div class='pop_wrap' isunum='" + d[i].scriptUid + "' isunm='" + cursrc + "'>+</div>");
			rankDiv.append($a_tag);
			wrapDiv.append(rankDiv).append(popDiv);
			parent.append(wrapDiv).append("<div class='content_bdr'></div>");	
		}
	}
	$(".pop_wrap").off();
	$(".pop_wrap").on("click", function() {

		var isuNm =  $(this).attr("isunm");
		var isuNum = $(this).attr("isunum");

		if (!isuNm || !isuNum) return false;

		$(".stock_mini_list").attr("isunm",  isuNm);
		$(".stock_mini_list").attr("isunum", isuNum);

		$(".stock_pop_mini_title").text(isuNm);
		$(".stock_pop_mini").css("top", event.pageY + "px").css("left", event.pageX + "px").show();
		
		return true;
	});

	$(".stock_pop_mini_close").off();
	$(".stock_pop_mini_close").on("click", function() {
		$(".stock_pop_mini").hide();
		return true;
	});
	
	$(".btn_stock").off();
	$(".btn_stock").on("click", function() {
		
		$(".control_bar_show_wrap").slideDown();
		$(".chart_trend_sub_close").click();
		$(".docs_smmry_wrap").attr("isOpen", 0).hide();
		$(".news_wrap").attr("isOpen", 1).slideDown("fast");
		$(".chart_doc_list").html("");
		
		sv.category = "5";
		sv.cmpyList = [];
		//sv.period   = "day";

		$(".dashboard_trend_title").text(sf.getTrendChartName() + " 브리핑");
		$("#chart_trend").html(sf.getTrendLoadingText());
		$("#ctrl_trend").html("");

		$(".chart_network_upper_wrap").show();
		$(".chart_kospi_sub_wrap").hide();

		$(".dashboard_trend_isu_sch").show();
		
		$(".chart_trend_menu").hide();
		
		var fn = $(this).attr("func-num");
		
		if (!fn) return false;

		var isuNm  = $(this).parent().attr("isunm");
		var isuNum = $(this).parent().attr("isunum");
		
		$(".stock_pop_mini_close").click();
		
		if (fn === "0") {
			sf.onDrawTrendChart(isuNm);
			return false;
		}

		sf.onDrawStkPop();
		
		if (fn === "1") {
			sf.onDrawExpertOpinion(isuNm);
		} else if (fn === "2") {
			sf.onDrawBoardLink(isuNm, isuNum);
		} else {
			sf.onDrawDisclosure(isuNm);
		}
		
		return true;
	});
	
	if (sv.category === "5") sf.onDrawTrendChart();
	
	return true;
}

// 트렌드 차트 요청 공통 
sf.onDrawTrendChart = function(schKwd) {

	$("body").loading();

	sf.clearNetwork();

	var $trendParam = {
	  	businessCode: sv.groupCd,
	  	period      : sv.period,
	  	fid         : sv.reqMap[sv.category].trend,
	  	startDate   : $(".flatpickr input[name='date_from']").val(),
	  	endDate     : $(".flatpickr input[name='date_to']").val()
	};
	
	if (sv.category === "5" || sv.category === "8"){
  		if (sv.category === "5") 
  			$trendParam.kwdA = $(".rank_wrap a:first").text();

  		if (schKwd) $trendParam.kwdA = schKwd;
  		
  		if ( sv.category === "8" && !schKwd) $trendParam.nerInfoA = "질병/증상";
  	}

	$.ajax({
    type: "post",
  	contentType: 'application/json; charset=utf-8',
		dataType: "JSON",
    url: SDII.Url + "/report/get_kwd_trd_v2",
    data: JSON.stringify($trendParam),
    success: function (data) {
      if (!data.isSuccess) {
        alert("데이터 요청에 실패하였습니다.");
        $("body").loading("stop");
        return false;
      }
      $(".chart_trend_tooltip").show();
      sv.curTrendChart = new SDII.Chart.trendChart().setData(data).onDrawChart();
      
      return true;
    },
    error: sf.onErrorComm
	});
}

// 기간별 연관어 Top 10
sf.onRequestKwdAssoV2 = function(kwd) {

	if (kwd && typeof kwd === "object") kwd = kwd.text;
	
	if (!kwd) {
		alert("조회될 키워드가 없습니다.");
		$(".chart_trend_sub_wrap").slideUp("fast");
		$("#chart_trend_sub").html("");
		return false;
	}

	$("body").loading();

	var to = $(".flatpickr")[1]._flatpickr.input.value;

	$(".chart_trend_sub_wrap").slideDown("fast");
	$("#chart_trend_sub").html("<br/>&nbsp;연관어 기간별 추이를 조회중 입니다.");

	var newParam = {
     kwd: kwd,
     startDate: moment(to).subtract(45, "days").format("YYYY-MM-DD"),
     endDate: to,
     period: "week",
     cnt: "10",
     businessCode: sv.groupCd,
     fid: sv.reqMap[sv.category].assoKwd
  };

	$.ajax({
		url: SDII.Url + "/cus/get_kwd_asso_v2",
		type: "POST",
		contentType: "application/json; charset=utf-8",
		dataType: "JSON",
		data:  JSON.stringify(newParam),
		success: function(kwdAssoTimeResult) {
			$("body").loading("stop");

		  	if (!kwdAssoTimeResult.isSuccess) {
		  		alert("데이터 요청에 실패하였습니다.");
		  		return false;
		  	}
	
		  	if (!kwdAssoTimeResult.data || kwdAssoTimeResult.data.length === 0) {
					$("#chart_trend_sub").html("");
					$(".chart_trend_sub_wrap").slideUp("fast");
					alert("[" + kwd + "]에 대한 연관어 검색 결과를 찾을 수 없습니다.");
		  		return false;
		  	}
	
		  	$("#chart_trend_sub").html(
		  		"<div class='chart_trend_sub_title'>[" + kwd + "] 연관어 기간별 Top " + (newParam.cnt) + "</div>" +
		  		"<div class='chart_trend_sub_container'></div>"
	  		);
		  	new SDII.Chart.kwdAssoTimeTable(newParam.cnt, $(".chart_trend_sub_container")).setData(kwdAssoTimeResult).onDrawChart();
		  	return true;
		}
	});
	return true;
}
window.onRequestKwdAssoV2 = sf.onRequestKwdAssoV2;

// 감정 연관어 Top 10
sf.onReqPosNegStat = function(kwd) {

	//console.log(kwd);
	if (!kwd || kwd.split(",").length !== 3) {
		return false;
	}

	$("body").loading();

	var sKwd = kwd.split(",");

	$.ajax({
		url: SDII.Url + "/report/get_emo_asso",
		type: "post",
		contentType: 'application/json; charset=utf-8',
		dataType: "JSON",
		data: JSON.stringify({
		  	period: sv.period,
		  	kwdA: sKwd[0],
		  	kwdB: sKwd[1],
		  	date: sKwd[2],
		  	fid: sv.reqMap[sv.category].posneg,
			cnt : 10
		}),
   		success: function (data) {

	   		$("body").loading("stop");
	
	   		if (!data.isSuccess) {
	   			alert("데이터 요청에 실패하였습니다.");
				return false;
	   		}
	
	   		if (data.data.length === 0) {
	   			alert(sKwd[0] + "/" + sKwd[1] + "에 대한 긍부정 연관어가 조회되지 않습니다.");
	   			$(".chart_trend_sub_wrap").slideUp("fast");
	   			return false;
	   		}
	   		//console.log(data);
	
			$(".chart_trend_sub_wrap").slideDown("fast");
			$("#chart_trend_sub").html("<div class='chart_trend_sub_title'>" + sKwd[0] + "/" + sKwd[1] + "에 대한 긍부정 연관어 조회중입니다.</div>");
	
			new SDII.Chart.posNegAssoChart(10, $("#chart_trend_sub"), kwd).setData(data).onDrawChart();
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
}
window.onReqPosNegStat = sf.onReqPosNegStat;

// 키워드네트워크
sf.onRequestKwdNetwork = function(top3Kwd, date, cName) {

	$("body").loading();

	$(".chart_doc_list").html("<div class='doc_search_title'>좌측 키워드 네트워크 차트에서 키워드를 선택 해주세요.</div>");

	$.ajax({
    type: "post",
  	contentType: 'application/json; charset=utf-8',
		dataType: "JSON",
    url: SDII.Url + "/report/get_kwd_asso",
    data: JSON.stringify({
    	businessCode : sv.groupCd,
    	fid: sv.reqMap[sv.category].kwd,
    	kwd: top3Kwd, //"서울,은행장,태승",
    	period: sv.period,
    	cnt: "10",
    	date: date
    }),

    success: function (cName, top3Kwd, data) {

      if (!data.isSuccess) {
        alert("데이터 요청에 실패하였습니다.");
        return false;
      }

      if (data.data.length === 0) {
    	var relTitle = top3Kwd;
    	if (sv.category === "5") relTitle = cName + "/" + relTitle;
      	$("#chart_network_title").text("'" + relTitle + "'에 대한 연관어를 찾을 수 없습니다.");
      	$("#chart_network_chart").html("");
      	$("body").loading("stop");
      	return false;
      }
      new SDII.Chart.kwdNetwork().setData(data, cName, top3Kwd).onDrawChart();
      return true;
    }.bind(this, cName, top3Kwd),
    error: sf.onErrorComm
	});
	return true;
}
window.onRequestKwdNetwork = sf.onRequestKwdNetwork;

sf.channelAction = function(t, p, d) {

	$("body").loading();

	$(".chart_trend_sub_wrap").slideDown();

	//$("#chart_trend_sub").html("<div class='chart_trend_sub_title'>" + p + " " + t + "에 대한 채널별 확산 조회중입니다.</div>");
	$("#chart_trend_sub").html("<div class='chart_trend_sub_title'>" + p + "에 대한 채널별 확산 조회중입니다.</div>");

	var $frmDate = moment(d);
	var $toDate  = moment($("input[name=date_to]").val());
	var $gapDate = ($toDate - $frmDate) / 1000;

	if ($gapDate < 604800)
		$frmDate = $toDate.clone().subtract(7, "days");

	$.ajax({

		url: SDII.Url + "/report/get_trd_v2",
		type: 'post',
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data: JSON.stringify({
			kwdA: p,
			kwdB: t,
			startDate: $frmDate.format("YYYY-MM-DD"),
			endDate: $toDate.format("YYYY-MM-DD"),
    	period: "day",
    	fid   : sv.reqMap[sv.category].chnl,
    	businessCode: sv.groupCd
		}),
    	success: function(chnlResult) {

	    	$("#chart_trend_sub").html("");
	
	    	var isSuccess = true;
	
	    	if (!chnlResult.isSuccess) {
	    		alert("데이터 요청에 실패하였습니다.");
	    		isSuccess = false;
	    	}
	
	    	if (!chnlResult.data || chnlResult.data.length === 0) {
	    		//if (isSuccess) alert("[" + p + " " + t + "] 에 대한 채널별 검색 결과가 존재하지 않습니다.");
	    		if (isSuccess) alert("[" + p + "] 에 대한 채널별 검색 결과가 존재하지 않습니다.");
	    		$(".chart_trend_sub_wrap").slideUp("fast");
	    		$("body").loading("stop");
	    		return false;
	    	}
	    	//new SDII.Chart.chnlChart().setData(chnlResult).onDrawChart();
    	
			var fmt = "YYYY-MM-dd", i = 0;

			//if (SDII.Globals.Vars.period === "mon") fmt = "YYYY-MM"

			var d = chnlResult.data;

			var chartOpts = {
				//selectionMode: "multiple",
				aggregationTarget: "category",
				lineWidth: 1,
				pointSize: 2,
				pointShape: 'circle',
		  		theme: {
		  			chartArea: {margin:0, width: "90%", height: "80%"},
		  		},
		      	animation:{
			      	startup: true,
			        duration: 1000,
			        easing: 'inAndOut',
		      	},
			    //title: "none",
			    titleTextStyle: {
			    	fontSize: 20,
			    	color: "rgba(255,255,0,0.5)",
			    },
					tooltip: {
						trigger: "selection"
					},
			    curveType: 'function',
			    legend: {
			    	position: "top",
			    	alignment: "end"
			    },
			    hAxis: {
			    	 gridlines: {
			      	color:  "none"
			      },
			    	format: fmt
			    },
			    vAxis: { 
			      //viewWindowMode:'maximized',
			      //textPosition: "none",
			      viewWindow:{
			      	//max:55,
			        min: 0
			      }
			    },
		
			    series: {
			      0: { color: "#ef65a2", lineDashStyle: [0]},//lineDashStyle: [1, 1] },
			      1: { color: "#a377fe", lineDashStyle: [0] },
			      2: { color: "#d0a45f", lineDashStyle: [0] },
			      3: { color: "#65cfc2", lineDashStyle: [0] }
					},
					
			    crosshair: {
		    		color: '#bbb',
		    		opacity: 1,
	 				trigger: 'both',
	 				orientation: 'both'
				}
			};
			
			var n = {}; var maxCnt = 10;
		
			for (i = 0; i < d.length; i++) {
		
				var curDate = moment.unix(d[i].docDate / 1000).format("YYYY-MM-DD");
				d[i].d = curDate;
		
				if (!n[curDate]) {
					n[curDate] = [new Date(d[i].docDate), 0, 0, 0];
				}
				n[curDate][(chnlResult.cnames.indexOf(d[i].cId)) + 1] = d[i].docCntBoth;
		
				maxCnt = maxCnt < d[i].docCntBoth ? d[i].docCntBoth : maxCnt;
			}
		
			chartOpts.vAxis.viewWindow.max = maxCnt * 1.2;
		
			var r = [], rCnt = 0;
			for (var el in n) r[rCnt++] = n[el];
			
			var cIds = chnlResult.cnames;
		
			var chnData = new google.visualization.DataTable();
			chnData.addColumn("date", "date");
			for (i = 0; i < cIds.length; i++) {
				chnData.addColumn("number", cIds[i]);	
			}
		    chnData.addRows(r);
		
		    $("#chart_trend_sub").html("");
				$("#chart_trend_sub").append("<div class='chart_trend_sub_title'>채널별 검색키워드 : " + p + "</div>");
				$("#chart_trend_sub").append("<div id='chart_trend_sub_area'></div>");
		
		    var chart = new google.visualization.LineChart(document.getElementById('chart_trend_sub_area'));
		    chart.draw(chnData, chartOpts);
		
		    $("body").loading("stop");
		    return true;
		},

	    error: function (request, status, error) {
	    	$("body").loading("stop");
	    }
	});
	return true;
	//alert("Click Keyword : " + $(t).text());
}
window.channelAction = sf.channelAction;

window.timer = null;
$(document).ready(function() {
	
	$("body").on("mousemove keydown", function() {
		window.lastTime = new Date();
	});
	
	//checkSession();

	function checkSession () {
		$.ajax({
			url: '/bdp/sm/checksn',
			type : 'POST',
        	contentType: 'application/json; charset=utf-8',
			//data: JSON.stringify({userId: userId}),
			success: function(res) {
				//var res = JSON.parse(response);		
				if (res.status) return false;
				var prefix = "";
				if (res.loginIp) prefix = res.loginIp + "에서 ";
				
				alert(prefix + "중복 로그인 되어 로그아웃 됩니다.");
				location.href = "/bdp/logout";
			}, 
			error: function(res) {
				alert("세션 검증에 실패하여 로그아웃 됩니다.");
				window.location.href = '/bdp/logout';
			},
			complete: function(){
				if (window.timer) clearInterval(window.timer);
				window.timer = setInterval(checkSession, 1000);
			}
		});
	}
	
	window.lastTime = new Date();
	checkTime();

	function checkTime() {
		
		if (window.actTimer) clearInterval(window.actTimer);
		var curTime = new Date();
		var subtrs  = Math.ceil((curTime - lastTime) / 1000);
		
		if (subtrs > 1800) {
			alert('30분 이상 응답이 없어 자동 로그아웃 됩니다');
			location.href = "/bdp/logout";
			return false;
		}
		window.actTimer = setInterval(checkTime, 1000);
	}

	if (!$("body").loading) {
		location.href = location.href;
		return false;
	}

	$("body").loading({ message: '데이터 조회중 입니다'});
	$(".flatpickr").flatpickr({locale : "ko", wrap: true});

	// 구글 Lib 로드
	google.charts.load('current', {'packages': ['corechart', 'line', 'controls']});
	// 구글 LIB 로딩완료 시 뉴스브리핑 트랜드 차트그려지도록 - html 파일에 category에 매칭되는 reqMap의 trend.
	google.charts.setOnLoadCallback(SDII.Globals.Func.onDrawTrendChart);

	//채널별 확산 영역 감추기
	$(".chart_trend_sub_wrap").hide();
	//원문요약 리스트 영역 감추기
	$(".docs_smmry_wrap").hide();
	// 툴팁 영역 감추기 
	$(".chart_trend_tooltip").hide();
	// 코스피/코스닥 추이 영역 감추기 (GS)
	$(".chart_kospi_sub_wrap").hide();

	//조회기준 현재일 - 1달전 셋업
	var $fromDate = moment(new Date()).subtract(1, "months").format("YYYY-MM-DD");
	$(".flatpickr")[0]._flatpickr.setDate($fromDate);
	$(".flatpickr")[1]._flatpickr.setDate(new Date());
	
	// 테스트용 임시
	$(".flatpickr")[0]._flatpickr.setDate(new Date(2019, 0, 1));
	$(".flatpickr")[1]._flatpickr.setDate(new Date(2019, 0, 15));
	// -- 화면 버튼 이벤트 설정 시작 -- 

	// 왼쪽 카드메뉴 접기 버튼 
	$(".side_container_btn").on("click", function() {

		var isOpen = $(".side_container").attr("isOpen");

		if (isOpen === "1") {
			$(".side_container").animate({width: "toggle", opacity: 0}, 200).delay(200);		
			$(".side_container").attr("isOpen", "0");
			$(this).text(">");
		} else {
			$(".side_container").animate({width: "toggle", opacity: 1}, 200).delay(200);		
			$(".side_container").attr("isOpen", "1");
			$(this).text("<");
		}
	});

	// 상단 4社명 클릭 시 해당 Insight Report로 이동 기능
	$(".navibar_group_title").on("click", function() {
		var $tgt = $(this).attr("link-target");
		if (!$tgt) return false;
		location.href = pathUrl + "/" + $tgt + ".html";
		return true;
	});

	// 상단 다음레포트 가기 버튼 (돋보기)
	$(".trend_report_btn").on("click", function() {
		var from = $(".flatpickr input[name='date_from']").val();
		var to   = $(".flatpickr input[name='date_to']").val();
		location.href = docBase + "/report/cus.html?from=" + from + "&to=" + to;
		return true;
	});

	// 조회 단위 구간 설정 (일/주/월/분기/년)
	$(".range_wrap .btn_range").on("click", function() {
		$(this).parent().find("div").removeClass("btn_range_click");
		$(this).addClass("btn_range_click");

		var $curVal = $(this).attr("data-value");

		if ($curVal === "day")          sv.period = "day";
		else if ($curVal === "week")    sv.period = "week";
		else if ($curVal === "mon")     sv.period = "mon";
		else if ($curVal === "quarter") sv.period = "quarter";
		else if ($curVal === "year")    sv.period = "year";

		//console.log("Selected Peried : " + sv.period);
	});

	// 채널별 확산 영역 닫기버튼
	$(".chart_trend_sub_close").on("click", function() {
		$(this).parent().slideUp();
	});
  
	// Cboard 이동 버튼
	$(".navibar_menu").on("click", function() {
		if (isDev) {
			alert("일반분석환경 이동버튼은 /bdp 에서만 제공됩니다.");
			return false;
		}
		location.href = "/bdp/cboard/starter.jsp";
		return true;
	});

	// 왼쪽 메뉴 카테고리명 클릭 시 이벤트
	$(".side_card .title").on("click", function() {

		var cateNum = $(this).attr("card-cate-value");
		sv.category = cateNum;

		$("body").loading();

		$(".control_bar_show_wrap").slideDown();
		$(".chart_trend_sub_close").click();
		$(".docs_smmry_wrap").attr("isOpen", 0).hide();
		$(".news_wrap").attr("isOpen", 1).slideDown("fast");
		$(".chart_doc_list").html("");
		
		$(".dashboard_trend_title").text(sf.getTrendChartName() + " 브리핑");
		$("#chart_trend").html(sf.getTrendLoadingText());
		$("#ctrl_trend").html("");

		if (sv.category === "4") { // 시장브리핑은 연관어 네트워크 /원문검색 없음
			$(".chart_network_upper_wrap").hide();
			$(".chart_kospi_sub_wrap").show();
			sf.onReqKospiChart(); // report_gs.js
		} else {
			$(".chart_network_upper_wrap").show();
			$(".chart_kospi_sub_wrap").hide();
		}

		if (cateNum === "5") {	// 종목브리핑은 종목검색버튼 보이기
			$(".dashboard_trend_isu_sch").show();
		} else {
			$(".dashboard_trend_isu_sch").hide();	
		}

		// 생명사/손보사별 보기 Select box
		$(".chart_trend_menu").hide();
		
		//$("div[data-value='day'").click();
		//sv.period   = "day";
		sv.cmpyList = [];
		sv.category = cateNum;
		sf.onDrawTrendChart();
	});

	$(".control_search_btn").on("click", function() {

		$(".docs_smmry_wrap").hide();
		$(".chart_doc_list").html("");
		$(".news_wrap").show();

		$("#chart_trend").html(sf.getTrendLoadingText());
		$("#ctrl_trend").html("");

		if (sv.category === "4") {
			$(".chart_kospi_sub_wrap").show();
			sf.onReqKospiChart(); // report_gs.js
		} else {
			$(".chart_kospi_sub_wrap").hide();
		}
		
		sf.onReqAbsScrpt();
		if (sv.category !== "5") sf.onDrawTrendChart();
		return true;
	});

	//요약 스크립트 호출
	sf.onReqAbsScrpt();
	//트랜드 차트 호출 (구글 Lib 로딩완료 시 호출, 상단에 코드 있음)
	//google.charts.setOnLoadCallback(SDII.Globals.Func.onDrawTrendChart);
	return true;
});

})();
