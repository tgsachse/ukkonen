// Part of Suffix Tree Visualizer by Tiger Sachse

package SuffixTreeVisualizer;

import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

// Provides a suffix tree that can be built and printed in JavaFX.
public class SuffixTree {
    private Node root;
    private String string;
    public int diameter;
    private int ovalThickness;
    private int fontSize;
    private int defaultLength;
    private Color color;

    // Constructor that also builds the tree.
    public SuffixTree(String string, int diameter, int defaultLength, Color color) {
        this.string = string;
        this.diameter = diameter;
        this.defaultLength = defaultLength;
        this.color = color;
        buildTree();
    }

    // Build the suffix tree using Ukkonen's algorithm.
    private void buildTree() {
        root = new Node();
       
        // Parameters to track location in the tree.
        Node current = root;
        int path = -1;
        int length = 0;
        int remaining = 0;


        System.out.println("strIndex index path length remaining");
        for (int stringIndex = 0; stringIndex < string.length(); stringIndex++) {
            remaining++;
           
            //System.out.println(string.charAt(stringIndex));

            Node previous = null;
            
            while (remaining > 0) {
                System.out.printf("%d %s, %d, %d, %d\n", stringIndex, current.toString(), path, length, remaining);
                if (current != root) {
                    System.out.printf("this node is %d %d %d\n", current.index, current.edgeStart, current.edgeStop);
                }
                int childIndex = string.charAt(stringIndex) - 'a';

                // Check if insertion is at current node and not down an edge.
                if (path == -1 || length == 0) {
                    // If the current node has no child for the given character,
                    // create that node and decrement remaining.
                    if (current.children[childIndex] == null) {
                        current.children[childIndex] = new Node(stringIndex, stringIndex, -1);
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
                    Node edgeNode = current.children[path];
                    char compareChar = string.charAt(edgeNode.edgeStart + length);
                   
                    // Adjust the current parameters and break the loop.
                    if (compareChar == string.charAt(stringIndex)) {
                        length++;
                        int start = (edgeNode.edgeStart != -1) ? edgeNode.edgeStart : 0;
                        int stop = (edgeNode.edgeStop != -1) ? edgeNode.edgeStop : stringIndex;
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

                        edgeNode.edgeStop = edgeNode.edgeStart + length;
                        edgeNode.children[compareIndex] = new Node(edgeNode.index,
                                                                   edgeNode.edgeStop,
                                                                   -1);
                        edgeNode.children[childIndex] = new Node(stringIndex, stringIndex, -1);
                       
                        if (previous != null) {
                            System.out.printf("prev:%s link:%s\n", previous.toString(), edgeNode.toString());
                            previous.suffixLink = edgeNode;
                            System.out.println("setting link");
                            previous = edgeNode;
                        }
                        else {
                            previous = edgeNode;
                        }
                        
                        edgeNode.index = -1;
                        
                        if (current != root) {
                            //System.out.printf("current%s link\n", current.toString());
                            if (current.suffixLink != null) {
                                current = current.suffixLink;
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

    public void drawTree(GraphicsContext context, int width, int depth) {
        drawTree(context, root, width, depth, 0);
    }

    private void drawTree(GraphicsContext context,
                          Node node,
                          int width,
                          int depth,
                          int inset) {

        int centerX = inset + (width / 2);
        int centerY = depth;
        int ovalX = centerX - (diameter / 2);
        int ovalY = centerY - (diameter / 2);
        int length = defaultLength;
        int innerDiameter = diameter - ovalThickness;
        int innerOvalX = ovalX + (ovalThickness / 2);
        int innerOvalY = ovalY + (ovalThickness / 2);

        int children = 0;
        for (int i = 0; i < 26; i++) {//magic fix later
            if (node.children[i] != null) {
                children++;
            }
            if (node.length > length) {
                length = node.length;
            }
        }

        int widthSegment = width / ((children > 0) ? children : 1);
        int lineTargets[] = new int[children];
        for (int i = 0; i < children; i++) {
            lineTargets[i] = inset + (widthSegment / 2) + (i * widthSegment);
        }

        context.setFill(color);
        context.setStroke(color);

        for (int i = 0; i < children; i++) {
            context.strokeLine(centerX, centerY, lineTargets[i], centerY + length); 
        }

        // Draw two ovals, the outer oval being the color of the tree, and the
        // inner oval being white.
        context.fillOval(ovalX, ovalY, diameter, diameter);
        context.setFill(Color.WHITE);
        context.fillOval(innerOvalX, innerOvalY, innerDiameter, innerDiameter);
        context.setFill(color);
        
        context.setTextAlign(TextAlignment.CENTER);
        context.setFont(new Font(fontSize));
        context.fillText(Integer.toString(node.index), centerX, centerY);

        // All done. :)
        if (children < 1) {
            return;
        }

        for (int i = 0, j = 0; i < 26; i++) {//magic
            if (node.children[i] != null) {
                drawTree(context,
                         node.children[i],
                         widthSegment,
                         depth + length,
                         inset + widthSegment * j);
                j++;
            }
        }
    }

    // Temporary ***************************************************
    /*
    public static void main(String[] args) {
        SuffixTree tree = new SuffixTree("abcabxabcd");
        //SuffixTree tree = new SuffixTree("xyz");
        //SuffixTree tree = new SuffixTree("ababab");
        tree.print();
    }*/

    // Temporary
    private void _buildTree() {
        root = new Node();
        root.children[0] = new Node(0, 1, 1);
        root.children[10] = new Node(1, 20, 20);
        root.children[12] = new Node(2, 30, 31);
        root.children[10].children[2] = new Node(3, 100, 100);
    }
    
    // probably temporary
    public void print() {
        print(root, 0);
    }

    // temp
    private void print(Node node, int level) {
        if (node == null) {
            return;
        }
        int stringStart = (node.edgeStart == -1) ? 0 : node.edgeStart;
        int stringStop = (node.edgeStop == -1) ? string.length() : node.edgeStop;

        String suffix = string.substring(stringStart, stringStop);
        System.out.printf("level %d: ", level);
        if (node.suffixLink != null) {
            System.out.printf("linkFound to node %d (%d %d) %s |||",
                              node.suffixLink.index,
                              node.suffixLink.edgeStart,
                              node.suffixLink.edgeStop,
                              string.substring(node.suffixLink.edgeStart,
                                               node.suffixLink.edgeStop));
        }
        System.out.printf("node %d (%d %d) %s\n",
                          node.index,
                          node.edgeStart,
                          node.edgeStop,
                          suffix);
        for (int i = 0; i < 26; i++) {
            print(node.children[i], level+1);
        }
    }//*************************************************************
}

// Provides nodes for the suffix tree. These nodes also contain information
// for the edges between the nodes. Storing the edge information with the nodes
// cuts down on the complexity and number of objects required in this implementation.
class Node {
    public int index;
    public int length;
    public int edgeStop;
    public int edgeStart;
    public Node[] children;
    public Node suffixLink;

    // Construct a root node.
    public Node() {
        length = 1;
        index = -1;
        edgeStop = -1;
        edgeStart = -1;
        suffixLink = null;
        children = new Node[26];//magic
    }

    // Construct a node with a provided index, as well as indices for the substring
    // contained on the edge leading into the node.
    public Node(int index, int edgeStart, int edgeStop) {
        length = 1;
        suffixLink = null;
        this.index = index;
        children = new Node[26];//magic
        this.edgeStop = edgeStop;
        this.edgeStart = edgeStart;
    }
}
