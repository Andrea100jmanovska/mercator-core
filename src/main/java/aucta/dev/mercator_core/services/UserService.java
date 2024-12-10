package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.auth.JwtUtil;
import aucta.dev.mercator_core.auth.MyUserDetailsService;
import aucta.dev.mercator_core.auth.models.EditUserRequest;
import aucta.dev.mercator_core.enums.MailMessageStatus;
import aucta.dev.mercator_core.enums.MailTemplateType;
import aucta.dev.mercator_core.enums.SearchOperation;
import aucta.dev.mercator_core.exceptions.HttpException;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.models.dtos.UserDTO;
import aucta.dev.mercator_core.repositories.MailTemplateRepository;
import aucta.dev.mercator_core.repositories.UserRepository;
import aucta.dev.mercator_core.repositories.specifications.*;
import aucta.dev.mercator_core.utils.BasicSystemSettingsProps;
import aucta.dev.mercator_core.utils.Crypto;
import aucta.dev.mercator_core.validators.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    @Lazy
    private MailMessageService mailMessageService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private MailTemplateRepository mailTemplateRepository;

    @Autowired
    private UserValidator validator;

    @Autowired
    private CaptchaService captchaService;

    @Value("${app.base.url}")
    String BASE_URL;

    private final JavaMailSender mailSender;

    public UserService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public Page<UserDTO> getAll(Map<String, String> params, Pageable pageable) throws ParseException {
        UserSpecification userSpecification = new UserSpecification();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue()))
                if (entry.getKey().equals("dateCreated")) {
                    Calendar from = Calendar.getInstance();
                    from.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                    from.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    from.set(Calendar.HOUR_OF_DAY, 0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    Calendar to = Calendar.getInstance();
                    to.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    to.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    to.set(Calendar.HOUR_OF_DAY, 0);
                    to.set(Calendar.MINUTE, 0);
                    to.set(Calendar.SECOND, 0);
                    to.set(Calendar.MILLISECOND, 0);
                    to.add(Calendar.HOUR, 23);
                    to.add(Calendar.MINUTE, 59);
                    to.add(Calendar.SECOND, 59);

                    userSpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    userSpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                }else if (entry.getKey().equals("isEnabled")) {
                    Boolean value;
                    if (entry.getValue().equals("true"))
                        value = Boolean.TRUE;
                    else if (entry.getValue().equals("false"))
                        value = Boolean.FALSE;
                    else
                        value = null;
                    if (value != null)
                        userSpecification.add(new SearchCriteria(entry.getKey(), value, SearchOperation.EQUAL));
                } else {
                    userSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }
        }

        List<User> users = repo.findAll(userSpecification);

        List<UserDTO> dtos = new ArrayList<>();
        for(User user : users) {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setDateCreated(user.getDateCreated());
            dto.setIsEnabled(user.getIsEnabled());
            dtos.add(dto);
        }
        dtos.sort(Comparator.comparing(UserDTO::getDateCreated).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        List<UserDTO> paginatedList = dtos.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, dtos.size());

    }

    public User getUserByUsername(String username) {
        return repo.findFirstByUsername(username);
    }

    public UserDTO getUser() throws Exception {
        User user = getCurrentUser();
        if (user == null) throw new Exception("User not found!");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setDateCreated(user.getDateCreated());
        userDTO.setIsEnabled(user.getIsEnabled());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setOrganization(user.getOrganization());

        return userDTO;
    }

    public User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            Object principal = SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                String userId = ((CustomUserDetails) principal).getUserId();
                return get(userId);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public CustomUserDetails getCurrentUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            Object principal = SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public User get(String id) {
        return repo.getReferenceById(id);
    }

    public User getById(String id){
        return repo.findById(id).get();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User existingUser = get(user.getId());
            user.setGroups(existingUser.getGroups());
            if (!StringUtils.isEmpty(user.getNewPassword())) {
                user.setPassword(passwordEncoder.encode(user.getNewPassword()));
            } else {
                user.setPassword(repo.getById(user.getId()).getPassword());
            }
        }
        User savedUser = repo.save(user);
        return savedUser;
    }

    public User resetPassword(String username, String newPassword, String oldPassword) throws HttpException {
        User tmp = repo.findByUsername(username);
        if (tmp != null) {
            if (passwordEncoder.matches(oldPassword, tmp.getPassword())) {
                validator.validateUpdatePassword(newPassword);
                tmp.setPassword(passwordEncoder.encode(newPassword));
                return repo.save(tmp);
            } else {
                throw new IllegalArgumentException("Old password is not correct");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void forgotPasswordRequest(String email, String captchaResponse) throws Exception {
        if (!captchaService.verifyCaptcha(captchaResponse)) {
            throw new Exception("reCAPTCHA validation failed.");
        }
        User user = repo.findFirstByEmail(email);
        if (user != null) {
            Crypto crypto = new Crypto();
            ObjectMapper mapper = new ObjectMapper();
            ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
            forgotPasswordToken.setUserId(user.getId());
            //set valid until 30 minutes from now
            forgotPasswordToken.setValidUntil(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            String token = crypto.encrypt(mapper.writeValueAsString(forgotPasswordToken));
            //send email with token
            MailMessage mailMessage = new MailMessage();
            mailMessage.setSender(systemSettingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME).getValue());
            mailMessage.setReceivers(List.of(email));
            mailMessage.setStatus(MailMessageStatus.PENDING);
            HashMap<String, String> params = new HashMap<>();
            params.put("subject", "Forgot password request");
            params.put("link", BASE_URL + "/reset-password?token=" + token);
            params.put("user", user.getDisplayName());

            MailTemplate mailTemplate = mailTemplateRepository.findFirstByTemplateType(MailTemplateType.MAIL_TEMPLATE_FORGOT_PASSWORD);
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

            mailMessageService.save(mailMessage, MailTemplateType.MAIL_TEMPLATE_FORGOT_PASSWORD, params);
        }
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

    public Boolean checkIfPasswordRequestIsValid(String token) throws Exception {
        Crypto crypto = new Crypto();
        ObjectMapper mapper = new ObjectMapper();
        try {
            ForgotPasswordToken forgotPasswordToken = mapper.readValue(crypto.decrypt(URLDecoder.decode(token.replaceAll("%20", "%2b"))), ForgotPasswordToken.class);
            if (forgotPasswordToken.getValidUntil().after(new Date())) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    public User forgotPasswordReset(String token, String newPassword) throws Exception {
        Crypto crypto = new Crypto();
        ObjectMapper mapper = new ObjectMapper();
        try {
            ForgotPasswordToken forgotPasswordToken = mapper.readValue(crypto.decrypt(URLDecoder.decode(token.replaceAll("%20", "%2b"))), ForgotPasswordToken.class);
            if (forgotPasswordToken.getValidUntil().after(new Date())) {
                User user = repo.getById(forgotPasswordToken.getUserId());
                validator.validateUpdatePassword(newPassword);
                user.setPassword(passwordEncoder.encode(newPassword));
                repo.save(user);
                return user;
            } else {
                throw new Exception("Token is not valid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Token is not valid");
        }
    }


    public ResponseEntity updateUser(String token, EditUserRequest request) throws Exception {
        Optional<User> optionalUser = repo.findById(getUser().getId());
        if(optionalUser.isEmpty()) throw new Error("User not found!");

        User user = optionalUser.get();
        final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        validator.validateUpdateUserInfo(request);
        Boolean validated = jwtTokenUtil.validateToken(token, userDetails);

        if(validated){
            checkSanitization(request);

            user.setUsername(request.getUsername());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setPhoneNumber(request.getPhoneNumber());

            repo.save(user);
        } else {
            throw new Error("User not authorized for this action!");
        }

        return ResponseEntity.ok("User successfully updated!");
    }


    public boolean sendEmailForAnalyzing(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
         try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(systemSettingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME).getValue());
            helper.setTo("ppetko901@gmail.com");
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }

    private void checkSanitization(EditUserRequest request) {
        List<String> sanitizedFields = new ArrayList<>();

        if (isSanitized(request.getUsername())) sanitizedFields.add("username");
        if (isSanitized(request.getFirstName())) sanitizedFields.add("firstName");
        if (isSanitized(request.getLastName())) sanitizedFields.add("lastName");
        if (isSanitized(request.getEmail())) sanitizedFields.add("email");
        if (isSanitized(request.getPhoneNumber())) sanitizedFields.add("phoneNumber");
        if (isSanitized(request.getTwitterUrl())) sanitizedFields.add("twitterUrl");
        if (isSanitized(request.getLinkedInUrl())) sanitizedFields.add("linkedInUrl");

        if (!sanitizedFields.isEmpty()) {
            throw new IllegalArgumentException("Input contains prohibited content in fields: " + String.join(", ", sanitizedFields));
        }
    }

    private boolean isSanitized(String input) {
        if (input == null) {
            return false;
        }
        // Check if the input changed after sanitization
        String sanitized = input.replaceAll("(?i)<script.*?>.*?</script>", "")
                .replaceAll("(?i)<.*?>", "")
                .replaceAll("(?i)on\\w+=", "")
                .replaceAll("(?i)javascript:", "")
                .replaceAll("(?i)vbscript:", "")
                .replaceAll("(?i)expression\\(", "")
                .replaceAll("(?i)eval\\((.*)\\)", "");

        return !input.equals(sanitized);
    }

}