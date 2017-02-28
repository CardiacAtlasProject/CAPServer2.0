(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('baseline-diagnosis', {
            parent: 'entity',
            url: '/baseline-diagnosis?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'BaselineDiagnoses'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/baseline-diagnosis/baseline-diagnoses.html',
                    controller: 'BaselineDiagnosisController',
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
        .state('baseline-diagnosis-detail', {
            parent: 'baseline-diagnosis',
            url: '/baseline-diagnosis/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'BaselineDiagnosis'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/baseline-diagnosis/baseline-diagnosis-detail.html',
                    controller: 'BaselineDiagnosisDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'BaselineDiagnosis', function($stateParams, BaselineDiagnosis) {
                    return BaselineDiagnosis.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'baseline-diagnosis',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('baseline-diagnosis-detail.edit', {
            parent: 'baseline-diagnosis-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/baseline-diagnosis/baseline-diagnosis-dialog.html',
                    controller: 'BaselineDiagnosisDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BaselineDiagnosis', function(BaselineDiagnosis) {
                            return BaselineDiagnosis.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('baseline-diagnosis.new', {
            parent: 'baseline-diagnosis',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/baseline-diagnosis/baseline-diagnosis-dialog.html',
                    controller: 'BaselineDiagnosisDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                diagnosis_date: null,
                                age: null,
                                height: null,
                                weight: null,
                                heart_rate: null,
                                dbp: null,
                                sbp: null,
                                history_of_alcohol: null,
                                history_of_diabetes: null,
                                history_of_hypertension: null,
                                history_of_smoking: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('baseline-diagnosis', null, { reload: 'baseline-diagnosis' });
                }, function() {
                    $state.go('baseline-diagnosis');
                });
            }]
        })
        .state('baseline-diagnosis.edit', {
            parent: 'baseline-diagnosis',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/baseline-diagnosis/baseline-diagnosis-dialog.html',
                    controller: 'BaselineDiagnosisDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BaselineDiagnosis', function(BaselineDiagnosis) {
                            return BaselineDiagnosis.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('baseline-diagnosis', null, { reload: 'baseline-diagnosis' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('baseline-diagnosis.delete', {
            parent: 'baseline-diagnosis',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/baseline-diagnosis/baseline-diagnosis-delete-dialog.html',
                    controller: 'BaselineDiagnosisDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BaselineDiagnosis', function(BaselineDiagnosis) {
                            return BaselineDiagnosis.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('baseline-diagnosis', null, { reload: 'baseline-diagnosis' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
