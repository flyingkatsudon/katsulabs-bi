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

	window.channelAction = function(t, p, d) {

		if (!sv.searchEnable) {
			return;
		}

		$(".chart_trend_sub_wrap").slideDown();

		$("#chart_trend_sub").html("<div class='chart_trend_sub_title'>" + p + " " + t + "에 대한 채널별 확산 조회중입니다.</div>");

		sv.searchEnable = false;

		$frmDate = moment(d);
		$toDate  = moment($("input[name=date_to]").val());
		$gapDate = ($toDate - $frmDate) / 1000;

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
	    	period: "day"
	    }),
	    success: function(trendResult) {

	    	$("#chart_trend_sub").html("");
	    	if (!trendResult.isSuccess) {

	    		$(".chart_trend_sub_wrap").slideUp("fast");
	    		$("#chart_trend_sub").html("");
	    		alert("데이터 요청에 실패하였습니다.");
	    		return;
	    	}
	    	
	    	if (!trendResult.data || trendResult.data.length == 0) {
	    		alert(p + " " + t + "에 대한 채널별 검색 결과가 존재하지 않습니다.");
	    		$(".chart_trend_sub_wrap").slideUp("fast");
	    		$("#chart_trend_sub").html("");
	    		sv.searchEnable = true;
	    		return;
	    	}
	    	

				var fmt = "YYYY-MM-dd";

				//if (SDII.Globals.Vars.period == "mon") fmt = "YYYY-MM"

				var d = trendResult.data;

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
			      0: { color: "#3366CC", lineDashStyle: [0]},//lineDashStyle: [1, 1] },
			      1: { color: "#DC3912", lineDashStyle: [0] },
			      2: { color: "#FF9900", lineDashStyle: [0] },
			      3: { color: "#109618", lineDashStyle: [0] }
			      /*
			      0: { color: "#00B3F9", lineDashStyle: [1, 1]},//lineDashStyle: [1, 1] },
			      1: { color: "#91D03C", lineDashStyle: [1, 1] },
			      2: { color: "#FF5663", lineDashStyle: [1, 1] },
			      3: { color: "#00D2ED", lineDashStyle: [1, 1] }
			      */
					},
					/*
			    crosshair: {
			    	color: '#bbb',
			    	opacity: 1,
		 				trigger: 'both',
		        orientation: 'both'
					}
					*/
				};
	    	
	    	var n = {}; var maxCnt = 10;

	    	for (var i = 0; i < d.length; i++) {

					var curDate = moment.unix(d[i].docDate / 1000).format(fmt);
					d[i].d = curDate;

					if (!n[curDate]) {
						n[curDate] = [new Date(moment.unix(d[i].docDate / 1000)), 0, 0, 0];
					}

					n[curDate][trendResult.cnames.indexOf(d[i].cId) + 1] = d[i].docCntBoth;

					maxCnt = maxCnt < d[i].docCntBoth ? d[i].docCntBoth : maxCnt;
				}

				chartOpts.vAxis.viewWindow.max = maxCnt * 1.2;

				var r = [], rCnt = 0;
				for (var el in n) r[rCnt++] = n[el];
				
				var cIds = trendResult.cnames;

				var chnData = new google.visualization.DataTable();
				chnData.addColumn("date", "date");
		 		for (var i = 0; i < cIds.length; i++) {
		 			chnData.addColumn("number", cIds[i]);	
		 		}
		    chnData.addRows(r);

		    $("#chart_trend_sub").html("");
				$("#chart_trend_sub").append("<div class='chart_trend_sub_title'>채널별 검색키워드 : " + p + "/" + t + "</div>");
				$("#chart_trend_sub").append("<div id='chart_trend_sub_area'></div>");

		    var chart = new google.visualization.LineChart(document.getElementById('chart_trend_sub_area'));
		    chart.draw(chnData, chartOpts);

		    sv.searchEnable = true;
	    },

	    error: function (request, status, error) {
	    	sv.searchEnable = true;
	    }
		});

		//alert("Click Keyword : " + $(t).text());
	}

$(document).ready(function() {});

})();
