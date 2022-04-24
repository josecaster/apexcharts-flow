package sr.we.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import sr.we.data.entity.SamplePerson;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

}