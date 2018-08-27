<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="https://www.jeasyui.com/easyui/themes/material/easyui.css">
		<link rel="stylesheet" type="text/css" href="https://www.jeasyui.com/easyui/themes/icon.css">
		<link rel="stylesheet" type="text/css" href="https://www.jeasyui.com/easyui/themes/color.css">
		<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
		<link rel="stylesheet" type="text/css" href="/css/apps/archivemanager-apps.css" />
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
		<script type="text/javascript" src="https://www.jeasyui.com/easyui/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="https://www.jeasyui.com/easyui/easyloader.js"></script>
		<script type="text/javascript" src="https://www.jeasyui.com/easyui/datagrid-scrollview.js"></script>
		<script type="text/javascript" src="/js/utilities.js"></script>
		<script src="/js/apps/archivemanager-apps.js"></script>
	</head>
	<body>
		<div class="loader"></div>
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<div class="navbar-brand" style="float:left;">
						<a href="/"><img style="width:200px;" src="/images/logo/ArchiveManager200.png" /></a>
					</div>
					<div style="float:right;">
						<div id="header-links">							
							<a style="float:right;width:50px;text-align:center;margin:5px;" th:href="@{/logout}"><i class="fa fa-sign-out fa-2x" aria-hidden="true"></i>logout</a>
							<a style="float:right;width:50px;text-align:center;margin:5px;" href="/apps/user/profile"><i class="fa fa-address-card-o fa-2x" aria-hidden="true"></i>profile</a>
							<a style="float:right;width:47px;text-align:center;margin:5px;" href="/"><i class="fa fa-home fa-2x" aria-hidden="true"></i>home </a>
						</div>
					</div>					
				</div>
			</div>
		</nav>
		<div class="container">
	