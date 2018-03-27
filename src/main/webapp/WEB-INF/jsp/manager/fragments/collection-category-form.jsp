	
<div id="collection-category-form" class="screen" title="collection-category-form">
	<div class="form-row" style="min-height:25px;">
		<div style="width:20%;float:left;margin-top:3px;">
   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
   			<label id="collection-category-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
   		</div>   		
	</div>	
   	<div class="form-row">
   		<label class="textbox-label textbox-label-top" style="text-align: left;">Heading:</label>
    	<div id="collection-category-name" class="easyui-texteditor" style="width:100%;height:100px;padding:5px;">
		    
		</div>
	</div>
   	<div class="form-row">
   		<label class="textbox-label textbox-label-top" style="text-align: left;">Description:</label>
    	<div id="collection-category-description" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
			    
		</div>
	</div>
	<div class="form-row">
   		<label class="textbox-label textbox-label-top" style="text-align: left;">Biographical Note:</label>
    	<div id="collection-category-summary" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
			    
		</div>
	</div>
	<div class="form-row">
		<input id="collection-category-comment" class="easyui-textbox" label="Comments:" labelPosition="top" multiline="true" style="width:100%;height:200px">
    </div>
    <div class="form-row">
   		<input id="collection-category-url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" />
   	</div>
   	<div class="form-row">
   		<input id="collection-category-container" class="easyui-textbox" label="Container:" labelPosition="top" style="width:100%;" />
   	</div>
   	<div class="form-row-col1">
   		<div class="form-row">
   			<input id="collection-code" class="easyui-textbox" label="Code:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<input id="collection-accession-date" class="easyui-textbox" label="Accession Date:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<input id="collection-begin-date" class="easyui-textbox" label="Begin Date:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<div style="width:60%;float:left;">
   				<input id="collection-extent-units" class="easyui-textbox" label="Extent Units:" labelPosition="top" style="width:100%;" />
   			</div>
   			<div style="width:30%;float:right;">
   				<input id="collection-extent-value" class="easyui-numberbox" label="Extent Value:" labelPosition="top" style="width:100%;">
   			</div>
   		</div>
   		<div class="form-row" style="margin-top:15px;">
   			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()" style="width:80px">Save</a>
   		</div>		
   	</div>
   	<div class="form-row-col2">
   		<div class="form-row">
   			<input id="collection-identifier" class="easyui-textbox" label="Identifier:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<input id="collection-date-expression" class="easyui-textbox" label="Date Expression:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<input id="collection-end-date" class="easyui-textbox" label="End Date:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<div style="width:40%;float:left;">
   				<input id="collection-size" class="easyui-textbox" label="Size:" labelPosition="top" style="width:100%;" />
   			</div>
   			<div style="width:40%;float:right;">
   				<input id="collection-order" class="easyui-numberbox" label="Order:" labelPosition="top" style="width:100%;">
   			</div>
   		</div>
   		<div class="form-row" style="min-height:35px;">
   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Restrictions:</label>
   			<input id="collection-restrictions" type="checkbox" style="float:left;" />
   			
   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-left:50px;margin-right:10px;">Internal:</label>
   			<input id="collection-internal" type="checkbox" style="float:left;" />
   		</div>   		
   	</div>
</div>