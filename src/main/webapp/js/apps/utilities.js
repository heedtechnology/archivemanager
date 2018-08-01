function handleKeyPress(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        doSearch2();
    }
}

function doSearch2() {
    $('#assocSelectionGrid').datagrid('load', {
        qname: $('#searchQName').val(),
        query: $('#query').val()
    });
}

function loadAndOpen(qnameIn) {

    $('#addAssocSearch').after('<input id="searchQName" type="hidden" value="' + qnameIn + '"></input>');
    $('#assocSelectionGrid')
        .datagrid({ url: '/service/search/entity.json', queryParams: { qname: $('#searchQName').val() } });
    $('#addAssociationsWindow').window('open');

}

function clearChecked(dataGrid) {
    dataGrid.datagrid('clearChecked');
}

function removeChecked(dataGrid) {

    var rows = dataGrid.datagrid('getChecked');
    var cnt = rows.length;
    var ids = [];

    for (i = 0; i < rows.length; i++) {
        ids.push(rows[i].id);
    }
    for (i = 0; i < ids.length; i++) {
        var id = ids[i];
        var rowIndex = dataGrid.datagrid('getRowIndex', id);
        dataGrid.datagrid('deleteRow', rowIndex);
    }
}

function getAssocQName(title){

          switch (title) {
              case 'Corporations':
                  assoc_qname = 'openapps_org_classification_1_0_named_entities';
                  break;
              case 'Digital Objects':
                  assoc_qname = 'openapps_org_content_1_0_files';
                  break;
              case 'Notes':
                  assoc_qname = 'openapps_org_system_1_0_notes';
                  break;
              case 'People':
                  assoc_qname = 'openapps_org_classification_1_0_named_entities';
                  break;
              case 'Permissions':
                  assoc_qname = 'openapps_org_system_1_0_permissions';
                  break;
              case 'Subjects':
                  assoc_qname = 'openapps_org_classification_1_0_subjects';
                  break;
              case 'Web Links':
                  assoc_qname = 'openapps_org_content_1_0_web_links';
                  break;
          }
  return assoc_qname;
}

function getTargetQName(title){

          switch (title) {
              case 'Corporations':
                  assoc_qname = 'openapps_org_classification_1_0_corporation';
                  break;
              case 'Digital Objects':
                  assoc_qname = 'openapps_org_content_1_0_file';
                  break;
              case 'Notes':
                  assoc_qname = 'openapps_org_system_1_0_note';
                  break;
              case 'People':
                  assoc_qname = 'openapps_org_classification_1_0_person';
                  break;
              case 'Permissions':
                  assoc_qname = 'openapps_org_system_1_0_permission';
                  break;
              case 'Subjects':
                  assoc_qname = 'openapps_org_classification_1_0_subject';
                  break;
              case 'Web Links':
                  assoc_qname = 'openapps_org_content_1_0_web_link';
                  break;
          }
  return assoc_qname;
}

  function goHome() {
      var urlParams = new URLSearchParams(window.location.search);
      var tabIndex = urlParams.get('tab');
      var rowIndex = urlParams.get('row');
      window.location.replace('/apps/manager/home?tab=' + tabIndex + '&row=' + rowIndex);
      $('#tt').tabs('select', tabIndex);
  }