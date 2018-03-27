package org.heed.openapps.ehcache;
import java.net.URL;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.heed.openapps.cache.CacheService;


public class EhCacheService implements CacheService {
	private static final long serialVersionUID = -1087299086538846714L;
	private static final Log log = LogFactory.getLog(EhCacheService.class);
	protected CacheManager manager;
	
	
	public void initialize() {
		URL url = getClass().getResource("/ehcache.xml");
		manager = CacheManager.newInstance(url);
	}
	
	@Override
	public void put(String cacheName, String id, Object entity) {
		Cache cache = manager.getCache(cacheName);
		Element element = cache.get(id);
		if(element == null) {
			element = new Element(id, entity);
			cache.put(element);
		} else {
			Element newElement = new Element(id, entity);
			cache.replace(element, newElement);
		}
		log.debug("cache update : object put "+id);
	}
	@SuppressWarnings("deprecation")
	@Override
	public Object get(String cacheName, String id) {
		Cache cache = manager.getCache(cacheName);
		Element element = cache.get(id);
		log.debug("cache update : object get "+id);
		if(element != null) return element.getValue();
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getKeys(String cacheName) {
		Cache cache = manager.getCache(cacheName);
		return cache.getKeys();
	}
	@Override
	public void remove(String cacheName, String id) {
		Cache cache = manager.getCache(cacheName);
		cache.remove(id);
		log.debug("cache update : entity removed "+id);
	}
	@Override
	public boolean has(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
