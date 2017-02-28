(function() {
    'use strict';
    angular
        .module('xpacswebApp')
        .factory('AuxFile', AuxFile);

    AuxFile.$inject = ['$resource', 'DateUtils'];

    function AuxFile ($resource, DateUtils) {
        var resourceUrl =  'api/aux-files/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creation_date = DateUtils.convertLocalDateFromServer(data.creation_date);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.creation_date = DateUtils.convertLocalDateToServer(copy.creation_date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.creation_date = DateUtils.convertLocalDateToServer(copy.creation_date);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
