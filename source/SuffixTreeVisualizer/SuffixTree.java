// Part of Suffix Tree Visualizer by Tiger Sachse

package SuffixTreeVisualizer;

import java.util.*;

// Provides a suffix tree that can be built and printed in JavaFX.
public class SuffixTree {
    private Node root;
    private String string;
    private char charShift;

    private final int SENTINEL = -1;
    private final int CHILDREN = 27;
    private final boolean DEBUG = true;

    // Constructor that automatically builds the suffix tree.
    public SuffixTree(String string) {
        this.string = string;
        
        charShift = (Character.isUpperCase(string.charAt(0))) ? 'A' : 'a';

        build();
        
        if (DEBUG) {
            printDebuggingInformation();
        }
    }

    // Build the suffix tree using Ukkonen's algorithm. This function is
    // certainly too long, but it is not easily split into subfunctions because
    // of the importance of the triple parameters from round to round. I apologize
    // in advance, but if you really want to know how this algorithm works I suggest
    // looking up some (much better) articles.
    private void build() {
        root = new Node(0, 0, SENTINEL, CHILDREN);
       
        // Parameter triple to track location in the tree.
        int length = 0;
        int remaining = 0;
        int path = SENTINEL;

        Node current = root;

        if (DEBUG) {
            System.out.println("INDEX (NODE, EDGE, LENGTH) REMAINING");
        }

        // Process every character in the string.
        for (int stringIndex = 0; stringIndex < string.length(); stringIndex++) {
            Node previous = null;
            remaining++;
           
            
            // Get the integer value of the character at the current string index.
            int childIndex;
            if (string.charAt(stringIndex) == '$') {
                childIndex = CHILDREN - 1;
            }
            else {
                childIndex = string.charAt(stringIndex) - charShift;
            }

            // Perform the algorithm as long as there are suffixes remaining.
            while (remaining > 0) {
                if (DEBUG) {
                    System.out.printf("%d (%s, %d, %d) %d\n",
                                      stringIndex,
                                      current.toString(),
                                      path,
                                      length,
                                      remaining);
                }

                // Check if insertion is at current node and not down an edge.
                if (path == SENTINEL || length == 0) {
                    // If the current node has no child for the given character,
                    // create that node and decrement remaining.
                    if (current.getChild(childIndex) == null) {
                        current.setChild(childIndex, new Node(stringIndex,
                                                              SENTINEL,
                                                              stringIndex,
                                                              CHILDREN));
                        remaining--;
                    }
                    // Else adjust the parameter triple and break the loop (rule one).
                    else {
                        path = childIndex;
                        length++;
                        // Adjust the current node if the length extends past the
                        // present child's edge.
                        if (length >= current.getChild(childIndex).getLength(stringIndex)) {
                            path = SENTINEL;
                            length = 0;
                            current = current.getChild(childIndex);
                        }
                        break;
                    }
                }
                // Else check down an edge.
                else {
                    // Store the child for easy access.
                    Node child = current.getChild(path);
                   
                    // If the current character and the character on the edge
                    // at the appropriate length match, adjust the current
                    // parameters and break the loop.
                    if (string.charAt(child.getStart() + length) == string.charAt(stringIndex)) {
                        length++;
                        // Adjust the current node if the length extends past the
                        // present child's edge.
                        if (length >= child.getLength(stringIndex)) {
                            path = SENTINEL;
                            length = 0;
                            current = child;
                        }
                        break;
                    }
                    // Else the characters don't match and we need a new node.
                    else {
                        int compareIndex = string.charAt(child.getStart() + length) - charShift;

                        // Set the child's new stopping point, then create two new
                        // nodes. The first is for the rest of the child's old edge, and
                        // the second is for the mismatching character from the string.
                        child.setStop(child.getStart() + length);
                        child.setChild(compareIndex,
                                       new Node(child.getStop(),
                                                SENTINEL,
                                                child.getTerminus(),
                                                CHILDREN));
                        child.setChild(childIndex, new Node(stringIndex,
                                                            SENTINEL,
                                                            stringIndex,
                                                            CHILDREN));
                        
                        // If this is not the first newly created node, then apply rule two
                        // from Ukkonen's algorithm.
                        if (previous != null) {
                            if (DEBUG) {
                                System.out.printf("%s -> %s\n",
                                                  previous.toString(),
                                                  child.toString());
                            }

                            // Set the suffix link of the previous new node to point to
                            // the current new node.
                            previous.setLink(child);
                        }

                        // This node is now the previous node.
                        previous = child;
                       
                        // This child's terminus doesn't matter anymore, so it's set to
                        // a sentinel value.
                        child.setTerminus(SENTINEL);
                       
                        // If the current node is not the root, follow a suffix link
                        // if possible, (rule three).
                        if (current != root) {
                            if (current.getLink() != null) {
                                current = current.getLink();
                            }
                            else {
                                current = root;
                            }
                        }
                        // Else adjust the path and length values of the parameter triple.
                        else {
                            //if (length < 2) {
                              //  path = SENTINEL;
                            //}
                            //else {
                            if (length - 1 == 0) {
                                path = SENTINEL;
                                length--;
                            }else {//now missing final $
                              path = string.charAt(stringIndex - length + 1) - charShift;
                              System.out.printf("i%d l%d\n", stringIndex, length);
                              System.out.printf("path%d\n", path);
                                length--;
                                if (length >= current.getChild(path).getLength(stringIndex)) {//
                                    current = current.getChild(path);
                                    path = SENTINEL;
                                    length = 0;
                                    System.out.printf("this work %s\n", current.toString());
                                }
                            }
                            //}
                            //System.out.printf("%d length\n", length);
                            //length--;


                        }
                        
                        // One down! Several to go...
                        remaining--;
                    }
                }
            }
        }

        // After the conclusion of that nightmarish algorithm, replace any remaining
        // sentinels in the nodes.
        correctSentinels();

        setHeights();
    }

