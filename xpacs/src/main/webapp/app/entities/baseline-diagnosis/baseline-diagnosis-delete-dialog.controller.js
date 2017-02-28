(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('BaselineDiagnosisDeleteController',BaselineDiagnosisDeleteController);

    BaselineDiagnosisDeleteController.$inject = ['$uibModalInstance', 'entity', 'BaselineDiagnosis'];

    function BaselineDiagnosisDeleteController($uibModalInstance, entity, BaselineDiagnosis) {
        var vm = this;

        vm.baselineDiagnosis = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            BaselineDiagnosis.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
