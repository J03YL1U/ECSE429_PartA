import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.io.IOException;
import java.net.ConnectException;

public class CommonTests implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final OkHttpClient client = new OkHttpClient();

    public void beforeTestExecution(ExtensionContext context) {
        try {
            String filePath = "src\\test\\java\\runTodoManagerRestAPI-1.5.5.jar";
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", filePath);
            processBuilder.start();

            Thread.sleep(500);

            Request request = new Request.Builder()
                    .url("http://localhost:4567/")
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("API is not running or is not responding as expected.");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error starting the API: " + e.getMessage(), e);
        }
    }

    public void afterTestExecution(ExtensionContext context) {
        try {
            Request request = new Request.Builder()
                    .url("http://localhost:4567/shutdown")
                    .get()
                    .build();
            client.newCall(request).execute();
        } catch (ConnectException e) {
            // Server doesn't respond anymore after shutdown; no need for further action.
        } catch (IOException e) {
            throw new RuntimeException("Error shutting down the API: " + e.getMessage(), e);
        }
    }

    public static OkHttpClient getClient() {
        return client;
    }
}
