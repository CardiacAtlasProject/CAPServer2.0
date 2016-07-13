<%@page import="javax.naming.InitialContext"%>
<%@page import="nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote"%>
<%@page import="javax.ejb.EJB"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	PACSCAPDatabaseSynchronizerRemote sync;
	InitialContext context = new InitialContext();
	sync = (PACSCAPDatabaseSynchronizerRemote) context
			.lookup("java:global/CAP2.0EJB/CAP2.0Application/PACSCAPDatabaseSynchronizer!nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote");
	sync.recordLogin(request.getRemoteUser());
	String aetitle = sync.getAetitle();
	String hostname = sync.getHostname();
	String port = sync.getPort();
	String wadoPort = sync.getWadoPort();
	String dcmprotocol = sync.getPacsProtocol();

	String tempdir = sync.getTempDir();
	String caetitle = sync.getCaetitle();
	String caeHostname = sync.getCaetname();
	String caePort = sync.getCaeport();
	String modalities = sync.getModalities();
	long syncperiod = sync.getMaximumStoredSubjectTableLifeTime();
	int idletime = sync.getMaxIdleTime();
	long tempFileLife = sync.getTemporaryFileLifeTime();
	long tokensize = sync.getDownloadTokenSize();
	boolean cachepacs = sync.cachePACSImageData();
	boolean constrainModels = sync.constrainModelsToPACSStudies();

	String cachepacsinstancesString = "";
	if (cachepacs)
		cachepacsinstancesString = "checked";
	String constrainModelsString = "";
	if (constrainModels)
		constrainModelsString = "checked";
