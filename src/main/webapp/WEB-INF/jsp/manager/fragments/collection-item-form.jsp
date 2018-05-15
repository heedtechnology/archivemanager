
<div id="collection-item-form" class="screen" title="collection-item-form">
	<div class="form-row" style="min-height:25px;">
		<div style="width:20%;float:left;margin-top:3px;">
   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
   			<label id="collection.id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
   		</div>
   		<div style="float:right;">
			<a class="btn btn-primary" style="float:right;margin-right:5px;" href="javascript:void(0)" onclick="navigate('collection-import')">
				<i class="fa fa-upload fa-fw"></i>Import</a>
		</div>
	</div>
   	<div class="form-row" style="height:50px;">
   		<div style="width:38%;float:left;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Language:</label>
          	<select id="collection.language" class="easyui-combobox" style="width:100%;float:left;">
          		<option></option>
          		<option value="en">English</option>
          	</select>
        </div>
        <div style="width:38%;float:right;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Owner:</label>
          	<input id="collection.owner" class="easyui-textbox" style="width:100%;float:left;" />
        </div>
   	</div>
   	<div class="form-row">
   		<label class="textbox-label textbox-label-top" style="text-align: left;">Heading:</label>
    	<div id="collection-item-name" class="easyui-texteditor" style="width:100%;height:100px;padding:5px;">

		</div>
	</div>
   	<div class="form-row">
   		<label class="textbox-label textbox-label-top" style="text-align: left;">Scope Note:</label>
    	<div id="collection-scopeNote" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">

		</div>
	</div>
	<div class="form-row">
   		<label class="textbox-label textbox-label-top" style="text-align: left;">Biographical Note:</label>
    	<div id="collection-bioNote" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">

		</div>
	</div>
	<div class="form-row">
		<input id="collection-comment" class="easyui-textbox" label="Comments:" labelPosition="top" multiline="true" style="width:100%;height:200px">
    </div>
    <div class="form-row">
   		<input id="collection-url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" />
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
   			<input id="collection-bulk-begin-date" class="easyui-textbox" label="Bulk Begin Date:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row">
   			<div style="width:60%;margin-right:25px;float:left;">
   				<input id="collection-extent-units" class="easyui-textbox" label="Extent Units:" labelPosition="top" style="width:100%;" />
   			</div>
   			<div style="width:30%;float:left;">
   				<input id="collection-extent-value" class="easyui-numberbox" label="Extent Value" labelPosition="top" style="width:100%;">
   			</div>
   		</div>
   		<div id="lastrow" class="form-row">

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
   			<input id="collection-bulk-end-date" class="easyui-textbox" label="Bulk End Date:" labelPosition="top" style="width:100%;" />
   		</div>
   		<div class="form-row" style="min-height:35px;">
   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Restrictions:</label>
   			<input id="collection-restrictions" type="checkbox" style="float:left;" />

   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-left:50px;margin-right:10px;">Internal:</label>
   			<input id="collection-internal" type="checkbox" style="float:left;" />
   		</div>
   		<div class="form-row">
   			<label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Public:</label>
   			<input id="collection-public" type="checkbox" style="float:left;" />
   		</div>
   		   		<div class="form-row">
         			<input id="collection-identifier" class="easyui-textbox" label="Identifier:" labelPosition="top" style="width:100%;" />
         		</div>
         		<div class="form-row">
         			<input id="collection-date-expression" class="easyui-textbox" label="Date Expression:" labelPosition="top" style="width:100%;" />
         		</div>
   	</div>
</div>