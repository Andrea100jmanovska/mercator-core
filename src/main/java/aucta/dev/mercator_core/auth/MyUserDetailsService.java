package aucta.dev.mercator_core.auth;

import aucta.dev.mercator_core.models.Privilege;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsername(s);
        List<Privilege> privileges = new ArrayList<>();
        List<Object[]> objects = userRepository.getPrivilegesByUser(user.getId());
        for(Object[] obj : objects){
            Privilege privilege = new Privilege();
            privilege.setId(String.valueOf(obj[0].toString()));
            privilege.setName((String) obj[3]);
            privileges.add(privilege);
        }
        user.setPrivileges(privileges);
        return new CustomUserDetails(user);
    }
}
