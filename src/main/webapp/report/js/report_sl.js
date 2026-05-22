/*
 * report.js
 * - insight report base script
 *
 * 
 18.09
 *
 * JayKeem
 *
 * jaykeem@weni.co.kr
*/

(function() {

$(document).ready(function() {
	
	$(".btn_jsch").on("click", function() {

		var inpVal = $(".inp_jsch").val() || "";
		inpVal = inpVal.replace(/\s+/gi, "");

		if (!inpVal) {
			alert("질병명을 입력해주세요");
			return false;
		}

		$(".control_bar_show_wrap").slideDown();
		$(".chart_trend_sub_close").click();
		$(".docs_smmry_wrap").attr("isOpen", 0).hide();
		$(".news_wrap").attr("isOpen", 1).slideDown("fast");
		sv.category = "8";
		sv.cmpyList = [];

		$(".dashboard_trend_title").text(sf.getTrendChartName() + " 브리핑");
		$("#chart_trend").html(sf.getTrendLoadingText());
		$("#ctrl_trend").html("");
		$(".chart_network_upper_wrap").show();
		$(".chart_trend_menu").hide();

		sf.onDrawTrendChart(inpVal);
		
		return true;
	});	
});

	
})();
