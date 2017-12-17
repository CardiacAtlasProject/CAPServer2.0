(function() {
	'use strict';

	angular
	.module('xpacswebApp')
	.factory('DownloadService', DownloadService);

	DownloadService.$inject = ['$http', 'AlertService'];

	function DownloadService ($http, AlertService) {
		
		var service = {
				get: get
		}
		
		return service;
		
		// http://jaliyaudagedara.blogspot.co.nz/2016/05/angularjs-download-files-by-sending.html

		function get( folder, filename ) {
			var url = '/api/dicom/download/' + folder;
			
			$http({
				method: 'GET',
				url: url,
				params: {
					filename: filename
				},
				responseType: 'arraybuffer'
			})
			.success(function (data, status, headers) {
				headers = headers();
				
				var filename = headers['x-filename'];
				var contentType = headers['content-type'];
				
				var linkElement = document.createElement('a');
				try {
					
					var blob = new Blob([data], { type: contentType });
					var url = window.URL.createObjectURL(blob);
					
					linkElement.setAttribute('href', url);
					linkElement.setAttribute('download', filename);
					
					var clickEvent = new MouseEvent("click", {
						"view": window,
						"bubbles": true,
						"cancelable": false
					});
					linkElement.dispatchEvent(clickEvent);
					
				} catch (ex) {
					console.log(ex);
					AlertService.error('Something is wrong. See the console.log.');
				}
			})
			.error(function (data) {
				console.log(data);
				AlertService.error('Something is wrong. See the console.log.');
			});
			
		}
	}
})();
