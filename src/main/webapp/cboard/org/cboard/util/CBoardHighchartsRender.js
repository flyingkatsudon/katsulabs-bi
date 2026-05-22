var CBoardHighchartsRender = function (jqContainer, options) {
    this.container = jqContainer; // jquery object
    this.options = options;
};

CBoardHighchartsRender.prototype.html = function (persist) {
    var self = this;
    var temp = "" + self.template; //self.template은 곧 CBoardKpiRender.prototype.template임, 여기에 html 태그들이 정의되있음, temp에 집어넣음
    var html = temp.render(self.options); //temp에 집어넣은 html태그들을 CBoardCommonUtils.js의 render를 통해서 재가공함

    return html;
};

CBoardHighchartsRender.prototype.realTimeTicket = function () {
    var self = this;
    return function (o) {
        $(self.container).find('h3').html(o.kpiValue);
    }
};

CBoardHighchartsRender.prototype.do = function () {
    var self = this;
    $(self.container).html(self.rendered());
};

CBoardHighchartsRender.prototype.template = "<div id='highcharts' style='min-width: 410px; max-width: 600px; height: 400px; margin: 0 auto'></div>";
