function makeBubble(data) {
		
	//DB에서 받아온 데이터: 함수의 매개변수 data 의 포맷을 변환하는 단계
     var keyName = "Name";
     var valueName = "Count";
     var newArr = [];
    
     for(var i=0; i<data.keys.length; i++) {
    	 var t = [];
    	 t[keyName] = data.keys[i][0]; //키 삽입
    	 t[valueName] = data.data[0][i]; //밸류 삽입
    	 newArr.push(t);
     }
    
	 dataset = {children: newArr};
	
	 //버블차트 그리기 위한 사전세팅 단계
	 var diameter = 600;
     var color = d3.scaleOrdinal(d3.schemeCategory20);

     var bubble = d3.pack(dataset)
         .size([diameter, diameter])
         .padding(1.5);

     var svg = d3.select("svg")
         .attr("width", diameter)
         .attr("height", diameter)
         .attr("class", "bubble");
     
     //3번째 함수
     var nodes = d3.hierarchy(dataset)
         .sum(function(d) { return d.Count; });

     var node = svg.selectAll(".node")
         .data(bubble(nodes).descendants())
         .enter()
         .filter(function(d){
             return  !d.children
         })
         .append("g")
         .attr("class", "node")
         .attr("transform", function(d) {
             return "translate(" + d.x + "," + d.y + ")";
         });

     node.append("title")
         .text(function(d) {
             return d.Name + ": " + d.Count;
         });

     node.append("circle")
         .attr("r", function(d) {
             return d.r;
         })
         .style("fill", function(d,i) {
             return color(i);
         });

     node.append("text")
         .attr("dy", ".2em")
         .style("text-anchor", "middle")
         .text(function(d) {
             return d.data.Name.substring(0, d.r / 3);
         })
         .attr("font-family", "sans-serif")
         .attr("font-size", function(d){
             return d.r/5;
         })
         .attr("fill", "white");

     node.append("text")
         .attr("dy", "1.3em")
         .style("text-anchor", "middle")
         .text(function(d) {
             return d.data.Count;
         })
         .attr("font-family",  "Gill Sans", "Gill Sans MT")
         .attr("font-size", function(d){
             return d.r/5;
         })
         .attr("fill", "white");

     d3.select(self.frameElement)
         .style("height", diameter + "px");
}