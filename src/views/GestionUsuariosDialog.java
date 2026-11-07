package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import posglagerman.ConexionDB;
import posglagerman.Sesion;

public class GestionUsuariosDialog extends JDialog {
    private JTable tablaUsuarios;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cmbRol;

    public GestionUsuariosDialog(Frame parent) {
        super(parent, "Gestión de Usuarios", true);
        setSize(650, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(250, 250, 250));

        // 1. Tabla Central (Arriba)
        tablaUsuarios = new JTable();
        tablaUsuarios.setModel(new DefaultTableModel(new Object[]{"ID", "Usuario", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        });
        tablaUsuarios.setRowHeight(25);
        tablaUsuarios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tablaUsuarios.getTableHeader().setBackground(new Color(168, 197, 227));
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // 2. Panel Sur (Formulario y Botones)
        JPanel panelSur = new JPanel(new GridBagLayout());
        panelSur.setBackground(new Color(230, 235, 240));
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelSur.add(new JLabel("Usuario:"), gbc);
        
        txtUser = new JTextField(12);
        gbc.gridx = 1;
        panelSur.add(txtUser, gbc);

        gbc.gridx = 2;
        panelSur.add(new JLabel("Contraseña:"), gbc);
        
        txtPass = new JPasswordField(12);
        gbc.gridx = 3;
        panelSur.add(txtPass, gbc);

        gbc.gridx = 4;
        panelSur.add(new JLabel("Rol:"), gbc);
        
        cmbRol = new JComboBox<>(new String[]{"VENDEDOR", "ADMIN"});
        gbc.gridx = 5;
        panelSur.add(cmbRol, gbc);

        // Botones
        JButton btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> agregarUsuario());
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        panelSur.add(btnAgregar, gbc);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(244, 168, 168));
        btnEliminar.addActionListener(e -> eliminarUsuario());
        gbc.gridx = 3; gbc.gridwidth = 3;
        panelSur.add(btnEliminar, gbc);

        add(panelSur, BorderLayout.SOUTH);

        // Cargar datos al iniciar
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        DefaultTableModel model = (DefaultTableModel) tablaUsuarios.getModel();
        model.setRowCount(0);
        try (Connection con = ConexionDB.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_usuario, username, rol FROM Usuario ORDER BY id_usuario")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id_usuario"), rs.getString("username"), rs.getString("rol")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarUsuario() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        String r = cmbRol.getSelectedItem().toString();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement("INSERT INTO Usuario (username, password, rol) VALUES (?, ?, ?)")) {
            ps.setString(1, u);
            ps.setString(2, p);
            ps.setString(3, r);
            ps.executeUpdate();
            
            txtUser.setText("");
            txtPass.setText("");
            cargarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario agregado exitosamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tablaUsuarios.getValueAt(fila, 0);
        String nombre = (String) tablaUsuarios.getValueAt(fila, 1);

        // Evitar que el usuario se borre a sí mismo
        if (id == Sesion.idUsuario) {
            JOptionPane.showMessageDialog(this, "No puedes eliminar tu propia cuenta mientras estás conectado.", "Acción Denegada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar al usuario " + nombre + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = ConexionDB.getConexion();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                cargarUsuarios();
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}