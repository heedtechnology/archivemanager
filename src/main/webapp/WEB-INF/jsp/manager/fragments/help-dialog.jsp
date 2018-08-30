 <div id="helpDialog" class="easyui-dialog" style="width:600px;height:300px;" closed="true"
    		data-options="title:'Help',buttons:'#dlg-buttons',modal:true">

      <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Help Question:</label>
        <div id="help-question" name="help_question" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">

       <img src="../../images/apps/help.jpg" alt="Smiley face" height="42" width="42">
        </div>
      <div id="help-dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="javascript:alert('Help')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#helpDialog').dialog('close')" style="width:90px">Cancel</a>
      </div>

  </div>