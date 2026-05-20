/**
 * Created by Owlnest on 2018/10/08.
 */
'use strict';
cBoard.service('chartParallelService', function ($state, $window) {

    this.render = function (containerDom, option, scope, persist, drill, relations, chartConfig) {
        var render = new CBoardEChartRender(containerDom, option);
        render.addClick(chartConfig, relations, $state, $window);
        return render.chart(null, persist);
    };

    this.parseOption = function () {
    	var option = {
    			parallelAxis: [
    		        {dim: 0, name: 'Price'},
    		        {dim: 1, name: 'Net Weight'},
    		        {dim: 2, name: 'Amount'},
    		        {
    		            dim: 3,
    		            name: 'Score',
    		            type: 'category',
    		            data: ['Excellent', 'Good', 'OK', 'Bad']
    		        }
    		    ],
    		    series: {
    		        type: 'parallel',
    		        lineStyle: {
    		            width: 4
    		        },
    		        data: [
    		            [12.99, 100, 82, 'Good'],
    		            [9.99, 80, 77, 'OK'],
    		            [20, 120, 60, 'Excellent']
    		        ]
    		    }

    	};

        return option;
    }; //parseOption 함수 끝
});
