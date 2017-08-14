(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('UploadController', UploadController);

    UploadController.$inject = ['Principal', 'Auth'];

    function UploadController (Principal, Auth) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.account = null;
        vm.success = null;

        
        Principal.identity().then(function(account) {
            vm.account = account;
        });

        function save () {
            
            var form = $('#uploadForm')[0];

            var data = new FormData(form);

            
	    console.log($('#upload_entity').val());
	    data.append("UploadEntity",$('#upload_entity').val());
            $("#btnSubmit").prop("disabled", true);


            $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "/upload/status",
            data: data,
            processData: false, //prevent jQuery from automatically transforming the data into a query string
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                vm.success = 'OK';
                $("#successBanner").hide();
                $("#successBanner").show();
                
            },
            error: function (e) {
                vm.error = 'ERROR';
                $("#failureBanner").hide();
                $("#failureBanner").show();
            }
        });
        }
    }
})();
