
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
                 result.followersCount = results[0].get("followersCount");
                 result.followingsCount = results[0].get("followingsCount");
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

// 获取post
Parse.Cloud.define("timeline",function(request,response){
    var meId = request.params.meId;
    var userId = request.params.userId;
    var posts = [];
     var query = new Parse.Query("Connections");
        query.equalTo("followerId",meId);
        query.find().then(function(results){
            var id = [];
            for(var i = 0; i < results.length;i ++){
             var conn = results[i];
                var followerId = conn.get("followingId");
                id.push(followerId);
            }
          id.push(meId);
         var query2 = new Parse.Query("Posts");
          query2.containedIn("authorId",id);
          return query2.find();
        }).then(function(results){
             for(var j = 0; j < results.length; j ++){
                    posts.push(results[j]);
             }
           var query3 = new Parse.Query("Heart");
           query3.equalTo("authorId",meId);
           return query3.find();
        }).then(function(results){
            for(var i = 0; i < results.length; i++){
                for(var j = 0; j < posts.length; j ++){
                    if(results[i].get("postId") == posts[j].get("pid")){
                        posts[j].set("hasHearted",results[i].get("hasHearted"));
                        posts[j].save();
                    }
                }
            }
            response.success(posts);
        },
       function(error) {
             response.error("movie lookup failed" + JSON.stringify(error));
       });
});

// 获取comment
Parse.Cloud.define("getComments",function(request,response){
    var postId = request.params.postId;
    var query = new Parse.Query("Comment");
    query.equalTo("postId",postId);
    query.find({
           success:function(results){
                response.success(results);
           },
           error:function(){
               response.error("following look failed...");
           }

       });
});

// 删除post
Parse.Cloud.define("deletePost",function(request,response){
    var postId = request.params.postId;
    var query = new Parse.Query("Posts");
    query.equalTo("pid",postId);
    query.find().then(function(results){
        if(results != null && results.length > 0){
            return results[0].destroy();
        }else{
            return null;
        }
    }).then(function(result){
        var succ = false;
        if(result != null){
            succ = true;
        }
        response.success(succ);
    },
    function(error) {
        response.error("delete post error:" + JSON.stringify(error));
    });
});

// 获取关注
Parse.Cloud.define("getFollowings",function(request,response){
    var user_id = request.params.id;
    var query = new Parse.Query("Connections");
    query.equalTo("followerId",user_id);
    query.find().then(function(results){
        var id = [];
        for(var i = 0; i < results.length;i ++){
         var conn = results[i];
            var followerId = conn.get("followingId");
            id.push(followerId);
        }
     var query2 = new Parse.Query("Users");
      query2.containedIn("uid",id);
      return query2.find();
    }).then(function(results){
        var users = [];
         for(var j = 0; j < results.length; j ++){
                users.push(results[j]);
         }
         response.success(users);
    },
      function(error) {
            response.error("movie lookup failed" + JSON.stringify(error));
      });
});


// 获取粉丝
Parse.Cloud.define("getFollowers",function(request,response){
    var user_id = request.params.id;
    var query = new Parse.Query("Connections");
    query.equalTo("followingId",user_id);
    query.find().then(function(results){
        var id = [];
        for(var i = 0; i < results.length;i ++){
         var conn = results[i];
            var followerId = conn.get("followerId");
            id.push(followerId);
        }
     var query2 = new Parse.Query("Users");
      query2.containedIn("uid",id);
      return query2.find();
    }).then(function(results){
        var users = [];
         for(var j = 0; j < results.length; j ++){
                users.push(results[j]);
         }
         response.success(users);
    },
      function(error) {
            response.error("movie lookup failed" + JSON.stringify(error));
      });
});


//  关注
Parse.Cloud.define("setFollow",function(request,response){
    var me = request.params.me;
    var toggleId = request.params.toggleId;
    var follow = request.params.follow;
    var isSuccess = false;
    var connection = new Parse.Object("Connections");
    connection.set("followerId",me);
    connection.set("followingId",toggleId);
    connection.save(null,{
        success:function(conn){
            response.success(conn);
        },
        error:function(connection,error){
           console.log("failed to create connection!");
            response.error(error);
        }
    });
});

