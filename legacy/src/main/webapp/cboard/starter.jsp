<%@page import="org.cboard.dto.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
This is a starter template page. Use this page to start your new project from
scratch. This page gets rid of all links and provides the needed markup only.
-->
<html ng-app="cBoard" ng-controller="cBoardCtrl">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Shinhan | BDP</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="dist/css/AdminLTE.css">
    <link rel="stylesheet" href="css/cboard.css">
    <link rel="stylesheet" href="css/tabs.sideways.css">
    <!-- ngJsTree -->
    <link rel="stylesheet" href="plugins/ngJsTree/style.css">
    <link rel="stylesheet" href="plugins/jQueryUI/theme.css">
    <link rel="stylesheet" href="plugins/jQueryUI/jquery-ui.css">

    <!-- AdminLTE Skins. We have chosen the skin-blue for this starter
          page. However, you can choose any other skin. Make sure you
          apply the skin class to the body tag so the changes take effect.
    -->
    <link rel="stylesheet" href="dist/css/skins/skin-blue.min.css">

    <script src="http://api.map.baidu.com/api?v=2.0&ak=ZUONbpqGBsYGXNIYHicvbAbM"></script>
    <script src="plugins/echart/echarts-3.6.2.min.js"></script>
    <!-- bmap-->
    <script src="plugins/echart/echarts-bmap.min.js"></script>


    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="plugins/jQuery/jquery-2.2.3.min.js"></script>

    <!--<script src="http://cdn.bootcss.com/angular.js/1.5.8/angular.js"></script>-->
    <script src="org/cboard/Settings.js"></script>
    <script src="lib/angular.min.js"></script>
    <script src="lib/angular-ui-router.min.js"></script>
    <script src="lib/angular-md5.min.js"></script>
    <script src="lib/angular-drag-and-drop-lists.js"></script>
    <script src="lib/angular-sanitize.min.js"></script>
    <script src="lib/ui-bootstrap-tpls-2.1.3.min.js"></script>
    <script src="lib/angular-translate.js"></script>
    <script src="lib/angular-translate-loader-partial.js"></script>

    <script src="lib/underscore-min.js"></script>
    <script src="lib/numbro.min.js"></script>
    <script src="lib/ui-select.min.js"></script>

    <script src="plugins/jQueryUI/jquery-ui.min.js"></script>
    <!-- Bootstrap 3.3.6 -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.js"></script>
    <!--ngJsTree -->
    <script src="plugins/ngJsTree/jstree.js"></script>
    <script src="plugins/ngJsTree/ngJsTree.js"></script>

    <!-- ECharts -->
    <script src="plugins/echart/theme-fin1.js"></script>
    <script src="plugins/echart/echarts-wordcloud.min.js"></script>
    <script src="plugins/echart/echarts-liquidfill.min.js"></script>

    <script src="lib/jquery.ba-resize.js"></script>
    <!--<script src="plugins/echart/world.json"></script>-->
    <!--<script src="plugins/echart/world.js"></script>-->
    <!--<script src="plugins/echart/china.json"></script>-->
    <!--<script src="plugins/echart/china.js"></script>-->
    <script src="plugins/ace/ace.js"></script>
    <script src="plugins/ace/cb-complete-list.js"></script>
    <script type="text/javascript" src="plugins/ace/ext-language_tools.js"></script>
    <script src="plugins/ui-ace/ui-ace.js"></script>
    <script src="plugins/FineMap/d3.v3.js"></script>
    <script src="plugins/FineMap/d3.tip.js"></script>

    <link rel="stylesheet" href="plugins/rzslider/rzslider.min.css">
    <script src="plugins/rzslider/rzslider.min.js"></script>

    <link rel="stylesheet" href="plugins/daterangepicker/daterangepicker.css">
    <script src="plugins/daterangepicker/moment.js"></script>
    <script src="plugins/daterangepicker/daterangepicker.js"></script>
    <script src="lib/angular-daterangepicker.min.js"></script>
    <link rel="stylesheet" href="css/angular-cron-jobs.min.css">
    <script src="lib/angular-cron-jobs.min.js"></script>

    <!--<script src="plugins/datatables/jquery.dataTables.js"></script>-->
    <!--<script src="plugins/datatables/dataTables.bootstrap.min.js"></script>-->
    <!--<link rel="stylesheet" href="plugins/datatables/dataTables.bootstrap.css">-->
    <!--<link rel="stylesheet" href="css/ui-select.min.css">-->
    <link rel="stylesheet" href="css/select.css">

    <link rel="stylesheet" href="plugins/timeline/map_tree.css">
    <script src="plugins/timeline/timeline.js"></script>

    <link rel="stylesheet" href="plugins/tree/tree.css">
    <script src="plugins/tree/tree.js"></script>
    <script src="lib/angular-uuid4.min.js"></script>

    <link rel="stylesheet" href="plugins/jquery-contextmenu/jquery.contextMenu.min.css">
    <script src="plugins/jquery-contextmenu/jquery.contextMenu.min.js"></script>
    <script src="plugins/jquery-contextmenu/jquery.ui.position.min.js"></script>

    <link rel="stylesheet" href="plugins/colorpicker/bootstrap-colorpicker.min.css">
    <script src="plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
    
    
    <!-- FROM HERE, LIBRARIES FOR Additional Widgets  -->
    <!-- GoogleMap API -->
    <script src="http://maps.google.com/maps/api/js?key=AIzaSyCdKmAOh3nO-Y-dDx--uL2rDmC4ajAKoJs&region=ko"></script>
    
    <!-- for fusioncharts -->
	<script type="text/javascript" src="https://static.fusioncharts.com/code/latest/fusioncharts.js"></script>
	<script type="text/javascript" src="https://static.fusioncharts.com/code/latest/themes/fusioncharts.theme.fusion.js"></script>
    
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts-gl/echarts-gl.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts-stat/ecStat.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/extension/dataTool.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/simplex.js"></script>
    
     <!--  for highcharts -->
    <script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/exporting.js"></script>
	<script src="https://code.highcharts.com/modules/export-data.js"></script>
	<script src="https://code.highcharts.com/modules/pareto.js"></script>
    <script src="https://code.highcharts.com/modules/funnel.js"></script>
    <script src="https://d3js.org/d3.v4.min.js"></script>
