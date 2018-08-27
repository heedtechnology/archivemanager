<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
	<head>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/material/easyui.css"/>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/icon.css"/>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/color.css"/>
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
		<link rel="stylesheet" type="text/css" href="/css/archivemanager.css"/>
		<link rel="stylesheet" type="text/css" href="/css/easyui/jquery-texteditor.css"/>
		<style>
			#cc .combo-panel{height:75px;}
		</style>
		
		<script src="/easyui/jquery.min.js"></script>
		<script type="text/javascript" src="/easyui/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="/easyui/easyloader.js"></script>
		<script type="text/javascript" src="https://www.jeasyui.com/easyui/datagrid-scrollview.js"></script>
		<script type="text/javascript" src="/js/archivemanager.js"></script>		
	</head>
	<body>
		<div class="loader"></div>	
		<div class="container">			
			<nav class="navbar navbar-default">
				<div class="container-fluid">
					<div class="navbar-header">
						<div class="navbar-brand" style="float:left;">
							<a href="/"><img style="width:200px;" src="/images/logo/ArchiveManager200.png" /></a>
						</div>
						<div style="float:right;">
							<div id="header-links">							
								<a style="float:right;width:50px;text-align:center;margin:5px;" th:href="@{/logout}"><i class="fas fa-sign-out-alt fa-2x" aria-hidden="true"></i>logout</a>
								<a style="float:right;width:50px;text-align:center;margin:5px;" href="/apps/user/profile"><i class="fas fa-address-card fa-2x" aria-hidden="true"></i>profile</a>
								<a style="float:right;width:47px;text-align:center;margin:5px;" href="/"><i class="fas fa-home fa-2x" aria-hidden="true"></i>home </a>
							</div>
						</div>					
					</div>
				</div>
			</nav>
			<%@ include file="menu.jsp" %>
			<div id="app_body" class="rounded-border" style="float:left;margin-right:5px;">
			