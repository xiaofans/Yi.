
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("signup", function(request, response) {
  var p = request.params.name_value_pairs;
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
        var result = {};
        var query = new Parse.Query("User");
        query.equalTo("id", resultJson.id);
        query.find({
            success:function(results){
                result.isRegisterOnServer = true;
            },
            error:function(){
            result.isRegisterOnServer = false;
            }
        });
        result.id = resultJson.id;
        result.avatar = resultJson.avatar_large;
        result.name = resultJson.name;
        response.success(result);
      },
      error: function(httpResponse) {
        console.error('Request failed with response code ' + httpResponse.status);
        response.success("MY ERROR! token is;" + p.token +" "+httpResponse.text);
      }
    });

});
