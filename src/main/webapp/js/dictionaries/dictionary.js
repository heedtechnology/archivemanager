$(window).load(function() {
	$('#dg').datagrid({
		url:'/dictionaries/model/search.json?id='+$.urlParam('id')        
    });
});
function addTransaction(){
	$('#dg').datagrid('gotoPage', 1);
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Add Model');
}
function doSearch() {
	//$('#dg').datagrid('options').url = '/service/search/entity.json?&page=1&rows=20';
	$('#dg').datagrid('load',{
		query: $('#search_type').val()
	});
}
function doSaveModel(){
	$.ajax({
		type: "POST",
        url: '/dictionaries/model/save.json',
        data: $("#fm").serialize(),
        success: function(data) {
            if(data.status == 200) {
            	$('#user-msg').toggleClass('success-msg');
            	$('#user-msg').html('user saved successfully');
            } else {
            	$('#user-msg').toggleClass('failure-msg');
            	$('#user-msg').html(data.message);
            }
            setTimeout(function() {
            	$('#user-msg').html('');
            }, 4000);
        }
    });
}
function doAddModel(){	
	var data = $("#fm2").serialize();
	var dateVal = $('#date').val();
	if(dateVal && dateVal.length > 0) {
		var date = dateVal.split("/");
		dateVal = date[2]+date[0]+date[1];
	} else dateVal = 0;
	data += "&date="+dateVal
	$.ajax({
        type: "POST",
        url: '/dictionaries/model/add.json?dictionaryId='+$.urlParam('id'),
        data: data,
        success: function(data) {
        	$('#dg').datagrid('gotoPage', 1);
        	$('#dlg').dialog('close');
        }
      });
}
function doRemoveModel(transactionId){
	var record = $('#dg').datagrid('getSelected');
	$.ajax({
        type: "POST",
        url: '/dictionaries/model/remove.json?modelId='+record.id,
        success: function(data) {
        	$('#dg').datagrid('gotoPage', 1);
        	$('#dlg').dialog('close');
        }
      });
}
function doIndex(id){
	$.ajax({
		method: 'POST',
	    type: 'POST',
        url: '/dictionaries/model/index.json?dictionaryId='+$.urlParam('id')+'&modelId='+id,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
        	$('#dg').datagrid('reload');
        }
    });
}
function doCancel(){
	window.location.replace("/dictionaries");
}
function formatIcons(value) {
	return '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="doIndex('+value+')"><i class="fa fa-refresh" aria-hidden="true"></i> Index</a>';
}