<%@ include file="../fragments/site_header.jsp" %>	
	<script type="text/javascript" src="/js/users/users.js"></script>
	<table id="dg" title="User Manager" class="easyui-datagrid" toolbar="#toolbar" pagination="true" 
				rownumbers="true" fitColumns="true" singleSelect="true" pageSize="20" url="/users/search.json">
		        <thead>
		            <tr>
		            	<th field="username" width="150">Username</th>
		            	<th field="email" width="200">Email</th>
		            	<th field="roles" width="100" formatter="formatRoles">Role</th>
		            	<th field="enabled" width="70">Enabled</th>	      
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
		        <form id="fm" method="post" style="margin:0;padding:20px 40px" th:object="${newUser}" role="from">
		        	<input name="id" type="hidden" />
		            <div class="form-group" style="margin-bottom:10px">
		                <input name="name" class="easyui-textbox" required="true" label="Name:" style="width:100%"/>		            	
		            </div>
		            <div class="form-group" style="margin-bottom:10px">
		                <input name="username" class="easyui-textbox" required="true" label="Username:" style="width:100%"/>
		            </div>
		            <div class="form-group" style="margin-bottom:10px">
		                <input name="email" class="easyui-textbox" required="true" label="Email:" style="width:100%"/>
		            </div>
		            <div class="form-group" style="margin-bottom:10px">
		                <input name="password" class="easyui-passwordbox" prompt="Password" iconWidth="28" required="true" label="Password:" style="width:100%"/>
		            </div>
		            <div class="form-group" style="margin-bottom:10px">
		            	<label class="textbox-label textbox-label-before" for="roles">Role : </label>
				        <select id="roles" class="easyui-combobox" required="true" th:field="*{roles}">
					       <option th:each="role: ${roles}" th:value="${role.id}" th:text="${role.name}"></option>
					    </select>
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