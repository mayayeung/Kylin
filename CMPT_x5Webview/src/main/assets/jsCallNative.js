    function callSyn() {
        alert(dsBridge.call("testSyn", "testSyn"))
    }

    function callAsyn() {
        dsBridge.call("testAsyn","testAsyn", function (v) {
            alert(v)
        })
    }

    function hasNativeMethod(name) {
        alert(dsBridge.hasNativeMethod(name))
    }