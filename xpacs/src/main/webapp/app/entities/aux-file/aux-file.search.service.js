(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .factory('AuxFileSearch', AuxFileSearch);

    AuxFileSearch.$inject = ['$resource'];

    function AuxFileSearch($resource) {
        var resourceUrl =  'api/_search/aux-files/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
