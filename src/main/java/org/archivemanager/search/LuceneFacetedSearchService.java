package org.archivemanager.search;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.heed.openapps.QName;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.indexing.EntityIndexer;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.heed.openapps.property.PropertyService;
import org.archivemanager.search.indexing.EntityIndexingJob;
import org.archivemanager.search.parsing.LuceneEntityQueryParser;
import org.archivemanager.search.parsing.LuceneSearchQueryParser;
import org.heed.openapps.scheduling.Job;
import org.heed.openapps.scheduling.SchedulingService;
import org.heed.openapps.search.EntityQuery;
import org.heed.openapps.search.EntityResultSet;
import org.heed.openapps.search.SearchAttribute;
import org.heed.openapps.search.SearchAttributeValue;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchService;
import org.heed.openapps.search.indexing.IndexingService;

import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.search.CountFacetRequest;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;


public class LuceneFacetedSearchService implements SearchService {
	private static final long serialVersionUID = 586282630873987158L;
	private final static Logger log = Logger.getLogger(LuceneFacetedSearchService.class.getName());
	@Autowired private PropertyService propertyService;
	@Autowired private SchedulingService schedulingService;
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private IndexingService indexingService;
	@Autowired private LuceneEntityQueryParser entityParser;
	@Autowired private LuceneSearchQueryParser searchParser;
	
	private Map<String,String> contentTypes;
	private Map<String,String> languages;	
	protected List<SearchPlugin> plugins = new ArrayList<SearchPlugin>();
	private Map<String, EntityIndexer> indexers = new HashMap<String, EntityIndexer>();
	
	public static final int MAX_RESULTS = 999999;
	
	
	public void initialize() {
		log.info("ArchiveManager Search Service starting...");
		contentTypes = getQNameLabels();
        languages = getLanguageLabels();
	}
	public void shutdown() {
		
	}
	
