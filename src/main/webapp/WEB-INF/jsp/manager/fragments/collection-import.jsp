
<div id="collection-import" class="screen" title="collection-import">	
	<div class="form-row" style="min-height:50px;margin-top:20px;">
		<form id="collection-upload-form" action="/service/entity/import/upload.json" method="post" enctype="multipart/form-data">
			<input type="hidden" name="qname" value="openapps_org_repository_1_0_collection" />
			<div class="form-group" style="margin-right:5px;">
				<select id="processor-select" name="mode" class="easyui-combobox">
					<c:forEach items="${processors}" var="processor" varStatus="rstatus">
						<option value="${processor.id}">${processor.name}</option>			
					</c:forEach>
				</select>
			</div>
			
			<div class="form-group" style="margin-right:5px;">
				<input name="file" class="easyui-filebox" style="width:320px" 
					data-options="buttonText:'file', plain:true"/>
			</div>
			<div class="form-group">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#collection-upload-form').form('submit');" style="width:80px">submit</a>
			</div>
			<div class="form-group">
				<img id="spinner" class="spinner" src="/images/giphy-downsized.gif" />
			</div>
		</form>
	</div>
	<div class="form-row" style="min-height:25px;">
		<ul id="tree1" class="easyui-tree" style="width:100%;" data-options="animate:true,dnd:false,
    		formatter:treeFormat,url:'/service/entity/import/fetch.json',method:'get'">	    	
	    </ul>
	</div>
</div>