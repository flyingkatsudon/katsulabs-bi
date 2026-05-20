//update on 11.01
$(document).ready(function() {
	
(function(global) {
	
	var cusTrendChart = function() {
		this.sourceArr = [];
	};
	
	cusTrendChart.prototype = {

		setData: function(trendDataset) {
			
			this.sourceArr = [];

			if (!trendDataset || !trendDataset.data || trendDataset.data.length === 0) {
				return this;
			}
			var sourceArr    = [];
			var dateArr      = ['buzzAmt'];
			var categoryArr1 = ['NEWS'];
			var categoryArr2 = ['SNS'];
			var categoryArr3 = ['COMMUNITY'];
			var categoryArr4 = ['BLOG'];
			//var categoryArr5 = ['ETC'];
			
			var i = 0;
			// data pre-process
			// 1. sort date
			trendDataset.data = trendDataset.data.sort(function(a, b) {return a['docDate'] - b['docDate'];});
			
			// 2. Timestamp --> date
			for (i = 0; i < trendDataset.data.length; i++) {
				trendDataset.data[i].docDate = moment(trendDataset.data[i].docDate).format("YY/MM/DD");
			}
			
			// 3. dateArr
			for (i = 0; i < trendDataset.data.length; i++) {
				if (!trendDataset.data[i] || !trendDataset.data[i].categoryCode) continue;
				if(i === 0) {
					dateArr.push(trendDataset.data[i].docDate);
				} else {
					if(trendDataset.data[i-1].docDate !== trendDataset.data[i].docDate) {
						dateArr.push(trendDataset.data[i].docDate);
					}
				}
			}
			
			// 4. categoryArr1,2,3,4,5
			for (i = 1; i < dateArr.length; i++) {
				//0을 집어넣음
				categoryArr1.push(0);
				categoryArr2.push(0);
				categoryArr3.push(0);
				categoryArr4.push(0);
				//categoryArr5.push(0);
				
				//그다음 값을 부여
				for(var j = 0; j < trendDataset.data.length; j++) {				
					if (!trendDataset.data[j] || !trendDataset.data[j].categoryCode) {
						continue;
					}
					
					if((trendDataset.data[j].categoryCode === "1") && (trendDataset.data[j].docDate === dateArr[i])) {
						categoryArr1[i] = trendDataset.data[j].docCntBoth;
					} else if((trendDataset.data[j].categoryCode === "2") && (trendDataset.data[j].docDate === dateArr[i])) {
						categoryArr2[i] = trendDataset.data[j].docCntBoth;
					} else if((trendDataset.data[j].categoryCode === "3") && (trendDataset.data[j].docDate === dateArr[i])) {
						categoryArr3[i] = trendDataset.data[j].docCntBoth;
					} else if((trendDataset.data[j].categoryCode === "4") && (trendDataset.data[j].docDate === dateArr[i])) {
						categoryArr4[i] = trendDataset.data[j].docCntBoth;
					}
				}
			}
			
			sourceArr.push(dateArr);
			sourceArr.push(categoryArr1);
			sourceArr.push(categoryArr2);
			sourceArr.push(categoryArr3);
			sourceArr.push(categoryArr4);
			//sourceArr.push(categoryArr5);
			
			this.sourceArr = sourceArr; 
			
			console.log(this.sourceArr);
			return this;
		},
		
		onDrawChart: function() {
			
			var that = this;
			
			$("#trend").attr("_echarts_instance_", "");
			
			if (!that.sourceArr || this.sourceArr.length === 0) {
				$("#trend").html("검색된 데이터가 없습니다.");
				return false;
			}
			
			var sourceArr = that.sourceArr;
			var trendChart = document.getElementById("trend");
			var myTrend = echarts.init(trendChart);
			
			setTimeout(function () {

			    var option = {
			        legend: {},
			        tooltip: {
		            trigger: 'axis',
		            showContent: true
			        },
			        dataset: {
	            	source: sourceArr
			        },
			        xAxis: {type: 'category'},
			        yAxis: {gridIndex: 0},
			        grid: {x: '5%', y: '7%', width: '63%', height: '77%'},
			        series: [
			            { type: "line", seriesLayoutBy: "row", color: "#83c4ff"},
			            { type: "line", seriesLayoutBy: "row", color: "#ff9c00"},
			            { type: "line", seriesLayoutBy: "row", color: "#ff6d6d"},
			            { type: "line", seriesLayoutBy: "row", color: "#00d995"},
			            {
			                type: 'pie',
			                id  : 'pie',
			                radius: [80, 130],
			                center: [850, 165],
			                color : ['#83c4ff', '#ff9c00', '#ff6d6d', '#00d995'],
			                label: {
			                    formatter: '{b}: {@' + sourceArr[0][(sourceArr[0].length)-1] + '} ({d}%)',
			                    normal: {
			                      show: true,
			                      position: 'center'
								},
								emphasis: {
								  show: true,
								  textStyle: {
								     color: 'blue',
								     fontSize: '20',
								     fontWieght: 'bold',
								     align: 'center'
								  }
								}
			                },
			                
			                encode: {
			                	itemName: 'buzzAmt',
			                    value: sourceArr[0][(sourceArr[0].length)-1],
			                    tooltip: sourceArr[0][(sourceArr[0].length)-1]
			                }
			                
			            }
			        ]
			    };

			    myTrend.on('updateAxisPointer', function (event) {
			    	
			        var xAxisInfo = event.axesInfo[0];
			        if (xAxisInfo) {
			            var dimension = xAxisInfo.value + 1;
			            if (xAxisInfo.value === undefined || xAxisInfo.value === null) return false;
			            myTrend.setOption({
			            	 	//grid: {x: '3%', y: '3%', width: '50%', height: '50%'},
			                series: {
			                    id: 'pie',
			                    //center: ['75%', '75%']
			                    label: {
			                    	formatter: '{b}: {@[' + dimension + ']} ({d}%)'
			                    },
			                    encode: {
			                        value: dimension,
			                        tooltip: dimension
			                    } 
			                }
			                
			            });//setoption end
			        }
			        return true;
			    });//function end

			    myTrend.setOption(option);
			    setTimeout(function(){
			    	  $("body").loading("stop");
		    	}, 100);
			}, 10);
			return true;
		}
	};
	
	window.SDII.Chart.cusTrendChart = window.SDII.Chart.cusTrendChart || cusTrendChart;
	
})(window);
});
