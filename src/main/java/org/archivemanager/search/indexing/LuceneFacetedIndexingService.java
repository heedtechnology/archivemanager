package org.archivemanager.search.indexing;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.index.FacetFields;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.heed.openapps.entity.indexing.IndexFacet;
import org.heed.openapps.entity.indexing.IndexField;
import org.heed.openapps.property.PropertyService;
import org.heed.openapps.search.indexing.IndexSearcher;
import org.heed.openapps.search.indexing.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;


public class LuceneFacetedIndexingService implements IndexingService {
	private final static Logger log = Logger.getLogger(LuceneFacetedIndexingService.class.getName());
	@Autowired private PropertyService propertyService;
	
	protected Map<String, IndexWriter> writers = new HashMap<String, IndexWriter>();
	
	
	public void initialize() {
		log.info("ArchiveManager Indexing Service starting...");
	}
	
	public void update(IndexEntity data) {
		IndexWriter indexWriter = null;
		DirectoryTaxonomyWriter taxoWriter = null;
		try {	
			String dir = propertyService.getPropertyValue("home.dir") + "/data/lucene";
	    	Directory indexDir = FSDirectory.open(new File(dir, "index"));
	    	Directory taxoDir = FSDirectory.open(new File(dir, "facets"));
	    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
	    	IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_43, analyzer);
	    	iwc.setRAMBufferSizeMB(256.0);
	    	indexWriter = new IndexWriter(indexDir, iwc);
			taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
			FacetFields facetFields = new FacetFields(taxoWriter);
			
			Document document = getDocument(data);			
			List<CategoryPath> paths = new ArrayList<CategoryPath>();
		    for(IndexFacet facet : data.getFacets()) {
		      paths.add(new CategoryPath(facet.getQname().toString(), facet.getValue()));
		    }		    
		    if(paths.size() > 0) facetFields.addFields(document, paths);
		    indexWriter.updateDocument(new Term("id", String.valueOf(data.getId())), document);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			try {
				indexWriter.close();
				taxoWriter.close();
			} catch(IOException e) {
				log.log(Level.SEVERE, "", e);
			}
		}
	}

	public void update(List<IndexEntity> data) {
		IndexWriter indexWriter = null;
		DirectoryTaxonomyWriter taxoWriter = null;
		try {	
			String dir = propertyService.getPropertyValue("home.dir") + "/data/lucene";
	    	Directory indexDir = FSDirectory.open(new File(dir, "index"));
	    	Directory taxoDir = FSDirectory.open(new File(dir, "facets"));
			indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(Version.LUCENE_43, 
					new StandardAnalyzer(Version.LUCENE_43)));
			taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
			FacetFields facetFields = new FacetFields(taxoWriter);
			
			for(IndexEntity entity : data) {				
				Document document = getDocument(entity);
				List<CategoryPath> paths = new ArrayList<CategoryPath>();
			    for(IndexFacet facet : entity.getFacets()) {
			      paths.add(new CategoryPath(facet.getQname().toString(), facet.getValue()));
			    }		    
			    if(paths.size() > 0) facetFields.addFields(document, paths);
			    indexWriter.updateDocument(new Term("id", String.valueOf(entity.getId())), document);
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			try {
				indexWriter.close();
				taxoWriter.close();
			} catch(IOException e) {
				log.log(Level.SEVERE, "", e);
			}
		}		
	}
	@Override
	public void remove(Long id) {
		IndexWriter indexWriter = null;
		try {
			String dir = propertyService.getPropertyValue("home.dir") + "/data/lucene";
	    	Directory indexDir = FSDirectory.open(new File(dir, "index"));
	    	indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(Version.LUCENE_43, 
					new StandardAnalyzer(Version.LUCENE_43)));
			indexWriter.deleteDocuments(new Term("id", String.valueOf(id)));
		
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			try {
				indexWriter.close();
			} catch(IOException e) {
				log.log(Level.SEVERE, "", e);
			}
		}
	}
	public IndexSearcher getIndexSearcher(String ctx) throws IOException {
		IndexSearcher indexSearcher = null;
		IndexWriter indexWriter = getIndexWriter(ctx);
		if(indexWriter != null) {
			String dir = propertyService.getPropertyValue("home.dir") + "/data/lucene";
	    	Directory indexDir = FSDirectory.open(new File(dir, "index"));
	    	IndexReader indexReader = DirectoryReader.open(indexDir);
			org.apache.lucene.search.IndexSearcher luceneSearcher = new org.apache.lucene.search.IndexSearcher(indexReader);
			indexSearcher = new LuceneIndexSearcher(luceneSearcher);
		}
		return indexSearcher;
	}
	protected IndexWriter getIndexWriter(String ctx) throws IOException {
		IndexWriter indexWriter = writers.get(ctx);
		if(indexWriter == null) {
			synchronized(writers) {
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
				File indexDir = new File(propertyService.getPropertyValue("home.dir")+"/data/lucene/"+ctx);
				Directory directory = FSDirectory.open(indexDir);
				IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
				LogByteSizeMergePolicy mergePolicy = new LogByteSizeMergePolicy();
				//mergePolicy.setUseCompoundFile(false);
				config.setMergePolicy(mergePolicy);
				indexWriter = new IndexWriter(directory, config);
				writers.put(ctx, indexWriter);
			}
		}
		return indexWriter;
	}
	protected Document getDocument(IndexEntity entity) {
		Document document = new Document();
		for(IndexField field : entity.getFields()) {				
			Field indexField = null;
			if(field.getType() == Property.INTEGER) {
				/*
				if(field.getValue() instanceof String) indexField = new IntField(field.getName(), Integer.valueOf((String)field.getValue()), Field.Store.YES);
				if(field.getValue() instanceof Long) indexField = new IntField(field.getName(), ((Long)field.getValue()).intValue(), Field.Store.YES);
				else indexField = new IntField(field.getName(), (int)field.getValue(), Field.Store.YES);
				*/
				indexField = new StringField(field.getName(), String.valueOf(field.getValue()), Field.Store.YES);
			} else if(field.getType() == Property.DOUBLE) {
				/*
				if(field.getValue() instanceof String) indexField = new DoubleField(field.getName(), Long.valueOf((String)field.getValue()), Field.Store.YES);
				else indexField = new DoubleField(field.getName(), (double)field.getValue(), Field.Store.YES);
				*/
				indexField = new StringField(field.getName(), String.valueOf(field.getValue()), Field.Store.YES);
			} else if(field.getType() == Property.LONG) {
				/*
				if(field.getValue() instanceof String) indexField = new LongField(field.getName(), Long.valueOf((String)field.getValue()), Field.Store.YES);
				else if(field.getValue() instanceof Integer) indexField = new LongField(field.getName(), ((Integer)field.getValue()).longValue(), Field.Store.YES);
				else indexField = new LongField(field.getName(), (long)field.getValue(), Field.Store.YES);
				*/
				indexField = new StringField(field.getName(), String.valueOf(field.getValue()), Field.Store.YES);
			} else if(field.getType() == Property.BOOLEAN) {
				indexField = new StringField(field.getName(), field.getValue().toString(), Field.Store.YES);
			} else if(field.getType() == Property.STRING || field.getType() == Property.LONGTEXT || field.getType() == Property.SERIALIZABLE || field.getType() == Property.NULL) {
				if(field.isTokenized())
					indexField = new TextField(field.getName(), (String)field.getValue(), Field.Store.YES);
				else
					indexField = new StringField(field.getName(), (String)field.getValue(), Field.Store.YES);
			}
			document.add(indexField);
		}
		document.add(new TextField("freetext", entity.getFreeText(), Field.Store.YES));
		return document;
	}
	/*
	try {			
		IndexSearcher indexSearcher = getIndexSearcher(ctx);
		TopDocs hits = indexSearcher.search((org.apache.lucene.search.Query)query.getQueryOrQueryObject(), 1000);
		NodeIndexHitsImpl result = new NodeIndexHitsImpl();
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
		    Document document = indexSearcher.doc(scoreDoc.doc);
			long nodeId = GetterUtil.getLong(document.get("nodeId"));
			result.getHits().add(nodeId);
			result.getScores().add(scoreDoc.score);
		}
		return result;
	} catch(Exception e) {
		throw new NodeException("", e);
	}
	*/
	
	@Override
	public org.heed.openapps.search.indexing.IndexReader getIndexReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<org.heed.openapps.search.indexing.IndexReader> getIndexReaders() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnIndexReaders(List<org.heed.openapps.search.indexing.IndexReader> arg0) {
		// TODO Auto-generated method stub
		
	}
}
