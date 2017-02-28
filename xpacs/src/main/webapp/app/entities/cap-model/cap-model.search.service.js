(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .factory('CapModelSearch', CapModelSearch);

    CapModelSearch.$inject = ['$resource'];

    function CapModelSearch($resource) {
        var resourceUrl =  'api/_search/cap-models/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
