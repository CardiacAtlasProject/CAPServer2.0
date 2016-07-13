<!DOCTYPE html>
<html>
<head>
<!-- Do not use any code/images stored as that would require authentication
	 This code should be standalone
 -->
<meta charset="UTF-8">
<style type="text/css">

/*******************
SELECTION STYLING
*******************/
::selection {
	color: #fff;
	background: #f676b2; /* Safari */
}

::-moz-selection {
	color: #fff;
	background: #f676b2; /* Firefox */
}

/*******************
BODY STYLING
*******************/
* {
	margin: 0;
	padding: 0;
	border: 0;
}

body {
	background: #FDFFFF;
	font-family: "HelveticaNeue-Light", "Helvetica Neue Light",
		"Helvetica Neue", Helvetica, Arial, "Lucida Grande", sans-serif;
	font-weight: 300;
	text-align: left;
	text-decoration: none;
}

#wrapper {
	/* Center wrapper perfectly */
	width: 300px;
	height: 400px;
	position: absolute;
	left: 50%;
	top: 50%;
	margin-left: -150px;
	margin-top: -200px;
}

.download {
	display: block;
	position: absolute;
	float: right;
	right: 25px;
	bottom: 25px;
	padding: 5px;
	font-weight: bold;
	font-size: 11px;
	text-align: right;
	text-decoration: none;
	color: rgba(0, 0, 0, 0.5);
	text-shadow: 1px 1px 0 rgba(256, 256, 256, 0.5);
}

.download:hover {
	color: rgba(0, 0, 0, 0.75);
	text-shadow: 1px 1px 0 rgba(256, 256, 256, 0.5);
}

.download:focus {
	bottom: 24px;
}

/*******************
LOGIN FORM
*******************/
.login-form {
	width: 300px;
	margin: 0 auto;
	position: relative;
	z-index: 5;
	background: #dadada;
	border: 1px solid #fff;
	border-radius: 5px;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
}

/*******************
HEADER
*******************/
.login-form .header {
	padding: 40px 30px 30px 30px;
}

.login-form .header h1 {
	font-family: serif;
	font-weight: 300;
	font-size: 34px;
	line-height: 34px;
	color: #090D1D;
	text-shadow: 1px 1px 0 rgba(256, 256, 256, 1.0);
	margin-bottom: 10px;
}

.login-form .header span {
	font-size: 11px;
	line-height: 16px;
	color: #678889;
	text-shadow: 1px 1px 0 rgba(256, 256, 256, 1.0);
}

/*******************
CONTENT
*******************/
.login-form .content {
	padding: 0 30px 25px 30px;
}

/* Input field */
.login-form .content .input {
	width: 188px;
	padding: 15px 25px;
	font-family: "HelveticaNeue-Light", "Helvetica Neue Light",
		"Helvetica Neue", Helvetica, Arial, "Lucida Grande", sans-serif;
	font-weight: 400;
	font-size: 14px;
	color: #9d9e9e;
	text-shadow: 1px 1px 0 rgba(256, 256, 256, 1.0);
	background: #fff;
	border: 1px solid #fff;
	border-radius: 5px;
	box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.50);
	-moz-box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.50);
	-webkit-box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.50);
}

/* Second input field */
.login-form .content .password, .login-form .content .pass-icon {
	margin-top: 25px;
}

.login-form .content .input:hover {
	background: #dfe9ec;
	color: #414848;
}

.login-form .content .input:focus {
	background: #dfe9ec;
	color: #414848;
	box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.25);
	-moz-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.25);
	-webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.25);
}

/* Animation */
.input, .button, .register {
	transition: all 0.5s;
	-moz-transition: all 0.5s;
	-webkit-transition: all 0.5s;
	-o-transition: all 0.5s;
	-ms-transition: all 0.5s;
}

/*******************
FOOTER
*******************/
.login-form .footer {
	padding: 25px 30px 40px 30px;
	overflow: auto;
}

/* Login button */
.login-form .footer .button {
	float: right;
	padding: 11px 25px;
	font-family: serif;
	font-weight: 300;
	font-size: 18px;
	color: #fff;
	text-shadow: 0px 1px 0 rgba(0, 0, 0, 0.25);
	background: #6D7B8D;
	border: 1px solid;
	border-radius: 5px;
	cursor: pointer;
	box-shadow: inset 0 0 2px rgba(256, 256, 256, 0.75);
	-moz-box-shadow: inset 0 0 2px rgba(256, 256, 256, 0.75);
	-webkit-box-shadow: inset 0 0 2px rgba(256, 256, 256, 0.75);
}

.login-form .footer .button:hover {
	background: #3f9db8;
	border: 1px solid rgba(256, 256, 256, 0.75);
	box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.5);
	-moz-box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.5);
}

.login-form .footer .button:focus {
	position: relative;
	bottom: -1px;
	background: #56c2e1;
	box-shadow: inset 0 1px 6px rgba(256, 256, 256, 0.75);
	-moz-box-shadow: inset 0 1px 6px rgba(256, 256, 256, 0.75);
	-webkit-box-shadow: inset 0 1px 6px rgba(256, 256, 256, 0.75);
}

/* Register button */
.login-form .footer .register {
	display: block;
	float: right;
	padding: 10px;
	margin-right: 20px;
	background: none;
	border: none;
	cursor: pointer;
	font-family: 'Bree Serif', serif;
	font-weight: 300;
	font-size: 18px;
	color: #414848;
	text-shadow: 0px 1px 0 rgba(256, 256, 256, 0.5);
}

.login-form .footer .register:hover {
	color: #3f9db8;
}

.login-form .footer .register:focus {
	position: relative;
	bottom: -1px;
}
</style>

<title>Cardiac Atlas Project Server Login</title>
</head>
<body>

	<div id="wrapper">

		<div class="user-icon"></div>
		<div class="pass-icon"></div>

		<form name="login-form" class="login-form" action="j_security_check" method="post">
			<div class="header">
				<h1>Login</h1>
				<span>Cardiac Atlas Project Server</span>
			</div>
			<div class="content">
				<input name="j_username" type="text" class="input username" value="Username" onfocus="this.value=''" /> <input name="j_password" type="password"
					class="input password" value="Password" onfocus="this.value=''" />
			</div>
			<div class="footer">
				<input type="submit" name="submit" value="Login" class="button" />
				<h4>
					<%
						if ("error".equals(request.getParameter("action"))) {
							out.println("Invalid user ID or password");
						}
						if ("logout".equals(request.getParameter("action"))) {
							session.invalidate();
							request.getSession(true);
							out.println("You have been successfully logged off");
						}
					%>
				</h4>
			</div>
		</form>
	</div>

</body>
</html>