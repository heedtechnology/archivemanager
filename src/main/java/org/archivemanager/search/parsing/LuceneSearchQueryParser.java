package org.archivemanager.search.parsing;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.archivemanager.search.parsing.date.LuceneDateParsingUtility;
import org.heed.openapps.Group;
import org.heed.openapps.QName;
import org.heed.openapps.data.RequestBuffer;
import org.heed.openapps.entity.Property;
import org.heed.openapps.search.Clause;
import org.heed.openapps.search.Dictionary;
import org.heed.openapps.search.EntityQuery;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.Token;
import org.heed.openapps.search.dictionary.CategoryDefinition;
import org.heed.openapps.search.dictionary.CompoundDateDefinition;
import org.heed.openapps.search.parsing.ParserPlugin;
import org.heed.openapps.search.parsing.QueryParser;
import org.heed.openapps.search.parsing.QueryTokenizer;
import org.heed.openapps.util.NumberUtility;


public class LuceneSearchQueryParser implements QueryParser {
	private static final Log log = LogFactory.getLog(LuceneSearchQueryParser.class);
	private QueryTokenizer tokenizer;
	private Dictionary dictionary;
	protected List<ParserPlugin> plugins = new ArrayList<ParserPlugin>();
	protected String defaultFields = "name";
	protected org.apache.lucene.queryparser.classic.QueryParser lucene;
	protected Analyzer analyzer;
		
	private LuceneDateParsingUtility dateParser = new LuceneDateParsingUtility();
	
	
	public void initialize() {
		
	}
	
	public EntityQuery parse(EntityQuery request) {
		return parse(this.tokenizer, request);
	}
	