%>
<script>
	var userJson;
	var usersTable;
	var userActivityTable;
	var selectedUser;

	function activateUserTable() {

		// Call the necessary code to active jquery and datatables
		// Determine when the table has been reloaded
		$('#usertable').on('xhr.dt', function(e, settings, json) {
			userJson = json.data;
		});

		usersTable = $('#usertable').DataTable({
			"processing" : true,
			"serverSide" : true,
			"ajax" : "./CAPUserServices",
			"dom" : 'T<"clear">lfrtip',
			"contentType" : "application/json",
			"columns" : [ {
				"data" : "ID",
				className : "dt-body-center"
			}, {
				"data" : "NAME"
			} ],
			"order" : [ [ 0, 'asc' ] ]
		});

		userActivityTable = $('#userActivitytable').DataTable({
			"processing" : true,
			"serverSide" : true,
			"ajax" : "./CAPUserServices/?activitylog=true",
			"dom" : 'T<"clear">lfrtip',
			"contentType" : "application/json",
			"columns" : [ {
				"data" : "ACTIVITY",
				className : "dt-body-center",
				"orderable" : false
			}, {
				"data" : "QUANTITY",
				"orderable" : false
			}, {
				"data" : "TIME"
			} ],
			"order" : [ [ 2, 'asc' ] ]
		});

		$('#usertable tbody').on(
				'click',
				'tr',
				function() {
					//Highlighting
					usersTable.$('tr.selected').removeClass('selected');
					$(this).addClass('selected');

					var tr = $(this).closest('tr');
					var rdata = usersTable.row(this).data();
					if (rdata.ID != selectedUser) {
						selectedUser = rdata.ID;
						$('#currentuserprop').show();
						$("#currentusername").html(
								"</b>Properties of " + rdata.NAME + "</b>");
						$("#currentuserdescription")
								.html(
										"</b>Brief description: </b><br> "
												+ rdata.DESC);
						$("#currentuserlastlogin").html(
								"<b>Last Login:</b> " + rdata.LASTLOGIN);
						$("#currentuserlastpass").html(
								"<b>Last Password Change: </b>"
										+ rdata.LASTPWDCHANGE);
						var roles = JSON.parse(rdata.ROLES);
						if ($.inArray("CAPADMIN", roles) > -1) {
							$('#currentuseradmin').addClass('ui-icon-check')
									.removeClass('ui-icon-delete');
						} else {
							$('#currentuseradmin').addClass('ui-icon-delete')
									.removeClass('ui-icon-check');
						}
						if ($.inArray("CAPAUTHOR", roles) > -1) {
							$('#currentuserauthor').addClass('ui-icon-check')
									.removeClass('ui-icon-delete');
						} else {
							$('#currentuserauthor').addClass('ui-icon-delete')
									.removeClass('ui-icon-check');
						}

						$('#useractivityport').show();
						userActivityTable.ajax.url(
								'./CAPUserServices?activitylog=' + rdata.ID)
								.load();
					}
				});

		//System log
		systemActivityTable = $('#systemNotification').DataTable({
			"processing" : true,
			"serverSide" : true,
			"ajax" : "./admin?systemlog",
			"dom" : 'T<"clear">lfrtip',
			"contentType" : "application/json",
			"columns" : [ {
				"data" : "EVENT",
				className : "dt-body-center",
				"orderable" : false
			}, {
				"data" : "MESSAGE",
				"orderable" : false
			}, {
				"data" : "TIME"
			} ],
			"order" : [ [ 2, 'desc' ] ]
		});

		$('#systemNotification tbody').on('click', 'tr', function() {
			$(this).toggleClass('selected');
		});

		//Activity log
		systemTasksTable = $('#systemtaskstable').DataTable({
			"processing" : true,
			"serverSide" : true,
			"ajax" : "./admin?activeTasks",
			"dom" : 'T<"clear">lfrtip',
			"contentType" : "application/json",
			"columns" : [ {
				"data" : "RESOURCE",
				"orderable" : false
			}, {
				"data" : "PROGRESS",
				className : "dt-body-center",
				"orderable" : false
			} ],
			"order" : [ [ 0, 'asc' ] ]
		});

		$('#systemtaskstable tbody').on('click', 'tr', function() {
			$(this).toggleClass('selected');
		});

		systemActivitySummarytableS = $('#systemActivitySummarytable')
				.DataTable({
					"processing" : true,
					"serverSide" : true,
					"ajax" : "./admin?useractivitysummary",
					"contentType" : "application/json",
					"columns" : [ {
						"data" : "ACTIVITY",
						"orderable" : false
					}, {
						"data" : "QUANTITY",
						className : "dt-body-center",
						"orderable" : false
					}, {
						"data" : "DATE",
						className : "dt-body-center",
						"orderable" : false
					} ],
					"order" : [ [ 0, 'asc' ] ]
				});

	}

	$('.unSelectallActivity').bind('click', function() {
		systemActivityTable.$('tr').removeClass('selected');
	});

	$('.selectallActivity').bind('click', function() {
		systemActivityTable.$('tr').addClass('selected');
	});

	function callterminatetask(byt) {
		var row = $('#systemtaskstable').DataTable().rows('.selected').data();
		if (row.length < 1) {
			return;
		}
		var rid = row[0].RESOURCE;
		var requestObj = new Object();
		requestObj["resourceid"] = rid;
		requestObj["purgeTask"] = "true"
		$
				.ajax({
					url : "./admin",
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
						$('#systemtaskstable').DataTable().ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});
	}

	function purgecapsystemlog(byt) {
		var data = systemActivityTable.rows('.selected').data();
		if (data.length < 1) {
			return;
		}
		var arr = new Array();
		for (var i = 0; i < data.length; i++) {
			arr.push(data[i].DT_RowId);
		}

		var requestObj = new Object();
		requestObj["select"] = JSON.stringify(arr);
		requestObj["purgesystemlog"] = "true"
		$
				.ajax({
					url : "./admin",
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
						$('#systemNotification').DataTable().ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});
	}

	function roleICONClicker(aref) {
		//console.debug(selectedUser)
		if (selectedUser == null) {
			alert("No user has been selected. Click on the table row to select a user")
			return;
		}
		var aref = $(aref);
		//console.debug(aref.text());
		//Do the processing and if successful
		var role = 1;
		if (aref.text() == "ADMIN") {
			role = 2;
		}
		if (aref.text() == "READER") {
			role = 0;
		}
		var classes = aref.attr('class');
		var checked = classes.indexOf('ui-icon-check') > -1;

		var requestObj = new Object();
		if (checked)
			requestObj["removeRole"] = selectedUser;
		else
			requestObj["addRole"] = selectedUser;
		requestObj["roles"] = "[" + role + "]";
		$
				.ajax({
					url : "./CAPUserServices",
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
						if (checked) {
							aref.addClass('ui-icon-delete').removeClass(
									'ui-icon-check');
						} else {
							aref.addClass('ui-icon-check').removeClass(
									'ui-icon-delete');
						}
						$('#systemNotification').DataTable().ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});

	}

	$("#createnewuser").on('click', function() {
		$.mobile.changePage("./createNewUser.html", {
			transition : 'pop',
			role : 'dialog'
		});
	});

	function createnewuser(but) {

		$('.uierror').hide();

		var userid = $('#createnewuseruserID').val();
		var username = $('#createnewuseruserName').val();
		var userdesc = $('#createnewuseruserDesc').val();
		var userpass = SHA1($('#createnewuseruserPass').val());
		var author = $('#createnewuserAuthor').is(':checked');
		var admin = $('#createnewuserAdmin').is(':checked');
		if (userid.length < 5) {
			$('#useriderror').show();
			$('#createnewuseruserID').attr('placeholder', 'Set user id');
			return;
		}
		if (username.length < 5) {
			$('#usernameerror').show();
			$('#createnewuseruserName').attr('placeholder', 'Set user name');
			return;
		}
		if (userdesc.length < 5) {
			$('#userdescerror').show();
			$('#createnewuseruserDesc').attr('placeholder',
					'Provide some description');
			return;
		}
		if ($('#createnewuseruserPass').val().length < 6) {
			$('#userpasserror').show();
			return;
		}

		var roles = [ 0 ];
		if (author)
			roles.push(1);
		if (admin)
			roles.push(2);
		var requestObj = new Object();
		requestObj["adduser"] = userid;
		requestObj["username"] = username;
		requestObj["userdesc"] = userdesc;
		requestObj["newpass"] = userpass;
		requestObj["roles"] = JSON.stringify(roles);

		$
				.ajax({
					url : "./CAPUserServices",
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
						$(".ui-dialog").dialog("close");
						alert("Successfully added the user.");
						usersTable.ajax.reload(null, false); // user paging is not reset on reload
						$('#systemNotification').DataTable().ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						try {
							if (errorThrow.error.indexOf("exists")) {
								alert("UserID already exists!!");
								return;
							}
						} catch (e) {

						}
						$(".ui-dialog").dialog("close");
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});
	}

	function resetUserPassword(bt) {
		$.mobile.changePage("./resetPassword.html", {
			transition : 'pop',
			role : 'dialog'
		});
	}

	
	function resetSelectedUserPassword(bt) {
		//console.debug(selectedUser)
		if (selectedUser == null) {
			alert("No user has been selected. Click on the table row to select a user")
			return;
		}

		if ($('#resetUserPass').val().length < 6) {
			$('#resetpasserror').show();
			return;
		}
		$('#resetpasserror').hide();
		var requestObj = new Object();
		requestObj["changePassword"] = selectedUser;
		requestObj["newpass"] = SHA1($('#resetUserPass').val());
		$
				.ajax({
					url : "./CAPUserServices",
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
						$(".ui-dialog").dialog("close");
						alert("Successfully changed password for "
								+ selectedUser);
						selectedUser = null;
						usersTable.ajax.reload(null, false); // user paging is not reset on reload
						$('#systemNotification').DataTable().ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						$(".ui-dialog").dialog("close");
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});
	}

	function deleteSelectedUser(bt) {
		//console.debug(selectedUser)
		if (selectedUser == null) {
			alert("No user has been selected. Click on the table row to select a user")
			return;
		}
		var requestObj = new Object();
		requestObj["removeuser"] = selectedUser;
		$
				.ajax({
					url : "./CAPUserServices",
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
						alert("Successfully removed user." + selectedUser);
						selectedUser = null;
						usersTable.ajax.reload(null, false); // user paging is not reset on reload
						$('#systemNotification').DataTable().ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});
	}

	function purgeactivitylogforselecteduser(bt) {
		//console.debug(selectedUser)
		if (selectedUser == null) {
			alert("No user has been selected. Click on the table row to select a user")
			return;
		}
		var requestObj = new Object();
		requestObj["activitylogpurge"] = selectedUser;
		$
				.ajax({
					url : "./CAPUserServices",
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
						$(".ui-dialog").dialog("close");
						alert("Successfully purged " + selectedUser
								+ "'s activity log' ");
						selectedUser = null;
						userActivityTable.ajax.reload();
					},
					error : function(jqXHR, textStatus, errorThrown) {
						$(".ui-dialog").dialog("close");
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});

	}

	setInterval(function() {
		var alive = checkIfSessionIsAlive();
		if(alive){
			$('#systemNotification').DataTable().ajax.reload();
			usersTable.ajax.reload(null, false); // user paging is not reset on reload
			userActivityTable.ajax.reload();
			systemTasksTable.ajax.reload();
			systemActivitySummarytableS.ajax.reload();
		}
	}, 300000);//Refresh every 5 mins

	function pacs(byt) {
		var bt = $(byt);

		var key = bt.val();
		var ctrl = key.toString().toLowerCase();

		var value = $('#' + ctrl).val();
		var requestObj = new Object();
		requestObj["update"] = bt.val();
		requestObj["entity"] = value;
		$
				.ajax({
					url : "./admin",
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
						alert("Updated");
					},
					error : function(jqXHR, textStatus, errorThrown) {
						$(".ui-dialog").dialog("close");
						alert("Unable to complete request. Server Error!!"
								+ textStatus);
						//console.debug(errorThrown);
					}
				});

	}
