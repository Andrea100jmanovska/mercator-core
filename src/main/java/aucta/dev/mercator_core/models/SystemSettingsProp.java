package aucta.dev.mercator_core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "SYSTEM_SETTINGS_PROPS")
public class SystemSettingsProp extends AbstractEntity implements Serializable{

    @Column(name = "PROP_KEY")
    private String key;

    @Column(name = "PROP_VAL")
    private String value;
}
