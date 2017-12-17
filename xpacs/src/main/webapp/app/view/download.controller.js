(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('DownloadStudyController', DownloadStudyController);

    DownloadStudyController.$inject = ['$stateParams','$uibModalInstance', '$http'];

    function DownloadStudyController($stateParams, $uibModalInstance, $http) {
        var vm = this;
        
        vm.studyUid = $stateParams.studyUid;
        vm.patientId = $stateParams.patientId;
        vm.cancel = cancel;
        vm.isPreparing = true;
        
        function cancel(reason) {
        		$uibModalInstance.dismiss('User cancelled.');
        }
                
        $http({
        		method: 'GET',
        		url: '/api/dicom/study',
        		params: {
        			patientId: vm.patientId,
        			studyInstanceUid: vm.studyUid
        		}
        })
        .success( function(data) {
        		$uibModalInstance.close({ folder: data.folder, id: vm.studyUid });
        })
        .error( function(error) {
        		if( error )
        			$uibModalInstance.dismiss(error.message);
        		else
        			$uibModalInstance.dismiss('Unknown error');
        });
        
    }
})();
