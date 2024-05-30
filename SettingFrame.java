package TeamProject;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SettingFrame {
    public static void main(String[] args) {
        JFrame f = new JFrame("사용자 설정");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 400);
        f.setLocationRelativeTo(null);

        JPanel p1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("폴더 추가");
        gbc.gridx = 0;
        gbc.gridy = 0;
        p1.add(l1, gbc);

        JTextField text1 = new JTextField(90);
        gbc.gridx = 1;
        gbc.gridy = 0;
        p1.add(text1, gbc);

        JButton saveButton1 = new JButton("저장");
        saveButton1.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 2;
        gbc.gridy = 0;
        p1.add(saveButton1, gbc);

        JLabel l2 = new JLabel("할일 추가");
        gbc.gridx = 0;
        gbc.gridy = 1;
        p1.add(l2, gbc);

        JTextField text2 = new JTextField(90);
        gbc.gridx = 1;
        gbc.gridy = 1;
        p1.add(text2, gbc);

        JButton saveButton2 = new JButton("저장");
        saveButton2.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 2;
        gbc.gridy = 1;
        p1.add(saveButton2, gbc);

        JLabel l3 = new JLabel("할일 수정");
        gbc.gridx = 0;
        gbc.gridy = 2;
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

        // 드래그 앤 드롭 기능 추가
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());

        JScrollPane treeView = new JScrollPane(tree);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        p1.add(treeView, gbc);

        JLabel l4 = new JLabel("이벤트 목록");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 10, 0, 10); // 공백 줄이기
        p1.add(l4, gbc);

        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        
        JScrollPane eventScrollPane = new JScrollPane(eventPanel);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        p1.add(eventScrollPane, gbc);

        ArrayList<JTextField> eventButtons = new ArrayList<>();
        ArrayList<JComboBox<String>> eventCombos = new ArrayList<>();

        String[] items = {"5분 전", "10분 전", "15분 전", "30분 전", "1시간 전", "하루 전", "일주일 전"};

        addEventRow(eventPanel, eventButtons, eventCombos, "생일", items);
        addEventRow(eventPanel, eventButtons, eventCombos, "약속", items);

        JButton addButton = new JButton("+");
        addButton.addActionListener(e -> addEventRow(eventPanel, eventButtons, eventCombos, "", items));
        eventPanel.add(addButton);

        JTabbedPane tab = new JTabbedPane();
        tab.add("캘린더", new JTextArea());
        tab.add("과목별 일정", new JTextArea());
        tab.add("D-Day", new JTextArea());
        tab.add("통계", new JTextArea());
        tab.add("사용자 설정", p1);

        f.add(tab);
        f.setVisible(true);

       
        saveButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String folderName = text1.getText();
                if (!folderName.isEmpty()) {
                    new AddFolderWorker(folderName, treeModel, jcomponent).execute();
                    text1.setText(""); // 입력칸 지우기
                }
            }
        });

        saveButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String todo = text2.getText();
                if (!todo.isEmpty()) {
                    new AddTodoWorker(todo, treeModel, jcomponent).execute();
                    text2.setText(""); 
                }
            }
        });
    }

    static class AddFolderWorker extends SwingWorker<Void, Void> {
        private String folderName;
        private DefaultTreeModel treeModel;
        private DefaultMutableTreeNode root;

        public AddFolderWorker(String folderName, DefaultTreeModel treeModel, DefaultMutableTreeNode root) {
            this.folderName = folderName;
            this.treeModel = treeModel;
            this.root = root;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Thread.sleep(2000); // 예시로 2초 대기
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(folderName);
            SwingUtilities.invokeLater(() -> treeModel.insertNodeInto(newNode, root, root.getChildCount()));
            return null;
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(null, "폴더 추가 완료: " + folderName);
        }
    }

    static class AddTodoWorker extends SwingWorker<Void, Void> {
        private String todo;
        private DefaultTreeModel treeModel;
        private DefaultMutableTreeNode root;

        public AddTodoWorker(String todo, DefaultTreeModel treeModel, DefaultMutableTreeNode root) {
            this.todo = todo;
            this.treeModel = treeModel;
            this.root = root;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Thread.sleep(2000); // 예시로 2초 대기
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(todo);
            SwingUtilities.invokeLater(() -> treeModel.insertNodeInto(newNode, root, root.getChildCount()));
            return null;
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(null, "할일 추가 완료: " + todo);
        }
    }

    private static void addEventRow(JPanel panel, ArrayList<JTextField> buttons, ArrayList<JComboBox<String>> combos, String buttonText, String[] items) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField button = new JTextField(buttonText, 10);
        button.setEditable(true);
        JComboBox<String> comboBox = new JComboBox<>(items);

        comboBox.addActionListener(e -> {
            if (comboBox.getSelectedItem().equals("직접 설정")) {
                String input = JOptionPane.showInputDialog("알림 시간을 분 단위로 입력하세요:");
                try {
                    int minutes = Integer.parseInt(input);
                    if (minutes > 0) {
                        comboBox.setSelectedItem(minutes + "분 전");
                    } else {
                        JOptionPane.showMessageDialog(null, "양의 정수를 입력하세요.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "올바른 숫자를 입력하세요.");
                }
            }
        });

        row.add(button);
        row.add(comboBox);
        buttons.add(button);
        combos.add(comboBox);

        panel.add(row, panel.getComponentCount() - 1);
        panel.revalidate();
        panel.repaint();
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



