 <div id="dlg" class="easyui-dialog" style="width:600px;height:300px;" closed="true"
    		data-options="title:'My Dialog',buttons:'#dlg-buttons',modal:true">
  <form id="home-add-form" method="post">
  <input type="hidden" id="addqname" name="qname" value=""/>
    <div style="margin-bottom:10px">
      <input id="home-add-name" name="name" class="easyui-textbox" required="true" label="Name:" style="width:100%">
      </div>
      <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Description:</label>
        <div id="home-add-scopeNote" name="scope_note" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
        </div>
      <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save('home-add-form')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
      </div>
    </form>
  </div>