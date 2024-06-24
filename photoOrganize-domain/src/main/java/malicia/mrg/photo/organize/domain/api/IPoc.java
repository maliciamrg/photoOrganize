package malicia.mrg.photo.organize.domain.api;

import malicia.mrg.photo.organize.domain.Analysis;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface IPoc {
    List<String> getPhysicalFilesNotLogic();

    Analysis analyseFilePhysiqueAndLogic();

    Analysis synchroDatabase() throws SQLException, IOException, ParseException;
}
