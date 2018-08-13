$(function() {	
	$('#error').hide();
});
$(window).load(function() {
	$('#dg').datagrid('resize',{width:$(window).width()-340});
});
function doSearch() {
	//$('#dg').datagrid('options').url = '/service/search/entity.json?&page=1&rows=20';
	$('#dg').datagrid('load',{});
}
function add(){
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Add Dictionary');
    $('#fm').form('clear');
}
function edit(){
	var record = $('#dg').datagrid('getSelected');
	window.location.href = "/dictionaries/edit?id="+record.id;
}
function save(){
	var valid = $('#fm').form('validate');
	if(valid) {
		$.ajax({
	        type: "POST",
	        url: '/dictionaries/add.json',
	        data: $("#fm").serialize(),
	        success: function(data) {
	        	location.reload();	        	
	        }
	      });
	} else {
		$('#error').show();
	}	
}
function destroy(){
	var row = $('#dg').datagrid('getSelected');
    if(row){
    	$.messager.confirm('Confirm','Are you sure you want to destroy this?',function(r){
    		if(r) {
	        	$.ajax({
	        		type: "POST",
	        	    url: '/dictionaries/remove.json?id='+row.id,
	        	    success: function(data) {
	        	    	$('#dg').datagrid('gotoPage', 1);
	        	    }
	        	});
	        }
    	});
    }
}
function doCancel() {
	$('#error').hide();
	$('#fm').form('clear');
	$('#dlg').dialog('close')
}
function formatRoles(val,row) {
	var str = '';
	for ( var i = 0, l = val.length; i < l; i++ ) {
		str += val[i].name + ", ";
	}
	return str.substring(0, str.length-2);
	
}