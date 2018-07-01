// Part of Suffix Tree Visualizer by Tiger Sachse

package SuffixTreeVisualizer;

import javafx.scene.text.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;

// Provides a suffix tree that can be built and printed in JavaFX.
public class SuffixTree {
    private Node root;
    private Color color;
    private int fontSize;
    private int diameter;
    private String string;
    private int ovalThickness;
    private int minimumLength;

    private final int CHILDREN = 26;
    private final boolean DEBUG = false;

    // Constructor that builds the tree with some sensible defaults.
    public SuffixTree(String string) {
        diameter = 40;
        fontSize = 24;
        minimumLength = 100;
        ovalThickness = 10;
        color = Color.BLACK;
        
        this.string = string;
        
        build();
    }

    // Constructor builds the tree with custom visual parameters.
    public SuffixTree(String string,
                      Color color,
                      int diameter,
                      int fontSize,
                      int ovalThickness,
                      int minimumLength) {
        
        this.color = color;
        this.string = string;
        this.diameter = diameter;
        this.fontSize = fontSize;
        this.minimumLength = minimumLength;
        this.ovalThickness = ovalThickness;

        build();
    }

    // Build the suffix tree using Ukkonen's algorithm.
    private void build() {
        root = new Node();
       
        // Parameters to track location in the tree.
        int path = -1;
        int length = 0;
        int remaining = 0;
        Node current = root;


        System.out.println("strIndex index path length remaining");
        for (int stringIndex = 0; stringIndex < string.length(); stringIndex++) {
            remaining++;
           
            //System.out.println(string.charAt(stringIndex));

            Node previous = null;
            
            while (remaining > 0) {
                /*System.out.printf("%d %s, %d, %d, %d\n", stringIndex, current.toString(), path, length, remaining);
                if (current != root) {
                    System.out.printf("this node is %d %d %d\n", current.terminus, current.start, current.stop);
                }*/
                int childIndex = string.charAt(stringIndex) - 'a';

                // Check if insertion is at current node and not down an edge.
                if (path == -1 || length == 0) {
                    // If the current node has no child for the given character,
                    // create that node and decrement remaining.
                    if (current.getChild(childIndex) == null) {
                        current.setChild(childIndex, new Node(stringIndex, stringIndex, -1));
                        remaining--;
                    }
                    // Else adjust the current parameters and break the loop.
                    else {
                        path = childIndex;
                        length++;
                        break;
                    }
                }
                // Else check down an edge.
                else {
                    Node edgeNode = current.getChild(path);
                    char compareChar = string.charAt(edgeNode.getStart() + length);
                   
                    // Adjust the current parameters and break the loop.
                    if (compareChar == string.charAt(stringIndex)) {
                        length++;
                        int start = (edgeNode.getStart() != -1) ? edgeNode.getStart() : 0;
                        int stop = (edgeNode.getStop() != -1) ? edgeNode.getStop() : stringIndex;
                        int diff = stop - start;
                        if (length >= diff) {
                            path = -1;
                            length = 0;
                            current = edgeNode;
                        }
                        break;
                    }
                    else {
                        int compareIndex = compareChar - 'a';

                        edgeNode.setStop(edgeNode.getStart() + length);
                        edgeNode.setChild(compareIndex, new Node(edgeNode.getTerminus(),
                                                                 edgeNode.getStop(),
                                                                 -1));
                        edgeNode.setChild(childIndex, new Node(stringIndex, stringIndex, -1));
                       
                        if (previous != null) {
                            //System.out.printf("prev:%s link:%s\n", previous.toString(), edgeNode.toString());
                            previous.setLink(edgeNode);
                            //System.out.println("setting link");
                            previous = edgeNode;
                        }
                        else {
                            previous = edgeNode;
                        }
                        
                        edgeNode.setTerminus(-1);
                        
                        if (current != root) {
                            //System.out.printf("current%s link\n", current.toString());
                            if (current.getLink() != null) {
                                current = current.getLink();
                                //System.out.println("following link");
                            }
                            else {
                                current = root;
                                //System.out.println("no link");
                            }
                        }
                        else {
                            path = string.charAt(stringIndex - length + 1) - 'a';
                            length--;
                        }
                        
                        remaining--;
                    }
                }
            }
        }
    }

    // Wrapper to draw the tree onto a context. This calls the recursive draw function
    // starting at the root of the tree, with an initial depth of diameter and
    // an inset of zero.
    public void draw(GraphicsContext context, int width) {
        draw(context, root, width, diameter, 0);
    }

