<table>
	<tr>
		<td style="vertical-align: top;width:300px;min-height:400px;">
			<div id="attributes"></div>
		</td>
		<td style="vertical-align: top;">
			<table id="dg" class="easyui-datagrid lines-both" style="width:100%;" toolbar="#toolbar" pagination="true" striped="true"
			   fitColumns="true" singleSelect="true" pageSize="20">
			   <thead>
			      <tr>
			         <th field="name" width="50">Name</th>
			         <th field="abstractNote" width="100">Description</th>
			      </tr>
			   </thead>
			</table>
		</td>
	</tr>
</table>
<div id="toolbar">
	<div id="breadcrumb" style="float:left;padding:5px;"></div>
   	<div style="float:right;">
      	<input id="query" style="width:200px;border:1px solid #ccc" onkeypress="handleKeyPress(event)">
      	<a href="#" class="easyui-linkbutton" plain="true" onclick="doSearch();">Search</a>
   	</div>
</div>
<div id="detail-window" title="Item Detail" class="easyui-dialog" closed="true">
	<div class="collection-detail">
		<div id="collection-detail-left" class="detail-panel">
			<div id="detail-name-row" class="detail-row">
				<div id="detail-name-label" class="collection-detail-label">Name</div>
				<div id="detail-name" class="collection-detail-value"></div>
			</div>
			<div id="detail-native-row" class="detail-row">
				<div id="detail-native-label" class="collection-detail-label"></div>
				<div id="detail-native" class="collection-detail-value"></div>
			</div>
			<div id="" class="detail-row">
				<div id="detail-collection-label" class="collection-detail-label">Collection</div>
				<div id="detail-collection" class="collection-detail-value"></div>
			</div>
			<div id="detail-date-expression-row" class="detail-row">		
				<div id="detail-date-expression-label" class="collection-detail-label">Date Expression</div>
				<div id="detail-date-expression" class="collection-detail-value"></div>
			</div>
			<div id="detail-content-type-row" class="detail-row">
				<div id="detail-content-type-label" class="collection-detail-label">Content Type</div>
				<div id="detail-content-type" class="collection-detail-value"></div>
			</div>
			<div id="detail-language-row" class="detail-row">
				<div id="detail-language-label" class="collection-detail-label">Language</div>
				<div id="detail-language" class="collection-detail-value"></div>
			</div>
			<div id="detail-container-row" class="detail-row">
				<div id="detail-container-label" class="collection-detail-label">Container</div>
				<div id="detail-container" class="collection-detail-value"></div>
			</div>
			<div id="detail-description-row" class="detail-row">
				<div id="detail-description-label" class="collection-detail-label">Description</div>
				<div id="detail-description" class="collection-detail-value"></div>
			</div>
			<div id="detail-summary-row" class="detail-row">
				<div id="detail-summary-label" class="collection-detail-label">Summary</div>
				<div id="detail-summary" class="collection-detail-value"></div>			
			</div>
			<div id="detail-abstract-note-row" class="detail-row">
				<div id="detail-abstract-note-label" class="collection-detail-label">Abstract</div>
				<div id="detail-abstract-note" class="collection-detail-value"></div>			
			</div>
			<div id="detail-path-row" class="detail-row">
				<div id="detail-path-label" class="collection-detail-label">Path</div>
				<div id="detail-path" class="collection-detail-value"></div>			
			</div>
		</div>
		<div id="collection-detail-center" class="detail-panel">
			<div id="detail-object-viewer" class="rounded-border">
				<img style="display:block;margin-top:130px;margin-right:auto;margin-left:auto;" src="/images/logo/ArchiveManager200.png" />
			</div>
			<div id="detail-object-navigation" class="rounded-border">
			
			</div>
		</div>
		<div id="collection-detail-right" class="detail-panel">
			<div id="collection-detail-attributes"></div>
		</div>
	</div>	
</div>