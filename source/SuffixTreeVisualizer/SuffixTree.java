// Part of Suffix Tree Visualizer by Tiger Sachse

package SuffixTreeVisualizer;

public class SuffixTree {
    public String string;
    private Node root;

    public SuffixTree(String string) {
        this.string = string;
        buildTree();
    }

    private void buildTree() {
        root = new Node();
        
        Node current = root;
        int path = -1;
        int length = 0;
        int remaining = 0;

        for (int i = 0; i < 3; i++) {//magic
            remaining++;

            int characterIndex = string.charAt(i) - 'a';//magic
            while (remaining > 0) {

                if (path == -1) {
                    //at current node not down an edge
                }
                else {
                    Node working = current.children[path];
                    char match = string.charAt(working.edgeStart + length);

                    
                }
            }
        }
    }
}

class Node {
    public int edgeStart;
    public int edgeStop;
    public int index;
    public Node[] children;

    public Node() {
        edgeStart = -1;
        edgeStop = -1;
        index = -1;
        children = new Node[26];//magic
    }

    public Node(int index, int edgeStart, int edgeStop) {
        this.edgeStart = edgeStart;
        this.edgeStop = edgeStop;
        this.index = index;
        children = new Node[26];//magic
    }
}
