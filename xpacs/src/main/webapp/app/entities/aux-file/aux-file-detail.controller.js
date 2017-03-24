(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('AuxFileDetailController', AuxFileDetailController);

    AuxFileDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'AuxFile', 'PatientInfo'];

    function AuxFileDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, AuxFile, PatientInfo) {
        var vm = this;

        vm.auxFile = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('xpacswebApp:auxFileUpdate', function(event, result) {
            vm.auxFile = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
