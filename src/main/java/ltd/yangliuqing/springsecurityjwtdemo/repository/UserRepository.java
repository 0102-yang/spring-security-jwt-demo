package ltd.yangliuqing.springsecurityjwtdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.Optional;

/**
 * @author yang
 */
@Repository
@Table(name = "user")
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

}
