// Part of Suffix Tree Visualizer by Tiger Sachse

package SuffixTreeVisualizer;

// What a nightmare of an import block, am I right?
import java.io.*;
import java.awt.image.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javax.imageio.*;
import javafx.geometry.*;
import javafx.embed.swing.*;
import javafx.application.*;

import javafx.scene.shape.*;

// A GUI class to display suffix trees.
public class GraphicalInterface extends Application {
    private Stage stage;
    private Scene scene;
    private String string;
    final private int MAX_LENGTH = 20;
    final private int MIN_HEIGHT = 800;
    final private int MIN_WIDTH = 1000;
    final private int CANVAS_WIDTH = MIN_WIDTH;
    final private int CANVAS_HEIGHT = MIN_HEIGHT - 200;
    final private String TITLE = "Suffix Tree Visualizer";
    final private String STYLESHEET = "Styles/DefaultStyle.css";
   
    // Entry point to the program.
    public static void main(String[] args) {
       launch(args); 
    }

    // The real main! Configure the scene and stage for display.
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        
        createScene();
       
        // Configure and show the stage.
        stage.setTitle(TITLE);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.setScene(scene);
        stage.show();
    }
   
    // Create the main scene of the program.
    private void createScene() {
        // Create pane for center of scene.
        Pane centerPane = new Pane();
        
        // Create horizontal row with text field and button for top of scene.
        HBox topBox = new HBox();
        TextField stringField = getStringField(centerPane);
        Button submitButton = getSubmitButton(stringField, centerPane);
        
        // Create horizontal row with two buttons for bottom of scene.
        HBox bottomBox = new HBox();
        Button quitButton = getQuitButton();
        Button exportButton = getExportButton(centerPane);
        
        // Add buttons to their fields.
        topBox.getChildren().addAll(submitButton, stringField);
        bottomBox.getChildren().addAll(exportButton, quitButton);
      
        // Create the border pane that contains everything, then add everything.
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(topBox);
        mainPane.setCenter(centerPane);
        mainPane.setBottom(bottomBox);
        
        // Initialize the scene with the main border pane.
        scene = new Scene(mainPane, MIN_WIDTH, MIN_HEIGHT); 
        
        // Load the default style sheet and apply it to the horizontal rows.
        scene.getStylesheets().add(STYLESHEET); 
        bottomBox.getStyleClass().add("botHBox");
        topBox.getStyleClass().add("topHBox");
    }

    // Create a text field that accepts a suffix string.
    private TextField getStringField(Pane pane) {
        TextField stringField = new TextField();
        stringField.setPromptText("enter a string here");
        stringField.setPrefWidth(380);

        stringField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    createTree(pane, stringField);
                }
            }
        });

        return stringField;
    }

    // Create a submit button for the text field.
    private Button getSubmitButton(TextField field, Pane pane) {
        Button submitButton = new Button("submit");
        
        // Set the action of the button using this gorgeous syntax.
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                createTree(pane, field);
            }
        });

        return submitButton;
    }
 
    // Create and draw a suffix tree to the pane, based on the string
    // in the text field.
    private void createTree(Pane pane, TextField field) {
        //GraphicsContext context = pane.getGraphicsContext2D();
        
        string = field.getText();
        // Hard cap the max length of a string so the trees don't look terrible.
        if (string.length() > MAX_LENGTH) {
            StringBuilder error = new StringBuilder();
            
            error.append("Your string is too long.");
            error.append("The maximum length this program can handle is ");
            error.append(MAX_LENGTH);
            error.append(".");
            
            popError(error.toString());
        }
        else {
            // Clear any previous trees from the screen.
            //context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT); 
            pane.getChildren().clear();

            SuffixTree tree = new SuffixTree(string);
        
            //pane.getChildren().add(new Circle(MIN_WIDTH/2, 600, 50));
            tree.draw(pane, MIN_WIDTH);
        }
    }

    // Show a popup error.
    private void popError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        
        alert.setTitle("Something happened...");
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setContentText(error);

        alert.showAndWait();
    }

    // Create a quit button.
    private Button getQuitButton() {
        Button quitButton = new Button("quit");
        
        // Set the action of the button.
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

        return quitButton;
    }
   
    // Create an export button to save the pane to a file.
    private Button getExportButton(Pane pane) {
        Button exportButton = new Button("export");

        // Set the action of the button. This portion of my code is largely
        // lifted from java-buddy.blogspot.com. Thanks guys!
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // Create a chooser that allows the user to pick a file save
                // location (with file type restrictions).
                FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().add(new ExtensionFilter("png files (*.png)",
                                                                      "*.png"));
                // Get the file location from the user.
                File file = chooser.showSaveDialog(stage);

                if (file != null) {
                    try {
                        // Take a snapshot of the pane and write it to file.
                        WritableImage image = new WritableImage(CANVAS_WIDTH, CANVAS_HEIGHT);
                        pane.snapshot(null, image);
                        RenderedImage render = SwingFXUtils.fromFXImage(image, null);
                        ImageIO.write(render, "png", file);
                    }
                    // Print the error if something goes wrong with file I/O.
                    catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        return exportButton;
    }
}
