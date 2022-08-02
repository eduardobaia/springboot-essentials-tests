package academy.devdojo.springboot2.repository;

import academy.devdojo.springboot2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String name);

}
