(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ViewPatientsController', ViewPatientsController);

    ViewPatientsController.$inject = ['ViewPatientsService'];

    function ViewPatientsController (ViewPatientsService) {
        var vm = this;
        
        vm.patients = ViewPatientsService.collectAll();
        
    }
})();
