
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
                result.avatar = resultJson.avatar_large;
                result.name = resultJson.name;
                if(results.length > 0){
                 result.objectId = results[0].id;
                 result.isRegisterOnServer = true;
                }else{
                    result.objectId = "";
                    result.isRegisterOnServer = false;
                }
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

// 获取关注
Parse.Cloud.define("getFollowings",function(request,response){
    var user_id = request.params.id;
    var query = new Parse.Query("Connections");
    query.equalTo("followingId",user_id);
    query.find({
        success:function(results){
            response.success(results);
        },
        error:function(){
            response.error("following look failed...");
        }

    });
});

// 获取粉丝
Parse.Cloud.define("getFollowers",function(request,response){
    var user_id = request.params.id;
    var query = new Parse.Query("Connections");
    query.equalTo("followerId",user_id);
    query.find({
        success:function(results){
            response.success(results);
        },
        error:function(){
            response.error("following look failed...");
        }

    });
});

// 获取动态
Parse.Cloud.define("getTimeline",function(request,response){

});


Parse.Cloud.afterSave("Posts",function(request){
      var query = new Parse.Query("Posts");
      query.get(request.object.id, {
          success: function(post) {
            post.set("pid",request.object.id.hashCode());
            post.save();
          },
          error: function(error) {
            console.error("Got an error " + error.code + " : " + error.message);
          }
        });
})

Parse.Cloud.job("setPostsId",function(request,status){
     var query = new Parse.Query("Posts");
      query.each(function(post) {
            var pid = post.get("pid");
            if(undefined == pid){
                post.set("pid",post.id.hashCode());
            }
            post.save();
           return null;
       }).then(function() {
         // Set the job's success status
         status.success("Migration completed successfully.");
       }, function(error) {
         // Set the job's error status
         status.error("Uh oh, something went wrong.");
       });
});


 String.prototype.hashCode = function(){
 	var hash = 0;
 	if (this.length == 0) return hash;
 	for (i = 0; i < this.length; i++) {
 		char = this.charCodeAt(i);
 		hash = ((hash<<5)-hash)+char;
 		hash = hash & hash; // Convert to 32bit integer
 	}
 	return hash;
 }
