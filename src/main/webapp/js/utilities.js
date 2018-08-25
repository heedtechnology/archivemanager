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
      if (s.length == 10 && isValidDate(s)) {
          var ss = (s.split('-'));

          var y = parseInt(ss[0], 10);
          var m = parseInt(ss[1], 10);
          var d = parseInt(ss[2], 10);
          if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
              return new Date(y, m - 1, d);
          } else {
              return new Date();
          }
      } else if (s.length == 10 && !isValidDate(s)) {

          $.messager.alert({
              title: 'Error',
              icon: 'error',
              msg: 'Please enter a validate date must be in format yyyy-mm-dd'

          });

          $('#' + this.id).datebox('setValue', '');
          $('#' + this.id).datebox('calendar').focus();

      }
  }

  function isValidDate(dateString) {
      var regEx = /^\d{4}-\d{2}-\d{2}$/;
      var validDate = false;
      if (!dateString.match(regEx)) return false; // Invalid format
      var d = new Date(dateString);
      if (Number.isNaN(d.getTime())) return false; // Invalid date

      return d.toISOString().slice(0, 10) === dateString;
  }

  function mapContenTypeToAssocQ(contentType) {
      switch (contentType) {
          case 'category':
              return 'categories';
          default:
              return "items";

      }
  }

  function convertNullString(inString) {
      if (inString == "null") {
          return "";
      } else {
          return inString;
      }
  }

  function goHome() {
      var urlParams = new URLSearchParams(window.location.search);
      var tabIndex = urlParams.get('tab');
      var pageIndex = urlParams.get('page');
      var rowIndex = urlParams.get('row');

      window.location.replace('/manager');
      $('#tt').tabs('select', tabIndex);
  }

  function updateEntity(form_name) {
      var node = $('#collectionTree').tree('getSelected');
      $('#' + form_name + '-id').val(node.id);
      $.ajax({
          type: 'POST',
          url: '/service/entity/update.json',
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
                      msg: result.response.data[0].name + ' updated successfully',
                      fn: function() {
                          // update the selected node text if the name of the entity was updated
                          var node = $('#collectionTree').tree('getSelected');
                          if (node.text != result.response.data[0].name) {
                              $('#collectionTree').tree('update', {
                                  target: node.target,
                                  text: result.response.data[0].name
                              });
                          }
                      },
                      style: {
                          right: '',
                          bottom: ''
                      }
                  });
              }
          },
          error: function(result, statusCode, errorMsg) {
              $.messager.alert({
                  title: 'Error',
                  msg: 'There was an internal error processing this request: ' + statusCode + ': ' + errorMsg
              });
          }
      });
  }