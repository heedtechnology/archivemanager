<%@ include file="../fragments/site_header.jsp" %>
<script type="text/javascript" src="/js/search/search.js"></script>

<table id="dg" class="easyui-datagrid" toolbar="#toolbar" pagination="true"
   rownumbers="true" fitColumns="true" singleSelect="true" pageSize="20">
   <thead>
      <tr>
         <th field="id" width="8">ID</th>
         <th field="name" width="50">Name</th>
         <th field="abstractNote" width="100">Description</th>
      </tr>
   </thead>
</table>
<div id="toolbar">
   <div style="float:right;">
      <input id="query" style="width:200px;border:1px solid #ccc" onkeypress="handleKeyPress(event)">
      <a href="#" class="easyui-linkbutton" plain="true" onclick="doSearch();">Search</a>
   </div>
</div>
<%@ include file="../fragments/site_footer.jsp" %>