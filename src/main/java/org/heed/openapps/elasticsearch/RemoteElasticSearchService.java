package org.heed.openapps.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.search.SortField.Type;
import org.heed.openapps.QName;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityIndexingJob;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.indexing.EntityIndexer;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.heed.openapps.scheduling.Job;
import org.heed.openapps.scheduling.SchedulingService;
import org.heed.openapps.search.SearchAttribute;
import org.heed.openapps.search.SearchAttributeValue;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.SearchService;
import org.heed.openapps.search.indexing.IndexingService;
import org.heed.openapps.search.parsing.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;


public class RemoteElasticSearchService implements SearchService {
	private static final long serialVersionUID = 586282630873987158L;
	private final static Logger log = Logger.getLogger(RemoteElasticSearchService.class.getName());
	@Autowired private SchedulingService schedulingService;
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private IndexingService indexingService;
	@Autowired private QueryParser searchParser;
	@Autowired private RestHighLevelClient client;
	
	protected Map<String,String> labels = getContentTypeLabels();
	protected List<SearchPlugin> plugins = new ArrayList<SearchPlugin>();
	private Map<String, EntityIndexer> indexers = new HashMap<String, EntityIndexer>();
	
	public static final int MAX_RESULTS = 999999;
	
		
	@Override
	public SearchResponse search(SearchRequest query) {
		SearchResponse response = new SearchResponse();	
		//long startTime = System.currentTimeMillis();
		int start = 0;
		int end = 10;
		if(query.getEndRow() > 0) {
			start = query.getStartRow();
			end = query.getEndRow();
		} else query.setEndRow(75);		
		if(query.getQuery() == null) query.setQuery("");
		int size = query.getEndRow() - query.getStartRow();
		
		org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest("nodes");
		searchRequest.scroll(TimeValue.timeValueSeconds(1000));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();		
		searchSourceBuilder.size(size);
		//searchSourceBuilder.from(start);
		/*
		if(query.getSorts() != null && query.getSorts().size() > 0) {
	    	for(int i=0; i < query.getSorts().size(); i++) {
				Sort s = query.getSorts().get(i);
				SortOrder order = s.isReverse() ? SortOrder.ASC : SortOrder.DESC;
				searchSourceBuilder.sort(new FieldSortBuilder(s.getField()).order(order));
			}
		} else {
			SortOrder order = SortOrder.DESC;
			searchSourceBuilder.sort(new FieldSortBuilder(SystemModel.NAME.toString()).order(order));
		}
		*/
		if(query.hasAttributes()) {								
			TermsAggregationBuilder qnameAggregation = AggregationBuilders.terms("qname").field("qname");
			qnameAggregation.size(10);			
			searchSourceBuilder.aggregation(qnameAggregation);
			TermsAggregationBuilder sourceAggregation = AggregationBuilders.terms("source_assoc").field("source_assoc");
			sourceAggregation.size(50);
			searchSourceBuilder.aggregation(sourceAggregation);
		}
		
		try {
			query = searchParser.parse(query);
			QueryBuilder queryBuilder = (QueryBuilder)query.getNativeQuery();
			searchSourceBuilder.query(queryBuilder);			
			searchRequest.source(searchSourceBuilder);
						
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			response.setSearchId(searchResponse.getScrollId());
			
			boolean moreResultsExist = true;
		    int resultCount = 0;
		    while(moreResultsExist) {
		        String scrollId = searchResponse.getScrollId();
		        for (SearchHit hit : searchResponse.getHits()) {
		        	if(resultCount >= start && resultCount <= end) {
			        	String id = hit.getId();
						if(id != null) {
							Entity entity = entityService.getEntity(Long.valueOf(id));
							response.getResults().add(new SearchResult(entity));
						} else log.log(Level.SEVERE, "no entity returned for id:"+id);			            
		        	}
		        	resultCount++;
		        }
		        if(resultCount >= searchResponse.getHits().getTotalHits() || resultCount >= end) {
		            moreResultsExist = false;
		            ClearScrollRequest request = new ClearScrollRequest();
		            request.addScrollId(scrollId);
		            client.clearScroll(request);
		            
		            response.setResultSize((int)searchResponse.getHits().getTotalHits());
					response.setStartRow(start);
					response.setEndRow(end);
					for(SearchPlugin plugin : plugins) {
						plugin.response(query, response);
					}					
		            break;
		        }
		        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
		        scrollRequest.scroll(TimeValue.timeValueSeconds(1000));
		        searchResponse = client.searchScroll(scrollRequest);
		    }
		    if(query.hasAttributes() && searchResponse.getAggregations() != null) {
			    SearchAttribute qnameAttribute = new SearchAttribute("Content Type");			    
			    Terms qnameAggregation = searchResponse.getAggregations().get("qname");
			    for(Bucket bucket : qnameAggregation.getBuckets()) {
			    	if(bucket.getDocCount() != response.getResultSize()) {			    		
			    		QName qname = new QName(bucket.getKeyAsString());
			    		SearchAttributeValue value = new SearchAttributeValue(labels.get(qname.getLocalName()));
						//value.setAttribute(qnameAttribute);
			    		value.setQuery("qname:"+qname.toString());
						value.setCount((int)bucket.getDocCount());
						qnameAttribute.getValues().add(value);					
			    	}
			    }
			    if(qnameAttribute.getValues().size() > 0) response.getAttributes().add(qnameAttribute);
			    
			    SearchAttribute personAttribute = new SearchAttribute("Personal Entities");
				SearchAttribute corporateAttribute = new SearchAttribute("Corporate Entities");
				SearchAttribute subjAttribute = new SearchAttribute("Subjects");
				Terms sourceAggregation = searchResponse.getAggregations().get("source_assoc");
				for(Bucket bucket : sourceAggregation.getBuckets()) {
					try {
						long entityId = Long.valueOf(bucket.getKeyAsString());
						Entity entity = entityService.getEntity(entityId, false, false);
						if(entity != null) {
							SearchAttributeValue value = new SearchAttributeValue(entity.getName());
							String name = entity.getName();
							if(name != null) {
								value.setCount((int)bucket.getDocCount());
								value.setName(name);
								if(entity.getQName().equals(ClassificationModel.SUBJECT)) {
									//value.setAttribute(subjAttribute);
									value.setQuery("subj:"+entity.getId());
									subjAttribute.getValues().add(value);
								} else if(entity.getQName().equals(ClassificationModel.PERSON)) {
									//value.setAttribute(personAttribute);
									value.setQuery("name:"+entity.getId());
									personAttribute.getValues().add(value);
								} else if(entity.getQName().equals(ClassificationModel.CORPORATION)) {
									//value.setAttribute(corporateAttribute);
									value.setQuery("name:"+entity.getId());
									corporateAttribute.getValues().add(value);
								}
							}
						}
					} catch(Exception e) {
						//log.error("no entity found for source reference:"+facet.getValue());
					}
				}
				if(personAttribute.getValues().size() > 0) response.getAttributes().add(personAttribute);
				if(corporateAttribute.getValues().size() > 0) response.getAttributes().add(corporateAttribute);
				if(subjAttribute.getValues().size() > 0) response.getAttributes().add(subjAttribute);
		    }
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		
		return response;
	}
	/*
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
	*/
	@Override
	public int count(SearchRequest query) {
		//long startTime = System.currentTimeMillis();
		if(query.getQuery() == null) query.setQuery("");
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
		//indexingService.remove(id);
	}
	
	protected Map<String,String> getContentTypeLabels() {
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
			indexingService.index(indexEntity);
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
		indexingService.index(indexEntities);
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
		//this.indexingService = indexingService;
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
	
}