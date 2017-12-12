(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('DialogStudyController', DialogStudyController);

    DialogStudyController.$inject = ['$stateParams','AlertService','$uibModalInstance'];

    function DialogStudyController($stateParams, AlertService, $uibModalInstance) {
        var vm = this;
        
        console.log($stateParams);
        
        vm.studyUid = $stateParams.studyUid;
        vm.patientId = $stateParams.patientId;
        vm.cancel = cancel;
        
        function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function cancel() {
        		$uibModalInstance.dismiss('cancel');
        }
        
    }
})();
