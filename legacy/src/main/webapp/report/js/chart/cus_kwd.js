//daum report: network chart

$(document).ready(function() {
	
(function(global) {
	
	var cusKwdAssoNetwork = function(sv) {
		this.chartData = {};
		this.data = [];
		this.sv   = sv || 10;
		this.chartObj = null;
	};
	
	cusKwdAssoNetwork.prototype = {

		setData: function(kwd_asso_data) {
			
			$("#network").html("");
			
			if (!kwd_asso_data.data || !kwd_asso_data.data.length) {
				return this;
			}

			var t = kwd_asso_data.data;
			var n = {};
			var r = [];
			
			var i = 0;
			//데이터 중복처리
			for (i = 0; i < t.length; i++) {
				if (!n[t[i].kwdB]) n[t[i].kwdB] = [];
				n[t[i].kwdB].push(t[i]);
			}

			//합계처리
			for (var el in n) {
				var nt  = {};
				var sum = 0;
				for (i = 0; i < n[el].length; i++) {	
					sum += n[el][i].docCntBoth;
				}
				r.push({title: n[el][0].kwdB, value: sum});
			}

			//오름차순 정렬하기
			r = r.sort(function(a, b) { 
			    return b['value'] - a['value'];
			});
			
			//d3 chart를 위한 데이터 세팅
			this.temp = {
					data: {
						nodes:[{ID: 1, LABEL: $(".search_box_inp").val().trim(), COLORVALUE: "blue", SIZEVALUE: 10000}],
						links: []
					}
			}
			
			for(i = 0; i < r.length; i++) {
				var id = "1-"+(i+3);
				this.temp.data.nodes.push({ID: id, LABEL: r[i].title, COLOVALUE: "red", SIZEVALUE: r[i].value});
				this.temp.data.links.push({FROMID: 1, TOID: id, STYLE: "solid"});
			}
			
			this.data = r; 
			cusKwdAssoNetwork.rawData = r;
			this.chartData = this.temp;
		
			return this;
		},
		
		onDrawChart: function() {
			var that = this;
			if (!that.data || that.data.length === 0) {
				$(".cus_network_chart_title").html("\"" + $(".search_box_inp").val() + "\"의 연관어가 검색되지 않았습니다.");
				$("#network").html("");
				$("#sliderBarValueAfterEvent").html("");
				$('#sliderBar').off();
				$('#sliderBar').hide();
				return false;
			}
			
			$('#sliderBar').show();
			//1. 슬라이드 바 그리기
			var slider = document.getElementById("sliderBar");
			var curSv  = this.sv;
			//slider.innerHTML = "<input type='range' min='1' max='20' value='" + curSv + "' class='slider' id='myRange' style='width:70%; margin-left: 60px;'><div class='row container'><span id='demo' style='font-weight:bold; color: black; margin-left: 230px;'></span></div>";
			slider.innerHTML = "<div style='text-align: center;'><input type='range' min='1' max='20' value='" + curSv + "' class='slider' id='myRange' style='width: 300px;'></div><div><div id='demo' style='font-weight:bold; color: black; text-align: center; width: 300px; font-size: 12px;' class='d-flex justify-content-between mx-auto'></div></div>";
			
			slider = document.getElementById("myRange");
			var output = document.getElementById("demo");
			output.innerHTML = "<div>" + 1 + "</div><div>" + 10 + "</div><div>" + 20 + "</div>";
			
			slider.oninput = function() {
				output.innerHTML = this.value;
			}

			var chartOpts = {
					minNodeRadius: 10,
					maxNodeRadius: 45,
					colorScheme: "green",
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
					gridSize: 400,
					linkDistance: 80,
					showLinkDirection: false,
					showSelfLinks: false,
					selfLinkDistance: 8,
					useDomParentWidth: true,
					showBorder: false,
					showLegend: false,
					lassoMode: false, //true,
					zoomMode: true,
					minZoomFactor: 1,
					maxZoomFactor: 3,
					charge: -1500,
					gravity: 0.1,
					//dragMode: true
			};
			
			this.chartData.data.nodes=this.temp.data.nodes.slice(0, Number(slider.value) + 1);
			this.chartData.data.links=this.temp.data.links.slice(0, Number(slider.value) + 1);
			
			this.chartObj = netGobrechtsD3Force('network', chartOpts);
			this.chartObj.alignFixedNodesToGrid(true).height(450).render(this.chartData);
			window.chartObj = this.chartObj;  
			
		    $("#sliderBar").off();
		    $('#sliderBar').on('mouseup', '#myRange', function(ch) {

				var rawData = SDII.Chart.cusKwdAssoNetwork.rawData;

				var newData = {
						data: {
							nodes:[{ID: 1, LABEL: $(".search_box_inp").val().trim(), COLORVALUE: "20", SIZEVALUE: 10000}],
							links: []
						}
				}
				for(var i = 0; i < rawData.length; i++) {
					var id = "1-"+(i+3);
					newData.data.nodes.push({ID: id, LABEL: rawData[i].title, COLOVALUE: "20", SIZEVALUE: rawData[i].value});
					newData.data.links.push({FROMID: 1, TOID: id, STYLE: "solid"});
				}
				
				newData.data.nodes=newData.data.nodes.slice(0, (Number($('#myRange').val())+1));
				newData.data.links=newData.data.links.slice(0, (Number($('#myRange').val())+1));
				
				that.chartObj = null
				
				that.chartObj = netGobrechtsD3Force('network', chartOpts);
				that.chartObj.alignFixedNodesToGrid(true).render(newData);
				window.chartObj = that.chartObj;
				
				//barchart overflow:scroll control	
				if(Number($('#myRange').val()) <= 10) {
					$('html').addClass('hide-scrollbar');
					$('#myScroll').css('overflow', 'hidden');
				} else {
					$('html').removeClass('hide-scrollbar');
					$('html').addClass('show-scrollbar');
					$('#myScroll').css('overflow', 'scroll');
				}
				
				//myScroll css control
				var val = ($(this).val() - $(this).attr('min')) / ($(this).attr('max') - $(this).attr('min'));
				    
			    $(this).css('background-image',
			                '-webkit-gradient(linear, left top, right top, '
			                + 'color-stop(' + val + ', #0066ff), '
			                + 'color-stop(' + val + ', #DCDCDC)'
			                + ')'
			    );// END-myScroll css control
			    
			    //slider bar setting
			    var output = document.getElementById("demo");
				output.innerHTML = "<div>" + 1 + "</div><div>" + 20 + "</div>";
				
				var outputAfterEvent = document.getElementById("sliderBarValueAfterEvent");
				outputAfterEvent.innerHTML = "<div class='outputAfterEvent' style='font-size: 12px; text-align: center; font-weight: bold;'>"+$('#myRange').val()+"</div>";
				
				new SDII.Chart.cusKwdAssoChart().setData(SDII.Chart.cusKwdAssoChart.rawData).onDrawChart();
	    	
		    });/*.bind(curS));*/
	    
	    	return true;
		}
	};
	
	window.SDII.Chart.cusKwdAssoNetwork = window.SDII.Chart.cusKwdAssoNetwork || cusKwdAssoNetwork;
	
})(window);
});