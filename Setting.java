package TeamProject;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.Position;
import javax.swing.tree.*;

public class Setting {
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String JDBC_USER = "system";
    private static final String JDBC_PASSWORD = "databaseoracle";

    private static JComboBox<String> subjectComboBox = new JComboBox<>();
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

        DefaultMutableTreeNode subjectRoot = new DefaultMutableTreeNode("과목");
        DefaultTreeModel subjectTreeModel = new DefaultTreeModel(subjectRoot);
        JTree subjectTree = new JTree(subjectTreeModel);
        subjectTree.setEditable(true);
        subjectTree.setDragEnabled(true);
        subjectTree.setDropMode(DropMode.ON_OR_INSERT);
        subjectTree.setTransferHandler(new TreeTransferHandler(subjectTree, subjectTreeModel, true));

        JTree eventTree = null;
        subjectTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = subjectTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        subjectTree.setSelectionPath(path);
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        String nodeName = JOptionPane.showInputDialog(f, "새로운 하위 항목 이름:");
                        if (nodeName != null && !nodeName.trim().isEmpty()) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
                            selectedNode.add(newNode);
                            subjectTreeModel.reload();
                            saveSubTask(selectedNode.toString(), nodeName);
                            updateComboBoxes(subjectTree, eventTree);
                        }
                    }
                }
            }
        });

        JScrollPane subjectTreeView = new JScrollPane(subjectTree);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        p1.add(subjectTreeView, gbc);

        JButton addSubjectButton = new JButton("과목 추가");
        addSubjectButton.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog(f, "새로운 과목 이름:");
            if (subjectName != null && !subjectName.trim().isEmpty()) {
                DefaultMutableTreeNode newSubject = new DefaultMutableTreeNode(subjectName);
                subjectRoot.add(newSubject);
                subjectTreeModel.reload();
                saveSubject(subjectName);
                updateComboBoxes(subjectTree, eventTree);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
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
                            saveSubEvent(selectedNode.toString(), nodeName);
                            updateComboBoxes(subjectTree, eventTree1);
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
                updateComboBoxes(subjectTree, eventTree1);
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

        JLabel subjectLabel = new JLabel("과목:");
        npGbc.gridx = 0;
        npGbc.gridy = 0;
        notificationPanel.add(subjectLabel, npGbc);

        npGbc.gridx = 1;
        npGbc.gridy = 0;
        notificationPanel.add(subjectComboBox, npGbc);

        JCheckBox subjectCheckBox = new JCheckBox();
        npGbc.gridx = 2;
        npGbc.gridy = 0;
        notificationPanel.add(subjectCheckBox, npGbc);

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
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            String selectedEvent = (String) eventComboBox.getSelectedItem();
            ArrayList<String> notificationTimes = new ArrayList<>();

            if (cb5Min.isSelected()) notificationTimes.add("5분 전");
            if (cb10Min.isSelected()) notificationTimes.add("10분 전");
            if (cb15Min.isSelected()) notificationTimes.add("15분 전");
            if (cb30Min.isSelected()) notificationTimes.add("30분 전");
            if (cb1Hour.isSelected()) notificationTimes.add("1시간 전");
            if (cb1Day.isSelected()) notificationTimes.add("하루 전");
            if (cb3Days.isSelected()) notificationTimes.add("3일 전");
            if (cb1Week.isSelected()) notificationTimes.add("일주일 전");

            saveNotificationSettings(selectedSubject, selectedEvent, notificationTimes);
        });

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 10, 10, 10);
        p1.add(notificationPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        p1.add(saveButton, gbc);

        f.getContentPane().add(p1);
        f.setVisible(true);
    }

    private static void updateComboBoxes(JTree subjectTree, JTree eventTree) {
        subjectComboBox.removeAllItems();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) subjectTree.getModel().getRoot();
        Enumeration<?> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) {
                subjectComboBox.addItem(node.toString());
            }
        }

        eventComboBox.removeAllItems();
        root = (DefaultMutableTreeNode) eventTree.getModel().getRoot();
        e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) {
                eventComboBox.addItem(node.toString());
            }
        }
    }

    private static void saveSubject(String subjectName) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO subjects (name) VALUES (?)")) {
            ps.setString(1, subjectName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveSubTask(String parentTask, String subTaskName) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO subtasks (parent_task, name) VALUES (?, ?)")) {
            ps.setString(1, parentTask);
            ps.setString(2, subTaskName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveEvent(String eventName) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO events (name) VALUES (?)")) {
            ps.setString(1, eventName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveSubEvent(String parentEvent, String subEventName) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO subevents (parent_event, name) VALUES (?, ?)")) {
            ps.setString(1, parentEvent);
            ps.setString(2, subEventName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveNotificationSettings(String subject, String event, ArrayList<String> times) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            for (String time : times) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO notifications (subject, event, time) VALUES (?, ?, ?)")) {
                    ps.setString(1, subject);
                    ps.setString(2, event);
                    ps.setString(3, time);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class TreeTransferHandler extends TransferHandler {
    private JTree tree;
    private DefaultTreeModel model;
    private boolean isSubjectTree;

    public TreeTransferHandler(JTree tree, DefaultTreeModel model, boolean isSubjectTree) {
        this.tree = tree;
        this.model = model;
        this.isSubjectTree = isSubjectTree;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath path = dl.getPath();
        return path != null;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        Transferable transferable = support.getTransferable();
        String transferData;
        try {
            transferData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath path = dl.getPath();
        DefaultMutableTreeNode newParentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(transferData);
        model.insertNodeInto(newNode, newParentNode, newParentNode.getChildCount());

        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            StringBuilder sb = new StringBuilder();
            for (TreePath path : paths) {
                sb.append(path.getLastPathComponent().toString());
                sb.append("\n");
            }
            return new StringSelection(sb.toString());
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
}
