<div id="collection-category-form" class="screen" title="collection-category-form">
    <form id="collection-category-update-form" method="post" enctype="multipart/form-data">
        <div class="form-row" style="min-height:25px;">
            <div style="width:20%;float:left;margin-top:3px;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
                <label id="collection-category-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
                <input id="collection-category-update-form-id" type="hidden" name="id" value=${collection.id}>
            </div>
        </div>
        <div class="form-row">
            <input id="collection-category-name" name="name" class="easyui-textbox" label="Name:" labelPosition="top" style="width:100%;" />
        </div>

        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Description:</label>
            <div id="collection-category-description" name="description" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
            </div>
        </div>
                <div class="form-row">
                    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="updateEntity('collection-category-update-form')">Save</a>
                </div>
    </form>
</div>