	@Override
	public EntityResultSet search(EntityQuery query) {
		EntityResultSet response = new EntityResultSet();	
		//long startTime = System.currentTimeMillis();
		int start = 0;
		int end = 100;
		if(query.getEndRow() > 0) {
			start = query.getStartRow();
			end = query.getEndRow();
		} else query.setEndRow(75);		
		if(query.getQueryString() == null) query.setQueryString("");
		int size = query.getEndRow() - query.getStartRow();
				
		DirectoryReader indexReader = null;
		IndexSearcher searcher = null;
		TaxonomyReader taxoReader = null;
	    try {
	    	String dir = propertyService.getPropertyValue("home.dir") + "/data/lucene";
	    	File indexFile = new File(dir, "index");
	    	if(!indexFile.exists()) indexFile.mkdir();	    	
	    	File facetFile = new File(dir, "facets");
	    	if(!facetFile.exists()) facetFile.mkdir();
	    	
	    	if(indexFile.list().length > 0) {
		    	Directory indexDir = FSDirectory.open(indexFile);
		    	Directory taxoDir = FSDirectory.open(facetFile);
		    	indexReader = DirectoryReader.open(indexDir);
			    searcher = new IndexSearcher(indexReader);
			    taxoReader = new DirectoryTaxonomyReader(taxoDir);
					    
			    //br.setOffset(query.getStartRow());				
				SortField[] sortFields;
			    if(query.getSorts() != null && query.getSorts().size() > 0) {
			    	sortFields = new SortField[query.getSorts().size()];
					for(int i=0; i < query.getSorts().size(); i++) {
						Sort s = query.getSorts().get(i);
						SortField field = new SortField(s.getField(), getSortFieldType(s.getType()), s.isReverse());
						sortFields[i] = field;
					}
				} else {
					sortFields = new SortField[]{new SortField("name_e", Type.STRING, false)};
				}
			    TopFieldCollector tdc = TopFieldCollector.create(new org.apache.lucene.search.Sort(sortFields), MAX_RESULTS, true,false,false,false);
			    
				query = query.getType() == EntityQuery.TYPE_SEARCH ?  searchParser.parse(query) : entityParser.parse(query);
				response.setExplanation(query.getNativeQuery().toString());
				
				if(query.fetchAttributes()) {
					FacetSearchParams fsp = new FacetSearchParams(
			                new CountFacetRequest(new CategoryPath("qname"), 100), 
			                new CountFacetRequest(new CategoryPath("source_assoc"), 100),
			                new CountFacetRequest(new CategoryPath("path"), 100),
			                new CountFacetRequest(new CategoryPath("language"), 100));
					 
					FacetsCollector fc = FacetsCollector.create(fsp, searcher.getIndexReader(), taxoReader);
					searcher.search((Query)query.getNativeQuery(), MultiCollector.wrap(tdc, fc));					
					doAttributes(response, fc);
					
				} else {
					searcher.search((Query)query.getNativeQuery(), tdc);
					//searcher.search(new MatchAllDocsQuery(), tdc);
				}			
				TopDocs topDocs = tdc.topDocs(start, size); 
				if(topDocs != null) {
					for(ScoreDoc hit : topDocs.scoreDocs) {
						try {
							Document document = searcher.doc(hit.doc);
							String id = document.get("id");
							if(id != null) {
								Entity entity = entityService.getEntity(Long.valueOf(id));
								response.getResults().add(entity);
							} else log.log(Level.SEVERE, "no entity returned for id:"+id);
						} catch(Exception e) {
							log.log(Level.SEVERE, "", e);
						}
					}
					response.setResultSize(topDocs.totalHits);
					response.setStartRow(start);
					response.setEndRow(end);
					//for(SearchPlugin plugin : plugins) {
						//plugin.response(request, response);
					//}				
				}
	    	}
	    	log.log(Level.INFO, query.getQueryExplanation());
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if(indexReader != null) indexReader.close();
				if(taxoReader != null) taxoReader.close();
			} catch(IOException e) {
				log.log(Level.SEVERE, "", e);
			}
		}		
		return response;
	}
	protected void doAttributes(EntityResultSet response, FacetsCollector fc) throws IOException {
		SearchAttribute qnameAttribute = new SearchAttribute("qname");
		SearchAttribute personAttribute = new SearchAttribute("person");
		SearchAttribute corporateAttribute = new SearchAttribute("corporate");
		SearchAttribute subjAttribute = new SearchAttribute("subject");
		SearchAttribute collectionAttribute = new SearchAttribute("collection");
		SearchAttribute languageAttribute = new SearchAttribute("language");
		
		List<FacetResult> facetResults = fc.getFacetResults();
		for(FacetResult facet : facetResults) {
			
			log.log(Level.INFO, facet.toString());
			/*
			FacetAccessible facets = result.getFacetMap().get(key);
			for(BrowseFacet facet : facets.getFacets()) {
				SearchAttributeValue value = new SearchAttributeValue(facet.getValue(), key+":"+facet.getValue(), facet.getFacetValueHitCount());
				if(key.equals("source_assoc")) {
					try {
						Entity entity = entityService.getEntity(Long.valueOf(facet.getValue()));
						if(entity != null) {
							String name = entity.getName();
							if(name != null) {
								value.setName(name);
								if(entity.getQName().equals(ClassificationModel.SUBJECT)) {
									value.setQuery("subj:"+entity.getId());
									subjAttribute.getValues().add(value);
								} else if(entity.getQName().equals(ClassificationModel.PERSON)) {
									value.setQuery("name:"+entity.getId());
									personAttribute.getValues().add(value);
								} else if(entity.getQName().equals(ClassificationModel.CORPORATION)) {
									value.setQuery("name:"+entity.getId());
									corporateAttribute.getValues().add(value);
								}
							}
						}
					} catch(Exception e) {
						//log.error("no entity found for source reference:"+facet.getValue());
					}
				} else if(key.equals("path")) {
					try {
						Entity entity = entityService.getEntity(Long.valueOf(facet.getValue()));
						if(entity != null && entity.getQName().equals(RepositoryModel.COLLECTION)) {
							String name = entity.getName();
							if(name != null) {
								value.setName(name);
								value.setQuery("path:"+entity.getId());
								collectionAttribute.getValues().add(value);
							}
						}
					} catch(Exception e) {
						
					}
				} else if(key.equals("qname")) {
					QName qname = new QName(value.getName());
					value.setQuery("localName:"+qname.getLocalName());
					qnameAttribute.getValues().add(value);
				} else if(key.equals("language")) {
					String language = facet.getValue();
					value.setQuery("language:"+language);
					languageAttribute.getValues().add(value);
				}
			}
			*/
		}				
		if(personAttribute.getValues().size() > 0) response.getAttributes().add(personAttribute);
		if(corporateAttribute.getValues().size() > 0) response.getAttributes().add(corporateAttribute);
		if(subjAttribute.getValues().size() > 0) response.getAttributes().add(subjAttribute);
		if(qnameAttribute.getValues().size() > 0) response.getAttributes().add(qnameAttribute);
		if(collectionAttribute.getValues().size() > 0) response.getAttributes().add(collectionAttribute);
		if(languageAttribute.getValues().size() > 0) response.getAttributes().add(languageAttribute);
			
		for(SearchAttribute attribute : response.getAttributes()) {
			if(attribute.getName().equals("qname")) {
				attribute.setName("Content Type");
				List<SearchAttributeValue> attributes = new ArrayList<SearchAttributeValue>();
				for(SearchAttributeValue value : attribute.getValues()) {
					String label = contentTypes.get(value.getName());
					if(label != null) {
						value.setName(label);
						attributes.add(value);
					}
				}
				attribute.setValues(attributes);
			} else if(attribute.getName().equals("language")) {
				attribute.setName("Language");
				List<SearchAttributeValue> attributes = new ArrayList<SearchAttributeValue>();
				for(SearchAttributeValue value : attribute.getValues()) {
					String label = languages.get(value.getName());
					if(label != null) {
						value.setName(label);
						attributes.add(value);
					}
				}
				attribute.setValues(attributes);
			}
			else if(attribute.getName().equals("subject")) attribute.setName("Subjects");
			else if(attribute.getName().equals("person")) attribute.setName("Personal Entities");
			else if(attribute.getName().equals("corporate")) attribute.setName("Corporate Entities");
			else if(attribute.getName().equals("collection")) attribute.setName("Collections");					
		}
		
	}
	@Override
	public int count(EntityQuery query) {
		//long startTime = System.currentTimeMillis();
		if(query.getQueryString() == null) query.setQueryString("");
		/*
		BrowseRequest br = new BrowseRequest();
	    BrowseHit[] hits = null;
		MultiBoboBrowser browseService = null;
		List<org.heed.openapps.search.indexing.IndexReader> readerList = null;
		try {
			query = entityParser.parse(query);
			br.setQuery((Query)query.getNativeQuery());
			readerList = indexingService.getIndexReaders();
			List<BoboSegmentReader> readers = new ArrayList<BoboSegmentReader>();
			for(org.heed.openapps.search.indexing.IndexReader r : readerList) {
				BoboSegmentReader boboReader = BoboSegmentReader.getInstance((BoboSegmentReader)r.getNativeIndexReader(), null, null);
				readers.add(boboReader);
			}
			browseService = new MultiBoboBrowser(BoboBrowser.createBrowsables(readers));		
			BrowseResult result = browseService.browse(br);
			hits = result.getHits();
			if(hits != null) {
				return hits.length;
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {			
			try {
				if(browseService != null){
					try {
						browseService.close();
					} catch(IOException e) {
						log.log(Level.SEVERE, "", e);
					}
				}
			} finally {
				if(readerList!=null){
					indexingService.returnIndexReaders(readerList);
				}
			}
		}
		*/	
		return 0;
	}
	@Override
	public void remove(Long id) {
		indexingService.remove(id);
	}
	
	protected Map<String,String> getLabels() {
		Map<String,String> labels = new HashMap<String,String>();
		labels.put("manuscript", "Manuscript");
		labels.put("correspondence", "Correspondence");
		labels.put("printed_material", "Printed Material");
		labels.put("audio", "Audio");
		labels.put("professional", "Professional Material");
		labels.put("memorabilia", "Memorabilia");
		labels.put("journals", "Journals");
		labels.put("scrapbooks", "Scrapbooks");
		labels.put("financial", "Financial");
		labels.put("legal", "Legal Material");
		labels.put("artwork", "Artwork");
		labels.put("photographs", "Photographs");
		labels.put("notebooks", "Notebooks");
		labels.put("medical", "Medical");
		labels.put("research", "Research");
		labels.put("miscellaneous", "Miscellaneous");
		labels.put("video", "Video");
		return labels;
	}
		
	
	@Override
	public void update(Entity entity) {		
		try {
			IndexEntity indexEntity = getIndexEntity(entity);
			indexingService.update(indexEntity);
		} catch(InvalidEntityException e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	@Override
	public void update(List<Entity> entities) {
		List<IndexEntity> indexEntities = new ArrayList<IndexEntity>();
		for(Entity entity : entities) {
			try {
				IndexEntity indexEntity = getIndexEntity(entity);
				indexEntities.add(indexEntity);
			} catch(InvalidEntityException e) {
				log.log(Level.SEVERE, "", e);
			}
		}
		indexingService.update(indexEntities);
	}
	@Override
	public Job update(QName qname) {
		EntityIndexingJob job = new EntityIndexingJob(dictionaryService, entityService, this, qname);
		schedulingService.run(job);
		return job;
	}
	protected IndexEntity getIndexEntity(Entity entity) throws InvalidEntityException {
		IndexEntity indexEntity = new IndexEntity(entity.getId());
		Model model = dictionaryService.getSystemDictionary().getModel(entity.getQName());
		if(model != null) {			
			/*
			if(model.isEntityIndexed()) {
				EntityPersistenceListener listener = entityService.getEntityPersistenceListener(entity.getQName().toString());
				indexEntity.getData().addAll(listener.index(entity));
			}
			*/
			//if(model.isSearchIndexed()) {
				EntityIndexer indexer = indexers.get(entity.getQName().toString());
				if(indexer == null) indexer = indexers.get("default");
				indexer.index(entity, indexEntity);
			//}
		}
		return indexEntity;
	}
	
	public void setIndexingService(IndexingService indexingService) {
		this.indexingService = indexingService;
	}
	
	public EntityIndexer getEntityIndexer(String name) {
		return indexers.get(name);
	}
	public void setIndexers(Map<String, EntityIndexer> indexers) {
		this.indexers = indexers;
	}
	public List<SearchPlugin> getPlugins() {
		return plugins;
	}
	public void setPlugins(List<SearchPlugin> plugins) {
		this.plugins = plugins;
	}
		
	protected Map<String,String> getLanguageLabels() {
		Map<String,String> labels = new HashMap<String,String>();
		labels.put("ar", "Arabic");
		labels.put("zh", "Chinese");
		labels.put("cs", "Czech");
		labels.put("da", "Danish");
		labels.put("nl", "Dutch");
		labels.put("en", "English");
		labels.put("fi", "Finnish");
		labels.put("fr", "French");
		labels.put("de", "German");
		labels.put("el", "Greek");
		labels.put("he", "Hebrew");
		labels.put("hu", "Hungarian");
		labels.put("is", "Icelandic");
		labels.put("it", "Italian");
		labels.put("ja", "Japanese");
		labels.put("ko", "Korean");
		labels.put("no", "Norwegian");
		labels.put("pl", "Polish");
		labels.put("pt", "Portugese");
		labels.put("ru", "Russian");
		labels.put("es", "Spanish");
		labels.put("sv", "Swedish");
		labels.put("th", "Thai");
		labels.put("tr", "Turkish");
		labels.put("yi", "Yiddish");
		return labels;
	}
	protected Map<String,String> getQNameLabels() {
		Map<String,String> labels = new HashMap<String,String>();
		labels.put("openapps_org_repository_1_0_manuscript", "Manuscript");
		labels.put("openapps_org_repository_1_0_correspondence", "Correspondence");
		labels.put("openapps_org_repository_1_0_printed_material", "Printed Material");
		labels.put("openapps_org_repository_1_0_audio", "Audio");
		labels.put("openapps_org_repository_1_0_professional", "Professional Material");
		labels.put("openapps_org_repository_1_0_memorabilia", "Memorabilia");
		labels.put("openapps_org_repository_1_0_journals", "Journals");
		labels.put("openapps_org_repository_1_0_scrapbooks", "Scrapbooks");
		labels.put("openapps_org_repository_1_0_financial", "Financial");
		labels.put("openapps_org_repository_1_0_legal", "Legal Material");
		labels.put("openapps_org_repository_1_0_artwork", "Artwork");
		labels.put("openapps_org_repository_1_0_photographs", "Photographs");
		labels.put("openapps_org_repository_1_0_notebooks", "Notebooks");
		labels.put("openapps_org_repository_1_0_medical", "Medical");
		labels.put("openapps_org_repository_1_0_research", "Research");
		labels.put("openapps_org_repository_1_0_miscellaneous", "Miscellaneous");
		labels.put("openapps_org_repository_1_0_video", "Video");
		return labels;
	}
	protected Type getSortFieldType(int type) {
		switch(type) {
		case Sort.INTEGER :
			return Type.INT;
		case Sort.LONG :
			return Type.LONG;
		case Sort.DOUBLE :
			return Type.DOUBLE;
		}
		return Type.STRING;
	}
	public void setSearchParser(LuceneSearchQueryParser searchParser) {
		this.searchParser = searchParser;
	}
	public void setEntityParser(LuceneEntityQueryParser entityParser) {
		this.entityParser = entityParser;
	}
	
}