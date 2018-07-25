// Part of Willow by Tiger Sachse.

//package Willow;

import java.util.*;

public class SuffixTree2 {
    private Node root;
    private String string;

    public static void main(String[] args) {
        SuffixTree2 tree = new SuffixTree2("abcab$");
        tree.print();
    }

    public void print() {
        System.out.println("Start, Begin, End, String");
        print(root);
    }

    private void print(Node current) {
        if (current == null) {
            return;
        }

        System.out.println("Node:");
        System.out.printf("%d, %d, %d, ",
                          current.start,
                          current.indices.begin,
                          current.indices.end);
        if (current.indices.end > current.indices.begin) {
            System.out.printf("%s\n",
                              string.substring(current.indices.begin, current.indices.end + 1));
        }
        else {
            System.out.println("(no string)");
        }

        for (Node child : current.children.values()) {
            print(child);
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
        Node parent = root;
        Node child = root;

        int suffixPosition = index;
        int edgePosition = 0;

        while (suffixPosition < string.length()) {
            if (edgePosition > child.indices.end) {
                parent = child;
                child = child.getChild(string.charAt(suffixPosition));

                if (child == null) {
                    parent.addChild(string.charAt(suffixPosition),
                                    index,
                                    suffixPosition,
                                    string.length() - 1);
                    break;
                }
            }
            
            if (string.charAt(suffixPosition) != string.charAt(edgePosition)) {
                // not set up right.
                Node split = new Node(null, child.indices.begin, edgePosition - 1);
                parent.addChild(string.charAt(child.indices.begin), split);

                child.indices.begin = edgePosition;
                split.addChild(string.charAt(edgePosition), child);
                split.addChild(string.charAt(suffixPosition),
                               index,
                               suffixPosition,
                               string.length() - 1);
                break;
            }
            else {
                suffixPosition++;
                edgePosition++;
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
        indices = new Indices(-1, -1);
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

    public void addChild(Character key, Integer childStart, int childBegin, int childEnd) {
        children.put(key, new Node(childStart, childBegin, childEnd));
    }

    public void addChild(Character key, Node child) {
        children.put(key, child);
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
