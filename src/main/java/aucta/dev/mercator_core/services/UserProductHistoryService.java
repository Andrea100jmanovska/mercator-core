package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.SearchOperation;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.UserProductHistory;
import aucta.dev.mercator_core.models.dtos.UserProductHistoryDTO;
import aucta.dev.mercator_core.repositories.UserProductHistoryRepository;
import aucta.dev.mercator_core.repositories.specifications.SearchCriteria;
import aucta.dev.mercator_core.repositories.specifications.UserProductHistorySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static aucta.dev.mercator_core.auth.AuthUtils.getCurrentUser;

@Service
public class UserProductHistoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProductHistoryRepository userProductHistoryRepository;

    public void logProductHistory(Product product) {
        User currentUser = userService.getCurrentUser();
        if (!userProductHistoryRepository.existsByUserAndProduct(currentUser, product)) {
            UserProductHistory historyEntry = new UserProductHistory();
            historyEntry.setUser(currentUser);
            historyEntry.setProduct(product);
            historyEntry.setDateAccessed(new Date());

            userProductHistoryRepository.save(historyEntry);
        }
    }

    public List<UserProductHistoryDTO> getUserHistoryProductsAccessWithoutPaging(Map<String, String> params, Boolean topValuesOnly) throws ParseException {
        UserProductHistorySpecification userProductHistorySpecification = new UserProductHistorySpecification();
        userProductHistorySpecification.add(new SearchCriteria("user.id", getCurrentUser().getId(), SearchOperation.MATCH));
        Boolean isFavorited = null;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
                if (entry.getKey().equals("dateAccessed")) {
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

                    userProductHistorySpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    userProductHistorySpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                } if (entry.getKey().equals("isFavorited")) {
                    if (entry.getValue().equals("true"))
                        isFavorited = Boolean.TRUE;
                    else if (entry.getValue().equals("false"))
                        isFavorited = Boolean.FALSE;
                } else {
                    userProductHistorySpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }

            }
        }
        List<UserProductHistory> userProductHistories = userProductHistoryRepository.findAll(userProductHistorySpecification);

        if (isFavorited != null) {
            if(isFavorited){
                userProductHistories = userProductHistories.stream()
                        .filter(product -> product.getProduct().getUsers().contains(getCurrentUser()))
                        .collect(Collectors.toList());
            } else {
                userProductHistories = userProductHistories.stream()
                        .filter(product -> !product.getProduct().getUsers().contains(getCurrentUser()))
                        .collect(Collectors.toList());
            }

        }

        List<UserProductHistoryDTO> userProductHistoryDTOS = userProductHistories.stream()
                .map(access -> new UserProductHistoryDTO(access, getCurrentUser()))
                .collect(Collectors.toList());

        userProductHistoryDTOS.sort(Comparator.comparing(UserProductHistoryDTO::getDateAccessed).reversed());

        if(topValuesOnly != null && topValuesOnly) {
            return userProductHistoryDTOS.stream()
                    .collect(Collectors.toMap(UserProductHistoryDTO::getDateAccessed, dto -> dto, (existing, replacement) -> existing, LinkedHashMap::new))
                    .values()
                    .stream()
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            return userProductHistoryDTOS;
        }

    }

    public Page<UserProductHistoryDTO> getUserHistoryProducts(Pageable pageable) {
        UserProductHistorySpecification userProductHistorySpecification = new UserProductHistorySpecification();
        userProductHistorySpecification.add(new SearchCriteria("user.id", getCurrentUser().getId(), SearchOperation.MATCH));
        List<UserProductHistory> userProductHistories = userProductHistoryRepository.findAll(userProductHistorySpecification);

        List<UserProductHistoryDTO> userProductHistoryDTOS = userProductHistories.stream()
                .map(access -> new UserProductHistoryDTO(access, getCurrentUser()))
                .collect(Collectors.toList());

        userProductHistoryDTOS.sort(Comparator.comparing(UserProductHistoryDTO::getDateAccessed).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), userProductHistoryDTOS.size());

        List<UserProductHistoryDTO> paginatedList = userProductHistoryDTOS.subList(start, end);
        return new PageImpl<>(paginatedList, pageable, userProductHistoryDTOS.size());
    }
}