<%
	request.getSession().setAttribute("starter", "starter");
	
	User user = (User) request.getSession().getAttribute("user");
	String changePwd = (String) request.getSession().getAttribute("changePwd");
	
	if (changePwd != null && changePwd.equals("changePwd")) {
		request.getSession().invalidate();
		response.sendRedirect("/bdp/logout");
	}
	if (user == null) response.sendRedirect("/bdp");	
%>
	<script type="text/javascript">
	
		window.timer = null;
		$(document).ready(function(){

			$("body").on("mousemove keydown", function() {
				window.lastTime = new Date();
			});
			
			<%-- <% if (user != null) {%>
				var userId = '<%= user.getUserId()%>';
				checkSession();
			<% }%> --%>

			checkSession();
		
			function checkSession () {
				$.ajax({
					url: '/bdp/sm/checksn',
					type : 'POST',
		        	contentType: 'application/json; charset=utf-8',
					//data: JSON.stringify({userId: userId}),
					success: function(res) {
						//var res = JSON.parse(response);		
						if (res.status) return false;
						var prefix = "";
						if (res.loginIp) prefix = res.loginIp + "에서 ";
						
						alert(prefix + "중복 로그인 되어 로그아웃 됩니다.");
						location.href = "/bdp/logout";
					}, 
					error: function(res) {
						alert("세션 검증에 실패하여 로그아웃 됩니다.");
						window.location.href = '/bdp/logout';
					},
					complete: function(){
						if (window.timer) clearInterval(window.timer);
						window.timer = setInterval(checkSession, 1000);
					}
				});
			}
			
			window.lastTime = new Date();
			checkTime();

			function checkTime() {
				
				if (window.actTimer) clearInterval(window.actTimer);
				var curTime = new Date();
				var subtrs  = Math.ceil((curTime - lastTime) / 1000);
				
				if (subtrs > 1800) {
					alert('30분 이상 응답이 없어 자동 로그아웃 됩니다');
					location.href = "/bdp/logout";
					return false;
				}
				window.actTimer = setInterval(checkTime, 1000);
			}
		});
	</script>
	
    <style>
        .modal-fit .modal-dialog {
            left: 0;
            top: 0;
            right: 0;
            bottom: 0;
            margin: auto;
            padding: 50px;
        }

        .modal-fit .modal-body {
            height: calc(100vh - 200px);
            overflow: auto;
        }

        @media (min-width: 768px) {
            .modal-fit .modal-dialog {
                width: auto;
            }
        }
    </style>
