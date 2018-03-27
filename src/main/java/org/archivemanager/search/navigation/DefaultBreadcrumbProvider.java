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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.heed.openapps.SystemModel;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.search.SearchNode;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.Token;
import org.heed.openapps.search.parsing.QueryTokenizer;
import org.heed.openapps.util.HTMLUtility;


public class DefaultBreadcrumbProvider extends BreadcrumbProviderPlugin {
	private static final Log log = LogFactory.getLog(DefaultBreadcrumbProvider.class);
	private QueryTokenizer tokenizer;
	private Map<String,String> labels;
	
	
	public DefaultBreadcrumbProvider() {
		labels = getLabels();
	}
	@Override
	public void request(SearchRequest request) {
		
	}
	public void response(SearchRequest request, SearchResponse response) {
		List<SearchNode> nodes = new ArrayList<SearchNode>();
		
		List<Token> tokens = tokenizer.tokenize(request.getQuery());
		String query2 = "";
		for(int i=0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			if(!token.getName().equals("All Results")) {
				SearchNode node = new SearchNode();	
				if(i != 0) query2 += " ";
				if(token.getName().equals("source_assoc") || token.getName().equals("target_assoc") || token.getName().equals("name") || token.getName().equals("subj") || token.getName().equals("path")) {
					query2 += token.getName()+":"+token.getValue();
					try {
						Entity entity = getEntityService().getEntity(Long.valueOf(token.getValue()));
						node.setLabel(entity.getPropertyValue(SystemModel.NAME));
					} catch(Exception e) {
						log.error("breadcrumb not found for : " + token.getValue());
					}
				} else if(token.getName().equals("localName")) {
					query2 += token.getName()+":"+token.getValue();
					String label = labels.get(token.getValue());
					if(label == null) label = token.getValue();
					node.setLabel(label);
				} else {
					node.setLabel(HTMLUtility.removeTags(token.getValue()));
					query2 += token.getValue();
				}
				if(node != null && node.getLabel() != null && node.getLabel().indexOf("date:[") > -1) {
					String[] term = node.getLabel().split("date:");
					StringBuffer buff = new StringBuffer();
					buff.append(term[1].substring(1, 5));
					buff.append(" - ");
					buff.append(term[1].substring(14, 18));
					node.setLabel(node.getLabel().substring(0, node.getLabel().indexOf("date:["))+" "+buff.toString());
				}
				node.setQuery(query2);
				nodes.add(node);
			}
		}
		/*
		String[] parts = request.getQuery().split("//");
		if(parts.length < 2) parts = request.getQuery().split(" ");
		String query2 = "";
		for(int i=0; i < parts.length; i++) {
			SearchNode node = new SearchNode();
			query2 += (i != 0) ? "//"+parts[i] : parts[i];			
			node.setQuery(query2);
			if(parts[i].startsWith("source_assoc:") || parts[i].startsWith("target_assoc:") || parts[i].startsWith("name:") || parts[i].startsWith("subj:") || parts[i].startsWith("path:")) {
				String[] term = parts[i].split(":");
				try {
					Entity entity = getEntityService().getEntity(Long.valueOf(term[1]));
					node.setLabel(entity.getPropertyValue(SystemModel.NAME));
				} catch(Exception e) {
					e.printStackTrace();
					node.setLabel(parts[i]);
				}
			} else if(parts[i].startsWith("localName:")) {
				String[] term = parts[i].split(":");
				String label = labels.get(term[1]);
				if(label == null) label = term[1];
				node.setLabel(label);
			} else node.setLabel(parts[i]);
			if(node.getLabel().indexOf("date:[") > -1) {
				String[] term = node.getLabel().split("date:");
				StringBuffer buff = new StringBuffer();
				buff.append(term[1].substring(1, 5));
				buff.append(" - ");
				buff.append(term[1].substring(14, 18));
				node.setLabel(node.getLabel().substring(0, node.getLabel().indexOf("date:["))+" "+buff.toString());
			}
			nodes.add(node);
		}
		*/
		response.setBreadcrumb(nodes);
	}
	protected Map<String,String> getLabels() {
		Map<String,String> labels = new HashMap<String,String>();
		labels.put("manuscript", "Manuscript");
		labels.put("correspondence", "Correspondence");
		labels.put("printed_material", "Printed Material");
		labels.put("audio", "Audio");
		labels.put("professional", "Professional Material");
		labels.put("memorabilia", "Memorabilia");
		labels.put("journals", "Journals");
		labels.put("scrapbooks", "Scrapbooks");
		labels.put("financial", "Financial");
		labels.put("legal", "Legal Material");
		labels.put("artwork", "Artwork");
		labels.put("photographs", "Photographs");
		labels.put("notebooks", "Notebooks");
		labels.put("medical", "Medical");
		labels.put("research", "Research");
		labels.put("miscellaneous", "Miscellaneous");
		labels.put("video", "Video");
		return labels;
	}
	public void setTokenizer(QueryTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	
}
