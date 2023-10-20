import okhttp3.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(CommonTests.class)
public class InteroperabilityAPITests {


    // /todos/:id/categories/:id - done
    // /todos/:id/tasksof/:id - done
    // /projects/:id/tasks/:id - done
    // /projects/:id/categories/:id - done
    // /categories/:id/projects/:id - done
    // /categories/:id/todos/:id - done (Ke)

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

    }

    @Test
    public void testTodoTaskOfIDDeleteValid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/todos/1/tasksof/1")
            .delete()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

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

    //-----------------------------------------------------------/projects/:id/tasks/:id

    //method not allowed and not in api documentation
    @Test
    public void testProjectTodoRelationshipGet() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectTodoRelationshipPost() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .post(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectTodoRelationshipPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .put(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectTodoRelationshipPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .patch(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testProjectTodoRelationshipDeleteValid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .delete()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
    }

    @Test
    public void testProjectTodoRelationshipDeleteDoubleInvalid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .delete()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
    }

    
    //method not allowed and not in api documentation
    @Test
    public void testProjectTodoRelationshipDeleteInvalid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/2023")
            .delete()
            .build();
        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());

    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectTodoRelationshipHead() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/tasks/1")
            .head()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //----------------------------------------------------------- projects/:id/categories/:id
    
    //method not allowed and not in api documentation
    @Test
    public void testProjectCategoryRelationshipGet() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/1")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectCategoryRelationshipPost() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/1")
            .post(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectCategoryRelationshipPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/1")
            .put(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectCategoryRelationshipPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/1")
            .patch(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testProjectCategoryRelationshipDeleteValid() throws Exception {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories")
            .post(requestBody)
            .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/3")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());


    }

    @Test
    public void testProjectCategoryRelationshipDeleteDoubleInvalid() throws Exception {

       JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories")
            .post(requestBody)
            .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/3")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/3")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());


    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectCategoryRelationshipDeleteInvalid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/2023")
            .delete()
            .build();
        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());

    }

    //method not allowed and not in api documentation
    @Test
    public void testProjectCategoryRelationshipHead() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/projects/1/categories/1")
            .head()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }


    //----------------------------------------------------------- categories/:id/projects/:id
    
    //method not allowed and not in api documentation
    @Test
    public void testCategoryProjectRelationshipGet() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/1")
            .get()
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryProjectRelationshipPost() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/1")
            .post(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryProjectRelationshipPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/1")
            .put(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryProjectRelationshipPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/1")
            .patch(dummyBody)
            .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    @Test
    public void testCategoryProjectRelationshipDeleteValid() throws Exception {


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects")
            .post(requestBody)
            .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/2")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());


    }

    @Test
    public void testCategoryProjectRelationshipDeleteDoubleInvalid() throws Exception {


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects")
            .post(requestBody)
            .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/2")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/2")
            .delete()
            .build();

        response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());


    }
    
    //method not allowed and not in api documentation
    @Test
    public void testCategoryProjectRelationshipDeleteInvalid() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/2023")
            .delete()
            .build();
        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());

    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoryProjectRelationshipHead() throws Exception {
        Request request = new Request.Builder()
            .url("http://localhost:4567/categories/1/projects/1")
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
        for (int i = 0; i < firstProjectTasks.size(); i++) {
            JSONObject task = (JSONObject) firstProjectTasks.get(i);
            if (Integer.parseInt((String)(task).get("id")) == 1) {
                found = true;
                break;
            }
        }
        assertTrue(found);



    }



}