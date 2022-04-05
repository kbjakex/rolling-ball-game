package rollingball;

import javafx.application.Application;
import javafx.stage.Stage;

public class RollingBall extends Application {
    
    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rolling Ball Game");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
