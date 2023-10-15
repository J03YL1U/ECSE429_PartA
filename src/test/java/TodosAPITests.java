import okhttp3.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(CommonTests.class)
public class TodosAPITests {

    //----------------------------------------------------------- http://localhost:4567/todos -----------------------------------------------------------//
    //return all the instances of todo
    @Test
    public void testTodosGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //headers for all the instances of todo
    @Test
    public void testTodosHead() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //Create todo without a ID using the field values in the body of the message
    @Test
    public void testTodosPost() throws Exception {
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

        Response response = CommonTests.getClient().newCall(request).execute();
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
    public void testTodosPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .put(dummyBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testTodosDelete() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testTodosPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .patch(dummyBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/todos/id -----------------------------------------------------------//

    //return a specific instances of todo using an id
    @Test
    public void testTodosGetWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
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
    public void testTodosGetWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosHeadID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //given an existing id, amend a specific instances of todo using a id with a body containing the fields to amend
    @Test
    public void testTodosPostWithValidID() throws Exception {
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

        Response response = CommonTests.getClient().newCall(request).execute();
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
    public void testTodosPostWithInvalidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosPutWithValidID() throws Exception {
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

        Response response = CommonTests.getClient().newCall(request).execute();
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
    public void testTodosPutWithInvalidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .put(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosDeleteWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/3")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosDeleteWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testTodosPatchWithValidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1")
                .patch(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/todos/id/tasksof -----------------------------------------------------------//
    @Test
    public void testTodosTasksOfHeadWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/tasksof")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testTodosTasksOfHeadWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/5/tasksof")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testTodosTasksOfPostWithValidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/2/tasksof")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(201, response.code());
        assertEquals("Created", response.message());
    }

    @Test
    public void testTodosTasksOfPostWithInvalidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/6/tasksof")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosTasksOfPut() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/tasksof")
                .put(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testTodosTasksOfDelete() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/tasksof")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testTodosTasksOfPatch() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/tasksof")
                .patch(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/todos/id/categories -----------------------------------------------------------//

    @Test
    public void testTodosCategoriesGetWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/categories")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assert response.body() != null;
        String responseBody = response.body().string();

        assertEquals(200, response.code());
        assertEquals("OK", response.message());

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray categories = (JSONArray) responseJson.get("categories");

        for (Object categoryObj : categories) {
            JSONObject category = (JSONObject) categoryObj;
            String id = (String) category.get("id");
            String title = (String) category.get("title");
            String description = (String) category.get("description");
            assertEquals("1", id);
            assertEquals("Office", title);
            assertEquals("", description);
        }
    }

    @Test
    public void testTodosCategoriesGetWith2ndValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/2/categories")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assert response.body() != null;
        String responseBody = response.body().string();

        assertEquals(200, response.code());
        assertEquals("OK", response.message());

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray categories = (JSONArray) responseJson.get("categories");

        assertEquals(0, categories.size());
    }

    @Test
    public void testTodosCategoriesHeadWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/categories")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testTodosCategoriesPostWithValidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/2/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(201, response.code());
        assertEquals("Created", response.message());
    }

    @Test
    public void testTodosCategoriesPostWithInvalidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/4/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosCategoriesPostWithInvalidCategoryID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "9");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testTodosCategoriesPutWithValidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/categories")
                .put(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testTodosCategoriesDeleteWithValidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/categories")
                .delete(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testTodosCategoriesPatchWithValidID() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos/1/categories")
                .patch(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }
}
