
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;

public class Grid extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private static JFrame ob = new JFrame("Grid");
    private final int m, n;
    private final Cell cells[][];
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;
    private int radius, length, padding;
    private boolean flag;
    private Node node1, node2, src, dest;
    private String mouseCoords;
        
    public static void main(String[] a) {
        ob.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Grid p = new Grid();
        ob.add(p);
        ob.setSize(500, 500);
        //ob.setUndecorated(true);
        ob.setVisible(true);
        p.addMouseListener(p);
        p.addKeyListener(p);
        p.addMouseMotionListener(p);
    }

    Grid() {
        flag = true;
        m = n = 5;
        radius = 5;
        length = 70;
        padding = 0;
        mouseCoords = "Init";

        cells = new Cell[m][n];
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell(i, j, length / 2 * (2 * j + 1), length / 2 * (2 * i + 1), length);
            }
        }
    }

    private Node getNodeInCell(Cell cell) {
        for (Node node : nodes) {
            if (getCell(node.getX(), node.getY()).equals(cell)) {
                return node;
            }
        }
        return null;
    }

    public boolean nodeExists(int x, int y) {
        for (Node node : nodes) {
            if ((node.getX() - x) * (node.getX() - x) + (node.getY() - y) * (node.getY() - y) < radius * radius) {
                return true;
            }
        }

        return false;
    }

    private Cell getCell(int x, int y) {
        for (Cell cellRow[] : cells) {
            for (Cell cell : cellRow) {
                if (Math.abs(x - cell.getX()) * 2 <= cell.getLength() && Math.abs(y - cell.getY()) * 2 <= cell.getLength()) {
                    return cell;
                }
            }
        }

        return null;
    }

    private boolean areNeighbours(Cell cell1, Cell cell2) {
        if (cell1.getPosX() == cell2.getPosX() && Math.abs(cell1.getPosY() - cell2.getPosY()) == 1) {
            return true;
        } else if (cell1.getPosY() == cell2.getPosY() && Math.abs(cell1.getPosX() - cell2.getPosX()) == 1) {
            return true;
        }

        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.WHITE);

        for (Cell cellRow[] : cells) {
            for (Cell cell : cellRow) {
                if ((cell.getPosX() + cell.getPosY()) % 2 == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(Color.yellow);
                }
                g.fillRect(cell.getX() - cell.getLength() / 2, 40 + cell.getY() - cell.getLength(), cell.getLength(), cell.getLength());
            }
        }
        g.setColor(Color.black);
        for (Node node : nodes) {
            g.fillOval(node.getX(), node.getY(), 2 * node.getRadius(), 2 * node.getRadius());
            g.drawString(node.getId() + " " + node.getPos(), node.getX(), node.getY());
        }

        for (Edge edge : edges) {
            g.drawLine(edge.getX1(), edge.getY1(), edge.getX2(), edge.getY2());
        }

        g.drawString(mouseCoords, 400, 400);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Cell cellClicked = getCell(e.getX(), padding + e.getY());
        Node nodeInCell = getNodeInCell(cellClicked);
        if (cellClicked != null) {
            System.out.println(cellClicked.getPosX() + "," + cellClicked.getPosY());
        }

        if (cellClicked == null) {
            System.out.println("Outside");
            flag = true;
        } else if (nodes.isEmpty()) {
            System.out.println("First!");
            nodes.add(new Node(nodes.size(), e.getX(), e.getY(), radius));
        } else if (flag == false && nodeInCell != null) {
            System.out.println("Reset");
            flag = true;
        } else if (flag == true && nodeInCell != null) {
            System.out.println("node1");
            node1 = nodeInCell;
            flag = false;
        } else if (flag == false && nodeInCell == null) {
            System.out.println("node2");
            if (areNeighbours(cellClicked, getCell(node1.getX(), node1.getY()))) {
                node2 = new Node(nodes.size(), e.getX(), e.getY(), radius);
                nodes.add(node2);
                edges.add(new Edge(node1, node2));
            }
            flag = true;
        } else {
            System.out.println("try again " + flag);
        }

        createGraph();

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Cell overCell = getCell(e.getX(), padding + e.getY());
        mouseCoords = (overCell == null) ? "None" : overCell.getPosX() + "," + (overCell.getPosY());
        repaint();
    }

    private void createGraph() {
        Graph g = new Graph(nodes.size());

        for (Edge edge : edges) {
            g.addEdge(edge.getNode1().getId(), edge.getNode2().getId());
        }

        int pos[] = g.BFS(0);
        for (int i = 0; i < pos.length; i++) {
            nodes.get(pos[i]).setPos(i);
        }

        int parent[] = g.getParent();

        for (int x : parent) {
            System.out.print(x + " ");
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class Cell {

    private final int x, y, posX, posY, length;

    public Cell(int posX, int posY, int x, int y, int length) {
        this.posX = posX;
        this.posY = posY;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getLength() {
        return length;
    }

}

class Node {

    private final int id, x, y, radius;
    private int pos;

    Node(int id, int x, int y, int radius) {
        this.id = id;
        this.radius = radius;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public int getId() {
        return id;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}

class Edge {

    private final Node node1, node2;
    private final int x1, y1, x2, y2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
        this.x1 = node1.getX();
        this.y1 = node1.getY();
        this.x2 = node2.getX();
        this.y2 = node2.getY();
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

}

class Graph {

    private final LinkedList<Integer> adj[];
    private final int pos[], V, parent[];

    Graph(int v) {
        pos = new int[v];
        parent = new int[v];
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i) {
            adj[i] = new LinkedList();
        }
    }

    void addEdge(int v, int w) {
        adj[v].add(w);
    }

    int[] BFS(int s) {
        boolean visited[] = new boolean[V];

        LinkedList<Integer> queue = new LinkedList<>();

        parent[s] = -1;
        visited[s] = true;
        queue.add(s);

        int x = 0;

        while (!queue.isEmpty()) {
            s = queue.poll();
            pos[x++] = s;

            Iterator<Integer> it = adj[s].listIterator();
            while (it.hasNext()) {
                int n = it.next();
                if (!visited[n]) {
                    parent[n] = s;
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }

        return pos;
    }

    public int[] getParent() {
        return parent;
    }
}
