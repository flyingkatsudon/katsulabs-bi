$(document).ready(function() {
	
(function(global) {
	
	var cusKwdAssoChart = function() {
		this.xArr = [];
		this.yArr = [];
		this.avgArr = [];
	};
	
	cusKwdAssoChart.prototype = {
		setData: function(kwd_asso_data) {

			if (!kwd_asso_data || !kwd_asso_data.data || kwd_asso_data.data.length === 0) {
				return this;
			}
			var dataset = [];
			
			var xAxis=[]; var tempAvg=[]; var xArr=[];
			var yAxis=[]; var yArr=[]; var avgArr = [];

			var t = kwd_asso_data.data;
			var n = {};
			
			var i = 0;
			//중복 연관어 골라서 n배열에 저장
			for (i = 0; i < t.length; i++) { 
				if (!n[t[i].kwdB]) n[t[i].kwdB] = [];
				n[t[i].kwdB].push(t[i]);
			}
			
			for (var el in n) {
				var nt  = {};
				var freqSum = 0;
				var corrValueSum = 0;
				var corrValueAvg = 0;
					
				yAxis.push(n[el][0].kwdB);

				for (i = 0; i < n[el].length; i++) {	
					freqSum += n[el][i].docCntBoth;
					corrValueSum += n[el][i].corrValue;
					
					if(i === n[el].length-1) {
						corrValueAvg = corrValueSum/n[el].length;
					}
					
				}
				xAxis.push(freqSum);
				tempAvg.push(corrValueAvg);
			}
			
			for(i = 0; i < xAxis.length; i++ ) {
				dataset.push({kwdB: yAxis[i], docCntBoth: xAxis[i], corrValue: tempAvg[i]});
			}
			
			//오름차순 정렬하기
			dataset = dataset.sort(function(a, b) { 
			    return b['docCntBoth'] - a['docCntBoth'];
			});
			
			for(i = 0; i < dataset.length; i++) {
				xArr.push(dataset[i].docCntBoth);
				yArr.push(dataset[i].kwdB);
				avgArr.push(dataset[i].corrValue.toFixed(2));
			}
			
			xArr = xArr.slice(0, ($('#myRange').val()));
			yArr = yArr.slice(0, ($('#myRange').val()));
			avgArr = avgArr.slice(0, ($('#myRange').val()));
			
			cusKwdAssoChart.rawData = kwd_asso_data;
			this.xArr = xArr; 
			this.yArr = yArr; 
			this.avgArr = avgArr;
			
			return this;
		},
		
		onDrawChart: function() {
			
			var that = this;
			var xArr = that.xArr;
			var yArr = that.yArr;
			//var avgArr = that.avgArr;
				
			if (!xArr || xArr.length === 0) {
				$(".cus_bar_chart_title").html("\"" + $(".search_box_inp").val() + "\"에 동시출현 빈도수가 검색되지 않았습니다.");
				//$(".barChart").html("");
				$("#barChart").html("");
				return false;
			}

			var chartOptBase = {
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'shadow'
		        },
		        //formatter: '동시출현빈도수: {c0}'
		        formatter: function (params, ticket, callback){
		        	var res = ""; //"연관어: " + params[0].name + "<br/>"; 
							for (var i = 0, l = params.length; i < l; i++) {
								//res += '동시출현빈도수: ' + params[i].value + '건 <br /> 연관도 : ' + avgArr[params[i].dataIndex] + '%';  
								res += "동시출현빈도수: " + params[i].value + " (건)";
		          }
		          setTimeout(function() { callback(ticket, res)}, 1);
		        	return '로딩';
		        }	        	
		    },
		    /*legend: {
		        data: ['동시출현빈도'],
		        align: 'right'
		    },*/
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true,
		        height: 470,
		        width: "90%",
		        top: 30
		    },
		    xAxis: {
		        /*type: 'value',
		        min: 0,
		        max: Math.floor((xArr[0] * 1.2)/100) * 100,
		        axisLabel: {rotate: 50, interval: 0}*/
		    	type: 'value'
	        },
		    yAxis: {
		        type: 'category',
		        inverse: true,
		        data: yArr
		    },
		    series: [
	        {
	          name: '동시출현빈도',
	          type: 'bar',
	          label: {
	          	normal: {
	          		position: 'right',
	          		show: true
	          	}
	          },
	          data: xArr,
	          itemStyle:{color:'#3c81f0'},
	          barWidth: "20"
	        }
		    ]
			};
			//control height
			/*
			chartOptBase.grid.height = 400 + (($('#myRange').val()-10)*40);
			*/
			var parent  = document.getElementById("barChart");
			$(parent).attr("_echarts_instance_", "");
			var myBarChart = echarts.init(parent);
			myBarChart.setOption(chartOptBase);
			
			return true;
		}
	};
	
	window.SDII.Chart.cusKwdAssoChart = window.SDII.Chart.cusKwdAssoChart || cusKwdAssoChart;
	
})(window);
});
