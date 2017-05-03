(function() {
    'use strict';
    angular
        .module('xpacswebApp')
        .factory('PatientInfo', PatientInfo);

    PatientInfo.$inject = ['$resource'];

    function PatientInfo ($resource) {
        var resourceUrl =  'api/patient-infos/:id';

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
