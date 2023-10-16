import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(CommonTests.class)
public class ProjectsAPITests {
    static String url = "http://localhost:4567/";

    /* Projects (GET, HEAD, POST) */
    // Get all projects available
    @Test
    public void projectsGetRequest() throws IOException {
        Request request = new Request.Builder()
                .url(url + "projects")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertNotNull(response.body().string());
        assertEquals(200, response.code());
    }

    // Try executing a GET request with an incorrect URL
    @Test
    public void projectsGetWrongRequest() throws IOException {
        Request request = new Request.Builder()
                .url(url + "project")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
    }

    // Receive headers
    @Test
    public void projectsHeadRequest() throws IOException {
        Request request = new Request.Builder()
                .url(url + "projects")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertNotNull(response.headers());
        assertEquals("application/json", response.headers().get("Content-Type"));
        assertEquals(200, response.code());
    }

    // Create a new project using title and description
    @Test
    public void projectsPostRequest() throws IOException, ParseException {
        // Create a JSON object for the request body
        JSONObject obj = new JSONObject();
        obj.put("title", "MyProject");
        obj.put("description", "This is my project.");

        RequestBody requestBody = RequestBody.create(obj.toString(),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url + "projects")
                .post(requestBody)
                .build();

        // Ensure response went through (201)
        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(201, response.code());
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(responseBody);

        // Verify attributes were correctly set
        assertEquals("MyProject", responseObj.get("title"));
        assertEquals("This is my project.", responseObj.get("description"));
    }

    // Putting a non-existent field; field not found error
    @Test
    public void projectsPostWrongFieldRequest() throws IOException, ParseException {
        // Create a JSON object for the request body
        JSONObject obj = new JSONObject();
        obj.put("tile", "MyProject");

        RequestBody requestBody = RequestBody.create(obj.toString(),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url + "projects")
                .post(requestBody)
                .build();

        // Ensure response was bad request
        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(400, response.code());
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(responseBody);
        JSONArray arr = (JSONArray) parser.parse(responseObj.get("errorMessages").toString());

        // Verify correct message
        assertEquals("Could not find field: tile", arr.get(0));
    }

    /* Projects (with ID) */
    // Get the first project in the system
    @Test
    public void projectsIDGetRequest() throws IOException, ParseException {
        // Get the default project when a system starts (ID = 1)
        int id = 1;

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        JSONParser parser = new JSONParser();

        // Parse the array of JSON objects step by step to check id
        JSONObject responseObj = (JSONObject) parser.parse(response.body().string());
        JSONArray jsonArray = (JSONArray) parser.parse(responseObj.get("projects").toString());
        JSONObject project = (JSONObject) parser.parse(jsonArray.get(0).toString());

        assertEquals(200, response.code());
        assertEquals("1", project.get("id"));
    }

    // Get project request with ID that doesn't exist
    @Test
    public void projectsIDGetInvalidRequest() throws IOException, ParseException {
        // Get an invalid project id
        int id = 2;
        String error = "Could not find an instance with projects/" + id;

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());

        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(response.body().string());
        JSONArray responseArr = (JSONArray) parser.parse(responseObj.get("errorMessages").toString());

        // Check if an error was returned
        assertEquals(error, responseArr.get(0));

    }

    @Test
    public void projectsIDHeadRequest() throws IOException {
        // Get the default project when a system starts (ID = 1)
        int id = 1;

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(200, response.code());
        assertNotNull(response.headers());
    }

    // Regular post request for original project
    @Test
    public void projectsIDPostRequest() throws IOException, ParseException {
        // Get the default project when a system starts (ID = 1)
        int id = 1;

        // Values for testing
        String newTitle = "School Work";
        String newDescription = "This is my school work.";
        boolean newActiveStatus = true;

        // Create a JSON object for the request body
        JSONObject obj = new JSONObject();
        obj.put("title", newTitle);
        obj.put("description", newDescription);
        obj.put("active", newActiveStatus);

        // Create the request
        RequestBody requestBody = RequestBody.create(obj.toString(),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        String responseBody = response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(responseBody);

        // Check if the project was correctly changed
        assertEquals(newTitle, responseObj.get("title"));
        assertEquals(newDescription, responseObj.get("description"));
        assertEquals("" + newActiveStatus, responseObj.get("active"));
    }

    // Post request for project with ID that does not exist
    @Test
    public void projectsIDPostErrRequest() throws IOException, ParseException {
        // Get a project that does not exist to test
        int id = 2;

        // Values for testing
        String error = "No such project entity instance with GUID or ID " + id + " found";

        // Create a JSON object for the request body
        JSONObject obj = new JSONObject();

        // Create the request
        RequestBody requestBody = RequestBody.create(obj.toString(),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());

        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(response.body().string());
        JSONArray responseArr = (JSONArray) parser.parse(responseObj.get("errorMessages").toString());

        // Check if an error was returned
        assertEquals(error, responseArr.get(0));
    }

    // Create a new project then update it
    @Test
    public void projectsIDPutRequest() throws IOException, ParseException, InterruptedException {
        // Create a project first
        // Get the default project when a system starts (ID = 1)
        int id = 2;

        JSONObject objPost = new JSONObject();
        RequestBody requestBodyPost = RequestBody.create(objPost.toString(),
                MediaType.parse("application/json"));

        Request requestPost = new Request.Builder()
                .url(url + "projects")
                .post(requestBodyPost)
                .build();

        CommonTests.getClient().newCall(requestPost).execute();

        // Values for testing
        String newTitle = "School Work";
        String newDescription = "This is my school work.";
        boolean newActiveStatus = true;

        // Create a new JSON object for the request body
        JSONObject objPut = new JSONObject();
        objPut.put("title", newTitle);
        objPut.put("description", newDescription);
        objPut.put("active", newActiveStatus);

        // Create the PUT request
        RequestBody requestBodyPut = RequestBody.create(objPut.toString(),
                MediaType.parse("application/json"));

        Request requestPut = new Request.Builder()
                .url(url + "projects/" + id)
                .put(requestBodyPut)
                .build();

        Response response = CommonTests.getClient().newCall(requestPut).execute();
        assertEquals(200, response.code());

        String responseBody = response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(responseBody);

        // Check if the project was correctly changed
        assertEquals(newTitle, responseObj.get("title"));
        assertEquals(newDescription, responseObj.get("description"));
        assertEquals("" + newActiveStatus, responseObj.get("active"));
    }

    // Delete the original project
    @Test
    public void projectsIDDeleteRequest() throws IOException {
        // Get the default project when a system starts (ID = 1)
        int id = 1;

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
    }

    // Try to delete a project not found
    @Test
    public void projectsIDDeleteInvalidRequest() throws IOException, ParseException {
        // Use an ID that doesn't exist
        int id = 3;

        Request request = new Request.Builder()
                .url(url + "projects/" + id)
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());

        JSONParser parser = new JSONParser();
        String responseBody = response.body().string();
        JSONObject responseObj = (JSONObject) parser.parse(responseBody);
        JSONArray arr = (JSONArray) parser.parse(responseObj.get("errorMessages").toString());

        // Verify correct message
        assertEquals("Could not find any instances with projects/" + id, arr.get(0));
    }

}
