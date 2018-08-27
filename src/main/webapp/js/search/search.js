var query;
var cardview = $.extend({}, $.fn.datagrid.defaults.view, {
	renderRow: function(target, fields, frozen, rowIndex, rowData){
		var cc = [];
        cc.push('<td colspan=' + fields.length + ' style="padding:10px 5px;border:0;">');
        if (!frozen && rowData.id){
        	cc.push('<div class="name" onclick="doViewDetail(\''+rowData.id+'\');">'+rowData.name+'</div>');
        	cc.push('<div class="description">'+rowData.description+'</div>');
        }
        cc.push('</td>');
        return cc.join('');
	}
});
$(window).on('resize', function(){
	$('#dg').datagrid('resize',{width:$(window).width()-675});
	$('#breadcrumb').width($(window).width()-970);
});
$(window).load(function() {
	$('#dg').datagrid('options').url = '/service/search/entity.json';	
	$('#dg').datagrid('resize',{width:$(window).width()-675});
	$('#breadcrumb').width($(window).width()-970);
	doSearch();
});
$(function(){
	$('#dg').datagrid({
		view: cardview,
		onLoadSuccess: function(data) {
			if(data.attributes) {
				var html = '<div id="attribute-accordian" class="easyui-accordion">';
				for(i=0; i < data.attributes.length; i++) {
					var attribute = data.attributes[i];
					html += '<ul class="easyui-datalist attribute-list" title="'+attribute.name+'" style="width:297px;">';
					for(j=0; j < attribute.values.length; j++) {
						var value = attribute.values[j];
						html += '<li value="'+value.query+'">'+value.name+' ('+value.count+')</li>';
					}
					html += '</ul>';
				}
				html += '</div>';
				$('#attributes').empty();
				$('#attributes').append(html);
				
				$('.attribute-list').datalist({
			        lines: true,
			        onClickRow:function(index,row) {
			        	doSelectAttribute(row.value);
			        }
			    });
			    $('#attribute-accordian').accordion({
			        animate:true
			    });
			}
			var breadcrumbHtml = '';
			for(i=0; i < data.breadcrumb.length; i++) {
				var crumb = data.breadcrumb[i];
				breadcrumbHtml += '<a class="breadcrumb-entry" href="#" onclick="doSearch(\''+crumb.query+'\');">'+crumb.label+'</a>';
				if(i < data.breadcrumb.length-1) breadcrumbHtml += '<svg aria-hidden="true" data-prefix="fac" data-icon="dot" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512" class="svg-inline--fa fa-dot fa-w-20"><path fill="currentColor" d="M256,176c44.2,0,80,35.8,80,80s-35.8,80-80,80s-80-35.8-80-80S211.8,176,256,176z" class=""></path></svg>';
			}
			$('#breadcrumb').empty();
			$('#breadcrumb').append(breadcrumbHtml);
		}
    });
});

