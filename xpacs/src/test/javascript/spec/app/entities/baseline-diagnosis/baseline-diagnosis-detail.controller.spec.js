'use strict';

describe('Controller Tests', function() {

    describe('BaselineDiagnosis Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockBaselineDiagnosis, MockPatientInfo;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockBaselineDiagnosis = jasmine.createSpy('MockBaselineDiagnosis');
            MockPatientInfo = jasmine.createSpy('MockPatientInfo');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'BaselineDiagnosis': MockBaselineDiagnosis,
                'PatientInfo': MockPatientInfo
            };
            createController = function() {
                $injector.get('$controller')("BaselineDiagnosisDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'xpacswebApp:baselineDiagnosisUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
