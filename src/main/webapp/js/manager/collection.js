  $(function() {
      //collection form
      $('#collection-name').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'formatblock', 'fontname', 'fontsize']
      });

      $('#collection-scopeNote').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });
      $('#collection-bioNote').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });
      $('#collection-notes').texteditor({
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
      //$('#collection-category-name').texteditor({
      //    toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      //});
      $('#collection-category-description').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });

      $('#collection-category-summary').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });

      $('#collection-category-notes').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });

      $('#collection-item-description').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });
            $('#collection-item-summary').texteditor({
                toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
            });

      $('#collection-subject-datalist').datalist({
          data: [
            {}
          ]})

      $('#repository-description').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });

      $('#named-entity-role').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });
            $('#named-entity-description').texteditor({
                toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
            });
            $('#named-entity-note').texteditor({
                toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
            });

      $('#subject-description').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'insertorderedlist', 'insertunorderedlist', '-', 'formatblock', 'fontname', 'fontsize']
      });

      $('#node-add-content-type').combobox({
          url: '/service/archivemanager/entity/content_type.json',
          method: 'get',
          valueField: 'id',
          textField: 'text'
      });

      $('#collection-language').combobox({
          url: '/service/archivemanager/entity/language.json',
          method: 'get',
          valueField: 'id',
          textField: 'text'
      });

      $('#collection-item-language').combobox({
          url: '/service/archivemanager/entity/language.json',
          method: 'get',
          valueField: 'id',
          textField: 'text'
      });

      $('#repository-language').combobox({
          url: '/service/archivemanager/entity/language.json',
          method: 'get',
          valueField: 'id',
          textField: 'text'
      });
