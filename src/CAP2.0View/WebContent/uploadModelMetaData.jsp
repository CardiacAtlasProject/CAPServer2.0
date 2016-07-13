<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String requestType = request.getParameter("action");
	String operation = "addModelMetaData";
	String message = "Success fully added Metadata";
	if (requestType.equalsIgnoreCase("UploadMetadata")) {
		operation = "uploadModelMetaData";
		message = "Success fully uploaded Metadata";
	} else if (requestType.equalsIgnoreCase("ReplaceMetadata")) {
		operation = "replaceModelMetaData";
		message = "Success fully replaced Metadata";
	}
%>
<div data-role="page" id="uploadmodelMetaDataFile">
	<div data-role="header">
		<h1>Model Meta Data</h1>
	</div>
	<script>
    var input = document.getElementById('modelMetaDatafiles');
    var files= null;
    var modelmetadataresourceid;
    var modelmetadataresourceloc;
    
    input.onchange = function(e) {
       files = e.target.files[0]; // File
    }

    $('#confirmmodelmetadataAdd').on('click', function(){
    	if(selectedModel==null){
			alert('UI in illegal state, please refresh page and retry');
			return;
		}
		var rid = new Object();
		if(modelmetadataresourceid!=null){
			rid["resourceid"] = modelmetadataresourceid;
		}else if(resourceloc!=null){
			rid["resourceloc"] = modelmetadataresourceloc;
		}else{
			alert("Uploaded Resource identifier missing!! Please contact the administrator.")
			return;
		}
        var requestObj = new Object();
		requestObj["<%=operation%>"]=selectedModel;
		requestObj["resource"]=rid;
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
			    //console.debug(data);
				alert("<%=message%>	");
											$('#modeltable').DataTable().ajax
													.reload(null, false); // user paging is not reset on reload
											$( ".ui-dialog" ).dialog( "close" ); 
										},
										error : function(jqXHR, textStatus,
												errorThrown) {
											alert('Internal server error!Please contact the Administrator');
											//console.debug(jqXHR);
										}
									});

						});

		$('#uploadmodelmetadata_btn').on(
				'click',
				function() {
					modelmetadataresourceid = null;
					modelmetadataresourceloc = null;
					var fData = new FormData();
					if(files!=null)
						fData.append(files.name, files);
					else{
						alert("No file selected!!")
						return;
						}
					$.ajax({
						url : './upload', //Server script to process data
						type : 'POST',
						xhr : function() { // Custom XMLHttpRequest
							var myXhr = $.ajaxSettings.xhr();
							if (myXhr.upload) { // Check if upload property exists
								myXhr.upload.addEventListener('progress',
										modelmetadataprogressHandlingFunction,
										false); // For handling the progress of the upload
							}
							return myXhr;
						},
						//Ajax events
						success : function(data) {
							try {
								var js = JSON.parse(data);
								modelmetadataresourceid = js.resourceid;
								modelmetadataresourceloc = js.resourceloc;
								//console.debug(js)
								$('#modelmetadataprogressbar').hide();
								$('#uploadmodelmetadata_btn').hide();
								$('#confirmmodelmetadataAdd').show();
								$('#confirmmodelmetadataAdd').show();
							} catch (e) {
								console.debug(data);
							}
						},
						error : function(data) {
							alert("Upload Error");
							//console.debug(data);
						},
						// Form data
						data : fData,
						//Options to tell jQuery not to process data or worry about content-type.
						cache : false,
						contentType : false,
						processData : false
					});
				});

		function modelmetadataprogressHandlingFunction(e) {
			$('#modelmetadataprogressbar').show();
			if (e.lengthComputable) {
				//console.debug(e);
				$('progress').attr({
					value : e.loaded,
					max : e.total
				});
			}
			//console.debug(e);
		}
		$('progress').attr({
			value : 0,
			max : 1
		});
	</script>
	<div data-role="content">
		<table>
			<tr>
				<td><input type="file" id="modelMetaDatafiles" name="modelMetaDatafiles" /></td>
				<td>
					<div id="uploadmodelmetadatapopup">
						<a href="#uploadmodelmetadatapopupInfo" data-rel="popup" data-transition="pop"
							class="my-tooltip-btn ui-btn ui-alt-icon ui-nodisc-icon ui-btn-inline ui-icon-info ui-btn-icon-notext" title="File" style="float: right;">File
							Information</a>
						<div data-role="popup" id="uploadmodelmetadatapopupInfo" class="ui-content" data-theme="a" style="max-width: 350px;">
							<p>Select a single file to upload.</p>
						</div>
					</div>
				</td>
			</tr>
		</table>
	</div>
	<button id="uploadmodelmetadata_btn">Upload</button>
	<button id="confirmmodelmetadataAdd" style="display: none">Update Model</button>
</div>
<div data-role="footer" data-mini="true">
	<progress id="modelmetadataprogressbar" style="width: 100%; display: none"></progress>
</div>
</div>