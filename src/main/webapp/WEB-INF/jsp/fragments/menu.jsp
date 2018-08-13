<div class="rounded-border" style="float:left;width:225px;margin-right:5px;">
	<div style="display:table;">
		<div style="float:left;font-size: 12px;height:17px;margin-right:5px;">You are logged in as</div>
		<div sec:authentication="name" style="float:left;font-size: 12px;height:17px;float:left;margin-right:15px;font-weight:bold;"></div>						
	</div>
	<div class="title">Applications</div>
	<ul style="margin:5px 0;">
		<li><a href="/manager">Collection Manager</a></li>				
		<li><a href="/search">Search and Navigation</a></li>		
	</ul>
	<div class="title">Reporting</div>
	<ul style="margin:5px 0;">
		<li><a href="http://localhost:9000/reporting/collection/list.csv">Collection List CSV</a></li>				
	</ul>						
	<div class="title">Administration</div>
	<ul style="margin:5px 0;">					
		<li><a href="/dictionaries">Data Dictionaries</a></li>
		<li><a href="/users">User Management</a></li>			
	</ul>			
	<div class="title"><a href="/documentation">Documentation</a></div>
</div>