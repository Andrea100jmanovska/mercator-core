package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.SystemSettingsProp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingsRepository extends JpaRepository<SystemSettingsProp, String> {

    SystemSettingsProp findFirstByKey(String key);
}