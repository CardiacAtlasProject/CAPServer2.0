(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('CapModelDeleteController',CapModelDeleteController);

    CapModelDeleteController.$inject = ['$uibModalInstance', 'entity', 'CapModel'];

    function CapModelDeleteController($uibModalInstance, entity, CapModel) {
        var vm = this;

        vm.capModel = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            CapModel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
