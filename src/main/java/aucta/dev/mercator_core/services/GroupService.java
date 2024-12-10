package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.SearchOperation;
import aucta.dev.mercator_core.models.Group;
import aucta.dev.mercator_core.models.Organization;
import aucta.dev.mercator_core.models.Privilege;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.repositories.GroupRepository;
import aucta.dev.mercator_core.repositories.UserRepository;
import aucta.dev.mercator_core.repositories.specifications.GroupSpecification;
import aucta.dev.mercator_core.repositories.specifications.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Group> getUserGroupByOrganization(Organization organization) {
        GroupSpecification specification = new GroupSpecification();
        specification.add(new SearchCriteria("organization.id", organization.getId(), SearchOperation.EQUAL));
        specification.add(new SearchCriteria("code", "_CLIENTS", SearchOperation.MATCH_END));
        return groupRepository.findAll(specification);
    }

    public Group findGroupByCode(String code) {
        return groupRepository.findByCode(code);
    }

    public Group create(Group group) {
        if (group.getId() != null) {
            Group tmp = groupRepository.getById(group.getId());
            group.setUsers(tmp.getUsers());
        }
        return groupRepository.save(group);
    }

    public Group update(Group group) {
        return groupRepository.save(group);
    }

    public Boolean delete(String id) {
        Group group = groupRepository.getById(id);
        groupRepository.delete(group);
        return true;
    }

    @Transactional
    public Group addPrivilege(String groupId, Privilege privilege) {
        Group group = groupRepository.findById(groupId).orElse(null);
        group.getPrivileges().add(privilege);
        return groupRepository.save(group);
    }

    @Transactional
    public Group removePrivilege(String groupId, Privilege privilege) {
        Group group = groupRepository.findById(groupId).orElse(null);
        Privilege privilegeToRemove = group.getPrivileges().stream().filter(privilege1 -> privilege1.getId().equals(privilege.getId())).findFirst().orElse(null);
        group.getPrivileges().remove(privilegeToRemove);
        return groupRepository.save(group);
    }

    @Transactional
    public Boolean addUserToGroup(String groupId, String userId) {
        Group group = groupRepository.getById(groupId);
        User user = userRepository.getById(userId);
        if (!user.getGroups().contains(group)) {
            user.getGroups().add(group);
            userRepository.save(user);
        }
        return Boolean.TRUE;
    }

    @Transactional
    public Group addGroupToOrganization(String groupId, String orgId) {
        Organization organization = new Organization();
        organization.setId(orgId);
        Group group = groupRepository.getById(groupId);
        group.setOrganization(organization);
        return groupRepository.save(group);
    }

    @Transactional
    public Boolean removeUserFromGroup(String groupId, String userId) {
        Group group = groupRepository.getById(groupId);
        User user = userRepository.getById(userId);
        user.getGroups().remove(group);
        userRepository.save(user);
        return Boolean.TRUE;
    }



}
