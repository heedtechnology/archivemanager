package org.archivemanager.data.out;
import java.util.List;

import org.heed.openapps.RepositoryModel;
import org.heed.openapps.entity.DefaultExportProcessor;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.data.FormatInstructions;


public class CollectionExportProcessor extends DefaultExportProcessor {
	private static final long serialVersionUID = 8533795347647880712L;

	@Override
	public Object export(FormatInstructions instructions, List<Entity> entities) throws InvalidEntityException {
		StringBuilder buff = new StringBuilder("id,name,identifier,collection_code\n");
		for(Entity entity : entities) {
			if(instructions.getFormat().equals(FormatInstructions.FORMAT_CSV)) 
				buff.append(toCsv(entity, instructions.printSources(), instructions.printTargets()));			
		}
		return buff.toString();
	}
	public String toCsv(Entity entity, boolean printSources, boolean printTargets) throws InvalidEntityException {
		StringBuffer buff = new StringBuffer("\""+entity.getId()+"\",\""+csvClean(entity.getName())+"\",");		
		try {
			if(entity.getPropertyValue(RepositoryModel.IDENTIFIER) != null) {
				Property property = entity.getProperty(RepositoryModel.IDENTIFIER);
				buff.append("\""+property.getValue()+"\",");
			} else buff.append("\"\",");
			if(entity.getPropertyValue(RepositoryModel.COLLECTION_CODE) != null) {
				Property property = entity.getProperty(RepositoryModel.COLLECTION_CODE);
				buff.append("\""+property.getValue()+"\",");
			} else buff.append("\"\",");			
		} catch(Exception e) {
			throw new InvalidEntityException("", e);
		}
		buff.append("\n");
		return buff.toString();
	}
}
