import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

class QuickNode {
    int length;
    int label;
    QuickNode next[];

    public QuickNode(int label, int length) {
        this.length = length;
        this.label = label;
        next = new QuickNode[26];
    }
}

public class QuickTree {
    QuickNode root;
    int diameter;
    int ovalThickness;
    int fontSize;
    int defaultLength;
    Color color;

    public QuickTree(int diameter, int defaultLength, Color color) {
         root = new QuickNode(0, 10);
         this.diameter = diameter;
         this.defaultLength = defaultLength;
         this.color = color;
         
         ovalThickness = 10;
         fontSize = 26;
         addNodes();
    }

    public void addNodes() {
        root.next[0] = new QuickNode(1, 30);
        root.next[1] = new QuickNode(2, 50);
        root.next[2] = new QuickNode(3, 20);
        root.next[3] = new QuickNode(4, 10);
        QuickNode down = root.next[2];
        down.next[4] = new QuickNode(5, 45);
        down.next[8] = new QuickNode(6, 200);
        down.next[25] = new QuickNode(7, 20);
        down = down.next[4];
        down.next[1] = new QuickNode(8, 36);
        down.next[2] = new QuickNode(9, 45);
        down = root.next[0];
        down.next[0] = new QuickNode(10, 120);
    }

    
    public void drawTree(GraphicsContext context, int width, int depth) {
        drawTree(context, root, width, depth, 0);
    }

    private void drawTree(GraphicsContext context,
                          QuickNode node,
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
            if (node.next[i] != null) {
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
        context.fillText(Integer.toString(node.label), centerX, centerY);

        // All done. :)
        if (children < 1) {
            return;
        }

        for (int i = 0, j = 0; i < 26; i++) {//magic
            if (node.next[i] != null) {
                drawTree(context,
                         node.next[i],
                         widthSegment,
                         depth + length,
                         inset + widthSegment * j);
                j++;
            }
        }
    }
}
