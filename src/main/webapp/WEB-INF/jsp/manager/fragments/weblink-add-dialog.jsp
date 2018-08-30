 <div id="webLinkAddDialog" class="easyui-dialog" style="width:600px;height:150px;" closed="true"
    		data-options="title:'My Dialog',buttons:'#web-link-buttons',modal:true">
  <form id="webLinkAddForm" method="post">
      <input id="web-link-add-source" name="source" type="hidden" value=""></>
      <input id="web-link-add-assoc_qname" name="assoc_qname" type="hidden" value="openapps_org_content_1_0_web_links"></>
      <input id="web-link-add-entity_qname" name="entity_qname" type="hidden" value="openapps_org_content_1_0_web_link"></>

      <div class="form-row">
        <input id="web-link-add-url" name="url" class="easyui-textbox" required="true" label="URL:" style="width:100%;">
      </div>

      <div id="web-link-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="createWebLink()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#webLinkAddDialog').dialog('close')" style="width:90px">Cancel</a>
      </div>
    </form>
  </div>