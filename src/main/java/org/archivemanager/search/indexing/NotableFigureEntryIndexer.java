package org.archivemanager.search.indexing;

import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.heed.openapps.entity.indexing.IndexField;


public class NotableFigureEntryIndexer extends DefaultEntityIndexer {

	
	@Override
	public void index(Entity entity, IndexEntity data) throws InvalidEntityException {
		super.index(entity, data);
		
		if(entity.hasProperty(ClassificationModel.COLLECTION_NAME)) {
			data.getFields().add(new IndexField(Property.STRING, "collection_name_e", entity.getPropertyValue(ClassificationModel.COLLECTION_NAME), false));
		}
	}
}
