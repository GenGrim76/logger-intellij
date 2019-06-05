import java.time.Instant;

public class Metric {
    public String activity_type;
    public String tabName;
    public Instant startDate;
    public Instant endDate;
    public String value;

    public Metric(String at, String tn, Instant sd, Instant ed, String v){
        this.activity_type = at;
        this.tabName = tn;
        this.startDate = sd;
        this.endDate = ed;
        this.value = v;
    }
}
