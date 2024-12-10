package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.enums.SearchOperation;
import aucta.dev.mercator_core.models.Privilege;
import aucta.dev.mercator_core.repositories.GroupRepository;
import aucta.dev.mercator_core.repositories.PrivilegeRepository;
import aucta.dev.mercator_core.repositories.specifications.PrivilegeSpecification;
import aucta.dev.mercator_core.repositories.specifications.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class PrivilegeService {

    @Autowired
    PrivilegeRepository privilegeRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserService userService;

    public Page<Privilege> all(Pageable pageable) {
        return privilegeRepository.findAll(pageable);
    }

    public Page<Privilege> all(Map<String, String> params, Pageable pageable) {
        PrivilegeSpecification privilegeSpecification = new PrivilegeSpecification();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue()))
                privilegeSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
            Date date = new Date();

        }
        return privilegeRepository.findAll(privilegeSpecification, pageable);
    }

    public Privilege get(String id) {
        return privilegeRepository.getById(id);
    }

    public Boolean exists(String id) {
        return privilegeRepository.existsById(id);
    }

    public Privilege save(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    public List<Privilege> all() {
        List<Privilege> privileges = privilegeRepository.findAll();
        CustomUserDetails customUserDetails = userService.getCurrentUserDetails();
        if (!customUserDetails.getAuthorities().stream().anyMatch(r -> r.getAuthority().startsWith("ROLE_ADMINISTRATION"))) {
            privileges.removeIf(p -> p.getName().equals("ADMINISTRATION"));
        }
        return privileges;
    }

    public Boolean remove(String id) {
        Privilege privilegeToRemove = privilegeRepository.getById(id);
        privilegeRepository.delete(privilegeToRemove);
        return true;
    }

    public Privilege findByName(String name) {
        return privilegeRepository.findByName(name);
    }

}
