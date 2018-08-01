 <div id="assocCreateDlg" class="easyui-dialog" style="width:600px;height:300px;" closed="true"
    		data-options="title:'My Dialog',buttons:'#assocCreateDlgButtons',modal:true">
  <form id="assocCreateDlgForm" method="post">

       <input id="entity_qname" name="entity_qname" type="hidden" value=""></>
      <div>
      <input id="assocCreateDlgName" name="name" class="easyui-textbox" required="true" label="Name:" style="width:100%;">
      </div>

      <div id="assocCreateDlgButtons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="addNewToSelectedItems()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#assocCreateDlg').dialog('close')" style="width:90px">Cancel</a>
      </div>
    </form>
  </div>