import config.ApiConfig;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestApi {

    public static void main(String[] args) throws Exception {
        System.out.println("Testing API connection...");
        System.out.println("API Key: " + ApiConfig.API_KEY.substring(0, Math.min(10, ApiConfig.API_KEY.length())) + "...");

        URL url = new URL(ApiConfig.BASE_URL + "/competitions/PL");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("X-Auth-Token", ApiConfig.API_KEY);

        int code = conn.getResponseCode();
        System.out.println("Response Code: " + code);

        if (code == 200) {
            System.out.println("API KEY IS VALID!");
        } else {
            System.out.println("API KEY FAILED! Check your key at https://www.football-data.org/");
        }
    }
}
