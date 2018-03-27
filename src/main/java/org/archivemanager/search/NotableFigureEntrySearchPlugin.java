package org.archivemanager.search;

import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;

public class NotableFigureEntrySearchPlugin implements SearchPlugin {

	@Override
	public void initialize() {}

	@Override
	public void request(SearchRequest request) {
		if(request.getQname().equals(ClassificationModel.ENTRY)) {
			request.addSort(new Sort(Sort.STRING, "name_e", true));
			if(request.getQuery() != null && request.getQuery().length() > 0 && !request.getQuery().equals("all results")) {
				if(request.getQuery().contains(" "))
					request.setQuery("name:\""+request.getQuery()+"\"");
				else
					request.setQuery("name_e:"+request.getQuery()+"*");
			} else {
				String query = "";
				if(request.getRequestParameters().containsKey("name")) {
					if(request.getRequestParameters().get("name").length > 0) {
						String[] names = request.getRequestParameters().get("name");
						if(names != null && names.length > 0) {
							if(names[0].contains(" ")) {
								String[] parts = names[0].toLowerCase().split(" ");
								for(String part : parts) {
									query += " name:"+part+"";
								}
							} else {
								query += " name_e:"+names[0].toLowerCase()+"*";
							}
						}
					}
				}
				if(request.getRequestParameters().containsKey("description")) {
					if(request.getRequestParameters().get("description").length > 0) {
						String[] names = request.getRequestParameters().get("description");
						if(names != null && names.length > 0) {
							query += " items:"+names[0].toLowerCase();
						}
					}
				}
				if(request.getRequestParameters().containsKey("collection")) {
					if(request.getRequestParameters().get("collection").length > 0) {
						String[] names = request.getRequestParameters().get("collection");
						if(names != null && names.length > 0) {
							if(names[0].contains(" "))
								query += " collection_name:"+names[0].toLowerCase();
							else
								query += " collection_name:"+names[0].toLowerCase()+"*";
						}
					}
				}
				if(query .length() > 0) {
					request.setQuery(query);
				}
			}
		}
	}

	@Override
	public void response(SearchRequest request, SearchResponse response) {
		
	}

}
