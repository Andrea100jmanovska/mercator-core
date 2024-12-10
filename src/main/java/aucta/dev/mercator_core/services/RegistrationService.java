package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.auth.models.UserRegistrationRequest;
import aucta.dev.mercator_core.enums.MailMessageStatus;
import aucta.dev.mercator_core.enums.MailTemplateType;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.repositories.MailTemplateRepository;
import aucta.dev.mercator_core.repositories.UserRepository;
import aucta.dev.mercator_core.utils.BasicSystemSettingsProps;
import aucta.dev.mercator_core.utils.Crypto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RegistrationService {

    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    GroupService groupService;

    @Autowired
    MailMessageService mailMessageService;

    @Autowired
    SystemSettingsService systemSettingsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MailTemplateRepository mailTemplateRepository;

    @Autowired
    private CaptchaService captchaService;

    @Value("${app.base.url}")
    String BASE_URL;

    private final JavaMailSender mailSender;

    public RegistrationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional
    public User registerUserNow(UserRegistrationRequest userRegistrationRequest) throws Exception {
        boolean isCaptchaValid = captchaService.verifyCaptcha(userRegistrationRequest.getCaptchaResponse());
        if (!isCaptchaValid) {
            throw new Exception("Invalid CAPTCHA.");
        }
        User user = new User();
        user.setUsername(userRegistrationRequest.getUsername());
        user.setEmail(userRegistrationRequest.getEmail());
        String email = userRegistrationRequest.getEmail();
        if (existsByEmail(email)) {
            throw new Exception("A user with this email already exists.");
        }
        user.setDateOfBirth(userRegistrationRequest.getDateOfBirth());
        user.setPhoneNumber(userRegistrationRequest.getPhoneNumber());
        user.setPassword(userRegistrationRequest.getPassword());
        user.setIsEnabled(Boolean.FALSE);
        user.setFirstName(userRegistrationRequest.getFirstName());
        user.setLastName(userRegistrationRequest.getLastName());
        Organization organization = organizationService.getDefaultOrganization();
        user.setOrganization(organization);

        List<Group> groups = groupService.getUserGroupByOrganization(organization);
        user.setGroups(groups);
        user = userService.save(user);
        if (user != null) {
            Crypto crypto = new Crypto();
            ObjectMapper mapper = new ObjectMapper();
            ActivateUserToken activateUserToken = new ActivateUserToken();
            activateUserToken.setUserId(user.getId());
            //set valid until 24 hours from now
            activateUserToken.setValidUntil(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
            String token = crypto.encrypt(mapper.writeValueAsString(activateUserToken));
            String encodedToken = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
            //send email with token
            MailMessage mailMessage = new MailMessage();
            mailMessage.setSender(systemSettingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME).getValue());
            mailMessage.setReceivers(List.of(email));
            mailMessage.setStatus(MailMessageStatus.PENDING);
            HashMap<String, String> params = new HashMap<>();
            params.put("subject", "Activate User");
            params.put("user", user.getFirstName() + " " + user.getLastName());
            params.put("link",  BASE_URL + "/activate-user?token=" + encodedToken);

            MailTemplate mailTemplate = mailTemplateRepository.findFirstByTemplateType(MailTemplateType.MAIL_TEMPLATE_REGISTRATION);
            String mailContent = mailTemplate.getContent();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                mailContent = mailContent.replace(placeholder, entry.getValue());
            }

            if (sendEmailMessage(email, params.get("subject"), mailContent)) {
                mailMessage.setStatus(MailMessageStatus.SUCCESS);
            } else {
                mailMessage.setStatus(MailMessageStatus.FAILED);
            }
            mailMessageService.save(mailMessage, MailTemplateType.MAIL_TEMPLATE_REGISTRATION, params);
        }

        return user;
    }

    public boolean sendEmailMessage(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(systemSettingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME).getValue());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.findFirstByEmail(email) != null;
    }
}
