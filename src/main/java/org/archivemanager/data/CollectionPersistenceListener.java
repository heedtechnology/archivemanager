package org.archivemanager.data;
import org.heed.openapps.dictionary.DataDictionary;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.service.DefaultEntityPersistenceListener;


public class CollectionPersistenceListener extends DefaultEntityPersistenceListener {
  public CollectionPersistenceListener() {}
  
  public void onAfterAdd(Entity entity)
  {
    DataDictionary sourceDictionary = dictionaryService.getDataDictionary(entity.getDictionary());
    Model sourceModel = sourceDictionary.getModel(entity.getQName());
    if (sourceModel.isEntityIndexed()) {
      searchService.update(entity);
    }
  }
  
  public void onAfterUpdate(Entity entity)
  {
    DataDictionary sourceDictionary = dictionaryService.getDataDictionary(entity.getDictionary());
    Model sourceModel = sourceDictionary.getModel(entity.getQName());
    if (sourceModel.isEntityIndexed()) {
      searchService.update(entity);
    }
  }
  
  public void onAfterDelete(Entity entity)
  {
    DataDictionary sourceDictionary = dictionaryService.getDataDictionary(entity.getDictionary());
    Model sourceModel = sourceDictionary.getModel(entity.getQName());
    if (sourceModel.isEntityIndexed()) {
      searchService.remove(entity.getId());
    }
  }
  
  public void onAfterAssociationAdd(Association association)
  {
    if (association.getQName().equals(org.heed.openapps.search.SearchModel.DEFINITIONS)) {
      try {
        Entity collection = entityService.getEntity(null, association.getSource().longValue());
        searchService.update(collection);
      } catch (InvalidEntityException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void onAfterAssociationDelete(Association association)
  {
    if (association.getQName().equals(org.heed.openapps.search.SearchModel.DEFINITIONS)) {
      try {
        Entity collection = entityService.getEntity(null, association.getSource().longValue());
        searchService.update(collection);
      } catch (InvalidEntityException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void onAfterAssociationUpdate(Association association) {}
  
  public void onBeforeAdd(Entity arg0) {}
  
  public void onBeforeAssociationAdd(Association association) {}
  
  public void onBeforeAssociationDelete(Association association) {}
  
  public void onBeforeAssociationUpdate(Association association) {}
  
  public void onBeforeDelete(Entity arg0) {}
  
  public void onBeforeUpdate(Entity arg0, Entity arg1) {}
}