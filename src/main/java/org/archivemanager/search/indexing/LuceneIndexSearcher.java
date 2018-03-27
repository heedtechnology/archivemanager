package org.archivemanager.search.indexing;

import org.heed.openapps.search.indexing.IndexSearcher;

public class LuceneIndexSearcher implements IndexSearcher {
	private org.apache.lucene.search.IndexSearcher searcher;
	
	public LuceneIndexSearcher(org.apache.lucene.search.IndexSearcher searcher) {
		this.searcher = searcher;
	}
	
	public org.apache.lucene.search.IndexSearcher getNativeIndexSearcher() {
		return searcher;
	}
}