import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class GeoLocationService {

    public static JSONObject getLocation(String ip) {
        String apiURL = "https://ipinfo.io/" + ip + "/geo";
        JSONObject locationData = null;

        try {
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the response to JSON
            locationData = new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationData;
    }

    public static void main(String[] args) {
        // Example IP for testing; use "127.0.0.1" or dynamic IP retrieval in production
        JSONObject location = getLocation("8.8.8.8");
        if (location != null) {
            System.out.println("Location Data: " + location.toString());
            // Pass the location to OneTrust
            passLocationToOneTrust(location);
        }
    }

    private static void passLocationToOneTrust(JSONObject location) {
        String country = location.optString("country", "unknown");
        String region = location.optString("region", "unknown");
        String city = location.optString("city", "unknown");

        // Embed location information in JavaScript code for OneTrust
        String oneTrustScript = String.format(
            "<script>\n" +
            "window.OneTrustLocationData = { country: '%s', region: '%s', city: '%s' };\n" +
            "window.OnetrustActiveGroups = 'C0001, C0002';\n" +
            "</script>",
            country, region, city
        );

        System.out.println("OneTrust Script:\n" + oneTrustScript);
    }
}
