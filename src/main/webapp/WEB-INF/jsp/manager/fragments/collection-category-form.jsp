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
  <%--
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Biographical Note:</label>
            <div id="collection-category-summary" name="bio_note" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
            </div>
        </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Notes:</label>
                                <ul class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Notes'">
                                    <c:forEach items="${collection.notes}" var="note" varStatus="rstatus">
                                        <li id="${note.id}">${subject.name}</li>
                                    </c:forEach>
                                </ul>
    </div>
        <div class="form-row">
            <input id="collection-category-url" name="url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Container:</label>
            <input id="collection-category-container" class="easyui-validatebox" data-options="validateOnBlur:true,validType:'isNumberAndLength'" style="width:10%;" />
        </div>


        <div class="form-row-col1">
            <div class="form-row">
                <input id="collection-category-code" class="easyui-textbox" label="Code:" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-category-accession-date" class="easyui-datebox" label="Accession Date:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-category-begin-date" class="easyui-textbox" label="Begin Date:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <div style="width:40%;">
                    <input id="collection-category-order" class="easyui-numberbox" label="Order:" labelPosition="top" style="width:100%;">
                </div>
            </div>
        </div>
        <div class="form-row-col2">
            <div class="form-row">
                <input id="collection-category-identifier" class="easyui-textbox" label="Identifier:" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-category-date-expression" class="easyui-textbox" label="Date Expression:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-category-end-date" class="easyui-textbox" label="End Date:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row" style="min-height:35px;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Internal:</label>
                <input id="collection-category-internal" type="checkbox" style="float:left;margin-top:10px;" />
            </div>
        </div>
--%>
    </form>
</div>