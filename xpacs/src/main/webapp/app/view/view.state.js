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
    		.state('view-image-studies', {
    			parent: 'app',
    			url: '/view-image-studies',
    			data: {
    				authorities: ['ROLE_USER'],
    				pageTitle: 'List of image studies'
    			},
    			views: {
    				'content@': {
    					templateUrl: 'app/view/view-image-studies.html',
    					controller: 'ViewImageStudiesController',
    					controllerAs: 'vm'
    				}
    			}
    		})
    		.state('download-study', {
    			parent: 'view-image-studies',
    			url: '/download-study',
    			data: {
    				authorities: ['ROLE_USER']
    			},
    			onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
    				$uibModal.open({
    					templateUrl: 'app/view/download-dialog.html',
    					controller: 'DialogStudyController',
    					controllerAs: 'vm',
    					backdrop: 'static',
    					size: 'md'
    				}).result.then(function() {
    					$state.go('^', {}, { reload: false });
    				}, function() {
    					$state.go('^');
    				});
    			}]
    		})
    }
    
})();