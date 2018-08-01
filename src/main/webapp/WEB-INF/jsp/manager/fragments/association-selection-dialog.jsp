<div id="addAssociationsWindow" style="width:1000px;height:600px;padding:5px;">
    <div class="easyui-layout" style="width:100%;height:100%;">
        <div data-options="region:'east', collapsible: false, title: 'Selected Items To Add', split:true" title="East" style="width:40%;">
            <table id="selectedItems" style="width:100%;height=100%">
            </table>
            <div id="selectedItemsToolBar">
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="clearChecked($('#selectedItems'))">Clear Checked</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="removeChecked($('#selectedItems'))">Remove Checked</a>
                <a id="addSelectedSaveBtn" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-save" plain="true" onclick="saveAssociations()">Save</a>
            </div>
        </div>
        <form id="addAssocSearch"></form>
        <div data-options="region:'center',title:'Items'" style="width:60%">

                <table id="assocSelectionGrid" style="width:100%;height:100%">
                <div id="addAssocSearchToolBar">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="clearChecked($('#assocSelectionGrid'))">Clear Checked</a>
                    <a id="addSelectedBtn" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addToSelectedItems()">Add</a>
                    <a id="createEntityBtn" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="$('#assocCreateDlg').dialog('open').dialog('center').dialog('setTitle', 'Create ');">New</a>
                    <div style="float:right;">
                        <input id="query" style="width:200px;border:1px solid #ccc" onkeypress="handleKeyPress(event)">
                        <a href="#" class="easyui-linkbutton" plain="true" onclick="doSearch2();">Search</a>
                    </div>
                </div>
                </table>
        </div>
    </div>
</div>
<%@ include file="association-create-dialog.jsp" %>