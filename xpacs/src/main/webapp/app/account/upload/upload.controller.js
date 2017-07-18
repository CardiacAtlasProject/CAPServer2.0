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

            data.append("CustomField", "This is some extra data, testing");
	    console.log($('#upload_entity').val());
	    data.append("UploadEntity",$('#upload_entity').val());
            //var data = vm.file;
            console.log(data);
            $("#btnSubmit").prop("disabled", true);


            $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "/upload/status",
            data: data,
            //http://api.jquery.com/jQuery.ajax/
            //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
            processData: false, //prevent jQuery from automatically transforming the data into a query string
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                vm.success = 'OK';
                $("#btnSubmit").prop("disabled", false);
                console.log("hello");
                $("success").text("<strong>Database was successfully populated!</strong>");
                $("success").text("<strong>Database was successfully populated!</strong>")
                //window.location.reload()
                //$getUrl = window.location;
                //$baseUrl = $getUrl.protocol + "//" + $getUrl.host + "/" + $getUrl.pathname.split('/')[1];
                //window.location.replace($baseUrl+"runjob")
            },
            error: function (e) {
                vm.error = 'ERROR';
                $("#btnSubmit").prop("disabled", false);
                $("failure").text("<strong>An error has occurred! Database could not be populated!</strong>");
                console.log("fail");
                //window.location.reload()
            }
        });

            // Auth.updateAccount(vm.settingsAccount).then(function() {
            //     vm.error = null;
            //     vm.success = 'OK';
            //     Principal.identity(true).then(function(account) {
            //         vm.settingsAccount = copyAccount(account);
            //     });
            // }).catch(function() {
            //     vm.success = null;
            //     vm.error = 'ERROR';
            // });
        }
    }
})();
