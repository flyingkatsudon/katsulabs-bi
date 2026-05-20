$(document).ready(function() {
	
(function(global) {
	
	var kwdAssoTimeTable = function(cnt, p, usePopup) {
		this.container = p;
		this.rowMax    = cnt || 10;
		this.tableMax  = 5;
		this.dataSet   = {};
		this.usePop    = usePopup || false;

		if (this.rowMax > 25) this.rowMax = 25;
	};
	
	kwdAssoTimeTable.prototype = {

		setData: function(kwd_asso_time_data) {

			var t      = kwd_asso_time_data.data,
				t2     = {},
				newObj = {},
				nData  = {},
				lastD  = "",
				i      = 0;

			// 중복제거 filtering
			for (i = 0; i < t.length; i++) {
				if (t2[t[i].kwdB]) continue;
				t2[t[i].kwdB] = t[i];
			}

			t = [];
			for (var el in t2) t.push(t2[el]);
			lastD = moment(new Date(t[t.length - 1].docDate)).format("YYYY-MM-DD");
			nData[lastD] = [];
			
			for (i = 1; i < 5; i++) {
				nData[moment(lastD).subtract(i * 7, "days").format("YYYY-MM-DD")] = [];
			}

			for (i = t.length - 1; i >= 0; i--) {
			  var nDate = moment(new Date(t[i].docDate)).format("YYYY-MM-DD");
			  if (!nData[nDate]) nData[nDate] = [];
			  nData[nDate].push(t[i]);
			}
	
			this.dataSet = nData;
			return this;
		},
		
		onDrawChart: function() {
			
			var that      = this,
				p         = that.container,
				newObj    = that.dataSet,
				//tableCnt  = Object.keys(newObj).length,
				drawCnt   = 0,
				row       = that.rowMax,
				tbCnt     = that.tableMax,
				//tableRank = $("#tableRank"),
				i         = 0;

			var wrapHtml = $("<div class='d-flex asso_table_conatiner'></div>");
			var rankHtml = $("<div class='rank_wrap'><table class='table' id='asso_rank'></table></div>");	
			wrapHtml.append(rankHtml);
			for (i = 1; i < 6; i++) {
				var tbHtml = $("<div class='asso_table_wrap'><table id='table" + i +"' class='table asso_table'></table></div>");
				wrapHtml.append(tbHtml);
			}
			p.append(wrapHtml);

			$("#asso_rank").append("<tr><th>Rank</th></tr>");

			for (var el in newObj) {

				if (drawCnt === tbCnt) break;
				var padCnt  = 0;
				var curList = newObj[el];
				var newList = curList.sort(function(a, b) { 
					return b['docCntBoth'] - a['docCntBoth'];
				});

				if (newList.length < row) {
					padCnt  = row - newList.length;
					newList = newList.concat(new Array(padCnt).fill({dummy:true}));
				}

				var curChart = $("#table" + (6 - (drawCnt + 1))),
					  curWeek  = Math.ceil(moment(el).date() / 7),
					  curMont  = moment(el).month() + 1;

				curChart.append("<tr><th colspan='2' class='text-center'>" + curMont + "월 " + curWeek + "주차</th></tr>");

				for (var j = 0; j < row; j++) {

					var curRow = newList[j],
							kwd    = curRow.kwdB || "-",
							dCnt   = curRow.docCntBoth || "-";
					curChart.append("<tr row-idx=" + j + "><td class='title td1'>" + kwd + "</td><td class='text-right'>" + dCnt + "</td></tr>");	

					if (drawCnt === 0)
						$("#asso_rank").append("<tr><td class='title text-center'>" + (j + 1) + "</td>");
				}

				drawCnt++;
			}

			$(".td1").on("click", function() {
				
				if (!$(this).parent().attr("row-idx")) return false;

				var //rowIdx = parseInt($(this).parent().attr("row-idx")) + 1,
					curKey = $(this).text();

				for (i = 0 ; i < tbCnt; i ++) {
					var curChild = $("#table" + (i + 1)).children();
					for (var j = 1; j < curChild.length; j++) {
						$(curChild[j]).css("color", "#000").css("background-color", "#fff").css("font-weight", "normal");
						$(curChild[j]).next().css("color", "#000").css("background-color", "#fff").css("font-weight", "normal");

						if (curKey !== "-" && curKey === $(curChild[j]).find(".td1").text()) {
							$(curChild[j]).css("color", "#fff").css("background-color", "#3c81f0").css("font-weight", "bold");
							$(curChild[j]).next().css("color", "#fff").css("background-color", "#3c81f0").css("font-weight", "bold");
						}
					}
					//var tgtRow = $(curChild.get(rowIdx));
					//if ($("td:first", tgtRow).text() === "-") continue;
					//tgtRow.css("background-color", "#91c7ae");
				}

				if (curKey === "-") return false;
				//console.log(curKey);
				sv.curAssoKey = curKey;
				return true;
			});

			if (!that.usePop) return false;
			$(".btn_search_kwd").on("click", function() {

				if (!sv.curAssoKey || sv.curAssoKey === "-") {
					alert("검색할 키워드를 테이블에서 선택해주세요");
					return false;
				}

				$(".temp_wrap").hide();
				$("body").loading();

				var t = {
			  	query: $(".search_box_inp").val() + " " + sv.curAssoKey,
			  	startDate: $(".flatpickr")[0]._flatpickr.input.value,
			  	endDate  : $(".flatpickr")[1]._flatpickr.input.value,
			  	businessCode : sv.groupCd,
			  	//fid : sv.reqMap[sv.category].doc
			  };

				//if (sv.category === "2") t["domainOpt"] = "03";

				$.ajax({
					url: SDII.Url + "/report/get_full_src",
					contentType: "application/json;charset=utf-8",
				    type: "post",
				    dataType: "json",
				    data: JSON.stringify(t),
					success: function (data) {

				    	$("body").loading("stop");
	
				    	if (!data.isSuccess) {
				        alert("데이터 요청에 실패하였습니다.");
				        return false;
				      }

				      if (!data.hasDocs) {
				      	$(".doc_area").html("조회된 결과가 없습니다.");
				      	return false;
				      }
	
				      $(".cus_kwd_asso_time_table_wrap").hide();
				      $(".cus_kwd_asso_time_kwd_search").show();

				      var docs = JSON.parse(data.data);

				      //console.log(docs);

				      if (!docs.response || !docs.response.docs || docs.response.docs.length === 0) {
				      	// 2019.01.07
				    	// '돌아가기'버튼 추가
				    	$(".cus_kwd_asso_time_kwd_search").html(
			    			"<div class='d-flex justify-content-between'>" + 
			      				"<div class='doc_search_title'>조회된 결과가 없습니다.</div>" +
							"<div class='btn_doc_list_close'>[돌아가기]</div>" + 
						"</div>");
				      	
						// 임시로 close 기능 붙임 - 추후 수정 필요
						$(".btn_doc_list_close").on("click", function() {
							$(".temp_wrap").show();
							$(".cus_kwd_asso_time_table_wrap").show();
							$(".cus_kwd_asso_time_kwd_search").hide();
							$(document).scrollTop(1040)
						});
						return false;
			      }
			      
			      docs = docs.response.docs;

					$(".cus_kwd_asso_time_kwd_search").html(
							"<div class='d-flex justify-content-between'>" + 
								"<div class='doc_search_title'>\"" + t.query + "\"에 대한 원문<span class='ea'>조회중</span></div>" +
								"<div class='btn_doc_list_close'>[돌아가기]</div>" + 
							"</div>"
						).append("<div class='doc_area'></div>");
					
					$(".ea").text("(" + docs.length + ")건");

			      for (var i = docs.length - 1; i > 0; i--) {
			      	var wrap = $("<div class='row_wrap d-flex align-items-center'>");

			      	var tt = docs[i].title || "";
							var cont   = docs[i].content;
			      	var date   = docs[i].date  || docs[i].documentDate;
			      	var press  = docs[i].press || docs[i].domain;
			      	var url    = docs[i].url;
			      	var imgUrl = docs[i].img || docs[i].profileImageUrl;

			      	if (!tt && cont) tt = cont.substr(0, 100);
			      	tt = tt.replace(/<mark>|<\/mark>/gi, "");

			      	if (date && date.length === 12)
								date = moment(date, "YYYYMMDDkkmmss", false).format("YYYY-MM-DD kk:mm:ss");
							
			      	if (tt && tt.length > 100) tt = tt.substr(0, 100) + "...";

			      	if (cont) cont = cont.replace(/<mark>|<\/mark>/gi, "");
			      	if (cont.length > 300) cont = cont.substr(0, 300) + "...";

			      	var colWrap   = $("<div class='col_wrap'></div>");
			      	var docsTitle = $("<div class='docs_title' url='" + url + "'></div>");
			      	var docsCont  = $("<div class='docs_cont'>" + cont + "<br/> " + press + " (" + date + ")</div>");
			      	docsTitle.text(tt);
			      	colWrap.append(docsTitle).append(docsCont);

			      	var imgWrap   = $("<div class='img_wrap' url='" + url + "'>");
			      	imgWrap.append(imgUrl ? "<img src='" + imgUrl + "'/>" : "");

			      	wrap.append(colWrap).append(imgWrap);

			      	/*
			      	wrap.append(
			      		"<div class='col_wrap'>" +
			      			"<div class='docs_title' contno='" + i + "'>" + 
			      				"<span>" + tt +  "</span>" +
			      			"</div>" +
			      			"<div class='docs_cont'>" + press + " (" + date + ")</div>" +
		      			"</div>" +
		      			"<div class='img_wrap' url='" + url + "'>" + //"' contno='" + i + "'>" +
									(imgUrl ? "<img src='" + imgUrl + "'/>" : "") + 
		      			"</div>"
			      	);
			      	*/
							$(".doc_area").append(wrap).append("<div class='row_wrap_bdr'></div>");
			      }

			      $(".docs_title, .img_wrap").on("click", function() {
			      	window.open($(this).attr("url"), "", "width=1200,height=800");
			      });
			      
			      $(".btn_doc_list_close").on("click", function() {
			      	$(".temp_wrap").show();
				      $(".cus_kwd_asso_time_table_wrap").show();
			      	$(".cus_kwd_asso_time_kwd_search").hide();
			      	$(document).scrollTop(1040)

			      });
			      //$(".doc_area").html(JSON.parse(data.data));
			      return true;
			    },

			    error: function (request, status, error) {
			    	console.log('code: '+request.status+"\n"+'message: '+request.responseText+"\n"+'error: '+error);
				    alert("데이터 요청에 실패하였습니다..");
				    return false;
			  	}
				});
				return true;
			});
			
			return true;
		}
	};
	window.SDII.Chart.kwdAssoTimeTable = window.SDII.Chart.kwdAssoTimeTable || kwdAssoTimeTable;
	
})(window);
});