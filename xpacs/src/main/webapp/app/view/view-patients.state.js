(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    
    function stateConfig($stateProvider) {
    		$stateProvider
    		.state('view-patients', {
    			parent: 'app',
    			url: '/view-patients',
    			data: {
    				authorities: ['ROLE_USER'],
    				pageTitle: 'List of patients'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'app/view/view-patients.html',
    	                 controller: 'ViewPatientsController',
    	                 controllerAs: 'vm'
    				}
    			}
    		})
    }
    
})();