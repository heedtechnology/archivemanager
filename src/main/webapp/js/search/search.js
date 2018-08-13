var cardview = $.extend({}, $.fn.datagrid.defaults.view, {
	renderRow: function(target, fields, frozen, rowIndex, rowData){
		var cc = [];
        cc.push('<td colspan=' + fields.length + ' style="padding:10px 5px;border:0;">');
        if (!frozen && rowData.id){
        	cc.push('<div>'+rowData.name+'</div>');
            
        }
        cc.push('</td>');
        return cc.join('');
	}
});
$(window).load(function() {
	$('#dg').datagrid('resize',{width:$(window).width()-350});
});
$(function(){
	$('#dg').datagrid({
		view: cardview		
    });
});

function doSearch() {
	$('#dg').datagrid('options').url = '/service/search/entity.json';
    $('#dg').datagrid('load', {
        qname: 'openapps_org_repository_1_0_item',
        query: $('#query').val()
    });
}