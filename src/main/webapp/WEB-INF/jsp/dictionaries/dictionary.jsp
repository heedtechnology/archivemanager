<%@ include file="../fragments/site_header.jsp" %>	
<script type="text/javascript" src="/js/dictionaries/dictionary.js"></script>
	<style>
		.accordion-collapse {float: right;}
		#model-list{width:300px;float:left;}
		#model-detail{width:435px;float:right;}
		.textbox-label{font-weight:bold;}
	</style>	
	<div style="width:100%;height:65px;">
		<a style="float:left;margin:5px;" href="/apps/dictionary"><i class="fa fa-arrow-circle-o-left fa-2x" aria-hidden="true"></i></a>
		<h2 style="float:left;margin-top: 10px;">Dictionary</h2>
	</div>
	<div class="rounded-border" style="width:750px;;display:table;margin-bottom:10px;">				
		<form id="fm" method="post" style="margin:0;">
	       	<input name="id" th:value="${dictionary.id}" type="hidden" />
	        <div style="width:70%;float:left;">
	        	<div style="margin:10px">
		        	<input name="name" class="easyui-textbox" label="Name:" style="width:100%"/>
		        </div>
		        <div style="margin:10px">
		        	<input name="description" class="easyui-textbox" label="Description:" style="width:100%"/>
		        </div>
	        </div>
	        <div style="width:30%;float:left;">
	        	<div style="margin:10px">
		        	
		        </div>
		        <div style="margin:10px">
					<label class="textbox-label textbox-label-before" for="public">Public:</label>
				    <input id="public" name="public" type="checkbox" class="easyui-radio" />
				</div>
	        </div>
	    </form>
	    <div style="display:table;width:100%;margin:10px;">
			<a href="#" class="easyui-linkbutton" style="margin-right:5px;" onclick="doSaveUser();">Save</a>
			<a href="#" class="easyui-linkbutton" onclick="doCancel();">Cancel</a>
			<div id="user-msg"></div>
		</div>
	</div>
			
	<div class="rounded-border" style="width:750px;display:table;margin-bottom:10px;">
	   	<table id="dg" style="width:100%;height:650px;" title="Models" class="easyui-datagrid" toolbar="#toolbar" singleSelect="true" pageSize="20">
	        <thead>
	            <tr>
	            	<th field="name" width="200">Name</th>
	            	<th field="qname" width="300">QName</th>
	            	<th field="id" width="150" formatter="formatIcons"></th>		            	      
	            </tr>
	        </thead>
	    </table>
	    <div id="toolbar">
	        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="doAddModel()">Add</a>
	        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="doRemoveModel()">Remove</a>
	       	<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="doEditModel()">Edit</a>
		</div>		    	
	</div>
	<div id="model-window" class="easyui-dialog" style="width:750px;min-height:400px;" closed="true">
	   	<div id="model-list"></div>
	   	<div id="model-detail">
	   	
	   	</div>
	</div>
	<div id="field-form">
		<div style="margin:10px">
		   	<input id="type" name="type" class="easyui-textbox" label="Type:" style="width:400px;"/>
		</div>	
		<div style="margin:10px">
		   	<input id="name" name="name" class="easyui-textbox" label="Name:" style="width:400px;"/>
		</div>
		<div style="margin:10px">
		   	<input id="qname" name="qname" class="easyui-textbox" label="QName:" style="width:400px;"/>
		</div>
		<div style="margin:10px">
		   	<input id="description" name="description" class="easyui-textbox" label="Description:" style="width:400px;"/>
		</div>	
	</div>
<%@ include file="../fragments/site_footer.jsp" %>