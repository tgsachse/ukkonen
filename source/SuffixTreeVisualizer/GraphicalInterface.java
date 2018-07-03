// Part of Suffix Tree Visualizer by Tiger Sachse

package SuffixTreeVisualizer;

// What a nightmare of an import block, am I right?
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.geometry.*;
import javafx.embed.swing.*;
import javafx.application.*;

// A GUI class to display suffix trees.
public class GraphicalInterface extends Application {
    private Stage stage;
    private Scene scene;
    private String string;

    final private int MAX_LENGTH = 20;
    final private int MIN_HEIGHT = 800;
    final private int MIN_WIDTH = 1000;
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
        NodePane centerPane = new NodePane();
        
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
    private TextField getStringField(NodePane pane) {
        TextField stringField = new TextField();
        stringField.setPromptText("enter a string here");
        stringField.setPrefWidth(380);

        // Create the suffix tree on Enter.
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
    private Button getSubmitButton(TextField field, NodePane pane) {
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
    private void createTree(NodePane pane, TextField field) {
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
            pane.getChildren().clear();

            SuffixTree tree = new SuffixTree(string);
        
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
    private Button getExportButton(NodePane pane) {
        Button exportButton = new Button("export");

        // Set the action of the button.
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                pane.export(stage);
            }
        });

        return exportButton;
    }
}

class NodePane extends Pane {
    int height = 600;//
    int width = 400;//

    // Work in progress!!!
    public void export(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new ExtensionFilter("png files (*.png)", "*.png"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            try {
                WritableImage image = new WritableImage(width, height);//
                snapshot(null, image);
                RenderedImage render = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(render, "png", file);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    // Draw a node to the pane.
    public void drawNode(int nodeX,
                         int nodeY,
                         int radius,
                         int thickness,
                         Node node,
                         Color color) {
        final int innerRadius = radius - (thickness / 2);
        
        Circle outer = new Circle(nodeX, nodeY, radius, color);
        Circle inner = new Circle(nodeX, nodeY, innerRadius, Color.WHITE);
        
        getChildren().addAll(outer, inner);

        // If this node is a leaf, print the suffix terminus inside the circle.
        if (node != null && node.getTerminus() >= 0) {
            Text text = new Text(Integer.toString(node.getTerminus()));
            
            double textX = nodeX - text.prefWidth(-1) / 2;
            double textY = nodeY - text.prefHeight(-1) / 2;
            
            text.relocate(textX, textY);
            
            getChildren().add(text);
        }
    }

    // Draw an edge with appropriate suffix to the pane. This function was *very*
    // stressful to write.
    public void drawEdge(int startX, int startY, int endX, int endY, String suffix) {
        final int textOffset = 10;
       
        Text text = new Text(suffix);
        text.setTextAlignment(TextAlignment.CENTER);
        Line line = new Line(startX, startY, endX, endY);

        // The x and y coordinates for the text node. The text is placed
        // midway down the edge. These values are also adjusted for the size
        // of the text.
        double textX = startX - (startX - endX) / 2 - text.prefWidth(-1) / 2;
        double textY = startY - (startY - endY) / 2 - text.prefHeight(-1) / 2;
       
        // Calculate the angle of the edge.
        double rise = startY - endY;
        double run = startX - endX;
        double degrees = Math.toDegrees(Math.atan2(rise, run));

        // If the angle is basically vertical, offset the text in the x direction.
        if (degrees > -95 && degrees < -85) {
            textX -= textOffset;
        }
        // Else offset in the y direction.
        else {
            textY -= textOffset;
        }

        // If the angle is extreme enough to result in upside down text, decrease the
        // degree so the text will remain upright.
        if (degrees < -90) {
            degrees -= 180;
        }
        
        text.setRotate(degrees);
        text.relocate(textX, textY); 

        getChildren().addAll(line, text);
    }
}
