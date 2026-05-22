$(document).ready(function() {
	
(function(global) {
	
	var posNegAssoChart = function(cnt, p, kwdSet) {
		this.container = p;
		this.rowMax    = cnt || 10;
		this.tableMax  = 5;
		this.dataSet   = {};
		this.kwdSet    = kwdSet || "";
		this.pMax      = 0;
		this.nMin      = 0;
		p.html("");

	};
	
	posNegAssoChart.prototype = {

		setData: function(pos_neg_data, kwd) {

			var data     = pos_neg_data.data,
					newObj   = {},
					nData    = {},

					posxAxis = [],
					posyAxis = [],
					negxAxis = [],
					negyAxis = [];
			
			//var kwds = this.kwdSet.split(",");
			//var kwdA = kwds[0];
			//var kwdB = kwds[0];
			var pMax = 0;
			var nMin = 0;

			for (var i = 0; i < data.length; i++) {

				if (data[i].sentiFlag === "POS") {
					posxAxis.push(data[i].kwdSentiValue);
					posyAxis.push(data[i].kwd);
					if (data[i].kwdSentiValue > pMax) pMax = data[i].kwdSentiValue;

				} else if (data[i].sentiFlag === "NEG") {
					negxAxis.push(data[i].kwdSentiValue);
					negyAxis.push(data[i].kwd);	
					if (data[i].kwdSentiValue < nMin) nMin = data[i].kwdSentiValue;
				}
			}

			negxAxis.reverse();
			negyAxis.reverse();

			if (posxAxis.length > this.rowMax) {
				posxAxis = posxAxis.slice(0, 7);
				posyAxis = posyAxis.slice(0, 7);
			}

			if (negxAxis.length > this.rowMax) {
				negxAxis = negxAxis.slice(0, this.rowMax);
				negyAxis = negyAxis.slice(0, this.rowMax);
			}

			if (posxAxis.length < this.rowMax) {
				posxAxis = posxAxis.concat(new Array(this.rowMax - posxAxis.length).fill(0));
				posyAxis = posyAxis.concat(new Array(this.rowMax - posyAxis.length).fill("-"));
			}

			if (negxAxis.length < this.rowMax) {
				negxAxis = negxAxis.concat(new Array(this.rowMax - negxAxis.length).fill(0));
				negyAxis = negyAxis.concat(new Array(this.rowMax - negyAxis.length).fill("-"));
			}

			this.dataSet = {
				pos : {title: "긍정", xAxis: posxAxis, yAxis: posyAxis},
				neg : {title: "부정", xAxis: negxAxis, yAxis: negyAxis}
			};

			this.pMax = pMax;
			this.nMin = nMin;
 			return this;
		},
		
		onDrawChart: function() {
			
			var that      = this,
					p         = that.container,
					data      = that.dataSet,
					sKwd      = that.kwdSet.split(",");
			//var titleHtml = $("<div class='chart_trend_sub_title'>" + sKwd[0] + " " + sKwd[1] + "에 대한 긍/부정연관어</div>");
			var titleHtml = $("<div class='chart_trend_sub_title'>" + sKwd[0] + "에 대한 긍/부정연관어</div>");
			var wrapHtml  = $("<div class='d-flex pos_neg_chart_conatiner justify-content-center'></div>");
			var negHtml   = $("<div id='neg_asso_chart' class='neg_asso_chart'></div>");	
			var posHtml   = $("<div id='pos_asso_chart' class='pos_asso_chart'></div>");	
			wrapHtml.append(negHtml).append(posHtml);
			p.append(titleHtml).append(wrapHtml);	

			var posChart = echarts.init($("#pos_asso_chart").get(0));
			var negChart = echarts.init($("#neg_asso_chart").get(0));

			//차트 옵션설정
			var basicOption = {
					title: {
						text: null,
						textStyle: {
							fontSize:14,
							fontWeight: "normal"
						}
					},
					grid: {
				    left: '3%',
				    right: '4%',
				    bottom: '3%',
				    containLabel: true
					},
					xAxis: {
				    type: 'value',
				    boundaryGap: ['20%', '20%'],

				    inverse: null,
				    borderRadius: 5
					},
					yAxis: {
				    type: 'category',
				    data: [],
				    position: 'right',
				    inverse: true
					},
					series: [
				    {
				        name: null,
				        type: 'bar',
				        data: [],
				        itemStyle:{color:'green'}
				    }
					]
			};

			basicOption.title.text=data.pos.title;
			basicOption.xAxis.min = 0;
			basicOption.xAxis.max = 1;
			basicOption.yAxis.data=data.pos.yAxis;
			basicOption.yAxis.position= "right";
			basicOption.series[0].data=data.pos.xAxis;
			basicOption.series[0].itemStyle = {color:'#3c81f0'};
			posChart.setOption(basicOption);

			basicOption.title.text=data.neg.title;
			basicOption.xAxis.min = -1;
			basicOption.xAxis.max = 0;
			basicOption.yAxis.data=data.neg.yAxis;
			basicOption.yAxis.position= "left";
			basicOption.series[0].data=data.neg.xAxis;
			basicOption.series[0].itemStyle = {color:'#ff5663'};
			negChart.setOption(basicOption);
			
			return true;
		}
	};
	
	window.SDII.Chart.posNegAssoChart = window.SDII.Chart.posNegAssoChart || posNegAssoChart;
	
})(window);
});