    // Wrapper to draw the tree onto a context. This calls the recursive draw function
    // starting at the root of the tree, with an initial depth of radius and
    // an inset of zero.
    public void draw(NodePane pane, int width) {
        draw(pane, root, width, pane.getRadius(), 0);
    }

    // Recursively draw each node and it's edges.
    private void draw(NodePane pane, Node node, int width, int depth, int inset) {
        // What good is a recursive function without a base case?
        if (node == null) {
            return;
        }
        
        // A bunch of geometric variables used for drawing the ovals and edges.
        // I just renamed depth here to match centerX...
        final int centerY = depth;
        final int centerX = inset + (width / 2);

        int length = pane.getMinimumLength();
        Node[] children = node.getChildren();
        
        // Calculate how many horizontal slices are available based on the number of children.
        int widthSegment = width / ((node.getChildCount() > 0) ? node.getChildCount() : 1);

        Node[] sortedChildren = MiddleSort.sort(children, node.getChildCount());
        
        // Draw the edges going to each child.
        int childIndex = 0;
        for (Node child : sortedChildren) {
            int targetX = inset + (widthSegment / 2) + (childIndex * widthSegment);
            String suffix = child.getSuffix(string);

            pane.drawEdge(centerX, centerY, targetX, centerY + length, suffix);

            childIndex++;
        }

        pane.drawNode(centerX, centerY, node);

        // Recursively call this function on all the children.
        childIndex = 0;
        for (Node child : sortedChildren) {
            int newInset = inset + widthSegment * childIndex;

            draw(pane, child, widthSegment, depth + length, newInset);

            childIndex++;
        }
    }

    private void setHeights() {
        setHeight(root);
    }

    private Integer setHeight(Node node) {
        Integer height = Integer.valueOf(-1);
        
        if (node == null) {
            return height;
        }

        for (Node child : node.getChildren()) {
            int childHeight = setHeight(child);
            if (childHeight > height) {
                height = childHeight;
            }
        }

        node.setHeight(height + 1);

        return height + 1;
    }

