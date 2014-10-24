
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("signup", function(request, response) {
  var p = request.params.nameValuePairs;
   Parse.Cloud.httpRequest({
      url:'https://api.weibo.com/2/users/show.json',
      params: {
      access_token:p.token,
      uid:p.id,
      source:"2718899853"
      },
      success: function(httpResponse) {
        console.log(httpResponse.text);
        var resultJson = JSON.parse(httpResponse.text);
        var query = new Parse.Query("Users");
        query.equalTo("uid", resultJson.id);
        query.find({
            success:function(results){
                var result = {};
                result.id = resultJson.id;
                result.isRegisterOnServer = true;
                result.avatar = resultJson.avatar_large;
                result.name = resultJson.name;
                result.objectId = results[0].id;
                response.success(result);
            },
            error:function(){
                var result = {};
                result.id = resultJson.id;
                result.isRegisterOnServer = false;
                result.avatar = resultJson.avatar_large;
                result.name = resultJson.name;
                response.success(result);
            }
        });


      },
      error: function(httpResponse) {
        console.error('Request failed with response code ' + httpResponse.status);
        response.success("MY ERROR! token is;" + p.token +" "+httpResponse.text);
      }
    });

});
