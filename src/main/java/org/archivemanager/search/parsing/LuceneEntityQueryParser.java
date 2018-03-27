package org.archivemanager.search.parsing;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.heed.openapps.QName;
import org.heed.openapps.entity.Property;
import org.heed.openapps.search.Clause;
import org.heed.openapps.search.EntityQuery;


public class LuceneEntityQueryParser {
	
	
	public EntityQuery parse(EntityQuery entityQuery) throws ParseException {
		BooleanQuery query = new BooleanQuery();
		if(entityQuery.getEntityQnames().length == 1)
			query.add(new BooleanClause(new TermQuery(new Term("qname", entityQuery.getEntityQnames()[0].toString())), Occur.MUST));
		else {
			BooleanQuery qnameQuery = new BooleanQuery();
			for(QName qname : entityQuery.getEntityQnames()) {
				qnameQuery.add(new BooleanClause(new TermQuery(new Term("qname", qname.toString())), Occur.SHOULD));
			}
			query.add(qnameQuery, Occur.MUST);
		}
		
		/** User clause **/
		if(entityQuery.getUser() != null) {
			BooleanQuery userQuery = new BooleanQuery();			
			//userQuery.add(new BooleanClause(new TermQuery(new Term("target_assoc", String.valueOf(entityQuery.getUser()))), Occur.SHOULD));
			userQuery.add(new BooleanClause(new TermQuery(new Term("acl", String.valueOf(entityQuery.getUser().getId()))), Occur.SHOULD));
			userQuery.add(new BooleanClause(new TermQuery(new Term("user", String.valueOf(entityQuery.getUser().getId()))), Occur.SHOULD));
			for(Long groupId : entityQuery.getGroups()) {
				userQuery.add(new BooleanClause(new TermQuery(new Term("acl", String.valueOf(groupId))), Occur.SHOULD));
			}
			query.add(userQuery, Occur.MUST);
		}
		/** Cross-Reference id **/
		if(entityQuery.getXid() > 0) {
			query.add(new TermQuery(new Term("xid", String.valueOf(entityQuery.getXid()))), Occur.MUST);
		}
		/** Deleted clause **/
		if(entityQuery.isDeleted() != null) {
			if(entityQuery.isDeleted()) 
				query.add(new TermQuery(new Term("deleted", "true")), Occur.MUST);
			else 
				query.add(new TermQuery(new Term("deleted", "true")), Occur.MUST_NOT);
		}
		if(entityQuery.getQueryString() != null && entityQuery.getQueryString().length() > 0) {			
			if(entityQuery.getFields().length == 0) entityQuery.setFields(new String[] {"openapps_org_system_1_0_name"});
			String[] chunks = entityQuery.getQueryString().split(" ");
			if(chunks.length == 1 && entityQuery.getFields().length == 1) {
				if(entityQuery.getFields()[0].equals("uuid")) {
					TermQuery termQuery = new TermQuery(new Term(entityQuery.getFields()[0],chunks[0]));
					entityQuery.setNativeQuery(termQuery);
					return entityQuery;
				}
				String name = entityQuery.getFields()[0];				
				Query q = null;
				if(name.endsWith("_e")) 
					q = new PrefixQuery(new Term(name,chunks[0].toLowerCase()));
				else
					q = new TermQuery(new Term(name,chunks[0].toLowerCase()));
				query.add(q, Occur.MUST);
				entityQuery.setNativeQuery(query);
				return entityQuery;
			} else {
				BooleanQuery searchQuery = new BooleanQuery();
				if(entityQuery.tokenize()) {
					for(int i=0; i < chunks.length; i++) {
						BooleanQuery termQuery = new BooleanQuery();
						for(String field : entityQuery.getFields()) {
							Query q = null;
							if(field.endsWith("_e")) 
								q = new PrefixQuery(new Term(field,chunks[i].toLowerCase()));
							else
								q = new TermQuery(new Term(field,chunks[i].toLowerCase()));
							if(q != null) {
								termQuery.add(q, Occur.SHOULD);								
							}
						}
						if(entityQuery.getDefaultOperator().equals("AND")) searchQuery.add(termQuery, Occur.MUST);
						else searchQuery.add(termQuery, Occur.SHOULD);
					}
				} else {
					for(String field : entityQuery.getFields()) {
						PhraseQuery phrase = new PhraseQuery();
						for(int i=0; i < chunks.length; i++) {
							phrase.add(new Term(field,chunks[i].toLowerCase()));
						}
						if(phrase != null) {
							if(entityQuery.getDefaultOperator().equals("AND")) searchQuery.add(phrase, Occur.MUST);
							else searchQuery.add(phrase, Occur.SHOULD);
						}
					}
				}
				query.add(searchQuery, Occur.MUST);
			}			
		}
		if(entityQuery.getClauses().size() > 0) {			
			for(Clause clause : entityQuery.getClauses()) {
				BooleanQuery clauseQuery = new BooleanQuery();
				for(Property parm : clause.getProperties()) {
					if(clause.getOperator().equals(Clause.OPERATOR_OR))
						clauseQuery.add(new BooleanClause(new TermQuery(new Term(parm.getQName().toString(), String.valueOf(parm.getValue()))), Occur.SHOULD));
					else if(clause.getOperator().equals(Clause.OPERATOR_AND))
						clauseQuery.add(new BooleanClause(new TermQuery(new Term(parm.getQName().toString(), String.valueOf(parm.getValue()))), Occur.MUST));
					else if(clause.getOperator().equals(Clause.OPERATOR_NOT))
						clauseQuery.add(new BooleanClause(new TermQuery(new Term(parm.getQName().toString(), String.valueOf(parm.getValue()))), Occur.MUST_NOT));
				}
				query.add(new BooleanClause(clauseQuery, Occur.MUST));
			}		
		}
		if(!entityQuery.getProperties().isEmpty()) {	
			BooleanQuery q = new BooleanQuery();
			for(Property prop : entityQuery.getProperties()) {
				if(entityQuery.getDefaultOperator().equals("AND")) 
					q.add(new TermQuery(new Term(prop.getQName().toString(), String.valueOf(prop.getValue()))), Occur.MUST);
				else
					q.add(new TermQuery(new Term(prop.getQName().toString(), String.valueOf(prop.getValue()))), Occur.SHOULD);
			}
			query.add(q, Occur.MUST);
		} 
		if(entityQuery.getUser() != null && entityQuery.getXid() == 0 && entityQuery.getProperties().isEmpty() && (entityQuery.getQueryString() == null || entityQuery.getQueryString().length() == 0)) 
			query.add(new MatchAllDocsQuery(), Occur.MUST);
		entityQuery.setNativeQuery(query);
		return entityQuery;
	}
}
