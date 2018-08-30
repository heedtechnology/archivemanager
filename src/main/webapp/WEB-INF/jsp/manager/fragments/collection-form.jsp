<div id="collection-form" title="collection-form">
    <form id="collection-update-form" method="post" enctype="multipart/form-data">
        <div class="form-row" style="min-height:25px;">
            <div style="width:20%;float:left;margin-top:3px;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
                <label id="collection-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
                <input id="collection-update-form-id" type="hidden" name="id" value=${collection.id}>
            </div>
        </div>
        <div class="form-row" style="height:50px;">
            <div style="width:38%;float:left;">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Language:</label>
                <select id="collection-language" name="language" class="easyui-combobox" style="width:100%;float:left;">
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
            <label class="textbox-label textbox-label-top" style="text-align: left;">Heading:</label>
            <div id="collection-name" name="name" class="easyui-texteditor" data-options="fit:true">
            </div>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Scope Note:</label>
            <div id="collection-scopeNote" name="scope_note" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
            </div>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Biographical Note:</label>
            <div id="collection-bioNote" name="bio_note" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
            </div>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Notes:</label>
                <div class="form-row">
                    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="showAddNoteDialog()">Add</a>
                    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="removeAssociation('collection-form-notes')">Remove</a>
                </div>
                <c:if test="${am:hasProperty(collection,'notes')}">
                    <ul class="easyui-datalist" id="collection-form-notes" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Notes'">
                    </ul>
                </c:if>
        </div>
<!--        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Subjects:</label>
            <c:if test="${am:hasProperty(collection,'subjects')}">
                <ul class="easyui-datalist" id="collection-form-subjects" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Subjects'">
                </ul>
            </c:if>
        </div>
-->
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Subjects:</label>
            <c:if test="${am:hasProperty(collection,'subjects')}">
                <ul class="easyui-datalist" id="collection-form-subjects" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Subjects'">
                </ul>
            </c:if>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">Corporations:</label>
            <c:if test="${am:hasProperty(collection,'corporations')}">
                <ul class="easyui-datalist" id="collection-form-corporations" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Corporations'">
                </ul>
            </c:if>
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align: left;">People:</label>

                <ul class="easyui-datalist" id="collection-form-people" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No People'">
                </ul>

        </div>
        <div class="form-row">
            <input id="collection-url" name="url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="updateEntity('collection-update-form')">Save</a>
        </div>
        <div class="form-row-col1">
            <div class="form-row">
                <input id="collection-code" name="code" class="easyui-textbox" label="Code:" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-date-expression" name="date_expression" class="easyui-datebox" label="Date Expression:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-begin-date" name="begin" class="easyui-datebox" label="Begin Date:" data-options="formatter:customDateFormatter,parser:customDateParser," labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-bulk-begin-date" name="bulk_begin" class="easyui-datebox" label="Bulk Begin Date:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row" style="min-height:35px;">
                <label class="textbox-label textbox-label-top">Access:</label>
                <select id="collection-restrictions" name="restrictions">
                    <option value="open" selected>Open</option>
                    <option value="partial">Partially Restricted</option>
                    <option value="closed">Closed</option>
                </select>
            </div>
        </div>
        <div class="form-row-col2">
            <div class="form-row">
                <input id="collection-identifier" name="identifier" class="easyui-textbox" label="Identifier:" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row" style="height: 60px"></div>
            <div class="form-row">
                <input id="collection-end-date" name="end" class="easyui-datebox" label="End Date:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="collection-bulk-end-date" name="bulk_end" class="easyui-datebox" label="Bulk End Date:" data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Internal:</label>
                <input id="collection-internal" name="internal" type="checkbox" style="float:left;margin-top:10px" />
            </div>
            <div class="form-row">
                <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Public:</label>
                <input id="collection-public" name="isPublic" type="checkbox" style="float:left;margin-top:10px;" />
            </div>
        </div>
    </form>
</div>