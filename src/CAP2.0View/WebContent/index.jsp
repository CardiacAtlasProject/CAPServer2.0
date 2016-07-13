<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<!-- Ensure that the page is not cached -->
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />

<title>CAP2.0</title>

<link rel="stylesheet" type="text/css" href="./css/jquery.dataTables.css">
<script type="text/javascript" src="./js/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="./js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="./js/dojosha1.js"></script>

<script src="./mobile/jquery.mobile-1.4.5.min.js"></script>
<link rel="stylesheet" href="./mobile/jquery.mobile-1.4.5.min.css">
<script src="./mobile/jquery.ui.datepicker.js"></script>
<script id="mobile-datepicker" src="./mobile/jquery.mobile.datepicker.js"></script>
<link rel="stylesheet" href="./mobile/jquery.mobile.datepicker.css">
<meta name="viewport" content="width=device-width, initial-scale=1">
<script type="text/javascript" src="./extensions/Responsive/js/dataTables.responsive.min.js">
	</script>
<link href="./extensions/Responsive/css/dataTables.responsive.css" rel="stylesheet" type="text/css"/>

<style type="text/css" class="init">
td.details-control {
	background: url('./images/details_open.png') no-repeat center center;
	cursor: pointer;
}

tr.details td.details-control {
	background: url('./images/details_close.png') no-repeat center center;
}
</style>
<style id="custom-label-flipswitch">
/* Custom indentations are needed because the length of custom labels differs from
   the length of the standard labels */
.custom-label-flipswitch.ui-flipswitch .ui-btn.ui-flipswitch-on {
	text-indent: -4em;
}

.custom-label-flipswitch.ui-flipswitch .ui-flipswitch-off {
	text-indent: 0.0em;
}
</style>


	
<%//Get user information to determine access levels
String username = request.getRemoteUser();
boolean adminuser = request.isUserInRole("CAPADMIN");
boolean author = request.isUserInRole("CAPAUTHOR");
boolean reader = request.isUserInRole("CAPREADER");
String indexFile = "void.js"; 
if(reader)
	indexFile = "reader.js";
if(adminuser){
	indexFile = "admin.js";
}
if(author){
	indexFile = "author.js";
}
if(adminuser&&author){
	indexFile = "adminauthor.js";
}
if(adminuser&&reader&!author){
	indexFile = "adminreader.js";
}

%>

<script type="text/javascript" src="./js/<%=indexFile%>"></script>

<script>

function checkIfSessionIsAlive(){
	var alive = false;
	$.ajax({
		url : "./UserRequests?session=true",
		type : "POST",
		success : function(data, textStatus, jqXHR) {
			alive = true;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			//console.debug(jqXHR);
			$.mobile.changePage("./Login.jsp", {transition: 'pop', role: 'dialog'});
		}
	});
	return alive;
}


function userRestPassword(but){
	$('.uierror').hide();
	if($('#userresetPass').val().length<6){
		$('#passreseterror').show();
		return;
	}
	var oldPass = SHA1($('#resetUserOldPass').val());
	var newPass = SHA1($('#userresetPass').val());
	var confirmPass = SHA1($('#resetUserConfirmPass').val());
	var match = false;
	// compare lengths - can save a lot of time 
    if (newPass.length == confirmPass.length)
        match=true;
	if(match){
	    for (var i = 0, l=newPass.length; i < l; i++) {
	        // Check if we have nested arrays
	        if (newPass[i] instanceof Array && confirmPass[i] instanceof Array) {
	            // recurse into the nested arrays
	            if (!newPass[i].equals(confirmPass[i])){
	                match=false;
	                break;       
	            }
	        }           
	        else if (newPass[i] != confirmPass[i]) { 
	            // Warning - two different object instances will never be equal: {x:20} != {x:20}
	        	match=false;
                break;          
	        }           
	    }       
	}
	if(match){
		var requestObj = new Object();
		requestObj["changePassword"] = 'true';
		requestObj["oldpass"] = oldPass;
		requestObj["newpass"] = newPass;
		$.ajax({
			url : "./UserRequests",
			type : "POST",
			data : requestObj,
			dataType : 'json',
			beforeSend : function() {
				$.mobile.loading('show', {
					theme : 'z',
					html : ""
				});
			},
			complete : function() {
				$.mobile.loading('hide');
			},
			success : function(data, textStatus, jqXHR) {
				if(data.error){
					alert("Failed to change password: "+data.error);
				}else{
					alert("Password changed");
				}
				$( ".ui-dialog" ).dialog( "close" ); 
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert('Internal server error!Please contact the Administrator');
				console.debug(jqXHR);
			}
		});
	}else{
		$('#').show();
		return;
	}
}