//item-genre
      $('#mainLayout').layout();
      $('#collection-heading').layout();
      $('#mainLayout').layout('resize', { width: 1200, height: 1700 });
      $('#node-add-abstract-note').texteditor({
          toolbar: ['bold', 'italic', 'strikethrough', 'underline', '-', 'formatblock', 'fontname', 'fontsize'],
          name: 'abstractNote'
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
          pageSize: 500,
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
          pageSize: 500,
          striped: true,
          fit: true,
          fitColumns: true

      });

      $('#addAssociationsWindow').window({
          title: 'Add Associations',
          closed: true,
          onClose: function() {
              clearChecked($('#assocSelectionGrid'));
              clearChecked($('#selecctedItems'));
              $('#selectedItems').datagrid('loadData', { "total": 0, "rows": [] });

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
      if (name == 'collection-import') $('#application-properties').tabs('select', 'collection-item-associations');
      else $('#application-properties').tabs('select', 'collection-associations');
      $('#application-body').tabs('select', name);
  }

  function selectRootNode(node, data){
      var rootNode = $('#collectionTree').tree('getRoot');
      $('#collectionTree').tree('select', rootNode.target);
  }

  function treeSelect(node) {


      var ajaxReq = $.ajax({
          url: '/service/archivemanager/entity/fetch.json?id=' + node.id,
          type: 'GET',
          cache: false,
          contentType: false,
          processData: false
      });
      ajaxReq.done(function(entity) {
          $('#collection-associations').show();
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
              $('#collection-bulk-begin-date').textbox('setValue', entity.bulkBegin);
              $('#collection-bulk-end-date').textbox('setValue', entity.bulkEnd);
              $('#collection-begin-date').textbox('setValue', entity.begin);
              $('#collection-end-date').textbox('setValue', entity.end);
              $('#collection-extent-units').textbox('setValue', entity.extentUnits);
              $('#collection-extent-value').textbox('setValue', entity.extentValue);
              $('#application-body').tabs('select', 'collection-form');
              $('#application-properties').tabs('select', 'collection-associations');
          } else if (entity.contentType == 'category') {
              $('#collection-category-id').html(entity.id);
              $('#collection-category-name').textbox('setValue', entity.name);
              $('#collection-category-description').texteditor('setValue', entity.description);
              $('#contentType').val(entity.contentType);
              $('#application-body').tabs('select', 'collection-category-form');
          } else if (entity.contentType == 'repository'){
              $('#collection-associations').hide();
              $('#repository-id').html(entity.id);
              $('#repository-language').combobox('setValue', entity.language);
              $('#repository-owner').textbox('setValue', convertNullString(entity.user));
              $('#repository-name').textbox('setValue', convertNullString(entity.name));
              $('#repository-description').texteditor('setValue', convertNullString(entity.description));
              $('#repository-agency-code').textbox('setValue', convertNullString(entity.data.agency_code));
              $('#repository-branding').textbox('setValue', convertNullString(entity.data.branding));
              $('#repository-country-code').textbox('setValue', convertNullString(entity.data.country_code));
              $('#repository-institution').textbox('setValue', convertNullString(entity.data.institution));
              $('#repository-internal').textbox('setValue', convertNullString(entity.data.isPublic));
              $('#repository-nces').textbox('setValue', convertNullString(entity.data.nces));
              $('#repository-short-name').textbox('setValue', convertNullString(entity.data.short_name));
              $('#repository-type').textbox('setValue', convertNullString(entity.data.type));
              $('#repository-url').textbox('setValue', convertNullString(entity.data.url));
              $('#application-body').tabs('select', 'repository-form');
          } else if (entity.contentType == 'corporation' || entity.contentType == 'person' ) {
              $('#named-entity-name').textbox('setValue', convertNullString(entity.name));
              $('#named-entity-description').texteditor('setValue', convertNullString(entity.description));
              $('#named-entity-note').texteditor('setValue', convertNullString(entity.note));
              $('#named-entity-role').texteditor('setValue', convertNullString(entity.role));
              $('#named-entity-source').textbox('setValue', convertNullString(entity.source));
              $('#application-body').tabs('select', 'collection-named-entity-form');
          } else if (entity.contentType == 'subject'){
              $('#subject-name').textbox('setValue', convertNullString(entity.name));
              $('#subject-description').texteditor('setValue', convertNullString(entity.description));
              $('#subject-source').textbox('setValue', convertNullString(entity.source));
              $('#subject-type').textbox('setValue', convertNullString(entity.type));
              $('#application-body').tabs('select', 'collection-subject-form');
          } else {
              $('#application-body').tabs('select', 'collection-item-form');
              $('#collection-item-id').html(entity.id);
              $('#collection-item-name').textbox('setValue', entity.name);
              $('#collection-item-description').texteditor('setValue',entity.description);
              $('#collection-item-summary').texteditor('setValue',entity.summary);
              $('#item-collection-id').textbox('setValue', entity.collectionId);
              $('#item-collection-name').textbox('setValue', entity.collectionName);
              $('#item-collection-url').textbox('setValue', entity.collectionUrl);
              $('#collection-item-container').val(entity.container);

              if (entity.data.medium != null){
                getMedium(entity.contentType);
                $('#item-medium-div').css('display','block');
                $('#item-medium').combobox('setValue',entity.data.medium);
              } else {
                $('#item-medium-div').css('display','none');
              }

              if (entity.data.genre != null){
                getGenre(entity.contentType);
                $('#item-genre-div').css('display','block');
                $('#item-genre').combobox('setValue',entity.data.genre);
              } else {
                $('#item-genre-div').css('display','none');
              }
              if (entity.data.form != null){
                getForm(entity.contentType);
                $('#item-form-div').css('display','block');
                $('#item-form').combobox('setValue',entity.data.form);
              } else {
                $('#item-form-div').css('display','none');
              }
          }

          reloadAssociations(entity);


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
      $('#node-add-content-type').combobox('clear');
      $('#node-add-name').textbox('clear');
      $('#node-add-abstract-note').texteditor('setValue','');
      document.getElementById("nodeAddForm").reset();

      if ( type == null ){
         var node = $('#collectionTree').tree('getSelected');
         var iconCls = node.iconCls;
         if ( iconCls.indexOf("repository") > 0 ){
             showAddNodeDialog('collection');
             return;
         } else if ( iconCls.indexOf("collection") > 0){
             showAddNodeDialog('category');
             return;
         } else {
            $('#divContentType').show();
         }
      } else if (type == 'category') {
          $('#divContentType').hide();
          $('#node-add-content-type').combobox('setValue', 'category');
      } else if (type == 'collection') {
          $('#divContentType').hide();
          $('#node-add-content-type').combobox('setValue', 'collection');
      } else {
          $('#divContentType').show();
      }

      $('#nodeAddDlg').dialog('open').dialog('center').dialog('setTitle', 'Add ');
  }

  function addToCollection() {
      var rootNode = $('#collectionTree').tree('getRoot');
      var node = $('#collectionTree').tree('getSelected');
      var contentType = $('#node-add-content-type').val();
      $('#source').val(node.id);

       var qLocalName = mapContenTypeToAssocQ(contentType);
      $('#node-add-entity_qname').val('openapps_org_repository_1_0_'+contentType);
      if ( qLocalName == 'items'){
          $('#node-add-description').val($('#node-add-abstract-note').texteditor('getValue'));
      }
      $('#node-add-assoc_qname').val('openapps_org_repository_1_0_'+ qLocalName);
          $.ajax({
            type: 'POST',
            url: '/service/entity/association/add.json',
            data: $('#nodeAddForm').serialize(),
            success: function (result) {
                    if (result.response.status != 0 ){
                      $.messager.alert({
                        title: 'Error',
                            msg: result.errorMsg
                        });
                    } else {
                      $.messager.alert({
                        title: 'Success',
                        msg: result.response.messages[0],
                        fn: function(){$('#nodeAddDlg').dialog('close');},
                        style:{
                            right:'',
                            bottom:''
                        }
                      });
                      if( rootNode.id == node.id ){
                        $('#collectionTree').tree('reload');
                      } else {
                        $('#collectionTree').tree('reload',node.target);
                      }
                      var newNode = $('#collectionTree').tree('find', result.response.data[0].id);
                      $('#collectionTree').tree('select', newNode);
                    }
            }
          });

 //Reload tree node

          $('#nodeAddDlg').dialog('close');        // close the dialog


  }

  function showContextMenu(e, node) {
      e.preventDefault();

      var iconCls = node.iconCls;
      if ( iconCls.indexOf("repository") > 0 ){
        $('#mm2').menu('show', {
            left: e.pageX,
            top: e.pageY
        });
      } else {
        $('#mm').menu('show', {
            left: e.pageX,
            top: e.pageY
        });
      }
      $('#collectionTree').tree('select',node.target);
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
              var id = rows[i].id;
              var name = rows[i].name;
              var rowIndex = $('#selectedItems').datagrid('getRowIndex', id);
              if (rowIndex == -1) {
                  $('#selectedItems').datagrid('appendRow', {
                      id: id,
                      name: name
                  });
              }
          }
      }
  }

  function addNewToSelectedItems() {
      var count = $('#selectedItems').datagrid('getData').total;
      var name = $('#assocCreateDlgName').textbox('getText');
      if (count == 0) {
          $('#selectedItems').datagrid({ data: [{ id: 'New', name: name }] });
      } else {
          $('#selectedItems').datagrid('appendRow', {
              id: 'New',
              name: name
          });
      }
      $('#assocCreateDlg').dialog('close');
  }

  function saveAssociations() {
      var associationsToAdd = $('#selectedItems').datagrid('getData');
      var count = $('#selectedItems').datagrid('getData').total;

      if (count < 1) {
          $.messager.alert({
              title: 'Error',
              msg: 'Nothing to Save. Please Select some items to Add',
              style: {
                  right: '',
                  bottom: ''
              }
          });
      } else if (count > 0) {

          var accPanel = $('#collection-associations').accordion('getSelected');
          var title = accPanel.panel('options').title;
          var assoc_qname = getAssocQName(title);
          var target_qname = getTargetQName(title);

          var targetIds = '';
          for (i = 0; i < associationsToAdd.rows.length; i++) {
              if (i == associationsToAdd.rows.length - 1)
                  targetIds += associationsToAdd.rows[i].id + ':' + associationsToAdd.rows[i].name;
              else
                  targetIds += associationsToAdd.rows[i].id + ':' + associationsToAdd.rows[i].name + '~~';
          }

          var node = $('#collectionTree').tree('getSelected');
          var source = node.id;

          $.ajax({
              type: 'POST',
              url: '/service/entity/association/addall.json',
              data: { assoc_qname: assoc_qname, target_qname: target_qname, source: source, targetIds: targetIds },
              success: function(result) {
                  if (result.response.status != 200) {
                      $.messager.alert({
                          title: 'Error',
                          msg: result.errorMsg
                      });
                  } else {
                      $.messager.alert({
                          title: 'Success',
                          msg: name + ' association(s) created successfully',
                          style: {
                              right: '',
                              bottom: ''
                          }
                      });
                      $('#collectionTree').tree('select', node.target);
                  }
              }
          });
      }

      $('#addAssociationsWindow').window('close');
  }

  function reloadAssociations(entity){

      var panels = $('#collection-associations').accordion('panels');

      panels.forEach(
        function(panel){
          var divName = null;
          var data = null;
          var panel1 = panel;
          var panelTitle = panel.panel('options').title;
          console.log(panelTitle);

          switch(panelTitle){
            case 'Corporations': divName = 'coll_assoc_dl_corp'; if ( entity.corporations != null && entity.corporations.length > 0 ){data=entity.corporations} else data=null; break;
            case 'Digital Objects': divName = 'coll_assoc_dl_digitalObjects'; if ( entity.digitalObjects != null && entity.digitalObjects.length > 0 ){data=entity.digitalObjects} else data=null; break;
            case 'People': divName = 'coll_assoc_dl_people'; if ( entity.people != null && entity.people.length > 0 ){data=entity.people} else data=null; break;
            case 'Permissions': divName = 'coll_assoc_dl_permissions'; if ( entity.permissions != null && entity.permissions.length > 0 ){data=entity.permissions} else data=null; break;
            case 'Subjects': divName = 'coll_assoc_dl_subjects'; if ( entity.subjects != null && entity.subjects.length > 0 ){data=entity.subjects} else data=null; break;
            case 'Web Links': divName = 'coll_assoc_dl_weblinks'; if ( entity.weblinks != null && entity.weblinks.length > 0 ){data=entity.weblinks} else data=null; break;
          }

          if ( divName != null){
            updateAssocRows(divName,data);
          }
        });
  }

  function updateAssocRows(datalistName,data){
//      var rows = $('#'+datalistName).datalist('getRows');
//      var numberOfRows = rows.length;
//      for ( i = 0; i < numberOfRows; i++){
//        $('#'+datalistName).datalist('deleteRow',0);
//      };
      $('#'+datalistName).datagrid('loadData', []);
      if ( data != null ){
        data.forEach(
          function(entry){
            $('#'+datalistName).datalist('appendRow', { text: entry.name, value: entry.name});
          }
        );
      }
  }

  function getGenre(contentType){
      var ajaxReq = $.ajax({
          url: '/service/dictionary/model/fetch.json?qname=openapps_org_repository_1_0_' + contentType.toLowerCase(),
          type: 'GET',
          cache: false,
          contentType: false,
          processData: false
      });

      ajaxReq.done(function(entity) {
      $('#item-genre').combobox('clear');
        var fields = entity.fields;
        for ( i=0; i < fields.length; i++ ){
          if (fields[i].name == "Genre"){
            var values = fields[i].values;
            var genres = { genre: [] };
            for ( var i in values ){
              var temp = values[i];
              genres.genre.push({
                "label": temp.name,
                "value": temp.name
              });
            }
            $('#item-genre').combobox('loadData', genres.genre);
            var stuff = $('#item-genre').combobox('getValues');
          }
        }
      });

  }

function getForm(contentType){
      var ajaxReq = $.ajax({
          url: '/service/dictionary/model/fetch.json?qname=openapps_org_repository_1_0_' + contentType.toLowerCase(),
          type: 'GET',
          cache: false,
          contentType: false,
          processData: false
      });

      ajaxReq.done(function(entity) {
      $('#item-form').combobox('clear');
        var fields = entity.fields;
        for ( i=0; i < fields.length; i++ ){
          if (fields[i].name == "Form"){
            var values = fields[i].values;
            var forms = { form: [] };
            for ( var i in values ){
              var temp = values[i];
              forms.form.push({
                "label": temp.name,
                "value": temp.name
              });
            }
            $('#item-form').combobox('loadData', forms.form);
          }
        }
      });

  }

function getMedium(contentType){
      var ajaxReq = $.ajax({
          url: '/service/dictionary/model/fetch.json?qname=openapps_org_repository_1_0_' + contentType.toLowerCase(),
          type: 'GET',
          cache: false,
          contentType: false,
          processData: false
      });

      ajaxReq.done(function(entity) {
      $('#item-medium').combobox('clear');
        var fields = entity.fields;
        for ( i=0; i < fields.length; i++ ){
          if (fields[i].name == "Medium"){
            var values = fields[i].values;
            var mediums = { medium: [] };
            for ( var i in values ){
              var temp = values[i];
              mediums.medium.push({
                "label": temp.name,
                "value": temp.name
              });
            }
            $('#item-medium').combobox('loadData', mediums.medium);
          }
        }
      });

  }