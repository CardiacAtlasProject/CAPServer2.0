(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('CapModelDetailController', CapModelDetailController);

    CapModelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'CapModel', 'PatientInfo'];

    function CapModelDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, CapModel, PatientInfo) {
        var vm = this;

        vm.capModel = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('xpacswebApp:capModelUpdate', function(event, result) {
            vm.capModel = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
