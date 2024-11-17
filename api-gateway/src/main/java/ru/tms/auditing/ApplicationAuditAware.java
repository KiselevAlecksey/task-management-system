package ru.tms.auditing;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import ru.tms.user.User;

import java.util.Optional;

@Component
public class ApplicationAuditAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Предполагается, что имя пользователя - это ID (или можно извлечь ID из UserDetails)
            return Optional.of(((User) authentication.getPrincipal()).getId());
        }
        return Optional.empty(); // Вернуть пустое значение, если пользователь не аутентифицирован
    }
}

