import okhttp3.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(CommonTests.class)
public class CategoriesAPITests {

    //----------------------------------------------------------- http://localhost:4567/categories -----------------------------------------------------------//
    //return all the instances of categories
    @Test
    public void testCategoriesGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //headers for all the instances of Categories
    @Test
    public void testCategoriesHead() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //Create categories without a ID using the field values in the body of the message
    @Test
    public void testCategoriesFullPost() throws Exception {
        String title = "ECSE 429";
        String description =  "software validation!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("description", description);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String responseTitle = (String) responseJson.get("title");
        String responseDescription = (String) responseJson.get("description");
        String responseId = (String) responseJson.get("id");

        assertEquals(201, response.code());
        assertEquals(title, responseTitle);
        assertEquals(description, responseDescription);
        assertNotNull(responseId);
    }

    //Create categories with only title in the body of the message
    @Test
    public void testCategoriesTitlePost() throws Exception {
        String title = "ECSE 429";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        String responseTitle = (String) responseJson.get("title");
        String responseDescription = (String) responseJson.get("description");
        String responseId = (String) responseJson.get("id");

        assertEquals(201, response.code());
        assertEquals(title, responseTitle);
        assertEquals("", responseDescription);
        assertNotNull(responseId);
    }

    //Create categories with and ID
    @Test
    public void testCategoriesIDPost() throws Exception {
        String title = "ECSE 429";
        String id = "100";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("id", id);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoriesInvalidPut() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .put(dummyBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }
    //method not allowed and not in api documentation
    @Test
    public void testCategoriesDelete() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //Create categories with and ID
    @Test
    public void testCategoriesMalformedJsonPost() throws Exception {
        String title = "ECSE 429";
        String id = "100";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("tfdsuvaej", id);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testCategoriesPatch() throws Exception {
        RequestBody dummyBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .patch(dummyBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/categories/id -----------------------------------------------------------//

    //return a specific instances of categories using an id
    @Test
    public void testCategoriesGetWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());

        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);
        JSONArray categories = (JSONArray) responseJson.get("categories");

        for (Object categoryObject : categories) {
            JSONObject category = (JSONObject) categoryObject;
            String id = (String) category.get("id");
            String title = (String) category.get("title");
            String description = (String) category.get("description");
            assertEquals("1", id);
            assertEquals("Office", title);
            assertEquals("", description);
        }
    }

    //get categories with invalid id
    @Test
    public void testCategoriesGetWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/10")
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testCategoriesHeadID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //given an existing id, amend a specific instances of category using a id with a body containing the fields to amend
    @Test
    public void testCategoriesPostWithValidID() throws Exception {
        String title = "ECSE 429";
        boolean doneStatus = false;
        String description =  "validate your software B))";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("description", description);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1")
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
        String actualDoneStatus = (String) responseJson.get("doneStatus");
        assertEquals("1", id);
        assertEquals(title, actualTitle);
    }

    @Test
    public void testCategoriesPostWithInvalidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/5")
                .post(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testCategoriesPutWithValidID() throws Exception {
        String title = "pumpkin";
        boolean doneStatus = false;
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);
        jsonObject.put("description", description);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1")
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
        assertEquals("1", id);
        assertEquals(title, actualTitle);
        assertEquals(description, actualDescription);
    }

    @Test
    public void testCategoriesPutWithInvalidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/3")
                .put(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testCategoriesDeleteWithInvalidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/3")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testCategoriesDeleteWithValidID() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testCategoriesPatchWithValidID() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1")
                .patch(requestBody)
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/categories/:id/projects -----------------------------------------------------------//

    @Test
    public void testCategoriesPostExistingProjects() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        assertEquals(201, response.code());
        assertEquals("Created", response.message());
    }

    @Test
    public void testCategoriesPostNewProjects() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        assertEquals(201, response.code());
        assertEquals("Created", response.message());
    }

    @Test
    public void testCategoriesPostInvalidProjects() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "10");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testCategoriesProjectsHead() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testCategoriesGetProjects() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/categories/:id/projects/:id -----------------------------------------------------------//

    @Test
    public void testCategoriesDeleteValidProjects() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects/1")
                .delete()
                .build();


        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testCategoriesDeleteInvalidProjects() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "10");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects/1")
                .delete()
                .build();


        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    //----------------------------------------------------------- http://localhost:4567/categories/:id/todos -----------------------------------------------------------//

    @Test
    public void testCategoriesPostExistingTodos() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        assertEquals(201, response.code());
        assertEquals("Created", response.message());
    }

    @Test
    public void testCategoriesPostNewTodos() throws Exception {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(201, response.code());
        assertEquals("Created", response.message());
    }

    @Test
    public void testCategoriesPostInvalidTodos() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "10");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }

    @Test
    public void testCategoriesTodosHead() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testInvalidCategoriesTodosHead() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/10/todos")
                .head()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
    }

    @Test
    public void testCategoriesGetTodos() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testCategoriesGetTodosInvalidCategory() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/8/todos")
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assertEquals(200, response.code());
    }

    //----------------------------------------------------------- http://localhost:4567/categories/:id/todos/:id -----------------------------------------------------------//

    @Test
    public void testCategoriesDeleteValidTodos() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos/1")
                .delete()
                .build();


        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(200, response.code());
        assertEquals("OK", response.message());
    }

    @Test
    public void testCategoriesDeleteInvalidTodos() throws Exception {
        String title = "pumpkin";
        String description =  "spice!";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "10");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos/1")
                .delete()
                .build();


        response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
    }


    //interoperability

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

}
