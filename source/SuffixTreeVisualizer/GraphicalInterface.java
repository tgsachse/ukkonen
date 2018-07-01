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
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javax.imageio.*;
import javafx.geometry.*;
import javafx.embed.swing.*;
import javafx.application.*;

// A GUI class to display suffix trees.
public class GraphicalInterface extends Application {
    private Stage stage;
    private Scene scene;
    private String string;
    private int minHeight = 800;
    private int minWidth = 1000;
    private int canvasWidth = minWidth;
    private int canvasHeight = minHeight - 200;
    private String title = "Suffix Tree Visualizer";
    private String stylesheet = "Styles/DefaultStyle.css";
   
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
        stage.setTitle(title);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.setScene(scene);
        stage.show();
    }
    
    private void createScene() {
        // Create canvas for center of scene.
        Canvas centerCanvas = new Canvas(canvasWidth, canvasHeight);
        
        // Create horizontal row with text field and button for top of scene.
        HBox topBox = new HBox();
        TextField stringField = new TextField();
        Button submitButton = getSubmitButton(stringField, centerCanvas);
        
        // Create horizontal row with two buttons for bottom of scene.
        HBox bottomBox = new HBox();
        Button quitButton = getQuitButton();
        Button exportButton = getExportButton(centerCanvas);
        
        // Add buttons to their fields.
        topBox.getChildren().addAll(submitButton, stringField);
        bottomBox.getChildren().addAll(exportButton, quitButton);
      
        // Create the border pane that contains everything, then add everything.
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(topBox);
        mainPane.setCenter(centerCanvas);
        mainPane.setBottom(bottomBox);
        
        // Initialize the scene with the main border pane.
        scene = new Scene(mainPane, minWidth, minHeight); 
        
        // Load the default style sheet and apply it to the horizontal rows.
        scene.getStylesheets().add(stylesheet); 
        bottomBox.getStyleClass().add("botHBox");
        topBox.getStyleClass().add("topHBox");
        // Tweak the text field to perfection.
        stringField.setPromptText("enter a string here");
        stringField.setPrefWidth(380);
    }

    // Create a submit button for the text field.
    private Button getSubmitButton(TextField field, Canvas canvas) {
        Button submitButton = new Button("submit");
        
        // Set the action of the button using this gorgeous syntax.
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // All of this stuff will change once the real tree is done.
                string = field.getText();
                GraphicsContext context = canvas.getGraphicsContext2D();
                SuffixTree tree = new SuffixTree(string, 50, 100, Color.GREEN);
                tree.drawTree(context, minWidth, tree.diameter);
            }
        });

        return submitButton;
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
   
    // Create an export button to save the canvas to a file.
    private Button getExportButton(Canvas canvas) {
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
                        // Take a snapshot of the canvas and write it to file.
                        WritableImage image = new WritableImage(canvasWidth, canvasHeight);
                        canvas.snapshot(null, image);
                        RenderedImage render = SwingFXUtils.fromFXImage(image, null);
                        ImageIO.write(render, "png", file);
                    }
                    // Print the error if something goes wrong with file I/O.
                    catch (IOException exception) {
                        System.out.println(exception);
                    }
                }
            }
        });

        return exportButton;
    }
}
