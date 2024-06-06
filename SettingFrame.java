package TeamProject;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;

public class SettingFrame {
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

        DefaultMutableTreeNode jtextcomponent1 = new DefaultMutableTreeNode("객체지향프로그래밍");
        DefaultMutableTreeNode jtextcomponent1_1 = new DefaultMutableTreeNode("HW#1");

        DefaultMutableTreeNode jtextcomponent2 = new DefaultMutableTreeNode("데이터구조");
        DefaultMutableTreeNode jtextcomponent2_1 = new DefaultMutableTreeNode("Term Project");

        DefaultMutableTreeNode jtextcomponent3 = new DefaultMutableTreeNode("토익");
        DefaultMutableTreeNode jtextcomponent3_1 = new DefaultMutableTreeNode("단어 외우기");

        jcomponent.add(jtextcomponent1);
        jcomponent.add(jtextcomponent2);
        jcomponent.add(jtextcomponent3);
        jtextcomponent1.add(jtextcomponent1_1);
        jtextcomponent2.add(jtextcomponent2_1);
        jtextcomponent3.add(jtextcomponent3_1);

        DefaultTreeModel treeModel = new DefaultTreeModel(jcomponent);
        JTree tree = new JTree(treeModel);
        tree.setEditable(true);

        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());

        JScrollPane treeView = new JScrollPane(tree);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        p1.add(treeView, gbc);

        JLabel l4 = new JLabel("이벤트 목록");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 10, 0, 10);
        p1.add(l4, gbc);

        DefaultMutableTreeNode eventRoot = new DefaultMutableTreeNode("이벤트 목록");
        DefaultMutableTreeNode birthdayNode = new DefaultMutableTreeNode("생일");
        birthdayNode.add(new DefaultMutableTreeNode("엄마"));
        birthdayNode.add(new DefaultMutableTreeNode("아빠"));

        DefaultMutableTreeNode meetingNode = new DefaultMutableTreeNode("약속");
        meetingNode.add(new DefaultMutableTreeNode("삼식"));
        meetingNode.add(new DefaultMutableTreeNode("점순"));

        eventRoot.add(birthdayNode);
        eventRoot.add(meetingNode);

        DefaultTreeModel eventTreeModel = new DefaultTreeModel(eventRoot);
        JTree eventTree = new JTree(eventTreeModel);
        eventTree.setEditable(true);

        JScrollPane eventTreeView = new JScrollPane(eventTree);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        p1.add(eventTreeView, gbc);

        JLabel l5 = new JLabel("알림 시간 설정");
        gbc.gridx = 0;
        gbc.gridy = 8;
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
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p1.add(notificationPanel, gbc);

        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> {
            String selectedEvent = (String) eventComboBox.getSelectedItem();
            String selectedTodo = (String) todoComboBox.getSelectedItem();

            StringBuilder sb = new StringBuilder();
            sb.append("저장된 알림 설정:\n");
            if (todoCheckBox.isSelected() && selectedTodo != null) {
                sb.append("할일: ").append(selectedTodo).append("\n");
            }
            if (eventCheckBox.isSelected() && selectedEvent != null) {
                sb.append("이벤트: ").append(selectedEvent).append("\n");
            }
            sb.append("알림 시간: ");
            if (cb5Min.isSelected()) sb.append("5분 전 ");
            if (cb10Min.isSelected()) sb.append("10분 전 ");
            if (cb15Min.isSelected()) sb.append("15분 전 ");
            if (cb30Min.isSelected()) sb.append("30분 전 ");
            if (cb1Hour.isSelected()) sb.append("1시간 전 ");
            if (cb1Day.isSelected()) sb.append("하루 전 ");
            if (cb1Week.isSelected()) sb.append("일주일 전 ");
            if (cb3Days.isSelected()) sb.append("3일 전 ");

            JOptionPane.showMessageDialog(null, sb.toString());

            // 체크박스 초기화
            cb5Min.setSelected(false);
            cb10Min.setSelected(false);
            cb15Min.setSelected(false);
            cb30Min.setSelected(false);
            cb1Hour.setSelected(false);
            cb1Day.setSelected(false);
            cb1Week.setSelected(false);
            cb3Days.setSelected(false);

            todoCheckBox.setSelected(false);
            eventCheckBox.setSelected(false);
        });
        
        npGbc.gridx = 0;
        npGbc.gridy = 3;
        npGbc.gridwidth = 3;
        npGbc.gridheight = 1;
        notificationPanel.add(saveButton, npGbc);

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.add(p1, BorderLayout.CENTER);

        f.add(tabPanel);
        f.setVisible(true);

        updateComboBoxes(tree, eventTree, eventComboBox, todoComboBox);
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
                updateComboBoxes(tree, eventTree, eventComboBox, todoComboBox);
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                updateComboBoxes(tree, eventTree, eventComboBox, todoComboBox);
            }
        });
        eventTree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                updateComboBoxes(tree, eventTree, eventComboBox, todoComboBox);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                updateComboBoxes(tree, eventTree, eventComboBox, todoComboBox);
            }
        });
    }

    private static void updateComboBoxes(JTree tree, JTree eventTree, JComboBox<String> eventComboBox, JComboBox<String> todoComboBox) {
        eventComboBox.removeAllItems();
        todoComboBox.removeAllItems();

        DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) tree.getModel().getRoot();
        DefaultMutableTreeNode root2 = (DefaultMutableTreeNode) eventTree.getModel().getRoot();

        addLeafNodesToComboBox(root1, todoComboBox);
        addLeafNodesToComboBox(root2, eventComboBox);
    }

    private static void addLeafNodesToComboBox(DefaultMutableTreeNode node, JComboBox<String> comboBox) {
        if (node.isLeaf()) {
            comboBox.addItem(node.toString());
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                addLeafNodesToComboBox((DefaultMutableTreeNode) node.getChildAt(i), comboBox);
            }
        }
    }

    static class TreeTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            return new NodesTransferable(tree.getSelectionPaths());
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE) {
                JTree tree = (JTree) source;
                TreePath[] paths = null;
                try {
                    paths = (TreePath[]) data.getTransferData(NodesTransferable.nodesFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paths != null) {
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    for (TreePath path : paths) {
                        model.removeNodeFromParent((MutableTreeNode) path.getLastPathComponent());
                    }
                }
            }
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDrop() || !support.isDataFlavorSupported(NodesTransferable.nodesFlavor)) {
                return false;
            }
            support.setShowDropLocation(true);
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            JTree tree = (JTree) support.getComponent();
            int dropRow = tree.getRowForPath(dl.getPath());
            int[] selRows = tree.getSelectionRows();
            for (int selRow : selRows) {
                if (selRow == dropRow) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            Transferable t = support.getTransferable();
            TreePath[] paths = null;
            try {
                paths = (TreePath[]) t.getTransferData(NodesTransferable.nodesFlavor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            int childIndex = dl.getChildIndex();
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getLastPathComponent();
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            int index = childIndex == -1 ? parent.getChildCount() : childIndex;
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                model.insertNodeInto(node, parent, index++);
            }
            return true;
        }

        private static class NodesTransferable implements Transferable {
            static DataFlavor nodesFlavor = new DataFlavor(TreePath[].class, "TreePath array");
            private TreePath[] paths;

            public NodesTransferable(TreePath[] paths) {
                this.paths = paths;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{nodesFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(nodesFlavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return paths;
            }
        }
    }
}



