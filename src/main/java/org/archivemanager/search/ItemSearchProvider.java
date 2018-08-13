package org.archivemanager.search;

import org.heed.openapps.SystemModel;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.Property;
import org.heed.openapps.search.Clause;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.SearchService;


public class ItemSearchProvider  implements SearchPlugin {
	protected EntityService entityService;
	protected SearchService searchService;
	
	
	@Override
	public void initialize() {
		
	}

	@Override
	public void request(SearchRequest request) {
		if(request.getContext() != null && request.getContext().length() > 0 && request.getQname().equals(RepositoryModel.ITEM) && !request.getContext().equals("archive")) {			
			SearchRequest sQuery = new SearchRequest(RepositoryModel.COLLECTION, "code:"+request.getContext());
			sQuery.setAttributes(false);
			sQuery.setStartRow(0);
			sQuery.setEndRow(100);
			
			SearchResponse collectionResults = searchService.search(sQuery);
			if(collectionResults.getResults().size() == 1) {
				SearchResult collection = collectionResults.getResults().get(0);
				request.addParameter("path", String.valueOf(collection.getId()));
			} else {
				Clause clause = new Clause();
				clause.setOperator(Clause.OPERATOR_OR);
				for(SearchResult collection : collectionResults.getResults()) {
					clause.addProperty(new Property(SystemModel.PATH, String.valueOf(collection.getId())));
				}
				request.addClause(clause);
			}
		}
	}
	protected void search() {
		/*
		 * EntityQuery eQuery = new EntityQuery(RepositoryModel.COLLECTION, RepositoryModel.CODE.toString()+":"+request.getContext());		
			eQuery.setType(EntityQuery.TYPE_LUCENE);
			eQuery.setStartRow(0);
			eQuery.setEndRow(10);
			EntityResultSet results = getEntityService().search(eQuery);
			
			if(results.getResults().size() == 1) {
				Entity collection = results.getResults().get(0);
				request.addParameter("path", String.valueOf(collection.getId()));
			} else {
				Clause clause = new Clause();
				clause.setOperator(Clause.OPERATOR_OR);
				for(Entity collection : results.getResults()) {
					clause.addParamater(new Parameter("path", String.valueOf(collection.getId())));
				}
				request.addClause(clause);
			}
			
		*/
	}
	@Override
	public void response(SearchRequest request, SearchResponse response) {
		
	}
	
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	public SearchService getSearchService() {
		return searchService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
}