</script>

<div role="main" class="ui-content" id="mainpage">
	<div data-role="tabs" id="admintabs">
		<div data-role="navbar" id="adminnavbar">
			<ul>
				<li><a href="#usermanagement" id="usermanagementnav">User Management</a></li>
				<li><a href="#capstatistics" id="capstatisticsnav">Server Monitor</a></li>
				<li><a href="#systemsettings" id="systemsettingsnav">System Settings</a></li>
			</ul>
		</div>
		<div id="usermanagement" class="ui-body-d ui-content">
			<div role="main" class="ui-content">
				<div class="ui-grid-a">
					<!-- Two column grid -->
					<div class="ui-block-a">
						<div style="width: 75%">
							<h4>Users</h4>
							<table id="usertable" class="display">
								<thead>
									<tr>
										<th>ID</th>
										<th>Name</th>
									</tr>
								</thead>

								<tfoot>
									<tr>
										<th>ID</th>
										<th>Name</th>
									</tr>
								</tfoot>
							</table>
						</div>
					</div>
					<div class="ui-block-b">
						<div style="width: 100%; margin: 0 auto;">
							<h4>Manage</h4>
							<button class="ui-btn" id="createnewuser">Create User</button>
							<button class="ui-btn" id="deleteuser" onclick="deleteSelectedUser(this);">Delete Selected User</button>
						</div>
					</div>
					<div class="ui-block-a">
						<div style="width: 70%;">
							<h4>Usage Statistics</h4>
							<div style="display: none" id="useractivityport">

								<table id="userActivitytable" class="display">
									<thead>
										<tr>
											<th>Activity</th>
											<th>Bytes</th>
											<th>Date</th>
										</tr>
									</thead>

									<tfoot>
										<tr>
											<th>Activity</th>
											<th>Bytes</th>
											<th>Date</th>
										</tr>
									</tfoot>
								</table>

								<button class="ui-btn" id="purgeactivitylog" onclick="purgeactivitylogforselecteduser(this);">Purge</button>
							</div>
						</div>
					</div>
					<div class="ui-block-b">
						<div style="width: 100%; margin: 0 auto; display: none;" id="currentuserprop">
							<h4>User Properties</h4>
							<div class="ui-field-contain">
								<fieldset data-role="controlgroup">
									<h4 id="currentusername">User Name</h4>
									<p id="currentuserdescription">Brief</p>
									<label id="currentuserlastlogin">Last Login:</label> <label id="currentuserlastpass">Last Password Change:</label>
								</fieldset>
								<fieldset data-role="controlgroup" data-type="horizontal" id="rolegroup">
									<legend>Roles:</legend>
									<table>
										<tr>
											<td><a class="ui-btn ui-icon-check ui-btn-icon-left" onclick="roleICONClicker(this);" id="currentuserreader">READER</a></td>
											<td><a class="ui-btn ui-icon-check ui-btn-icon-left" onclick="roleICONClicker(this);" id="currentuserauthor">AUTHOR</a></td>
											<td><a class="ui-btn ui-icon-delete ui-btn-icon-left" onclick="roleICONClicker(this);" id="currentuseradmin">ADMIN</a></td>
										</tr>
									</table>
								</fieldset>
							</div>
							<button class="ui-btn" onclick="resetUserPassword(this);">Reset Password</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="capstatistics" class="ui-body-d ui-content">
			<div style="height: 40%;">
				<div role="main" class="ui-content">
					<h4>CAP Events</h4>
					<div>
						<div>
							<fieldset data-role="controlgroup" data-type="horizontal" class="localnav" style="float: right" id="systemactivitycontrols">
								<input type="button" value="Un Select All" class="unSelectallActivity" data-mini="true"> <input type="button" value="Select All"
									class="selectallActivity" data-mini="true">
							</fieldset>
						</div>

						<table id="systemNotification" class="display">
							<thead>
								<tr>
									<td>EVENT</td>
									<td>MESSAGE</td>
									<td>TIME</td>
								</tr>
							</thead>
							<tfoot>
								<tr>
									<td>EVENT</td>
									<td>MESSAGE</td>
									<td>TIME</td>
								</tr>
							</tfoot>
						</table>
						<div>
							<fieldset data-role="controlgroup" data-type="horizontal" class="localnav" style="float: right" id="systemactivitycontrols">
								<input type="button" value="Un Select All" class="unSelectallActivity" data-mini="true"> <input type="button" value="Select All"
									class="selectallActivity" data-mini="true">
							</fieldset>
						</div>

						<button class="ui-btn" id="purgesystemlog" onclick="purgecapsystemlog(this);">Purge selected records</button>
					</div>
				</div>
			</div>
			<div class="ui-grid-a">
				<!-- Two column grid -->
				<div class="ui-block-a">
					<h4>System Events</h4>
					<div id="systemactivityport" style="width: 60%; margin: 0 auto;">
						<table id="systemActivitySummarytable" class="display">
							<thead>
								<tr>
									<th>Activity</th>
									<th># Calls/MB</th>
									<th>Date</th>
								</tr>
							</thead>

							<tfoot>
								<tr>
									<th>Activity</th>
									<th># Calls/MB</th>
									<th>Date</th>
								</tr>
							</tfoot>
						</table>
					</div>
				</div>
				<div class="ui-block-b">
					<div style="width: 60%; margin: 0 auto;" id="systemtasksport">
						<h4>Active tasks</h4>
						<div id="systemtasksports">
							<table id="systemtaskstable" class="display">
								<thead>
									<tr>
										<th>Resource ID</th>
										<th>Progress</th>
									</tr>
								</thead>

								<tfoot>
									<tr>
										<th>Resource ID</th>
										<th>Progress</th>
									</tr>
								</tfoot>
							</table>
							<button class="ui-btn" id="terminatetask" onclick="callterminatetask(this);">Terminate selected task</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="systemsettings" class="ui-body-d ui-content">
			<div role="main" class="ui-content">
				<h2>CAP settings</h2>
			</div>
			<div class="ui-grid-a">
				<!-- Two column grid -->
				<div class="ui-block-a">
					<div style="width: 45%">
						<h4>PACS Settings</h4>
						<table>
							<tr>
								<td><label for="aetitle">AET</label></td>
								<td><input data-mini="true" type="text" name="aetitle" id="aetitle" value="<%=aetitle%>"></td>
								<td>
									<button type="submit" id="AETBTN" value="AETITLE" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="hostname">HOSTNAME</label></td>
								<td><input data-mini="true" type="text" name="hostname" id="hostname" value="<%=hostname%>"></td>
								<td>
									<button type="submit" id="HOSTNAMEBTN" value="HOSTNAME" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="hostport">PORT</label></td>
								<td><input data-mini="true" type="text" name="hostport" id="hostport" value="<%=port%>"></td>
								<td>
									<button type="submit" id="HOSTPORTBTN" value="HOSTPORT" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="protocol">PROTOCOL</label></td>
								<td><input data-mini="true" type="text" name="protocol" id="protocol" value="<%=dcmprotocol%>"></td>
								<td>
									<button type="submit" id="HOSTPROTOCOLBTN" value="PROTOCOL" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="wadoport">WADOPORT</label></td>
								<td><input data-mini="true" type="text" name="wadoport" id="wadoport" value="<%=wadoPort%>"></td>
								<td>
									<button type="submit" id="HOSTWADOPORTBTN" value="WADOPORT" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="ui-block-b">
					<div style="width: 100%; margin: 0 auto;">
						<h4>Calling AET</h4>
						<table>
							<tr>
								<td><label for="caetitle">AET</label></td>
								<td><input data-mini="true" type="text" name="caetitle" id="caetitle" value="<%=caetitle%>"></td>
								<td>
									<button type="submit" id="CAETBTN" value="CAETITLE" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="caehostname">HOSTNAME</label></td>
								<td><input data-mini="true" type="text" name="caehostname" id="caehostname" value="<%=caeHostname%>"></td>
								<td>
									<button type="submit" id="CAEHOSTNAMEBTN" value="CAEHOSTNAME" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="caeport">PORT</label></td>
								<td><input data-mini="true" type="text" name="caeport" id="caeport" value="<%=caePort%>"></td>
								<td>
									<button type="submit" id="CAEPORTBTN" value="CAEPORT" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="modalities">Retrieve Modalities</label></td>
								<td><input data-mini="true" type="text" name="modalities" id="modalities" value="<%=modalities%>"></td>
								<td>
									<button type="submit" id="MODALITIESBTN" value="MODALITIES" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="cachepacsinstances">CACHE Pacs Instances</label></td>
								<td><input type="checkbox" name="cachepacsinstances" id="cachepacsinstances" class="custom" data-mini="true" <%=cachepacsinstancesString%> /></td>
								<td>
									<button type="submit" id="CACHEPACSINSTANCESBTN" value="CACHEPACSINSTANCES" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td colspan=2><label for="constrainmodels">Models must have a study in the DB</label> <input type="checkbox" name="constrainmodels"
									id="constrainmodels" class="custom" data-mini="true" <%=constrainModelsString%> /></td>
								<td>
									<button type="submit" id="CONSTRAINMODELSBTN" value="CONSTRAINMODELS" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="ui-block-a">
					<div style="width: 100%;">
						<h4>Resources</h4>
						<table>
							<tr>
								<td><label for="scratch">Temporary Directory</label></td>
								<td><input data-mini="true" type="text" name="scratch" id="scratch" value="<%=tempdir%>"></td>
								<td>
									<button type="submit" id="SCRATCHBTN" value="SCRATCH" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="tokensize">MAX Download Token Size</label></td>
								<td><input data-mini="true" type="text" name="tokensize" id="tokensize" value="<%=tokensize%>"></td>
								<td>
									<button type="submit" id="TOKENSIZEBTN" value="TOKENSIZE" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="resourcelife">MAX Download Resource Life</label></td>
								<td><input data-mini="true" type="text" name="resourcelife" id="resourcelife" value="<%=tempFileLife%>"></td>
								<td>
									<button type="submit" id="RESOURCELIFEBTN" value="RESOURCELIFE" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="ui-block-b">
					<div style="width: 100%; margin: 0 auto;">
						<h4>Synchronization</h4>
						<table>
							<tr>
								<td><label for="spsc">STABLE PACS SYNC CHECK</label></td>
								<td><input data-mini="true" type="text" name="idletime" id="idletime" value="<%=idletime%>"></td>
								<td>
									<button type="submit" id="SPSCBTN" value="IDLETIME" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="sywpe">FULL SYNC WITH PACS PERIOD</label></td>
								<td><input data-mini="true" type="text" name="syncperiod" id="syncperiod" value="<%=syncperiod%>"></td>
								<td>
									<button type="submit" id="SYWPEBTN" value="SYNCPERIOD" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
							<tr>
								<td><label for="syncnow">SYNC NOW</label></td>
								<td><input data-mini="true" type="text" name="syncnow" id="syncnow" value=""></td>
								<td>
									<button type="submit" id="syncnow" value="SYNCNOW" onclick="pacs(this);" class="ui-btn">Update</button>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>