import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SegmentIntegration {

    // Replace with your Segment Write Key
    private static final String SEGMENT_WRITE_KEY = "your_segment_write_key";
    private static final String SEGMENT_API_URL = "https://api.segment.io/v1/track";

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

    public static void sendToSegment(JSONObject location) {
        try {
            // Prepare the Segment track event payload
            JSONObject event = new JSONObject();
            event.put("event", "User Location Detected");
            event.put("anonymousId", "user-anonymous-id"); // Replace with actual user or session ID

            // Add properties from the location data
            JSONObject properties = new JSONObject();
            properties.put("country", location.optString("country", "unknown"));
            properties.put("region", location.optString("region", "unknown"));
            properties.put("city", location.optString("city", "unknown"));
            properties.put("ip", location.optString("ip", "unknown"));
            event.put("properties", properties);

            // Send the event to Segment
            postToSegment(event.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void postToSegment(String jsonPayload) {
        try {
            URL url = new URL(SEGMENT_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Set the authentication header with the write key
            String basicAuth = "Basic " + Base64.getEncoder()
                    .encodeToString((SEGMENT_WRITE_KEY + ":").getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/json");

            // Send the payload
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Segment Response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example IP for testing; use "127.0.0.1" or dynamic IP retrieval in production
        JSONObject location = getLocation("8.8.8.8");
        if (location != null) {
            System.out.println("Location Data: " + location.toString());
            // Send location data to Segment
            sendToSegment(location);
        }
    }
}
