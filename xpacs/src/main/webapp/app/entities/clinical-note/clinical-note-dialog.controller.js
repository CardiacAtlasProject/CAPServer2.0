(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ClinicalNoteDialogController', ClinicalNoteDialogController);

    ClinicalNoteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'ClinicalNote', 'PatientInfo'];

    function ClinicalNoteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, ClinicalNote, PatientInfo) {
        var vm = this;

        vm.clinicalNote = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
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
            if (vm.clinicalNote.id !== null) {
                ClinicalNote.update(vm.clinicalNote, onSaveSuccess, onSaveError);
            } else {
                ClinicalNote.save(vm.clinicalNote, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:clinicalNoteUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.assessment_date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
