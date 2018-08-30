<div id="collection-associations" title="collection-associations" class="easyui-accordion" data-options="fit:true" style="width:100%;height:100px;">
        <div id="collection-corporations" title="Corporations">
            <div id="collection-corporations-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_classification_1_0_corporation')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="removeAssociation('coll_assoc_dl_corp')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="displayHelp()"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-corporations-toolbar'">
                <ul id="coll_assoc_dl_corp" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no corporations'">
                </ul>
            </div>
        </div>
        <div id="collection-people" title="People">
            <div id="collection-people-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_classification_1_0_person')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="removeAssociation('coll_assoc_dl_people')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="displayHelp()"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-people-toolbar'">
                <ul id="coll_assoc_dl_people" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no people'">
                </ul>
            </div>
        </div>
        <div id="collection-permissions" title="Permissions">
            <div id="collection-permissions-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_system_1_0_permissions')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="removeAssociation('coll_assoc_dl_permissions')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="displayHelp()"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-permissions-toolbar'">
                <ul id="coll_assoc_dl_permissions" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no permissions'">
                </ul>
            </div>
        </div>
        <div id="collection-subjects" title="Subjects">
            <div id="collection-subjects-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_classification_1_0_subject')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="removeAssociation('coll_assoc_dl_subjects')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="displayHelp()"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-subjects-toolbar'">
                <ul id="coll_assoc_dl_subjects" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no subjects'">
                </ul>
            </div>
        </div>
        <div id="collection-web_links" title="Web Links" data-options="selected:true">
            <div id="collection-weblinks-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="showCreateWebLinkDialog()"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="removeAssociation('coll_assoc_dl_weblinks')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="displayHelp()"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-weblinks-toolbar'">
                <ul id="coll_assoc_dl_weblinks" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no web links'">
                </ul>
            </div>
        </div>
</div>