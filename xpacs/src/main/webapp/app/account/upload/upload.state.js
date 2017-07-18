(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('upload', {
            parent: 'account',
            url: '/upload',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Upload CSV files'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/upload/upload.html',
                    controller: 'UploadController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
