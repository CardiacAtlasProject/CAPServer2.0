(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('DownloadStudyController', DownloadStudyController);

    DownloadStudyController.$inject = ['$timeout', '$uibModalInstance', '$stateParams', '$resource'];
    
    function DownloadStudyController($timeout, $uibModalInstance, $stateParams, $resource) {
    		var vm = this;
    		
    		vm.isPreparing = true;
    		vm.cancel = cancel;
    		vm.download = download;
    		vm.studyUid = $stateParams.studyUid;
    		vm.isCanceling = false;
    		
    		$timeout(function() {
    			vm.bar = new ProgressBar.Line('#progressbar', {
    				strokeWidth: 1,
    				easing: 'easeInOut',
    				color: '#5d9ddc',
    				trailColor: '#eee',
    				trailWidth: 1
    			});
    			
    			start();
    		})
    		
    		var getImage = $resource('/api/view-image-studies/download', {}, {
                'get': {
                    method: 'GET',
                    transformResponse: function (data) {
                        if (data) {
                            data = angular.fromJson(data);
                        }
                        return data;
                    }
                }
            });
    		    		
    		function start() {
    			getImage.get({
    				studyUid: vm.studyUid,
    				status: 'start'
    			}, advance, downloadError );
    		}
    		
    		function advance(response) {
    			vm.bar.set((response.currentImageNumber + 1) / response.totalImages);
    			
    			// check user canceling first
    			if( vm.isCanceling ) {
    				return;
    			}
    			
    			if( response.currentImageNumber + 1 == response.totalImages) {
    				getImage.get({
    					studyUid: vm.studyUid,
    					status: 'stop'
    				}, ready, downloadError );
    			} else {
    				getImage.get({
    					studyUid: vm.StudyUid,
    					status: 'continue'
    				}, advance, downloadError );
    			}
    		}
    		
    		function ready(response) {
    			vm.isPreparing = false;
    		}
    		
    		function downloadError(response) {
    			$uibModalInstance.dismiss(response.data.error);
    		}
    		
    	    function cancel() {
    	    		vm.isCanceling = true;
    	        $uibModalInstance.dismiss('User cancelled.');
    	    }
    	    
    	    function download() {
    	    		$uibModalInstance.close();
    	    }
    }
        
})();