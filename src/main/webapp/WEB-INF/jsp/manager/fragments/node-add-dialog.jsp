 <div id="nodeAddDlg" class="easyui-dialog" style="width:600px;height:300px;" closed="true"
    		data-options="title:'My Dialog',buttons:'#dlg-buttons',modal:true">
  <form id="nodeAddForm" method="post">
      <input id="node-add-source" name="source" type="hidden" value=""></>
       <input id="node-add-assoc_qname" name="assoc_qname" type="hidden" value=""></>
       <input id="node-add-entity_qname" name="entity_qname" type="hidden" value=""></>
        <input id="node-add-description" name="description" type="hidden" value=""></>
      <div id="divContentType" style="margin-bottom: 2px" >
        <input id="node-add-content-type" name="contentType" label="Item Type:" value="" style="width:50%">
        </div>
      <div>
      <input id="node-add-name" name="name" class="easyui-textbox" required="true" label="Name:" style="width:100%;">
      </div>

      <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Description:</label>
        <div id="node-add-abstract-note" name="abstractNote" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;"/>
        </div>
      <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="addToCollection()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#nodeAddDlg').dialog('close')" style="width:90px">Cancel</a>
      </div>
    </form>
  </div>