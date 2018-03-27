package org.heed.openapps.ehcache;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.heed.openapps.cache.CacheService;


public class SpringEhCacheService implements CacheService {
	private static final long serialVersionUID = 2544806007443182054L;
	private static final Log log = LogFactory.getLog(SpringEhCacheService.class);
	protected Cache globalCache;
	
	/*
	@Override
	public void putEntity(Entity entity) {
		Cache cache = globalCache.getCacheManager().getCache("entityCache");
		Element element = cache.get(entity.getId());
		if(element == null) {
			element = new Element(entity.getId(), entity);
			cache.put(element);
		} else {
			Element newElement = new Element(entity.getId(), entity);
			cache.replace(element, newElement);
		}
		log.debug("cache update : entity put "+entity.getId());
	}
	@Override
	public Entity getEntity(Long id) {
		Cache cache = globalCache.getCacheManager().getCache("entityCache");
		Element element = cache.get(id);
		log.debug("cache update : entity put "+id);
		if(element != null) return (Entity)element.getValue();
		return null;
	}
	@Override
	public void removeEntity(Long id) {
		Cache cache = globalCache.getCacheManager().getCache("entityCache");
		cache.remove(id);
		log.debug("cache update : entity removed "+id);
	}
	*/
	public void setGlobalCache(Cache globalCache) {
		this.globalCache = globalCache;
	}
	@Override
	public void put(String cacheName, String id, Object entity) {
		Cache cache = globalCache.getCacheManager().getCache(cacheName);
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
	@Override
	public Object get(String cacheName, String id) {
		Cache cache = globalCache.getCacheManager().getCache(cacheName);
		Element element = cache.get(id);
		log.debug("cache update : object get "+id);
		if(element != null) return element.getValue();
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getKeys(String cacheName) {
		Cache cache = globalCache.getCacheManager().getCache(cacheName);
		return cache.getKeys();
	}
	@Override
	public void remove(String cacheName, String id) {
		Cache cache = globalCache.getCacheManager().getCache(cacheName);
		cache.remove(id);
		log.debug("cache update : entity removed "+id);
	}
	@Override
	public boolean has(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
