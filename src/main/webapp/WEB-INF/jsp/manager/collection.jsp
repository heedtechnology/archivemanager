<%@ taglib uri="http://jsptagutils" prefix="am"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../fragments/site_header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/manager/collection.css">
		<link rel="stylesheet" type="text/css" href="/easyui/themes/material/easyui.css"/>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/icon.css"/>
		<link rel="stylesheet" type="text/css" href="/easyui/themes/color.css"/>
		<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"/>
		<link rel="stylesheet" type="text/css" href="/css/archivemanager.css"/>
    <link rel="stylesheet" type="text/css" href="/css/easyui/jquery-texteditor.css"/>


<script type="text/javascript" src="/js/manager/utilities.js"></script>
<script type="text/javascript" src="/easyui/plugins/jquery.texteditor.js"></script>
<script type="text/javascript" src="/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="/easyui/easyloader.js"></script>
<script type="text/javascript" src="https://www.jeasyui.com/easyui/datagrid-scrollview.js"></script>
<script type="text/javascript" src="/js/archivemanager.js"></script>
<script type="text/javascript" src="/js/manager/collection.js"></script>
<script type="text/javascript" src="/js/manager/easyui_overrides.js"></script>

<style>
	#cc .combo-panel{height:75px;}
</style>

<div id="mainLayout" class="easyui-layout"  data-options="fit:true">
    <div id="toolbar1">
		<a id="add" href="javascript:void(0)" class="icon-add" style="float:left;margin:0 5px;" onclick='showAddNodeDialog()'></a>
	    <a id="delete" href="javascript:void(0)" class="icon-remove" style="float:left;margin:0 5px;" onclick="destroyCollection()"></a>
	          <a id="back" href="javascript:void(0)" class="icon-back" style="float:left;margin:0 5px;" onclick="goHome()"></a>
	</div>
    <div id="collection-tree" title=" " style="width: 15%" data-options="region:'west',split:true,tools:'#toolbar1'">

    	<ul id="collectionTree" class="easyui-tree" style="width:100%;" data-options="animate:true,dnd:true,
    		onContextMenu:showContextMenu,onSelect:treeSelect,formatter:treeFormat,onLoadSuccess:selectRootNode,
    		url:'/service/archivemanager/collection/taxonomy.json?collection='+${collection.id},method:'get'">
	    </ul>
    </div>

    <div id="bullshit" class="easyui-tabs" title=" " style="width=60%" data-options="region:'center'">
    <div id="application-body" class="easyui-tabs" title=" " data-options="fit:true">
    	<%@ include file="fragments/repository-form.jsp" %>
    	<%@ include file="fragments/collection-form.jsp" %>
    	<%@ include file="fragments/collection-category-form.jsp" %>
		  <%@ include file="fragments/collection-item-form.jsp" %>
		  <%@ include file="fragments/collection-named-entity-form.jsp" %>
		  <%@ include file="fragments/collection-subject-form.jsp" %>
		  <%@ include file="fragments/collection-import.jsp" %>
    </div>
    </div>
    <div id="bullshit2" class="easyui-tabs" title=" " style="width: 25%" data-options="region:'east'">
    <div id="application-properties" class="easyui-tabs" title=" " data-options="fit:true">
    	<%@ include file="fragments/collection-associations.jsp" %>
    	<%@ include file="fragments/collection-import-properties.jsp" %>
    </div>
    </div>
    <div data-options="region:'south'" style="height:50px;"></div>
</div>

<%@ include file="fragments/association-selection-dialog.jsp" %>
<%@ include file="fragments/note-dialog.jsp" %>
<%@ include file="fragments/node-add-dialog.jsp" %>
<%@ include file="fragments/collection-context-menu.jsp" %>
<%@ include file="../fragments/site_footer.jsp" %>