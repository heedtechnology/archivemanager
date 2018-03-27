<%@ include file="../fragments/apps_header.jsp" %>
	<style type="text/css">
		body{margin:0px;}
		.sc{margin:auto;width:1078px;}
		.donorAddressPanel{margin-right:5px;}
	    .donorAddAddress{margin-right:5px;margin-top:3px;}
	    #loadingWrapper {position:absolute;top:25%;width:100%;text-align:center;z-index:900001;text-align:center;}
	    #loading {margin:0 auto;border:1px solid #ccc;width:235px;padding:2px;text-align:left;}
	    #loading a {color:#225588;}
	    #loading .loadingIndicator {background:white;padding:10px;margin:0;height:auto;color:#444;}
	    #loadingMsg {margin-left:35px;font:normal 10px arial, tahoma, sans-serif;}
	    #versionMsg{height:60px;margin-left:35px;font:bold 13px tahoma, arial, helvetica;}  
	</style>
	
	<script type="text/javascript">
		var isomorphicDir = "../js/apps/smartclient/";
		var service_path = 'http://localhost:9000/';
	</script>
	<div id="loadingWrapper">
		<div id="loading">
		    <div class="loadingIndicator">
	        	<img src="/theme/images/logo/ArchiveManager200.png" style="margin-right:8px;float:left;vertical-align:top;"/>
		        <div id="versionMsg">Data Manager 1.0</div>
		        <div id="loadingMsg">Loading styles and images...</div>
		    </div>
		</div>
	</div>
	
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>	
	
	<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading Core API...';</script>
	<script src="/js/apps/smartclient/modules/ISC_Core.js"></script>
	<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading UI Components...';</script>
	<script src="/js/apps/smartclient/modules/ISC_Foundation.js"></script>
	<script src="/js/apps/smartclient/modules/ISC_Containers.js"></script>
	<script src="/js/apps/smartclient/modules/ISC_Grids.js"></script>
	<script src="/js/apps/smartclient/modules/ISC_Forms.js"></script>
	<script src="/js/apps/smartclient/modules/ISC_RichTextEditor.js"></script>
	<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading Data API...';</script>
	<script src="/js/apps/smartclient/modules/ISC_DataBinding.js"></script>
	<script src="/js/apps/smartclient/modules/ISC_Calendar.js"></script>
	<script src="/js/apps/smartclient/modules/ISC_Drawing.js"></script>
	<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading skin...';</script>
	<script type="text/javascript" src="/js/apps/smartclient/skins/Enterprise/load_skin.js?isc_version=10.0.js"></script>
	<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading Application...';</script>
	
	<script th:inline="javascript">
		var mapping_processors = {'none':'Not Included','text':'Text/String','number':'Number','relation':'Relationship'};
		var file_formats = {'spreadsheet':'Spreadsheet','oaxml':'OpenAppsXML'}
		var user = {'username':'${openapps_user.username}','fullname':'${openapps_user.firstName} ${openapps_user.lastName}'};
		var user_roles = ${roles};
		
		var height = '850px';
		var width = '1080px';
	</script>
	
	<div class="sc">
		<div id="gwt" style="width:1080px;height:850px;"></div>
		<script type="text/javascript" language="javascript" src="/js/apps/DataManager/DataManager.nocache.js"></script>
	</div>

<%@ include file="../fragments/apps_footer.jsp" %>