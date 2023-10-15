import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.Random.class)
public class ProjectsTestAPI {
    OkHttpClient client = new OkHttpClient();

    static String url = "http://localhost:4567/";

    @BeforeEach
    public void startApi() {
        try {
            String filePath = "../../../runTodoManagerRestAPI-1.5.5.jar";
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", filePath);
            processBuilder.start();

            Thread.sleep(200);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:4567/")
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Assertions.fail("API is not running");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Shutdown after each test to reset the system and return to initial state
    @AfterEach
    public void shutdownAPI() throws IOException {
        try{
            Request request = new Request.Builder()
                    .url("http://localhost:4567/shutdown")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
        }catch(ConnectException e){
            //server doesn't respond anymore after shutdown
        }
    }

    /* Projects (GET, HEAD, POST) */
    @Test
    public void projectsGetRequest() throws IOException {
        Request request = new Request.Builder()
                .url(url + "projects")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertNotNull(response.body().string());
        assertEquals(200, response.code());
    }
//
//    @Test
//    public void projectsHeadRequest() throws IOException {
//        Request request = new Request.Builder()
//                .url(url + "projects")
//                .head()
//                .build();
//
//        Response response = client.newCall(request).execute();
//        assertNotNull(response.headers());
//        assertEquals(200, response.code());
//    }
//
//    @Test
//    public void projectsPostRequest() throws IOException, ParseException {
//        // Create a JSON object for the request body
//        JSONObject obj = new JSONObject();
//        obj.put("title", "MyProject");
//        obj.put("description", "This is my project.");
//
//        RequestBody requestBody = RequestBody.create(obj.toString(),
//                MediaType.parse("application/json"));
//
//        Request request = new Request.Builder()
//                .url(url + "projects")
//                .post(requestBody)
//                .build();
//
//        // Ensure response went through (201)
//        Response response = client.newCall(request).execute();
//        assertEquals(201, response.code());
//        String responseBody = response.body().string();
//
//        JSONParser parser = new JSONParser();
//        JSONObject responseObj = (JSONObject) parser.parse(responseBody);
//
//        // Verify attributes were correctly set
//        assertEquals("MyProject", responseObj.get("title"));
//        assertEquals("This is my project.", responseObj.get("description"));
//    }
//
//    /* Projects (with ID) */
//    @Test
//    public void projectsIDGetRequest() throws IOException, ParseException {
//        // Get the default project when a system starts (ID = 1)
//        int id = 1;
//
//        Request request = new Request.Builder()
//                .url(url + "projects/" + id)
//                .get()
//                .build();
//
//        Response response = client.newCall(request).execute();
//
//        JSONParser parser = new JSONParser();
//
//        // Parse the array of JSON objects step by step to check id
//        JSONObject responseObj = (JSONObject) parser.parse(response.body().string());
//        JSONArray jsonArray = (JSONArray) parser.parse(responseObj.get("projects").toString());
//        JSONObject project = (JSONObject) parser.parse(jsonArray.get(0).toString());
//
//        assertEquals(200, response.code());
//        assertEquals("1", project.get("id"));
//    }
//
//    @Test
//    public void projectsIDHeadRequest() throws IOException {
//        // Get the default project when a system starts (ID = 1)
//        int id = 1;
//
//        Request request = new Request.Builder()
//                .url(url + "projects/" + id)
//                .head()
//                .build();
//
//        Response response = client.newCall(request).execute();
//
//        assertEquals(200, response.code());
//        assertNotNull(response.headers());
//    }
//
//    @Test
//    public void projectsIDPostErrRequest() throws IOException, ParseException {
//        // Get a project that does not exist to test
//        int id = 2;
//
//        // Values for testing
//        String newTitle = "School Work";
//        String error = "No such project entity instance with GUID or ID " + id + " found";
//
//        // Create a JSON object for the request body
//        JSONObject obj = new JSONObject();
//
//        // Create the request
//        RequestBody requestBody = RequestBody.create(obj.toString(),
//                MediaType.parse("application/json"));
//
//        Request request = new Request.Builder()
//                .url(url + "projects/" + id)
//                .post(requestBody)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        assertEquals(404, response.code());
//
//        JSONParser parser = new JSONParser();
//        JSONObject responseObj = (JSONObject) parser.parse(response.body().string());
//        JSONArray responseArr = (JSONArray) parser.parse(responseObj.get("errorMessages").toString());
//
//        // Check if an error was returned
//        assertEquals(error, responseArr.get(0));
//    }
//
//    @Test
//    public void projectsIDPutRequest() throws IOException, ParseException {
//        // Get the default project when a system starts (ID = 1)
//        int id = 1;
//
//        // Values for testing
//        String newTitle = "School Work";
//        String newDescription = "This is my school work.";
//        boolean newActiveStatus = true;
//
//        // Create a JSON object for the request body
//        JSONObject obj = new JSONObject();
//        obj.put("title", newTitle);
//        obj.put("description", newDescription);
//        obj.put("active", newActiveStatus);
//
//        // Create the request
//        RequestBody requestBody = RequestBody.create(obj.toString(),
//                MediaType.parse("application/json"));
//
//        Request request = new Request.Builder()
//                .url(url + "projects/" + id)
//                .post(requestBody)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        assertEquals(200, response.code());
//
//        String responseBody = response.body().string();
//        JSONParser parser = new JSONParser();
//        JSONObject responseObj = (JSONObject) parser.parse(responseBody);
//
//        // Check if the project was correctly changed
//        assertEquals(newTitle, responseObj.get("title"));
//        assertEquals(newDescription, responseObj.get("description"));
//        assertEquals("" + newActiveStatus, responseObj.get("active"));
//    }
//
//    @Test
//    public void projectsIDDeleteRequest() throws IOException {
//        // Get the default project when a system starts (ID = 1)
//        int id = 1;
//
//        Request request = new Request.Builder()
//                .url(url + "projects/" + id)
//                .delete()
//                .build();
//
//        Response response = client.newCall(request).execute();
//        assertEquals(200, response.code());
//    }

}
