package TeamProject;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.*;

public class SettingFrameConnect {
private static final String BASE_URL = "http://localhost:8080/settings";
private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521";
private static final String JDBC_USER = "system";
private static final String JDBC_PASSWORD = "databaseoracle";
private static JComboBox<String> todoComboBox = new JComboBox<>();
private static JComboBox<String> eventComboBox = new JComboBox<>();

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        createAndShowGUI();
    });
}

private static void createAndShowGUI() {
    JFrame f = new JFrame("사용자 설정");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(500, 600);
    f.setLocationRelativeTo(null);

    JPanel p1 = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel l3 = new JLabel("과목 목록");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    p1.add(l3, gbc);

    DefaultMutableTreeNode taskRoot = new DefaultMutableTreeNode("과목");
    DefaultTreeModel taskTreeModel = new DefaultTreeModel(taskRoot);
    JTree taskTree = new JTree(taskTreeModel);
    taskTree.setEditable(true);
    taskTree.setDragEnabled(true);
    taskTree.setDropMode(DropMode.ON_OR_INSERT);
    taskTree.setTransferHandler(new TreeTransferHandler(taskTree, taskTreeModel, true));

    JTree eventTree = null;
	taskTree.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                TreePath path = taskTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    taskTree.setSelectionPath(path);
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    String nodeName = JOptionPane.showInputDialog(f, "새로운 하위 항목 이름:");
                    if (nodeName != null && !nodeName.trim().isEmpty()) {
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
                        selectedNode.add(newNode);
                        taskTreeModel.reload();
                        updateComboBoxes(taskTree, eventTree);
                    }
                }
            }
        }
    });

    JScrollPane taskTreeView = new JScrollPane(taskTree);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.gridheight = 4;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    p1.add(taskTreeView, gbc);

    JButton addTaskButton = new JButton("과목 추가");
    addTaskButton.addActionListener(e -> {
        String taskName = JOptionPane.showInputDialog(f, "새로운 과목 이름:");
        if (taskName != null && !taskName.trim().isEmpty()) {
            DefaultMutableTreeNode newTask = new DefaultMutableTreeNode(taskName);
            taskRoot.add(newTask);
            taskTreeModel.reload();
            saveTask(taskName);
            updateComboBoxes(taskTree, eventTree);
        }
    });
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.insets = new Insets(5, 10, 0, 10);
    p1.add(addTaskButton, gbc);

    JLabel l4 = new JLabel("이벤트 목록");
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    gbc.insets = new Insets(5, 10, 0, 10);
    p1.add(l4, gbc);

    DefaultMutableTreeNode eventRoot = new DefaultMutableTreeNode("이벤트");
    DefaultTreeModel eventTreeModel = new DefaultTreeModel(eventRoot);
    JTree eventTree1 = new JTree(eventTreeModel);
    eventTree1.setEditable(true);
    eventTree1.setDragEnabled(true);
    eventTree1.setDropMode(DropMode.ON_OR_INSERT);
    eventTree1.setTransferHandler(new TreeTransferHandler(eventTree1, eventTreeModel, false));

    eventTree1.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                TreePath path = eventTree1.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    eventTree1.setSelectionPath(path);
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    String nodeName = JOptionPane.showInputDialog(f, "새로운 하위 항목 이름:");
                    if (nodeName != null && !nodeName.trim().isEmpty()) {
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
                        selectedNode.add(newNode);
                        eventTreeModel.reload();
                        updateComboBoxes(taskTree, eventTree1);
                    }
                }
            }
        }
    });

    JScrollPane eventTreeView = new JScrollPane(eventTree1);
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 3;
    gbc.gridheight = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    p1.add(eventTreeView, gbc);

    JButton addEventButton = new JButton("이벤트 추가");
    addEventButton.addActionListener(e -> {
        String eventName = JOptionPane.showInputDialog(f, "새로운 이벤트 이름:");
        if (eventName != null && !eventName.trim().isEmpty()) {
            DefaultMutableTreeNode newEvent = new DefaultMutableTreeNode(eventName);
            eventRoot.add(newEvent);
            eventTreeModel.reload();
            saveEvent(eventName);
            updateComboBoxes(taskTree, eventTree1);
        }
    });
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.insets = new Insets(5, 10, 10, 10);
    p1.add(addEventButton, gbc);

    JLabel l5 = new JLabel("알림 시간 설정");
    gbc.gridx = 0;
    gbc.gridy = 10;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    gbc.insets = new Insets(5, 10, 0, 10);
    p1.add(l5, gbc);

    JPanel notificationPanel = new JPanel();
    notificationPanel.setLayout(new GridBagLayout());
    GridBagConstraints npGbc = new GridBagConstraints();
    npGbc.insets = new Insets(5, 10, 5, 10);
    npGbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel todoLabel = new JLabel("할일:");
    npGbc.gridx = 0;
    npGbc.gridy = 0;
    notificationPanel.add(todoLabel, npGbc);

    npGbc.gridx = 1;
    npGbc.gridy = 0;
    notificationPanel.add(todoComboBox, npGbc);

    JCheckBox todoCheckBox = new JCheckBox();
    npGbc.gridx = 2;
    npGbc.gridy = 0;
    notificationPanel.add(todoCheckBox, npGbc);

    JLabel eventLabel = new JLabel("이벤트:");
    npGbc.gridx = 0;
    npGbc.gridy = 1;
    notificationPanel.add(eventLabel, npGbc);

    npGbc.gridx = 1;
    npGbc.gridy = 1;
    notificationPanel.add(eventComboBox, npGbc);

    JCheckBox eventCheckBox = new JCheckBox();
    npGbc.gridx = 2;
    npGbc.gridy = 1;
    notificationPanel.add(eventCheckBox, npGbc);

    JLabel timeLabel = new JLabel("시간:");
    npGbc.gridx = 0;
    npGbc.gridy = 2;
    notificationPanel.add(timeLabel, npGbc);

    JCheckBox cb5Min = new JCheckBox("5분 전");
    JCheckBox cb10Min = new JCheckBox("10분 전");
    JCheckBox cb15Min = new JCheckBox("15분 전");
    JCheckBox cb30Min = new JCheckBox("30분 전");
    JCheckBox cb1Hour = new JCheckBox("1시간 전");
    JCheckBox cb1Day = new JCheckBox("하루 전");
    JCheckBox cb3Days = new JCheckBox("3일 전");
    JCheckBox cb1Week = new JCheckBox("일주일 전");

    JPanel checkBoxPanel = new JPanel(new GridLayout(2, 4));
    checkBoxPanel.add(cb5Min);
    checkBoxPanel.add(cb10Min);
    checkBoxPanel.add(cb15Min);
    checkBoxPanel.add(cb30Min);
    checkBoxPanel.add(cb1Hour);
    checkBoxPanel.add(cb1Day);
    checkBoxPanel.add(cb3Days);
    checkBoxPanel.add(cb1Week);
    
    npGbc.gridx = 1;
    npGbc.gridy = 2;
    npGbc.gridwidth = 2;
    notificationPanel.add(checkBoxPanel, npGbc);

    JButton saveButton = new JButton("저장");
    saveButton.addActionListener(e -> {
        saveSettings();
        JOptionPane.showMessageDialog(f, "설정이 저장되었습니다.");
    });

    npGbc.gridx = 1;
    npGbc.gridy = 3;
    npGbc.gridwidth = 2;
    npGbc.fill = GridBagConstraints.NONE;
    npGbc.anchor = GridBagConstraints.EAST;
    notificationPanel.add(saveButton, npGbc);

    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    gbc.insets = new Insets(5, 10, 10, 10);
    p1.add(notificationPanel, gbc);

    f.add(p1);
    f.setVisible(true);

    loadTasksFromDatabase(taskRoot, taskTreeModel);
    loadEventsFromDatabase(eventRoot, eventTreeModel);
    loadSettings();
    updateComboBoxes(taskTree, eventTree1);
}

