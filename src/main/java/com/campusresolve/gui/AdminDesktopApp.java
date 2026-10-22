package com.campusresolve.gui;

import com.campusresolve.dao.ComplaintDAO;
import com.campusresolve.model.Complaint;
import com.campusresolve.model.Priority;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Optional desktop operations console demonstrating Swing, AWT layouts and event handling. */
public final class AdminDesktopApp extends JFrame {
    private final ComplaintDAO complaints = new ComplaintDAO();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Issue", "Category", "Priority", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public AdminDesktopApp() {
        super("Campus Resolve | Operations Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 520));
        setLayout(new BorderLayout(16, 16));
        getContentPane().setBackground(new Color(246, 248, 252));
        add(header(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions(), BorderLayout.SOUTH);
        refresh();
        setLocationByPlatform(true);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 4, 22));
        panel.setBackground(new Color(246, 248, 252));
        JLabel title = new JLabel("Campus Resolve  /  Operations");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, BorderLayout.WEST);
        JLabel hint = new JLabel("Complaint command center");
        hint.setForeground(new Color(96, 110, 140));
        panel.add(hint, BorderLayout.EAST);
        return panel;
    }

    private JPanel actions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton refresh = new JButton("Refresh queue");
        JButton resolve = new JButton("Mark selected resolved");
        refresh.addActionListener(event -> refresh());
        resolve.addActionListener(event -> markResolved());
        panel.add(refresh); panel.add(resolve);
        return panel;
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<Complaint> rows = complaints.findAll();
            for (Complaint c : rows) model.addRow(new Object[]{c.getId(), c.getTitle(), c.getCategory(), c.getPriority(), c.getStatus()});
        } catch (RuntimeException error) {
            JOptionPane.showMessageDialog(this, error.getMessage(), "Database unavailable", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markResolved() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a complaint first."); return; }
        JOptionPane.showMessageDialog(this, "Status changes are performed through the authenticated admin workflow.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDesktopApp().setVisible(true));
    }
}
