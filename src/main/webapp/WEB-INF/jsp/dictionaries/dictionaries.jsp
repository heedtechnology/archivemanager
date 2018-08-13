<%@ include file="../fragments/site_header.jsp" %>		
<script type="text/javascript" src="/js/dictionaries/dictionaries.js"></script>
	
	<table id="dg" title="Dictionaries" class="easyui-datagrid" toolbar="#toolbar" pagination="true" 
		singleSelect="true" fitColumns="true" pageSize="20" url="/dictionaries/search.json">
		<thead>
			<tr>
		       	<th field="id" width="50">ID</th>
		       	<th field="name" width="250">Name</th>
		       	<th field="description" width="500">Description</th>
		       	<th field="public" width="50">Public</th>	      
		    </tr>
		</thead>
	</table>
	<div id="toolbar">
		<a id="add" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="add()">Add</a>
		<a id="edit" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="edit()">Edit</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="destroy()">Remove</a>
		<div style="float:right;">
		   	<input id="query" style="border:1px solid #ccc"/>
		   	<a href="#" class="easyui-linkbutton" plain="true" onclick="doSearch();">Search</a>
		</div>
	</div>
	<div id="dlg" class="easyui-dialog" style="width:400px;height:340px;" closed="true" buttons="#dlg-buttons">
		<form id="fm" method="post" style="margin:0;padding:20px 40px" th:object="${newDictionary}" role="from">
	       	<input name="id" type="hidden" />
	        <div class="form-group" style="margin-bottom:10px">
		        <input name="name" class="easyui-textbox" required="true" label="Name:" style="width:100%"/>		            	
		    </div>
			<div class="form-group">
			   	<div id="error" style="color:red;">Problem with form data, please try again</div>
			</div>				    				                  
		</form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="save()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="doCancel()" style="width:90px">Cancel</a>
    </div>
<%@ include file="../fragments/site_footer.jsp" %>