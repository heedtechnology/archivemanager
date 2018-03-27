package org.archivemanager.search.plugins;

import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;

public class TopDocumentScorePlugin implements SearchPlugin {

	@Override
	public void initialize() {
		
	}

	@Override
	public void request(SearchRequest request) {
		
	}

	@Override
	public void response(SearchRequest request, SearchResponse response) {
		if(response.getResults().size() > 0) {
			Float topScore = response.getTopScore();
			for(int i=0; i < response.getResults().size(); i++) {
				float score = response.getResults().get(i).getRawScore();
				double nScore = (double)(score/topScore)*100;
				int normScore = (int)nScore;
				response.getResults().get(i).setNormalizedScore(normScore);
			}
		}
	}

}
