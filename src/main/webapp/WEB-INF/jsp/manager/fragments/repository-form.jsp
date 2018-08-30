<div id="repository-form" class="screen" title="repository-form">
<form id="repository-update-form" method="post" enctype="multipart/form-data">
    <div class="form-row" style="min-height:25px;">
        <div style="width:20%;float:left;margin-top:3px;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;">ID:</label>
            <label id="repository-id" class="textbox-label textbox-label-top" style="text-align:left;float:left;">${collection.id}</label>
            <input type="hidden" name="id" value=${collection.id}>
        </div>
    </div>
    <div class="form-row" style="height:50px;">
        <div style="width:38%;float:left;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Language:</label>
            <select id="repository-language" class="easyui-combobox" style="width:100%;float:left;">
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
    <div class="form-row" style="height:50px;">
        <div style="width:100%;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Name:</label>
            <input id="repository-name" name="name" class="easyui-textbox" style="width:100%;float:left;" />
        </div>
    </div>
    <div class="form-row" style="height:50px;">
        <div style="width:38%;">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:5px;margin-top:2px;">Short Name:</label>
            <input id="repository-short-name" name="short_name" class="easyui-textbox" style="width:100%;float:left;" />
        </div>
    </div>
    <div class="form-row">
        <input id="repository-url" name="url" class="easyui-textbox" label="URL:" labelPosition="top" style="width:100%;" />
    </div>
<div>
    <div class="form-row-col1">
            <div class="form-row">
                <input id="repository-agency-code" name="agency_code" class="easyui-textbox" label="Agency Code:" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="repository-branding" name="branding" class="easyui-textbox" label="Branding:" labelPosition="top" style="width:100%;" />
            </div>
            <div class="form-row">
                <input id="repository-nces" name="nces" class="easyui-textbox" label="NCES:" labelPosition="top" style="width:100%;" />
            </div>
        <div class="form-row">
            <label class="textbox-label textbox-label-top" style="text-align:left;float:left;margin-right:10px;">Public:</label>
            <input id="collection-public" name="isPublic" type="checkbox" style="float:left;margin-top:10px;" />
        </div>
                      <div class="form-row">
                          <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'"
                                  onclick="updateEntity('repository-update-form')">Save</a>
                      </div>
    </div>

    <div class="form-row-col2">
                <div class="form-row">
                    <input id="repository-country-code" name="country_code" class="easyui-textbox" label="Country Code:" labelPosition="top" style="width:100%;" />
                </div>
                <div class="form-row">
                    <input id="repository-institution" name="institution" class="easyui-textbox" label="Institution:" labelPosition="top" style="width:100%;" />
                </div>
          </div>
          </div>
</form>
</div>