package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.models.SystemSettingsProp;
import aucta.dev.mercator_core.repositories.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingsService {

    @Autowired
    SystemSettingsRepository repo;

    @Autowired
    @Lazy
    UserService userService;

    public SystemSettingsProp updateSettingsProp(String key, String value, CustomUserDetails userDetails) {
        SystemSettingsProp prop = repo.findFirstByKey(key);
        if (prop == null) {
            prop = new SystemSettingsProp();
            prop.setValue(value);
            prop.setKey(key);
        }
        if (userDetails != null) {
            prop.setModifiedBy(userService.get(userDetails.getUserId()));
        }
        prop.setValue(value);
        return repo.save(prop);
    }

    public SystemSettingsProp getSystemSettingsPropByKey(String key) {
        return repo.findFirstByKey(key);
    }

}
