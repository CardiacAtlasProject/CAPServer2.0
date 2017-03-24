(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('AuxFileDeleteController',AuxFileDeleteController);

    AuxFileDeleteController.$inject = ['$uibModalInstance', 'entity', 'AuxFile'];

    function AuxFileDeleteController($uibModalInstance, entity, AuxFile) {
        var vm = this;

        vm.auxFile = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AuxFile.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
