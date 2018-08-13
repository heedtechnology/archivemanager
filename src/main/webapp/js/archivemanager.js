$(window).load(function() {
	$('#app_body').width($(window).width()-350);
    $(".loader").fadeOut("slow");
});
$(window).on('resize', function(){
	$('#app_body').width($(window).width()-350);
});
$(document).ajaxComplete(function(event, xhr, settings) {
	if(xhr.responseText.indexOf('<meta name="page" content="login" />') >= 0)
		location.reload();
});
$.urlParam = function(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null){
       return null;
    }
    else{
       return decodeURI(results[1]) || 0;
    }
}
function formatDate(val,row) {
	if(val == 0) return "";
	return val.toString().substring(4,6)+'/'+val.toString().substring(6,8)+'/'+val.toString().substring(0,4);
}
function message(title,message) {
	$.messager.show({
        title:title,
        msg:message,
        showType:'slide',
        style:{
            right:'',
            top:document.body.scrollTop+document.documentElement.scrollTop,
        	bottom:''
    	}
    });
}
function autoHeight() {
 	var c = $('#mainLayout');
    var p = c.layout('panel','center');
    var oldHeight = p.panel('panel').outerHeight();
    p.panel('resize', {height:'auto'});
    var newHeight = p.panel('panel').outerHeight();
    c.layout('resize',{
    	height: (c.height() + newHeight - oldHeight)
    });
}