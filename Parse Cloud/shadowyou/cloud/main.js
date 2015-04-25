
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
// Parse.Cloud.define("hello", function(request, response) {
//   response.success("Hello world!");
// });
Parse.Cloud.define("Logger", function(request, response) {
 
  // console.log(request.params);
  // response.success();
});
Parse.Cloud.beforeSave("Parent_relation_child", function(request, response) {
  var device = request.object.get('device');
  var parent_user = request.object.get('user');
  var ParentObj = Parse.Object.extend("Parent_relation_child");
  var ParentQuery = new Parse.Query(ParentObj);
  ParentQuery.equalTo("user",parent_user);
  ParentQuery.equalTo("device",device);
  ParentQuery.find({
    success:function(results){
      if(results.length > 0){
        response.error("duplicate");
      }else{
        // alert("Insert");
        response.success();
      }
    },
    error: function(object, error) {
      // alert("NULL");
    }
  });
});


Parse.Cloud.afterSave("Parent_relation_child", function(request, response) {
  var device = request.object.get('device');
  var parent_user = request.object.get('user');
  var DeviceObj = Parse.Object.extend("Device");
  var DeviceQuery = new Parse.Query(DeviceObj);
  DeviceQuery.equalTo("objectId",device.id);
  DeviceQuery.find({
    success: function(reuslts) {
      console.log(reuslts);
      if(reuslts.length > 0){
        reuslt = reuslts[0];
        // alert("Update");
        var temp_array = reuslt.get('arrayParent');
        if(temp_array){
          var flag = false;
          for (var j = 0; j < temp_array.length; j++) {
            if(temp_array[j].id == parent_user.id){
              flag = true;
            }
          };
          if(!flag){
            temp_array.push(parent_user);
          }
        }else{
          temp_array = [parent_user];
        }
        obj = reuslt;
        obj.set("arrayParent", temp_array);
        obj.save();
      }
    },
    error: function(object, error) {
      console.log(object,error);
      // The object was not retrieved successfully.
      // error is a Parse.Error with an error code and message.
    }
  });
});

Parse.Cloud.afterDelete("Parent_relation_child", function(request) {
  var device = request.object.get('device');
  var parent_user = request.object.get('user');
  var locationObj = Parse.Object.extend("Device");
  var query = new Parse.Query(locationObj);
  query.equalTo("objectId", device.id);
  query.find({
    success: function(reuslts) {
      if(reuslts.length > 0){
        reuslt = reuslts[0];
        // alert("Delete");
        // DELETE ยังไม่ได้อัพเดท
          var temp_array = reuslt.get('arrayParent');
          var flag = false;
          for (var j = 0; j < temp_array.length; j++) {
            if(temp_array[j].id == parent_user.id){
              temp_array.splice(j, 1);
            }
          obj = reuslt;
          obj.set("arrayParent", temp_array);
          obj.save();
        };
      }
    },
    error: function(object, error) {
      // The object was not retrieved successfully.
      // error is a Parse.Error with an error code and message.
    }
  });
});

