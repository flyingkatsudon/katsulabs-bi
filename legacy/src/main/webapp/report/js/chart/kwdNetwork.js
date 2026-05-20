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

	var kwdNetwork = function() {
		this.chartData = null;
		this.chartObj  = null;
	}

	kwdNetwork.prototype = {

		setData: function(ajaxData, cName, top3Kwd) {

			if (!cName) cName = " ";

			this.cName = cName;

			var d  = ajaxData.data,
		      	n  = {},
		      	n2 = [],
		      	t3 = top3Kwd.split(","),
		      	r  = {
					data: {
						nodes : [
			      	  	/*
			      	  		{
											ID: 1,
							        LABEL: cName,
							        COLORVALUE:"10",
							        SIZEVALUE: 3000,
			      	  		}
			      	  	*/
						], 
						links : []
					}
				}, i = 0;
		
			if (sv.category === "5") {
				r.data.nodes.push({
					ID: 1,
			        LABEL: cName,
			        COLORVALUE:"#83c4ff",
			        SIZEVALUE: 8000,
				});
			}
		
	     	var nt3 = [];
			t3.filter(function(item) {
				if ($.inArray(item, nt3) === -1) nt3.push(item);
			});
			
	        for (i = 0; i < nt3.length; i++) {
	        	for (var j = 0; j < d.length; j++) {
	        		if (nt3[i] !== d[j].kwdA) continue;
	        		n2.push(d[j]);
	        	}
	        }

	        for (i = 0; i < n2.length; i++) {
	        	if (!n[n2[i].kwdA]) n[n2[i].kwdA] = []; 
	        	n[n2[i].kwdA].push(n2[i]);
	        }

		    var cateCnt = 2;
		    var ca = [];
		    var c1 = ["#01579b", "#26bacb", "#a07be6"];
		    var c2 = ["#83c4ff", "#b0e5ee", "#efcbea"];

		    for (var el in n) {
	    		r.data.nodes.push({
					ID: cateCnt,
			        LABEL: n[el][0].kwdA,// + "(" + (cateCnt - 1) + ")",
			        COLORVALUE: c1[cateCnt - 2],
			        SIZEVALUE: 10000 - ((cateCnt - 2)  * 4000),
			        CHANNEL : (sv.category === "2") ? "SNS"     : "",
			        SITE    : (sv.category === "2") ? "twitter" : ""
	    		});

	    		ca.push(n[el][0].kwdA);

				r.data.links.push({
			        FROMID: 1,
			        TOID: cateCnt,
			        STYLE:"solid"
	    		});
      	
				for (i = 0; i < n[el].length; i++) {

					if (!n[el][i].docCntBoth) continue;

		      		r.data.nodes.push({
						ID: cateCnt + "-" + (i + 3),
				        LABEL: n[el][i].kwdB,
				        COLORVALUE: c2[cateCnt - 2],
				        SIZEVALUE: n[el][i].docCntBoth,
				        DATE: n[el][i].docDate,       
				        CHANNEL: (sv.category === "2") ? n[el][i].channel : "",
				        SITE:    (sv.category === "2") ? n[el][i].site    : ""
		      		});
		      		
		      		r.data.links.push({
				        FROMID: cateCnt,
				        TOID: cateCnt + "-" + (i + 3),
				        STYLE:"solid"
		      		});
		      	}
				cateCnt++;
		    }
			//console.log(n);
			  
			this.chartData = r;
			this.cateList  = ca;
			this.top3Kwd   = nt3.join(", ");
			
		    return this;
		},

		onDrawChart: function() {

			$("#chart_network").html("");

			var that      = this,
				chartOpts = {};

			chartOpts = {
				minNodeRadius: 5,
				maxNodeRadius: 30,
				//colorScheme: "color20c",
				colorScheme: "direct",
				pinMode: false, //true,
				nodeEventToStopPinMode: "true",
				nodeEventToOpenLink: "click",
				nodeLinkTarget: "none",
				wrapLabels: true,
				wrappedLabelLineHeight: 1,
				labelDistance: 4,
				preventLabelOverlappingOnForceEnd: true,
				showTooltips: false,
				tooltipPosition: "node",
				gridSize: 80,
				linkDistance: 100,
				showLinkDirection: false,
				showSelfLinks: false,
				selfLinkDistance: 8,
				useDomParentWidth: true,
				showBorder: false,
				showLegend: false,
				lassoMode: false, //true,
				zoomMode: true,
				minZoomFactor: 0,
				maxZoomFactor: 2,
				charge: -200,
				gravity: 0.1,
				alignFixedNodesToGrid: true,
				height: "446"
			};

			$("#chart_network")
				.append("<div id='chart_network_title'></div>")
				.append("<div id='chart_network_chart'></div>");

			try {
				that.chartObj = netGobrechtsD3Force('chart_network_chart', chartOpts);	
			} catch (e) {
				console.log(e);
			}
			
			that.chartObj.alignFixedNodesToGrid(true).render(this.chartData);
			
			var relTitle = that.top3Kwd + "에 대한 연관어";
			if (sv.category === "5") relTitle = that.cName + " / " + relTitle;
			$("#chart_network_title").text(relTitle);

			$("text", "#chart_network_chart").css("font-size", "14px");
			$("text:first", "#chart_network_chart").css("font-size", "18px");

			that.chartObj.onNodeClickFunction(function(cateData, cName, evt, data) {

				$("body").loading();

				if (!data || !data.ID) {
					$("body").loading("stop");
					return false;
				}
				
				var curKey = data.LABEL,
					curId  = data.ID;

				if (curId === 1) {
					$("body").loading("stop");
					return false;
				}
				
				var t = "";
				if (typeof (curId) !== "number" && curId.indexOf("-") > -1) {
					t = cateData[parseInt(curId.split("-")[0]) - 2];
					curKey = t + " " + curKey;
					curKey = cName ? cName + " " + curKey : curKey;

				} else {

					curKey = cName + " " + curKey;
				}

				var srcText = "";
				if (data.SITE) srcText = " / " + data.SITE;
				if (sv.category === "2" && typeof (curId) === "number") srcText = "";
					
				$(".chart_doc_list")
					.html("<div class='doc_search_title'>\"" + curKey + "\"에 대한 원문<span class='ea'>조회중" + srcText + "</span></div>")
					.append("<div class='doc_area'></div>");

				console.log(data);
				t = {
				  	query: curKey,
				  	//startDate: moment(data.DATE).format("YYYY-MM-DD"),
				  	//endDate  : moment(data.DATE).format("YYYY-MM-DD"),
				  	startDate: $(".flatpickr")[0]._flatpickr.input.value,
				  	endDate: $(".flatpickr")[1]._flatpickr.input.value,
				  	businessCode : sv.groupCd,
				  	fid : sv.reqMap[sv.category].doc || "",
				    cnt: 50 // 검색엔진 limitCnt 풀라는 고책임 요청 최소 50
				};
				
				var siteName = data.SITE;
				t["domainOpt"] = "01";
				if (data.CHANNEL && data.CHANNEL === "SNS") {
					if (siteName === "twitter")   t["domainOpt"] = "03";
					if (siteName === "instagram") t["domainOpt"] = "02";
				}
				if (sv.category === "2" && typeof (curId) === "number") t["domainOpt"] = "03";
				
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

					    var docs = JSON.parse(data.data);
					    console.log(t);
					    if (!docs.response || !docs.response.docs || docs.response.docs.length === 0) {
					    	$(".doc_area").html("조회된 결과가 없습니다.");
					    	return false;
					    }

					    docs = docs.response.docs;
			      
					    $(".ea").text("(" + (docs.length - 1) + ")건 " + srcText);

					    //for (var i = docs.length - 1; i > 0; i--) {
					    for (var i = 0; i < docs.length; i++) {
					    	
					    	var wrap = $("<div class='row_wrap d-flex align-items-center'>");
					      	var tt     = docs[i].title || "";
							//var cont   = docs[i].content;
					      	var date   = docs[i].date  || docs[i].documentDate;
					      	var press  = docs[i].press || docs[i].domain;
					      	var url    = docs[i].url;
					      	var imgUrl = docs[i].img || docs[i].profileImageUrl;

					      	if (!tt && cont) tt = cont.substr(0, 20);
					      	tt = tt.replace(/<mark>|<\/mark>/gi, "");

					      	if (date && date.length >= 12) {
					      		var dt1 = [date.substr(0, 4), date.substr(4, 2), date.substr(6, 2)].join("-");
					      		var dt2 = [date.substr(8, 2), date.substr(10, 2)].join(":");
					      		date = dt1 + " " + dt2;
					      	} 
					      	if (tt && tt.length > 20) tt = tt.substr(0, 20) + "...";
					      	//if (cont.length > 30) cont = cont.substr(0, 30) + "...";
					      	
					      	var colWrap   = $("<div class='col_wrap'></div>");
					      	var docsTitle = $("<div class='docs_title'></div>");
					      	var docsCont  = $("<div class='docs_cont'>" + press + " (" + date + ")</div>");
					      	docsTitle.text(tt);
					      	var docWord   = $("<div class='docs_word'>&nbsp;- 포함단어 : " + docs[i].mKeywords + "</div>");
					      	
					      	colWrap.append(docsTitle).append(docsCont).append(docWord);
					      	
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

							//$("div[contno='" + i + "']")

							var hoverTitle = sv.category === "2" ? tt : docs[i].title;

							docsTitle
								.attr("data-container", "body")
			  				.attr("data-trigger", "hover")
								.attr("data-toggle", "popover")
								.attr("data-placement", "top")
								.attr("data-content", hoverTitle.replace(/<mark>|<\/mark>/gi, ""))
								.attr("data-url", imgUrl ? imgUrl : "");
							
							//$("div[contno='" + i + "']")
							docsTitle
								.on('mouseover',function(e){
	    						e.preventDefault();
	    						$(this).css("cursor", "pointer");
	  						}).popover();

		  					docsTitle.on("click", function() {
		    					event.preventDefault();
		    					$(this).parent().next().click();
		  					});
					    }

						$(".img_wrap").on("click", function() {
							event.preventDefault();
							var url = $(this).attr("url");
							if (!url) return false;
							window.open(url, "", "width=1200,height=800");
							return true;
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
			}.bind(that.chartObj, that.cateList, that.cName));
			
			$("body").loading("stop");
			
			return true;
		}
	}

	global.SDII.Chart.kwdNetwork = global.SDII.Chart.kwdNetwork || kwdNetwork;

})(window);

});