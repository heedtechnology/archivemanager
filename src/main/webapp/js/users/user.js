$(function() {	
	//doSearch();
});
$(window).load(function() {
    $(".loader").fadeOut("slow");
});
function add(){
	$('#dg').datagrid('gotoPage', 1);
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Add Accounts');
}
function doSearch() {
	//$('#dg').datagrid('options').url = '/service/search/entity.json?&page=1&rows=20';
	$('#dg').datagrid('load',{
		query: $('#query').val()
	});
}
function doSaveUser(){
	$.ajax({
		type: "POST",
        url: '/apps/user/save.json',
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
function doAddAccount(){
	var record = $('#dg').datagrid('getSelected');
	$.ajax({
        type: "POST",
        url: '/apps/user/account/add.json?accountId='+record.id+'&userId='+$.urlParam('id'),
        success: function(data) {
        	location.reload();
        }
      });
}
function doRemoveAccount(accountId){
	$.ajax({
        type: "POST",
        url: '/apps/user/account/remove.json?accountId='+accountId+'&userId='+$.urlParam('id'),
        success: function(data) {
        	location.reload();
        }
      });
}
function doCancel(){
	window.location.replace("/apps/user");
}