package TeamProject;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SettingFrameConnect {
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String JDBC_USER = "system";
    private static final String JDBC_PASSWORD = "databaseoracle";

    public static void main(String[] args) {
        JFrame f = new JFrame("사용자 설정");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 600);
        f.setLocationRelativeTo(null);

        JPanel p1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l3 = new JLabel("할일 수정");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        p1.add(l3, gbc);

        DefaultMutableTreeNode jcomponent = new DefaultMutableTreeNode("과목");
        DefaultTreeModel treeModel = new DefaultTreeModel(jcomponent);
        JTree tree = new JTree(treeModel);
        tree.setEditable(true);

        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        tree.setSelectionPath(path);
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        String nodeName = JOptionPane.showInputDialog(f, "새로운 하위 항목 이름:");
                        if (nodeName != null && !nodeName.trim().isEmpty()) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
                            selectedNode.add(newNode);
                            treeModel.reload();
                            updateComboBoxes((JComboBox<String>) null, tree);
                        }
                    }
                }
            }
        });

        JScrollPane treeView = new JScrollPane(tree);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        p1.add(treeView, gbc);

        JButton addSubjectButton = new JButton("과목 추가");
        addSubjectButton.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog(f, "새로운 과목 이름:");
            if (subjectName != null && !subjectName.trim().isEmpty()) {
                DefaultMutableTreeNode newSubject = new DefaultMutableTreeNode(subjectName);
                jcomponent.add(newSubject);
                treeModel.reload();
                saveToDatabase(subjectName, "과목");
                updateComboBoxes((JComboBox<String>) null, tree);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
        p1.add(addSubjectButton, gbc);

        JLabel l4 = new JLabel("이벤트 목록");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
        p1.add(l4, gbc);

        DefaultMutableTreeNode eventRoot = new DefaultMutableTreeNode("이벤트 목록");
        DefaultTreeModel eventTreeModel = new DefaultTreeModel(eventRoot);
        JTree eventTree = new JTree(eventTreeModel);
        eventTree.setEditable(true);

        eventTree.setDragEnabled(true);
        eventTree.setDropMode(DropMode.ON_OR_INSERT);
        eventTree.setTransferHandler(new TreeTransferHandler());

        eventTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = eventTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        eventTree.setSelectionPath(path);
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        String nodeName = JOptionPane.showInputDialog(f, "새로운 하위 항목 이름:");
                        if (nodeName != null && !nodeName.trim().isEmpty()) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
                            selectedNode.add(newNode);
                            eventTreeModel.reload();
                            updateComboBoxes((JComboBox<String>) null, eventTree);
                        }
                    }
                }
            }
        });

        JScrollPane eventTreeView = new JScrollPane(eventTree);
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
                saveToDatabase(eventName, "이벤트");
                updateComboBoxes((JComboBox<String>) null, eventTree);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
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

        JComboBox<String> todoComboBox = new JComboBox<>();
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

        JComboBox<String> eventComboBox = new JComboBox<>();
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
        JCheckBox cb1Week = new JCheckBox("일주일 전");
        JCheckBox cb3Days = new JCheckBox("3일 전");

        JPanel checkBoxPanel = new JPanel(new GridLayout(2, 4));
        checkBoxPanel.add(cb5Min);
        checkBoxPanel.add(cb10Min);
        checkBoxPanel.add(cb15Min);
        checkBoxPanel.add(cb30Min);
        checkBoxPanel.add(cb1Hour);
        checkBoxPanel.add(cb1Day);
        checkBoxPanel.add(cb1Week);
        checkBoxPanel.add(cb3Days);

        npGbc.gridx = 1;
        npGbc.gridy = 2;
        npGbc.gridwidth = 2;
        notificationPanel.add(checkBoxPanel, npGbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        p1.add(notificationPanel, gbc);

        updateComboBoxes(todoComboBox, tree);
        updateComboBoxes(eventComboBox, eventTree);

        f.add(p1);
        f.setVisible(true);
    }

    private static void saveToDatabase(String itemName, String type) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO settings (item_name, item_type) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, itemName);
                stmt.setString(2, type);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateComboBoxes(JComboBox<String> comboBox, JTree tree) {
        if (comboBox == null) return;
        comboBox.removeAllItems();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        addLeafNodesToComboBox(comboBox, root);
    }

    private static void addLeafNodesToComboBox(JComboBox<String> comboBox, DefaultMutableTreeNode node) {
        if (node.isLeaf()) {
            comboBox.addItem(node.toString());
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                addLeafNodesToComboBox(comboBox, (DefaultMutableTreeNode) node.getChildAt(i));
            }
        }
    }

    private static class TreeTransferHandler extends TransferHandler {
        private DataFlavor nodesFlavor;
        private DataFlavor[] flavors = new DataFlavor[1];
        private DefaultMutableTreeNode[] nodesToRemove;

        public TreeTransferHandler() {
            try {
                String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                        + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
                nodesFlavor = new DataFlavor(mimeType);
                flavors[0] = nodesFlavor;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            return support.isDataFlavorSupported(nodesFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            TreePath path = dl.getPath();
            DefaultMutableTreeNode newParent = (DefaultMutableTreeNode) path.getLastPathComponent();
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

            Transferable t = support.getTransferable();
            DefaultMutableTreeNode[] nodes = null;
            try {
                nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
            } catch (UnsupportedFlavorException | java.io.IOException e) {
                e.printStackTrace();
            }

            int childIndex = dl.getChildIndex();
            int index = childIndex == -1 ? newParent.getChildCount() : childIndex;

            for (DefaultMutableTreeNode node : nodes) {
                model.insertNodeInto(node, newParent, index++);
            }

            return true;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                List<DefaultMutableTreeNode> copies = new ArrayList<>();
                List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
                for (TreePath path : paths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    copies.add(copy(node));
                    toRemove.add(node);
                }
                DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[0]);
                nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[0]);
                return new NodesTransferable(nodes);
            }
            return null;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action != MOVE || nodesToRemove == null) {
                return;
            }

            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (DefaultMutableTreeNode node : nodesToRemove) {
                model.removeNodeFromParent(node);
            }
            nodesToRemove = null;
        }

        private DefaultMutableTreeNode copy(DefaultMutableTreeNode node) {
            DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node.getUserObject());
            for (int i = 0; i < node.getChildCount(); i++) {
                copy.add(copy((DefaultMutableTreeNode) node.getChildAt(i)));
            }
            return copy;
        }

        public class NodesTransferable implements Transferable {
            DefaultMutableTreeNode[] nodes;

            public NodesTransferable(DefaultMutableTreeNode[] nodes) {
                this.nodes = nodes;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return nodes;
            }
        }
    }
}




