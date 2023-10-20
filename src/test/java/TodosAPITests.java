import okhttp3.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.json.XML;

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
        // check todo object before post request
        JSONArray initialTodos = fetchTodoList("http://localhost:4567/todos");

        // Create a new TODO item
        String title = "Joey";
        boolean doneStatus = false;
        String description = "doesn't like to work";

        JSONObject newTodo = createNewTodoObject(title, doneStatus, description);

        //Send a POST request to add the new TODO item
        sendPostRequestToCreateTodoItem(newTodo, "http://localhost:4567/todos", 201);

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        //Verify the previous TODO items are still there, and the new one is created
        verifyTodoItemsAreUpdated(initialTodos, updatedTodos);
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
            boolean doneStatus = Boolean.parseBoolean(String.valueOf(todo.get("doneStatus")));
            assertEquals("1", id);
            assertEquals("scan paperwork", title);
            assertEquals("", description);
            assertFalse(doneStatus);
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
        // Step 2: Create a new TODO item
        String title = "Joey";
        boolean doneStatus = false;
        String description = "doesn't like to work";

        JSONObject newTodo = createNewTodoObject(title, doneStatus, description);

        //Send a POST request to add the new TODO item
        sendPostRequestToCreateTodoItem(newTodo, "http://localhost:4567/todos/1", 200);

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        //Verify if new list is correct
        for(Object todoObject: updatedTodos){
            JSONObject todo = (JSONObject) todoObject;
            String id = (String) todo.get("id");
            String titleCheck = (String) todo.get("title");
            String descriptionCheck = (String) todo.get("description");
            boolean doneStatusCheck = Boolean.parseBoolean(String.valueOf(todo.get("doneStatus")));

            if(id.equals("1")){
                assertEquals(title, titleCheck);
                assertEquals(description, descriptionCheck);
                assertFalse(doneStatusCheck);
            }

            if(id.equals("2")){
                assertEquals("file paperwork", titleCheck);
                assertEquals("", descriptionCheck);
                assertFalse(doneStatusCheck);
            }
        }
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
        //Create a new TODO item
        String title = "Joey";
        boolean doneStatus = false;
        String description = "doesn't like to work";

        JSONObject newTodo = createNewTodoObject(title, doneStatus, description);

        //Send a POST request to add the new TODO item
        sendPutRequestToCreateTodoItem(newTodo, "http://localhost:4567/todos/1", 200);

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        //Verify if new list is correct
        for(Object todoObject: updatedTodos){
            JSONObject todo = (JSONObject) todoObject;
            String id = (String) todo.get("id");
            String titleCheck = (String) todo.get("title");
            String descriptionCheck = (String) todo.get("description");
            boolean doneStatusCheck = Boolean.parseBoolean(String.valueOf(todo.get("doneStatus")));

            if(id.equals("1")){
                assertEquals(title, titleCheck);
                assertEquals(description, descriptionCheck);
                assertFalse(doneStatusCheck);
            }

            if(id.equals("2")){
                assertEquals("file paperwork", titleCheck);
                assertEquals("", descriptionCheck);
                assertFalse(doneStatusCheck);
            }
        }
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

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        //Verify if new list is correct
        for(Object todoObject: updatedTodos){
            JSONObject todo = (JSONObject) todoObject;
            String id = (String) todo.get("id");
            String titleCheck = (String) todo.get("title");
            String descriptionCheck = (String) todo.get("description");
            boolean doneStatusCheck = Boolean.parseBoolean(String.valueOf(todo.get("doneStatus")));

            if(id.equals("1")){
                Assertions.fail();
            }

            if(id.equals("2")){
                assertEquals("file paperwork", titleCheck);
                assertEquals("", descriptionCheck);
                assertFalse(doneStatusCheck);
            }
        }

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
        // check todo object before post request
        JSONArray initialTodos = fetchTodoList("http://localhost:4567/todos");

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

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        //Verify the previous TODO items are still there, and the new one is created
        verifyTodoItemsAreUpdated(initialTodos, updatedTodos);
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
        // check todo object before post request
        JSONArray initialTodos = fetchTodoList("http://localhost:4567/todos");

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

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        //Verify the previous TODO items are still there, and the new one is created
        verifyTodoItemsAreUpdated(initialTodos, updatedTodos);
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

    @Test
    public void testTodosPostMalformedJson() throws Exception {
        String title = "Joey";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("malformed", title);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
    }

    @Test
    public void testTodosPostMalformedXML() throws Exception {
        String title = "Joey";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("malformed", title);

        String xmlString = XML.toString(jsonObject);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xmlString);

        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();

        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());

    }

    //interoperability

    //-----------------------------------------------------------/todos/:id/categories/:id

    //method not allowed and not in api documentation
    @Test
    public void testCategoryTodoGet() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryTodoPost() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .post(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryTodoPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .put(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryTodoPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .patch(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testCategoryTodoRelDeleteValid() throws Exception {
        // verify todo has category with id 1
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assert response.body() != null;

        String responseBody = response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray categories = (JSONArray) responseJson.get("categories");

        String id = (String)((JSONObject) categories.get(0)).get("id");
        assertEquals("1", id);

        // delete category to todo relationship
        request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(200, response.code());

        // verify todo no longer has category with id 1
        request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories")
            .get()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        assert response.body() != null;
        responseBody = response.body().string();

        parser = new JSONParser();
        responseJson = (JSONObject) parser.parse(responseBody);
        categories = (JSONArray) responseJson.get("categories");
        assert(categories.size() == 0);
    }

    @Test
    public void testCategoryTodoRelDeleteDoubleInvalid() throws Exception {
        // verify todo has category with id 1
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assert response.body() != null;

        String responseBody = response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray categories = (JSONArray) responseJson.get("categories");

        String id = (String)((JSONObject) categories.get(0)).get("id");
        assertEquals("1", id);

        // delete category to todo relationship
        request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(200, response.code());

         request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
    }

    @Test
    public void testCategoryTodoRelDeleteInvalid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/2023")
            .delete()
            .build();
        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());

    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryTodoHead() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/categories/1")
            .head()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }


    //-----------------------------------------------------------/todos/:id/tasksof/:id

    //method not allowed and not in api documentation
    @Test
    public void testTodoTaskOfIDGet() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testTodoTaskOfIDPost() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .post(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testTodoTaskOfIDPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .put(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testTodoTaskOfIDPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .patch(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testTodoTaskOfIDDeleteDoubleInvalid() throws Exception {
        //check todo object before post request
        JSONArray initialTodos = fetchTodoList("http://localhost:4567/todos");

        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .delete()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .delete()
            .build();

       response = CommonTests.getClient().newCall(request).execute();
       assertEquals(404, response.code());

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        // Step 5: Verify the previous TODO items are still there, and the new one is created
        verifyTodoItemsAreUpdated(initialTodos, updatedTodos);
    }

    @Test
    public void testTodoTaskOfIDDeleteValid() throws Exception {
        //check todo object before post request
        JSONArray initialTodos = fetchTodoList("http://localhost:4567/todos");

        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .delete()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        //Fetch TODO items again
        JSONArray updatedTodos = fetchTodoList("http://localhost:4567/todos");

        // Step 5: Verify the previous TODO items are still there, and the new one is created
        verifyTodoItemsAreUpdated(initialTodos, updatedTodos);
    }
    
    //method not allowed and not in api documentation
    @Test
    public void testTodoTaskOfIDDeleteInvalid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/2023")
            .delete()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testTodoTaskOfIDHead() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .head()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    ///application logic, todos/:id/tasksof
    @Test
    public void testProjectTodoRelDelete() throws Exception {
        // verify todo has category with id 1
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assert response.body() != null;

        String responseBody = response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray projects = (JSONArray) responseJson.get("projects");

        JSONObject firstProject = (JSONObject) projects.get(0);
        String id = (String)(firstProject).get("id");
        assertEquals("1", id);

        JSONArray firstProjectTasks = (JSONArray) firstProject.get("tasks");

        boolean found = false;
        for (Object firstProjectTask : firstProjectTasks) {
            JSONObject task = (JSONObject) firstProjectTask;
            if (Integer.parseInt((String) (task).get("id")) == 1) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    public static JSONArray fetchTodoList(String getRequest) throws Exception {
        Request request = new Request.Builder()
                .url(getRequest)
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        return (JSONArray) responseJson.get("todos");
    }

    public static JSONObject createNewTodoObject(String title, boolean doneStatus, String description) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("doneStatus", doneStatus);
        jsonObject.put("description", description);
        return jsonObject;
    }

    public static void verifyTodoItemsAreUpdated(JSONArray initialTodos, JSONArray updatedTodos) {
        // Verify that the previous TODO items are still there
        for (Object initialTodoObject : initialTodos) {
            JSONObject initialTodo = (JSONObject) initialTodoObject;
            String initialId = (String) initialTodo.get("id");
            String initialStatus = (String) initialTodo.get("doneStatus");
            String initialDescription = (String) initialTodo.get("description");


            boolean found = false;
            for (Object updatedTodoObject : updatedTodos) {
                JSONObject updatedTodo = (JSONObject) updatedTodoObject;
                String updatedId = (String) updatedTodo.get("id");
                String updatedStatus = (String) updatedTodo.get("doneStatus");
                String updatedDescription = (String) updatedTodo.get("description");
                if (initialId.equals(updatedId) && initialStatus.equals(updatedStatus) && initialDescription.equals(updatedDescription)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Previous TODO item with ID " + initialId + " is different or not found");
        }
    }

    public static void sendPostRequestToCreateTodoItem(JSONObject newTodo, String postRequest, int expectedCode) throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), newTodo.toString());
        Request request = new Request.Builder()
                .url(postRequest)
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(expectedCode, response.code());

        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String responseTitle = (String) responseJson.get("title");
        boolean responseStatus = Boolean.parseBoolean(String.valueOf(responseJson.get("doneStatus")));
        String responseDescription = (String) responseJson.get("description");

        assertEquals(newTodo.get("title"), responseTitle);
        assertEquals(newTodo.get("doneStatus"), responseStatus);
        assertEquals(newTodo.get("description"), responseDescription);
    }

    public static void sendPutRequestToCreateTodoItem(JSONObject newTodo, String postRequest, int expectedCode) throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), newTodo.toString());
        Request request = new Request.Builder()
                .url(postRequest)
                .put(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(expectedCode, response.code());

        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String responseTitle = (String) responseJson.get("title");
        boolean responseStatus = Boolean.parseBoolean(String.valueOf(responseJson.get("doneStatus")));
        String responseDescription = (String) responseJson.get("description");

        assertEquals(newTodo.get("title"), responseTitle);
        assertEquals(newTodo.get("doneStatus"), responseStatus);
        assertEquals(newTodo.get("description"), responseDescription);
    }
}
