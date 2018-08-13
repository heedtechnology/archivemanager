package org.archivemanager.server.web.auth;

import org.archivemanager.server.config.PropertyConfiguration;
import org.heed.openapps.Role;
import org.heed.openapps.User;
import org.heed.openapps.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired private SecurityService securityService;;
    @Autowired private PropertyConfiguration properties;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	User user = null;
    	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
    	if(username.equals("administrator")) {
    		user = new User();
    		user.setEnabled(true);
    		user.setUsername("administrator");
    		String p = properties.getaAministratorPassword();
    		user.setPassword(passwordEncoder.encode(p));
    		grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
    	} else {
    		user = securityService.getUserByUsername(username);
    		if(user == null) throw new UsernameNotFoundException("");
    		for (Role role : user.getRoles()){
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            }
    	}
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}