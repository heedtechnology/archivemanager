<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../fragments/apps_header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/apps/easyui/jquery-texteditor.css">
<link rel="stylesheet" type="text/css" href="/css/apps/manager/collection.css">

<script type="text/javascript" src="/js/apps/manager/collection.js"></script>
<script type="text/javascript" src="/js/apps/easyui/jquery.texteditor.js"></script>
<style>
	#cc .combo-panel{height:75px;}
</style>

<div id="mainLayout" class="easyui-layout" style="width:100%;height:350px;">        
    <div id="toolbar1">
		<a id="add" href="javascript:void(0)" class="icon-add" style="float:left;margin:0 5px;" onclick="newCollection()"></a>
	    <a id="delete" href="javascript:void(0)" class="icon-remove" style="float:left;margin:0 5px;" onclick="destroyCollection()"></a>      
	</div>
    <div id="collection-tree" title=" " style="width:25%;text-align:left;" data-options="region:'west',split:true,tools:'#toolbar1'">
    	
    	<ul id="collectionTree" class="easyui-tree" style="width:100%;" data-options="animate:true,dnd:true,
    		onSelect:treeSelect,formatter:treeFormat,
    		url:'/service/archivemanager/collection/taxonomy.json?collection='+${collection.id},method:'get'">	    	
	    </ul>
    </div>
        
    <div id="application-body" class="easyui-tabs" title=" " data-options="region:'center'">    	
    	<%@ include file="fragments/collection-form.jsp" %> 
    	<%@ include file="fragments/collection-category-form.jsp" %>
		<%@ include file="fragments/collection-item-form.jsp" %>
		<%@ include file="fragments/collection-import.jsp" %>
    </div> 
       
    <div id="application-properties" class="easyui-tabs" title=" " style="width:25%;" data-options="region:'east',split:true">
    	<%@ include file="fragments/collection-associations.jsp" %>
    	<%@ include file="fragments/collection-import-properties.jsp" %>
    </div>
    <div data-options="region:'south'" style="height:50px;"></div>
</div>

<%@ include file="fragments/association-selection-dialog.jsp" %>
<%@ include file="fragments/note-dialog.jsp" %> 

<%@ include file="../fragments/apps_footer.jsp" %>