<div id="noteAddDialog" class="easyui-dialog" title="Subject" style="width:600px;height:600px;padding:10px"
	data-options="closed:true,modal:true">
     <form id="noteAddForm" class="form">
      <input id="note-add-source" name="source" type="hidden" value=""></>
       <input id="note-add-assoc_qname" name="assoc_qname" type="hidden" value="openapps_org_system_1_0_notes"></>
       <input id="note-add-entity_qname" name="entity_qname" type="hidden" value="openapps_org_system_1_0_note"></>

      <div id="divNoteType" style="margin-bottom: 2px" >
        <input id="note-add-type" name="type" label="Item Type:" value="null" style="width:50%">
        </div>
      <div>
      <input id="note-add-name" name="title" class="easyui-textbox" required="true" label="Title:" style="width:100%;">
      </div>

      <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Content:</label>
        <div id="note-add-content" name="content" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;"/>
        </div>
      <div id="note-dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="addNoteToCollection()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#noteAddDlg').dialog('close')" style="width:90px">Cancel</a>
      </div>
	</form>
</div>