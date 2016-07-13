<%@page import="java.util.Enumeration"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote"%>
<%@page import="javax.ejb.EJB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	PACSCAPDatabaseSynchronizerRemote sync;
	InitialContext context = new InitialContext();
	sync = (PACSCAPDatabaseSynchronizerRemote) context
			.lookup("java:global/CAP2.0EJB/CAP2.0Application/PACSCAPDatabaseSynchronizer!nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote");
	sync.recordLogin(request.getRemoteUser());
%>
<script>
	var elements = new Object();
	var activeStudies = new Array();
	var detailDicomRows = new Array;
	var dicomJson;
	var modelelements = new Object();
	var activeModels = new Array();
	var detailModelRows = new Array;
	var modelJson;
	var dicomTab = -1;
	var modelab = -1;


	var selectedModel;
    var selectedStudy;
	
	function processMetaDataRequest(button){
		selectedModel = null;
	    selectedStudy = null;
		var byt = $(button);//Get the jquery handle
		var value = byt.val()
	    var text = byt.text();

		//These options are available if any metadata is available
	    if(text=="Download"){
		 var e = document.getElementById("select-choice-"+value);
		 var selection = e.options[e.selectedIndex].text;
		 if(text=="Download"){
			selectedStudy = value;
			downloadStudyMetaData(value,selection);
		 }
		 
		 //console.debug("Act: "+text+" on "+selection+" study is "+value);
	    }else if(text=="Download Meta Data"){
		    selectedModel = value;
		    downloadModelMetaData();
		}
	}

	
    function downloadStudyMetaData(studyid,selection){
        var requestObj = new Object();
		requestObj["downloadStudyMetaData"]=studyid;
		requestObj["descriptor"]=selection;
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
			    window.open(applicationBaseLocation+"/CAP2.0View/resource/"+data.RESOURCE,"_blank");
		    },
		    error: function (jqXHR, textStatus, errorThrown)
		    {
			    alert('Internal server error!Please contact the Administrator');
		 		//console.debug(jqXHR);
		    }
		}); 
    }


    function downloadModelMetaData(){
        var requestObj = new Object();
		requestObj["downloadModelMetaData"]=selectedModel;
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
			    window.open(applicationBaseLocation+"/CAP2.0View/resource/"+data.RESOURCE,"_blank");
		    },
		    error: function (jqXHR, textStatus, errorThrown)
		    {
			    alert('Internal server error!Please contact the Administrator');
		 		//console.debug(jqXHR);
		    }
		}); 
    }


   	
	function modelformat(d) {
		var metadata = d.COMMENTS;
		if (metadata.length == 0)
			metadata = "No comments";
		var mytable = '<table><tr><td>Comments: <textarea rows="10" cols="50" id="modelcommentsfor'+d.MODELID+'">'
				+ metadata
				+ ' </textarea>';
	
		if(d.HASMETADATA=="0"){		
			mytable += '<td><button class="ui-btn" onclick="processMetaDataRequest(this)" value="'+d.MODELID+'" >Download Meta Data</button></td></tr>';
		}else{
			mytable += '</tr>'
		}

		return mytable + '</table></td></tr></table>';
	}
	
	
	function format(d) {
		var metadataj = d.METADATA;
		var metadata;
		if (metadataj.length == 0){
			var options = '<table><tr><td><h4>No MetaData Files</h4></td>';
			metadata = options + '</tr></table>';
		}
		else{
			var metaobj = JSON.parse(metadataj);
			var options = '<table><tr><td><h4>MetaData Files:</h4><select name="select-choice-'+d.UID+'" id="select-choice-'+d.UID+'" class="metadataselect">'
			for(var i=0;i<metaobj.length;i++){
				var dx = metaobj[i];
				options += '<option value="'+i+'">'+dx+'</option>'
			}
			
			options += '</select></td>';
			options += '<td><button class="ui-btn" onclick="processMetaDataRequest(this)" value="'+d.UID+'" >Download</button></td>';
			metadata = options + '</tr></table>';
		}
		
		var mytable = '<table><tr><td>'
				+ metadata
				+ '</td></tr>';
				mytable += '<tr><td><table id = "STUDIES' + d.UID
							+ '" class="studytable">';
				mytable += '<thead><tr><th>SERIES UID</th><th>Modality</th><th>No. of Instances</th></tr></thread>';

		var obj = $.parseJSON(d.SERIES);
		for (series in obj) {
			var uid = obj[series].UID;
			var count = obj[series].INSTANCES.length;
			var modality = obj[series].MODALITY;
			if (count > 0)
				mytable += '<tr><td>' + uid + '</td><td>' + modality
						+ '</td><td>' + count + '</td></tr>'
		}
		var JS = document.createElement('script');
		JS.setAttribute("type", "text/javascript");
		JS.text = '$("#STUDIES' + d.ID + '").DataTable(); $(".metadataselect").selectmenu();';
		document.body.appendChild(JS);
		elements[d.UID] = JS;
		return mytable + '</table></td></tr></table>';
	}

	function toggleStudy(cbox) {
		//console.debug(cbox)
		var idx = $.inArray(cbox.value, activeStudies);
		if (cbox.checked) {// Add the study to activeStudies, note this study could
			// have been removed prior
			if (idx < 0) {
				activeStudies.push(cbox.value);
			}
		} else {
			// Remove from the studies array
			activeStudies.splice(idx, 1);
		}
	}

	function toggleModel(cbox) {
		//console.debug(cbox)
		var idx = $.inArray(cbox.value, activeModels);
		if (cbox.checked) {// Add the model to activeModels, note this model could
			// have been removed prior
			if (idx < 0) {
				activeModels.push(cbox.value);
			}
		} else {
			// Remove from the models array
			activeModels.splice(idx, 1);
		}
	}
	
	function activateModelSearch(query){

		// Call the necessary code to active jquery and datatables
		// Determine when the table has been reloaded
		$('#modeltable').on('xhr.dt', function(e, settings, json) {			
			modelJson = json.data;
			// Load the studies into the active studies array
			activeModels = new Array();
			for (var i = 0, ien = modelJson.length; i < ien; i++) {
				activeModels.push(""+modelJson[i].DT_RowId);
			}

			// remove script elements
			for ( var sc in modelelements) {
				document.body.removeChild(modelelements[sc]);
			}
			modelelements = new Object();
		});

		var dt = $('#modeltable')
				.DataTable(
						{
							"processing" : true,
							"serverSide" : true,
							responsive: {
					            details: {
					                renderer: function ( api, rowIdx ) {
					                    // Select hidden columns for the given row
					                    var data = api.cells( rowIdx, ':hidden' ).eq(0).map( function ( cell ) {
					                        var header = $( api.column( cell.column ).header() );
					 					    //Do not show the more column
					 					    if(cell.column!=9){
					                        return '<tr>'+
					                                '<td>'+
					                                header.text()+':'+
					                                '</td> '+
					                                '<td>'+
					                                    api.cell( cell ).data()+
					                                '</td>'+
					                            '</tr>';
					 					    }else{
												return null;
						 					    }						                    } ).toArray().join('');
					 
					                    return data ?
					                        $('<table/>').append( data ) :
					                        false;
					                }
					            }
					     },
							"ajax" : "./query?search="+JSON.stringify(query),
							"contentType": "application/json",
							"columns" : [
									{
										"data" : "ID",
										className : "dt-body-center"
									},
									{
										"data" : "NAME"
									},
									{
										"data" : "DOB",
										className : "dt-body-center"
									},
									{
										"data" : "GENDER",
										className : "dt-body-center"
									},
									{
										"data" : "MODELNAME",
										className : "dt-body-center"
									},
									{
										"data" : "DATE",
										className : "dt-body-center"
									},
									{
										"data" : "DESC",
										"orderable" : false,
									},
									{
										"data" : "MODALITIES",
										"orderable" : false,
										className : "dt-body-center"
									},
									{
										"data" : "MODELID",
										render : function(data, type, row) {
											return '<input type="checkbox" value = "'
													+ data
													+ '" onchange="toggleModel(this);" class="modelSelector" checked="true">';
										},
										className : "dt-body-center",
										"orderable" : false
									},									
									{
										"class" : "details-control",
										"orderable" : false,
										"data" : null,
										"defaultContent" : ""
									}, 
									],
							
							"order" : [ [ 0, 'asc' ] ]
						}
						);

		// Array to track the ids of the details displayed rows
		detailModelRows = [];

		$('#modeltable tbody').on('click', 'tr td:last-child', function() {
			var tr = $(this).closest('tr');
			if (tr.attr('id')) {// Do this only if the clicked row belongs to the
				// primary table
				var row = dt.row(tr);

				var idx = $.inArray(tr.attr('id'), detailModelRows);

				if (row.child.isShown()) {
					tr.removeClass('details');
					row.child.hide();

					// Remove from the 'open' array
					detailModelRows.splice(idx, 1);
				} else {
					tr.addClass('details');
					if (!elements[tr.attr('id')])// Create child data if not
						// created ahead
						row.child(modelformat(row.data()));
					// row.child( "Series data here" );
					row.child.show();

					// Add to the 'open' array
					if (idx === -1) {
						detailModelRows.push(tr.attr('id'));
					}
				}
			}
		});


		// On each draw, loop over the `detailRows` array and show any child rows
		dt.on('draw', function() {
			$.each(detailModelRows, function(i, id) {
				//console.debug(this, '\t', id)
				$('#' + id + ' td:last-child').trigger('click');
			});
		});
	
		
	}
	
	function activateDicomSearch(query) {

		// Call the necessary code to active jquery and datatables
		// Determine when the table has been reloaded
		$('#dicomtable').on('xhr.dt', function(e, settings, json) {
			dicomJson = json.data;
			//console.debug(JSON.stringify(dicomJson));
			// Load the studies into the active studies array
			activeStudies = new Array();
			for (var i = 0, ien = dicomJson.length; i < ien; i++) {
				activeStudies.push(""+dicomJson[i].UID);
			}

			// remove script elements
			for ( var sc in elements) {
				document.body.removeChild(elements[sc]);
			}
			elements = new Object();
		});

		var dt = $('#dicomtable')
				.DataTable(
						{
							"processing" : true,
							"serverSide" : true,
							responsive: {
					            details: {
					                renderer: function ( api, rowIdx ) {
					                    // Select hidden columns for the given row
					                    var data = api.cells( rowIdx, ':hidden' ).eq(0).map( function ( cell ) {
					                        var header = $( api.column( cell.column ).header() );
					 					    //Do not show the more column
					 					    if(cell.column!=8){
					                        return '<tr>'+
					                                '<td>'+
					                                header.text()+':'+
					                                '</td> '+
					                                '<td>'+
					                                    api.cell( cell ).data()+
					                                '</td>'+
					                            '</tr>';
					 					    }else{
												return null;
						 					    }						                    } ).toArray().join('');
					 
					                    return data ?
					                        $('<table/>').append( data ) :
					                        false;
					                }
					            }
					     },
							"ajax" : "./query?search="+JSON.stringify(query),
							"dom" : 'T<"clear">lfrtip',
 							//"data" : JSON.stringify(query),
							"contentType": "application/json", 
							"columns" : [
									{
										"data" : "ID",
										className : "dt-body-center"
									},
									{
										"data" : "NAME"
									},
									{
										"data" : "DOB",
										className : "dt-body-center"
									},
									{
										"data" : "GENDER",
										className : "dt-body-center"
									},
									{
										"data" : "DESC",
										"orderable" : false,
									},
									{
										"data" : "MODALITIES",
										"orderable" : false,
										className : "dt-body-center"
									},
									{
										"data" : "DATE",
										className : "dt-body-center"
									},									
									{
										"data" : "UID",
										render : function(data, type, row) {
											return '<input type="checkbox" value = "'
													+ data
													+ '" onchange="toggleStudy(this);" class="dicomSelector" checked="true">';
										},
										className : "dt-body-center",
										"orderable" : false
									}, {
										"class" : "details-control",
										"orderable" : false,
										"data" : null,
										"defaultContent" : ""
									}, ],							
							"order" : [ [ 0, 'asc' ] ]
						});

		// Array to track the ids of the details displayed rows
		detailDicomRows = [];

		$('#dicomtable tbody').on('click', 'tr td:last-child', function() {
			var tr = $(this).closest('tr');
			if (tr.attr('id')) {// Do this only if the clicked row belongs to the
				// primary table
				var row = dt.row(tr);

				var idx = $.inArray(tr.attr('id'), detailDicomRows);

				if (row.child.isShown()) {
					tr.removeClass('details');
					row.child.hide();

					// Remove from the 'open' array
					detailDicomRows.splice(idx, 1);
				} else {
					tr.addClass('details');
					if (!elements[tr.attr('id')])// Create child data if not
						// created ahead
						row.child(format(row.data()));
					// row.child( "Series data here" );
					row.child.show();

					// Add to the 'open' array
					if (idx === -1) {
						detailDicomRows.push(tr.attr('id'));
					}
				}
			}
		});

		// On each draw, loop over the `detailRows` array and show any child rows
		dt.on('draw', function() {
			$.each(detailDicomRows, function(i, id) {
				//console.debug(this, '\t', id)
				$('#' + id + ' td:last-child').trigger('click');
			});
		});

	}


	$(".downloadModels").bind("click", function(event, ui) {
		if(activeModels.length==0){
			alert("At least one model should be chosen");
			return null;
		}else{
			$.mobile.changePage("DicomDownloadDialog.jsp?model=true", {transition: 'pop', role: 'dialog'});
		}
	});
		
	$(".downloadDicom").bind("click", function(event, ui) {
		if(activeStudies.length==0){
			alert("At least one study should be chosen");
			return null;
		}else{
			$.mobile.changePage("DicomDownloadDialog.jsp", {transition: 'pop', role: 'dialog'});
		}
	});


	
	$(".selectallDicom").bind("click", function(event, ui) {
		// Load the studies into the active studies array
		activeStudies = new Array();
		for (var i = 0, ien = dicomJson.length; i < ien; i++) {
			activeStudies.push(dicomJson[i].UID);
		}

		$('.dicomSelector').prop('checked', true);
	});

	$(".unSelectallDicom").bind("click", function(event, ui) {
		activeStudies = new Array();
		$('.dicomSelector').prop('checked', false);
	});


	$(".selectallModels").bind("click", function(event, ui) {
		// Load the studies into the active studies array
		activeModels = new Array();
		for (var i = 0, ien = modelJson.length; i < ien; i++) {
			activeModels.push(""+modelJson[i].DT_RowId);
		}

		$('.modelSelector').prop('checked', true);
	});

	$(".unSelectallModels").bind("click", function(event, ui) {
		activeModels = new Array();
		$('.modelSelector').prop('checked', false);
	});

	//Downloading
	//Dicom
	function openNewWindow(button){
		var but = $(button);
		window.open(but.val(),"_blank");
	}
	
	$('#DicomDowloadDialog').on({
		//Reset the display for next opening 
        popupafterclose: function() {
        	$('#dicomdownloadrequestArea').show();
        	$('#dicomdowloadresult').hide();
         }
     });


	function sendModelDownloadRequest() {
		var request = createDownloadRequest(false);
		if(request!=null){
			//Hide the choice
			$('#dicomdownloadrequestArea').hide();
			$.ajax({
			    url : "./getData",
			    type: "POST",
			    data : request,
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
			        var downloadurl = applicationBaseLocation+"/CAP2.0View/resource/"+data.RESOURCE;
			        $('#dicomdownloadurl').val(downloadurl);
			        $('#dicomdownloadurlform').val('./resource/'+data.RESOURCE);
			        $('#dicomdowloadresult').show();
			    },
			    error: function (jqXHR, textStatus, errorThrown)
			    {
			 		alert("Unable to complete request. Server Error!!");
			 		//console.debug(errorThrown);
			 		$('#dicomdownloadrequestArea').show();
			    }
			});

		}
	}

	
	
	function sendDICOMDownloadRequest() {
		var request = createDownloadRequest(true);
		if(request!=null){
			//Hide the choice
			$('#dicomdownloadrequestArea').hide();
			$.ajax({
			    url : "./getData",
			    type: "POST",
			    data : request,
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
			        var downloadurl = applicationBaseLocation+"/CAP2.0View/resource/"+data.RESOURCE;
			        //console.debug(downloadurl);
			        //console.debug($('#dicomdownloadurl'))
			        $('#dicomdownloadurl').val(downloadurl);
			        $('#dicomdownloadurlform').val('./resource/'+data.RESOURCE);
			        $('#dicomdowloadresult').show();
			    },
			    error: function (jqXHR, textStatus, errorThrown)
			    {
			 		alert("Unable to complete request. Server Error!!");
			 		//console.debug(errorThrown);
			 		$('#dicomdownloadrequestArea').show();
			    }
			});

		}
	}

	function createDownloadRequest(dicom) {
		var selectDicoms = $('#selectDicoms').is(':checked');
		var selectMeta = $('#selectMeta').is(':checked');
		var selectEx = $('#selectModelEXs').is(':checked');
		var selectVTPs = $('#selectModelVTPs').is(':checked');
		var selectModelMeta = $('#selectModelMeta').is(':checked');
		var pattern  = $('#metaPattern').text();
	
		if(!selectDicoms&&!selectMeta&&!selectEx&&!selectVTPs&&!selectModelMeta){
			alert("At least one form of data should be chosen");
			return null;
		}

		var requestArray = new Array();
		if(dicom){
			for(var i=0;i<activeStudies.length;i++){
				var key = new Object();
				key["STUDY"] = "true";
				key["UID"] = activeStudies[i];
				if(selectDicoms)
					key["DICOM"]="true";
				if(selectMeta)
					key["META"]="true";
				if(selectEx)
					key["EX"]="true";
				if(selectVTPs)
					key["VTP"]="true";
				if(selectModelMeta)
					if(pattern.length>0)
						key["MODELMETA"]=pattern;
					else
						key["MODELMETA"]="";
				requestArray.push(key);
			}
		}else{
			for(var i=0;i<activeModels.length;i++){
				var key = new Object();
				key["MODEL"] = "true";
				key["ID"] = ""+activeModels[i];
				if(selectDicoms)
					key["DICOM"]="true";
				if(selectMeta)
					key["META"]="true";
				if(selectEx)
					key["EX"]="true";
				if(selectVTPs)
					key["VTP"]="true";
				if(selectModelMeta)
					key["MODELMETA"]="true";
				requestArray.push(key);
			}
		}
	 return JSON.stringify(requestArray);
	}


	
