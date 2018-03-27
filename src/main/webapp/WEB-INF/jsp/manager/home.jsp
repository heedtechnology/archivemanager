<%@ include file="../fragments/apps_header.jsp" %>
<script type="text/javascript" src="/js/apps/easyui/jquery.texteditor.js"></script>
<script type="text/javascript" src="/js/apps/manager/home.js"></script>

<style>
	#cc .combo-panel{height:75px;}
</style>
	<table id="dg" title="Collection Manager" class="easyui-datagrid" toolbar="#toolbar" pagination="true" 
		rownumbers="true" fitColumns="true" singleSelect="true" pageSize="20" url="/service/search/entity.json">
        <thead>
            <tr>
            	<th field="id" width="5">ID</th>
            	<th field="name" width="50">Name</th>
                <th field="abstractNote" width="50">Description</th>
            </tr>
        </thead>
    </table>
    <div id="toolbar">
        <a id="add" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="add()">Add</a>
        <a id="edit" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="edit()">Edit</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="destroy()">Remove</a>
        <div style="float:right;">
        	<select id="qname" class="easyui-combobox" panelHeight="120px;" editable="false" style="width:150px;height:20px;">
		        <option value="openapps_org_repository_1_0_repository">Repository</option>
		        <option value="openapps_org_repository_1_0_collection">Collection</option>
		        <option value="openapps_org_classification_1_0_corporation">Corporate Entity</option>
		        <option value="openapps_org_classification_1_0_person">Personal Entity</option>
		        <option value="openapps_org_classification_1_0_subject">Subject</option>
		    </select>
	        <input id="query" style="width:200px;border:1px solid #ccc">
	    	<a href="#" class="easyui-linkbutton" plain="true" onclick="doSearch();">Search</a>
	    </div>
    </div>
    
    <div id="dlg" class="easyui-dialog" style="width:400px" closed="true" buttons="#dlg-buttons">
        <form id="fm" method="post" novalidate style="margin:0;padding:20px 50px">
            <div style="margin-bottom:20px;font-size:14px;border-bottom:1px solid #ccc">Collection Information</div>
            <div style="margin-bottom:10px">
                <input name="name" class="easyui-textbox" required="true" label="Name:" style="width:100%">
            </div>
            <div style="margin-bottom:10px">
                <input name="lastname" class="easyui-textbox" required="true" label="Description:" style="width:100%">
            </div>
            <div style="margin-bottom:10px">
			    <div id="scopeNoteEditor" class="easyui-texteditor" title="TextEditor" style="width:700px;height:300px;padding:20px">
			        <h3 style="text-align:center">TextEditor</h3>
			        <p style="text-align:center">TextEditor is a lightweight html5 editor based on EasyUI. It is completely customizable to fit your needs.</p>
			    </div>
            </div>
        </form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
    </div>

<%@ include file="../fragments/apps_footer.jsp" %>