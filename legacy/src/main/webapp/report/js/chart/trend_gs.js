/*
 * trend.js
 * - insight report trend chart comm
 *
 * 
 * 18.09
 *
 * JayKeem
 *
 * jaykeem@weni.co.kr
*/

$(document).ready(function() {

(function(global) {

	var trend = function() {
		this.trendType   = "";
		this.chartData   = {};
		this.emoData     = {};
		this.top3Data    = {};
		this.cpnyNames_o = [];
		this.cpnyNames   = [];
		this.filterNms   = [];
		this.cacheData   = null;
		this.isEmptyData = false;
		this.isCacheData = false;
	}

	trend.prototype = {

		setData: function(ajaxData, isCache) {

			console.log("isCache : " + isCache);

			if (ajaxData) this.cacheData = ajaxData;

			if (!ajaxData && this.cacheData) ajaxData = this.cacheData;
			
			this.isCacheData = isCache;

			var d   = ajaxData.data,
					nms = ajaxData.cnames,
				  n   = {},
				  k   = {},
				  e   = {},
				  rows = [];

			if (ajaxData) this.cacheData = ajaxData;

			if (!sv.cmpyList || sv.cmpyList.length === 0) {
				this.filterNms = nms.slice(0, 5);
				//if (sv.category === "5") this.filterNms = [nms[2]];
			} else {
				this.filterNms = sv.cmpyList;
			}

			if (!ajaxData.data || ajaxData.data.length === 0) {
				//alert("해당기간에 대한 데이터가 조회되지 않습니다.");
				$("body").loading("stop");
				this.isEmptyData = true;
				return this;
			}

			for (var i = 0; i < d.length; i++) {

				var idxPos = this.filterNms.indexOf(d[i].kwdA);
				if (idxPos === -1) {
					//console.log("Invalid Case", d[i]);
					continue;
				}

				var curDate = moment.unix(d[i].docDate / 1000).format("YYYY-MM-DD");
				d[i].d = curDate;

				if (!n[curDate]) {
					n[curDate] = [d[i].docDate];
					k[curDate] = {};
					e[curDate] = {};
					for (var j = 0; j < this.filterNms.length; j++) {
						n[curDate] = n[curDate].concat([0, ""]);
						if (!k[curDate][this.filterNms[j]]) k[curDate][this.filterNms[j]] = [];
						if (!e[curDate][this.filterNms[j]]) e[curDate][this.filterNms[j]] = [["긍정", 0, "0"], ["부정", 0, "0"], ["중립", 0, "0"]];
					}
				}

				// Top1만 동시출현빈도 카운트/ 긍부정 셋업
				if (d[i].rn === 1) {
					n[curDate][idxPos * 2 + 1] = d[i].docCntBoth;
					/*
					var totCnt = parseInt(d[i].negCntBoth) + parseInt(d[i].posCntBoth) + parseInt(d[i].neuCntBoth),
							pPcnt = 0, nPcnt = 0, jPcnt = 0;
					pPcnt = Math.round(d[i].posCntBoth / totCnt * 100);
					nPcnt = Math.round(d[i].negCntBoth / totCnt * 100);
					jPcnt = Math.round(d[i].neuCntBoth / totCnt * 100);

					e[curDate][d[i].kwdA][0][1] = d[i].posCntBoth
					e[curDate][d[i].kwdA][0][2] = pPcnt;
					e[curDate][d[i].kwdA][1][1] = d[i].negCntBoth;
					e[curDate][d[i].kwdA][1][2] = nPcnt;
					e[curDate][d[i].kwdA][2][1] = d[i].neuCntBoth;
					e[curDate][d[i].kwdA][2][2] = jPcnt;
					*/
					e[curDate][d[i].kwdA][0][1] = parseFloat(d[i].posPercent);
					if (d[i].posPercent) e[curDate][d[i].kwdA][0][2] = d[i].posPercent;
					
					e[curDate][d[i].kwdA][1][1] = parseFloat(d[i].negPercent);
					if (d[i].negPercent) e[curDate][d[i].kwdA][1][2] = d[i].negPercent;
					
					e[curDate][d[i].kwdA][2][1] = parseFloat(d[i].neuPercent);
					if (d[i].neuPercent) e[curDate][d[i].kwdA][2][2] = d[i].neuPercent;
				}

				//Top3는 공통으로 넣어주고
				k[curDate][d[i].kwdA].push([d[i].kwdB, d[i].docCntBoth]);
			}

			var nn = {};
			Object.keys(n).sort().forEach(function(key) {	
  			nn[key] = n[key];
			});

			for (var el in nn) rows.push(nn[el]);
			
			this.top3Data    = k;
			this.chartData   = rows;
			this.emoData     = e;
			this.cpnyNames   = this.filterNms;
			this.cpnyNames_o = nms;

			if (rows.length === 1) {
				alert("데이터가 1set만 조회되어 라인차트를 그릴 수 없습니다.");
				this.isEmptyData = true;
			}
			return this;
		},

		onDrawChart: function() {
			
			var that     = this,
				fromDate = $(".flatpickr input[name='date_from']").val(),
				toDate   = $(".flatpickr input[name='date_to']").val(),
				colors   = [
					"#83c4ff","#ff9c00","#ff6d6d","#00d995","#92d13c","#fa88c4","#00d1ec","#aa89e7",
					"#00b3f9","#33569b","#457ea6","#cca300","#b0e5ee","#ef65a2","#a377fe","#d0a45f",
					"#65cfc2","#3c81f0","#ff5663","#e0acd9","#ff7e5a","#915b93","#8f8371","#616fba",
					"#fe6883","#00a0ad","#d1a434","#1da4b5","#01579b","#45777d","#168ef1","#cd9655",
					"#353e47","#b9d6e7","#d1dfcd","#db6f4e","#c4b098","#bdc49a","#87a3b9","#bca2af"
				];

			if (that.isEmptyData) {
				
				$("#chart_trend").html("Trend Chart 데이터를 찾을 수 없습니다.");
				$(".chart_trend_combo_wrap").hide();
				$("#chart_trend_combo").html("");
				sf.clearTrend();
				sf.clearNetwork();
				$("body").loading("stop");
				return false;
			}
			
			// combo boxes
			$("#chart_trend_combo").html("");
			
			var i = 0;
			if (sv.category === "1") {

				$(".chart_trend_combo_wrap").show();
				$("#chart_trend_combo").append(
					"<div class='d-flex chart_trend_combo_sub_ctrl_wrap'>" +
						"<a class='btn_research'>[재조회하기]</a>" +
						"<a class='btn_select_all'>[전체선택]</a>" +
						"<a class='btn_deselect_all'>[전체해제]</a>" +
						"<a class='btn_combo_close'>[닫기]</a>" +
					"</div>"
				);
				
				for (i = 0; i < that.cpnyNames_o.length; i++) {
					var subWrap  = $("<div class='float-left'>");
					var subFlex  = $("<div class='ctrl_combo_box_sub_wrap d-flex'>");	
					var isSelect = that.cpnyNames.indexOf(that.cpnyNames_o[i]) > -1;
					subFlex.append(
						"<div class='ctrl_combo_box' select='" + (isSelect ? "Y" : "N") + "' cmpy-name='" + that.cpnyNames_o[i] + "'></div>" +
						"<div class='ctrl_name'>" + that.cpnyNames_o[i] + "</div>"
					);
					subWrap.append(subFlex);
					$("#chart_trend_combo").append(subWrap);	
				}
				
				$(".ctrl_combo_box_sub_wrap").find(".ctrl_combo_box").each(function(idx, el) {
					if ($(el).attr("select") === "Y") $(el).css("background-color", "#666666");
				});

				$(".combo_wrap_open_btn").on("click", function() {
					$(".combo_list_show_wrap").slideDown("fast");
				});
				
				$(".btn_combo_close").on("click", function() {
					$(".combo_list_show_wrap").slideUp("fast");
				});

				$(".ctrl_combo_box").on("click", function() {

					sv.cmpyList = [];
					var $select = $(this).attr("select");

					$(this).attr("select", "N");
					$(this).css("background-color", "#ffffff");
					if (!$select || $select === "N") $(this).attr("select", "Y");

					if ($(this).attr("select") === "Y") $(this).css("background-color", "#666666");

					$("#chart_trend_combo").find(".ctrl_combo_box").each(function(idx, el) {
						if ($(el).attr("select") === "Y") sv.cmpyList.push($(el).attr("cmpy-name"));
					});
				});

				$(".btn_select_all").on("click", function() {
					sv.cmpyList = [];
					$("#chart_trend_combo").find(".ctrl_combo_box").each(function(idx, el) {
						$(el).attr("select", "Y");
						$(this).css("background-color", "#666666");
						sv.cmpyList.push($(el).attr("cmpy-name"));
					});
				});

				$(".btn_deselect_all").on("click", function() {
					sv.cmpyList = [];
					$("#chart_trend_combo").find(".ctrl_combo_box").each(function(idx, el) {
						$(el).attr("select", "N");
						$(this).css("background-color", "#ffffff");
					});
				});

				$(".btn_research").on("click", function() {
					that.setData(null, true);
					that.onDrawChart();
					return false;
				});


			} else {
				$(".chart_trend_combo_wrap").hide();
			}

			var dashboard = new google.visualization.Dashboard(document.getElementById('dashboard_div'));
			var fmt = "yyyy-MM-dd",
				  pd   = SDII.Globals.Vars.period;

			if (pd === "mon" || pd === "quarter") {
				fmt = "yyyy-MM";
			} else if (pd === "year") {
				fmt = "yyyy";
			}

			var ctrlOpts = {
				controlType: "ChartRangeFilter",
				containerId: "ctrl_trend",
				state: {
					range: {
						start: new Date(moment(fromDate).format("YYYY/MM/DD")), //new Date(2018, 8, 1), 
						end  : new Date(moment(toDate).add(5, "days").format("YYYY/MM/DD"))// new Date(2018, 9, 30)
					}
				},
				options: {
					// Filter by the date axis.
					filterColumnIndex: 0,
					ui: {
					  chartType: "LineChart",
			    	//curveType: 'function',
			    	curveType: 'none',
					  chartOptions: {
							chartArea: {
								width: "90%", height: "40px"
							},
							colors: ["none"],
							//vAxis: {
								//viewWindow : {min: -2}
							//},
							hAxis: {
								baselineColor: "none", 
								//format: "dd.MM.yyyy" 
							},
					  },
					  	chartView: {
								columns: [0, 1]
					  	},
					   // 1 day in milliseconds = 24 * 60 * 60 * 1000 = 86,400,000
					   //minRangeSize: 86400000
					}
				}
			};

			var chartOpts = {
				chartType: "LineChart",
				containerId: "chart_trend",
				options: {
				  	lineWidth: 2,
						theme: {
				    	chartArea: {margin:0, width: "90%", height: "70%"},
				    },
				    animation:{
					    startup: true,
					    duration: 1000,
					    easing: 'inAndOut',
				    },
			    //title: 'none',
			    //titlePosition: "out",
					titleTextStyle: {
						fontSize: 20,
						color: "rgba(255,255,0,0.5)",
					},
					tooltip: {
						trigger: "both",
						isHtml : true
					},
					curveType: 'function',
				    //curveType: 'none',
				    //focusTarget: 'category',
				    //pointSize: 1.5,
				    //pointShape: 'circle',
					legend: { 
						position: 'top', 
						alignment: "end",
						textStyle: {
							fontSize: 16
						}
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

					series: {},
					
					crosshair: {
						color: '#bbb',
						opacity: 1,
		 				trigger: 'both',
		 				orientation: 'both'
					}
				}
			};

			for (i = 0; i < colors.length; i++)
				chartOpts.options.series[i] =  {color: colors[i], lineWidth: 1, lineDashStyle: [1, 1], pointSize: 1.5, pointShape: "cycle" };

			//var tooltipDiv = $("#chart_trend_piechart").get(0);
			//var tooltipChart = new google.visualization.PieChart(tooltipDiv);
			var dataRows = that.chartData,
	   			top3Rows = that.top3Data,
	   			emoRows  = that.emoData,
	   			cnames   = that.cpnyNames,
	   			tCnt     = 0;

			console.log(dataRows); console.log(top3Rows); console.log(emoRows);

	 		$(".chart_trend_tooltip").show();
	 		tCnt = (dataRows[0].length - 1) / 2;

	 		for (i = 0; i < dataRows.length; i++) {

		    	var tDate = moment(dataRows[i][0]).format("YYYY-MM-DD");
	
		    	for (var j = 0; j < tCnt; j++) {
	
		    		var pieDataTb = new google.visualization.DataTable();
				    pieDataTb.addColumn("string", "category");
				    pieDataTb.addColumn("number",  "count");
				    pieDataTb.addColumn({
						type: "string",
			    		label: "null",
			    		role: "tooltip"
			    	});
	
				    pieDataTb.addRows(emoRows[tDate][cnames[j]]);
		    	
		    		var tooltipDiv   = $("#chart_trend_piechart").get(0);
		    		var tooltipChart = new google.visualization.PieChart(tooltipDiv);
		    		var curDate      = moment(dataRows[i][0]).format("YYYY-MM-DD");
		    		
		    		google.visualization.events.addListener(tooltipChart, 'ready', function(subIdx, curName, curDate, cNames) {

		    			var curTop3 =  top3Rows[tDate][curName];

		    			$("#chart_trend_abs").html("");
					    $("#chart_trend_top3").html("");
					    $("#chart_trend_ctrl").html("");

			    		$("svg rect:first", "#chart_trend_piechart").attr("width", 248);
			    		$("svg g g text", "#chart_trend_piechart").each(function (i, v) {

						  	$(this).text(pieDataTb.getValue(i, 2) + (i === 0 ? "%(긍정)" : i === 1 ? "%(부정)" : "%(중립)"));
					    	$(this).attr("x", parseInt($(this).attr("x")) - 30);
					    	var $circle = $(this).parent().next();
					    	$circle.attr("cx", parseInt($circle.attr("cx")) - 30);
					  	});
				  	
			    		var $gNodes = $("svg g", "#chart_trend_piechart");
			    		var $gTgt   = [7, 8, 9];
			    		for (var tIdx = 0; tIdx < $gTgt.length; tIdx++) {
			    			var $ttNode = $($gNodes.get($gTgt[tIdx]));
							$ttNode.css("transform", "translate(-20px, 0px)");
			    		}
		    		
			    		//if (sv.category === "5" && subIdx > 0) $("#chart_trend_piechart").html("");
			    		if (sv.category === "5") $("#chart_trend_piechart").html("");
			    		/*
			    		$gNodes = $("svg g g", "#chart_trend_piechart");
			    		for (var tIdx = 0; tIdx < 6; tIdx++) {
			    			$ttNode = $($gNodes.get(tIdx));
								$ttNode.css("transform", "translate(-20px, 0px)");
			    		}
			    		*/

			    		//$isAssoEvt  = sv.category === "5" && subIdx > 0;
			    		var $isAssoEvt  = false;
			    		var $assoKwdAct = "'onRequestKwdAssoV2(this);'";
			    		//if ($isAssoEvt) $assoKwdAct = "''";

					    var $tdv = $("<div class='chart_trend_top3_list'></div>");
					   	for (var z = 0; z < curTop3.length; z++) {
					   		//var $curKwd = curTop3[z][0];
					   		var $ta = $(
					   			"<div class='chart_trend_top3_kwd_wrap'>" +
					   				"<div class='chart_trend_top3_kwd" + (z === 0 ? " top3_kwd_highlight" : "") + "'>" +
					   					"<a data-type='top3' onclick="+ $assoKwdAct + ">" + curTop3[z][0] + "</a>" +
				   					"</div>" +
					   				"<div class='chart_trend_top3_kwd_cnt'>" + curTop3[z][1] + "건</div><div></div>" +
					   			"</div>"
				   			);
				   			$tdv.append($ta).append("<br/>");
					    }

						var $issueKwd  = "이슈키워드";
						var $issueAct  = "'onRequestKwdAssoV2(\"" + curTop3[0][0] + "\")'";
				    	//if ($isAssoEvt) {
						if (sv.category === "5" && subIdx > 0) {
				    		$issueKwd  = "\"" + cNames[0] + "\"의 연관종목 " + subIdx;
				    		//$issueAct = "''";
				    	}
			    	
					    $("#chart_trend_top3")
				    		.append(

				    			"<div class='d-flex justify-content-between chart_trend_top3_title'>" +
				    				"<div><a onclick=" + $issueAct + ">" + $issueKwd + "</a></div>" +
				    				//+ "<div><a onclick='deselectTrendChart()'></a></div>" +
			    				"</div>" +
				    			"<div class='chart_trend_top3_title_sub'>" + curName + ", " + curDate + "</div>"
			    			).append($tdv);
					    
					    $(".chart_trend_tooltip_wrap").css("height", "295px");
					    $(".chart_trend_tooltip hr").show();
					    $(".chart_trend_piewrap").css("height", "80px");
					    $(".chart_trend_piechart").css("height", "80px");
					    
				    	if (sv.category !== "5") {
			    			$("#chart_trend_abs").append(
				    		"<div class='chart_trend_top3_title'>" +
			    				"<a onclick='onReqPosNegStat(\"" + curName + "," + curTop3[0][0] + "," + curDate + "\")'>긍/부정 현황</a>" +
		  					"</div>");	
				    	} else {
				    		$(".chart_trend_tooltip_wrap").css("height", "150px");
				    		$(".chart_trend_tooltip hr").hide();
				    		$(".chart_trend_piewrap").css("height", "0px");
				    		$(".chart_trend_piechart").css("height", "0px");
				    	}
				    	
				    	dataRows[i][(subIdx * 2) + 2] = $(".chart_trend_tooltip").html();

				    	$("#chart_trend_piechart").html("");
				    	$("#chart_trend_abs").html("");
					    $("#chart_trend_top3").html("");
					    $("#chart_trend_ctrl").html("");

		    		}.bind(this, j, cnames[j], curDate, cnames));
		      		
		    		tooltipChart.draw(pieDataTb, {
			    		colors: ["#3c81f0", "#ff5663", "#2f4554"],
			    		pieHole: 0.7,
			    		pieSliceText: "none",
			    		sliceVisibilityThreshold: 0,
			    		tooltip: {
			    			trigger: 'selection',
			    		},
			    		theme: {
					    	chartArea: {margin:0, width: "100%", height: "100%"},
					    }, 
					    legend: { 
				    		position: 'end',
				    		textStyle: {fontSize: 14}
					    },
					    //is3D: true
		    		});
		    	}
		    	dataRows[i][0] = new Date(moment.unix(dataRows[i][0] / 1000));
		    	//dataRows[i][0] = dataRows[0][i] / 1000
	 		}

	 		$(".chart_trend_tooltip").hide();
			var data = new google.visualization.DataTable();
			data.addColumn({
		 		//role: "domain",
		 		type: "date"
		 		//lable: "Date"
		 	});

		 	for (i = 0; i < cnames.length; i++) {
		 		data.addColumn('number', cnames[i]);
		 		data.addColumn({
		 			type: "string",
		 			label: "null",
		 			role: "tooltip",
		 			p: {html: true}
		 		});
		 	}
		 	data.addRows(dataRows);
	    
			var control  = new google.visualization.ControlWrapper(ctrlOpts);
			var chart    = new google.visualization.ChartWrapper(chartOpts); 

			google.visualization.events.addOneTimeListener(chart, 'ready', function(chart) {

				var tgtRow  = chart.getDataTable().getViewRows().length - 1,
					colSize = chart.getDataTable().getViewColumns().length,
					cmpArr  = [], cateCnt = 0, maxVal = 0, u = 0;
				
				cateCnt = (colSize - 1) / 2;

				sv.curTrendChart = chart.getChart();

			 	google.visualization.events.addListener(chart, 'select', function(chart, cateCnt) {
			 		
			 		var opts   = chart.getOptions(),
						//tTgt   = $("#dashboard_trend svg path"),
						tempI  = 0,
						tgtIdx = 0,
						slct   = chart.getChart().getSelection();

					if (!slct || slct.length === 0 || slct[0].column === null) { // || slct[0].row === null) {
						$("body").loading("stop");
						return false;
					}

					tgtIdx = (slct[0].column  - 1) / 2;
					
					for (tempI = 0; tempI < cateCnt; tempI++) {
						opts.series[tempI].lineWidth  = 1;
						opts.series[tempI].lineDashStyle = [1, 1];
						opts.series[tempI].pointSize  = 1.5;
						opts.series[tempI].pointShape ="circle";
					}

					opts.series[tgtIdx].lineWidth  = 2.5;
					opts.series[tgtIdx].lineDashStyle = 1;
					opts.series[tgtIdx].pointSize  = 3;
					opts.series[tgtIdx].pointShape = "square";

					if (slct[0].row === null && slct[0].column !== null) {
						chart.setOptions(opts);
						chart.draw();
						return false;
					}

					var ttData  = chart.getDataTable().getValue(slct[0].row, slct[0].column + 1);
					var top1Key = "";
					if ($("a[data-type='top3']", ttData)[0]) top1Key = $("a[data-type='top3']", ttData)[0].text.split(" ")[0];
					var top3Key = [];
					var reqDate = moment(chart.getDataTable().getValue(slct[0].row, 0)).format("YYYY-MM-DD");

					//channelAction(chart.getDataTable().getColumnLabel(slct[0].column));
					chart.setOptions(opts);
					chart.draw();
					chart.getChart().setSelection([{column: slct[0].column, row: slct[0].row}]);

					if (!ttData || !top1Key || top1Key === "데이터없음") {

						alert("조회할 키워드가 없습니다.");
						//$(tTgt.get(tgtIdx)).attr("stroke-width", 2).attr("stroke-dasharray", "1,1");
						$("#chart_trend_sub").html("");
						$(".chart_trend_sub_close").click();
						return false;
					}

					$(ttData).find("[data-type='top3']").each(function (idx, el) { 
						top3Key.push(el.text.split(" ")[0]);
					});

					$(".chart_network").html("<div id='chart_network_title'>연관어들에 대해 로딩중입니다.</div>");

					if (sv.category === "5") top3Key = this.cpnyNames.slice(1, 4);
					
					var mainLabel = chart.getDataTable().getColumnLabel(slct[0].column);
					if (sv.category === "5") mainLabel = this.cpnyNames[0];
					
					if (sv.category !== "4")
						sf.onRequestKwdNetwork(top3Key.join(","), reqDate, mainLabel);
					else
						$("body").loading("stop");

					if (SDII.Globals.Vars.category !== "1" && !(SDII.Globals.Vars.category === "4" || SDII.Globals.Vars.category === "5")) {
						$("body").loading("stop");
						return false;
					}
					
					if (sv.category === "5" && tgtIdx > 0) return false;
					if (sv.category === "4") return false; // 상품브리핑 임시로 막음
					sf.channelAction(top1Key, chart.getDataTable().getColumnLabel(slct[0].column), reqDate);
					
					return true;
				}.bind(this, chart, cateCnt));

				for (u = 0; u < cateCnt; u++) {
					cmpArr.push(chart.getDataTable().getValue(tgtRow, (u * 2) + 1));
				}
				
				maxVal = Math.max.apply(this, cmpArr);
				var tgtCol = cmpArr.indexOf(maxVal) * 2 + 1;
				
				//if (sv.category === "2") chart.getChart().setSelection([{column: tgtCol, row: tgtRow}]);
				
				var reqDate = moment(chart.getDataTable().getValue(tgtRow, 0)).format("YYYY-MM-DD");
				var ttDom    = $(chart.getDataTable().getValue(tgtRow, tgtCol + 1));
				var top3Dom  = $("[data-type='top3']", ttDom).slice(0, cateCnt);
				var newKeys  = [];
				top3Dom.each(function (idx, el) {
					newKeys.push(el.text.split(" ")[0]);
				});
				//console.log("onInitChart : " + that.isCacheData);

				if (!that.isCacheData) {
					$(".chart_network").html("<div id='chart_network_title'>연관어들에 대해 로딩중입니다.</div>");
					if (sv.category === "5") newKeys = this.cpnyNames.slice(1, 4);
					
					var mainLabel = chart.getDataTable().getColumnLabel(tgtCol);
					if (sv.category === "5") mainLabel = this.cpnyNames[0];
					
					if (sv.category !== "4") 
						sf.onRequestKwdNetwork(newKeys.join(","), reqDate, mainLabel);	
					else 
						$("body").loading("stop");	
				} else {
					$("body").loading("stop");
				}
				
				/*
				var boldLine = (tgtCol - 1) / 2 + cateCnt - 1;
				$($("#dashboard_trend svg path").get(boldLine)).attr("stroke-width", 3);
				*/
				//console.log(cmpArr); console.log(tgtCol);

			}.bind(this, chart));

		 	dashboard.bind(control, chart);
		 	dashboard.draw(data);

		 	return this;
		}
	}

	global.SDII.Chart.trendChart = global.SDII.Chart.trendChart || trend;

})(window);

});