(function() {
	'use strict';
	
	angular
		.module('xpacswebApp')
		.factory('ViewPatientsService',ViewPatientsService);
	
	ViewPatientsService.$inject = ['$resource'];
	
	function ViewPatientsService($resource) {
		var service = $resource('api/view-patients', {}, {
			'collectAll': { method: 'GET', isArray: true }
		});
		
		return service;
	}
	
})();