function doSearch() {
	query = $('#query').val();
	$('#dg').datagrid('load', {
        qname: 'openapps_org_repository_1_0_item',
        query: query
    });
}
function doSelectAttribute(newQuery) {
	if(query.length > 0)query = query+' '+newQuery;
	else query = newQuery;
	$('#dg').datagrid('load', {
        qname: 'openapps_org_repository_1_0_item',
        query: query
    });
}
function doViewDetail(id) {
	var html = '<ul class="easyui-datalist">';
	$.ajax({
        type: "GET",
        url: '/search/detail.json?id='+id,
        success: function(data) {
        	$('#detail-name').html(data.name);
        	$('#detail-collection').html(data.collectionName);
        	
        	if(data.dateExpression) $('#detail-date-expression').html(data.dateExpression);
        	else $('#detail-date-expression-row').hide();
        	
        	if(data.contentType) $('#detail-content-type').html(data.contentType);
        	else $('#detail-content-type-row').hide();
        	
        	if(data.language) $('#detail-language').html(data.language);
        	else $('#detail-language-row').hide();
        	
        	if(data.container) $('#detail-container').html(data.container);
        	else $('#detail-container-row').hide();

        	if(data.description) $('#detail-description').html(data.description);
        	else $('#detail-description-row').hide();

        	if(data.summary) $('#detail-summary').html(data.summary);
        	else $('#detail-summary-row').hide();
        	
        	if(data.abstractNote) $('#detail-abstract-note').html(data.abstractNote);
        	else $('#detail-abstract-note-row').hide();
        	
        	if(data.path) {
        		var pathHtml = '';
        		for(i=0; i < data.path.length; i++) {
    				var crumb = data.path[i];
    				pathHtml += '<div id="'+crumb.id+'" class="detail-path-crumb">'+crumb.title+'</div>';
        		}
        		$('#detail-path').empty();
    			$('#detail-path').append(pathHtml);
        	}
        	
        	if((data.subjects && data.subjects.length > 0) || (data.people && data.people.length > 0)
        			|| (data.corporations && data.corporations.length > 0) || (data.weblinks && data.weblinks.length > 0)
        			|| (data.digitalObjects && data.digitalObjects.length > 0)) {
	        	var detailAttributeHtml = '<div id="detail-attribute-accordian" class="easyui-accordion">';
				if(data.subjects && data.subjects.length > 0) {
					detailAttributeHtml += '<div data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="Subjects" style="width:198px;">';
		        	for(i=0; i < data.subjects.length; i++) {
						var attribute = data.subjects[i];					
						detailAttributeHtml += '<li>'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}
				if(data.people && data.people.length > 0) {
					detailAttributeHtml += '<div data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="People" style="width:198px;">';
		        	for(i=0; i < data.people.length; i++) {
						var attribute = data.people[i];					
						detailAttributeHtml += '<li>'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}
				if(data.corporations && data.corporations.length > 0) {
					detailAttributeHtml += '<div data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="Corporations" style="width:198px;">';
		        	for(i=0; i < data.corporations.length; i++) {
						var attribute = data.corporations[i];					
						detailAttributeHtml += '<li>'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}			
			    if(data.weblinks && data.weblinks.length > 0) {
					detailAttributeHtml += '<div data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="Web Links" style="width:198px;">';
		        	for(i=0; i < data.weblinks.length; i++) {
						var attribute = data.weblinks[i];					
						detailAttributeHtml += '<li onclick="doViewDigitalObject(\''+attribute.url+'\');">'+attribute.name+'</li>';									
					}
		        	detailAttributeHtml += '</ul></div>';
				}
			    if(data.digitalObjects && data.digitalObjects.length > 0) {
					detailAttributeHtml += '<div data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="Digital Objects" style="width:198px;">';
		        	for(i=0; i < data.digitalObjects.length; i++) {
						var attribute = data.digitalObjects[i];					
						detailAttributeHtml += '<li onclick="doViewDigitalObject(\''+attribute.url+'\');">'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}
				detailAttributeHtml += '</div>';
				$('#collection-detail-attributes').empty();
				$('#collection-detail-attributes').append(detailAttributeHtml);
				
				$('.detail-attribute-list').datalist({
			        lines: true
			    });
			    $('#detail-attribute-accordian').accordion({
			        animate:true
			    });
        	}
        }
    });
	$('#detail-window').dialog('open');
}
function doViewDigitalObject(url) {
	var html;
	if(endsWith(url, ".pdf")) {
		html += '<object data="'+url+'" type="application/pdf" width="500px" height="300px"></object>';				
	} if(endsWith(url, ".mp4")) {
		html += '<video id="my_video_1" class="video-js vjs-default-skin" controls="" preload="auto" width="500" height="300" data-setup="{}"> <source src="'+url+'" type="video/mp4"> </video>';
	} else {
		html += '<div class="easyui-panel" data-options="href:\''+url+'\',closed:true"></div>';
	}
	$('#detail-object-viewer').empty();
    $('#detail-object-viewer').html(object);
}