</head>
<!--
BODY TAG OPTIONS:
=================
Apply one or more of the following classes to get the
desired effect
|---------------------------------------------------------|
| SKINS         | skin-blue                               |
|               | skin-black                              |
|               | skin-purple                             |
|               | skin-yellow                             |
|               | skin-red                                |
|               | skin-green                              |
|---------------------------------------------------------|
|LAYOUT OPTIONS | fixed                                   |
|               | layout-boxed                            |
|               | layout-top-nav                          |
|               | sidebar-collapse                        |
|               | sidebar-mini                            |
|---------------------------------------------------------|
-->
<body class="hold-transition skin-blue sidebar-mini" ng-class="{'sidebar-collapse': liteMode}">
<div class="wrapper">

    <!-- Main Header -->
    <header class="main-header" ng-include="'view/starter/main-header.html'"></header>
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar" ng-include="'view/starter/main-sidebar.html'"></aside>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <ui-view>

        </ui-view>
        <!-- Content Header (Page header) -->
        <!--<section class="content-header">-->
        <!--<h1>-->
        <!--Page Header-->
        <!--<small>Optional description</small>-->
        <!--</h1>-->
        <!--<ol class="breadcrumb">-->
        <!--<li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>-->
        <!--<li class="active">Here</li>-->
        <!--</ol>-->
        <!--</section>-->

        <!-- Main content -->
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <!-- Main Footer -->
    <!-- <footer class="main-footer" ng-include="'view/starter/main-footer.html'"></footer> -->

</div>



<!-- ./wrapper -->
<script src="org/cboard/util/CBoardEChartUtils.js"></script>
<script src="org/cboard/util/CBoardEChartRender.js"></script>
<script src="org/cboard/util/CBoardCommonUtils.js"></script>
<script src="org/cboard/util/CBoardKpiRender.js"></script>
<script src="org/cboard/util/CBoardTableRender.js"></script>
<script src="org/cboard/util/CBoardMapRender.js"></script>
<!--<script src="org/cboard/util/CBoardGisRender.js"></script>-->
<script src="org/cboard/util/CBoardJsTreeUtils.js"></script>
<script src="org/cboard/util/CBoardHeatMapRender.js"></script>
<script src="org/cboard/util/CBoardBmapRender.js"></script>

<!-- AngularJS-->
<script src="org/cboard/ng-app.js"></script>
<script src="org/cboard/ng-config.js"></script>
<script src="org/cboard/controller/cboard/cBoardCtrl.js"></script>
<script src="org/cboard/controller/cboard/homepageCtrl.js"></script>
<script src="org/cboard/controller/dashboard/dashboardViewCtrl.js"></script>
<script src="org/cboard/controller/config/widgetCtrl.js"></script>
<script src="org/cboard/controller/config/datasourceCtrl.js"></script>
<script src="org/cboard/controller/config/boardCtrl.js"></script>
<script src="org/cboard/controller/config/freeLayoutCtrl.js"></script>
<script src="org/cboard/controller/config/folderTreeCtrl.js"></script>
<script src="org/cboard/controller/config/categoryCtrl.js"></script>
<script src="org/cboard/controller/config/datasetCtrl.js"></script>
<script src="org/cboard/controller/admin/userAdminCtrl.js"></script>
<script src="org/cboard/controller/config/jobCtrl.js"></script>
<script src="org/cboard/controller/config/job/mailJobCtrl.js"></script>
<script src="org/cboard/controller/dashboard/paramCtrl.js"></script>
<script src="org/cboard/controller/config/shareResCtrl.js"></script>
<script src="org/cboard/controller/utils/paramSelector.js"></script>

<script src="org/cboard/service/dashboard/dashboardService.js"></script>
<script src="org/cboard/service/dashboard/freeLayoutService.js"></script>
<script src="org/cboard/service/data/dataService.js"></script>
<script src="org/cboard/service/util/ModalUtils.js"></script>
<script src="org/cboard/service/updater/updateService.js"></script>
<script src="org/cboard/service/chart/chartDataProcess.js"></script>
<script src="org/cboard/service/chart/chartFunnelService.js"></script>
<script src="org/cboard/service/chart/chartKpiService.js"></script>
<script src="org/cboard/service/chart/chartLineService.js"></script>
<script src="org/cboard/service/chart/chartContrastService.js"></script>
<script src="org/cboard/service/chart/chartPieService.js"></script>
<script src="org/cboard/service/chart/chartSankeyService.js"></script>
<script src="org/cboard/service/chart/chartRadarService.js"></script>
<script src="org/cboard/service/chart/chartService.js"></script>
<script src="org/cboard/service/chart/chartMapService.js"></script>
<script src="org/cboard/service/chart/chartTableService.js"></script>
<script src="org/cboard/service/chart/chartScatterService.js"></script>
<script src="org/cboard/service/chart/chartGaugeService.js"></script>
<script src="org/cboard/service/chart/chartWordCloudService.js"></script>
<script src="org/cboard/service/chart/chartTreeMapService.js"></script>
<script src="org/cboard/service/chart/chartAreaMapService.js"></script>
<script src="org/cboard/service/chart/chartHeatMapCalendarService.js"></script>
<script src="org/cboard/service/chart/chartHeatMapTableService.js"></script>
<script src="org/cboard/service/chart/chartLiquidFillService.js"></script>
<script src="org/cboard/service/chart/chartChinaMapService.js"></script>
<script src="org/cboard/service/chart/chartChinaMapBmapService.js"></script>
<script src="org/cboard/service/chart/chartRelationService.js"></script>
<script src="org/cboard/directive/dashboard/dashboardWidget.js"></script>
<script src="org/cboard/filter/dashboard/dashboardViewFilter.js"></script>

