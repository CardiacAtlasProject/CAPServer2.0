(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('aux-file', {
            parent: 'entity',
            url: '/aux-file?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AuxFiles'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/aux-file/aux-files.html',
                    controller: 'AuxFileController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('aux-file-detail', {
            parent: 'aux-file',
            url: '/aux-file/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AuxFile'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/aux-file/aux-file-detail.html',
                    controller: 'AuxFileDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'AuxFile', function($stateParams, AuxFile) {
                    return AuxFile.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'aux-file',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('aux-file-detail.edit', {
            parent: 'aux-file-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/aux-file/aux-file-dialog.html',
                    controller: 'AuxFileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AuxFile', function(AuxFile) {
                            return AuxFile.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('aux-file.new', {
            parent: 'aux-file',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/aux-file/aux-file-dialog.html',
                    controller: 'AuxFileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                creationDate: null,
                                description: null,
                                file: null,
                                fileContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('aux-file', null, { reload: 'aux-file' });
                }, function() {
                    $state.go('aux-file');
                });
            }]
        })
        .state('aux-file.edit', {
            parent: 'aux-file',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/aux-file/aux-file-dialog.html',
                    controller: 'AuxFileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AuxFile', function(AuxFile) {
                            return AuxFile.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('aux-file', null, { reload: 'aux-file' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('aux-file.delete', {
            parent: 'aux-file',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/aux-file/aux-file-delete-dialog.html',
                    controller: 'AuxFileDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AuxFile', function(AuxFile) {
                            return AuxFile.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('aux-file', null, { reload: 'aux-file' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
