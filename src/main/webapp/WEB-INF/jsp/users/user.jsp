<!DOCTYPE html>
<html lang="en">
	<head>
		<div th:replace="fragments/header :: css"/>		
		<div th:replace="fragments/header :: javascript"/>	
		<script type="text/javascript" src="/js/apps/user.js"></script>
	</head>
	<body>
		<div class="loader"></div>
		<div th:replace="fragments/header :: content"/>
		<div class="container" style="width:750px;">
			<a style="float:left;margin:5px;" href="/apps/user"><i class="fa fa-arrow-circle-o-left fa-2x" aria-hidden="true"></i></a>
			<h2 style="float:left;margin-top: 10px;">User</h2>
			<div class="rounded-border" style="width:100%;display:table;margin-bottom:10px;">				
				<form id="fm" method="post" style="margin:0;" th:object="${user}">
			       	<input name="id" th:value="${user.id}" type="hidden" />
			        <div style="width:50%;float:left;">
			        	<div style="margin:10px">
				        	<input name="email" class="easyui-textbox" th:value="${user.email}" label="Email:" style="width:100%"/>
				        </div>
				        <div style="margin:10px">
				        	<input name="password" class="easyui-passwordbox" prompt="Password" iconWidth="28" label="Password:" style="width:100%"/>
				        </div>
			        </div>
			        <div style="width:50%;float:left;">
			        	<div style="margin:10px">
				        	<input name="username" class="easyui-textbox" th:value="${user.username}" required="true" label="Username:" style="width:100%"/>
				        </div>
				        <div style="margin:10px">
				          	<label class="textbox-label textbox-label-before" for="roles">Role:</label>
						    <select id="roles" class="easyui-combobox" th:field="*{roles}">
						       <option th:each="role: ${roles}" th:value="${role.id}" th:text="${role.name}"></option>
						    </select>						    
						       
						</div>
						<div style="margin:10px">
							<label class="textbox-label textbox-label-before" for="enabled">Enabled:</label>
						    <input id="enabled" name="enabled" type="checkbox" class="easyui-radio" th:checked="${user.enabled}" />
						</div>
			        </div>
			    </form>
			    <div style="display:table;width:100%;margin:10px;">
					<a href="#" class="easyui-linkbutton" style="margin-right:5px;" onclick="doSaveUser();">Save</a>
					<a href="#" class="easyui-linkbutton" onclick="doCancel();">Cancel</a>
					<div id="user-msg"></div>
				</div>
		    </div>
		    <div id="dlg-buttons">
		        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="doAddAccount()" style="width:90px">Save</a>
		        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
		    </div>			
		</div>
	</body>
</html>