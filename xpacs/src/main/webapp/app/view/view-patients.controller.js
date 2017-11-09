(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ViewPatientsController', ViewPatientsController);
    
    
    ViewPatientsController.$inject = ['$resource']
    
    function ViewPatientsController($resource) {
    		var vm = this;
    		
    		vm.patients = $resource('api/view-patients').query(); // query is the default GET with return array    		
    }

})();
