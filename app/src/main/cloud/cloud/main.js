
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

// 获取所有动态
Parse.Cloud.define("getTimeline",function(request,response){

});

//  关注
Parse.Cloud.define("setFollow",function(request,response){
    var me = request.params.me;
    var toggleId = request.params.toggleId;
    var follow = request.params.follow;
    var query = new Parse.Query("Connections");
    query.equalTo("followingId",me);
    query.equalTo("followerId",toggleId);
    var isSuccess = false;
    var connection = new Parse.Object("Connections");
    connection.set("followingId",me);
    connection.set("followerId",toggleId);
    connection.save(null,{
        success:function(conn){
            response.success(conn);
        },
        error:function(connection,error){
           console.log("failed to create connection!");
        }
    });
});

// 取消关注
Parse.Cloud.define("setCancelFollow",function(request,response){
    var id = request.params.objectId;
    var query = new Parse.Query("Connections");
    query.get(id,{
        success:function(connection){
            if(connection != null){
                connection.remove();
                response.success("true");
            }
        },
        error:function(){
         console.log("failed to cancel follow!");
         response.success("false");
        }
    });
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
