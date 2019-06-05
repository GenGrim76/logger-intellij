import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;

public class Server {

    public static String login (User user) {

        String token = null;

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(user.getServerAddress() + "/login" );

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("email", user.getEmail()));
        params.add(new BasicNameValuePair("password", user.getPassword()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }

        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String response_string = EntityUtils.toString(entity);
                JSONObject result = new JSONObject(response_string);
                try {
                    token = result.getString("token");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (ClientProtocolException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return token;
    }

    public static void post(Metrics metrics){
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(metrics.getUser().getServerAddress() + "/activity");

            JSONArray activities = new JSONArray();

            for (Metric metric : metrics.getMetrics()) {
                JSONObject json_metric = new JSONObject();
                json_metric.put("executable_name", metric.tabName);
                json_metric.put("start_time", metric.startDate);
                json_metric.put("end_time", metric.endDate);
                json_metric.put("ip_address", metrics.getIp());
                json_metric.put("mac_address", metrics.getMac());
                json_metric.put("activity_type", metric.activity_type);
                json_metric.put("value", metric.value);
                activities.put(json_metric);
            }
            JSONObject data = new JSONObject();
            data.put("activities", activities);

            StringWriter out = new StringWriter();
            data.write(out);

            String jsonText = out.toString();

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("activity", jsonText));
            httppost.addHeader("Authorization", String.format("Token %s", metrics.getUser().getToken()));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.out.println(e);
            }

            HttpResponse response;

            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            Integer responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == 201) {
                System.out.println("Post Successful");
            } else if (responseCode == 401) {
                System.out.println("error");
            }
        } catch (ConnectException e) {
            System.out.println(e);
        } catch (ClientProtocolException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (JSONException e) {
            System.out.println(e);
        }
        metrics.reset();
    }
}
