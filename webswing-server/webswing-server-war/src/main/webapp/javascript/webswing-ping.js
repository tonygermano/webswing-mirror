define([], function amdFactory() {
    "use strict";
    return function PingModule() {
        var module = this;
        var api,intervalHandle;
        var worker, ping, severity, mute;
        var count = 6, interval = 5, maxLatency = 500, notifyIf = 3;
        module.injects = api = {
            cfg: 'webswing.config',
            sendInput: 'input.sendInput',
            showWarning: 'dialog.showNetworkBar',
            hideWarning: 'dialog.hideNetworkBar',
            dialogContent: 'dialog.content'
        };
        module.provides = {
            mutePingWarning: mutePingWarning,
            start: start,
            dispose: dispose
        };
        module.ready = function () {
        };

        function start(){
            mute = 0;
            ping = getArrayWithLimitedLength(count);
            var connectionUrl=api.cfg.connectionUrl;
            if (connectionUrl.indexOf('http') != 0) {
                var host = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '')
                connectionUrl = host + connectionUrl;
            }
            var blobURL = URL.createObjectURL(new Blob(['onmessage=(', PingMonitor.toString(), ')("' + connectionUrl + '",' + interval + ')'], {type: 'application/javascript'}));
            worker = new Worker(blobURL);
            worker.onmessage = function (e) {
                var p = JSON.parse(e.data);
                ping.push(p);
                if (p.result === 'ok') {
                    api.sendInput({
                        timestamps: {
                            ping: p.latency
                        }
                    });
                    //first success after offline status - clear pings
                    if (severity == 2) {
                        ping = getArrayWithLimitedLength(count);
                    }
                }
                evaluateNetworkStatus();
            };
            intervalHandle=setInterval(function(){
                worker.postMessage('doPing');
            }, interval*1000);
        }

        function evaluateNetworkStatus() {
            var msg = null;
            var currentSeverity = 0;
            var warns = 0;
            var fails = 0;
            for (var i in ping) {
                var p = ping[i];
                if ("error" === p.result || "timeout" === p.result) {
                    warns++;
                    fails++;
                } else if (p.latency > maxLatency) {
                    warns++;
                }
            }
            if (fails == count) {
                msg = api.dialogContent.networkOfflineWarningDialog;
                severity = 2;
            } else if (warns >= notifyIf) {
                msg = api.dialogContent.networkSlowWarningDialog;
                severity = 1;
            } else {
                severity = 0;
            }

            if (msg != null && severity != mute) {
                api.showWarning(msg)
            } else {
                api.hideWarning();
            }
        }

        function mutePingWarning(severity){
            mute=severity;
        }

        function PingMonitor(connectionUrl, is) {
            var interval = is * 1000;

            function ping() {
                var startTime = new Date().getTime();
                var oReq = new XMLHttpRequest();
                oReq.addEventListener("load", function (e) {
                    result("ok", new Date().getTime() - startTime);
                });
                oReq.addEventListener("error", function (e) {
                    result("error", new Date().getTime() - startTime);
                });
                oReq.addEventListener("timeout", function (e) {
                    result("timeout", new Date().getTime() - startTime);
                });
                oReq.open("GET", connectionUrl + "rest/ping");
                oReq.timeout = interval;
                oReq.send();
            }

            function result(status, ms) {
                postMessage(JSON.stringify({result: status, latency: ms}));
            }

            return function onmessage(msg){
                if(msg.data==='doPing'){
                    ping();
                }
            }
        }


        function getArrayWithLimitedLength(length) {
            var array = new Array();
            array.push = function () {
                if (this.length >= length) {
                    this.shift();
                }
                return Array.prototype.push.apply(this, arguments);
            }
            return array;
        }

        function dispose(){
            if(worker!=null){
                worker.terminate();
            }
            if(intervalHandle!=null){
                clearInterval(intervalHandle);
                intervalHandle=null;
            }

        }

    };
});