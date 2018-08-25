<div id="subject-dialog" class="easyui-dialog" title="Subject" style="width:400px;height:200px;padding:10px"
	data-options="closed:true,modal:true">
     <form id="subject-collection-form" class="form">
    	<div class="form-row" style="height:50px;">
    		<div style="width:20%;float:left;margin-top:3px;">
	   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
	   			<label id="subject.id" class="textbox-label textbox-label-top" style="text-align:left;float:left;"></label>
    		</div>
    		<div style="width:38%;float:left;">    				   			
	            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Language:</label>	            	
	          	<select id="collection.owner" class="easyui-combo" style="width:100%;float:left;"> 
	          		<option></option>
	          	</select>
	        </div>
	        <div style="width:38%;float:right;">    				   			
	            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Owner:</label>	            	
	          	<input id="collection.owner" class="easyui-textbox" style="width:100%;float:left;" /> 
	        </div>
	   	</div>
	</form>
</div>