Parse.Cloud.beforeSave("Device", function(request, response) {
  var deviceId = request.object.id;
  var DeviceObj = Parse.Object.extend("Device");
  var DeviceQuery = new Parse.Query(DeviceObj);
  var _ = require('underscore.js');
  var currentLocation = {
    latitude:request.object.get('location').latitude,
    longitude:request.object.get('location').longitude,
  }
  var pushData = [];

  DeviceQuery.include("arrayParent");
  DeviceQuery.equalTo("objectId",deviceId);
  DeviceQuery.find().then(function(results) {
    if(results.length > 0){
      // alert("length "+results.length);
      var lastLocation = {
        latitude:results[0].get('location').latitude,
        longitude:results[0].get('location').longitude,
      }
      var promises = [];
      var users = results[0].get("arrayParent");
      _.each(users, function(user) {
        var AreaObj = Parse.Object.extend("Area");
        var AreaQuery = new Parse.Query(AreaObj);
        // AreaQuery.include("user");
        AreaQuery.equalTo("user",user);
        promises.push(
          AreaQuery.first({
            success: function(areasObj) {
              if(areasObj){
                var area = JSON.parse(areasObj.get("Area"));
                var push_user = areasObj.get("user")
                var pushMsg = "";

                if(area){
                  for (var i = 0; i < area.length; i++) {
                    var polygon = area[i].latlnglist;
                    var check_area = area[i].latlnglist;
                    var label = area[i].label;
                    var push_type = "";
                    if(inside(lastLocation,check_area) == true && inside(currentLocation,check_area) == false){
                      pushMsg = 'ออกข้างนอก '+label; 
                      push_type = "goout";           
                    }else if(inside(lastLocation,check_area) == false && inside(currentLocation,check_area) == true){
                      pushMsg = 'เข้าข้างใน '+label;             
                      push_type = "goin";           
                    }else if(inside(lastLocation,check_area) == false && inside(currentLocation,check_area) == false){
                      pushMsg = "อยู่ข้างนอก "+label;  
                      push_type = "out";           
                    }else{
                      pushMsg = "อยู่ข้างใน "+label;  
                      push_type = "in";           
                    }
                    pushData.push({
                      push_user:push_user,
                      pushMsg:pushMsg,
                      push_type:push_type
                    });
                  };
                }
              }
            },
            error: function() {
              response.error();
            }
          })        
        );
      });
      // Return a new promise that is resolved when all of the deletes are finished.
      return Parse.Promise.when(promises);
    }else{
      // response.success();
      // return;
    }
  }).then(function() {
    var promises = [];
    _.each(pushData, function(pushItem) {
      // if(true){
      if(pushItem.push_type == "goout" || pushItem.push_type == "goin" ){
        var pushquery = new Parse.Query(Parse.Installation);
        pushquery.equalTo("user",pushItem.push_user);
        promises.push(
          Parse.Push.send({
            where: pushquery, // Set our Installation query
            data: {
              alert: pushItem.pushMsg,
              action: "com.parse.loginsample.basic.SampleProfileActivity",
            }
            }, {
            success: function() {
              console.log(pushItem.pushMsg + " To : User "+pushItem.push_user.id);
            },
            error: function(error) {

            }
          })      
        );
        
      }
    });
    return Parse.Promise.when(promises);
  }).then(function() {
    response.success();
  });
});

Parse.Cloud.afterSave("Device", function(request, response) {
  var deviceId = request.object.id;
  var level = request.object.get('level');
  var location = request.object.get('location');
  var pluged = request.object.get('pluged');
  var DeviceHistoryObj = Parse.Object.extend("DeviceHistory");
  var DeviceHistoryQuery = new Parse.Query(DeviceHistoryObj);
  DeviceHistoryQuery.equalTo("deviceId",request.object);
  DeviceHistoryQuery.descending("createdAt");
  DeviceHistoryQuery.limit(1);
  DeviceHistoryQuery.find({
    success: function(reuslts) {
      alert(reuslts.length+" history");
      if(reuslts.length > 0){
        if(getDistance(location,reuslts[0].get('location')) >= 50){
          var obj = new DeviceHistoryObj();
          obj.set('deviceId',request.object);
          obj.set('location',location);
          obj.set('level',level);
          obj.set('pluged',pluged);
          obj.save();
        }
      }else{
        var obj = new DeviceHistoryObj();
        obj.set('deviceId',request.object);
        obj.set('location',location);
        obj.set('level',level);
        obj.set('pluged',pluged);
        obj.save();
       }
    },
    error: function(object, error) {
      // The object was not retrieved successfully.
      // error is a Parse.Error with an error code and message.
    }
  });
});

var inside = function (point, vs) {
  // ray-casting algorithm based on
  // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
  var x = point['latitude'], y = point['longitude'];
  var inside = false;
  for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
      var xi = vs[i]['latitude'], yi = vs[i]['longitude'];
      var xj = vs[j]['latitude'], yj = vs[j]['longitude'];
      var intersect = ((yi > y) != (yj > y))
          && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
      if (intersect) inside = !inside;
  }
  return inside;
};

var rad = function(x) {
  return x * Math.PI / 180;
};

var getDistance = function(p1, p2) {
  var R = 6378137; // Earth’s mean radius in meter
  var dLat = rad(p2.latitude - p1.latitude);
  var dLong = rad(p2.longitude - p1.longitude);
  var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(rad(p1.latitude)) * Math.cos(rad(p2.latitude)) *
    Math.sin(dLong / 2) * Math.sin(dLong / 2);
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  var d = R * c;
  return d; // returns the distance in meter
};

