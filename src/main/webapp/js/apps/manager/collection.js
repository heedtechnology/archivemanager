$(function(){
	//collection form
	$('#collection-name').texteditor({
		 toolbar:['bold','italic','strikethrough','underline','-','formatblock','fontname','fontsize']
	});
	$('#collection-scopeNote').texteditor({
		 toolbar:['bold','italic','strikethrough','underline','-','insertorderedlist','insertunorderedlist','-','formatblock','fontname','fontsize']
	});
	$('#collection-bioNote').texteditor({
		 toolbar:['bold','italic','strikethrough','underline','-','insertorderedlist','insertunorderedlist','-','formatblock','fontname','fontsize']
	});	 
	$('#collection-owner').textbox({
         icons: [{
             iconCls:'icon-edit',
             handler: function(e){
                 var v = $(e.data.target).textbox('getValue');
                 alert('The inputed value is ' + (v ? v : 'empty'));
             }
         }]
	});
    $('#collection-upload-form').form({
         success:function(data){
             $('#tree1').tree('reload');
         }
    });
    //collection category form
    $('#collection-category-name').texteditor({
		 toolbar:['bold','italic','strikethrough','underline','-','insertorderedlist','insertunorderedlist','-','formatblock','fontname','fontsize']
	});	 
    $('#collection-category-description').texteditor({
		 toolbar:['bold','italic','strikethrough','underline','-','insertorderedlist','insertunorderedlist','-','formatblock','fontname','fontsize']
	});
    $('#collection-category-summary').texteditor({
		 toolbar:['bold','italic','strikethrough','underline','-','insertorderedlist','insertunorderedlist','-','formatblock','fontname','fontsize']
	});
    
	$('#mainLayout').layout();
	$('#collection-heading').layout();
	$('#mainLayout').layout('resize',{height:1700});
});

function selectDigitalObject(index,row) {
	 alert(row.name);
}
function selectNote(index,row) {
	 alert(row.name);
}
function selectPerson(index,row) {
	 alert(row.name);
}
function selectCorporation(index,row) {
	 alert(row.name);
}
function selectSubject(index,row) {
	$('#subject-dialog').dialog('open')
}
function selectWebLink(index,row) {
	 alert(row.name);
}
function navigate(name) {
	if(name == 'collection-import') $('#application-properties').tabs('select', 'collection-import-properties');
	else $('#application-properties').tabs('select', 'collection-associations');
	$('#application-body').tabs('select', name);
}
function treeSelect(node) {
	$('#application-properties').tabs('select', 'collection-associations');
	var ajaxReq = $.ajax({
		url : '/service/archivemanager/entity/fetch.json?id='+node.id,
		type : 'GET',
		cache : false,
		contentType : false,
		processData : false
	});
	ajaxReq.done(function(entity) {		
		if(entity.contentType == 'collection') {
			$('#collection-id').html(entity.id);
			$('#collection-language').combobox('setValue', entity.language);
			$('#collection-owner').textbox('setValue', entity.user);
			$('#collection-name').texteditor('setValue', entity.name);
			$('#collection-scopeNote').texteditor('setValue', entity.scopeNote);
			$('#collection-bioNote').texteditor('setValue', entity.bioNote);
			$('#collection-comment').textbox('setValue', entity.comment);
			$('#collection-url').textbox('setValue', entity.url);
			$('#collection-code').textbox('setValue', entity.code);
			$('#collection-identifier').textbox('setValue', entity.identifier);
			$('#collection-accession-date').textbox('setValue', entity.accessionDate);
			$('#collection-date-expression').textbox('setValue', entity.dateExpression);
			$('#collection-begin-date').textbox('setValue', entity.beginDate);
			$('#collection-end-date').textbox('setValue', entity.endDate);
			$('#collection-begin').textbox('setValue', entity.begin);
			$('#collection-end').textbox('setValue', entity.end);
			$('#collection-extent-units').textbox('setValue', entity.extentUnits);
			$('#collection-extent-value').textbox('setValue', entity.extrnetValue);
			$('#application-body').tabs('select', 'collection-form');
		} else if(entity.contentType == 'category') {
			$('#collection-category-id').html(entity.id);
			$('#collection-category-name').texteditor('setValue', entity.name);
			$('#collection-category-description').texteditor('setValue', entity.description);
			$('#collection-category-description').texteditor('setValue', entity.description);
			$('#application-body').tabs('select', 'collection-category-form');
		} else {
			$('#collection-item-id').html(entity.id);
			$('#collection-item-name').texteditor('setValue', entity.name);
			$('#application-body').tabs('select', 'collection-item-form');
		}
	});
	ajaxReq.fail(function(jqXHR) {
		message('Entity Selection Failed', 'unable to locate entity for id:'+node.id+' text:'+node.text);
	});
}
function treeFormat(node){
	 var s = node.text;
	 //if(node.children){
		 //s += '&nbsp;<span style=\'color:blue\'>(' + node.children.length + ')</span>';
	 //}
	 return s;
}