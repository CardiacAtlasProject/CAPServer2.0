(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ClinicalNoteDetailController', ClinicalNoteDetailController);

    ClinicalNoteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'ClinicalNote', 'PatientInfo'];

    function ClinicalNoteDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, ClinicalNote, PatientInfo) {
        var vm = this;

        vm.clinicalNote = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('xpacswebApp:clinicalNoteUpdate', function(event, result) {
            vm.clinicalNote = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
