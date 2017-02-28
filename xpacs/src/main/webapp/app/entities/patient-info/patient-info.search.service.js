(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .factory('PatientInfoSearch', PatientInfoSearch);

    PatientInfoSearch.$inject = ['$resource'];

    function PatientInfoSearch($resource) {
        var resourceUrl =  'api/_search/patient-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
