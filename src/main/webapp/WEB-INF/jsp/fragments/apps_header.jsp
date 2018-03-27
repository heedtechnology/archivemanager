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
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<script type="text/javascript" src="https://www.jeasyui.com/easyui/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="https://www.jeasyui.com/easyui/easyloader.js"></script>
		<script src="/js/apps/archivemanager-apps.js"></script>
	</head>
	<body>
		<div class="loader"></div>
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<div class="navbar-brand" style="float:left;"><a href="/">Archive Manager</a></div>
					
				</div>
			</div>
		</nav>
		<div class="container">
	