package aucta.dev.mercator_core;

import aucta.dev.mercator_core.services.*;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableGlobalMethodSecurity(
		prePostEnabled = true,
		securedEnabled = true,
		jsr250Enabled = true)
@EnableCaching
public class MercatorCoreApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(MercatorCoreApplication.class, args);

		UserService userService = (UserService) context.getBean("userService");
		GroupService groupService = (GroupService) context.getBean("groupService");
		PrivilegeService privilegeService = (PrivilegeService) context.getBean("privilegeService");
		MailTemplateService mailTemplateService = (MailTemplateService) context.getBean("mailTemplateService");
		OrganizationService organizationService = (OrganizationService) context.getBean("organizationService");
		SystemSettingsService settingsService = (SystemSettingsService) context.getBean("systemSettingsService");
		ImportData.importBasicUsersGroupsPrivilegesData(userService, groupService, privilegeService, organizationService);
		ImportData.importSystemSettings(settingsService);
		ImportData.importMailTemplates(mailTemplateService);
	}

}
