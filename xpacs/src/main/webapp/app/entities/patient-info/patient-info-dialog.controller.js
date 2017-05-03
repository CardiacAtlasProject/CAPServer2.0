(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('PatientInfoDialogController', PatientInfoDialogController);

    PatientInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PatientInfo'];

    function PatientInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PatientInfo) {
        var vm = this;

        vm.patientInfo = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.patientInfo.id !== null) {
                PatientInfo.update(vm.patientInfo, onSaveSuccess, onSaveError);
            } else {
                PatientInfo.save(vm.patientInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:patientInfoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
