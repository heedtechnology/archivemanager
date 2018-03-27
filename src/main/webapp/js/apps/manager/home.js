$(function() {	
	$('#scopeNoteEditor').texteditor({
        
    });
});
function doSearch() {
	$('#dg').datagrid('load',{
		qname: $('#qname').val(), 
    	query: $('#query').val()
	});
}
function add(){
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Collections');
    $('#fm').form('clear');
    url = '/service/search/entity.json?query='+query;
}
function edit(){
    var row = $('#dg').datagrid('getSelected');
    if (row){
    	window.location.replace('collection?id='+row.id);
    }
}
function save(){
	$('#fm').form('submit',{
		url: url,
        onSubmit: function(){
        	return $(this).form('validate');
        },
        success: function(result){
        	var result = eval('('+result+')');
            if (result.errorMsg){
            	$.messager.show({
            		title: 'Error',
                    msg: result.errorMsg
                });
            } else {
            	$('#dlg').dialog('close');        // close the dialog
                $('#dg').datagrid('reload');    // reload the user data
            }
        }
	});
}
function destroy(){
	var row = $('#dg').datagrid('getSelected');
    if (row){
    	$.messager.confirm('Confirm','Are you sure you want to destroy this?',function(r){
	        if (r){
	        	$.post('destroy_user.php',{id:row.id},function(result){
		            if (result.success){
		            	$('#dg').datagrid('reload');    // reload the user data
		            } else {
		                $.messager.show({    // show error message
		                	title: 'Error',
		                	msg: result.errorMsg
		                });
		            }
	        	},'json');
	        }
    	});
    }
}
function formatContentType(val,row) {
	if(val == 'collection') return '<i class="fa fa-archive fa-lg" aria-hidden="true"></i>';
	return val;
}