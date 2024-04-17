package malicia.mrg.photo.organize.infrastructure.persistence.repository;

import malicia.mrg.photo.organize.infrastructure.persistence.model.MasterTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MasterTableRepository extends JpaRepository<MasterTable, UUID> {

}