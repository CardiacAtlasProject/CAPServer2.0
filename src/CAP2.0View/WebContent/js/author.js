var loadService = true;
var applicationBaseLocation;

function executeQuery(query, dicomTarget) {
	if (dicomTarget) {
		if (dicomTab == -1) {
			$('#dicomsearch').show();
			dicomTab++;
			activateDicomSearch(query);
		} else {
			var dt = $('#dicomtable').DataTable();
			dt.ajax.url('./query?search=' + JSON.stringify(query)).load();
		}
		$('#searchtabs').tabs("option", "active", 0);
	} else {
		if (modelab == -1) {
			$('#modelsearch').show();
			modelab++;
			activateModelSearch(query);
		} else {
			var dt = $('#modeltable').DataTable();
			dt.ajax.url('./query?search=' + JSON.stringify(query)).load();
		}
		$('#searchtabs').tabs("option", "active", 1);
	}
	if (dicomTab > -1 && modelab > -1) {
			$('#dicomsearchnav').show();
			$('#modelsearchnav').show();
	}
}


$(document).ready( function() {
		applicationBaseLocation = window.location.origin;
		
		$("#searchbutton").bind("click",function(event, ui) {
										// Set the busy window
										var subject_id = $('#id').val();
										var subject_name = $('#name').val();
										var lsd = $('#lsd').val();
										var usd = $('#usd').val();
										var dicomTarget = $('#queryTarget').is(':checked');

										var query = new Object();

										if (!dicomTarget) {
											query['model'] = 'true';
										}

										if (subject_id.length > 0) {
											query['subject_id'] = subject_id;
										}
										if (subject_name.length > 0) {
											query['subject_name'] = subject_name;
										}
										if (lsd.length > 0) {
											query['lsd'] = lsd;
										}
										if (usd.length > 0) {
											query['subject_name'] = usd;
										}

										$('#mainpage').load('AuthorContent.jsp',function(
															responseText,
															textStatus,
															req) {
														if (textStatus != "error") {
															loadService = false;
															$('#searchtabs').tabs();
															$('#tabnavbar').navbar();// Call navbar to create it
															$('.localnav').controlgroup();
															executeQuery(
																	query,
																	dicomTarget);
														} else {
															alert("Unable to initialize service!!");
															console.error(responseText);
														}
										});
											
										// Close the panel
										$("#searchform").panel("close");
									});
					$("#closepanel").bind("click", function(event, ui) {
						$("#searchform").panel("close");
					});
					setInterval(checkIfSessionIsAlive(),36000000);//Every 10 minutes
});
