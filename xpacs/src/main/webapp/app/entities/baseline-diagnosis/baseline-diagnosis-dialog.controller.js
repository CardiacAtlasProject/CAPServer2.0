(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('BaselineDiagnosisDialogController', BaselineDiagnosisDialogController);

    BaselineDiagnosisDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'BaselineDiagnosis', 'PatientInfo'];

    function BaselineDiagnosisDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, BaselineDiagnosis, PatientInfo) {
        var vm = this;

        vm.baselineDiagnosis = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.patientinfos = PatientInfo.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.baselineDiagnosis.id !== null) {
                BaselineDiagnosis.update(vm.baselineDiagnosis, onSaveSuccess, onSaveError);
            } else {
                BaselineDiagnosis.save(vm.baselineDiagnosis, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:baselineDiagnosisUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.diagnosis_date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