</script>

<div data-role="tabs" id="searchtabs">
	<div data-role="navbar" id="tabnavbar">
		<ul>
			<li><a href="#dicomsearch" id="dicomsearchnav" style="display: none">DICOMS</a></li>
			<li><a href="#modelsearch" id="modelsearchnav" style="display: none">Model</a></li>
		</ul>
	</div>
	<div id="dicomsearch" class="ui-body-d ui-content">

		<div role="main" class="ui-content">
			<div>
				<fieldset data-role="controlgroup" data-type="horizontal" class="localnav" style="float: right" id="dicomcontrols">
					<input type="button" value="Un Select All" class="unSelectallDicom" data-mini="true"> <input type="button" value="Select All"
						class="selectallDicom" data-mini="true"> <input type="button" value="Download" class="downloadDicom" data-mini="true">
				</fieldset>
			</div>
			<table id="dicomtable" class="display">
				<thead>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Date of Birth</th>
						<th>Gender</th>
						<th>Description</th>
						<th>Modalities</th>
						<th>Date</th>
						<th>Select</th>
						<th>MORE</th>
					</tr>
				</thead>

				<tfoot>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Date of Birth</th>
						<th>Gender</th>
						<th>Description</th>
						<th>Modalities</th>
						<th>Date</th>
						<th>Select</th>
						<th>MORE</th>
					</tr>
				</tfoot>
			</table>
			<div>
				<fieldset data-role="controlgroup" data-type="horizontal" class="localnav" style="float: right" id="dicomcontrols">
					<input type="button" value="Un Select All" class="unSelectallDicom" data-mini="true"> <input type="button" value="Select All"
						class="selectallDicom" data-mini="true"> <input type="button" value="Download" class="downloadDicom" data-mini="true">
				</fieldset>
			</div>
		</div>
	</div>
	<div id="modelsearch" class="ui-body-d ui-content">
		<div role="main" class="ui-content">
			<div>
				<fieldset data-role="controlgroup" data-type="horizontal" class="localnav" style="float: right" id="dicomcontrols">
					<input type="button" value="Un Select All" class="unSelectallModels" data-mini="true"> <input type="button" value="Select All"
						class="selectallModels" data-mini="true"> <input type="button" value="Download" class="downloadModels" data-mini="true"> 
				</fieldset>
			</div>
			<table id="modeltable" class="display">
				<thead>
					<tr>
						<th>Subject ID</th>
						<th>Subject Name</th>
						<th>Date of Birth</th>
						<th>Gender</th>
						<th>Model Name</th>
						<th>Study Date</th>
						<th>Study Description</th>
						<th>Study Modalities</th>
						<th>Select</th>
						<th>More</th>
					</tr>
				</thead>

				<tfoot>
					<tr>
						<th>Subject ID</th>
						<th>Subject Name</th>
						<th>Date of Birth</th>
						<th>Gender</th>
						<th>Model Name</th>
						<th>Study Date</th>
						<th>Study Description</th>
						<th>Study Modalities</th>
						<th>Select</th>
						<th>More</th>
					</tr>
				</tfoot>
			</table>

			<div>
				<fieldset data-role="controlgroup" data-type="horizontal" class="localnav" style="float: right" id="dicomcontrols">
					<input type="button" value="Un Select All" class="unSelectallModels" data-mini="true"> <input type="button" value="Select All"
						class="selectallModels" data-mini="true"> <input type="button" value="Download" class="downloadModels" data-mini="true"> 
				</fieldset>
			</div>
		</div>
	</div>
</div>