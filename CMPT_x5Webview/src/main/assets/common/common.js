var score = 70;
if(score>60){
    document.write("很棒!");
}else{
    document.write("加油!");
}



function nativeToast(message){
    window.jsBridge.showToast(message);
}

var RRT_PROTOCOL_SCHEME = 'rrtBridge';
var uniqueId = 1;
var callbackId;
window.Hybrid = window.Hybrid || {};

function invokeFunc(){
//    alert("调用函数了");
    rrt({
        tagname:'showToast',
        param:'abcde',
        callback:function(data){
            alert('msg form native ：'+data);
        }

    });
}
   function receive(msg){
        window.Hybrid[callbackId](msg);
   }

    // H5与Native基本交互
    var rrt = function(params) {
        if (!params.tagname) {
            alert('必须包含tagname');
        }
        //生成唯一执行函数，执行后销毁
        callbackId = 'hybrid_' + params.tagname + '_' + (uniqueId++) + '_'+ new Date().getTime();
        var tmpFn;

        //处理有回调的情况
        if (params.callback) {
            tmpFn = params.callback;
            params.callback = callbackId;

            window.Hybrid[callbackId] = function(data) {
                tmpFn(data);
                //delete window.Hybrid[t];
            }
        }

        window.jsBridge.showToast(JSON.stringify(params));
//        bridgePostMsg(params);
    };


    var bridgePostMsg = function(params) {
        var url = buildMsgUrl(params);
        //兼容ios6
        var ifr = $('<iframe style="display: none;" src="' + url + '"/>');
        console.log(params.tagname + '-hybrid请求发出-' + new Date().getTime() + 'url: ' + url)
        if ($.os.android) {
            //Android情况协议发的太快会有问题
            setTimeout(function() {
                $('body').append(ifr);
            })
        } else {
            $('body').append(ifr);
        }

        //这样会阻断第二次请求
        //window.location = url;

        setTimeout(function() {
            ifr.remove();
            ifr = null;
        }, 1000);
    };


    var buildMsgUrl = function(params) {
        var paramStr = '';
        var flag = '?';
        var url = RRT_PROTOCOL_SCHEME+ '://' + params.tagname;

        if (params.callback) {
            flag = '&';
            url += '?callback=' + params.callback;
            //delete params.callback;
        }
        if (params.param) {
            paramStr = typeof params.param == 'object' ? JSON.stringify(params.param) : params.param;
            url = url + flag + 'param=' + encodeURIComponent(paramStr);
        }
        return url;
    };