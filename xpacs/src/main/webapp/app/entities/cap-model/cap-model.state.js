(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('cap-model', {
            parent: 'entity',
            url: '/cap-model?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'CapModels'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cap-model/cap-models.html',
                    controller: 'CapModelController',
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
        .state('cap-model-detail', {
            parent: 'cap-model',
            url: '/cap-model/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'CapModel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cap-model/cap-model-detail.html',
                    controller: 'CapModelDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'CapModel', function($stateParams, CapModel) {
                    return CapModel.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'cap-model',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('cap-model-detail.edit', {
            parent: 'cap-model-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cap-model/cap-model-dialog.html',
                    controller: 'CapModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['CapModel', function(CapModel) {
                            return CapModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cap-model.new', {
            parent: 'cap-model',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cap-model/cap-model-dialog.html',
                    controller: 'CapModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                creation_date: null,
                                name: null,
                                type: null,
                                comment: null,
                                xml_file: null,
                                model_file: null,
                                model_fileContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('cap-model', null, { reload: 'cap-model' });
                }, function() {
                    $state.go('cap-model');
                });
            }]
        })
        .state('cap-model.edit', {
            parent: 'cap-model',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cap-model/cap-model-dialog.html',
                    controller: 'CapModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['CapModel', function(CapModel) {
                            return CapModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cap-model', null, { reload: 'cap-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cap-model.delete', {
            parent: 'cap-model',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cap-model/cap-model-delete-dialog.html',
                    controller: 'CapModelDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['CapModel', function(CapModel) {
                            return CapModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cap-model', null, { reload: 'cap-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
