package org.archivemanager.search.indexing;
import java.util.ArrayList;
import java.util.List;

import org.heed.openapps.QName;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.scheduling.JobSupport;
import org.heed.openapps.search.SearchService;


public class EntityIndexingJob extends JobSupport {
	private static final long serialVersionUID = -5348083461532426560L;
	private DataDictionaryService dictionaryService;
	private EntityService entityService;
	private SearchService searchService;
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
			List<Entity> entities = new ArrayList<Entity>();
			List<Model> models = dictionaryService.getSystemDictionary().getChildModels(qname);
			Model rootModel = dictionaryService.getSystemDictionary().getModel(qname);
			models.add(rootModel);		
			for(Model model : models) {
				entities.addAll(entityService.getEntities(model.getQName()));
			}
			setLastMessage("adding "+entities.size()+" "+qname.getLocalName()+"s to indexing queue.");
			
			double ratio = (double)entities.size() / BATCH_SIZE;
			int pages =	(int)(Math.ceil(ratio));
			for(int i=0; i < pages; i++) {
				int start = i*BATCH_SIZE;
				int end = (i*BATCH_SIZE) + BATCH_SIZE;
				if(entities.size() < end) end = entities.size();
				searchService.update(entities.subList(start, end));
				setLastMessage("search index added "+end+" of "+entities.size()+" "+qname.getLocalName()+"s");
			}
			setLastMessage(entities.size()+" "+qname.getLocalName()+"s indexed successfully");
			setComplete(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
