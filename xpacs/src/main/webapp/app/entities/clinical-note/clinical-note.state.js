(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('clinical-note', {
            parent: 'entity',
            url: '/clinical-note?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ClinicalNotes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/clinical-note/clinical-notes.html',
                    controller: 'ClinicalNoteController',
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
        .state('clinical-note-detail', {
            parent: 'clinical-note',
            url: '/clinical-note/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ClinicalNote'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/clinical-note/clinical-note-detail.html',
                    controller: 'ClinicalNoteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ClinicalNote', function($stateParams, ClinicalNote) {
                    return ClinicalNote.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'clinical-note',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('clinical-note-detail.edit', {
            parent: 'clinical-note-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/clinical-note/clinical-note-dialog.html',
                    controller: 'ClinicalNoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ClinicalNote', function(ClinicalNote) {
                            return ClinicalNote.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('clinical-note.new', {
            parent: 'clinical-note',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/clinical-note/clinical-note-dialog.html',
                    controller: 'ClinicalNoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                assessmentDate: null,
                                age: null,
                                height: null,
                                weight: null,
                                diagnosis: null,
                                note: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('clinical-note', null, { reload: 'clinical-note' });
                }, function() {
                    $state.go('clinical-note');
                });
            }]
        })
        .state('clinical-note.edit', {
            parent: 'clinical-note',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/clinical-note/clinical-note-dialog.html',
                    controller: 'ClinicalNoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ClinicalNote', function(ClinicalNote) {
                            return ClinicalNote.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('clinical-note', null, { reload: 'clinical-note' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('clinical-note.delete', {
            parent: 'clinical-note',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/clinical-note/clinical-note-delete-dialog.html',
                    controller: 'ClinicalNoteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ClinicalNote', function(ClinicalNote) {
                            return ClinicalNote.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('clinical-note', null, { reload: 'clinical-note' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
