package org.archivemanager.server.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("am")
public class PropertyConfiguration {	
	
	@Value("${am.home.directory}")
	private String homeDirectory;
	public String getHomeDirectory() {
		return homeDirectory;
	}
	
	@Value("#{'${am.dictionary.files}'.split(',')}")
	private List<String> dictionaryFiles;
	public List<String> getDictionaryFiles() {
		return dictionaryFiles;
	}
	
	@Value("${am.datastore.username}")
	private String datastoreUsername;
	public String getDatastoreUsername() {
		return datastoreUsername;
	}
	
	@Value("${am.datastore.password}")
	private String datastorePassword;
	public String getDatastorePassword() {
		return datastorePassword;
	}
	
	@Value("${am.administrator.password}")
	private String administratorPassword;
	public String getaAministratorPassword() {
		return administratorPassword;
	}
	
	@Value("${am.search.host}")
	private String searchHost;
	public String getSearchHost() {
		return searchHost;
	}
	
	@Value("${am.search.port}")
	private int searchPort;
	public int getSearchPort() {
		return searchPort;
	}
}
