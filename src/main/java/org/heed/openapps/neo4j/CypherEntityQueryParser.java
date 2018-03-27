package org.heed.openapps.neo4j;
import java.io.StringWriter;

import org.heed.openapps.entity.Property;
import org.heed.openapps.search.EntityQuery;


public class CypherEntityQueryParser {
	
	
	public String parse(EntityQuery query) {
		StringWriter cypher = new StringWriter();
		cypher.append(parseSelectStmt(query));
		
		if(query.getSorts() != null && query.getSorts().size() > 0) {
			//cypher.append(" AND has(n."+query.getSorts().getField()+")");
			cypher.append(" RETURN n");
			/*
			if(query.getSort().isReverse()) {
				cypher.append(" ORDER BY n."+query.getSort().getField()+" DESC");
			} else if(!query.getSort().isReverse()) {
				cypher.append(" ORDER BY n."+query.getSort().getField()+" ASC");
			}
			*/			
		} else cypher.append(" RETURN n");
		if(query.getEndRow() > 0) cypher.append(" SKIP "+query.getStartRow()+" LIMIT "+query.getEndRow());
		return cypher.toString();
	}
	public String parseCountQuery(EntityQuery query) {
		StringWriter cypher = new StringWriter();
		cypher.append(parseSelectStmt(query));
		cypher.append(" RETURN count(n)");
		return cypher.toString();
	}
	
	protected String parseSelectStmt(EntityQuery query) {
		StringWriter cypher = new StringWriter();
		if(query.getEntityQnames()[0] != null) {
			cypher.append("MATCH (n {qname:'"+query.getEntityQnames()[0]+"'})");
		} else {
			cypher.append("MATCH (n)");
		}
		if(query.getEntityQnames()[0] != null || !query.getProperties().isEmpty() || (query.getQueryString() != null && query.getQueryString().length() > 0)) {
			cypher.append(" WHERE");
			if(query.getUser() != null) {
				cypher.append(" ("
						+ "(has(n.user) AND n.user = '"+query.getUser()+"') OR "
						+ "(has(n.openapps_org_repository_1_0_isPublic) AND n.openapps_org_repository_1_0_isPublic = 'true')"
					+ ")"
				);
			}			
			if(!query.getProperties().isEmpty()) {
				for(int i=0; i < query.getProperties().size(); i++) {
					Property prop = query.getProperties().get(i);
					if(i == 0 && query.getUser() != null) cypher.append(" (has(n."+prop.getQName().toString()+") AND n."+prop.getQName().toString()+" = '"+prop.getValue()+"')");
					else cypher.append(" AND (has(n."+prop.getQName().toString()+") AND n."+prop.getQName().toString()+" = '"+prop.getValue()+"')");
				}
			}
			if(query.getQueryString() != null && query.getQueryString().length() > 0) {
				if(!query.getProperties().isEmpty() || query.getUser() != null) cypher.append(" AND");
				cypher.append(" (has(n.openapps_org_system_1_0_name) AND");
				cypher.append(" (");
				String[] tokens = tokenize(query.getQueryString());
				for(int i=0; i < tokens.length; i++) {
					String token = tokens[i];
					if(token.length() > 0) cypher.append(" n.openapps_org_system_1_0_name =~ '(?i).*\\\\b"+token+"\\\\b.*'");
					if(i < tokens.length - 1) {
						cypher.append(" AND");
					}
				}
				cypher.append("))");
			} else {
				if(!query.getProperties().isEmpty() || query.getUser() != null) cypher.append(" AND");
				cypher.append(" has(n.openapps_org_system_1_0_name)");
			}
		}
		return cypher.toString();
	}
	protected String[] tokenize(String in) {
		if(in == null || in.length() == 0) return new String[0];
		String stripped = in.replace("#", "");
		return stripped.split(" ");
	}
}
