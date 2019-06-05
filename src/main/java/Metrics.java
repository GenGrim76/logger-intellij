import java.util.ArrayList;

public class Metrics {
    ArrayList<Metric> metrics;
    private String Ip;
    private String Mac;
    private User user;

    public Metrics(User u){
        this.metrics = new ArrayList<Metric>();
        this.Ip = GetNetworkAddress.GetAddress("ip");
        this.Mac = GetNetworkAddress.GetAddress("mac");
        this.user=u;
    }

    public void sessionInfo(){
        System.out.println("Ip: " + GetNetworkAddress.GetAddress("ip"));
        System.out.println("Mac: " + GetNetworkAddress.GetAddress("mac"));
    }


    public void addMetric(Metric a){
        this.metrics.add(a);
    }

    public ArrayList<Metric> getMetrics(){
        return this.metrics;
    }
    public String getIp(){
        return this.Ip;
    }

    public String getMac(){
        return this.Mac;
    }

    public User getUser(){
        return this.user;
    }

    public void reset(){
        this.metrics = new ArrayList<Metric>();
    }
}
