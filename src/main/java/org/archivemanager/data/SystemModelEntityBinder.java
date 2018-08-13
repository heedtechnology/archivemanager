package org.archivemanager.data;

import org.heed.openapps.Group;
import org.heed.openapps.Role;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationImpl;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityImpl;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.InvalidPropertyException;

public class SystemModelEntityBinder {
	private EntityService entityService;
	
	
	public SystemModelEntityBinder(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public User getUser(Entity entity) {
		User user = new User();
		user.setId(entity.getId());
		user.setXid(entity.getXid());
		user.setUsername(entity.getPropertyValue(SystemModel.USERNAME));
		user.setEmail(entity.getPropertyValue(SystemModel.EMAIL));
		user.setPassword(entity.getPropertyValue(SystemModel.PASSWORD));
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = association.getTargetEntity();
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
					if(association.getQName().getLocalName().equals("roles")) {
						Role role = getRole(targetEntity);
						user.getRoles().add(role);
					} else if(association.getQName().getLocalName().equals("groups")) {
						Group group = getGroup(targetEntity);
						user.getGroups().add(group);
					}					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return user;
	}
	public Entity getEntity(User user) throws InvalidEntityException,InvalidPropertyException {
		Entity entity =  new EntityImpl(user.getId(), SystemModel.USER);			
		entity.setXid(user.getXid());
		entity.addProperty(SystemModel.USERNAME, user.getUsername());
		entity.addProperty(SystemModel.EMAIL, user.getEmail());
		entity.addProperty(SystemModel.PASSWORD, user.getPassword());
		for(Role role : user.getRoles()) {
			entity.getAssociations().add(new AssociationImpl(SystemModel.ROLES, entity, getEntity(role)));
		}
		return entity;
	}
	
	public Role getRole(Entity entity) {
		Role role = new Role();
		role.setId(entity.getId());
		role.setName(entity.getName());
		role.setXid(entity.getXid());
		return role;
	}
	public Entity getEntity(Role role) throws InvalidEntityException,InvalidPropertyException {
		Entity entity =  new EntityImpl(role.getId(), SystemModel.ROLE);			
		entity.setXid(role.getXid());
		entity.addProperty(SystemModel.NAME, role.getName());		
		return entity;
	}
	
	public Group getGroup(Entity entity) {
		Group group = new Group();
		group.setId(entity.getId());
		group.setName(entity.getName());
		group.setXid(entity.getXid());
		return group;
	}
	public Entity getEntity(Group group) throws InvalidEntityException,InvalidPropertyException {
		Entity entity =  new EntityImpl(group.getId(), SystemModel.GROUP);			
		entity.setXid(group.getXid());
		entity.addProperty(SystemModel.NAME, group.getName());		
		return entity;
	}

}
