package org.heed.openapps.elasticsearch;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/*
 * PUT my_index/_mapping/_doc
	{
	  "properties": {
	    "my_field": {"type":"text","fielddata": true
	    }
	  }
	}
 */
@JsonSerialize
public class FieldMappings {
	private Map<String,Object> properties = new HashMap<String,Object>();
	
	
	public FieldMappings() {
		Map<String,Object> qname = new HashMap<String,Object>();
		qname.put("type", "text");
		qname.put("fielddata", true);		
		properties.put("qname", qname);
	}


	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}	
	
}
