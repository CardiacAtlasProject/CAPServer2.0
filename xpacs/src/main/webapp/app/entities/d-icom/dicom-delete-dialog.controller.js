(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('DICOMDeleteController',DICOMDeleteController);

    DICOMDeleteController.$inject = ['$uibModalInstance', 'entity', 'DICOM'];

    function DICOMDeleteController($uibModalInstance, entity, DICOM) {
        var vm = this;

        vm.dICOM = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            DICOM.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
