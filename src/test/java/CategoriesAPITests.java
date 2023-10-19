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
    public void testcategoriesFullPost() throws Exception {
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
    public void testcategoriesTitlePost() throws Exception {
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
    public void testcategoriesIDPost() throws Exception {
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
    public void testcategoriesPut() throws Exception {
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
    public void testcategoriesDelete() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/categories")
                .delete()
                .build();

        Response response = CommonTests.getClient().newCall(request).execute();
        assert response.body() != null;
        assertEquals(405, response.code());
        assertEquals("Method Not Allowed", response.message());
    }

    //method not allowed and not in api documentation
    @Test
    public void testcategoriesPatch() throws Exception {
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


}
