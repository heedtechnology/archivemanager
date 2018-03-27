package org.archivemanager.search;

import org.heed.openapps.QName;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.Property;
import org.heed.openapps.search.Clause;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;


public class CollectionSearchProvider implements SearchPlugin {
	private EntityService entityService;
	
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	public void request(SearchRequest request) {
		if(request.getQname().equals(RepositoryModel.COLLECTION) && !request.getContext().equals("archive")) {
			if(request.getQuery() != null && request.getQuery().length() == 1) {
				request.setQuery("name_e:"+request.getQuery()+"*");
			}
			if(request.getContext() != null && request.getContext().length() > 0) {
				Clause clause = new Clause();
				clause.setOperator(Clause.OPERATOR_AND);
				clause.addProperty(new Property(RepositoryModel.CODE, request.getContext()));				
				request.addClause(clause);
			}
		}
	}
	@Override
	public void response(SearchRequest request, SearchResponse response) {
		for(SearchResult result : response.getResults()) {
			Association parent_assoc = result.getEntity().getTargetAssociation(RepositoryModel.CATEGORIES);
			if(parent_assoc == null) parent_assoc = result.getEntity().getTargetAssociation(RepositoryModel.ITEMS);
			if(parent_assoc != null && parent_assoc.getSource() != null) {
				try {
					Entity parent = entityService.getEntity(parent_assoc.getSource());
					while(parent != null) {
						QName parent_qname = parent.getQName();
						if(parent_qname.equals(RepositoryModel.COLLECTION)) {
							addCollection(result, parent);
							break;
						}
						parent_assoc = parent.getTargetAssociation(RepositoryModel.CATEGORIES);
						if(parent_assoc == null) parent_assoc = parent.getTargetAssociation(RepositoryModel.ITEMS);
						if(parent_assoc != null && parent_assoc.getSource() != null) {
							parent = entityService.getEntity(parent_assoc.getSource());
						} else parent = null;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	protected void addCollection(SearchResult result, Entity collection) {
		result.getData().put("collectionId", String.valueOf(collection.getId()));
		result.getData().put("collectionName", collection.getName());
	}
	
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
}
