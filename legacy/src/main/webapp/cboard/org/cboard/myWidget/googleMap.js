/**
 * created by Dongwook on 2018.08.01
 */
function makeGoogleMap() {
  var mapProp = {
    center:new google.maps.LatLng(37.570473, 126.992597), //종로3가
    zoom: 12,
    mapTypeId:google.maps.MapTypeId.ROADMAP
  };
  var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
}

//google.maps.event.addDomListener(window, 'load', initialize);