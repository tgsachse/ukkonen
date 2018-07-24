// Part of Willow by Tiger Sachse.

//package Willow;

import java.util.*;

public class SuffixTree2 {
    private Node root;
    private String string;

    public static void main(String[] args) {
        SuffixTree2 tree = new SuffixTree2("abc");

        for (Node node : tree.root.children.values()) {
            System.out.printf("%d %d\n", node.indices.begin, node.indices.end);
            System.out.println(tree.string.substring(node.indices.begin, node.indices.end + 1)); 
        }
    }

    public SuffixTree2(String string) {
        this.string = string;

        build();
    }

    private void build() {
        root = new Node();

        for (int index = string.length() - 1; index >= 0; index--) {
            insertSuffix(index);
        }
    }

    private void insertSuffix(int index) {
        Node child = root.getChild(string.charAt(index));
        
        if (child == null) {
            root.addChild(string.charAt(index), index, index, string.length() - 1); 
        }
        else {

            // know that the first char matches
            int suffixPosition = index + 1;
            int edgePosition = child.indices.begin + 1;
            
            while (suffixPosition < string.length()) {
                if (edgePosition > child.indices.end) {
                    // reset child
                }

                if (string.charAt(suffixPosition) != string.charAt(edgePosition)) {
                    Node breakNode = new
                    child.addChild(string.charAt(edgePosition,
                                   child.start,
                                   edgePosition,
                                   child.indicies.end));
                    child.addChild(string.charAt(suffixPosition))
                }
                else {
                    suffixPosition++;
                    edgePosition++;
                }
            } 
        
        }
    }
}

class Node {
    public Integer start;
    public Indices indices;
    public HashMap<Character, Node> children;
    
    public Node() {
        start = null;
        indices = null;
        children = new HashMap<>();
    }

    public Node(Integer start, int begin, int end) {
        children = new HashMap<>();
        indices = new Indices(begin, end);

        this.start = start;
    }

    public Node getChild(Character child) {
        return children.get(child);
    }

    public void addChild(Character child, Integer childStart, int childBegin, int childEnd) {
        children.put(child, new Node(childStart, childBegin, childEnd));
    }
}

class Indices {
    public int end;
    public int begin;

    public Indices(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public int length() {
        return end - begin;
    }
}
