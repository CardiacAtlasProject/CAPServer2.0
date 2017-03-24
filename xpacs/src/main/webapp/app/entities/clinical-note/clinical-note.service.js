(function() {
    'use strict';
    angular
        .module('xpacswebApp')
        .factory('ClinicalNote', ClinicalNote);

    ClinicalNote.$inject = ['$resource', 'DateUtils'];

    function ClinicalNote ($resource, DateUtils) {
        var resourceUrl =  'api/clinical-notes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.assessment_date = DateUtils.convertLocalDateFromServer(data.assessment_date);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.assessment_date = DateUtils.convertLocalDateToServer(copy.assessment_date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.assessment_date = DateUtils.convertLocalDateToServer(copy.assessment_date);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
