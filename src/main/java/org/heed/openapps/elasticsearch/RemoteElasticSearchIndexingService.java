package org.heed.openapps.elasticsearch;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.heed.openapps.entity.indexing.IndexField;
import org.heed.openapps.search.indexing.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteElasticSearchIndexingService implements IndexingService {
	private final static Logger log = Logger.getLogger(RemoteElasticSearchIndexingService.class.getName());
	@Autowired RestHighLevelClient client;
	
	
	@Override
	public void index(IndexEntity entity) {
		IndexRequest request = getIndexRequest(entity);
		try {
			client.index(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public void index(List<IndexEntity> entities) {
		BulkRequest request = new BulkRequest();
		for(IndexEntity entity : entities) {
			request.add(getIndexRequest(entity));
		}
		try {
			client.bulk(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public void remove(Long id) {
		DeleteRequest request = new DeleteRequest(
				"nodes", 
		        "node",  
		        String.valueOf(id));
		try {
			client.delete(request);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}
	
	protected IndexRequest getIndexRequest(IndexEntity entity) {
		IndexRequest request = new IndexRequest(
		        "nodes", 
		        "node",  
		        String.valueOf(entity.getId()));  
		Map<String, Object> jsonMap = new HashMap<>();
		for(IndexField field : entity.getFields()) {
			jsonMap.put(field.getName(), field.getValue());
		}
		jsonMap.put("freeText", entity.getFreeText());		
		request.source(jsonMap);
		request.opType(IndexRequest.OpType.INDEX);
		return request;
	}
}
