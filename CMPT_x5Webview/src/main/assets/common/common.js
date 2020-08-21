var score = 70;
if(score>60){
    document.write("很棒!");
}else{
    document.write("加油!");
}

function invokeFunc(){
    alert("调用函数了");
}

function nativeToast(message){
    window.jsBridge.showToast(message);
}
