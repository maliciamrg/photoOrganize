package malicia.mrg.photo.organize.domain.api;

import malicia.mrg.photo.organize.domain.dto.Analysis;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IKeyAction {
    Analysis makeActionRapprochement() ;

    Analysis makeActionMoveToRepertory() ;


}
