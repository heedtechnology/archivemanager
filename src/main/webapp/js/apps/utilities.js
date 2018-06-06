function handleKeyPress(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        doSearch2();
    }
}

function doSearch2() {
    //$('#qname').val('openapps_org_classification_1_0_subjects');
//    alert($('#searchQName').val());
//       $('#assocSelectionGrid')
//          .datagrid({queryParams: { qname: qnameIn, query: $('#query').val()}});
//       $('#assocSelectionGrid').datagrid({queryParams:{  qname:$('#searchQName').val(), query: $('#query').val()}});
//       $('#assocSelectionGrid').datagrid('reload');
alert($('#searchQName').val());
alert($('#query').val());
           $('#assocSelectionGrid').datagrid('load', {
               qname: $('#searchQName').val(),
               query: $('#query').val()
           });
}

function loadAndOpen(qnameIn){



  //searchQName = jQuery('<input id="searchQName" type="hidden" value="'+qnameIn+'"></input>');
  $('#addAssocSearch').after('<input id="searchQName" type="hidden" value="'+qnameIn+'"></input>');
       $('#assocSelectionGrid')
          .datagrid({url:'/service/search/entity.json',queryParams: { qname: $('#searchQName').val()}});
//               $('#assocSelectionGrid').datagrid('load', {
//           qname: $('#searchQName').val()
//           });
  $('#addAssociationsWindow').window('open');

}