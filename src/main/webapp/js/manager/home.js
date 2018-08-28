$(window).load(function() {
	$('#dg').datagrid('resize',{width:$(window).width()-340});
});
$(function() {
    $('#home-add-name').textbox({
        name: name
    });
    $('#home-add-scopeNote').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
    });

    var tabIndex = -1;
    var rowIndex = -1;

    $('#dg').datagrid({
        onLoadSuccess: function() {

//
//            var urlParams = new URLSearchParams(window.location.search);
//            if (urlParams.values() != null && tabIndex == -1 && rowIndex == -1) {
//                tabIndex = urlParams.get('tab');
//                rowIndex = urlParams.get('row');
//
//                $('#tt').tabs('select', parseInt(tabIndex));
//                $('#dg').datagrid('selectRow', parseInt(rowIndex));
//                $('#dg').datagrid('highlightRow', parseInt(rowIndex));
//                tabIndex = -2;
//                rowIndex = -2;
//            }
//
        }
    });



    $('#tt').tabs({
        border: false,

        onSelect: function(title) {
            if ($('#dg').length) {
                var tab = $('#tt').tabs('getSelected');
                var index = $('#tt').tabs('getTabIndex', tab);
                switch (title) {
                    case 'Repositories':
                        $('#qname').val('openapps_org_repository_1_0_repository');
                        break;
                    case 'Collections':
                        $('#qname').val('openapps_org_repository_1_0_collection');
                        break;
                    case 'Corporate Entities':
                        $('#qname').val('openapps_org_classification_1_0_corporation');
                        break;
                    case 'Personal Entities':
                        $('#qname').val('openapps_org_classification_1_0_person');
                        break;
                    case 'Subjects':
                        $('#qname').val('openapps_org_classification_1_0_subject');
                        break;

                };
                $('#query').val('');
                doSearch();
            }
        }
    });
    // add a new tab panel
    $('#tt').tabs('add', {
        title: 'Repositories',
        closable: false,
        tools: [{
            iconCls: 'icon-mini-refresh',
            handler: function() {
                $('#qname').val('openapps_org_repository_1_0_repository');
                $('#query').val('');
                doSearch();
            }
        }],
        selected: true
    });
    $('#tt').tabs('add', {
        title: 'Collections',
        closable: false,
        tools: [{
            iconCls: 'icon-mini-refresh',
            handler: function() {
                $('#qname').val('openapps_org_repository_1_0_collection');
                $('#query').val('');
                doSearch();
            }
        }],
        selected: false
    });
    $('#tt').tabs('add', {
        title: 'Corporate Entities',
        closable: false,
        tools: [{
            iconCls: 'icon-mini-refresh',
            handler: function() {
                $('#qname').val('openapps_org_classification_1_0_corporation');
                $('#query').val('');
                doSearch();
            }
        }],
        selected: false
    });
    $('#tt').tabs('add', {
        title: 'Personal Entities',
        closable: false,
        tools: [{
            iconCls: 'icon-mini-refresh',
            handler: function() {
                $('#qname').val('openapps_org_classification_1_0_person');
                $('#query').val('');
                doSearch();
            }
        }],
        selected: false
    });
    $('#tt').tabs('add', {
        title: 'Subjects',
        closable: false,
        tools: [{
            iconCls: 'icon-mini-refresh',
            handler: function() {
                $('#qname').val('openapps_org_classification_1_0_subject');
                $('#query').val('');
                doSearch();
            }
        }],
        selected: false
    });
});

function doSearch() {
    $('#dg').datagrid('load', {
        qname: $('#qname').val(),
        query: $('#query').val()
    });
}

function add() {
    var qname = $('#qname').val();
    var localName = '';

    switch (qname) {
        case 'openapps_org_repository_1_0_repository':
            localName = "Repository"
            break;
        case 'openapps_org_repository_1_0_collection':
            localName = "Collection"
            break;
        case 'openapps_org_classification_1_0_corporation':
            localName = "Corporate Entity"
            break;
        case 'openapps_org_classification_1_0_person':
            localName = "Personal Entity"
            break;
        case 'openapps_org_classification_1_0_subject':
            localName = "Subject"
            break;
    };

    $('#dlg').dialog('open').dialog('center').dialog('setTitle', 'Add ' + localName);
    //  $('#fm').form('clear');
    //    url = '/service/search/entity.json?query='+query;
}

function edit() {
    var row = $('#dg').datagrid('getSelected');
    var rowindex = $('#dg').datagrid('getRowIndex', row);
    var tab = $('#tt').tabs('getSelected');
    var index = tab.panel('options').title;

    if (row) {
        window.location.replace('manager/collection?id=' + row.id + '&tab=' + index + '&row=' + rowindex);
    }
}

function save(form_name) {

    $('#addqname').val($('#qname').val());
    $.ajax({
        type: 'POST',
        //url: '/service/archivemanager/collection/add.json',
        url: '/service/entity/add.json',
        data: $('#' + form_name).serialize(),
        success: function(result) {
            if (result.response.status != 0) {
                $.messager.alert({
                    title: 'Error',
                    msg: result.errorMsg
                });
            } else {
                $.messager.alert({
                    title: 'Success',
                    msg: result.response.data[0].name + ' created successfully',

                });

            }
                            $('#dlg').dialog('close'); // close the dialog
                            $('#dg').datagrid('reload'); // reload the user data
                            window.location.replace('manager/collection?id=' + result.response.data[0].id);
        },
        error: function(result, statusCode, errorMsg){
            $.messager.alert({
              title: 'Error',
              msg: statusCode + ': ' + errorMsg
            });
        }
    });
}

function destroy() {
    var row = $('#dg').datagrid('getSelected');
    if (row) {
        $.messager.confirm('Confirm', 'Are you sure you want to remove ' + row.name + '?', function(r) {
            if (r) {
                $.post('/service/entity/remove.json', { id: row.id }, function(result) {
                    if (result.response.status != 0) {
                        $.messager.show({ // show error message
                            title: 'Error',
                            msg: result.errorMsg
                        });
                    } else {
                                    $.messager.show({
                                        title: 'Success',
                                        msg: result.response.data[0].name + ' removed successfully',
                                        timeout: 1000,
                                        showType: 'fade',
                                        style: {
                                            right: '',
                                            bottom: ''
                                        }
                                    });

                    }
                }, 'json');
            }
        });
    }
    // call 'refresh' method for tab panel to update its content
                            var tab = $('#tt').tabs('getSelected');  // get selected panel
                            tab.panel('refresh');
}

function formatContentType(val, row) {
    if (val == 'collection') return '<i class="fa fa-archive fa-lg" aria-hidden="true"></i>';
    return val;
}