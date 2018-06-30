import javafx.scene.paint.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.application.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.control.*;

public class GraphicalInterface extends Application {
    private String string;
    private String title = "Suffix Tree Generator";
    private int minHeight = 800;
    private int minWidth = 1000;
    
    public static void main(String[] args) {
       launch(args); 
    }

    private BorderPane getMainBox() {
        Canvas centerCanvas = new Canvas(minWidth, minHeight - 200);

        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(40));

        HBox topBox = new HBox(12);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));
      
        Text promptText = new Text("Enter a string:");

        TextField stringField = new TextField();
        stringField.setPromptText("enter a string here");
        stringField.setPrefWidth(380);

        Button submitButton = new Button("submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                string = stringField.getText();
                GraphicsContext context = centerCanvas.getGraphicsContext2D();
                context.setFill(Color.GREEN);
                context.setStroke(Color.GREEN);
                context.setLineWidth(5);
                context.fillOval(minWidth / 2, 0, 30, 30);
                context.fillOval(minWidth / 2 + 100, 100, 30, 30);
                context.fillOval(minWidth / 2 - 100, 100, 30, 30);
                context.strokeLine(minWidth / 2, 0, minWidth / 2 + 100, 100);
                context.rotate(40);
                context.fillText(string, 300, 100);
                context.restore();
            }
        
        });
        
        Button quitButton = new Button("quit");
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        Button exportButton = new Button("export");

        topBox.getChildren().addAll(submitButton, stringField);
        bottomBox.getChildren().addAll(exportButton, quitButton);
        
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(topBox);
        mainPane.setCenter(centerCanvas);
        mainPane.setBottom(bottomBox);

        return mainPane; 
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(title);
        
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);

        Scene mainScene = new Scene(getMainBox(), minWidth, minHeight);
        stage.setScene(mainScene);
        
        stage.show();
    }
}
