package sr.we.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import sr.we.data.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}