<%@ include file="../fragments/site_header.jsp" %>
<script type="text/javascript" src="/js/manager/home.js"></script>
<script type="text/javascript" src="/js/manager/utilities.js"></script>
<script type="text/javascript" src="/easyui/plugins/jquery.texteditor.js"></script>

<input type="hidden" id="qname" value="openapps_org_repository_1_0_repository"/>
<div id="tt"></div>
<style>
   #cc .combo-panel{height:75px;}
   .tabs{padding-left:0px;}
</style>
<table id="dg" title="Collection Manager" class="easyui-datagrid" toolbar="#toolbar" pagination="true"
   rownumbers="true" fitColumns="true" singleSelect="true" pageSize="20" url="/service/search/entity.json">
   <thead>
      <tr>
         <th field="id" width="8">ID</th>
         <th field="name" width="50">Name</th>
         <th field="abstractNote" width="100">Description</th>
      </tr>
   </thead>
</table>
<div id="toolbar">
   <a id="add" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="add()">Add</a>
   <a id="edit" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="edit()">Edit</a>
   <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="destroy()">Remove</a>
   <div style="float:right;">
      <input id="query" style="width:200px;border:1px solid #ccc" onkeypress="handleKeyPressCollectionSearch(event)">
      <a href="#" class="easyui-linkbutton" plain="true" onclick="doSearch();">Search</a>
   </div>
</div>
<%@ include file="./fragments/home-add-dialog.jsp" %>
<%@ include file="../fragments/site_footer.jsp" %>