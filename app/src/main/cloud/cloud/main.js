
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("signup", function(request, response) {
    var result = {
        name:"xiaofan",
        avatar:"http://www.3gmfw.cn/qqtouxiang/UploadPic/2012-9/20129921285294.jpg",
        followersCount:12,
        followingsCount:34,
        id:123435,
        me:1
    };
    response.success(request);
   /* Parse.Cloud.httpRequest({
      url: 'https://api.weibo.com/2/users/show.json',
      params: {
      access_token:request.token,
      uid:request.id,
      source:"2718899853"
      },
      success: function(httpResponse) {
        console.log(httpResponse.text);
        response.success(httpResponse.text);
      },
      error: function(httpResponse) {
        console.error('Request failed with response code ' + httpResponse.status);
        response.success("MY ERROR!"+httpResponse.text);
      }
    });*/

});
