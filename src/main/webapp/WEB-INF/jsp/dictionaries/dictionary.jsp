<%@ include file="../fragments/site_header.jsp" %>	
	<script type="text/javascript" src="/js/dictionaries/dictionary.js"></script>
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
	<div id="dlg" class="easyui-dialog" style="width:500px;height:380px;" closed="true" buttons="#dlg2-buttons">
	   	<form id="fm2" method="post" style="margin:0;padding:20px;" object="${transaction}">
	    	<div class="form-table">
		       	<div class="form-row">
		           	<div style="width:48%;margin-bottom:10px;float:left">		            	
		                <label class="textbox-label textbox-label-top">Type:</label>
				        <select name="type" class="easyui-combobox" editable="false">
				        	<option th:each="type : ${transactionTypes}" th:value="${type.name}">option</option>
						</select>
		            </div>
		            <div style="width:46%;margin-bottom:10px;float:right;">
			        	<input id="date" class="easyui-datebox" labelPosition="top" label="Date:" style="width:100%"/>				            
			        </div>
			    </div>
			    <div class="form-row">
			    	<div style="width:48%;margin-bottom:10px;float:left;">
		        		<input name="fee" class="easyui-numberbox" labelPosition="top" precision="2" prefix="$" value="0.00" required="true" label="Fee:" style="width:100%"/>
		        	</div>
		        	<div style="width:48%;margin-bottom:10px;float:right;">
		        		<input name="tax" class="easyui-numberbox" labelPosition="top" precision="2" prefix="$" value="0.00" label="Tax:" style="width:100%"/>
		        	</div>						    
				</div>							
				<div class="form-row">
					<input name="note" class="easyui-textbox" labelPosition="top" multiline="true" label="Note:" style="width:100%;"/>
				</div>
			</div>			        
		</form>			    	                
	</div>
	<div id="dlg2-buttons">
		<div id="account-msg" style="float:left;margin-left:25px;"></div>
	    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="doAddTransaction()" style="width:90px">Save</a>
	    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
	</div>
<%@ include file="../fragments/site_footer.jsp" %>