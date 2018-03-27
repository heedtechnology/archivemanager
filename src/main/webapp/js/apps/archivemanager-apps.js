$(window).load(function() {
    $(".loader").fadeOut("slow");
});
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