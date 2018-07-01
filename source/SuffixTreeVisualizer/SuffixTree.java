// Part of Suffix Tree Visualizer by Tiger Sachse

//package SuffixTreeVisualizer;

public class SuffixTree {
    public String string;
    private Node root;

    public SuffixTree(String string) {
        this.string = string;
        buildTree();
    }

    private void _buildTree() {
        root = new Node();
        root.children[0] = new Node(0, 1, 1);
        root.children[10] = new Node(1, 20, 20);
        root.children[12] = new Node(2, 30, 31);
        root.children[10].children[2] = new Node(3, 100, 100);
    }

    private void buildTree() {
        root = new Node();
       
        // Parameters to track location in the tree.
        Node current = root;
        int path = -1;
        int length = 0;
        int remaining = 0;


        for (int stringIndex = 0; stringIndex < string.length(); stringIndex++) {
            remaining++;
           
            System.out.println(string.charAt(stringIndex));
            
            Node previous = null;
            
            while (remaining > 0) {
                int childIndex = string.charAt(stringIndex) - 'a';

                // Check if insertion is at current node and not down an edge.
                if (path == -1 || length == 0) {
                    // If the current node has no child for the given character,
                    // create that node and decrement remaining.
                    if (current.children[childIndex] == null) {
                        current.children[childIndex] = new Node(stringIndex, stringIndex, -1);
                        remaining--;
                        previous = current;//???? maybe garbage
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
                        break;
                    }
                    else {
                        int compareIndex = compareChar - 'a';

                        edgeNode.edgeStop = edgeNode.edgeStart + length;
                        edgeNode.children[compareIndex] = new Node(edgeNode.index,
                                                                   edgeNode.edgeStop,
                                                                   -1);
                        edgeNode.children[childIndex] = new Node(stringIndex, stringIndex, -1);
                       
                        //segfaulting
                        if (edgeNode != root && edgeNode.suffixLink != null) {
                            current = edgeNode.suffixLink;
                        }
                        else {
                            current = root;
                        }

                        if (previous != null) {
                            previous.suffixLink = edgeNode;
                        }
                        else {
                            previous = edgeNode;
                        }

                        edgeNode.index = -1;
                        
                        path = string.charAt(stringIndex - length + 1) - 'a';
                        
                        remaining--;
                        length--;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SuffixTree tree = new SuffixTree("abcabxabcd");
        //SuffixTree tree = new SuffixTree("xyz");
        //SuffixTree tree = new SuffixTree("ababab");
        tree.print();
    }


    public void print() {
        print(root, 0);
    }
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
    }
}

class Node {
    public int edgeStart;
    public int edgeStop;
    public int index;
    public Node[] children;
    public Node suffixLink;

    public Node() {
        edgeStart = -1;
        edgeStop = -1;
        index = -1;
        children = new Node[26];//magic
        suffixLink = null;
    }

    public Node(int index, int edgeStart, int edgeStop) {
        this.edgeStart = edgeStart;
        this.edgeStop = edgeStop;
        this.index = index;
        children = new Node[26];//magic
        suffixLink = null;
    }
}
