<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String requestType = request.getParameter("action");
	String operation = "addStudyMetaData";
	String message = "Success fully uploaded Metadata";
	String descriptor = null;
	boolean hideDesc = false;
	if (requestType.equalsIgnoreCase("replaceStudyMetaData")) {
		descriptor = request.getParameter("descriptor");
		operation = "replaceStudyMetaData";
		message = "Success fully replaced " + descriptor;
		hideDesc = true;
	}
%>
<div data-role="page" id="uploadStudyMetaDataFile">
	<div data-role="header">
		<h1>Study Meta Data</h1>
	</div>
	<script>
	var dinput = document.getElementById('StudyMetaDataDirectory');
    var dfiles= null;
    
    dinput.onchange = function(e) {
       dfiles = e.target.files; // FileList
    }

	
    var input = document.getElementById('StudyMetaDatafiles');
    var files= null;
    var studymetadataresourceid;
    var modelmetadataresourceloc;
    
    input.onchange = function(e) {
       files = e.target.files[0]; // File
    }

    $('#confirmstudymetadataAdd').on('click', function(){
        <%if(!hideDesc){%>
		var desc = $('#descriptorinput').val();
		if(desc.length<0){
			alert("Provide a descriptor for the metadata");
			return;
		}
		<%}else{%>
		var desc = "<%=descriptor%>";
		<%}%>
		if(selectedStudy==null){
			alert('UI in illegal state, please refresh page and retry');
			return;
		}
		var rid = new Object();
		if(studymetadataresourceid!=null){
			rid["resourceid"] = studymetadataresourceid;
		}else if(resourceloc!=null){
			rid["resourceloc"] = modelmetadataresourceloc;
		}else{
			alert("Uploaded Resource identifier missing!! Please contact the administrator.")
			return;
		}
        var requestObj = new Object();
		requestObj["<%=operation%>"]=selectedStudy;
		requestObj["resource"]=rid;
		requestObj["descriptor"]=desc;
		$.ajax({
		    url : "./CAPStudy",
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
				alert("<%=message%>");
											$('#dicomtable').DataTable().ajax
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

		$('#uploadstudymetadata_btn').on(
				'click',
				function() {
					studymetadataresourceid = null;
					modelmetadataresourceloc = null;
					var fData = new FormData();
					var fctr = 0;
					if(files!=null){
						fData.append(files.name, files);
						fctr++;
					}
					if(dfiles!=null){
						//Add the directory files if any
						for (var i = 0, f; f = dfiles[i]; ++i) {
							//console.debug(files[i].webkitRelativePath);
							fData.append(dfiles[i].webkitRelativePath, dfiles[i]);
							fctr++;
						}
					}
					if(fctr==0){
						alert("No files have been selected!!")
						return;
					}
					
					$.ajax({
						url : './upload', //Server script to process data
						type : 'POST',
						xhr : function() { // Custom XMLHttpRequest
							var myXhr = $.ajaxSettings.xhr();
							if (myXhr.upload) { // Check if upload property exists
								myXhr.upload.addEventListener('progress',
										studymetadataprogressHandlingFunction,
										false); // For handling the progress of the upload
							}
							return myXhr;
						},
						//Ajax events
						success : function(data) {
							try {
								var js = JSON.parse(data);
								studymetadataresourceid = js.resourceid;
								modelmetadataresourceloc = js.resourceloc;
								$('#studymetadataprogressbar').hide();
								$('#uploadstudymetadata_btn').hide();
								$('#confirmstudymetadataAdd').show();
								$('#confirmstudymetadataAdd').show();
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

		function studymetadataprogressHandlingFunction(e) {
			$('#studymetadataprogressbar').show();
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
				<td><input type="file" id="StudyMetaDataDirectory" name="files[]" multiple webkitdirectory /></td>
				<td>
					<div id="uploadstudymetadatapopup">
						<a href="#uploadstudymetadatapopupInfo" data-rel="popup" data-transition="pop"
							class="my-tooltip-btn ui-btn ui-alt-icon ui-nodisc-icon ui-btn-inline ui-icon-info ui-btn-icon-notext" title="File" style="float: right;">File
							Information</a>
					</div>
					<div data-role="popup" id="uploadstudymetadatapopupInfo" class="ui-content" data-theme="a" style="max-width: 350px;">
						<p>Works on Webkit enabled browsers. Loads all the files and tar gzips them at the server.</p>
					</div>
				</td>
			</tr>
		</table>

		<table>
			<tr>
				<td><input type="file" id="StudyMetaDatafiles" name="StudyMetaDatafiles" /></td>
				<td>
					<div id="uploadsinglestudymetadatapopup">
						<a href="#uploadsinglestudymetadatapopupInfo" data-rel="popup" data-transition="pop"
							class="my-tooltip-btn ui-btn ui-alt-icon ui-nodisc-icon ui-btn-inline ui-icon-info ui-btn-icon-notext" title="File" style="float: right;">File
							Information</a>
					</div>

					<div data-role="popup" id="uploadsinglestudymetadatapopupInfo" class="ui-content" data-theme="a" style="max-width: 350px;">
						<p>Select a single file to upload.</p>
					</div>
				</td>
			</tr>
		</table>
<%if(!hideDesc){ %>
		<label for="descriptorinput">Metadata Descriptor:</label> <input type="text" name="descriptorinput" id="descriptorinput" data-clear-btn="true">
<%} %>
		<button id="uploadstudymetadata_btn">Upload</button>
		<button id="confirmstudymetadataAdd" style="display: none">Update Study</button>
	</div>
	<div data-role="footer" data-mini="true">
		<progress id="studymetadataprogressbar" style="width: 100%; display: none"></progress>
	</div>
</div>