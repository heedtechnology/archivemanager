$(function() {
    //collection form
    $('#collection-name').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'formatblock', 'fontname', 'fontsize']
    });

    $('#collection-item-name').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'formatblock', 'fontname', 'fontsize']
    });

    $('#collection-scopeNote').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
    });
    $('#collection-bioNote').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
    });
    $('#collection-owner').textbox({
        icons: [{
            iconCls: 'icon-edit',
            handler: function(e) {
                var v = $(e.data.target).textbox('getValue');
                alert('The inputed value is ' + (v ? v : 'empty'));
            }
        }]
    });
    $('#collection-upload-form').form({
        success: function(data) {
            $('#tree1').tree('reload');
        }
    });
    //collection category form
    $('#collection-category-name').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
    });
    $('#collection-category-description').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
    });

    $('#collection-category-summary').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
    });

    $('#contentTypeDropDown').combobox({
        url: '/service/archivemanager/entity/content_type.json',
        method: 'get',
        valueField: 'id',
        textField: 'text'
    });

    $('#mainLayout').layout();
    $('#collection-heading').layout();
    $('#mainLayout').layout('resize', { height: 1700 });
    $('#abstractNoteEditor').texteditor({
        toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'formatblock', 'fontname', 'fontsize'],
        name: 'abstractNote'
    });

    $('#searchTabs').tabs({
        border: false,

        onSelect: function(title) {
            if ($('#dg').length) {
                var tab = $('#tt').tabs('getSelected');
                var index = $('#tt').tabs('getTabIndex', tab);
                switch (title) {
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
    $('#searchTabs').tabs('add', {
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
    $('#searchTabs').tabs('add', {
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
    $('#searchTabs').tabs('add', {
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

    $('#assocSelectionGrid').datagrid({
        //            url: '/service/search/entity.json',
        columns: [
            [
                { field: 'ck', checkbox: true },
                { field: 'id', title: 'ID', width: '20%' },
                { field: 'name', title: 'Name', width: '80%' }
            ]
        ],
        toolbar: '#addAssocSearchToolBar',
        view: scrollview,
        idField: 'id',
        autoRowHeight: false,
        pageSize: 50,
        striped: true,
        fit: true,
        fitColumns: true

    });

    $('#selectedItems').datagrid({
        columns: [
            [
                { field: 'ck', checkbox: true },
                { field: 'id', title: 'ID', width: 75 },
                { field: 'name', title: 'Name', width: 300 },
            ]
        ],
        toolbar: '#selectedItemsToolBar',
        view: scrollview,
        idField: 'id',
        autoRowHeight: false,
        pageSize: 50,
        striped: true,
        fit: true,
        fitColumns: true

    });

    $('#addAssociationsWindow').window({
        title: 'Add Associations',
        closed: true,
        onClose: function() {
          clearChecked($('#assocSelectionGrid'));
            $('#selectedItems').datagrid('loadData',[]);

          }
    });
})

function selectDigitalObject(index, row) {
    alert(row.name);
}

function selectNote(index, row) {
    alert(row.name);
}

function selectPerson(index, row) {
    alert(row.name);
}

function selectCorporation(index, row) {
    alert(row.name);
}

function selectSubject(index, row) {
    $('#subject-dialog').dialog('open')
}

function selectWebLink(index, row) {
    alert(row.name);
}

function navigate(name) {
    if (name == 'collection-import') $('#application-properties').tabs('select', 'collection-import-properties');
    else $('#application-properties').tabs('select', 'collection-associations');
    $('#application-body').tabs('select', name);
}

function treeSelect(node) {
    $('#application-properties').tabs('select', 'collection-associations');
    var ajaxReq = $.ajax({
        url: '/service/archivemanager/entity/fetch.json?id=' + node.id,
        type: 'GET',
        cache: false,
        contentType: false,
        processData: false
    });
    ajaxReq.done(function(entity) {
        if (entity.contentType == 'collection') {
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
        } else if (entity.contentType == 'category') {
            $('#collection-category-id').html(entity.id);
            $('#collection-category-name').texteditor('setValue', entity.name);
            $('#collection-category-description').texteditor('setValue', entity.description);
            $('#contentType').val(entity.contentType);
            $('#application-body').tabs('select', 'collection-category-form');
        } else {
            $('#collection-item-id').html(entity.id);
            $('#collection-item-name').texteditor('setValue', entity.name);
            addPropertyDiv();
            $('#application-body').tabs('select', 'collection-item-form');
        }
    });
    ajaxReq.fail(function(jqXHR) {
        message('Entity Selection Failed', 'unable to locate entity for id:' + node.id + ' text:' + node.text);
    });
}

function treeFormat(node) {
    var s = node.text;
    //if(node.children){
    //s += '&nbsp;<span style=\'color:blue\'>(' + node.children.length + ')</span>';
    //}
    return s;
}

function showAddNodeDialog(type) {

    if (type == 'category') {
        $('#divContentType').hide();
        $('#contentTypeDropDown').combobox('setValue', 'Category');
    } else {
        $('#divContentType').show();
    }

    var contentType = $('#contentType').val();
    var node = $('#collectionTree').tree('getSelected');
    var parentNode = $('#collectionTree').tree('getParent', node.target);
    $('#nodeAddDlg').dialog('open').dialog('center').dialog('setTitle', 'Add ');
}

function addToCollection() {
    var node = $('#collectionTree').tree('getSelected');
    var contentType = $('#contentTypeDropDown').val();
    $('#source').val(node.id);
    $('#assoc_qname').val('openapps_org_repository_1_0_categories');
    $('#entity_qname').val('openapps_org_repository_1_0_category');

    //    $.ajax({
    //      type: 'POST',
    //      url: '/service/entity/association/add.json',
    //      data: $('#nodeAddForm').serialize(),
    //      success: function (result) {
    //              if (result.response.status != 0 ){
    //                $.messager.show({
    //                  title: 'Error',
    //                      msg: result.errorMsg
    //                  });
    //              } else {
    //                $.messager.show({
    //                  title: 'Success',
    //                    msg: name + ' created successfully',
    //                    timeout: 1000,
    //                    showType: 'fade',
    //                  style:{
    //                      right:'',
    //                      bottom:''
    //                  }
    //                });
    //                $('#addNodeDialog').dialog('close');        // close the dialog
    //                $('#collectionTree').tree('reload', node.target); //Reload tree node
    //              }
    //      }
    //    });
    //$('#collectionTree').tree('reload', node.target);
}

function goHome() {
    var urlParams = new URLSearchParams(window.location.search);
    var tabIndex = urlParams.get('tab');
    var rowIndex = urlParams.get('row');
    window.location.replace('/apps/manager/home?tab=' + tabIndex + '&row=' + rowIndex);
}

function menuHandler(item) {

}

function showContextMenu(e, node) {
    e.preventDefault();
    $(this).tree('select', node.target);
    $('#mm').menu('show', {
        left: e.pageX,
        top: e.pageY
    });
}

function clearChecked(dataGrid) {
    dataGrid.datagrid('clearChecked');
}

function addToSelectedItems() {
    var itemsToAdd = $('#assocSelectionGrid').datagrid('getChecked');
    var count = $('#selectedItems').datagrid('getData').total;
    var rows = [];

    for (i = 0; i < itemsToAdd.length; i++) {
        rows.push({ id: itemsToAdd[i].id, name: itemsToAdd[i].name });
    }

    if (count == 0) {
        $('#selectedItems').datagrid('loadData', rows);
    } else {
        for (i = 0; i < rows.length; i++) {
            $('#selectedItems').datagrid('appendRow', {
                row: { id: rows[i].id, name: rows[i].name }
            });
        }
    }
}

function addToGataGrid(item, index) {
    var rows = [];
    rows.push({ id: 1, name: 'John,', description: 'This is a test' });
    $('#selectedItems').datagrid('loadData', rows);
    $('#selectedItems').datagrid('appendRow', {
        row: { id: '321', name: 'Bongo' }
    });
}

function addPropertyDiv() {

    $("<input id=\"collection-bulk-begin-date\" class=\"easyui-textbox\" label=\"Bulk Begin Date:\" labelPosition=\"top\" style=\"width:100%;\" />").appendTo("#lastrow");

}