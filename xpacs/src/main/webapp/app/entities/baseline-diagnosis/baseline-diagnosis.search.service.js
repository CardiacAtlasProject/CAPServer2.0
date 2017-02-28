(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .factory('BaselineDiagnosisSearch', BaselineDiagnosisSearch);

    BaselineDiagnosisSearch.$inject = ['$resource'];

    function BaselineDiagnosisSearch($resource) {
        var resourceUrl =  'api/_search/baseline-diagnoses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
