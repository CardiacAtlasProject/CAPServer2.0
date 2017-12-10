(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dicom', {
            parent: 'entity',
            url: '/dicom?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'DICOMS'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/d-icom/d-icoms.html',
                    controller: 'DICOMController',
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
                }]
            }
        })
        .state('dicom-detail', {
            parent: 'dicom',
            url: '/dicom/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'DICOM'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/d-icom/dicom-detail.html',
                    controller: 'DICOMDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'DICOM', function($stateParams, DICOM) {
                    return DICOM.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'dicom',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('dicom-detail.edit', {
            parent: 'dicom-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/d-icom/dicom-dialog.html',
                    controller: 'DICOMDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DICOM', function(DICOM) {
                            return DICOM.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dicom.new', {
            parent: 'dicom',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/d-icom/dicom-dialog.html',
                    controller: 'DICOMDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dicomFile: null,
                                dicomFileContentType: null,
                                image: null,
                                imageContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('dicom', null, { reload: 'dicom' });
                }, function() {
                    $state.go('dicom');
                });
            }]
        })
        .state('dicom.edit', {
            parent: 'dicom',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/d-icom/dicom-dialog.html',
                    controller: 'DICOMDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DICOM', function(DICOM) {
                            return DICOM.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dicom', null, { reload: 'dicom' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dicom.delete', {
            parent: 'dicom',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/d-icom/dicom-delete-dialog.html',
                    controller: 'DICOMDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DICOM', function(DICOM) {
                            return DICOM.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dicom', null, { reload: 'dicom' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
