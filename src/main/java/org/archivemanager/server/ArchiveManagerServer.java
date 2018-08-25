package org.archivemanager.server;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.archivemanager.util.RepositoryModelEntityBinder;
import org.archivemanager.data.SystemModelEntityBinder;
import org.archivemanager.data.out.CollectionExportProcessor;
import org.archivemanager.data.out.SubjectExportProcessor;
import org.archivemanager.search.ArchiveManagerDictionary;
import org.archivemanager.search.indexing.DefaultEntityIndexer;
import org.archivemanager.search.indexing.NotableFigureEntryIndexer;
import org.archivemanager.search.indexing.RepositoryEntityIndexer;
import org.archivemanager.search.parsing.BaseTokenizer;
import org.archivemanager.server.config.PropertyConfiguration;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.heed.openapps.cache.CacheService;
import org.heed.openapps.content.DigitalObjectService;
import org.heed.openapps.content.service.FileDigitalObjectService;
import org.heed.openapps.crawling.CrawlingService;
import org.heed.openapps.crawling.service.StandardCrawlingService;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.service.XmlDataDictionaryService;
import org.heed.openapps.ehcache.EhCacheService;
import org.heed.openapps.elasticsearch.ElasticSearchQueryParser;
import org.heed.openapps.elasticsearch.RemoteElasticSearchIndexingService;
import org.heed.openapps.elasticsearch.RemoteElasticSearchService;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.indexing.EntityIndexer;
import org.heed.openapps.entity.service.DefaultEntityPersistenceListener;
import org.heed.openapps.neo4j.RemoteNeo4jEntityService;
import org.heed.openapps.property.PropertyService;
import org.heed.openapps.property.service.StandardPropertyService;
import org.heed.openapps.reporting.ReportingService;
import org.heed.openapps.reporting.jasper.JasperReportingService;
import org.heed.openapps.scheduling.SchedulingService;
import org.heed.openapps.scheduling.service.OpenAppsSchedulingService;
import org.heed.openapps.search.SearchService;
import org.heed.openapps.search.indexing.IndexingService;
import org.heed.openapps.search.parsing.QueryParser;
import org.heed.openapps.search.parsing.QueryTokenizer;
import org.heed.openapps.security.SecurityService;
import org.heed.openapps.security.service.OpenAppsSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties({PropertyConfiguration.class})
public class ArchiveManagerServer {
	@Autowired private PropertyConfiguration properties;
	
	
	@Bean
	public ArchiveManagerDictionary getArchiveManagerDictionary() {
		ArchiveManagerDictionary dictionary = new ArchiveManagerDictionary();
		/*
		 * <bean class="org.archivemanager.search.plugins.QNameDictionaryPlugin">
					<property name="entity" value="openapps_org_repository_1_0_item" />
					<property name="dictionaryService" ref="dictionaryService" />
				</bean>
				<bean class="org.archivemanager.search.plugins.SubjectDictionaryPlugin">
					<property name="searchService" ref="searchService" />
				</bean>
		 */
		return dictionary;
	}
	@Bean
	public QueryTokenizer getArchiveManagerTokenizer(ArchiveManagerDictionary dictionary) {
		BaseTokenizer tokenizer = new BaseTokenizer();
		tokenizer.setDictionary(dictionary);
		tokenizer.initialize();
		return tokenizer;
	}
	@Bean
	public SearchService getSearchService(DataDictionaryService dictionaryService, EntityService entityService) {
		//InMemoryElasticSearchService service = new InMemoryElasticSearchService();
		RemoteElasticSearchService service = new RemoteElasticSearchService();
		Map<String,EntityIndexer> indexers = new HashMap<String,EntityIndexer>();		
		
		DefaultEntityIndexer defaultIndexer = new DefaultEntityIndexer();
		defaultIndexer.setDictionaryService(dictionaryService);
		defaultIndexer.setEntityService(entityService);
		indexers.put("default", defaultIndexer);
		
		RepositoryEntityIndexer repositoryEntityIndexer = new RepositoryEntityIndexer();
		repositoryEntityIndexer.setDictionaryService(dictionaryService);
		repositoryEntityIndexer.setEntityService(entityService);
		indexers.put("openapps_org_repository_1_0_collection", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_item", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_printed_material", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_audio", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_financial", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_journals", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_legal", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_medical", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_memorabilia", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_miscellaneous", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_notebooks", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_photographs", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_research", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_video", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_scrapbooks", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_professional", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_manuscript", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_correspondence", repositoryEntityIndexer);
		indexers.put("openapps_org_repository_1_0_artwork", repositoryEntityIndexer);
		
		NotableFigureEntryIndexer notableFigureEntryIndexer = new NotableFigureEntryIndexer();
		notableFigureEntryIndexer.setDictionaryService(dictionaryService);
		notableFigureEntryIndexer.setEntityService(entityService);
		indexers.put("openapps_org_classification_1_0_entry", notableFigureEntryIndexer);
		
		service.setIndexers(indexers);
		return service;
	}
	@Bean
	public RestHighLevelClient getElasticsearchClient() {
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(properties.getSearchHost(), properties.getSearchPort(), "http")));
		return client;
	}
	@Bean
	public IndexingService getElasticSearchIndexingService() {
		RemoteElasticSearchIndexingService service = new RemoteElasticSearchIndexingService();
		
		return service;
	}
	@Bean
	public QueryParser getParser() {
		ElasticSearchQueryParser parser = new ElasticSearchQueryParser();		
		return parser;
	}
	
	@Bean
	public ReportingService getReportingService() {
		JasperReportingService service = new JasperReportingService();
		
		return service;
	}
	@Bean 
	public SecurityService getSecurityService(EntityService entityService, CacheService cacheService, SearchService searchService) {		
		OpenAppsSecurityService service = new OpenAppsSecurityService();
		service.setEntityService(entityService);
		service.setCacheService(cacheService);
		service.setSearchService(searchService);
		return service;
	}
	@Bean
	public CrawlingService getCrawlingService() {
		StandardCrawlingService service = new StandardCrawlingService();
		
		return service;
	}
	@Bean
	public DigitalObjectService getDigitalObjectService() {
		FileDigitalObjectService service = new FileDigitalObjectService();
		
		return service;
	}	
	@Bean
	public EntityService getRemoteEntityService(DataDictionaryService dictionaryService, CacheService cacheService) {		
		RemoteNeo4jEntityService service = new RemoteNeo4jEntityService("localhost", properties.getDatastoreUsername(), properties.getDatastorePassword());
		service.setDictionaryService(dictionaryService);
		service.setCacheService(cacheService);
		return service;
	}
	/*
	@Bean
	public EntityService getEmbeddedEntityService(NodeService nodeService, DataDictionaryService dictionaryService, SchedulingService schedulingService, CacheService cacheService) {		
		RemoteNeo4jEntityService service = new RemoteNeo4jEntityService("localhost", properties.getDatastoreUsername(), properties.getDatastorePassword());
		service.setDictionaryService(dictionaryService);
		service.setCacheService(cacheService);
		
		//EmbeddedNeo4jEntityService service = new EmbeddedNeo4jEntityService(nodeService, cacheService, schedulingService, dictionaryService);
		return service;
	}
	/*
	@Bean
	public NodeService getNodeService(Neo4jService neo4jService) {
		Neo4jNodeServiceImpl service = new Neo4jNodeServiceImpl();
		service.setNeo4jService(neo4jService);
		service.initialize();
		return service;
	}	
	@Bean
	public Neo4jService getNeo4jService() {
		String homeDirectory = properties.getHomeDirectory();
		Neo4jService service = new EmbeddedNeo4jServiceImpl(homeDirectory);
		service.start();
		return service;
	}
	*/

	@Bean
	public DataDictionaryService getDataDictionaryService() {
		XmlDataDictionaryService service = new XmlDataDictionaryService();
		service.setPropertyService(getPropertyService());
		for(String file : properties.getDictionaryFiles()) {
			service.getSystemImports().add(file);
		}
		service.initialize();
		return service;
	}
	@Bean
	public CacheService getCacheService() {
		EhCacheService service = new EhCacheService();
		service.initialize();
		return service;
	}
	@Bean
	public SchedulingService getSchedulingService() {
		OpenAppsSchedulingService service = new OpenAppsSchedulingService();
		service.setThreadCount(3);
		return service;
	}
	@Bean 
	public PropertyService getPropertyService() {
		StandardPropertyService service = new StandardPropertyService();
		return service;
	}
	@Bean
	public RepositoryModelEntityBinder getRepositoryModelEntityBinder(EntityService service) {
		RepositoryModelEntityBinder binder = new RepositoryModelEntityBinder(service);
		return binder;
	}
	@Bean 
	SystemModelEntityBinder getSystemModelEntityBinder(EntityService service) {
		SystemModelEntityBinder binder = new SystemModelEntityBinder(service);
		return binder;
	}
	@Bean
	public CollectionExportProcessor getCollectionExportProcessor() {
		CollectionExportProcessor processor = new CollectionExportProcessor();
		
		return processor;
	}
	@Bean
	public SubjectExportProcessor getSubjectExportProcessor() {
		SubjectExportProcessor processor = new SubjectExportProcessor();
		
		return processor;
	}

	@Bean
	public DefaultEntityPersistenceListener getDefaultEntityPersistenceListener(){
		DefaultEntityPersistenceListener defaultEntityPersistenceListener = new DefaultEntityPersistenceListener();
		return defaultEntityPersistenceListener;
	}
	public static void main(String[] args) {
		SpringApplication.run(ArchiveManagerServer.class, args);
	}
}
