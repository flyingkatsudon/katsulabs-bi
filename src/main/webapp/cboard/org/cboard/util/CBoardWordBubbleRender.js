var CBoardWordBubbleRender = function (jqContainer, options) {
    this.container = jqContainer; // jquery object
    this.options = options;
};

CBoardWordBubbleRender.prototype.html = function (persist) {
    var self = this;
    var temp = "" + self.template; //self.template은 곧 CBoardKpiRender.prototype.template임, 여기에 html 태그들이 정의되있음, temp에 집어넣음
    var html = temp.render(self.options); //temp에 집어넣은 html태그들을 CBoardCommonUtils.js의 render를 통해서 재가공함
    if (persist) { //persist비어있음
        setTimeout(function () {
            self.container.css('background', '#cc0066'); //여기서 배경화면 색깔을 지정할 수 있음, #fff는 흰색임
            html2canvas(self.container, {
                onrendered: function (canvas) {
                    persist.data = canvas.toDataURL("image/jpeg");
                    persist.type = "jpg";
                    persist.widgetType = "kpi";
                }
            });
        }, 1000);
        // persist.data = {name: self.options.kpiName, value: self.options.kpiValue};
        // persist.type = "kpi";
    }
    return html;
};

CBoardWordBubbleRender.prototype.realTimeTicket = function () {
    var self = this;
    return function (o) {
        $(self.container).find('h3').html(o.kpiValue);
    }
};

CBoardWordBubbleRender.prototype.do = function () {
    var self = this;
    $(self.container).html(self.rendered());
};

CBoardWordBubbleRender.prototype.template = "<svg></svg>";