    private void correctSentinels() {
        correctSentinel(root);
    }

    // During the build process a SENTINEL is used to indicate that the stop
    // index of an edge (inside a node) is running until the current childIndex of
    // the build algorithm. Once the algorithm is complete any remaining SENTINELs
    // must be set to equal the length of the full string, indicating that the stop
    // index of that edge is the end of the string. This function finds the SENTINELs
    // and replaces them appropriately.
    private void correctSentinel(Node node) {
        // Safety base case.
        if (node == null) {
            return;
        }
        
        // If this node contains a sentinel, it doesn't anymore!
        if (node.getStop() == SENTINEL) {
            node.setStop(string.length());
        }

        // Call this function on all children.
        for (int childIndex = 0; childIndex < CHILDREN; childIndex++) {
            correctSentinel(node.getChild(childIndex));
        }
    }

    // Public wrapper to call the recursive debugging information function,
    // starting at the root with an initial level of zero.
    public void printDebuggingInformation() {
        System.out.println("TERMINUS (START, STOP) SUFFIX");
        printDebuggingInformation(root, 0);
    }

    // Recursively print the tree to the terminal with debugging information.
    private void printDebuggingInformation(Node node, int level) {
        // Base case for the recursive function.
        if (node == null) {
            return;
        }
        
        // Print some fancy bars to indicate depth of the node.
        for (int braceCount = 0; braceCount < level; braceCount++) {
            System.out.printf("| ");
        }
   
        // Print the fundamental properties of this node.
        String suffix = string.substring(node.getStart(), node.getStop());
        System.out.printf("%d (%d %d) %s",
                          node.getTerminus(),
                          node.getStart(),
                          node.getStop(),
                          suffix);

        // Print a message if a suffix link exists.
        Node link = node.getLink();
        if (link != null) {
            System.out.printf(" | %s -> %s\n", node.toString(), link.toString());
        }
        else {
            System.out.printf("\n");
        }
        
        // Recursively call this function for all children of the node.
        for (int childIndex = 0; childIndex < CHILDREN; childIndex++) {
            printDebuggingInformation(node.getChild(childIndex), level + 1);
        }
    }
}

// Provides nodes for the suffix tree. This node also contains edge information.
// Storing the edge information with the nodes cuts down on the complexity and
// number of objects required in this implementation.
class Node implements Comparable<Node> {
    private int stop;
    private int start;
    private int length;
    private int terminus;
    private Integer height;
    private int childCount;
    private Node[] children;
    private Node suffixLink;

    // Construct a node with a provided terminus, as well as indices for the substring
    // contained on the edge leading into the node.
    public Node(int start, int stop, int terminus, int children) {
        length = 1;
        childCount = 0;
        this.stop = stop;
        suffixLink = null;
        this.start = start;
        this.terminus = terminus;
        this.children = new Node[children];
    }

    @Override
    public int compareTo(Node other) {
        return height.compareTo(other.getHeight());
    }

    public int getChildCount() {
        return childCount;
    }

    public Node[] getChildren() {
        return children;
    }

    public Integer getHeight() {
        return height;
    }

    // Getter for length. Only callable after build process is complete and
    // sentinels have been removed (e.g. once the constructor is finished).
    public int getLength() {
        return length;
    }

    // Getter for length. Can be called during the build process with a provided
    // index.
    public int getLength(int index) {
        if (stop > 0) {
            return length;
        }
        else {
            return index - start;
        }
    }

    // Get the string represented by the start and stop indices of the node.
    public String getSuffix(String string) {
        return string.substring(start, stop);
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
   
    public void setHeight(Integer height) {
        this.height = height;
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
        if (children[index] == null && child != null) {
            childCount++;
        }
        else if (children[index] != null && child == null) {
            childCount--;
        }
        children[index] = child;
    }

    // Setter for suffix link.
    public void setLink(Node suffixLink) {
        this.suffixLink = suffixLink;
    }
}

class MiddleSort {
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
