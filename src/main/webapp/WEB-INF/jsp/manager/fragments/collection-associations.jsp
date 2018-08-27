<div id="collection-associations" title="collection-associations" class="easyui-accordion" style="width:100%;height:100px;">
    <c:if test="${am:hasProperty(collection,'corporations')}">
        <div id="collection-corporations" title="Corporations">
            <div id="collection-corporations-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_classification_1_0_corporation')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-corporations-toolbar'">
                <ul id="coll_assoc_dl_corp" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no corporations'">
                    <c:forEach items="${collection.corporations}" var="corporation" varStatus="rstatus">
                        <li id="${corporation.id}">${corporation.name}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
    <c:if test="${am:hasProperty(collection,'digitalObjects')}">
        <div id="collection-digital-objects" title="Digital Objects">
            <div id="collection-digital-objects-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_content_1_0_file')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-digital-objects-toolbar'">
                <ul id="coll_assoc_dl_digitalObjects" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no digital objects'">
                    <c:forEach items="${collection.digitalObjects}" var="digitalObject" varStatus="rstatus">
                        <li id="${digitalObject.id}">${digitalObject.title}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
    <c:if test="${am:hasProperty(collection,'people')}">
        <div id="collection-people" title="People">
            <div id="collection-people-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_classification_1_0_person')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-people-toolbar'">
                <ul id="coll_assoc_dl_people" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no permissions'">
                    <c:forEach items="${collection.people}" var="person" varStatus="rstatus">
                        <li id="${person.id}">${person.name}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
    <c:if test="${am:hasProperty(collection,'permissions')}">
        <div id="collection-permissions" title="Permissions">
            <div id="collection-permissions-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_system_1_0_permissions')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-permissions-toolbar'">
                <ul id="coll_assoc_dl_permissions" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no permissions'">
                    <c:forEach items="${collection.permissions}" var="permission" varStatus="rstatus">
                        <li id="${permission.id}">${permission.name}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
    <c:if test="${am:hasProperty(collection,'subjects')}">
        <div id="collection-subjects" title="Subjects">
            <div id="collection-subjects-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_classification_1_0_subject')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px%;padding:5px;" data-options="tools:'#collection-subjects-toolbar'">
                <ul id="coll_assoc_dl_subjects" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no subjects'">
                    <c:forEach items="${collection.subjects}" var="subject" varStatus="rstatus">
                        <li id="${subject.id}">${subject.name}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
    <c:if test="${am:hasProperty(collection,'weblinks')}">
        <div id="collection-web_links" title="Web Links" data-options="selected:true">
            <div id="collection-weblinks-toolbar">
                <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="loadAndOpen('openapps_org_content_1_0_web_links')"></a>
                <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
                <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
            </div>
            <div class="easyui-panel" title=" " style="width:100%;height:500px;padding:5px;" data-options="tools:'#collection-weblinks-toolbar'">
                <ul id="coll_assoc_dl_weblinks" class="easyui-datalist" style="width:100%;height:100%;" data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no web links'">
                    <c:forEach items="${collection.weblinks}" var="weblink" varStatus="rstatus">
                        <li id="${weblink.id}">${weblink.name}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
</div>