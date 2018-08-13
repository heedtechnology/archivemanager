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

import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.search.SearchService;
import org.heed.openapps.search.Definition;
import org.heed.openapps.search.DictionaryPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.dictionary.SubjectDefinition;


public class SubjectDictionaryPlugin implements DictionaryPlugin {
	private SearchService searchService;
	
	
	public List<Definition> getDefinitions() {
		List<Definition> defs = new ArrayList<Definition>();
		try {
			SearchResponse subjects = searchService.search(new SearchRequest(ClassificationModel.SUBJECT, null, "name", true));
			if(subjects != null) {
				for(SearchResult subject : subjects.getResults()) {
					SubjectDefinition def = new SubjectDefinition(subject.getEntity().getName(), String.valueOf(subject.getId()));
					defs.add(def);
					/*
					String search_values = name.getProperty(ClassificationModel.SUBJECT_SEARCH_VALUES).toString();
					if(search_values != null && !search_values.equals("")) {
						String[] values = search_values.split(",");
						for(int i=0; i < values.length; i++) {
							NamedEntityDefinition def = new NamedEntityDefinition(values[i].trim(), String.valueOf(id));
							defs.add(def);
						}
					}
					*/
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return defs;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
}
