/*
 * Copyright (C) 2010 Heed Technology Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.archivemanager.search.parsing.plugins;
import java.util.ArrayList;
import java.util.List;

import org.heed.openapps.QName;
import org.heed.openapps.dictionary.DataDictionary;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.dictionary.ModelField;
import org.heed.openapps.dictionary.ModelFieldValue;
import org.heed.openapps.search.Definition;
import org.heed.openapps.search.DictionaryPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.SearchService;
import org.heed.openapps.search.dictionary.PropertyValueDefinition;


public class PropertyValueDictionaryPlugin implements DictionaryPlugin {
	private DataDictionary dictionary;
	private SearchService searchService;
	private String entityName;
	private String propertyName;
	private int limit;
	
	
	public PropertyValueDictionaryPlugin(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public List<Definition> getDefinitions() {
		List<Definition> defs = new ArrayList<Definition>();
		try {
			QName entityQname = QName.createQualifiedName(entityName);
			QName propertyQname = QName.createQualifiedName(propertyName);
			Model model = dictionary.getModel(entityQname);
			ModelField field = model.getField(propertyQname);
			if(field != null && field.getValues().size() > 0) {
				for(ModelFieldValue value : field.getValues()) {
					defs.add(new PropertyValueDefinition(field.getQName(), value.getName(), field.getQName()+":"+value));
					
				}
			} else {
				SearchRequest eQuery = new SearchRequest(entityQname);
				SearchResponse results = searchService.search(eQuery);
				List<String> values = new ArrayList<String>();
				for(SearchResult result : results.getResults()) {
					String value = result.getEntity().getPropertyValue(propertyQname);
					if(!values.contains(value))
						values.add(value);
				}
				for(String value : values) {
					if(value != null && value.length() > 0)
						defs.add(new PropertyValueDefinition(field.getQName(), value, field.getQName().getLocalName()+":"+value));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return defs;
	}
	
	public String getEntity() {
		return entityName;
	}
	public void setEntity(String entityName) {
		this.entityName = entityName;
	}
	public String getProperty() {
		return propertyName;
	}
	public void setProperty(String propertyName) {
		this.propertyName = propertyName;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public void setSearchService(SearchService entitySvc) {
		this.searchService = entitySvc;
	}
	public void setDictionary(DataDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
}
