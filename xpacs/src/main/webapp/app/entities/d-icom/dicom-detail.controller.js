(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('DICOMDetailController', DICOMDetailController);

    DICOMDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'DICOM'];

    function DICOMDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, DICOM) {
        var vm = this;

        vm.dICOM = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('xpacswebApp:dICOMUpdate', function(event, result) {
            vm.dICOM = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
