(function() {
    'use strict';
    angular
        .module('xpacswebApp')
        .factory('BaselineDiagnosis', BaselineDiagnosis);

    BaselineDiagnosis.$inject = ['$resource', 'DateUtils'];

    function BaselineDiagnosis ($resource, DateUtils) {
        var resourceUrl =  'api/baseline-diagnoses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.diagnosisDate = DateUtils.convertLocalDateFromServer(data.diagnosisDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.diagnosisDate = DateUtils.convertLocalDateToServer(copy.diagnosisDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.diagnosisDate = DateUtils.convertLocalDateToServer(copy.diagnosisDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
