<%@ include file="../fragments/site_header.jsp" %>
<script type="text/javascript" src="/js/search/search.js"></script>
<style>	
	.name{font-weight:bold;margin-bottom:5px;}
	.name:hover {text-decoration: underline;cursor:pointer;}
	.description{margin-left:20px;}
	.accordion-collapse {float: right;}
	.datagrid-row{border-top:1px solid black;}
	.datalist .datagrid-cell{white-space:normal;text-overflow:unset;overflow:hidden;}
	.datagrid-btable{width:100%;}
	.breadcrumb-entry{font-weight:bold;line-height:20px;}
	.fa-w-20{width:25px;height:12px;}
	
	#detail-window{width:975px;}
	.detail-panel{padding:2px;}
	.detail-row{margin-bottom:8px;}
	#collection-detail-left{float:left;width:250px;}
	#collection-detail-center{float:left;width:500px;}
	#collection-detail-right{float:right;width:200px;}
	.collection-detail-label{font-weight:bold;}
	.collection-detail-value{margin-left:8px;}
	#detail-object-viewer{min-height:300px;margin:5px 0px;}
	#detail-object-navigation{height:100px;display:none;}
</style>

<%@ include file="embedded/results.jsp" %>

<%@ include file="../fragments/site_footer.jsp" %>