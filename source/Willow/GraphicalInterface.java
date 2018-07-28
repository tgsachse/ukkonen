// Part of Willow by Tiger Sachse.

package Willow;

// So many imports...
import java.io.*;
import java.util.*;
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

// The main GUI frame of Willow.
public class GraphicalInterface extends Application {
    private Stage stage;
    private Scene scene;
    private String treeString;

    final private int MAX_LENGTH = 20;
    final private int MIN_HEIGHT = 800;
    final private int MIN_WIDTH = 1000;
    final private int FIELD_WIDTH = 380;
    final private String TITLE = "Willow";
    final private String STYLESHEET = "Styles/Default.css";
    final private String STRING_ENTRY = "enter a string here";
   
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
        
        // Create a horizontal row with an entry text field and submission
        // button for top of scene.
        HBox topBox = new HBox();
        TextField stringField = getStringField(centerPane);
        Button submitButton = getSubmitButton(stringField, centerPane);
        topBox.getChildren().addAll(submitButton, stringField);
        
        // Create a horizontal row with some function buttons for bottom of scene.
        HBox bottomBox = new HBox();
        Button quitButton = getQuitButton();
        Button exportButton = getExportButton(centerPane);
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
        stringField.setPromptText(STRING_ENTRY);
        stringField.setPrefWidth(FIELD_WIDTH);

        // Create the suffix tree on Enter keypress.
        stringField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    treeString = sanitizeString(stringField.getText());
                    createTree(pane);
                }
            }
        });

        return stringField;
    }

    // Create an export button to save the pane to a file.
    private Button getExportButton(NodePane pane) {
        Button exportButton = new Button("export");

        // Call the pane's export method on press.
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                pane.export(stage);
            }
        });

        return exportButton;
    }

    // Create a submit button for the text field.
    private Button getSubmitButton(TextField field, NodePane pane) {
        Button submitButton = new Button("submit");
        
        // Set the action of the button using this gorgeous syntax.
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                treeString = sanitizeString(field.getText());
                createTree(pane);
            }
        });

        return submitButton;
    }
    
    // Create a quit button.
    private Button getQuitButton() {
        Button quitButton = new Button("quit");
        
        // Set to exit program on press.
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

        return quitButton;
    }
   
    // Clean up the user's string.
    private String sanitizeString(String string) {

        // Accept one or more alphabetic characters and an optional dollar sign
        // at the end.
        String regex = "^[A-za-z]+\\$?$";
       
        if (string.length() < 1) {
            popError("No string provided.");
        }
        else if (!string.matches(regex)) {
            popError("Unknown characters in string. Only alphabetic characters are valid.");
        }
        else if (string.length() > MAX_LENGTH) {
             popError(String.format("String is too long. Maximum length is %d.", MAX_LENGTH));
        }
        else {

            // If the first character is a capital, make all the characters capitals.
            if (Character.isUpperCase(string.charAt(0))) {
                string = string.toUpperCase();
            }

            // Else make all the characters lowercase.
            else {
                string = string.toLowerCase();
            }

            return string;
        }

        return null;
    }

    // Create and draw a suffix tree to the pane, based on the string
    // in the text field.
    private void createTree(NodePane pane) {
        
        // Clear any previous trees from the screen.
        pane.getChildren().clear();
        pane.resetSizeParameters();

        // If the treeString exists, create the tree and draw it.
        if (treeString != null) {
            SuffixTree tree = new SuffixTree(treeString);
            tree.draw(pane, MIN_WIDTH);
        }
    }

    // Show a popup error.
    private void popError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Something happened...");

        alert.showAndWait();
    }
}

// A pane for nodes of a suffix tree.
class NodePane extends Pane {
    private int maxY;
    private int maxX;
    private int minY;
    private int minX;

    final private int RADIUS = 20;
    final private int THICKNESS = 5;
    final private int MIN_LENGTH = 150;
    final private Font FONT = new Font(22);
    final private int VERTICAL_OFFSET = 85;
    final private Color COLOR = Color.BLACK;
    final private String DEFAULT_NAME = "willow.png";

    // Initialize a new NodePane.
    public NodePane() {
        resetSizeParameters();
    }

    // Reset the size parameters of the pane. These numbers are needed
    // to crop exported images from the pane.
    public void resetSizeParameters() {
        maxY = Integer.MIN_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        minX = Integer.MAX_VALUE;
    }

    // Get the RADIUS of a node in the pane. // remove these
    public int getRadius() {
        return RADIUS;
    }

    // Get the minimum length of an edge. // remove these
    public int getMinimumLength() {
        return MIN_LENGTH;
    }

