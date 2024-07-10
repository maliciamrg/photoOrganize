package malicia.mrg.photo.organize.infrastructure.persistence;

import malicia.mrg.photo.organize.domain.spi.IWritePersistence;
import malicia.mrg.photo.organize.infrastructure.persistence.model.MasterTable;
import malicia.mrg.photo.organize.infrastructure.persistence.repository.MasterTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.UUID;

@Service
public class WritePersistence implements IWritePersistence {

    private MasterTableRepository masterTableRepository;

    public WritePersistence(MasterTableRepository masterTableRepository) {
        this.masterTableRepository = masterTableRepository;
    }

    @Override
    public UUID write(String msg) {
        MasterTable toAdd = new MasterTable(msg);
        MasterTable ret = masterTableRepository.save(toAdd);
        return ret.getId();
//        return UUID.randomUUID();
    }


}
