(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .factory('ClinicalNoteSearch', ClinicalNoteSearch);

    ClinicalNoteSearch.$inject = ['$resource'];

    function ClinicalNoteSearch($resource) {
        var resourceUrl =  'api/_search/clinical-notes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
