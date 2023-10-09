import okhttp3.*;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestAPI {

    OkHttpClient client = new OkHttpClient();

    @BeforeAll
    public static void isAPIUp() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:4567/")
                .build();
        try {
            client.newCall(request).execute();
            System.out.println("Connection is established");
        } catch (IOException e) {
            Assertions.fail("We are not connected, please make sure we are connected to the API");
        }
    }

    // return all the instances of todo
    @Test
    public void todoGetAllRequest() throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:4567/todos")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertNotNull(response.body().string());
        assertEquals(200, response.code());
    }
}
