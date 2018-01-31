//cordova.define("cordova-plugin-dialog.CustomDialog",function(require, exports, module) {
        var exec = require("cordova/exec");
        module.exports = {
            show: function(content){
                exec(
                function(message){//成功回调function
                    console.log(message);
                },
                function(message){//失败回调function
                    console.log(message);
                },
                "CustomDialog",//feature name
                "show",//action
                [ content ]//要传递的参数，json格式
                );
            },
            save_data:function(jid ,password ,is_remember){
              exec(
              function(message){//成功回调function
                  console.log(message);
              },
              function(message){//失败回调function
                  console.log(message);
              },
              "CustomDialog",//feature name
              "save_data",//action
              [ jid ,password ,is_remember ]//要传递的参数，json格式
              );
          },
          show_data:function(){
            exec(
            function(message){//成功回调function
                console.log(message);
            },
            function(message){//失败回调function
                console.log(message);
            },
            "CustomDialog",//feature name
            "show_data",//action
            []//要传递的参数，json格式
            );
        },
		chat_video:function(){
            exec(
            function(message){//成功回调function
                console.log(message);
            },
            function(message){//失败回调function
                console.log(message);
            },
            "CustomDialog",//feature name
            "chat_video",//action
            []//要传递的参数，json格式
            );
        }

        }
//});