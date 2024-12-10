package aucta.dev.mercator_core.models.audit;

import aucta.dev.mercator_core.SpringContext;
import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.services.UserService;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class EnversListener implements RevisionListener {

    private UserService getUserService() {
        return SpringContext.getBean(UserService.class);
    }

    /**
     * Extend basic functionallity of RevisionListener with adding User.
     *
     * @param revisionEntity an object
     */
    @Override
    public void newRevision(Object revisionEntity) {
        EnversRevisionEntity exampleRevEntity = (EnversRevisionEntity) revisionEntity;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails) {
                User user = getUserService().getUserByUsername((
                        (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                ).getUsername());
                if (user != null)
                    exampleRevEntity.setUser(user);
            } else {
                exampleRevEntity.setUser(null);
            }
        }
    }
}
