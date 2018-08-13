package org.archivemanager.search.indexing;
import java.util.List;
import java.util.logging.Logger;

import org.heed.openapps.QName;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.scheduling.JobSupport;
import org.heed.openapps.search.SearchService;


public class EntityIndexingJob extends JobSupport {
	private static final long serialVersionUID = -5348083461532426560L;
	private final static Logger log = Logger.getLogger(EntityIndexingJob.class.getName());
	private SearchService searchService;
	private EntityService entityService;
	private DataDictionaryService dictionaryService;
	private QName qname;
		
	public static final int BATCH_SIZE = 20;
	
	public EntityIndexingJob(DataDictionaryService dictionaryService, EntityService entityService, SearchService searchService, QName qname) {
		this.dictionaryService = dictionaryService;
		this.entityService = entityService;
		this.searchService = searchService;
		this.qname = qname;
	}
	
	@Override
	public void execute() {
		super.execute();
		try {						
			List<Model> models = dictionaryService.getSystemDictionary().getChildModels(qname);
			Model rootModel = dictionaryService.getSystemDictionary().getModel(qname);
			models.add(rootModel);		
			for(Model model : models) {
				int count = entityService.count(model.getQName());
				double ratio = (double)count / BATCH_SIZE;
				int pages =	(int)(Math.ceil(ratio));
				
				for(int i=0; i < pages; i++) {
					int end = (i*BATCH_SIZE) + BATCH_SIZE;
					EntityResultSet results = entityService.getEntities(model.getQName(), i, BATCH_SIZE);				
					
					searchService.update(results.getData());
					setLastMessage("search index added "+end+" of "+count+" "+model.getQName().getLocalName()+"s");
				}
				setLastMessage(count+" "+qname.getLocalName()+"s indexed successfully");
			}			
			setComplete(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setLastMessage(String msg) {
		log.info(msg);
		super.setLastMessage(msg);
	}
}
