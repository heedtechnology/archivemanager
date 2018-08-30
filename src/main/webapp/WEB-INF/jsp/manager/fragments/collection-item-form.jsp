<div id="collection-item-form" class="screen" title="collection-item-form">
    <form id="collection-item-update-form" method="post" enctype="multipart/form-data">
        <div class="form-row" style="min-height:25px;">
            <div style="width:20%;float:left;margin-top:3px;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
                <label id="collection-item-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
                <input id="collection-item-update-form-id" type="hidden" name="id" value=${collection.id}>
            </div>
        </div>
        <div class="form-row" style="height:50px;">
            <div style="width:38%;float:left;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Language:</label>
                <select id="collection-item-language" class="easyui-combobox" style="width:50%;float:left;">
                    <option></option>
                    <option value="en">English</option>
                </select>
            </div>
            <!-- TODO - Implement Later
            <div style="width:38%;float:right;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Owner:</label>
                <input id="collection-owner" name="owner" class="easyui-textbox" style="width:100%;float:left;" />
            </div>
            -->
        </div>
        <div class="form-row">
            <input id="collection-item-name" name="name" class="easyui-textbox" label="Name:" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Description:</label>
            <div id="collection-item-description" name="description" class="easyui-texteditor" style="width:100%;height:100px;padding:5px;">
            </div>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Summary:</label>
            <div id="collection-item-summary" name="summary" class="easyui-texteditor" style="width:100%;height:100px;padding:5px;">
            </div>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Container:</label>
            <input id="collection-item-container" name="container" class="easyui-validatebox" data-options="validateOnBlur:true,validType:'isNumberAndLength'" style="width:25%;" />
        </div>
        <div class="form-row">
            <input id="item-collection-id" class="easyui-textbox" label="Collection ID:" labelPosition="top" style="width:100%;" data-options="editable:false" />
        </div>
        <div class="form-row">
            <input id="item-collection-name" class="easyui-textbox" label="Collection Name:" labelPosition="top" style="width:100%;" data-options="editable:false" />
        </div>
        <div class="form-row">
            <input id="item-collection-url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" data-options="editable:false" />
        </div>
        <div id="item-genre-div" class="form-row" style="display:none;">
            <div style="width:200px;float:left;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Genre:</label>
                <input id="item-genre" name="genre" class="easyui-combobox" data-options="valueField: 'value',textField: 'label'" style="width:150px;float:left;">
            </div>
        </div>
        <div id="item-form-div" class="form-row" style="display:none;">
            <div style="width:200px;float:left;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Form:</label>
                <input id="item-form" name="form" class="easyui-combobox" data-options="valueField: 'value',textField: 'label'" style="width:150px;float:left;">
            </div>
        </div>
        <div id="item-medium-div" class="form-row" style="display:none;">
            <div style="width:225px;float:left;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Medium:</label>
                <input id="item-medium" name="medium" class="easyui-combobox" data-options="valueField: 'value',textField: 'label'" style="width:150px;float:left;">
            </div>
        </div>
        <div class="form-row">
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="updateEntity('collection-item-update-form')">Save</a>
        </div>
    </form>
</div>