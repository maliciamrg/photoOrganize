package mrg.param.electx;

import java.util.ArrayList;
import java.util.List;

public class Workflow {
    public List<String> TODO = new ArrayList<>();
    public List<String> NOTTODO = new ArrayList<>();
    public Boolean IS_DRY_RUN;

    @Override
    public String toString() {
        return "Workflow{" +
                "TODO=" + TODO +
                ", NOTTODO=" + NOTTODO +
                ", IS_DRY_RUN=" + IS_DRY_RUN +
                '}';
    }
}
