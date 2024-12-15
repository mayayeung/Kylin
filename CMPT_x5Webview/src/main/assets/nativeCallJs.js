    dsBridge.register('addValue', function (r, l) {
        return r + l;
    })

    dsBridge.registerAsyn('append', function (arg1, arg2, arg3, responseCallback) {
        responseCallback(arg1 + " " + arg2 + " " + arg3);
    })

    dsBridge.registerAsyn('startTimer', function (responseCallback) {
        var t = 0;
        var timer = setInterval(function () {
            if (t == 5) {
                responseCallback(t)
                clearInterval(timer)
            } else {
                // if the 2nd argument is false,  the java callback handler will be not removed!
                responseCallback(t++, false)
            }
        }, 1000)

    })

    // namespace test for syn functions
    dsBridge.register("syn", {
        tag: "syn",
        addValue:function (r,l) {
            return r+l;
        },
        getInfo: function () {
            return {tag: this.tag, value:8}
        }
    })

    // namespace test for asyn functions
    dsBridge.registerAsyn("asyn", {
        tag: "asyn",
        addValue:function (r,l, responseCallback) {
            responseCallback(r+l);
        },
        getInfo: function (responseCallback) {
            responseCallback({tag: this.tag, value:8})
        }
    })