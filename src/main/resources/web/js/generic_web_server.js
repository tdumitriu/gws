$(document).ready(function() {

  //-----------------------------
  // Global variables
  //-----------------------------
  // The map
  var map;

  // Current route elements
  var directionsDisplay;
  var directionsService;
  var routePolyline;
  var encodedPath;

  // Initial coordinates
  var currentLat = 40.438014
  var currentLon = -79.914587

  // List of Alert objects
  var streetDataList = {};

  // Colored polylines
  var COLOR_CREATED     = "#0CC0FF"
  var COLOR_UPDATED     = ""
  var COLOR_SAVED       = "#FFA500"
  var COLOR_CURRENT     = "#00CC00"
  var COLOR_ALLOWED     = ""
  var COLOR_NOT_ALLOWED = ""

  //---------------------------------
  // Get/Set the current alert DOM
  //---------------------------------
  function getAlert() {
    var encodedPosition = {};
    encodedPosition['type']               = 'LineString';
    encodedPosition['encodedCoordinates'] = encodedPath;

    var schedule = {};
    schedule['fromTime']  = parseInt($('.sliderDailyTimeRange').val()[0], 10);
    schedule['toTime']    = parseInt($('.sliderDailyTimeRange').val()[1], 10);
    schedule['fromStart'] = parseInt($('.sliderYearlyMonthRange').val()[0], 10);
    schedule['toEnd']     = parseInt($('.sliderYearlyMonthRange').val()[1], 10);
    schedule['frequency'] = parseInt($("#comboFrequency").find('option:selected').val(), 10);
    schedule['status']    = parseInt($("#comboStatus").find('option:selected').val(), 10);

    var dows = "0";
    if($('#dow_1').is(":checked")) { dows = dows + $('#dow_1').val(); }
    if($('#dow_2').is(":checked")) { dows = dows + $('#dow_2').val(); }
    if($('#dow_3').is(":checked")) { dows = dows + $('#dow_3').val(); }
    if($('#dow_4').is(":checked")) { dows = dows + $('#dow_4').val(); }
    if($('#dow_5').is(":checked")) { dows = dows + $('#dow_5').val(); }
    if($('#dow_6').is(":checked")) { dows = dows + $('#dow_6').val(); }
    if($('#dow_7').is(":checked")) { dows = dows + $('#dow_7').val(); }
    schedule['dayOfWeek'] = parseInt(dows, 10);

    var requestAlert = {};
    requestAlert['id']              = $("#street_id").text();
    requestAlert['encodedPosition'] = encodedPosition;
    requestAlert['side']            = parseInt($("#comboSide").find('option:selected').val(), 10);
    requestAlert['schedule']        = schedule;

    return requestAlert;
  }

  function setAlert(alertRecord) {
    // Start-End month
    var startMonth = alertRecord.schedule.fromStart;
    var endMonth = alertRecord.schedule.toEnd;
    $('.sliderYearlyMonthRange').val([startMonth, endMonth]);
    // Start-End daily time
    var startTime = alertRecord.schedule.fromTime;
    var endTime = alertRecord.schedule.toTime;
    $('.sliderDailyTimeRange').val([startTime, endTime]);
    // Days of week
    var daysOfWeek = alertRecord.schedule.dayOfWeek.toString();
    var mo = false;
    if(daysOfWeek.indexOf('1') >= 0) { mo = true; }
    var tu = false;
    if(daysOfWeek.indexOf('2') >= 0) { tu = true; }
    var we = false;
    if(daysOfWeek.indexOf('3') >= 0) { we = true; }
    var th = false;
    if(daysOfWeek.indexOf('4') >= 0) { th = true; }
    var fr = false;
    if(daysOfWeek.indexOf('5') >= 0) { fr = true; }
    var sa = false;
    if(daysOfWeek.indexOf('6') >= 0) { sa = true; }
    var su = false;
    if(daysOfWeek.indexOf('7') >= 0) { su = true; }
    $('#dow_1').prop('checked', mo);
    $('#dow_2').prop('checked', tu);
    $('#dow_3').prop('checked', we);
    $('#dow_4').prop('checked', th);
    $('#dow_5').prop('checked', fr);
    $('#dow_6').prop('checked', sa);
    $('#dow_7').prop('checked', su);
    // Frequency
    var frequency = alertRecord.schedule.frequency;
    $("#comboFrequency").val(frequency);
    // Status
    var status = alertRecord.schedule.status;
    $("#comboStatus").val(status);
    // Side
    var side = alertRecord.side;
    $("#comboSide").val(side);
  }

  //---------------------------------------
  //
  //  Client request services
  //
  //---------------------------------------
  //  CREATE or UPDATE ALERT
  //---------------------------------------
  $('#create_alert').click(function() {
    $('#myModal').modal('hide');
    var crtAlert = getAlert();
    if(crtAlert.id.length > 0) {
      crtAlert.encodedPosition['encodedCoordinates'] = "tvd_2015";
      updateAlert(crtAlert);
    } else {
      createAlert(crtAlert);
    }
  });

  //---------------------------------------
  // DELETE ALERT
  //---------------------------------------
    $('#delete_alert').click(function() {
      $('#myModal').modal('hide');
      var alertId = $("#street_id").text();
      deleteAlert(alertId);
    });

  //---------------------------------------
  // READ ALERT
  //---------------------------------------
  function loadAllStreetAlerts(currentLon, currentLat) {
    readStreetAlerts(currentLon, currentLat, 0, 0);
  }

  //--------------------------------------
  // Data access implementation
  //--------------------------------------
  //------------------------
  // CREATE
  //------------------------
  function createAlert(newAlert) {
    //alert(JSON.stringify(newAlert));
    var request = $.ajax({
      url: "/sca/create",
      method: "POST",
      data: JSON.stringify(newAlert),
      dataType: "json",
      contentType: "application/json"
    });

    request.done(function(responseAlert) {
      var translatedEncodedPosition = responseAlert.encodedPosition;
      var side = responseAlert.side;
      var schedule = responseAlert.schedule;
      // alert("Data Saved:\n\t1. Encoded position: " + translatedEncodedPosition.encodedCoordinates + "\n\t2. Side: " + side + "\n\t3. Frequency: " + schedule.frequency);

      var decodedPolylinePath = google.maps.geometry.encoding.decodePath(translatedEncodedPosition.encodedCoordinates);
      var newPolyline = drawPath(decodedPolylinePath, COLOR_CREATED, responseAlert);
      streetDataList[responseAlert.id] = { "alert" : responseAlert,  "polyline" : newPolyline };
    });

    request.fail(function(jqXHR, textStatus) {
      alert("Error creating: " + jqXHR.responseText);
    });

    request.always(function() {
      // alert("Done!");
    });
  }

  //------------------------
  // UPDATE
  //------------------------
  function updateAlert(selectedAlert) {
    // alert(JSON.stringify(selectedAlert));
    var request = $.ajax({
      url: "/sca/update",
      type: "PUT",
      data: JSON.stringify(selectedAlert),
      dataType: "json",
      contentType: "application/json"
    });

    request.done(function(updatedAlertId) {
      //alert("done updating id = [" + updatedAlertId + "]")
      updateAlertById(updatedAlertId, selectedAlert)
    });

    request.fail(function(jqXHR, textStatus) {
      alert("Error updating: " + jqXHR.responseText + " status = " + textStatus);
    });

    request.always(function() {
      // alert("Done!");
    });
  }

  function updateAlertById(id, infoAlert) {
    var uAlert = streetDataList[id].alert
    // Side
    uAlert.side = infoAlert.side
    // Schedule
    uAlert.schedule.frequency = infoAlert.schedule.frequency
    uAlert.schedule.status    = infoAlert.schedule.status
    uAlert.schedule.fromStart = infoAlert.schedule.fromStart
    uAlert.schedule.toEnd     = infoAlert.schedule.toEnd
    uAlert.schedule.fromTime  = infoAlert.schedule.fromTime
    uAlert.schedule.toTime    = infoAlert.schedule.toTime
    uAlert.schedule.dayOfWeek = infoAlert.schedule.dayOfWeek
  }

  //------------------------
  // DELETE
  //------------------------
  function deleteAlert(crtAlertId) {
    //alert(crtAlertId);
    var request = $.ajax({
      url: "/sca/delete",
      method: "POST",
      data: crtAlertId,
      dataType: "json",
      contentType: "application/json"
    });

    request.done(function(response) {
      if(response.ok == 1) {
        var deleteAlert = streetDataList[crtAlertId]
        deleteAlert.polyline.setMap(null);
        delete deleteAlert;
      } else {
        alert("The alert with id [" + crtAlertId + "] couldn't be deleted");
      }
    });

    request.fail(function(jqXHR, textStatus) {
      alert("Error deleting: " + jqXHR.responseText);
    });

    request.always(function() {
//      alert("Done!");
    });
  }

  //------------------------
  // READ
  //------------------------
  function readStreetAlerts(currentLon, currentLat, distance, filter) {
  //curl -v -X POST http://localhost:8080/sca/near -H "Content-Type: application/json" -d '{ "longitude" : 1.2, "latitude" : 3.9 }'

    // distance = the diagonal length of the rectangle that covers the street polylines to be retrieved
    // filter = the conditions that needs to be satisfied by the retrieved street polylines.
    //          Example:
    //                    status=2, frequency=99, etc.
    var coords = {};
    coords['longitude'] = currentLon;
    coords['latitude'] = currentLat;

    var request = $.ajax({
      url: "/sca/near",
      method: "POST",
      data: JSON.stringify(coords),
      dataType: "json",
      contentType: "application/json"
    });

    request.done(function(streetAlerts) {
      routePolyline.setMap(null);
      for (var i = 0; i < streetAlerts.length; i++) {
        var streetAlert = streetAlerts[i];
        var encodedPosition = streetAlert.encodedPosition;
        var decodedPolylinePath = google.maps.geometry.encoding.decodePath(encodedPosition.encodedCoordinates);
        var polyline = drawPath(decodedPolylinePath, COLOR_SAVED, streetAlert);
        streetDataList[streetAlert.id] = { "alert" : streetAlert,  "polyline" : polyline };
      }
    });

    request.fail(function(jqXHR, textStatus) {
      alert("Error reading: " + jqXHR.responseText);
    });

    request.always(function() {
//      alert("Done!");
    });
  }

  //---------------------------------------
  //
  //  Alert data entry front-end services
  //
  //---------------------------------------
  $('.sliderYearlyMonthRange').noUiSlider({
    start: [ 4, 11 ],
    step: 1.0,
    behaviour: 'tap-drag',
    margin: 1,
    connect: true,
    range: {
      'min': 1,
      'max': 12
    }
  });

  $('.sliderYearlyMonthRange').noUiSlider_pips({
    mode: 'values',
    values: [1,2,3,4,5,6,7,8,9,10,11,12],
    density: 12,
    stepped: true,
    format: {
      to: function(value) {
        if(value == 1) return 'Jan';
        if(value == 2) return 'Feb';
        if(value == 3) return 'Mar';
        if(value == 4) return 'Apr';
        if(value == 5) return 'May';
        if(value == 6) return 'Jun';
        if(value == 7) return 'Jul';
        if(value == 8) return 'Aug';
        if(value == 9) return 'Sep';
        if(value == 10) return 'Oct';
        if(value == 11) return 'Nov';
        if(value == 12) return 'Dec';
      }
    }
  });

  $('.sliderDailyTimeRange').noUiSlider({
    start: [ 8.5, 14 ],
    step: 0.5,
    behaviour: 'tap-drag',
    margin: 1,
    connect: true,
    range: {
      'min': 0,
      'max': 24
    },
    slide: function(e,ui) {
      $('#value2').html(ui.values[1]);
    }
  });

  $('.sliderDailyTimeRange').noUiSlider_pips({
    mode: 'values',
    values: [0,4,8,12,16,20,24],
    density: 2,
    stepped: true
  });

  $('.sliderDailyTimeRange').Link('lower').to('-inline-<div style="display:block;position: absolute;border: 1px solid #D9D9D9;font: 400 12px/12px Arial;border-radius: 3px;background: #fff;top: -43px;padding: 5px;left: -9px;text-align: center;width: 50px;"></div>',
    function(value) {
      var time = value;
      var hour = Math.floor(value);
      var minute = value - hour;
      if(minute == 0) time = hour+":00"
      if(minute == 0.25) time = hour+":15"
      if(minute == 0.5) time = hour+":30"
      if(minute == 0.75) time = hour+":45"
      $(this).html('<strong>Start: </strong><span>'+time+'</span>');
    }
  );

  $('.sliderDailyTimeRange').Link('upper').to('-inline-<div style="display:block;position: absolute;border: 1px solid #D9D9D9;font: 400 12px/12px Arial;border-radius: 3px;background: #fff;top: -43px;padding: 5px;left: -9px;text-align: center;width: 50px;"></div>',
    function(value) {
      var time = value;
      var hour = Math.floor(value);
      var minute = value - hour;
      if(minute == 0) time = hour+":00"
      if(minute == 0.25) time = hour+":15"
      if(minute == 0.5) time = hour+":30"
      if(minute == 0.75) time = hour+":45"
      $(this).html('<strong>End: </strong><span>'+time+'</span>');
    }
  );

  //---------------------------------------
  //
  //  Google Geolocation services
  //
  //---------------------------------------
  function initialize() {
    // Instantiate a directions service.
    directionsService = new google.maps.DirectionsService();

    // Create a map and center it on the local client location.
    var mapOptions = {
      zoom: 18
    }
    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

    // Try HTML5 geolocation
    if(navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(function(position) {

        currentLat = position.coords.latitude;
        currentLon = position.coords.longitude;

        var pos = new google.maps.LatLng(currentLat, currentLon);
        map.setCenter(pos);

        geoMarkerSettings(currentLat, currentLon);
        loadAllStreetAlerts(currentLat, currentLon);

        // Create the DIV to hold the control and cal the
        // DrawingControl() constructor passing in this DIV.
        var drawingControlDiv = document.createElement('div');
        var drawingControl = new DrawingControl(drawingControlDiv, map);

        drawingControlDiv.index = 1;
        map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(drawingControlDiv);

      }, function() {
           handleNoGeolocation(true, currentLat, currentLon);
        });
    } else {
       // Browser doesn't support Geolocation
       handleNoGeolocation(false, currentLat, currentLon);
     }
  }

  function geoMarkerSettings(currentLat, currentLon) {
    var markerAposition = currentLat + ', ' + currentLon
    var markerBposition = currentLat + ', ' + (currentLon + 0.0006)

    var markerA = new google.maps.Marker({
      position: new google.maps.LatLng(currentLat, currentLon),
      map: map,
      draggable: true,
      icon: 'img/marker-pin-green.png'
    });

    var markerB = new google.maps.Marker({
      position: new google.maps.LatLng(currentLat, currentLon + 0.0006),
      map: map,
      draggable: true,
      icon: 'img/marker-pin-red.png'
    });

    // Initialize the routePolyline
    routePolyline = drawPath(new Array(), COLOR_CURRENT, "init")

    google.maps.event.addListener(markerA, 'dragend', function(event) {
      markerAposition = event.latLng.lat()+','+event.latLng.lng();
      calcRoute(markerAposition, markerBposition);
    });

    google.maps.event.addListener(markerB, 'dragend', function(event) {
      markerBposition = event.latLng.lat()+','+event.latLng.lng();
      calcRoute(markerAposition, markerBposition);
    });

    // Create a renderer for directions and bind it to the map.
    var rendererOptions = {
      map: map,
      suppressMarkers: true
    }

    directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions)

    google.maps.event.addListener(markerA, 'drag', function(event) {
      directionsDisplay.setMap(null);
      routePolyline.setMap(null);
    });

    google.maps.event.addListener(markerB, 'drag', function(event) {
      directionsDisplay.setMap(null);
      routePolyline.setMap(null);
    });
  }

  function handleNoGeolocation(errorFlag, currentLat, currentLon) {
    if (errorFlag) {
      var content = 'The Geolocation service failed, using default location.';
    } else {
      var content = 'Your browser doesn\'t support geolocation, using default location.';
    }

    var options = {
      map: map,
      position: new google.maps.LatLng(currentLat, currentLon),
      content: content
    };

    geoMarkerSettings(currentLat, currentLon)
    map.setCenter(options.position);
  }

  function DrawingControl(controlDiv, map) {
    // Set CSS for the control border
    var controlUI = document.createElement('div');
    controlUI.style.backgroundColor = '#fff';
    controlUI.style.border = '2px solid #fff';
    controlUI.style.borderRadius = '3px';
    controlUI.style.boxShadow = '0 2px 6px rgba(0,0,0,.3)';
    controlUI.style.cursor = 'pointer';
    controlUI.style.marginBottom = '22px';
    controlUI.style.textAlign = 'center';
    controlUI.title = 'Click to show street alerts';
    controlDiv.appendChild(controlUI);

    // Set CSS for the control interior
    var controlText = document.createElement('div');
    controlText.style.color = 'rgb(25,25,25)';
    controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
    controlText.style.fontSize = '16px';
    controlText.style.lineHeight = '38px';
    controlText.style.paddingLeft = '5px';
    controlText.style.paddingRight = '5px';
    controlText.innerHTML = 'Show All Alerts';
    controlUI.appendChild(controlText);

    // Setup the click event listeners:
    google.maps.event.addDomListener(controlUI, 'click', function() {
      // update the coordinates here
      var bounds = map.getBounds();
      var ne = bounds.getNorthEast(); // LatLng of the north-east corner
      var sw = bounds.getSouthWest(); // LatLng of the south-west corder
      var distance = google.maps.geometry.spherical.computeDistanceBetween (ne, sw)
      alert("Diagonal distance = " + distance)

      // Set current DOM alert information
      var currentAlertDom = getAlert();
      setAlert(currentAlertDom);

      // Load all streets info in the given area
      var latLng = map.getCenter()
      loadAllStreetAlerts(latLng.lat(), latLng.lng());
    });
  }

  function calcRoute(start, end) {
    var request = {
      origin: start,
      destination: end,
      travelMode: google.maps.TravelMode.DRIVING
    };

    // Route the directions and pass the response to a
    // function to create markers for each step.
    directionsService.route(request, function(response, status) {
      if (status == google.maps.DirectionsStatus.OK) {
        directionsDisplay.setDirections(response);
        showSteps(response);
      }
    });
  }

  function showSteps(directionResult) {
    // For each step, place a marker, and add the text to the marker's
    // info window. Also attach the marker to an array so we
    // can keep track of it and remove it when calculating new
    // routes.
    var myPolyline = new Array()
    var myRoute = directionResult.routes[0].legs[0];

    for (var i = 0; i < myRoute.steps.length; i++) {
      var stepEncodedPolyline = myRoute.steps[i].polyline.points;
      var decodedPolylinePath = google.maps.geometry.encoding.decodePath(stepEncodedPolyline);

      // In order to concatenate the polylines from each step
      // we need to remove the first element from decodedPolylinePath
      // if it is not the first step, because it's going to be repeated.
      if(i != 0) decodedPolylinePath.shift();
      myPolyline = myPolyline.concat(decodedPolylinePath);
    }

    encodedPath = google.maps.geometry.encoding.encodePath(myPolyline);
    // clone the last drawn alert and set the id to empty string
    var newAlert = getAlert();
    // This is a new alert so clean the existing ID
    newAlert.id = ""
    routePolyline = drawPath(myPolyline, COLOR_CURRENT, newAlert)
  }

  function drawPath(path, strokeColor, currentAlert) {
    // Define the custom symbols. All symbols are defined via SVG path notation.
    // They have varying stroke color, fill color, stroke weight,
    // opacity and rotation properties.
    var symbolStart = {
      path: 'M-1,-1 1,1 M1,-1 -1,1',
      strokeColor: COLOR_CURRENT,
      strokeWeight: 4
    };

    var symbolEnd = {
      path: 'M-2,-1 L0,0 -2,1 z',
      strokeColor: COLOR_CURRENT,
      fillColor: COLOR_CURRENT,
      strokeWeight: 4,
      fillOpacity: 1,
      rotation: -90
    };

    var symbolDirection = {
      path: 'M-1,-1 L1,0 -1,1',
      strokeColor: COLOR_CURRENT,
      strokeWeight: 4,
      rotation: -90
    };

    var orientedRouteIcons = [
      {
        icon: symbolStart,
        offset: '0%'
      }, {
        icon: symbolDirection,
        offset: '50%'
      }, {
        icon: symbolEnd,
        offset: '100%'
      }];

    // Show the symbols only for the current route
    if(currentAlert.id != "") orientedRouteIcons = [];

    var pathOptions = {
      map: map,
      path: path,
      icons: orientedRouteIcons,
      strokeColor: strokeColor,
      strokeWeight: 6,
      opacity: 0.8
    };

    var pathPolyline = new google.maps.Polyline(pathOptions);

    google.maps.event.addListener(pathPolyline, 'click', function(event) {
      // set the input screen data
      $("#street_id").text(currentAlert.id);
      // Hide/Show the DELETE button based on the alert action type
      if(currentAlert.id == "") {
        // If this is a CREATE action
        $("#delete_alert").hide();
      } else {
        // If this is not a CREATE action
        $("#delete_alert").show()
      }
      // Set the current configuration
      setAlert(currentAlert)
      // display the input screen
      $("#myModal").modal('show');
    });

    return pathPolyline;
  }

  google.maps.event.addDomListener(window, 'load', initialize);
});