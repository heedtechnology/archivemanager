$(function() {	
	$('#error').hide();
});
$(window).load(function() {
	$('#dg').datagrid('resize',{width:$(window).width()-350});
});
function doSearch() {
	//$('#dg').datagrid('options').url = '/service/search/entity.json?&page=1&rows=20';
	$('#dg').datagrid('load',{
		qname: $('#qname').val(), 
    	query: $('#query').val()
	});
}
function add(){
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Add User');
    $('#fm').form('clear');
}
function edit(){
	var record = $('#dg').datagrid('getSelected');
	window.location.href = "/users/edit?id="+record.id;
}
function save(){
	var valid = $('#fm').form('validate');
	if(valid) {
		$.ajax({
	        type: "POST",
	        url: '/users/add.json',
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
	        	    url: '/users/remove.json?id='+row.id,
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