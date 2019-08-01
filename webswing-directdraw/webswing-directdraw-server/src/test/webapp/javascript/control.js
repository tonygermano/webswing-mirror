$.ajax({
	url : "/draw/tests",
}).done(function(d) {
    ES6Promise.polyfill();
	var data = $.parseJSON(d);

	var selected = getUrlParameter('n');

	data.forEach(function(method, index, array) {
		if (selected == null || (selected != null && index == selected)) {
			document.body.innerHTML += '<div id="' + method + 'label"></div>';
			document.body.innerHTML += '<canvas id="' + method + 'DD" style="width:500px;height:100px"></canvas>';
			document.body.innerHTML += '<img id="' + method + 'PNG" />';
		}
	});
    var dpr= Math.ceil(window.devicePixelRatio) || 1;
    console.log("DPR="+dpr);
	var dd = new WebswingDirectDraw({dpr:dpr,logTrace:true});
	var sequence = Promise.resolve();
	data.forEach(function(method, index) {
		sequence = sequence.then(function(resolved) {
			return executeMethod(method,index);
		});
	});

	function executeMethod(method, index) {
		return new Promise(function(resolve, reject) {
			if (selected == null || (selected != null && index == selected)) {
				$.ajax({
					url : "/draw?reset&test=" + method,
				}).done(function(d) {
					var canvasDD = document.getElementById(method+'DD');
					canvasDD.width=500*dpr;
					canvasDD.height=100*dpr;
					var ctxDD = canvasDD.getContext("2d");
					var json = $.parseJSON(d);
					addInfo(json, index, document.getElementById(method + "label"));
					var sequence = Promise.resolve();
					json.protoImg.forEach(function(img, index) {
						sequence = sequence.then(function(resolved) {
							console.log('started ' + method);
							return dd.draw64(img);
						}).then(function(resultCanvas) {
							console.log('finished ' + method);
							ctxDD.drawImage(resultCanvas, 0, 0);
						}, function(error) {
							console.log(error);
						});
					});
					sequence.then(function() {
						resolve();
					});
					json.originalImg.forEach(function(img) {
						drawImage(method, img);
					});
				});
			}else{
				resolve();
			}
		});
	}

});

function addInfo(json, index, element) {
	element.innerHTML = index + ". " + ((json.protoRenderSize / json.originalRenderSize) * 100).toPrecision(4) + "% in size, "
			+ ((json.protoRenderTime / json.originalRenderTime) * 100).toPrecision(4) + "% in time";
}

function drawImage(method, b64image) {
    var imageObj = document.getElementById(method + 'PNG');
    // imageObj.onload = function () {
    //     var context = canvas.getContext("2d");
    //     context.drawImage(imageObj, 500, 0);
    //     imageObj.onload = null;
    //     imageObj.src = '';
    // };
    imageObj.src = 'data:image/png;base64,' + b64image;
}

function getUrlParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
	var sURLVariables = sPageURL.split('&');
	for ( var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam) {
			return sParameterName[1];
		}
	}
}