Parse.Cloud.afterSave("Connections", function(request, response) {
        var query = new Parse.Query("Users");
        query.equalTo("uid",request.object.get("followerId"));
        query.find({
            success:function(results){
            console.log("connection aftersave:" + results);
                 if(results != null && results.length > 0){
                    console.log(results);
                    var user = results[0];
                    user.set("followingsCount",user.get("followingsCount") + 1);
                    user.save();
                 }
            },
            error:function(){
                response.error("following look failed...");
            }

        });

          var query2 = new Parse.Query("Users");
           query2.equalTo("uid",request.object.get("followingId"));
           query2.find({
                success:function(results){
                console.log("connection aftersave:" + results);
                     if(results != null && results.length > 0){
                        console.log(results);
                        var user = results[0];
                        user.set("followersCount",user.get("followersCount") + 1);
                        user.save();
                     }
                },
                error:function(){
                    response.error("following look failed...");
                }

            });
});


// 取消关注
Parse.Cloud.define("setCancelFollow",function(request,response){
    var followerId = request.params.followerId;
    var followingId = request.params.followingId;
    var query = new Parse.Query("Connections");
    query.equalTo("followerId",followerId);
    query.equalTo("followingId",followingId);
    query.find().then(function(results){
       console.log("cancel follow results:" + JSON.stringify(results))
       var succ = false;
       return results[0].destroy();
    }).then(function(results){
        console.log("destory follow results:" + JSON.stringify(results));
        var succ = false;
        if(results != null && results.length > 0){
            succ = true;
        }
        response.success(true);
    },
    function(error) {
          response.error("movie lookup failed" + JSON.stringify(error));
    });
});

// add/cancel ❤
Parse.Cloud.define("setHeart",function(request,response){
    var postId = request.params.postId;
    var authorId = request.params.authorId;
    var hasHearted = request.params.hasHearted;
    var query = new Parse.Query("Heart");
    query.equalTo("postId",postId);
    query.equalTo("authorId",authorId);
    query.find().then(function(results){
        if(results == null || results.length == 0){
            var heart = new Parse.Object("Heart");
            heart.set("postId",postId);
            heart.set("authorId",authorId);
            heart.set("hasHearted",hasHearted);
            return heart.save();
        }else{
            results[0].set("hasHearted",hasHearted);
            return results[0].save();
        }
    }).then(function(results){
        var query2 = new Parse.Query("Posts");
        query2.equalTo("pid",postId);
        return query2.find();
    }).then(function(results){
        var succ = false;
        if(results != null && results.length > 0){
            var hc = results[0].get("heartCount");
            if(hasHearted){
                results[0].set("heartCount",hc + 1);
            }else{
                if(hc > 0){
                results[0].set("heartCount",hc - 1);
                }
            }
            results[0].save();
            succ = true;
        }
        response.success(succ);
    },function(error) {
         response.error("setHeart failed" + JSON.stringify(error));
    });
});

Parse.Cloud.afterDelete("Connections", function(request, response) {
        var query = new Parse.Query("Users");
        query.equalTo("uid",request.object.get("followerId"));
        query.find({
            success:function(results){
            console.log("connection aftersave:" + results);
                 if(results != null && results.length > 0){
                    console.log(results);
                    var user = results[0];
                    user.set("followingsCount",user.get("followingsCount") - 1);
                    user.save();
                 }
            },
            error:function(){
                response.error("following look failed...");
            }

        });
          var query2 = new Parse.Query("Users");
           query2.equalTo("uid",request.object.get("followingId"));
           query2.find({
                success:function(results){
                console.log("connection aftersave:" + results);
                     if(results != null && results.length > 0){
                        console.log(results);
                        var user = results[0];
                        user.set("followersCount",user.get("followersCount") - 1);
                        user.save();
                     }
                },
                error:function(){
                    response.error("following look failed...");
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

Parse.Cloud.afterSave("Comment",function(request){
      var query = new Parse.Query("Posts");
      var postId = request.object.get("postId");
      query.equalTo("pid",postId);
      query.find({
          success: function(results) {
              if(results != null && results.length > 0){
                    results[0].set("commentCount",results[0].get("commentCount") + 1);
                    results[0].save();
              }
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
