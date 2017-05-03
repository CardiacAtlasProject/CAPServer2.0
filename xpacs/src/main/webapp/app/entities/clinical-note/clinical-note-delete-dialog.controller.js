(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ClinicalNoteDeleteController',ClinicalNoteDeleteController);

    ClinicalNoteDeleteController.$inject = ['$uibModalInstance', 'entity', 'ClinicalNote'];

    function ClinicalNoteDeleteController($uibModalInstance, entity, ClinicalNote) {
        var vm = this;

        vm.clinicalNote = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ClinicalNote.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
