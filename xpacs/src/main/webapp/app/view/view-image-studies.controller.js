(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ViewImageStudiesController', ViewImageStudiesController);

    ViewImageStudiesController.$inject = ['$resource','AlertService'];

    function ViewImageStudiesController($resource, AlertService) {
        var vm = this;
        
        vm.studies = $resource('api/view-image-studies').query(onSuccess, onError); // query is the default GET with return array
        
        function onSuccess(data, headers) {
        	
        }
        
        function onError(error) {
            AlertService.error(error.data.message);
        }        
        
    }
})();
