package org.archivemanager.server.web.security;
import java.util.HashSet;
import java.util.Set;

import org.archivemanager.data.SystemModelEntityBinder;
import org.archivemanager.server.config.PropertyConfiguration;
import org.heed.openapps.Role;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

 
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{ 
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired private EntityService entityService;
	@Autowired private PropertyConfiguration properties;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Autowired private SystemModelEntityBinder binder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
        String password = authentication.getCredentials().toString();        
        logger.info("Name = " + name + " ,Password = " + password);        
        try {
        	User user = null;
        	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        	if(name.equals("administrator")) {
        		user = new User();
        		user.setEnabled(true);
        		user.setUsername("administrator");
        		String p = properties.getaAministratorPassword();
        		user.setPassword(passwordEncoder.encode(p));
        		grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
        	} else {
        		Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.USERNAME, name);
        		user = binder.getUser(entity);
    	        for (Role role : user.getRoles()){
    	            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
    	        }
        	}
	        if(user != null) {
	        	if(!user.isEnabled()) {
	        		logger.info("message", "there was an authentication problem, account disabled.");
	        	} else if(passwordEncoder.matches(password, user.getPassword())) {
	        		logger.info("Succesful authentication!");        		
	        		return new UsernamePasswordAuthenticationToken(name, password, grantedAuthorities);
	        	}
	        }  
        } catch(Exception e) {
        	logger.error("", e);
        }
        logger.info("Login fail!");        
        return null;
	}
 
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}