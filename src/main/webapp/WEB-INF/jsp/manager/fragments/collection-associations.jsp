<div id="collection-associations" title="collection-associations" class="easyui-accordion" style="width:100%;height:100%;">
	<div id="collection-corporations" title="Corporations">
   		<div id="collection-corporations-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-corporations-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no corporations'">
		       	<c:forEach items="${corporations.corporations}" var="corporation" varStatus="rstatus">
		        	<li id="${corporation.id}">${corporation.name}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>
    <div id="collection-digital-objects" title="Digital Objects">
   		<div id="collection-digital-objects-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-digital-objects-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no digital objects'">
		       	<c:forEach items="${collection.digitalObjects}" var="digitalObject" varStatus="rstatus">
		        	<li id="${digitalObject.id}">${digitalObject.type}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>	
    <div id="collection-notes" title="Notes">
   		<div id="collection-notes-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-notes-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no permissions'">
		       	<c:forEach items="${collection.notes}" var="note" varStatus="rstatus">
		        	<li id="${note.id}">${note.type}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>
    <div id="collection-people" title="People">
   		<div id="collection-people-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-people-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no permissions'">
		       	<c:forEach items="${collection.people}" var="person" varStatus="rstatus">
		        	<li id="${person.id}">${person.name}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>
    <div id="collection-permissions" title="Permissions">
   		<div id="collection-permissions-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-permissions-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no permissions'">
		       	<c:forEach items="${collection.permissions}" var="permission" varStatus="rstatus">
		        	<li id="${permission.id}">${permission.name}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>
    <div id="collection-subjects" title="Subjects">
   		<div id="collection-subjects-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-subjects-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no subjects'">
		       	<c:forEach items="${collection.subjects}" var="subject" varStatus="rstatus">
		        	<li id="${subject.id}">${subject.name}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>    
    <div id="collection-web_links" title="Web Links" data-options="selected:true">
   		<div id="collection-weblinks-toolbar">
	        <a href="javascript:void(0)" class="icon-add" style="margin:0 5px;" onclick="javascript:alert('add')"></a>
	        <a href="javascript:void(0)" class="icon-remove" style="margin:0 5px;" onclick="javascript:alert('edit')"></a>
	        <a href="javascript:void(0)" class="icon-help" style="float:right;margin:0 5px;" onclick="javascript:alert('help')"></a>
	    </div>
    	<div class="easyui-panel" title=" " style="width:100%;height:100%;padding:5px;"
            data-options="tools:'#collection-weblinks-toolbar'">
	       	<ul class="easyui-datalist" style="width:100%;height:100%;" 
	       		data-options="showHeader:false,lines:true,checkbox:true,nowrap:false,singleSelect:false,emptyMsg:'no web links'">
		       	<c:forEach items="${collection.weblinks}" var="weblink" varStatus="rstatus">
		        	<li id="${weblink.id}">${weblink.name}</li>
		        </c:forEach>
	    	</ul>
    	</div>
    </div>
</div>