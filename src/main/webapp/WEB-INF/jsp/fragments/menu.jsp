<div class="rounded-border" style="float:left;width:225px;margin-right:5px;">
	<div style="display:table;">
		<div style="float:left;font-size: 12px;height:17px;margin-right:5px;">You are logged in as</div>
		<div sec:authentication="name" style="float:left;font-size: 12px;height:17px;float:left;margin-right:15px;font-weight:bold;"></div>						
	</div>
	<div class="title">Applications</div>
	<ul class="fa-ul">
		<li><a href="/manager"><span class="fa-li"><i class="fas fa-archive"></i></span>Data Manager</a></li>				
		<li><a href="/search"><span class="fa-li"><i class="fas fa-search"></i></span>Search and Navigation</a></li>		
	</ul>
	<div class="title">Reporting</div>
	<ul class="fa-ul">
		<li><a href="http://localhost:9000/reporting/collection/list.csv"><span class="fa-li"><i class="fas fa-file-alt"></i></span>Collection List CSV</a></li>				
	</ul>						
	<div class="title">Administration</div>
	<ul class="fa-ul">					
		<li><a href="/dictionaries"><span class="fa-li"><i class="fas fa-book-open"></i></span>Data Dictionaries</a></li>
		<li><a href="/users"><span class="fa-li"><i class="fas fa-user-alt"></i></span>User Management</a></li>			
	</ul>			
	<div class="title"><a href="/documentation">Documentation</a></div>
</div>