import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class ZoomInfoIntegration {

    // Replace with your actual ZoomInfo API Key
    private static final String ZOOMINFO_API_KEY = "your_zoominfo_api_key";
    private static final String ZOOMINFO_API_URL = "https://api.zoominfo.com/person/contact";

    public static void main(String[] args) {
        try {
            // Create the payload with the mobile number and other contact info
            JSONObject payload = new JSONObject();
            payload.put("phoneNumber", "+1234567890");  // Mobile number to be shared
            payload.put("firstName", "John");
            payload.put("lastName", "Doe");
            payload.put("email", "john.doe@example.com");

            // Send the data to ZoomInfo
            sendToZoomInfo(payload.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToZoomInfo(String jsonPayload) {
        try {
            URL url = new URL(ZOOMINFO_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Set the Authorization header with the API key
            conn.setRequestProperty("Authorization", "Bearer " + ZOOMINFO_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            // Send the payload
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response from ZoomInfo
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("ZoomInfo Response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
