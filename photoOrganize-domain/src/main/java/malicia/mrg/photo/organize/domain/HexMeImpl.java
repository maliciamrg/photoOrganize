package malicia.mrg.photo.organize.domain;

import malicia.mrg.photo.organize.domain.api.HexMe;

public class HexMeImpl implements HexMe {

    private CharSequence defautValue = "hex?";
    private String ok = "Yes_Hex";
    private String notOk = "Not_Yet_Hex";
    private String write = "write?";

    public HexMeImpl() {

    }

    public String getMsgReturn(String msg) {
        if (msg.contains(defautValue)) {
            return ok;
        }
        return notOk;
    }
}
