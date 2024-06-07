package ce.web.daarbast.security.userdetails;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ce.web.daarbast.model.user.User;
import lombok.Setter;

@DataJpaTest
@Setter(onMethod = @__({ @Autowired }))
public class UserDetailsRepositoryTest {
    private TestEntityManager entityManager;
    private UserDetailsRepository userDetailsRepository;

    @BeforeEach
    public void setUp() {
        var user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        var userId = entityManager.persistAndGetId(user, String.class);
        var userdetails = new DaarbastUserDetails();
        userdetails.setUserId(userId);
        userdetails.setUser(entityManager.find(User.class, userId));
        userdetails.setUsername("testuser");
        userdetails.setPassword("encodedtestpassword");
        entityManager.persist(userdetails);
    }

    @Test
    public void whenFindByUsername_thenReturnPassword() {
        assertEquals(userDetailsRepository.findByUsername("testuser").get().getPassword(), "encodedtestpassword");
    }
}
