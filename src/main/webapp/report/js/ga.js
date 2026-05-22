
(function() {

window.uniq = function (list) {
    var result = [];
    $.each(list, function(i, e) {
        if ($.inArray(e, result) == -1) result.push(e);
    });
    return result;
}

window.onInitDetlSearch = function() {

	$(".detl_wrap").hide();
	$(".btn_search_close_btn").hide();
	$(".btn_search_detl_btn").hide();
	$(".btn_search_init_btn").hide();
	$(".api_container").hide();

	function getApiParam() {

		var kwd = $("input[name=search_kwd]").val();
		if (!kwd.trim()) {
			alert("검색어를 입력해주세요.");
			$("input[name=search_kwd]").focus();
			return null;
		}
		//(삼성)^(전자)#(넥쏘)

		//kwd = kwd.replace(/\s+/gi, "^");
		var kwdList = kwd.replace(/\(/gi, "").replace(/\)/gi,"")
			.replace(/(\[AND\])+/gi, "[AND]")
			.replace(/(\[OR\])+/gi,  "[OR]")
			.replace(/(\[NOT\])+/gi, "[NOT]")
			.replace(/(\[AND\]\[OR\])+/gi, "[AND]")
			.replace(/(\[AND\]\[NOT\])+/gi, "[AND]")
			.replace(/(\[OR\]\[AND\])+/gi, "[OR]")
			.replace(/(\[OR\]\[NOT\])+/gi, "[OR]")
			.replace(/(\[NOT\]\[AND\])+/gi, "[NOT]")
			.replace(/(\[NOT\]\[OR\])+/gi, "[NOT]")
			.replace(/(\[AND\])+/gi, "[AND]")
			.replace(/(\[OR\])+/gi,  "[OR]")
			.replace(/(\[NOT\])+/gi, "[NOT]")
			.replace(/(\[AND\])+/gi, "^")
			.replace(/(\[OR\])+/gi, ",")
			.replace(/(\[NOT\])+/gi, "#")
			.replace(/\s+/gi, "^")
			.split(/(\^|\,|\#)/gi);
		
		for (var i = 0; i < kwdList.length; i++) {
			if (kwdList[i] === "^" || kwdList[i] === "," || kwdList[i] === "#") continue;
			kwdList[i] = "(" + kwdList[i] + ")";
		}
		
		kwd = kwdList.join("");

		//console.log(kwd);
		//kwdList = kwd.split(/\s+/).join("^");

		var cateCds = "", tempCds = [];

		if (sv.slctNews) tempCds = sv.slctNews.split(",");
		if (sv.slctSns)  tempCds = tempCds.concat(sv.slctSns.split(","));
		cateCds = tempCds.join(",");

		if (!cateCds) {
			alert("선택된 출처가 없습니다.");
			return null;
		}

		return {
			searchPattern: kwd,
			startDate: $(".flatpickr.detl_pickr")[0]._flatpickr._input.value,
			endDate: $(".flatpickr.detl_pickr")[1]._flatpickr._input.value,
			categoryCode: cateCds,
			businessCode: sv.groupCd,
			searchData: sv.schPart,
			section: "100,101,102,103", //hard 

			analyticsCode: sv.slctApi,
			limitCnt: 10,
			pageCnt : 20,
			currentPage: -1
		};
	}

	function onDrawPatternSearchRes(data) {

		var parent = $(".doc_list_wrap");
		parent.html("<div class='list_row_wrap'>조회된 결과가 없습니다.</div><br/>");
		
		if (!data || !data.hasDocs || !data.data) {
			alert("조회된 결과가 없습니다.");
			$(".api_container").hide();
			return false;
		}

		try {
			var res      = JSON.parse(data.data);
			var realData = res.response;
			var nCnt = realData.docs.length;

			console.log(realData);

			if (!res || !res.response || !res.response.docs || res.response.docs.length === 0) {
				alert("조회된 결과가 없습니다.");
				$(".api_container").hide();
				return false;
			}

			if (nCnt > 0) {
				parent.html(
					"<div class='d-flex align-items-center excel_title_wrap'>" +
						"<img src='/bdp/report/img/icon/excel.ico' width='16' height='16'/>&nbsp;" +
	  				"<div class='title_excel'>조회결과 다운로드</div>" +
	  			"</div><hr/>" +
	  			"<div class='list_row_cont_wrap'></div>"
				);
			}

			var contWrap = $(".list_row_cont_wrap");

			for (var i = 0; i < nCnt; i++) {

				var date = realData.docs[i].date || "";
				if (date && date.length >= 12) {
					date = moment(date, "YYYYMMDDkkmmss", false).format("YYYY-MM-DD kk:mm:ss");
				}

				var cont = realData.docs[i].content || "";
				if (cont.length > 400) {
					cont = cont.substring(0, 350) + "...";
				}

				contWrap.append(
					"<div class='list_row_wrap'>" + 
						"<div><a class='go_url' url='" + realData.docs[i].url + "'> " + realData.docs[i].title + "</a></div>" +
						"<div class='doc_cont'>" + cont + "</div>" +
						"<div class='d-flex justify-content-start press_wrap'>" + 
							"<div class='press'>" + realData.docs[i].press + "</div>" +
							"<div class='doc_time'>&nbsp;/&nbsp;" + date + "</div>" +
						"</div>" +
					"</div><hr/>"
				);
			}

			$(".go_url").on("click", function() {
				window.open($(this).attr("url"), null, "width=1200,height=800");
			});

			$(".excel_title_wrap").on("click", function() {

				var param = getApiParam();
				if (!param) return false;
				param.analyticsCode = "A001";
				
				$("#download_form input").each(function(idx, el) {
				  $(el).val(encodeURIComponent(param[el.name]));
				});

				$("#download_form").attr("action", SDII.Url + "/ga/call_general_analytics_excel").submit();

				return true;
			});

			$(".api_container").show();

		} catch(e) {
			console.log(e);
			alert("JSON에 문제가 발생하였습니다.");
			return false;
		}
	}
	
	function onDrawA002(result) {
		
		var temp = {};
  		
  		var data = JSON.parse(result.data);
  		
  		console.log(data);
  		
  		if (!data.response || !data.response.docs || data.response.docs.length === 0) {
  			alert("조회되는 데이터가 없습니다.")
  			return false;
  		}
  	
  		var drawData = {};
  		var i = 0;
  		for (i = 0; i < data.response.docs.length; i++) {
  			var curDocs = data.response.docs[i];
  			var curCont = curDocs.mContent;
  			var $c   = curCont.split(" ");
  			$c.forEach(function(el, idx) {
	  		  var d = el.split("/");
	  		  //console.log("key: " + d[0] + "/value: " + d[1]);
	  		  if (!drawData[d[1]]) drawData[d[1]] = [];
	  		drawData[d[1]].push(d[0]);
	  		});
	  		console.log(drawData);
  		}
  		
  		if (Object.keys(drawData).length == 0) return false;
  		
  		
  		$(".analytics_container").show();

  		$(".analytics_title").text("형태소 분석결과");
  		
  		var $parent = $(".analytics_list_wrap");
  		$parent.html("");
  		
  		
  		for (var el in drawData) {
  			
  			$parent
  				.append($("<div style='font-size:18px;font-weight:bold'>" + el + "</div>"))
  				.append($("<div>" + uniq(drawData[el]).join(", ") + "<div>"))
  				.append($("<hr/>"));
  		}
	}
	
	function onDrawA003(result) {
		
		function classes(root) {
			
			var classes = [];
	
			function recurse(name, node) {
				if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
				else classes.push({packageName: name, className: node.name, value: node.size});
			}
	
			recurse(null, root);
			return {children: classes};
		}
		
		var temp = {};
  		
  		var data = JSON.parse(result.data);
  		
  		console.log(data);
  		
  		if (!data.response || !data.response.docs || data.response.docs.length === 0) {
  			alert("조회되는 데이터가 없습니다.")
  			return false;
  		}
  		
  		var docs = data.response.docs,
  			idxArr = {},
  			idxCnt = 0
  			r    = {
	  			name: "root",
	  			children: []
	  		}, 
	  		i = 0;
  		
  		for (i = 0; i < docs.length; i++) {
  			
  			if (idxArr[docs[i].pos_tag] !== undefined) continue;
  			
  			r.children[idxCnt] = {
				name: docs[i].pos_tag,
				children: []
			};
  			//console.log("Set [" + docs[i].pos_tag + "] index to" + idxCnt);
  			idxArr[docs[i].pos_tag] = idxCnt;
  			idxCnt += 1;
  		}

  		for (i = 0; i < docs.length; i++) {
  			
  			if (docs[i].word_count < 3) continue;
  			
  			var targetIdx = idxArr[docs[i].pos_tag];
  			
  			r.children[targetIdx].children.push({
  				name: docs[i].word, 
  				size: docs[i].word_count * 2
			});
  		}
  		/*
  		domain: "news"
  		pos_tag: "NNG"
  		word: "수소"
  		word_count: 68
  		word_length: "2"
  		*/
  		
  		$(".analytics_container").show();
	  	$(".analytics_list_wrap").html("");
	  	$(".analytics_title").text("키워드빈도 분석결과");
	  	
  		var diameter = 960,
	  	    format = d3.format(",d"),
	  	    color = d3.scaleOrdinal(d3.schemeCategory20c);
	
	  	var bubble = d3.pack()
	  	    .size([diameter, diameter])
	  	    .padding(1.5);
	
	  	var svg = d3.select(".analytics_list_wrap").append("svg")
	  	    .attr("width", diameter)
	  	    .attr("height", diameter)
	  	    .attr("class", "bubble");
	  	
	    var root = d3.hierarchy(classes(r))
	      .sum(function(d) { return d.value; })
	      .sort(function(a, b) { return b.value - a.value; });

	    bubble(root);
	    var node = svg.selectAll(".node")
	    	.data(root.children)
			.enter().append("g")
			.attr("class", "node")
			.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

	    node.append("title")
	    	.text(function(d) { return d.data.className + ": " + format(d.value / 2); });

	    node.append("circle")
	      	.attr("r", function(d) { return d.r; })
	      	.style("fill", function(d) { 
	      		return color(d.data.packageName); 
      	});

	    node.append("text")
	    	.attr("dy", ".3em")
	    	.style("text-anchor", "middle")
	    	.text(function(d) { return d.data.className.substring(0, d.r / 3); });
	}
	
	function calcSlctBox(t, keyName, target) {

		target     = "";
		var $calcTemp  = [];

		$(t).parent().children().each(function(idx, el) {
			$(el).removeClass("cate_box_click");
		});

		var $selectYn = $(t).attr("select");

		$(t).attr("select", "N");		
		if (!$selectYn || $selectYn === "N") $(t).attr("select", "Y");		

		$(t).parent().find("div[select=Y]").each(function(idx, el) {
			$(el).addClass("cate_box_click");
			$calcTemp.push($(el).attr(keyName));
		});
		target = $calcTemp.join(",");
		console.log(target);
		return target;
	}

	sv.newsMeta = [
		{"연합뉴스"     : "0820101001000000"}, {"머니투데이"   : "0820101003000000"}, {"한국경제"     : "0820101004000000"},
		{"매일경제"     : "0820101005000000"}, {"이데일리"     : "0820101006000000"}, {"파이낸셜뉴스" : "0820101007000000"},
		{"아시아경제"   : "0820101008000000"}, {"MBN"         : "0820101010000000"}, {"YTN"         : "0820101011000000"},
		{"동아일보"     : "0820101012000000"}, {"서울경제"     : "0820101013000000"}, {"세계일보"     : "0820101014000000"},
		{"노컷뉴스"     : "0820101016000000"}, {"경향신문"     : "0820101018000000"}, {"헤럴드경제"   : "0820101019000000"},
		{"서울신문"     : "0820101020000000"}, {"국민일보"     : "0820101022000000"}, {"문화일보"     : "0820101033000000"},
		{"중앙일보"     : "0820101037000000"}, {"조선일보"     : "0820101041000000"}, {"SBS"         : "0820101046000000"},
		{"데일리안"     : "0820101047000000"}, {"조선비즈"     : "0820101048000000"}, {"오마이뉴스"   : "0820101050000000"},
		{"쿠키뉴스"     : "0820101057000000"}, {"JTBC"        : "0820101096000000"}, {"KBS"         : "0820101183000000"},
		{"MBC"         : "0820101218000000"}, {"뉴시스"       : "0820101492000000"}, {"한국일보"     : "0820101496000000"},
		{"한겨레"       : "0820101499000000"}, {"뉴스1"        : "0820101512000000"}
	];

	sv.schPart    = 0;
	sv.slctSrc    = "";
	sv.slctNews   = "";
	sv.slctSns    = "";
	sv.slctApi    = "";

	var $p = $(".search_container");

	if ($p.length === 0) return false;

	// 검색범위, 검색기간
	$(".search_ctrl_btn").on("click", function() {

		if (
			$(this).attr("ctrl-cate") || $(this).attr("ctrl-mod") ||
			$(this).attr("sch-opts")
		) return false;

		// 검색범위
		if ($(this).attr("ctrl-part")) {

			$(this).parent().children().each(function(idx, el) {
				$(el).removeClass("cate_box_click");
			});

			var $selectYn = $(this).attr("select");
			$(this).attr("select", "N");		
			if (!$selectYn || $selectYn === "N") $(this).attr("select", "Y");
			$(this).addClass("cate_box_click");
			sv.schPart = $(this).attr("ctrl-part");

			return false;
		}

		// 검색기간 
		//var $toDay = moment(new Date);
		var $range = $(this).attr("ctrl-range");
		var $from  = "";
		var $to    = moment(new Date).format("YYYY-MM-DD");

		if ($range === "week") {
			$from = moment(new Date).subtract(7, "days").format("YYYY-MM-DD");
			
		} else if ($range === "mon") {
			$from = moment(new Date).subtract(1, "months").format("YYYY-MM-DD");
		} else if ($range === "mon3") {
			$from = moment(new Date).subtract(3, "months").format("YYYY-MM-DD");
		} else if ($range === "mon6") {
			$from = moment(new Date).subtract(6, "months").format("YYYY-MM-DD");
		} else if ($range === "year") {
			$from = moment(new Date).subtract(1, "years").format("YYYY-MM-DD");
		}

		$(".flatpickr.detl_pickr")[0]._flatpickr.setDate($from);
		$(".flatpickr.detl_pickr")[1]._flatpickr.setDate($to);
		
		return true;

	});


	// 출처 스크립트
	$(".cate_box").on("click", function() {
		sv.slctSrc = calcSlctBox($(this), "ctrl-cate", sv.slctSrc);
	});

	// 주요뉴스
	for (var i = 0; i < sv.newsMeta.length; i++) {
		for (var el in sv.newsMeta[i]) 
			$(".cate_news_wrap").append("<div class='float-left cate_news_box' cate-code='" + sv.newsMeta[i][el] +"''>" + el + "</div>");	
	}
	$(".nsrc").text(sv.newsMeta.length);
	$(".cate_news_all_btn").on("click", function() {		
		var $selectYn = $(this).attr("select");
		$(this).attr("select", "N");		
		if (!$selectYn || $selectYn === "N") $(this).attr("select", "Y");		

		if ($(this).attr("select") === "Y") {
			$(this).css("background-color", "#666666");
			sv.slctAll  = "Y";
			sv.slctNews = "";
			var $tempCalc   = [];
			$(".cate_news_wrap").children().each(function(idx, el) {
				$(el).addClass("cate_box_click");
				$(el).attr("select", "Y");
				$tempCalc.push($(el).attr("cate-code"));
			});
			sv.slctNews = $tempCalc.join(",");
		} else {
			$(this).css("background-color", "#fff");
			sv.slctAll = "N";
			$(".cate_news_wrap").children().each(function(idx, el) {
				$(el).attr("select", "N");
				$(el).removeClass("cate_box_click");
			});
			sv.slctNews = "";
		}
		//console.log(sv.slctAll);
	});
	$(".cate_news_box").on("click", function() {
		sv.slctNews = calcSlctBox($(this), "cate-code", sv.slctNews);
	});

	$(".cate_sns_all_btn").on("click", function() {

		sv.slctSns = "";
		var $selectYn = $(this).attr("select");

		$(this).attr("select", "N");		
		if (!$selectYn || $selectYn === "N") $(this).attr("select", "Y");

		if ($(this).attr("select") === "Y") {
			$(this).css("background-color", "#666666");
			sv.slctSns = "";
			var $tempCalc   = [];
			$(".cate_sns_wrap").children().each(function(idx, el) {
				$(el).addClass("cate_box_click");
				$(el).attr("select", "Y");
				$tempCalc.push($(el).attr("cate-code"));
			});
			sv.slctSns = $tempCalc.join(",");

		} else {

			$(this).css("background-color", "#fff");
			$(".cate_sns_wrap").children().each(function(idx, el) {
				$(el).attr("select", "N");
				$(el).removeClass("cate_box_click");
			});
			sv.slctSns = "";
		}
	});

	$(".cate_sns_box").on("click", function() {
		sv.slctSns = calcSlctBox($(this), "cate-code", sv.slctSns);
	});

	$(".cate_api_box").on("click", function() {
		sv.slctApi = "";
		$(".cate_api_box").each(function(idx, el) {
			$(el).removeClass("cate_box_click");
		})
		$(this).addClass("cate_box_click");
		sv.slctApi = $(this).attr("api-code");
	});

	$(".btn_search_detl_btn, .btn_search_simple_btn").on("click", function() {
		
		$(".api_container").hide();		
		var isSimpleSch = $(this).hasClass("btn_search_simple_btn");
		
		sv.slctApi = "A001";
		
		if (isSimpleSch) {
			$(".cate_api_box").each(function(idx, el) { 
				$(el).removeClass("cate_box_click"); 
			});
		}

		$(".btn_detl_opt").click();
		$(".docs_list_conatiner").hide();
		$(".analytics_container").hide();

		var param = getApiParam();
		if (!param) return false;
		
		$("body").loading();

		$.ajax({
			url: SDII.Url + "/ga/call_general_analytics_api",
			type: 'post',
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			data: JSON.stringify(param),
			success: function(result) {

				$("body").loading("stop");

			  	if (!result.isSuccess) {
			  		alert("데이터 요청에 실패하였습니다.");
			  		return false;
			  	}
	
			  	if (sv.slctApi === "A001") {
				  	$(".docs_list_conatiner").show();
						onDrawPatternSearchRes(result);	
			  	} else {
			  		console.log(result);
			  	}
			  	return true;
		    }
		});
		
		return true;
	});

	$(".sch_part").first().click();
	//$(".sch_range").first().click();
	$(".cate_news_box").first().click();
	$(".cate_api_box").first().click();

	$(".docs_list_conatiner").hide();
	$(".analytics_container").hide();
	
	$("input[name=search_kwd").on("focus", function() {
		//$(".detl_wrap").slideDown("fast");
	});

	$(".btn_detl_opt, .btn_search_close_btn").on("click", function() {
		$(".detl_wrap").slideUp("fast");
		$(".btn_search_close_btn").hide();
		$(".btn_search_detl_btn").hide();
		$(".btn_search_init_btn").hide();
	});

	$(".btn_detl_opt_open").on("click", function() {
		$(".detl_wrap").slideDown("fast");
		$(".btn_search_detl_btn").show();
		$(".btn_search_init_btn").show();
		$(".btn_search_close_btn").show();
	});

	//$("input[name=search_kwd]").focus();

	$(".search_ctrl_btn.sch_opts").on("click", function() {
		var opts = $(this).attr("sch-opts");
		if (!opts) return false;
		
		var optKwd = "[AND]";
		if (opts === "1") optKwd = "[OR]";
		if (opts === "2") optKwd = "[NOT]";

		$("input[name=search_kwd").val($("input[name=search_kwd").val() + optKwd);
		$("input[name=search_kwd").focus();
		
		return true;
	});

	$("input[name=search_kwd]").on("keydown", function() {

		if (event.keyCode === 13) { 
			$(".btn_search_simple_btn").click();
		}
		return true;
	});

	$(".btn_insight_report").on("click", function() {
		location.href = "/bdp";
	});

	$(".go_starter_btn").on("click", function() {
		location.href = "/bdp/cboard/starter.jsp";
	});
	
	$(".btn_start_analytics").on("click", function() {
		
		var apiCode = sv.slctApi;
		
		if (!apiCode) return false;
		
		var param = getApiParam();
		if (!param) return false;
		
		$("body").loading();
		
		$.ajax({
			url: SDII.Url + "/ga/call_general_analytics_api",
			type: 'post',
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			data: JSON.stringify(param),
			success: function(result) {
				
				var param = getApiParam();
				if (!param) return false;
				
				$("body").loading("stop");

			  	if (!result.isSuccess) {
			  		alert("데이터 요청에 실패하였습니다.");
			  		return false;
			  	}
	
			  	if (sv.slctApi === "A001") {
				  	$(".docs_list_conatiner").show();
					onDrawPatternSearchRes(result);
			  	} else if (sv.slctApi === "A002") { // 형태소분석
					onDrawA002(result);
			  	} else if (sv.slctApi === "A003") { // 키워드빈도
			  		onDrawA003(result);
			  	} else if (sv.slctApi === "A008") { // 키워드빈도
			  		var data = JSON.parse(result.data);
			  		debugger;
			  	} else {
			  		console.log(JSON.parse(result.data));
			  	}
			  	return true;
			},
			error: function(request, status, error) {
				$("body").loading("stop");
				console.log("code: " + request.status + "\nerror: " + error);
				alert("데이터 요청에 실패하였습니다..");
			}
		});
		
		return true;
	});
	
	return true;
}

$(document).ready(function() {
	//$("body").loading({ message: '데이터 조회중 입니다'});
	$(".flatpickr").flatpickr({locale : "ko", wrap: true});

	var tempDef = moment(new Date()),
			to   = "", from = "";

	to   = tempDef.format("YYYY-MM-DD");
	from = tempDef.subtract(7, "days").format("YYYY-MM-DD");
	$(".flatpickr.detl_pickr")[0]._flatpickr.setDate(from);
	$(".flatpickr.detl_pickr")[1]._flatpickr.setDate(to);

	$(".flatpickr.detl_pickr")[0]._flatpickr.setDate("2018-10-01");
	$(".flatpickr.detl_pickr")[1]._flatpickr.setDate("2018-10-15");

	onInitDetlSearch();

}); //document ready end

})(); //function end