function passwordReset(){
	$.mobile.changePage("./changePassword.html", {transition: 'pop', role: 'dialog'});	
};

function logout(usr){
/* 	var form = document.createElement("form");
	  form.action = "./Login.jsp";
	  form.method = 'POST';
	  form.target =  "_self";
      var input = document.createElement("textarea");
      input.name = "action";
      input.value = "logout";
      form.appendChild(input);
	  form.style.display = 'none';
	  document.body.appendChild(form);
	  form.submit(); */

	var requestObj = new Object();
	requestObj["logout"] = 'true';
	$.ajax({
		url : "./UserRequests",
		type : "POST",
		data : requestObj,
		dataType : 'json',
		success : function(data, textStatus, jqXHR) {
		},
		error : function(jqXHR, textStatus, errorThrown) {
			window.open("./","_self");
		}
	});
};


</script>

</head>
<body>
	<div data-role="page" id="searchcap" data-title="Cardiac Atlas Project">

		<div data-role="header" data-position="fulscreen" data-theme="a" data-fullscreen="true">
			<!-- <h1>Cardiac Atlas Project</h1> -->
			<div style="text-align: center; margin: 0 auto;">
				<img src="./images/caplogo.png" alt="Cardiac Atlas Project" />
			</div>
			<!-- <a href="#outside" data-icon="bars" data-iconpos="notext" style="top: 30%;">Login</a>  -->
			<a href="#loginMenu" style="top: 30%;" data-iconpos="notext" data-rel="popup" data-role="button" data-inline="true" data-transition="slidefade"
				data-icon="gear" data-theme="e">Choose an Action</a>
			<div data-role="popup" id="loginMenu" data-theme="d">
				<ul data-role="listview" data-inset="true" style="min-width: 210px;" data-theme="d">
					<li data-role="divider" data-theme="e">Hi <%=username%></li>
					<li><a onclick="passwordReset(this);">Change password</a></li>
					<li><a onclick="logout(this);">Logout</a></li>
				</ul>
			</div>


			<a href="#searchform" data-icon="search" data-iconpos="notext" style="top: 30%" id="searchparam">Search</a>

		</div>
		<!-- /header -->
		<div role="main" class="ui-content" id="mainpage">
			<div id="welcomemessage" style="width: 50%; text-align: center; margin: 0 auto; background-color: white;">
				<h2>The Cardiac Atlas Project</h2>
				<p align="left">The Cardiac Atlas Project seeks to establish a structural and functional atlas of the heart. This project is dedicated to combine
					cardiac modeling and biophysical analysis methods with a structural database for the comprehensive mapping of heart structure and function. We have
					collected more than 2,500 de-identified cardiac patients along with their corresponding 3D finite element models. Researchers can therefore apply to access
					the cardiac data for specific research projects and clinicians are encouraged to contribute new cases.</p>
				<p align="left">The Cardiac Atlas Project was funded by the National Heart, Lung and Blood Institute, USA, part of the National Institutes of Health.
					(R01HL087773).</p>
			</div>
		</div>
		<!--  page  -->
		<div data-role="panel" data-position="right" data-position-fixed="true" data-display="overlay" data-theme="a" id="searchform">
			<div class="ui-corner-all custom-corners">
				  
				<div class="ui-bar ui-bar-a">
					    
					<h3>Search Parameters</h3>

				</div>
				  
				<div class="ui-body ui-body-a">

					<div class="ui-mini">
						<label for="id">ID:</label> <input type="text" name="id" id="id" value="" data-clear-btn="true" /> <label for="name">Name:</label> <input type="text"
							name="name" id="name" value="" data-clear-btn="true" /> <label for="lsd">Study date after:</label> <input type="text" data-role="date" name="lsd"
							id="lsd" data-inline="false" data-clear-btn="true"> <label for="usd">Study date before:</label> <input type="text" data-role="date" name="usd"
							id="usd" data-inline="false" data-clear-btn="true"> <label for="queryTarget">Query:</label> <input type="checkbox" data-role="flipswitch"
							name="queryTarget" id="queryTarget" data-on-text="DICOM" data-off-text="Models" data-wrapper-class="custom-label-flipswitch" checked> <input
							type="button" value="Search" id="searchbutton"> <input type="button" value="Close" id="closepanel">
					</div>
					     
				</div>
			</div>

		</div>
		<!-- /panel -->

		<div data-role="footer" data-position="fixed" data-theme="a" data-fullscreen="true">
			<h1>&copy; University of Auckland</h1>
		</div>
		<!-- /footer -->
	</div>
</body>
</html>