    private void draw(GraphicsContext context, Node node, int width, int depth, int inset) {
        // What good is a recursive function without a base case?
        if (node == null) {
            return;
        }
        
        // A bunch of geometric variables used for drawing the ovals and edges.
        final int centerX = inset + (width / 2);
        // I just renamed depth here to match centerX...
        final int centerY = depth;
        final int ovalX = centerX - (diameter / 2);
        final int ovalY = centerY - (diameter / 2);
        final int innerDiameter = diameter - ovalThickness;
        final int innerOvalX = ovalX + (ovalThickness / 2);
        final int innerOvalY = ovalY + (ovalThickness / 2);

        int length = minimumLength;

        // Set the color of the drawing based on the instance variable for color.
        context.setFill(color);
        context.setStroke(color);

        // Count up all the children of this node.
        int children = 0;
        for (int childIndex = 0; childIndex < CHILDREN; childIndex++) {
            Node child = node.getChild(childIndex);
            if (child != null) {
                children++;
                
                // Set the length of the edges in this draw session to be the largest of all
                // the children's lengths, assuming any are larger than minimumLength.
                if (child.getLength() > length) {
                    length = child.getLength();
                }
            }
        }

        // Calculate how many horizontal slices are available based on the number of children.
        int widthSegment = width / ((children > 0) ? children : 1);

        // Set the target x coordinate for each edge line as i number of widthSegments,
        // with an added left-justified offset of inset. Also offset by half of a widthSegment.
        // The inset gives other nodes that haven't been visited yet to the left of the
        // current node room to print their children.
        for (int i = 0; i < children; i++) {
            int target = inset + (widthSegment / 2) + (i * widthSegment);
            context.strokeLine(centerX, centerY, target, centerY + length); 
        }

        // Draw two ovals, the outer oval being the color of the tree, and the
        // inner oval being white.
        context.fillOval(ovalX, ovalY, diameter, diameter);
        context.setFill(Color.WHITE);
        context.fillOval(innerOvalX, innerOvalY, innerDiameter, innerDiameter);
        context.setFill(color);
       
        //************************************************ CONSTRUCTION AREA
        context.setTextAlign(TextAlignment.CENTER);
        context.setFont(new Font(fontSize));
        //context.fillText(Integer.toString(node.terminus), centerX, centerY);
        int start = (node.getStart() == -1) ? 0 : node.getStart();
        int stop = (node.getStop() == -1) ? string.length() : node.getStop();
        context.fillText(string.substring(start, stop), centerX, centerY);
        //*********************************************

        // Recursively call this function on all the children.
        for (int i = 0, j = 0; i < CHILDREN; i++) {
            if (node.getChild(i) != null) {
                int newInset = inset + widthSegment * j;
                draw(context, node.getChild(i), widthSegment, depth + length, newInset);
                j++;
            }
        }
    }

    // Temporary ***************************************************
    // probably temporary
    public void print() {
        print(root, 0);
    }

    // temp
    private void print(Node node, int level) {
        if (node == null) {
            return;
        }
        int stringStart = (node.getStart() == -1) ? 0 : node.getStart();
        int stringStop = (node.getStop() == -1) ? string.length() : node.getStop();

        String suffix = string.substring(stringStart, stringStop);
        System.out.printf("level %d: ", level);
        if (node.getLink() != null) {
            System.out.printf("linkFound to node %d (%d %d) %s |||",
                              node.getLink().getTerminus(),
                              node.getLink().getStart(),
                              node.getLink().getStop(),
                              string.substring(node.getLink().getStart(),
                                               node.getLink().getStop()));
        }
        System.out.printf("node %d (%d %d) %s\n",
                          node.getTerminus(),
                          node.getStart(),
                          node.getStop(),
                          suffix);
        for (int i = 0; i < 26; i++) {
            print(node.getChild(i), level+1);
        }
    }//*************************************************************
}

// Provides nodes for the suffix tree. This node also contains edge information.
// Storing the edge information with the nodes cuts down on the complexity and
// number of objects required in this implementation.
class Node {
    private int stop;
    private int start;
    private int length;
    private int terminus;
    private Node[] children;
    private Node suffixLink;

    // Construct a root node.
    public Node() {
        stop = 0;
        start = 0;
        length = 0;
        terminus = -1;
        suffixLink = null;
        children = new Node[26];//magic
    }

    // Construct a node with a provided terminus, as well as indices for the substring
    // contained on the edge leading into the node.
    public Node(int terminus, int start, int stop) {
        length = 1;
        this.stop = stop;
        suffixLink = null;
        this.start = start;
        this.terminus = terminus;
        children = new Node[26];//magic
    }

    // Getter for length.
    public int getLength() {
        return length;
    }

    // Getter for starting index of substring on incoming edge.
    public int getStart() {
        return start;
    }

    // Getter for ending index of substring on incoming edge.
    public int getStop() {
        return stop;
    }

    // Getter for terminating index of substring in string.
    public int getTerminus() {
        return terminus;
    }

    // Getter for child node of node.
    public Node getChild(int index) {
        return children[index];
    }

    // Getter for suffix link of node.
    public Node getLink() {
        return suffixLink;
    }
    
    // Set the ending index and update the length parameter.
    public void setStop(int stop) {
        this.stop = stop;
        length = stop - start;
    }

    // Setter for terminating index of substring in string.
    public void setTerminus(int terminus) {
        this.terminus = terminus;
    }

    // Setter for child at index in children array.
    public void setChild(int index, Node child) {
        children[index] = child;
    }

    // Setter for suffix link.
    public void setLink(Node suffixLink) {
        this.suffixLink = suffixLink;
    }
}
