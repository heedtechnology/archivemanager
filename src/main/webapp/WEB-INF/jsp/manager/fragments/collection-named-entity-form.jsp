<div id="collection-named-entity-form" class="screen" title="collection-named-entity-form">
    <form id="collection-named-entity-update-form" method="post" enctype="multipart/form-data">
        <div class="form-row" style="min-height:25px;">
            <div style="width:20%;float:left;margin-top:3px;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
                <label id="collection-named-entity-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
                <input id="collection-named-entity-update-form-id" type="hidden" name="id" value=${collection.id}>
            </div>
        </div>
        <div class="form-row">
            <input id="named-entity-name" name="name" class="easyui-textbox" label="Name:" labelPosition="top" style="width:100%;" />
        </div>

        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Description:</label>
            <div id="named-entity-description" name="description" class="easyui-texteditor" style="width:100%;height:150px;padding:5px;">
            </div>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Note:</label>
            <div id="named-entity-note" name="note" class="easyui-texteditor" style="width:100%;height:150px;padding:5px;">
            </div>
        </div>

        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Role:</label>
            <div id="named-entity-role" name="role" class="easyui-texteditor" style="width:100%;height:150px;padding:5px;">
            </div>
        </div>
        <div class="form-row">
            <input id="named-entity-source" name="source" class="easyui-textbox" label="Source:" labelPosition="top" style="width:100%;" />
        </div>
                <div class="form-row">
                    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="updateEntity('collection-named-entity-update-form')">Save</a>
                </div>
    </form>
</div>