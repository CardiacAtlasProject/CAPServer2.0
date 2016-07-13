var loadService = true;
var applicationBaseLocation;


$(document).ready( function() {
		applicationBaseLocation = window.location.origin;
		$('#searchform').hide();
		$('#searchparam').hide();
					setTimeout(
							function() {
								$('#mainpage').load('AdminContent.jsp',
												function(responseText,textStatus, req) {
													if (textStatus != "error") {
														loadService = false;
														$('#admintabs').tabs();
														$('#adminnavbar').navbar();// Call navbar to create it
														$('#tabnavbar').navbar();// Call
														$('.localnav').controlgroup();
														activateUserTable();
														$('#administration').show();
													} else {
														alert("Unable to initialize service!!");
														console.error(responseText);
													}
												});
							}, 1000);
		setInterval(checkIfSessionIsAlive,36000000);//Every 10 minutes
});
