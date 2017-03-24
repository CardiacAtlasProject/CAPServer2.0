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
                        data.diagnosis_date = DateUtils.convertLocalDateFromServer(data.diagnosis_date);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.diagnosis_date = DateUtils.convertLocalDateToServer(copy.diagnosis_date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.diagnosis_date = DateUtils.convertLocalDateToServer(copy.diagnosis_date);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
