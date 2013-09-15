<%@ page
	import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes"%>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title><g:layoutTitle default="${meta(name: 'app.name')}" /></title>
<meta name="description" content="">
<meta name="author" content="">

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
<!--[if lt IE 9]>
			<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

<r:require modules="scaffolding" />

<!-- Le fav and touch icons -->
<link rel="shortcut icon"
	href="${resource(dir: 'images', file: 'favicon.ico')}"
	type="image/x-icon">
<link rel="apple-touch-icon"
	href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
<link rel="apple-touch-icon" sizes="114x114"
	href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">

<g:layoutHead />
<r:layoutResources />
</head>

<body>
	<nav class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">

				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="${createLink(uri: '/')}"></a>

				<div class="nav-collapse">
					<ul class="nav">
						<li
							<%= request.forwardURI == "${createLink(uri: '/')}" ? ' class="active"' : '' %>>
							<a href="${createLink(uri: '/')}">Home</a>
						</li>
						<sec:ifLoggedIn>
							<%--							<g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">--%>
							<li
								<%= 'Manufacturer'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="Manufacturer">Manufacturer</g:link></li>
							<li
								<%= 'Product'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="Product">Product</g:link></li>
							<li
								<%= 'ProductCategory'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="ProductCategory">ProductCategory</g:link></li>
							<li
								<%= 'ProductSegment'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="ProductSegment">ProductSegment</g:link></li>
							<li
								<%= 'LoyaltyProgram'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="LoyaltyProgram">LoyaltyProgram</g:link></li>
							<li
								<%= 'LoyaltyProgramLevels'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="LoyaltyProgramLevels">LoyaltyProgramLevels</g:link></li>
							<li
								<%= 'LoyaltyCard'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="LoyaltyCard">LoyaltyCard</g:link></li>
							<li
								<%= 'User'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="User">User</g:link></li>
							<li
								<%= 'Shopping'.equalsIgnoreCase(controllerName) ? ' class="active"' : '' %>><g:link
									controller="Shopping">ShoppingList</g:link></li>
					</ul>
					<div class="pull-right">
						<sec:loggedInUserInfo field="username" />
						<g:link controller="logout">
							<g:message code="Logout" />
						</g:link>
					</div>
					</sec:ifLoggedIn>
					<sec:ifNotLoggedIn>
						<ul class="nav pull-right">
							<li><a href="/Freebo/register">Sign Up</a></li>
							<li class="divider-vertical"></li>
							<li class="dropdown"><a class="dropdown-toggle" href="#"
								data-toggle="dropdown">Sign In <strong class="caret"></strong></a>
								<div class="dropdown-menu"
									style="padding: 15px; padding-bottom: 0px;">
									<!-- Login form here -->

									<form action="/Freebo/j_spring_security_check" id="loginForm"
										method="POST" accept-charset="UTF-8">
										<input id="user_username" style="margin-bottom: 15px;"
											type="text" name="user[username]" size="30" /> <input
											id="user_password" style="margin-bottom: 15px;"
											type="password" name="user[password]" size="30" /> <input
											id="user_remember_me"
											style="float: left; margin-right: 10px;" type="checkbox"
											name="user[remember_me]" value="1" /> <label
											class="string optional" for="user_remember_me">
											Remember me</label> <input class="btn btn-primary"
											style="clear: left; width: 100%; height: 32px; font-size: 13px;"
											type="submit" name="commit" value="Sign In" />
										<s2ui:submitButton elementId='loginButton' form='loginForm'
											messageCode='spring.security.ui.login.login' />
									</form>
								</div></li>
						</ul>
					</sec:ifNotLoggedIn>
				</div>
			</div>
		</div>
		<div></div>
	</nav>

	<div class="container-fluid">
		<g:layoutBody />

		<hr>

		<footer>
			<p>&copy; Freebo - Frequenty Buyer Online 2013</p>
		</footer>
	</div>

	<r:layoutResources />

</body>
</html>