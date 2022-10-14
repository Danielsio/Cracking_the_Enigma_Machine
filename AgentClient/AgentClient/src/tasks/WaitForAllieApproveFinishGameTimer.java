package tasks;

import app.MainController;
import app.MessageTone;
import com.google.gson.Gson;
import dto.DTOactive;
import dto.DTOstatus;
import dto.DTOtasks;
import javafx.application.Platform;
import okhttp3.*;

import java.io.IOException;
import java.util.TimerTask;

import static http.url.Constants.CONTENT_TYPE;
import static http.url.URLconst.*;

public class WaitForAllieApproveFinishGameTimer extends TimerTask {

    private OkHttpClient client;
    private MainController mainController;

    public WaitForAllieApproveFinishGameTimer(MainController mainController) {
        this.mainController = mainController;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + FETCH_APPROVAL_STATUS_SRC).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader(CONTENT_TYPE, "text/plain")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {


            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("fetch candidates task response");
                System.out.println("Code: " + response.code());
                String dtoAsStr = response.body().string();
                System.out.println("Body: " + dtoAsStr);
                Gson gson = new Gson();


                if (response.code() != 200) {
                    DTOstatus tasksStatus = gson.fromJson(dtoAsStr, DTOstatus.class);
                    Platform.runLater(() -> {
                        mainController.setStatusMessage(mainController.convertProblemToMessage(tasksStatus.getDetails()), MessageTone.ERROR);
                    });

                } else {
                    DTOactive approvalStatus = gson.fromJson(dtoAsStr, DTOactive.class);
                    Platform.runLater(() -> {
                        if (approvalStatus.isActive()) {
                            mainController.cleanOldResults();
                            mainController.setIsSubscribed(false);
                        }
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println("Oops... something went wrong..." + e.getMessage());
            }
        });
    }
}
