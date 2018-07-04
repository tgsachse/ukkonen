public class SuffixTree2 {

    private final int SENTINEL = -1;
    private final int CHILDREN = 27;
    
    private char characterShift = 'a';
    private String string = "abcbc$";

    private void build() {
        Node root = new Node(0, 0, SENTINEL, CHILDREN);

        int remaining = 0;
        Triple triple = new Triple(root, 0, 0);

        for (int stringIndex = 0; stringIndex < string.length(); stringIndex++) {
            remaining++;

            char stringChar = string.charAt(stringIndex);
            int charIndex = getCharIndex(stringChar);

            while (remaining > 0) {
                int checkIndex = triple.node.children[triple.edge].start + triple.length;
                if (string.charAt(checkIndex) == string.charAt(stringIndex)) {
                    triple.length++;
                    break;
                }
                else {
                    triple.node.children[triple.edge] = new Node(stringIndex,
                                                                 SENTINEL,
                                                                 stringIndex,
                                                                 CHILDREN);
                    
                }
            }
        }
    }

    /*
    private Node getWorkingNode(Triple triple) {
        if (triple.edge == SENTINEL || triple.node.stop == SENTINEL) {
            return triple.node;
        }
        else if (triple.length >= triple.node.length) {
            return triple.node.children[triple.edge]; 
        }
        else {
            return triple.node;
        }
    }

    */
    private int getCharIndex(char character) {
        if (character == '$') {
            return CHILDREN - 1;
        }
        else {
            return character - characterShift;
        }
    }
}

class Node {
    public int start;
    public int stop;
    public int length;
    public Node[] children;

    public Node(int start, int stop, int terminus, int children) {
        
    }

}

class Triple {
    public int edge;
    public Node node;
    public int length;

    public Triple(Node node, int edge, int length) {
        this.node = node;
        this.edge = edge;
        this.length = length;
    }
}
