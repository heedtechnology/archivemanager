package org.archivemanager.data.out;
import java.util.List;

import org.heed.openapps.RepositoryModel;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.DefaultExportProcessor;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.data.FormatInstructions;


public class SubjectExportProcessor extends DefaultExportProcessor {
	private static final long serialVersionUID = 602680215675344700L;

	
	@Override
	public Object export(FormatInstructions instructions, List<Entity> entities) throws InvalidEntityException {
		StringBuilder buff = new StringBuilder("id,name,type,source,collections\n");
		for(Entity entity : entities) {
			if(instructions.getFormat().equals(FormatInstructions.FORMAT_CSV)) 
				buff.append(toCsv(entity, instructions.printSources(), instructions.printTargets()));			
		}
		return buff.toString();
	}
	public String toCsv(Entity entity, boolean printSources, boolean printTargets) throws InvalidEntityException {
		StringBuffer buff = new StringBuffer("\""+entity.getId()+"\",\""+clean(entity.getName())+"\",");
		try {
			if(entity.getPropertyValue(ClassificationModel.TYPE) != null) {
				Property property = entity.getProperty(ClassificationModel.TYPE);
				buff.append("\""+property.getValue()+"\",");
			} else buff.append("\"\",");
			if(entity.getPropertyValue(ClassificationModel.SOURCE) != null) {
				Property property = entity.getProperty(ClassificationModel.SOURCE);
				buff.append("\""+property.getValue()+"\",");
			} else buff.append("\"\",");
			
			StringBuffer subjBuff = new StringBuffer("\"");
			List<Association> associations = entity.getTargetAssociations(ClassificationModel.SUBJECTS);
			for(Association assoc : associations) {
				if(assoc.getSourceName().equals(RepositoryModel.COLLECTION)) {
					Entity subject = assoc.getSourceEntity();
					subjBuff.append("["+subject.getName()+"],");
				}
			}
			subjBuff.append("\"");
			buff.append(subjBuff.toString());
		} catch(Exception e) {
			throw new InvalidEntityException("", e);
		}
		buff.append("\n");
		return buff.toString();
	}
}
