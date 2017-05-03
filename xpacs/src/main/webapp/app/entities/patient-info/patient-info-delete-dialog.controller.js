(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('PatientInfoDeleteController',PatientInfoDeleteController);

    PatientInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'PatientInfo'];

    function PatientInfoDeleteController($uibModalInstance, entity, PatientInfo) {
        var vm = this;

        vm.patientInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PatientInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
