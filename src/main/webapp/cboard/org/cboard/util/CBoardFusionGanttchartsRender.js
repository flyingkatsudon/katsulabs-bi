/**
 * Created by zyong on 2016/7/25.
 */

var CBoardFusionchartsRender = function (jqContainer, options) {
    this.container = jqContainer; // jquery object
    this.options = options;
};

CBoardFusionchartsRender.prototype.html = function (persist) {
    var self = this;
    var temp = "" + self.template; //self.template은 곧 CBoardKpiRender.prototype.template임, 여기에 html 태그들이 정의되있음, temp에 집어넣음
    var html = temp.render(self.options); //temp에 집어넣은 html태그들을 CBoardCommonUtils.js의 render를 통해서 재가공함

    return html;
};

CBoardFusionchartsRender.prototype.realTimeTicket = function () {
    var self = this;
    return function (o) {
        $(self.container).find('h3').html(o.kpiValue);
    }
};

CBoardFusionchartsRender.prototype.do = function () {
    var self = this;
    $(self.container).html(self.rendered());
};

CBoardFusionchartsRender.prototype.template = "<div id='chart-container'></div>";
