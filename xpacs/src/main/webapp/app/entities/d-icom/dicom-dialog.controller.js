(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('DICOMDialogController', DICOMDialogController);

    DICOMDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'DICOM'];

    function DICOMDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, DICOM) {
        var vm = this;

        vm.dICOM = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dICOM.id !== null) {
                DICOM.update(vm.dICOM, onSaveSuccess, onSaveError);
            } else {
                DICOM.save(vm.dICOM, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:dICOMUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setDicomFile = function ($file, dICOM) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        dICOM.dicomFile = base64Data;
                        dICOM.dicomFileContentType = $file.type;
                    });
                });
            }
        };

        vm.setImage = function ($file, dICOM) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        dICOM.image = base64Data;
                        dICOM.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