<script src="plugins/crossTable/plugin.js"></script>
<script src="plugins/FineMap/plugin.js"></script>
<!--<script src="plugins/FineMap/.js"></script>-->

<!-- FROM HERE, js files for additional widgets -->
<script src="org/cboard/myWidget/googleMap.js"></script>
<script src="org/cboard/myWidget/bubbleChart.js"></script>
<script src="org/cboard/myWidget/fusionganttcharts.js"></script>
<script src="org/cboard/myWidget/rainfall.js"></script>
<script src="org/cboard/myWidget/pyramid.js"></script>


<script src="org/cboard/util/CBoardHighchartsRender.js"></script>
<script src="org/cboard/util/CBoardGoogleMapRender.js"></script>
<script src="org/cboard/util/CBoardWordBubbleRender.js"></script>
<script src="org/cboard/util/CBoardFusionGanttchartsRender.js"></script>


<script src="org/cboard/service/chart/chartGoogleMapService.js"></script>
<script src="org/cboard/service/chart/chartWordBubbleService.js"></script>
<script src="org/cboard/service/chart/chartBoxplotService.js"></script>
<script src="org/cboard/service/chart/chartPyramidService.js"></script>
<script src="org/cboard/service/chart/chartParetoService.js"></script>
<script src="org/cboard/service/chart/chartFusionGanttchartsService.js"></script>
<script src="org/cboard/service/chart/chartThemeRiverService.js"></script>
<script src="org/cboard/service/chart/chartSunburstService.js"></script>
<script src="org/cboard/service/chart/chartParallelService.js"></script>

<script type="text/ng-template" id="echartContent">
    <div class="col-md-{{widget.width}}">
        <div class="box box-solid" style="z-index: 99;">
            <div class="box-header" ng-mouseover="x=true" ng-mouseleave="x=false">
                <!--<i class="fa fa-bar-chart-o"></i>-->
                <h3 class="box-title">{{widget.name}}</h3>
                <div class="box-tools pull-right" ng-show="x" ng-init="x=false">
                    <button name="reload_{{widget.widget.id}}" type="button" class="btn btn-box-tool" ng-click="reload(widget)"><i
                            class="fa fa-refresh"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-if="widgetCfg" ng-click="config(widget)"><i
                            class="fa fa-wrench"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-click="modalChart(widget)"><i
                            class="fa fa-square-o"></i>
                    </button>
                </div>
            </div>
            <div class="box-body" ng-style="{height:myheight+'px'}" style="padding: 3px 0px 3px 13px;">
            </div>
        </div>
    </div>
</script>
<script type="text/ng-template" id="kpiContent">
    <div class="col-md-{{widget.width}} kpi-body"/>
</script>
<script type="text/ng-template" id="chartContent">
    <div class="col-md-{{widget.width}}" style="z-index: 99;">
        <div class="box box-solid">
            <div class="box-header" ng-mouseover="x=true" ng-mouseleave="x=false" >
                <!--<i class="fa fa-bar-chart-o"></i>-->
                <h3 class="box-title">{{widget.name}}</h3>
                <div class="box-tools pull-right" ng-show="x" ng-init="x=false">
                    <button type="button" class="btn btn-box-tool" ng-click="reload(widget)"><i
                            class="fa fa-refresh"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-if="widgetCfg" ng-click="config(widget)"><i
                            class="fa fa-wrench"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-click="modalTable(widget)"><i
                            class="fa fa-square-o"></i>
                    </button>
                </div>
            </div>
            <div class="box-body" ng-style="{height:myheight+'px'}" style="padding: 3px 0px 3px 13px;">
            </div>
        </div>
    </div>
</script>
</body>
</html>
