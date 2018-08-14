<div id="collection-form" class="screen" title="collection-form">
    <div class="form-row" style="min-height:25px;">
        <div style="width:20%;float:left;margin-top:3px;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
            <label id="collection-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
        </div>
        <div style="float:right;">
            <a class="btn btn-primary" style="float:right;margin-right:5px;" href="javascript:void(0)" onclick="navigate('collection-import');">
        <i class="fa fa-upload fa-fw"></i>Import</a>
        </div>
    </div>
    <div class="form-row" style="height:50px;">
        <div style="width:38%;float:left;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Language:</label>
            <select id="collection-language" class="easyui-combobox" style="width:100%;float:left;">
                <option></option>
                <option value="en">English</option>
            </select>
        </div>
        <div style="width:38%;float:right;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Owner:</label>
            <input id="collection-owner" class="easyui-textbox" style="width:100%;float:left;" />
        </div>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Heading:</label>
        <div id="collection-name" class="easyui-texteditor" style="width:100%;height:100px;padding:5px;">
        </div>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Scope Note:</label>
        <div id="collection-scopeNote" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
        </div>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Biographical Note:</label>
        <div id="collection-bioNote" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
        </div>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Notes:</label>
        <div id="collection-notes" class="easyui-texteditor" style="width:100%;height:300px;padding:5px;">
        </div>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Subjects:</label>
                        <ul class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Subjects'">
                            <c:forEach items="${collection.subjects}" var="subject" varStatus="rstatus">
                                <li id="${subject.id}">${subject.name}</li>
                            </c:forEach>
                        </ul>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">Corporations:</label>

                        <ul class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No Corporations'">
                            <c:forEach items="${collection.corporations}" var="corporation" varStatus="rstatus">
                                                    <li id="${corporation.id}">${corporation.name}</li>
                            </c:forEach>
                        </ul>
    </div>
    <div class="form-row">
        <label class="textbox-label textbox-label-top" style="text-align: left;">People:</label>

                        <ul class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,nowrap:false,singleSelect:false,emptyMsg:'No People'">
                    <c:forEach items="${collection.people}" var="person" varStatus="rstatus">
                        <li id="${person.id}">${person.name}</li>
                    </c:forEach>
                        </ul>
    </div>
    <div class="form-row">
        <input id="collection-url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" />
    </div>
    <div class="form-row" style="margin-top:15px;">
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()" style="width:80px">Save</a>
    </div>
    <div class="form-row-col1">
        <div class="form-row">
            <input id="collection-code" class="easyui-textbox" label="Code:" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <input id="collection-accession-date" class="easyui-datebox" label="Accession Date:"
            data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <input id="collection-begin-date" class="easyui-datebox" label="Begin Date:"
            data-options="formatter:customDateFormatter,parser:customDateParser," labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <input id="collection-bulk-begin-date" class="easyui-datebox" label="Bulk Begin Date:"
            data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row" style="min-height:35px;">
            <label class="textbox-label textbox-label-top">Access:</label>
            <select id="collection-restrictions" name="collection-restrictions">
              <option value="open" selected >Open</option>
              <option value="partial">Partially Restricted</option>
              <option value="closed">Closed</option>
            </select>
        </div>
    </div>
    <div class="form-row-col2">
        <div class="form-row">
            <input id="collection-identifier" class="easyui-textbox" label="Identifier:" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <input id="collection-date-expression" class="easyui-datebox" label="Date Expression:"
            data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <input id="collection-end-date" class="easyui-datebox" label="End Date:"
            data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <input id="collection-bulk-end-date" class="easyui-datebox" label="Bulk End Date:"
            data-options="formatter:customDateFormatter,parser:customDateParser" labelPosition="top" style="width:100%;" />
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Internal:</label>
            <input id="collection-internal" type="checkbox" style="float:left;margin-top:10px" />
        </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Public:</label>
            <input id="collection-public" type="checkbox" style="float:left;margin-top:10px;" />
        </div>
    </div>
</div>