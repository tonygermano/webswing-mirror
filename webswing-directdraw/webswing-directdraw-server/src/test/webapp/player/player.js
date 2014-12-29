window.onload = function() {
	var fileInput = document.getElementById('fileInput');
	var btn = document.getElementById('buttonOK');
	var canvas = document.getElementById('canvas');
	var dd = new WebswingDirectDraw();

	btn.addEventListener('click', function(e) {
		var file = fileInput.files[0];

		var reader = new FileReader();

		reader.onload = function(e) {
			var dataArray = [];
			var currentOffset = 0;
			var buffer = reader.result;
			while (currentOffset < buffer.byteLength) {
				var dataviewlength = new DataView(buffer, currentOffset, 4).getUint32(0);
				var data = new Uint8Array(buffer, currentOffset + 4, dataviewlength);
				dataArray.push(data);
				currentOffset += 4 + dataviewlength;
			}
			dataArray.reduce(function(seq, current,index) {
				return seq.then(function() {
					console.log("drawing frame no."+index);
					return dd.drawBin(current, canvas);
				},function(e){
					console.log("drawing frame no."+index+" failed.");
				});
			}, Promise.resolve()).then(function(){
				console.log("done!");
			});
		}
		reader.readAsArrayBuffer(file);
	});
};