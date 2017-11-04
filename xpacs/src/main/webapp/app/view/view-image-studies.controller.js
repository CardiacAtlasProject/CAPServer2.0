(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ViewImageStudiesController', ViewImageStudiesController);

    ViewImageStudiesController.$inject = ['$resource'];

    function ViewImageStudiesController($resource) {
        var vm = this;
        
        vm.studies = $resource('api/view-image-studies').query(); // query is the default GET with return array
        
    }
})();
