/**
 * Created by Owlnest on 2018/10/08.
 */
'use strict';
cBoard.service('chartBoxplotService', function ($state, $window) {

    this.render = function (containerDom, option, scope, persist, drill, relations, chartConfig) {
        var render = new CBoardEChartRender(containerDom, option);
        render.addClick(chartConfig, relations, $state, $window);
        return render.chart(null, persist);
    };

    this.parseOption = function (data) {
    	
    	var data = echarts.dataTool.prepareBoxplotData([
    	    [850, 740, 900, 1070, 930, 850, 950, 980, 980, 880, 1000, 980, 930, 650, 760, 810, 1000, 1000, 960, 960],
    	    [960, 940, 960, 940, 880, 800, 850, 880, 900, 840, 830, 790, 810, 880, 880, 830, 800, 790, 760, 800],
    	    [880, 880, 880, 860, 720, 720, 620, 860, 970, 950, 880, 910, 850, 870, 840, 840, 850, 840, 840, 840],
    	    [890, 810, 810, 820, 800, 770, 760, 740, 750, 760, 910, 920, 890, 860, 880, 720, 840, 850, 850, 780],
    	    [890, 840, 780, 810, 760, 810, 790, 810, 820, 850, 870, 870, 810, 740, 810, 940, 950, 800, 810, 870]
    	]);

    	var option = {
    			 title: [
    			        {
    			            text: 'Michelson-Morley Experiment',
    			            left: 'center',
    			        },
    			        {
    			            text: 'upper: Q3 + 1.5 * IQR \nlower: Q1 - 1.5 * IQR',
    			            borderColor: '#999',
    			            borderWidth: 1,
    			            textStyle: {
    			                fontSize: 14
    			            },
    			            left: '10%',
    			            top: '90%'
    			        }
    			    ],
    			    tooltip: {
    			        trigger: 'item',
    			        axisPointer: {
    			            type: 'shadow'
    			        }
    			    },
    			    grid: {
    			        left: '10%',
    			        right: '10%',
    			        bottom: '15%'
    			    },
    			    xAxis: {
    			        type: 'category',
    			        data: data.axisData,
    			        boundaryGap: true,
    			        nameGap: 30,
    			        splitArea: {
    			            show: false
    			        },
    			        axisLabel: {
    			            formatter: 'expr {value}'
    			        },
    			        splitLine: {
    			            show: false
    			        }
    			    },
    			    yAxis: {
    			        type: 'value',
    			        name: 'km/s minus 299,000',
    			        splitArea: {
    			            show: true
    			        }
    			    },
    			    series: [
    			        {
    			            name: 'boxplot',
    			            type: 'boxplot',
    			            data: data.boxData,
    			            tooltip: {
    			                formatter: function (param) {
    			                    return [
    			                        'Experiment ' + param.name + ': ',
    			                        'upper: ' + param.data[5],
    			                        'Q3: ' + param.data[4],
    			                        'median: ' + param.data[3],
    			                        'Q1: ' + param.data[2],
    			                        'lower: ' + param.data[1]
    			                    ].join('<br/>');
    			                }
    			            }
    			        },
    			        {
    			            name: 'outlier',
    			            type: 'scatter',
    			            data: data.outliers
    			        }
    			    ]
    			
    	}
        /*var chartConfig = data.chartConfig;
        var casted_keys = data.keys;
        var casted_values = data.series;
        var aggregate_data = data.data;
        var newValuesConfig = data.seriesConfig;
        var series_data = new Array();
        var string_keys = _.map(casted_keys, function (key) {
            return key.join('-');
        });
        var tunningOpt = chartConfig.option;

        var sum_data = [];
        for (var j = 0; aggregate_data[0] && j < aggregate_data[0].length; j++) {
            var sum = 0;
            for (var i = 0; i < aggregate_data.length; i++) {
                sum += aggregate_data[i][j] ? Number(aggregate_data[i][j]) : 0;
                // change undefined to 0
                aggregate_data[i][j] = aggregate_data[i][j] ? Number(aggregate_data[i][j]) : 0;
            }
            sum_data[j] = sum;
        }

        var line_type;
        for (var i = 0; i < aggregate_data.length; i++) {
            var joined_values = casted_values[i].join('-');
            var s = angular.copy(newValuesConfig[joined_values]);
            s.name = joined_values;
            s.data = aggregate_data[i];
            s.barMaxWidth = 40;
            line_type = s.type;
            if (s.type == 'stackbar') {
                s.type = 'bar';
                s.stack = s.valueAxisIndex.toString();
            } else if (s.type == 'polarbar') {
                s.type = 'bar';
                s.stack = s.valueAxisIndex.toString();
                s.coordinateSystem = 'polar';
            } else if (s.type == 'percentbar') {
                if (chartConfig.valueAxis == 'horizontal') {
                    s.data = _.map(aggregate_data[i], function (e, i) {
                        return (e / sum_data[i] * 100).toFixed(2);
                    })
                } else {
                    s.data = _.map(aggregate_data[i], function (e, i) {
                        return [i, (e / sum_data[i] * 100).toFixed(2), e];
                    });
                }
                s.type = 'bar';
                s.stack = s.valueAxisIndex.toString();
            } else if (s.type == "arealine") {
                s.type = "line";
                s.areaStyle = {normal: {}};
            } else if (s.type == "stackline") {
                s.type = "line";
                s.stack = s.valueAxisIndex.toString();
                s.areaStyle = {normal: {}};
            } else if (s.type == 'percentline') {
                if (chartConfig.valueAxis == 'horizontal') {
                    s.data = _.map(aggregate_data[i], function (e, i) {
                        return (e / sum_data[i] * 100).toFixed(2);
                    })
                } else {
                    s.data = _.map(aggregate_data[i], function (e, i) {
                        return [i, (e / sum_data[i] * 100).toFixed(2), e];
                    });
                }
                s.type = "line";
                s.stack = s.valueAxisIndex.toString();
                s.areaStyle = {normal: {}};
            }
            if (chartConfig.valueAxis == 'horizontal') {
                s.xAxisIndex = s.valueAxisIndex;
            } else {
                s.yAxisIndex = s.valueAxisIndex;
            }
            series_data.push(s);
        }

        var valueAxis = angular.copy(chartConfig.values);
        _.each(valueAxis, function (axis, index) {
            axis.axisLabel = {
                formatter: function (value) {
                    return numbro(value).format("0a.[0000]");
                }
            };
            if (axis.series_type == "percentbar" || axis.series_type == "percentline") {
                axis.min = 0;
                axis.max = 100;
            } else {
                axis.min = axis.min ? axis.min : null;
                axis.max = axis.max ? axis.max : null;
            }
            if (index > 0) {
                axis.splitLine = false;
            }
            axis.scale = true;
        });

        if (tunningOpt) {
            var labelInterval, labelRotate;
            tunningOpt.ctgLabelInterval ? labelInterval = tunningOpt.ctgLabelInterval : 'auto';
            tunningOpt.ctgLabelRotate ? labelRotate = tunningOpt.ctgLabelRotate : 0;
        }


        var categoryAxis = {
            type: 'category',
            data: string_keys,
            axisLabel: {
                interval: labelInterval,
                rotate: labelRotate
            },
            boundaryGap: false
        };

        _.each(valueAxis, function (axis) {
            var _stype = axis.series_type;
            if (_stype.indexOf('bar') !== -1) {
                categoryAxis.boundaryGap = true;
            }
        });

        var echartOption = {
            grid: angular.copy(echartsBasicOption.grid),
            legend: {
                data: _.map(casted_values, function (v) {
                    return v.join('-');
                })
            },
            tooltip: {
                formatter: function (params) {
                    var name = params[0].name;
                    var s = name + "</br>";
                    for (var i = 0; i < params.length; i++) {
                        s += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                        if (params[i].value instanceof Array) {
                            s += params[i].seriesName + " : " + params[i].value[1] + "% (" + params[i].value[2] + ")<br>";
                        } else {
                            s += params[i].seriesName + " : " + params[i].value + "<br>";
                        }
                    }
                    return s;
                },
                axisPointer: {
                    type: 'cross',
                    label: {
                        backgroundColor: '#6a7985'
                    }
                }
            },
            series: series_data
        };

        if (line_type == 'polarbar') {
            echartOption.angleAxis = chartConfig.valueAxis == 'horizontal' ? valueAxis : categoryAxis;
            echartOption.radiusAxis = chartConfig.valueAxis == 'horizontal' ? categoryAxis : valueAxis;
            echartOption.polar = {};
        } else {
            echartOption.xAxis = chartConfig.valueAxis == 'horizontal' ? valueAxis : categoryAxis; console.log(categoryAxis);
            echartOption.yAxis = chartConfig.valueAxis == 'horizontal' ? categoryAxis : valueAxis; console.log(valueAxis);
        }

        if (chartConfig.valueAxis === 'horizontal') {
            echartOption.grid.left = 'left';
            echartOption.grid.containLabel = true;
            echartOption.grid.bottom = '5%';
        }
        if (chartConfig.valueAxis === 'vertical' && chartConfig.values.length > 1) {
            echartOption.grid.right = 40;
        }

        // Apply tunning options
        updateEchartOptions(tunningOpt, echartOption);*/

        return option;
    }; //parseOption 함수 끝
});
