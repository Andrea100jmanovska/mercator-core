package aucta.dev.mercator_core;

import aucta.dev.mercator_core.enums.MailTemplateType;
import aucta.dev.mercator_core.enums.OrganizationType;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.services.*;
import aucta.dev.mercator_core.utils.BasicSystemSettingsProps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImportData {

    @Value("${app.base.url}")
    private String base_url;

    @Value("${app.base.url}")
    private static String BASE_URL;

    @Value("${app.base.url}")
    public void setNameStatic(String base_url) {
        ImportData.BASE_URL = base_url;
    }

    public static void importBasicUsersGroupsPrivilegesData(
            UserService userService,
            GroupService groupService,
            PrivilegeService privilegeService,
            OrganizationService organizationService){

        Privilege privilegeAdministration = privilegeService.findByName("ADMINISTRATION");
        if (privilegeAdministration == null) {
            Privilege privilege = new Privilege();
            privilege.setName("ADMINISTRATION");
            privilegeAdministration = privilegeService.save(privilege);
        }

        Privilege privilegeClient = privilegeService.findByName("CLIENT");
        if (privilegeClient == null) {
            Privilege privilege = new Privilege();
            privilege.setName("CLIENT");
            privilegeClient = privilegeService.save(privilege);
        }

        Group mercatorAdminGroup = groupService.findGroupByCode("MERCATOR_ADMIN");
        if (mercatorAdminGroup == null) {
            Group group = new Group();
            group.setCode("MERCATOR_ADMIN");
            group.setName("Administrators - Mercator");
            mercatorAdminGroup = groupService.create(group);
            groupService.addPrivilege(mercatorAdminGroup.getId(), privilegeAdministration);
        }

        Group mercatorClientsGroup = groupService.findGroupByCode("MERCATOR_CLIENTS");
        if (mercatorClientsGroup == null) {
            Group group = new Group();
            group.setCode("MERCATOR_CLIENTS");
            group.setName("Clients - Mercator");
            mercatorClientsGroup = groupService.create(group);
            groupService.addPrivilege(mercatorClientsGroup.getId(), privilegeClient);
        }

        Organization mercatorOrgOffice = organizationService.findByNameEn("MERCATOR");
        if (mercatorOrgOffice == null) {
            Organization organization = new Organization();
            organization.setNameEn("MERCATOR");
            organization.setType(OrganizationType.MERCATOR_OFFICE);
            organization.setActive(1);
            mercatorOrgOffice = organizationService.save(organization);
            groupService.addGroupToOrganization(mercatorAdminGroup.getId(), mercatorOrgOffice.getId());
            groupService.addGroupToOrganization(mercatorClientsGroup.getId(), mercatorOrgOffice.getId());
        }

        Organization mercatorOrgClients = organizationService.findByNameEn("CLIENT");
        if (mercatorOrgClients == null) {
            Organization organization = new Organization();
            organization.setNameEn("CLIENT");
            organization.setType(OrganizationType.CLIENT);
            organization.setActive(1);
            mercatorOrgClients = organizationService.save(organization);
            groupService.addGroupToOrganization(mercatorAdminGroup.getId(), mercatorOrgClients.getId());
            groupService.addGroupToOrganization(mercatorClientsGroup.getId(), mercatorOrgClients.getId());
        }

        User rootUser = userService.getUserByUsername("root");
        if (rootUser == null) {
            rootUser = new User();
            rootUser.setEmail("devtest@aucta.dev");
            rootUser.setFirstName("Root");
            rootUser.setLastName("User");
            rootUser.setUsername("root");
            rootUser.setPassword("pass123");
            rootUser.setIsEnabled(true);
            rootUser.setIsEnabled(Boolean.TRUE);
            rootUser.setOrganization(mercatorOrgOffice);
            rootUser = userService.save(rootUser);
            groupService.addUserToGroup(mercatorAdminGroup.getId(), rootUser.getId());
        }
    }

    public static void importMailTemplates(MailTemplateService mailTemplateService) {
        MailTemplateType[] mailTemplatesTypes = MailTemplateType.values();
        for (int i = 0; i < mailTemplatesTypes.length; i++) {
            MailTemplate mailTemplate = mailTemplateService.getByType(mailTemplatesTypes[i]);
            if (mailTemplate == null) {
                mailTemplate = new MailTemplate();
                mailTemplate.setTemplateType(mailTemplatesTypes[i]);
                if (mailTemplatesTypes[i].equals(MailTemplateType.MAIL_TEMPLATE_REGISTRATION)) {
                    List<String> params = new ArrayList<>();
                    params.add("subject");
                    params.add("link");
                    params.add("user");
                    mailTemplate.setMailTemplateParameters(params);
                    mailTemplate.setContent("<h1><br></h1>\n" +
                            " <figure><img src=\"" + BASE_URL + "/logo.png\" height=\"auto\" width=\"auto\"/></figure>\n" +
                            " <p><br></p>\n" +
                            " <h4>Dear ${user},&nbsp;</h4>\n" +
                            " <h4>You have successfully registered on the Mercator Platform!&nbsp;</h4>\n" +
                            "<h4>The activation link is valid for 24 hours, please activate your account in order to use it.&nbsp;</h4>\n" +
                            " <h2><a href=\"${link}\"><span style=\"color: rgb(226, 80, 65)\"><u>ACTIVATE YOUR ACCOUNT</u></span></a><span style=\"color: rgb(226, 80, 65)\"><u> &nbsp;</u></span></h2>\n" +
                            " <h4>Thanks,&nbsp;</h4>\n" +
                            " <h4>Mercator Team&nbsp;</h4>");
                }else if (mailTemplatesTypes[i].equals(MailTemplateType.MAIL_TEMPLATE_FORGOT_PASSWORD)){
                    List<String> params = new ArrayList<>();
                    params.add("user");
                    params.add("link");
                    mailTemplate.setMailTemplateParameters(params);
                    mailTemplate.setContent("<h1><br></h1>\n" +
                            " <figure><img src=\"" + BASE_URL + "/logo.png\" height=\"auto\" width=\"auto\"/></figure>\n" +
                            " <p><br></p>\n" +
                            " <p>Dear ${user},</p>\n" +
                            " <p>You can reset your password on the following link:</p>\n" +
                            " <h2><a href=\"${link}\"><span style=\"color: rgb(226, 80, 65)\"><u>CHANGE PASSWORD</u></span></a><span style=\"color: rgb(226, 80, 65)\"><u> &nbsp;</u></span></h2>\n" +
                            " <p><br></p>\n" +
                            " <p><strong>You didn't submit this request?</strong></p>\n" +
                            " <p>Don't worry! Your email was entered by mistake. Please ignore this message.</p>\n" +
                             "<h4>Thanks,&nbsp;</h4>\n" +
                            "<h4>Mercator Team&nbsp;</h4>");
                }
                else if (mailTemplatesTypes[i].equals(MailTemplateType.MAIL_TEMPLATE_ORDER_STATUS_CHANGE)){
                    List<String> params = new ArrayList<>();
                    params.add("user");
                    params.add("link");
                    mailTemplate.setMailTemplateParameters(params);
                    mailTemplate.setContent("<h1><br></h1>\n" +
                            " <figure><img src=\"" + BASE_URL + "/logo.png\" height=\"auto\" width=\"auto\"/></figure>\n" +
                            " <p><br></p>\n" +
                            " <p>Dear ${user},</p>\n" +
                            " <p>The status of your order with id: ${orderId} is changed from ${oldStatus} to ${newStatus}.</p>\n" +
                            " <p><br></p>\n" +
                            " <p><strong>If you have any question, please contact out Mercator Team.</strong></p>\n" +
                            "<h4>Thanks,&nbsp;</h4>\n" +
                            "<h4>Mercator Team&nbsp;</h4>");
                }


                else if (mailTemplatesTypes[i].equals(MailTemplateType.MAIL_TEMPLATE_ANALYZE_COMPANY)){
                    List<String> params = new ArrayList<>();
                    params.add("user");
                    params.add("link");
                    mailTemplate.setMailTemplateParameters(params);
                    mailTemplate.setContent("<h1><br></h1>\n" +
                            " <figure><img src=\"" + BASE_URL + "/logo.png\" height=\"auto\" width=\"auto\"/></figure>\n" +
                            " <p><br></p>\n" +
                            " <p>Dear Dimitri,</p>\n" +
                            " <p>The user: ${user} with email: ${email} wants you to analyze this company: ${company} </p>\n" +
                           " <p><br></p>\n" +
                            "<h4>Thanks,&nbsp;</h4>\n" +
                            "<h4>Mercator Team&nbsp;</h4>");
                }
                mailTemplate.setName(mailTemplatesTypes[i].name());
                mailTemplateService.save(mailTemplate);
            }
        }
    }

    public static void importSystemSettings(SystemSettingsService settingsService) {
        SystemSettingsProp settingsProp1 = settingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SEND_MAILS_ENABLED);
        if (settingsProp1 == null) {
            settingsService.updateSettingsProp(BasicSystemSettingsProps.SEND_MAILS_ENABLED, "false", null);
        }
        SystemSettingsProp settingsProp2 = settingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_SERVER_HOST);
        if (settingsProp2 == null) {
            settingsService.updateSettingsProp(BasicSystemSettingsProps.SMTP_SERVER_HOST, "mail.aucta.dev", null);
        }
        SystemSettingsProp settingsProp3 = settingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_SERVER_PORT);
        if (settingsProp3 == null) {
            settingsService.updateSettingsProp(BasicSystemSettingsProps.SMTP_SERVER_PORT, "465", null);
        }
        SystemSettingsProp settingsProp4 = settingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME);
        if (settingsProp4 == null) {
            settingsService.updateSettingsProp(BasicSystemSettingsProps.SMTP_USERNAME, "devtest@aucta.dev", null);
        }
        SystemSettingsProp settingsProp5 = settingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_PASSWORD);
        if (settingsProp5 == null) {
            settingsService.updateSettingsProp(BasicSystemSettingsProps.SMTP_PASSWORD, "pass123", null);
        }
        SystemSettingsProp settingsProp6 = settingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.MAIL_SCHEDULER_ENABLED);
        if (settingsProp6 == null) {
            settingsService.updateSettingsProp(BasicSystemSettingsProps.MAIL_SCHEDULER_ENABLED, "false", null);
        }
    }

}
