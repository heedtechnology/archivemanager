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
import java.util.List;

import org.heed.openapps.QName;
import org.heed.openapps.search.SearchAttribute;
import org.heed.openapps.search.parsing.QueryParser;
import org.heed.openapps.search.parsing.QueryTokenizer;


public class DefinedAttributeProvider extends AttributeProviderPlugin {
	protected QueryParser parser;
	protected QueryTokenizer tokenizer;
	protected QName entityQName;
	
	
	public DefinedAttributeProvider(QueryParser parser, QueryTokenizer tokenizer, QName entityQName) {
		this.parser = parser;
		this.tokenizer = tokenizer;
		this.entityQName = entityQName;
	}
	public void initialize() {
		try {
        	for(SearchAttribute attribute : attributes) {
        		attribute.setSort(getSort());
        		//for(SearchAttributeValue value : attribute.getValues()) {
        			//SearchRequest request = new SearchRequest(entityQName, value.getQuery());
        			//SearchQuery query = parser.parse(request);
        			//OpenBitSet bits = entityService.getBitSet(entityQName, query.getValue());
        			//value.setBits(bits);
        		//}
        	}
        } catch(Exception e){
            e.printStackTrace();
        }
	}
	
	public void setSearchAttributes(List<SearchAttribute> attributes) {
		this.attributes.addAll(attributes);
	}
	
}
