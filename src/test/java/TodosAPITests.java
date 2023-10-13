import okhttp3.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ConnectException;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosAPITests {

    OkHttpClient client = new OkHttpClient();

    @BeforeEach
    public void startApi() {
        try {
            String filePath = "C:\\Users\\Joey\\OneDrive - McGill University\\Desktop\\MCGILLCourses\\FALL2023\\ECSE429\\" +
                    "Application_Being_Tested\\runTodoManagerRestAPI-1.5.5.jar";
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

    //return all the instances of todo
    @Test
    public void todosGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //headers for all the instances of todo
    @Test
    public void todosHead() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .head()
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //Create todo without a ID using the field values in the body of the message
    @Test
    public void todosPost() throws Exception {
        String title = "Joey";
        boolean doneStatus = false;
        String description =  "doesn't like to work";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("doneStatus", doneStatus);
        jsonObject.put("description", description);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String responseTitle = (String) responseJson.get("title");
        boolean responseStatus = Boolean.parseBoolean((String) responseJson.get("doneStatus"));
        String responseDescription = (String) responseJson.get("description");

        assertEquals(title, responseTitle);
        assertEquals(doneStatus, responseStatus);
        assertEquals(description, responseDescription);
    }

    //method not allowed and not in api documentation
    @Test
    public void todosPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .put(dummyBody)
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void todosDelete() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void todosPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .patch(dummyBody)
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //return a specific instances of todo using an id
    @Test
    public void todosGetWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());

        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray todos = (JSONArray) responseJson.get("todos");

        for (Object todoObject : todos) {
            JSONObject todo = (JSONObject) todoObject;
            String id = (String) todo.get("id");
            String title = (String) todo.get("title");
            String description = (String) todo.get("description");
            String doneStatus = (String) todo.get("doneStatus");
            assertEquals("1", id);
            assertEquals("scan paperwork", title);
            assertEquals("", description);
            assertEquals("false", doneStatus);
        }
    }

    //get todo with invalid id
    @Test
    public void todosGetWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void todosHeadID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .head()
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;

        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //given an existing id, amend a specific instances of todo using a id with a body containing the fields to amend
    @Test
    public void todosPostWithValidID() throws Exception {
        String title = "Joey";
        boolean doneStatus = false;
        String description =  "doesn't like to work";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("doneStatus", doneStatus);
        jsonObject.put("description", description);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());

        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String id = (String) responseJson.get("id");
        String actualTitle = (String) responseJson.get("title");
        String actualDescription = (String) responseJson.get("description");
        String actualDoneStatus = (String) responseJson.get("doneStatus");
        assertEquals("1", id);
        assertEquals(title, actualTitle);
        assertEquals(description, actualDescription);
        assertEquals(Boolean.toString(doneStatus), actualDoneStatus);
    }

    @Test
    public void todosPostWithInvalidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void todosPutWithValidID() throws Exception {
        String title = "Joey";
        boolean doneStatus = false;
        String description =  "doesn't like to work";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("doneStatus", doneStatus);
        jsonObject.put("description", description);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .put(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());

        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String id = (String) responseJson.get("id");
        String actualTitle = (String) responseJson.get("title");
        String actualDescription = (String) responseJson.get("description");
        String actualDoneStatus = (String) responseJson.get("doneStatus");
        assertEquals("1", id);
        assertEquals(title, actualTitle);
        assertEquals(description, actualDescription);
        assertEquals(Boolean.toString(doneStatus), actualDoneStatus);
    }

    @Test
    public void todosPutWithInvalidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .put(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void todosDeleteWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void todosDeleteWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void todosPatchWithValidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .patch(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }







}
