(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('patient-info', {
            parent: 'entity',
            url: '/patient-info?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'PatientInfos'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/patient-info/patient-infos.html',
                    controller: 'PatientInfoController',
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
        .state('patient-info-detail', {
            parent: 'patient-info',
            url: '/patient-info/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'PatientInfo'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/patient-info/patient-info-detail.html',
                    controller: 'PatientInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'PatientInfo', function($stateParams, PatientInfo) {
                  console.log("lol"+PatientInfo.get({id : $stateParams.id}).$promise);
                    return PatientInfo.get({id : $stateParams.id}).$promise;

                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'patient-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('patient-info-detail.edit', {
            parent: 'patient-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/patient-info/patient-info-dialog.html',
                    controller: 'PatientInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PatientInfo', function(PatientInfo) {
                            return PatientInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('patient-info.new', {
            parent: 'patient-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/patient-info/patient-info-dialog.html',
                    controller: 'PatientInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                patientId: null,
                                cohort: null,
                                ethnicity: null,
                                gender: null,
                                primaryDiagnosis: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('patient-info', null, { reload: 'patient-info' });
                }, function() {
                    $state.go('patient-info');
                });
            }]
        })
        .state('patient-info.edit', {
            parent: 'patient-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/patient-info/patient-info-dialog.html',
                    controller: 'PatientInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PatientInfo', function(PatientInfo) {
                            return PatientInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('patient-info', null, { reload: 'patient-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('patient-info.delete', {
            parent: 'patient-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/patient-info/patient-info-delete-dialog.html',
                    controller: 'PatientInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PatientInfo', function(PatientInfo) {
                            return PatientInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('patient-info', null, { reload: 'patient-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