// taskName을 DB의 tasks 테이블에 저장
private static void saveTask(String taskName) {  
    try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) { // DB 연결
        String sql = "INSERT INTO tasks (name) VALUES (?)";  // tasks 테이블에 새로운 행을 삽입하는 SQL문 준비 (name 열에 값 삽입)
        // 자원 해제 및 예외 처리
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { 
            stmt.setString(1, taskName);
            stmt.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// eventName을 DB의 events 테이블에 저장
private static void saveEvent(String eventName) { 
    try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) { // DB 연결
        String sql = "INSERT INTO events (name) VALUES (?)"; // events 테이블에 새로운 행을 삽입하는 SQL문 준비 (name 열에 값 삽입)
        // 자원 해제 및 예외 처리
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventName);
            stmt.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// DB의 tasks 테이블에서 작업 목록을 읽어와 트리 구조에 로드
private static void loadTasksFromDatabase(DefaultMutableTreeNode taskRoot, DefaultTreeModel taskTreeModel) {  
    try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) { // DB 연결
        String sql = "SELECT name FROM tasks";  // tasks 테이블에서 모든 작업 이름을 선택
        // 자원 해제 및 예외 처리
        try (Statement stmt = conn.createStatement();  // conn.createStatement 메서드를 호출, Statement 객체 생성
             ResultSet rs = stmt.executeQuery(sql)) { // stmt.executeQuery 메서드를 호출하여 쿼리 실행, ResultSet을 얻음  
            // 결과 집합 처리 
        	while (rs.next()) { // ResultSet 객체 'rs'를 순회하면서 각 행의 name 열 값을 읽어옴 
                String taskName = rs.getString("name");  // rs.getString("name") 메서드를 사용하여 작업 이름 가져옴
                DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(taskName);  // DefaultMutableTreeNode 객체를 생성하여 작업 이름을 노드로 추가
                taskRoot.add(taskNode);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    taskTreeModel.reload();  // 트리 모델 갱신
}

// DB의 events 테이블에서 이벤트 목록을 읽어와 트리 구조에 로드
private static void loadEventsFromDatabase(DefaultMutableTreeNode eventRoot, DefaultTreeModel eventTreeModel) {
    try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) { // DB 연결
        String sql = "SELECT name FROM events";  // events 테이블에서 모든 이벤트 이름 선택
        try (Statement stmt = conn.createStatement();  // 메서드를 호출하여 Statement 객체 생성
             ResultSet rs = stmt.executeQuery(sql)) { // conn.createStatement 메서드를 호출하여 쿼리 실행, ResultSet을 얻음
            // 결과 집합 처리
        	while (rs.next()) { 
                String eventName = rs.getString("name");
                DefaultMutableTreeNode eventNode = new DefaultMutableTreeNode(eventName);
                eventRoot.add(eventNode);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    eventTreeModel.reload();
}

private static void loadSettings() {
    // Placeholder for REST API call to load settings
}

private static void saveSettings() {
    // Placeholder for REST API call to save settings
}

private static void updateComboBoxes(JTree taskTree, JTree eventTree) {
    DefaultComboBoxModel<String> todoModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<String> eventModel = new DefaultComboBoxModel<>();

    DefaultMutableTreeNode taskRoot = (DefaultMutableTreeNode) taskTree.getModel().getRoot();
    DefaultMutableTreeNode eventRoot = (DefaultMutableTreeNode) eventTree.getModel().getRoot();

    addNodesToComboBox(taskRoot, todoModel);
    addNodesToComboBox(eventRoot, eventModel);

    todoComboBox.setModel(todoModel);
    eventComboBox.setModel(eventModel);
}

private static void addNodesToComboBox(DefaultMutableTreeNode root, DefaultComboBoxModel<String> model) {
    Enumeration<?> e = root.breadthFirstEnumeration();
    while (e.hasMoreElements()) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
        model.addElement(node.toString());
    }
}

static class TreeTransferHandler extends TransferHandler {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private boolean isTaskTree;

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;

    public TreeTransferHandler(JTree tree, DefaultTreeModel treeModel, boolean isTaskTree) {
        this.tree = tree;
        this.treeModel = treeModel;
        this.isTaskTree = isTaskTree;

        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                    ";class=\"" +
                    javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                    "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        int dropRow = tree.getRowForPath(dl.getPath());
        int selRow = tree.getRowForPath(tree.getSelectionPath());
        if (selRow == dropRow) {
            return false;
        }
        DefaultMutableTreeNode target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();
        DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) nodesToRemove[0];
        if (firstNode.isRoot()) {
            return false;
        }
        return true;
    }

    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            ArrayList<DefaultMutableTreeNode> copies =
                    new ArrayList<>();
            ArrayList<DefaultMutableTreeNode> toRemove =
                    new ArrayList<>();
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            DefaultMutableTreeNode copy = copy(node);
            copies.add(copy);
            toRemove.add(node);
            for (int i = 1; i < paths.length; i++) {
                DefaultMutableTreeNode next =
                        (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                if (next.getLevel() < node.getLevel()) {
                    break;
                } else if (next.getLevel() > node.getLevel()) {
                    copy.add(copy(next));
                } else {
                    copies.add(copy(next));
                    toRemove.add(next);
                }
            }
            DefaultMutableTreeNode[] nodes =
                    copies.toArray(new DefaultMutableTreeNode[copies.size()]);
            nodesToRemove =
                    toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            return new NodesTransferable(nodes);
        }
        return null;
    }

    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent =
                (DefaultMutableTreeNode) dest.getLastPathComponent();
        JTree tree = (JTree) support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        int index = childIndex;
        if (childIndex == -1) {
            index = parent.getChildCount();
        }
        try {
            Transferable t = support.getTransferable();
            DefaultMutableTreeNode[] nodes =
                    (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
            for (int i = 0; i < nodes.length; i++) {
                model.insertNodeInto(nodes[i], parent, index++);
            }
            return true;
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
        } catch (IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        }
        return false;
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }
        }
    }

    private DefaultMutableTreeNode copy(TreeNode node) {
        DefaultMutableTreeNode copy =
                new DefaultMutableTreeNode(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            copy.add(copy((TreeNode) node.getChildAt(i)));
        }
        return copy;
    }

    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
            	throw new UnsupportedFlavorException(flavor);
            }
            return nodes;
        }
    }
}
}
