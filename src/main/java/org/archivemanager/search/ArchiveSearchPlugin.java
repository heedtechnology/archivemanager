package org.archivemanager.search;

import org.heed.openapps.QName;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.ClassificationModel;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;

public class ArchiveSearchPlugin implements SearchPlugin {
	
	private QName[] archiveQNames = new QName[]{RepositoryModel.COLLECTION,ClassificationModel.SUBJECT,ClassificationModel.PERSON,ClassificationModel.CORPORATION};
	
	@Override
	public void initialize() {}

	@Override
	public void request(SearchRequest request) {
		if(request.getContext() != null && request.getContext().equals("archive")) {
			request.setQnames(archiveQNames);
			request.addSort(new Sort(Sort.SCORE, "score", false));
		}
	}

	@Override
	public void response(SearchRequest request, SearchResponse response) {
		
		
	}

}
