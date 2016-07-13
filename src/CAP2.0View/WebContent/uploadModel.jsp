<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div data-role="page" id="uploadModelFiles">
	<div data-role="header">
		<h1>Model(s) upload</h1>
	</div>
	<script>
    var input = document.getElementById('files');
    var files= null;
    var resourceid;
    var resourceloc;
    
    input.onchange = function(e) {
       files = e.target.files; // FileList
    }

    $('#confirmAdd').on('click', function(){

		var rid = new Object();
		if(resourceid!=null){
			rid["resourceid"] = resourceid;
		}else if(resourceloc!=null){
			rid["resourceloc"] = resourceloc;
		}else{
			alert("Uploaded Resource identifier missing!! Please contact the administrator.")
			return;
		}
        var requestObj = new Object();
		requestObj["addModel"]=rid;

		$.ajax({
		    url : "./CAPModel",
		    type: "POST",
		    data : requestObj,
		    dataType: 'json',
			beforeSend: function(){$.mobile.loading( 'show', {
				theme: 'z',
				html: ""
			}); },
			complete: function(){
				$.mobile.loading( 'hide');
			},
		    success: function(data, textStatus, jqXHR)
		    {
		    	$( ".ui-dialog" ).dialog( "close" ); 
				alert("Success fully added model(s)");
				$('#modeltable').DataTable().ajax.reload();
		    },
		    error: function (jqXHR, textStatus, errorThrown)
		    {
		    	$( ".ui-dialog" ).dialog( "close" ); 
			    if(jqXHR.responseText.indexOf("Study does not exist"))
		 			alert("Unable to complete request. Study corresponding to the model not available in the database. Check the model xml file");
			    else if (jqXHR.responseText.indexOf("Duplicate entry"))
			    	alert("Unable to complete request. Model with same name exists in the database");
			    else
				    alert('Internal server error!Please contact the Administrator');
		 		//console.debug(jqXHR);
		    }
		}); 
        
    });

    $('#upload_btn').on('click', function(){
      resourceid = null;
      resourceloc = null;
      var fData = new FormData();  
        var fctr = 0;  
        for (var i = 0, f; f = files[i]; ++i){
            //console.debug(files[i].webkitRelativePath);
            fData.append(files[i].webkitRelativePath,files[i]);
            fctr++;
        }
        if(fctr==0){
	       	alert("No files have been selected!!")
			return;
        }
        $.ajax({
            url: './upload',  //Server script to process data
            type: 'POST',
            xhr: function() {  // Custom XMLHttpRequest
                var myXhr = $.ajaxSettings.xhr();
                if(myXhr.upload){ // Check if upload property exists
                    myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
                 }
                return myXhr;
            },
            //Ajax events
            //beforeSend: function(){ console.debug("Starting upload")},
            success: function(data){ 
	            try{
		            var js = JSON.parse(data);
		            resourceid = js.resourceid;
		            resourceloc = js.resourceloc;
                    //console.debug(js)
                    $('#progressbar').hide(); $('#upload_btn').hide(); $('#confirmAdd').show();
                    $('#confirmAdd').show();
	            }catch(e){
		            console.debug(data);
	            }
                },
            error: function(data){ alert("Upload Error"); },
            // Form data
            data: fData,
            //Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false
        });
    });

    function progressHandlingFunction(e){
        $('#progressbar').show();
        if(e.lengthComputable){
           // console.debug(e);
            $('progress').attr({value:e.loaded,max:e.total});
        } 
        //console.debug(e);
    }
    $('progress').attr({value:0,max:1});
    </script>
	<div data-role="content">
		<table>
			<tr>
				<td><input type="file" id="files" name="files[]" multiple webkitdirectory /></td>
				<td>
					<div id="uploadpopup">
						<a href="#uploadpopupInfo" data-rel="popup" data-transition="pop"
							class="my-tooltip-btn ui-btn ui-alt-icon ui-nodisc-icon ui-btn-inline ui-icon-info ui-btn-icon-notext" title="File" style="float: right;">File
							Information</a>
						<div data-role="popup" id="uploadpopupInfo" class="ui-content" data-theme="a" style="max-width: 350px;">
							<p>Works on Webkit enabled browsers. Loads all the files if the target is a directory.</p>
						</div>
					</div>
				</td>
			</tr>
		</table>
		<button id="upload_btn">Start Uploading</button>
		<button id="confirmAdd" style="display: none">Update CAP</button>
		<progress id="progressbar" style="width: 100%; display: none"></progress>

	</div>
	<div data-role="footer" data-mini="true">
		<p>Maximum of 512 files per request</p>
	</div>
</div>