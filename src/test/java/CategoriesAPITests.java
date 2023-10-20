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
        // check categories object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

        // Create a new category item
        String title = "ECSE 429";
        String description =  "software validation!";

        JSONObject newCategoryObject = createNewCategoryObject(title, description);

        //Send a POST request to add the new category item
        sendPostRequestToCreateCategoryItem(newCategoryObject, "http://localhost:4567/categories", 201);

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    //Create categories with only title in the body of the message
    @Test
    public void testCategoriesTitlePost() throws Exception {
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

        String title = "ECSE 429";

        JSONObject newCategoryObject = createNewCategoryObject(title, "");

        //Send a POST request to add the new category item
        sendPostRequestToCreateCategoryItem(newCategoryObject, "http://localhost:4567/categories", 201);

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
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
        String description =  "validate your software B))";

        JSONObject newCategoryObject = createNewCategoryObject(title, description);

        //Send a POST request to add the new category item
        sendPostRequestToCreateCategoryItem(newCategoryObject, "http://localhost:4567/categories/1", 200);

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        for(Object categoryObject: updatedCategories){
            JSONObject category = (JSONObject) categoryObject;
            String id = (String) category.get("id");
            String titleCheck = (String) category.get("title");
            String descriptionCheck = (String) category.get("description");

            if(id.equals("1")){
                assertEquals(title, titleCheck);
                assertEquals(description, descriptionCheck);
            }

            if(id.equals("2")){
                assertEquals("Home", titleCheck);
                assertEquals("", descriptionCheck);
            }
        }
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
        String description =  "spice!";

        JSONObject newCategoryObject = createNewCategoryObject(title, description);

        //Send a POST request to add the new category item
        sendPutRequestToCreateCategoryItem(newCategoryObject, "http://localhost:4567/categories/1", 200);

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        for(Object categoryObject: updatedCategories){
            JSONObject category = (JSONObject) categoryObject;
            String id = (String) category.get("id");
            String titleCheck = (String) category.get("title");
            String descriptionCheck = (String) category.get("description");

            if(id.equals("1")){
                assertEquals(title, titleCheck);
                assertEquals(description, descriptionCheck);
            }

            if(id.equals("2")){
                assertEquals("Home", titleCheck);
                assertEquals("", descriptionCheck);
            }
        }
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
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoriesPostNewProjects() throws Exception {
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", "latte");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/projects")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoriesPostInvalidProjects() throws Exception {
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
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

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

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoriesDeleteInvalidProjects() throws Exception {
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
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "1");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Request request = new Request.Builder()
                .url("http://localhost:4567/categories/1/todos")
                .post(requestBody)
                .build();


        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;

        assertEquals(201, response.code());
        assertEquals("Created", response.message());

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoriesPostNewTodos() throws Exception {
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

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

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoriesPostInvalidTodos() throws Exception {
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
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

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

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoriesDeleteInvalidTodos() throws Exception {
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
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

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

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
    }

    @Test
    public void testCategoryProjectRelationshipDeleteDoubleInvalid() throws Exception {
        // check category object before post request
        JSONArray initialCategories = fetchCategoryList("http://localhost:4567/categories");

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

        //Fetch category items again
        JSONArray updatedCategories = fetchCategoryList("http://localhost:4567/categories");

        //Verify the previous category items are still there, and the new one is created
        verifyCategoryObjectAreUpdated(initialCategories, updatedCategories);
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

    public static JSONArray fetchCategoryList(String getRequest) throws Exception {
        Request request = new Request.Builder()
                .url(getRequest)
                .get()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        return (JSONArray) responseJson.get("categories");
    }

    public static JSONObject createNewCategoryObject(String title, String description) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("description", description);
        return jsonObject;
    }

    public static void verifyCategoryObjectAreUpdated(JSONArray initialCategories, JSONArray updatedCategories) {
        // Verify that the previous Category items are still there
        for (Object initialCategoryObject : initialCategories) {
            JSONObject initialCategory = (JSONObject) initialCategoryObject;
            String initialId = (String) initialCategory.get("id");
            String initialDescription = (String) initialCategory.get("description");


            boolean found = false;
            for (Object updatedCategoryObject : updatedCategories) {
                JSONObject updatedCategory = (JSONObject) updatedCategoryObject;
                String updatedId = (String) updatedCategory.get("id");
                String updatedDescription = (String) updatedCategory.get("description");
                if (initialId.equals(updatedId) && initialDescription.equals(updatedDescription)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Previous Category item with ID " + initialId + " is different or not found");
        }
    }

    public static void sendPostRequestToCreateCategoryItem(JSONObject newCategory, String postRequest, int expectedCode) throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), newCategory.toString());
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
        String responseDescription = (String) responseJson.get("description");

        assertEquals(newCategory.get("title"), responseTitle);
        assertEquals(newCategory.get("description"), responseDescription);
    }

    public static void sendPutRequestToCreateCategoryItem(JSONObject newCategory, String postRequest, int expectedCode) throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), newCategory.toString());
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
        String responseDescription = (String) responseJson.get("description");

        assertEquals(newCategory.get("title"), responseTitle);
        assertEquals(newCategory.get("description"), responseDescription);
    }

}
