(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('ViewImageStudiesController', ViewImageStudiesController);

    ViewImageStudiesController.$inject = ['$resource','AlertService', 'paginationConstants', 'ParseLinks', '$stateParams'];

    function ViewImageStudiesController($resource, AlertService, paginationConstants, ParseLinks, $stateParams) {
        var vm = this;
        
        vm.page = 0;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.links = {
                last: 0
            };
        
        // get the studies
        vm.studies = $resource('api/view-image-studies')
        					.query({
        						page: vm.page,
        						size: vm.itemsPerPage
        					}, onSuccess, onError); // query is the default GET with return array
        
        // show some error message if any
        if( $stateParams.error ) {
        		AlertService.error($stateParams.error);
        	}
        
        function onSuccess(data, headers) {
        		if( vm.studies.length==0 ) {
        			AlertService.warning("Database is empty");
        		} else {
        			vm.links = ParseLinks.parse(headers('link'));
        			vm.totalItems = headers('X-Total-Count');
        		}
        }
        
        function onError(error) {
            AlertService.error(error.data.message);
        }
        
    }
})();
