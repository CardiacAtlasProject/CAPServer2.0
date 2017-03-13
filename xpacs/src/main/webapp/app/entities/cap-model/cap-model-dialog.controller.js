(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('CapModelDialogController', CapModelDialogController);

    CapModelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'CapModel', 'PatientInfo'];

    function CapModelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, CapModel, PatientInfo) {
        var vm = this;

        vm.capModel = entity;
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
            if (vm.capModel.id !== null) {
                CapModel.update(vm.capModel, onSaveSuccess, onSaveError);
            } else {
                CapModel.save(vm.capModel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:capModelUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creation_date = false;

        vm.setModel_file = function ($file, capModel) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        capModel.model_file = base64Data;
                        capModel.model_fileContentType = $file.type;
                    });
                });
            }
        };

        vm.setXml_file = function ($file, capModel) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        capModel.xml_file = base64Data;
                        capModel.xml_fileContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
