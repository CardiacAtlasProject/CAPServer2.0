(function() {
    'use strict';
    angular
        .module('xpacswebApp')
        .factory('DICOM', DICOM);

    DICOM.$inject = ['$resource'];

    function DICOM ($resource) {
        var resourceUrl =  'api/d-icoms/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
