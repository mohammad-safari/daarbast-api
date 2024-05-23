package ce.web.daarbast.security.userdetails;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<DaarbastUserDetails, String> {
    Optional<DaarbastUserDetails> findByUsername(String username);
}
