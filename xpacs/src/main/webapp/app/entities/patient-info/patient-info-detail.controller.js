(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('PatientInfoDetailController', PatientInfoDetailController);

    PatientInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'PatientInfo'];

    function PatientInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, PatientInfo) {
        var vm = this;

        vm.patientInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('xpacswebApp:patientInfoUpdate', function(event, result) {
            vm.patientInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
