import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LocationSharingService {

    private static final String BUCKET_NAME = "your-bucket-name";
    private static final Region REGION = Region.US_EAST_1; // Change as needed
    private static final S3Client s3Client = S3Client.builder()
            .region(REGION)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    public static void main(String[] args) {
        // Example location data
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", 37.7749);
        locationData.put("longitude", -122.4194);
        locationData.put("timestamp", Instant.now().toString());
        locationData.put("deviceId", "123456789");

        shareLocationToAws(locationData);
    }

    public static void shareLocationToAws(Map<String, Object> locationData) {
        try {
            // Convert location data to JSON format
            String jsonData = mapToJson(locationData);

            // Generate a unique object key (filename)
            String objectKey = "locations/" + locationData.get("deviceId") + "_" + Instant.now().toEpochMilli() + ".json";

            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonData));

            System.out.println("Location shared successfully to AWS S3!");
        } catch (Exception e) {
            System.err.println("Failed to share location: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String mapToJson(Map<String, Object> map) {
        StringBuilder jsonBuilder = new StringBuilder("{");
        map.forEach((key, value) -> {
            jsonBuilder.append("\"").append(key).append("\": \"").append(value).append("\",");
        });

        // Remove trailing comma and close the JSON object
        if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }
}
