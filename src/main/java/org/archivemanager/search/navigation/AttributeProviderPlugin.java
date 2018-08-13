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
package org.archivemanager.search.navigation;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.search.SearchAttribute;
import org.heed.openapps.search.SearchAttributeValue;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchService;


public abstract class AttributeProviderPlugin implements SearchPlugin {
	private static final Log log = LogFactory.getLog(AttributeProviderPlugin.class);
	protected EntityService entityService;
	protected DataDictionaryService dictionaryService;
	protected SearchService searchService;
	protected List<SearchAttribute> attributes = new ArrayList<SearchAttribute>();
	protected int limit;
	protected String sort;
	
	
	@Override
	public void request(SearchRequest request) {
		
	}
	@Override
	public void response(SearchRequest request, SearchResponse response) {
		if(request.hasAttributes()) {
			List<SearchAttribute> result = new ArrayList<SearchAttribute>();
			for(SearchAttribute attribute : attributes) {
				SearchAttribute att = new SearchAttribute(attribute.getName());
				List<SearchAttributeValue> values = new ArrayList<SearchAttributeValue>();
				for(SearchAttributeValue value : attribute.getValues()) {
					BitSet b = (BitSet)response.getBits().clone();
					if(value.getBits() != null) {
						b.and(value.getBits());
						if(b.cardinality() > 0) 
							values.add(new SearchAttributeValue(value.getName(), value.getQuery(), (int)b.cardinality()));
					} else log.info("no bitset available for "+attribute.getName());
				}
				SearchAttributeValueSorter sorter = attribute.getSort() != null && attribute.getSort().equals(SearchAttribute.COUNT_SORT) ?
					new SearchAttributeValueSorter(SearchAttribute.COUNT_SORT) : new SearchAttributeValueSorter(SearchAttribute.ALPHA_SORT);
				Collections.sort(values, sorter);
				if(limit == 0) limit = values.size();
				int resultLimit = (values.size() > limit) ? limit : values.size();
				for(int i=0; i < resultLimit; i++)
					att.getValues().add(values.get(i));
				result.add(att);
				response.getAttributes().add(att);				
			}			
		}
	}
	public List<SearchAttribute> provide(BitSet bits) {
		List<SearchAttribute> result = new ArrayList<SearchAttribute>();
		for(SearchAttribute attribute : attributes) {
			SearchAttribute att = new SearchAttribute(attribute.getName());
    		List<SearchAttributeValue> values = new ArrayList<SearchAttributeValue>();
			for(SearchAttributeValue value : attribute.getValues()) {
    			BitSet b = (BitSet)bits.clone();
    			if(value.getBits() != null) {
    				b.and(value.getBits());
    				if(b.cardinality() > 0) values.add(new SearchAttributeValue(value.getName(), value.getQuery(), (int)b.cardinality()));
    			}
    		}
			SearchAttributeValueSorter sorter = attribute.getSort().equals(SearchAttribute.COUNT_SORT) ?
					new SearchAttributeValueSorter(SearchAttribute.COUNT_SORT) : new SearchAttributeValueSorter(SearchAttribute.ALPHA_SORT);
			Collections.sort(values, sorter);
			int resultLimit = (values.size() > limit) ? limit : values.size();
			for(int i=0; i < resultLimit; i++)
				att.getValues().add(values.get(i));
    		result.add(att);
		}
		log.info("providing "+attributes.size()+" attributes for "+this.getClass().getName());
		return result;
	}
	
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public List<SearchAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<SearchAttribute> attributes) {
		this.attributes = attributes;
	}
	
}
