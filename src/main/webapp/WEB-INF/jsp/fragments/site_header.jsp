<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
	<head>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/material/easyui.css"/>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/icon.css"/>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/color.css"/>
		<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"/>
		<link rel="stylesheet" type="text/css" href="/css/archivemanager.css"/>
		<link rel="stylesheet" type="text/css" href="/css/easyui/jquery-texteditor.css"/>
		<style>
			#cc .combo-panel{height:75px;}
		</style>
		
		<script src="/easyui/jquery.min.js"></script>
		<script type="text/javascript" src="/easyui/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="/easyui/easyloader.js"></script>
		<script type="text/javascript" src="/easyui/plugins/jquery.texteditor.js"></script>
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
								<a style="float:right;width:50px;text-align:center;margin:5px;" th:href="@{/logout}"><i class="fa fa-sign-out fa-2x" aria-hidden="true"></i>logout</a>
								<a style="float:right;width:50px;text-align:center;margin:5px;" href="/apps/user/profile"><i class="fa fa-address-card-o fa-2x" aria-hidden="true"></i>profile</a>
								<a style="float:right;width:47px;text-align:center;margin:5px;" href="/"><i class="fa fa-home fa-2x" aria-hidden="true"></i>home </a>
							</div>
						</div>					
					</div>
				</div>
			</nav>
			<%@ include file="menu.jsp" %>
			<div id="app_body" class="rounded-border" style="float:left;margin-right:5px;">
			