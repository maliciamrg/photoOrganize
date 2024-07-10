package malicia.mrg.photo.organize.infrastructure.persistence.repository;

import malicia.mrg.photo.organize.infrastructure.persistence.model.MasterTable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface MasterTableRepository extends CrudRepository<MasterTable, UUID> {

}