	public EntityQuery parse(QueryTokenizer tokenizer, EntityQuery request) {
		RequestBuffer buff = new RequestBuffer();
		EntityQuery q = new EntityQuery();
		
		String queryString = null;
		
		if(request.getQueryString() == null || request.getQueryString().length() == 0 || request.getQueryString().equals("all results")) {
			if(request.getProperties().size() > 0) {
				BooleanQuery boolQuery = new BooleanQuery();
				for(Property p : request.getProperties()) {
					buff.append("+"+p.getQName().toString()+":"+p.getValue()+" ");
					Term term = new Term(p.getQName().getLocalName(), String.valueOf(p.getValue()));
					boolQuery.add(new TermQuery(term), BooleanClause.Occur.MUST);
				}
				boolQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
				q.setNativeQuery(boolQuery);
			} else {
				q.setNativeQuery(new MatchAllDocsQuery());
			}
		} else {
			List<Token> tokens = tokenizer.tokenize(request.getQueryString());
			for(ParserPlugin plugin : plugins) {
				tokens = plugin.filter(tokens);
			}
			for(int i=0; i < tokens.size(); i++) {
				Token token = tokens.get(i);
				if(token.getType() == Token.ALL) {
					if(tokens.size() == 1)
						q.setNativeQuery(new MatchAllDocsQuery());
				}
				else if(token.getType() == Token.CATG) buff.append(getCategoryQuery(token));
				else if(token.isType(Token.ROOT)) {
					buff.append(token.getValue());
					if(token.getName().equals("(")) token.setInParens(true);
					else if(token.getName().equals(")")) token.setInParens(false);
				} else if(token.getType() == Token.ENTITY) {
					QName qname = new QName(token.getValue());
					q.setEntityQnames(new QName[]{qname});
				} else if(token.getType() == Token.DRNG && lookaheadType(tokens, i+1, Token.DRNG)) {
					String clause = dateParser.getDateClause(token, tokens.get(i));
					buff.append(clause);
					i++;
				} else {
					if(!token.inParens() && !buff.lastChar('-')) buff.append("+");
					if(token.isType(Token.MULT)) {
						buff.append("(");
						for(int j=0; j < token.getSubTypes().size(); j++) {
							token.setType(token.getSubTypes().get(j));
							token.setValue(token.getSubValues().get(j));
							tokenToQuery(token, buff);
							buff.append(" OR ");
						}
						buff.setLength(buff.length()-4);
						buff.append(")");
					} else {
						if(token.getType() == Token.MULT) {
							buff.append("(");
							for(int k=0; k < token.getSubTypes().size(); k++) {
								Integer type = token.getSubTypes().get(k);
								if(type != Token.NUMB) {
									tokenToQuery(new Token(token.getName(), token.getSubValues().get(k), token.getSubTypes().get(k)), buff);
									buff.append(" OR ");
								}
							}
							buff.setLength(buff.length()-4);
							buff.append(")");
						} else 
							tokenToQuery(token, buff);
					}
					buff.append(" ");
				}
			}
			if(request.getProperties().size() > 0) {
				for(Property p : request.getProperties()) {
					buff.append("+"+p.getQName().getLocalName()+":"+p.getValue()+" ");
				}
			}
			/*
			if(buff.toString().trim().equals("+(all results)")) {
				Query luceneQuery = new MatchAllDocsQuery();
				q = new SearchQuery(SearchQuery.TYPE_LUCENE, luceneQuery);
				return q;
			}
			*/		
			if(lucene == null) lucene = new OpenAppsMultiFieldQueryParser();
			queryString = buff.toString().trim();
			
			q.setQueryParse(queryString);
			if(queryString.length() == 0);
			else {
				try {
					if(request.getDefaultOperator() == SearchRequest.OPERATOR_AND) lucene.setDefaultOperator(Operator.AND);
					else lucene.setDefaultOperator(Operator.OR);
					Query luceneQuery = checkForAllNegativeClauses(lucene.parse(queryString));				
					q.setNativeQuery(luceneQuery);
					for(ParserPlugin plugin : plugins) {
						q = plugin.parse(q, tokens);
					}
				} catch(Exception e) {
					e.printStackTrace();
					log.fatal("problem parsing "+request.getQueryString()+" to "+queryString);
					//log.error(e.getMessage());
				}
			}
		}		
		BooleanQuery filter = new BooleanQuery();		
		BooleanQuery qnameQuery = new BooleanQuery();
		for(QName qname : request.getEntityQnames()) {
			if(qname != null)
				qnameQuery.add(new BooleanClause(new TermQuery(new Term("qname", qname.toString())), Occur.SHOULD));
		}
		filter.add(qnameQuery, Occur.MUST);		
		filter.add(new TermQuery(new Term("openapps_org_repository_1_0_internal", "true")), Occur.MUST_NOT);
		
		if(request.getClauses().size() > 0) {			
			for(Clause clause : request.getClauses()) {
				BooleanQuery clauseQuery = new BooleanQuery();
				for(Property parm : clause.getProperties()) {
					if(clause.getOperator().equals(Clause.OPERATOR_OR))
						clauseQuery.add(new BooleanClause(new TermQuery(new Term(parm.getQName().toString(), (String)parm.getValue())), Occur.SHOULD));
					else if(clause.getOperator().equals(Clause.OPERATOR_AND))
						clauseQuery.add(new BooleanClause(new TermQuery(new Term(parm.getQName().toString(), (String)parm.getValue())), Occur.MUST));
					else if(clause.getOperator().equals(Clause.OPERATOR_NOT))
						clauseQuery.add(new BooleanClause(new TermQuery(new Term(parm.getQName().toString(), (String)parm.getValue())), Occur.MUST_NOT));
				}
				filter.add(new BooleanClause(clauseQuery, Occur.MUST));
			}		
		}
		if(request.getUser() != null) {
			BooleanQuery aclQuery = new BooleanQuery();
			aclQuery.add(new BooleanClause(new TermQuery(new Term("openapps_org_repository_1_0_isPublic", "true")), Occur.SHOULD));
			if(!request.getUser().isGuest()) {
				aclQuery.add(new BooleanClause(new TermQuery(new Term("acl", "0")), Occur.SHOULD));
				aclQuery.add(new BooleanClause(new TermQuery(new Term("acl", String.valueOf(request.getUser().getId()))), Occur.SHOULD));
				for(Group group : request.getUser().getGroups()) {
					aclQuery.add(new BooleanClause(new TermQuery(new Term("acl", String.valueOf(group.getId()))), Occur.SHOULD));
				}
				filter.add(new BooleanClause(aclQuery, Occur.MUST));
			} else filter.add(new BooleanClause(new TermQuery(new Term("acl", "0")), Occur.MUST));
			
		}		
		QueryWrapperFilter qf = new QueryWrapperFilter(filter);
		FilteredQuery fq = new FilteredQuery((Query)q.getNativeQuery(), qf);
		q.setNativeQuery(fq);
		return q;
	}
	class OpenAppsMultiFieldQueryParser extends MultiFieldQueryParser {
		