    // Export the pane to a file.
    public void export(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new ExtensionFilter("png files", "*.png"));
        chooser.setInitialFileName(DEFAULT_NAME);

        // Choose a file to save the pane as, using a FileChooser dialog.
        File file = chooser.showSaveDialog(stage);

        // Take a snapshot of the pane if the file is valid, and write.
        if (file != null) {
            try {

                // The minimum and maximum coordinate parameters are adjusted
                // as the tree is built. Before the snapshot, adjust them a little
                // more so the edges of outer nodes don't get cut out. Also, it
                // is necessary to adjust the vertical coordinates by a magic number to
                // compensate for the top pane of the main window.
                maxX += RADIUS;
                minX -= RADIUS;
                minY += VERTICAL_OFFSET - RADIUS;
                maxY += VERTICAL_OFFSET + RADIUS;
                
                int width = maxX - minX;
                int height = maxY - minY;
               
                // Set the dimensions of the snapshot based on the numbers
                // calculated above.
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setViewport(new Rectangle2D(minX, minY, width, height));
               
                // Create a WritableImage and render it.
                WritableImage image = snapshot(parameters, null);
                RenderedImage render = SwingFXUtils.fromFXImage(image, null);
               
                // Write the render to a file.
                ImageIO.write(render, "png", file);
            }

            // If something was wrong with the file, print the stack trace.
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    // Draw a node to the pane. \\ these will probably be changed immensely soon.
    public void drawNode(int nodeX, int nodeY, Node node) {
        final int innerRadius = RADIUS - (THICKNESS / 2);

        if (nodeX > maxX) {
            maxX = nodeX + RADIUS;
        }
        if (nodeY > maxY) {
            maxY = nodeY + RADIUS;
        }
        if (nodeX < minX) {
            minX = nodeX - RADIUS;
        }
        if (nodeY < minY) {
            minY = nodeY - RADIUS;
        }

        Circle outer = new Circle(nodeX, nodeY, RADIUS, COLOR);
        Circle inner = new Circle(nodeX, nodeY, innerRadius, Color.WHITE);
        
        getChildren().addAll(outer, inner);

        // If this node is a leaf, print the suffix terminus inside the circle.
        if (node != null && node.getTerminus() >= 0) {
            Text text = new Text(Integer.toString(node.getTerminus()));
            text.setFont(FONT);

            double textX = nodeX - text.prefWidth(-1) / 2;
            double textY = nodeY - text.prefHeight(-1) / 2;
            
            text.relocate(textX, textY);
            
            getChildren().add(text);
        }
    }

    // Draw an edge with appropriate suffix to the pane. This function was *very*
    // stressful to write. This entire function can probably be reduced with some
    // math tricks but time constraints force me to move on.
    // \\ these will probably be changed immensely soon.
    public void drawEdge(int startX, int startY, int endX, int endY, String suffix) {
        final int textOffset = 15;
       
        Text text = new Text(suffix);
        text.setFont(FONT);
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
        double radianAngle = Math.atan2(rise, run);
        double degreeAngle = Math.toDegrees(radianAngle);

        // If the angle is extreme enough to result in upside down text, decrease the
        // degree so the text will remain upright.
        if (degreeAngle < -90) {
            degreeAngle -= 180;
        }

        // Offset the text from the lines. There is probably a cleaner math way to do
        // this.
        double textXOffset = textOffset * Math.abs(Math.sin(radianAngle));
        textX += (radianAngle < -(Math.PI / 2)) ? textXOffset : -textXOffset;
        textY -= (textOffset * Math.abs(Math.cos(radianAngle)));
        
        text.setRotate(degreeAngle);
        text.relocate(textX, textY); 

        getChildren().addAll(line, text);
    }
}

// A static class for concentrating taller nodes into the middle of an array.
class MiddleSort {

    // Move the tallest nodes to the middle of the array. // why do i need items here?
    // Rewrite this function to sort the given array so I don't have to copy
    // it so many times, if I keep this at all.
    public static Node[] sort(Node[] array, int items) {
        Node[] intermediateArray = new Node[items];

        int nodeIndex = 0;
        for (Node node : array) {
            if (node != null) {
                intermediateArray[nodeIndex] = node;
                nodeIndex++;
            }
        }

        Arrays.sort(intermediateArray);

        Node[] sortedArray = new Node[items];

        int frontIndex = 0;
        int backIndex = items - 1;
        for (int node = 0; node < items; node++) {
            if (node % 2 == 0) {
                sortedArray[frontIndex] = intermediateArray[node];
                frontIndex++;
            }
            else {
                sortedArray[backIndex] = intermediateArray[node];
                backIndex--;
            }
        }
        
        return sortedArray;
    }
}
