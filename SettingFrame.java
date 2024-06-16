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

public class SettingFrame{
	private static final String JDBC_DRIVERCLASSNAME = "oracle.jdbc.driver.OracleDriver";
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
            JOptionPane.showMessageDialog(f, "알림 설정이 저장되었습니다.");
            
            cb5Min.setSelected(false);
            cb10Min.setSelected(false);
            cb15Min.setSelected(false);
            cb30Min.setSelected(false);
            cb1Hour.setSelected(false);
            cb1Day.setSelected(false);
            cb3Days.setSelected(false);
            cb1Week.setSelected(false);
        });
        
        npGbc.gridx = 0;
        npGbc.gridy = 3;
        npGbc.gridwidth = 3;
        notificationPanel.add(saveButton, npGbc);

        JLabel colorLabel = new JLabel("과목별 색상 설정:");
        npGbc.gridx = 0;
        npGbc.gridy = 4;
        npGbc.gridwidth = 3;
        notificationPanel.add(colorLabel, npGbc);

        JButton setColorButton = new JButton("색상 설정");
        setColorButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedSubject != null) {
                Color newColor = JColorChooser.showDialog(f, "색상 선택", Color.WHITE);
                if (newColor != null) {
                    saveSubjectColor(selectedSubject, newColor);
                    JOptionPane.showMessageDialog(f, "색상이 저장되었습니다.");
                }
            }
        });
        
        npGbc.gridx = 0;
        npGbc.gridy = 5;
        npGbc.gridwidth = 3;
        notificationPanel.add(setColorButton, npGbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        p1.add(notificationPanel, gbc);

        f.add(p1);
        f.setVisible(true);

        updateComboBoxes(subjectTree, eventTree1);
    }

    private static void saveSubject(String subjectName) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO USERSETTINGTASKS (TASK_NAME) VALUES (?)")) {
            pstmt.setString(1, subjectName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveEvent(String eventName) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO USERSETTINGEVENTS (EVENT) VALUES (?)")) {
            pstmt.setString(1, eventName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveNotificationSettings(String subject, String event, ArrayList<String> times) {
        StringBuilder timeString = new StringBuilder();
        for (String time : times) {
            if (timeString.length() > 0) timeString.append(",");
            timeString.append(time);
        }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO USERSETTINGTASKS, USERSETTINGEVENTS (TASK_NAME, EVENT, ALERT_TIME) VALUES (?, ?, ?)")) {
            pstmt.setString(1, subject);
            pstmt.setString(2, event);
            pstmt.setString(3, timeString.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveSubjectColor(String subject, Color color) {
        String colorString = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE USERSETTINGTASKS SET COLOR = ? WHERE TASK_NAME = ?")) {
            pstmt.setString(1, colorString);
            pstmt.setString(2, subject);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateComboBoxes(JTree subjectTree, JTree eventTree) {
        subjectComboBox.removeAllItems();
        eventComboBox.removeAllItems();

        DefaultMutableTreeNode subjectRoot = (DefaultMutableTreeNode) subjectTree.getModel().getRoot();
        Enumeration<?> subjects = subjectRoot.children();
        while (subjects.hasMoreElements()) {
            DefaultMutableTreeNode subject = (DefaultMutableTreeNode) subjects.nextElement();
            subjectComboBox.addItem(subject.toString());
        }

        DefaultMutableTreeNode eventRoot = (DefaultMutableTreeNode) eventTree.getModel().getRoot();
        Enumeration<?> events = eventRoot.children();
        while (events.hasMoreElements()) {
            DefaultMutableTreeNode event = (DefaultMutableTreeNode) events.nextElement();
            eventComboBox.addItem(event.toString());
        }
    }

    static class TreeTransferHandler extends TransferHandler {
        private final JTree tree;
        private final DefaultTreeModel treeModel;
        private final boolean isSubject;

        public TreeTransferHandler(JTree tree, DefaultTreeModel treeModel, boolean isSubject) {
            this.tree = tree;
            this.treeModel = treeModel;
            this.isSubject = isSubject;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDrop();
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            int dropRow = dl.getChildIndex();
            TreePath path = dl.getPath();
            DefaultMutableTreeNode newParentNode = (DefaultMutableTreeNode) path.getLastPathComponent();

            try {
                Transferable t = support.getTransferable();
                Object data = t.getTransferData(DataFlavor.stringFlavor);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(data);
                if (dropRow == -1) {
                    treeModel.insertNodeInto(newNode, newParentNode, newParentNode.getChildCount());
                } else {
                    treeModel.insertNodeInto(newNode, newParentNode, dropRow);
                }
                return true;
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath path = tree.getSelectionPath();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            return new StringSelection(selectedNode.toString());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
    }
}