		public OpenAppsMultiFieldQueryParser() {
			super(Version.LUCENE_43, defaultFields.split(","), new StandardAnalyzer(Version.LUCENE_43));
			setDefaultOperator(org.apache.lucene.queryparser.classic.QueryParser.AND_OPERATOR);
		}
		/*
		@Override
		protected org.apache.lucene.search.Query getPrefixQuery(String field, String value) throws ParseException {
			return new PrefixQuery(new Term(field, value));
		}
		@Override
		protected org.apache.lucene.search.Query getFieldQuery(String field, String value, boolean quoted) throws ParseException {
			return super.getFieldQuery(field, value, quoted);
		}
	
		protected org.apache.lucene.search.Query getRangeQuery(String field, String begin, String end, boolean inclusive) throws ParseException {
			//return new DateRangeQuery(field, begin, end, inclusive);
			if(field.endsWith("_"))
				return NumericRangeQuery.newLongRange(field, Long.valueOf(begin), Long.valueOf(end), true, true);
			else
				return new TermRangeQuery(field, begin, end, true, true);
		}
		*/
	}
	
	protected Query checkForAllNegativeClauses(Query query) {
		if(query instanceof BooleanQuery) {
			BooleanQuery bQuery = (BooleanQuery)query;
			boolean positive = false;
			for(BooleanClause clause : bQuery.getClauses()) {
				if(clause.getOccur() == Occur.MUST || clause.getOccur() == Occur.SHOULD) positive = true;
			}
			if(!positive) bQuery.add(new MatchAllDocsQuery(), Occur.MUST);			
		}
		return query;
	}
	protected void tokenToQuery(Token token, RequestBuffer buff) {
		if(token.getType() == Token.TEXT) {
			buff.append(token.getValue());
		}
		else if(token.getType() == Token.PROP || token.getType() == Token.NUMB) buff.append(token.getValue());
		else if(token.getType() == Token.TERM || token.getType() == Token.QNAM) {
			if(token.getSubTokens().size() > 0) {
				if(!token.inParens()) buff.append("(");
				RequestBuffer buff2 = new RequestBuffer();
				for(Token t : token.getSubTokens()) {
					buff2.append("+");
					tokenToQuery(t, buff2);
					buff2.append(" ");
				}
				if(!token.inParens()) buff.append(buff2.toString().trim()+")");
			} else {
				if(token.inParens()) buff.append("("+token.getValue()+")");
				else buff.append(token.getValue());
			}
		} else if(token.getType() == Token.ATTR || token.getType() == Token.ID || token.getType() == Token.PATH || token.getType() == Token.COLM) {
			buff.append(token.getName()+":"+token.getValue());
		} else if(token.getType() == Token.NAME || token.getType() == Token.SUBJ) {
			buff.append("(source_assoc"+":"+token.getValue()+" OR source_assoc_inherited"+":"+token.getValue()+")");
		} else if(token.getType() == Token.DATE) {
			buff.append(getDateQuery(token));
		} else if(token.getType() == Token.YEAR) {
			buff.append(getYearQuery(token));
		} else if(token.getType() == Token.MONTH) {
			buff.append(getMonthQuery(token));
		} else if(token.getType() == Token.DRNG) {
			String clause = dateParser.getDateClause(token);
			buff.append(clause);
		} else if(token.getType() == Token.CATG) {
			buff.append(getCategoryQuery(token));
		} 
	}
	protected Query getFieldQuery(String field, String queryText, boolean b) {
		return getFieldQuery(field, queryText);
	}
	protected Query getFieldQuery(String field, String queryText) {
		BooleanQuery bq = new BooleanQuery();
		//TokenStream stream = analyzer.tokenStream("", new StringReader(queryText));
		List<String> terms = new ArrayList<String>();
		//org.apache.lucene.analysis.Token t;
		/*
		try {
			while((t = stream.next()) != null) {
				terms.add(t.term());
			}
		} catch (java.io.IOException e) {
			throw new ParseException(e.getMessage());
		}
		*/
		if(terms.size() == 0) return null;
		for(int i=0; i < defaultFields.split(",").length; i++) {
			PhraseQuery pq = new PhraseQuery();
			for(int j=0; j < terms.size(); j++) {
				pq.add(new Term(defaultFields.split(",")[i], terms.get(j)));
			}
			bq.add(pq, Occur.SHOULD);
		}
		return bq;
	}
	protected String getCategoryQuery(Token token) {
		StringBuffer buff = new StringBuffer();
		CategoryDefinition def = (CategoryDefinition)dictionary.lookup(Token.CATG, token.getName());
		try {
			if(def.getIds().size() == 1) {
				return "path:"+def.getIds().get(0);
			} else if(def.getIds().size() > 0) {
				for(String category : def.getIds()) {
					buff.append(category+" OR ");
				}
				return "path:("+buff.substring(0, buff.length()-4).toString()+")";
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public String getDateQuery(Token token) {
		return token.getName()+":"+token.getValue();
	}
	public String getYearQuery(Token token) {
		if(token.getDefinitions().size() > 0 && token.getDefinitions().get(0) instanceof CompoundDateDefinition) {
			StringWriter writer = new StringWriter();
			CompoundDateDefinition def = (CompoundDateDefinition)token.getDefinitions().get(0);
			String[] chunks = def.getValue().split(" ");
			String year = "";
			for(String chunk : chunks) {
				if(NumberUtility.isInteger(chunk)) year = chunk;
			}
			writer.append("(");
			for(Clause clause : def.getClauses()) {
				writer.append("(");
				for(Property parm : clause.getProperties()) {
					if(parm.getQName().getLocalName().equals("date")) writer.append("+"+parm.getValue()+":["+year+"0101 TO "+year+"1231] ");
					else writer.append("+"+parm.getQName().getLocalName()+":"+parm.getValue()+" ");
				}
				writer.append(") ");
			}
			writer.append(") ");
			return writer.toString().trim();
		} else {
			String[] value = token.getValue().split(":");
			if(value.length == 2) {
				return value[0]+":["+value[1]+"0101 TO "+value[1]+"1231]";
			}
			return token.getValue();
		}
	}
	public String getMonthQuery(Token token) {		
		if(token.getDefinitions().size() > 0 && token.getDefinitions().get(0) instanceof CompoundDateDefinition) {
			StringWriter writer = new StringWriter();
			CompoundDateDefinition def = (CompoundDateDefinition)token.getDefinitions().get(0);
			String[] value = def.getValue().split(" ");
			writer.append("(");
			for(Clause clause : def.getClauses()) {
				String month = "";	
				if(value[0].equals("december")) month = "12";
				if(value[0].equals("november")) month = "11";
				if(value[0].equals("october")) month = "10";	
				if(value[0].equals("september")) month = "09";
				if(value[0].equals("august")) month = "08";
				if(value[0].equals("july")) month = "07";
				if(value[0].equals("june")) month = "06";
				if(value[0].equals("may")) month = "05";
				if(value[0].equals("april")) month = "04";
				if(value[0].equals("march")) month = "03";
				if(value[0].equals("february")) month = "02";
				if(value[0].equals("january")) month = "01";					
				writer.append("(");
				for(Property parm : clause.getProperties()) {
					if(parm.getQName().getLocalName().equals("date")) writer.append("+"+parm.getValue()+":["+value[1]+month+"01 TO "+value[1]+month+"31] ");
					else writer.append("+"+parm.getQName().getLocalName()+":"+parm.getValue()+" ");
				}
				writer.append(") ");
			}
			writer.append(") ");
			return writer.toString().trim();
		} else {
			Calendar current = Calendar.getInstance();
			String[] value = token.getValue().split(":");
			String month = "";	
			if(value.length == 2) {
				if(value[1].equals("december")) month = "12";
				if(value[1].equals("november")) month = "11";
				if(value[1].equals("october")) month = "10";	
				if(value[1].equals("september")) month = "09";
				if(value[1].equals("august")) month = "08";
				if(value[1].equals("july")) month = "07";
				if(value[1].equals("june")) month = "06";
				if(value[1].equals("may")) month = "05";
				if(value[1].equals("april")) month = "04";
				if(value[1].equals("march")) month = "03";
				if(value[1].equals("february")) month = "02";
				if(value[1].equals("january")) month = "01";					
			}
			return value[0]+":["+current.get(Calendar.YEAR)+month+"01 TO "+current.get(Calendar.YEAR)+month+"31]";
		}
	}
		
	public Dictionary getDictionary() {
		return dictionary;
	}
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	public QueryTokenizer getTokenizer() {
		return tokenizer;
	}
	public void setDefaultFields(String defaultFields) {
		this.defaultFields = defaultFields;
	}
	public void setTokenizer(QueryTokenizer tokenizer) {
		this.tokenizer = tokenizer;		
	}
	public void setPlugins(List<ParserPlugin> plugins) {
		this.plugins = plugins;
	}
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	public void setDateParser(LuceneDateParsingUtility dateParser) {
		this.dateParser = dateParser;
	}
	public void setParser(Object lucene) {
		if(lucene instanceof org.apache.lucene.queryparser.classic.QueryParser)
			this.lucene = (org.apache.lucene.queryparser.classic.QueryParser)lucene;
	}
	
	protected boolean lookaheadType(List<Token> tokens, int index, int tokenType) {
		if(tokens.size() > index) {
			Token token = tokens.get(index);
			if(token != null && token.getType() == tokenType) return true;
		}
		return false;
	}
	
}
