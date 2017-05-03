(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('BaselineDiagnosisDetailController', BaselineDiagnosisDetailController);

    BaselineDiagnosisDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'BaselineDiagnosis', 'PatientInfo'];

    function BaselineDiagnosisDetailController($scope, $rootScope, $stateParams, previousState, entity, BaselineDiagnosis, PatientInfo) {
        var vm = this;

        vm.baselineDiagnosis = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('xpacswebApp:baselineDiagnosisUpdate', function(event, result) {
            vm.baselineDiagnosis = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
