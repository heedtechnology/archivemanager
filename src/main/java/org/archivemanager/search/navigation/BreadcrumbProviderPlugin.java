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

import org.heed.openapps.entity.EntityService;
import org.heed.openapps.search.SearchPlugin;
import org.heed.openapps.search.SearchService;


public abstract class BreadcrumbProviderPlugin implements SearchPlugin {
	protected SearchService searchSvc;
	protected EntityService entityService;
	
	
	@Override
	public void initialize() {
		
	}
	
	public SearchService getSearchService() {
		return searchSvc;
	}
	public void setSearchService(SearchService searchSvc) {
		this.searchSvc = searchSvc;
	}
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
}
