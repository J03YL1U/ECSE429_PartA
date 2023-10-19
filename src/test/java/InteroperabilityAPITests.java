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

    //----------------------------------------------------------- http://localhost:4567/todos -----------------------------------------------------------//
    //"/todos/:id/categories/:id"
    @Test
    public void deleteCategoryTodoRel() throws Exception {
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
        JSONArray todos = (JSONArray) responseJson.get("categories");

        for (Object todoObject : todos) {
            JSONObject todo = (JSONObject) todoObject;
            String id = (String) todo.get("id");
            assertEquals("1", id);
        }

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
        JSONArray categories = (JSONArray) responseJson.get("categories");
        System.out.println(categories);
        assert (categories.size() == 0);
       
    }
    
}
