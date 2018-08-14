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

  function getAssocQName(title) {

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

  function getTargetQName(title) {

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

  function customDateFormatter(date) {
      var y = date.getFullYear();
      var m = date.getMonth() + 1;
      var d = date.getDate();
      return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
  }

  function customDateParser(s) {

      if (!s) return new Date();
      if ( s.length == 10 && isValidDate(s)){
        var ss = (s.split('-'));

        var y = parseInt(ss[0], 10);
        var m = parseInt(ss[1], 10);
        var d = parseInt(ss[2], 10);
        if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
            return new Date(y, m - 1, d);
        } else {
            return new Date();
        }
      } else if (s.length == 10 && !isValidDate(s)){

          $.messager.alert({
              title: 'Error',
              icon: 'error',
              msg: 'Please enter a validate date must be in format yyyy-mm-dd'

          });

          $('#'+this.id).datebox('setValue','');
          $('#'+this.id).datebox('calendar').focus();

      }
  }

  function isValidDate(dateString) {
    var regEx = /^\d{4}-\d{2}-\d{2}$/;
    var validDate = false;
    if(!dateString.match(regEx)) return false;  // Invalid format
    var d = new Date(dateString);
    if(Number.isNaN(d.getTime())) return false; // Invalid date

    return d.toISOString().slice(0,10) === dateString;
  }

  function goHome() {
      var urlParams = new URLSearchParams(window.location.search);
      var tabIndex = urlParams.get('tab');
      var rowIndex = urlParams.get('row');
      window.location.replace('/apps/manager/home?tab=' + tabIndex + '&row=' + rowIndex);
      $('#tt').tabs('select', tabIndex);
  }