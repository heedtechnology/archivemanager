package org.archivemanager.data;
import java.util.Map;

import org.heed.openapps.content.DigitalObjectService;
import org.heed.openapps.content.FileNode;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.DefaultExportProcessor;
import org.heed.openapps.entity.data.FormatInstructions;


public class DigitalObjectExportProcessor extends DefaultExportProcessor {
	private static final long serialVersionUID = 4955815630883571562L;
	private DigitalObjectService digitalObjectService;
	
	@Override
	public Map<String, Object> exportMap(FormatInstructions format, Association association) {
		Map<String, Object> map = super.exportMap(format, association);
		try {
			if(association != null) {
				FileNode fileNode = digitalObjectService.getDigitalObject(association.getTarget());
				if(fileNode != null) {
					String contentType = fileNode.getContentType();
					int width = fileNode.getWidth();
					int height = fileNode.getHeight();
						
					if(contentType != null) map.put("content_type", contentType);
					if(width != 0) map.put("width", width);
					if(height != 0) map.put("height", height);
					map.put("size", fileNode.getSize());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public DigitalObjectService getDigitalObjectService() {
		return digitalObjectService;
	}

	public void setDigitalObjectService(DigitalObjectService digitalObjectService) {
		this.digitalObjectService = digitalObjectService;
	}
	
}
