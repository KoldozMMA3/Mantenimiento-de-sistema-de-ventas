// prueba de commit
package views;
import views.administrar_inventario.AdminInvPage;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
public class InicioPage extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(InicioPage.class.getName());

    /**
     * Creates new form Inicio
     */
public InicioPage() {
        initComponents();
        setTitle("Verdulería Glagerman - Sistema de Gestión");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // 1. Panel de Bienvenida (Inicio)
        panInicio.setLayout(new java.awt.GridBagLayout());
        javax.swing.JPanel panelBienvenida = new javax.swing.JPanel();
        panelBienvenida.setBackground(new java.awt.Color(250, 250, 250));
        panelBienvenida.setLayout(new javax.swing.BoxLayout(panelBienvenida, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.JLabel lblTitulo = new javax.swing.JLabel("Sistema de Gestión - Verdulería Glagerman");
        lblTitulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 26));
        lblTitulo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        javax.swing.JLabel lblSubtitulo = new javax.swing.JLabel("Panel de Control Principal");
        lblSubtitulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18));
        lblSubtitulo.setForeground(new java.awt.Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        javax.swing.JLabel lblDesc = new javax.swing.JLabel("<html><div style='text-align: center; width: 500px; margin-top: 20px; font-size: 14px; color: #555;'>Utiliza la barra de menú superior para navegar de forma rápida entre los módulos. El sistema se adaptará automáticamente al tamaño de tu pantalla.</div></html>");
        lblDesc.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        panelBienvenida.add(lblTitulo);
        panelBienvenida.add(javax.swing.Box.createVerticalStrut(10));
        panelBienvenida.add(lblSubtitulo);
        panelBienvenida.add(javax.swing.Box.createVerticalStrut(20));
        panelBienvenida.add(lblDesc);

        panInicio.removeAll();
        panInicio.add(panelBienvenida, new java.awt.GridBagConstraints());

        // 2. Contenedor Dinámico Central
        java.awt.CardLayout cardLayout = new java.awt.CardLayout();
        javax.swing.JPanel panelContenedor = new javax.swing.JPanel(cardLayout);

        panelContenedor.add(panInicio, "INICIO");
        panelContenedor.add(envolverPanelAdaptativo(new VentasPage().getContentPane()), "VENTAS");
        panelContenedor.add(envolverPanelAdaptativo(new CompraAProveedoresPage().getContentPane()), "COMPRAS");
        panelContenedor.add(envolverPanelAdaptativo(new AdminProPage().getContentPane()), "PROVEEDORES");
        panelContenedor.add(envolverPanelAdaptativo(new views.administrar_inventario.AdminInvPage().getContentPane()), "INVENTARIO");
        panelContenedor.add(envolverPanelAdaptativo(new ReportesPage().getContentPane()), "REPORTES");

        // 3. Barra Superior (Navegación + Reloj + Salir)
        javax.swing.JPanel topBar = new javax.swing.JPanel(new java.awt.BorderLayout());
        topBar.setBackground(new java.awt.Color(230, 235, 240));

        // Menú Izquierdo (Módulos)
        javax.swing.JPanel leftMenu = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        leftMenu.setOpaque(false);

        javax.swing.JButton btnInicio = crearBotonMenu("🏠 Inicio");
        javax.swing.JButton btnVentas = crearBotonMenu("🛒 Ventas");
        javax.swing.JButton btnCompras = crearBotonMenu("📦 Compras");
        javax.swing.JButton btnProveedores = crearBotonMenu("🤝 Proveedores");
        javax.swing.JButton btnInventario = crearBotonMenu("📋 Inventario");
        javax.swing.JButton btnReportes = crearBotonMenu("📊 Reportes");

        btnInicio.addActionListener(e -> cardLayout.show(panelContenedor, "INICIO"));
        btnVentas.addActionListener(e -> cardLayout.show(panelContenedor, "VENTAS"));
        btnCompras.addActionListener(e -> cardLayout.show(panelContenedor, "COMPRAS"));
        btnProveedores.addActionListener(e -> cardLayout.show(panelContenedor, "PROVEEDORES"));
        btnInventario.addActionListener(e -> cardLayout.show(panelContenedor, "INVENTARIO"));
        btnReportes.addActionListener(e -> cardLayout.show(panelContenedor, "REPORTES"));

        leftMenu.add(btnInicio); leftMenu.add(btnVentas); leftMenu.add(btnCompras);
        leftMenu.add(btnProveedores); leftMenu.add(btnInventario); leftMenu.add(btnReportes);

        // Menú Derecho (Reloj Dinámico y Botón Salir)
        javax.swing.JPanel rightMenu = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 10));
        rightMenu.setOpaque(false);

        javax.swing.JLabel lblReloj = new javax.swing.JLabel();
        lblReloj.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
        lblReloj.setForeground(new java.awt.Color(80, 80, 80));
        // Reloj en tiempo real
        new javax.swing.Timer(1000, e -> lblReloj.setText(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()))).start();
        lblReloj.setText(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())); // Mostrar al instante

        javax.swing.JButton btnSalirMaestro = new javax.swing.JButton("❌ Salir del Sistema");
        btnSalirMaestro.setBackground(new java.awt.Color(244, 168, 168));
        btnSalirMaestro.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        btnSalirMaestro.setFocusPainted(false);
        btnSalirMaestro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalirMaestro.addActionListener(e -> System.exit(0));

        rightMenu.add(lblReloj);
        rightMenu.add(btnSalirMaestro);

        topBar.add(leftMenu, java.awt.BorderLayout.WEST);
        topBar.add(rightMenu, java.awt.BorderLayout.EAST);

        // 4. Ensamblar Ventana
        this.getContentPane().setLayout(new java.awt.BorderLayout());
        this.getContentPane().add(topBar, java.awt.BorderLayout.NORTH);
        this.getContentPane().add(panelContenedor, java.awt.BorderLayout.CENTER);
    }

    // --- MÉTODOS AUXILIARES INTELIGENTES ---

    private javax.swing.JButton crearBotonMenu(String texto) {
        javax.swing.JButton boton = new javax.swing.JButton(texto);
        boton.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        boton.setBackground(new java.awt.Color(168, 197, 227));
        boton.setFocusPainted(false);
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return boton;
    }

    // Adaptador mágico MEJORADO (Ahora entra en cualquier capa de NetBeans)
    private javax.swing.JPanel envolverPanelAdaptativo(java.awt.Container contenidoOriginal) {
        if (contenidoOriginal instanceof javax.swing.JFrame) {
            contenidoOriginal = ((javax.swing.JFrame) contenidoOriginal).getContentPane();
        }
        
        ocultarBotonesNavegacion(contenidoOriginal);
        mejorarTablasUniversales(contenidoOriginal);

        // Buscar la tabla profundamente en el formulario
        java.awt.Component expansible = encontrarExpansible(contenidoOriginal);

        if (expansible != null && expansible.getParent() instanceof javax.swing.JPanel) {
            javax.swing.JPanel panelBase = (javax.swing.JPanel) expansible.getParent();
            panelBase.setSize(panelBase.getPreferredSize());
            panelBase.doLayout();
            
            int midY = expansible.getY();
            int midBottom = expansible.getY() + expansible.getHeight();
            
            javax.swing.JPanel pNorte = new javax.swing.JPanel(null);
            pNorte.setPreferredSize(new java.awt.Dimension(panelBase.getWidth(), midY));
            pNorte.setBackground(panelBase.getBackground());
            
            javax.swing.JPanel pSur = new javax.swing.JPanel(null);
            pSur.setPreferredSize(new java.awt.Dimension(panelBase.getWidth(), panelBase.getHeight() - midBottom));
            pSur.setBackground(panelBase.getBackground());
            
            for (java.awt.Component c : panelBase.getComponents()) {
                if (c == expansible) continue;
                if (c.getY() < midY) {
                    pNorte.add(c);
                } else {
                    c.setLocation(c.getX(), c.getY() - midBottom);
                    pSur.add(c);
                }
            }
            
            javax.swing.JPanel wrapNorte = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
            wrapNorte.setBackground(panelBase.getBackground());
            wrapNorte.add(pNorte);
            
            javax.swing.JPanel wrapSur = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
            wrapSur.setBackground(panelBase.getBackground());
            wrapSur.add(pSur);

            javax.swing.JPanel nuevoContenedor = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
            nuevoContenedor.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
            nuevoContenedor.setBackground(panelBase.getBackground());
            nuevoContenedor.add(wrapNorte, java.awt.BorderLayout.NORTH);
            nuevoContenedor.add(expansible, java.awt.BorderLayout.CENTER);
            nuevoContenedor.add(wrapSur, java.awt.BorderLayout.SOUTH);
            
            return nuevoContenedor;
        }

        javax.swing.JPanel fallback = new javax.swing.JPanel(new java.awt.BorderLayout());
        fallback.add(contenidoOriginal, java.awt.BorderLayout.CENTER);
        return fallback;
    }

    // Buscador recursivo para atrapar la tabla esté donde esté
    private java.awt.Component encontrarExpansible(java.awt.Container contenedor) {
        for (java.awt.Component c : contenedor.getComponents()) {
            if (c instanceof javax.swing.JScrollPane || c instanceof javax.swing.JTabbedPane) {
                return c;
            } else if (c instanceof java.awt.Container) {
                java.awt.Component encontrado = encontrarExpansible((java.awt.Container) c);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private void ocultarBotonesNavegacion(java.awt.Container contenedor) {
        for (java.awt.Component comp : contenedor.getComponents()) {
            if (comp instanceof javax.swing.JButton) {
                String txt = ((javax.swing.JButton) comp).getText();
                if (txt != null && (txt.equalsIgnoreCase("Volver") || txt.equalsIgnoreCase("Salir") || txt.equalsIgnoreCase("Cancelar"))) {
                    comp.setVisible(false);
                }
            } else if (comp instanceof java.awt.Container) {
                ocultarBotonesNavegacion((java.awt.Container) comp);
            }
        }
    }

    private void mejorarTablasUniversales(java.awt.Container contenedor) {
        for (java.awt.Component comp : contenedor.getComponents()) {
            if (comp instanceof javax.swing.JScrollPane) {
                java.awt.Component view = ((javax.swing.JScrollPane) comp).getViewport().getView();
                if (view instanceof javax.swing.JTable) {
                    javax.swing.JTable tabla = (javax.swing.JTable) view;
                    tabla.setFillsViewportHeight(true); 
                    tabla.setRowHeight(25); 
                    tabla.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
                    tabla.getTableHeader().setBackground(new java.awt.Color(168, 197, 227));
                }
            } else if (comp instanceof java.awt.Container) {
                mejorarTablasUniversales((java.awt.Container) comp);
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panInicio = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmdVentas = new javax.swing.JButton();
        cmdCompras = new javax.swing.JButton();
        cmdProveedores = new javax.swing.JButton();
        cmdInventario = new javax.swing.JButton();
        cmdReportes = new javax.swing.JButton();
        cmdSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Verduleria Glagerman");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panInicio.setBackground(new java.awt.Color(250, 250, 250));
        panInicio.setMaximumSize(new java.awt.Dimension(800, 600));
        panInicio.setMinimumSize(new java.awt.Dimension(800, 600));
        panInicio.setPreferredSize(new java.awt.Dimension(800, 600));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel1.setText("Negocio Glagerman");
        jLabel1.setAlignmentY(0.0F);

        cmdVentas.setBackground(new java.awt.Color(168, 197, 227));
        cmdVentas.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cmdVentas.setText("Registrar Venta");
        cmdVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdVentasActionPerformed(evt);
            }
        });

        cmdCompras.setBackground(new java.awt.Color(237, 237, 237));
        cmdCompras.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cmdCompras.setText("Registrar Compra");
        cmdCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdComprasActionPerformed(evt);
            }
        });

        cmdProveedores.setBackground(new java.awt.Color(237, 237, 237));
        cmdProveedores.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cmdProveedores.setText("Administrar Proveedor");
        cmdProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdProveedoresActionPerformed(evt);
            }
        });

        cmdInventario.setBackground(new java.awt.Color(237, 237, 237));
        cmdInventario.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cmdInventario.setText("Administrar Inventario");
        cmdInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInventarioActionPerformed(evt);
            }
        });

        cmdReportes.setBackground(new java.awt.Color(237, 237, 237));
        cmdReportes.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cmdReportes.setText("Reportes");
        cmdReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdReportesActionPerformed(evt);
            }
        });

        cmdSalir.setBackground(new java.awt.Color(244, 168, 168));
        cmdSalir.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cmdSalir.setText("Salir");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panInicioLayout = new javax.swing.GroupLayout(panInicio);
        panInicio.setLayout(panInicioLayout);
        panInicioLayout.setHorizontalGroup(
            panInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panInicioLayout.createSequentialGroup()
                .addGap(312, 312, 312)
                .addGroup(panInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panInicioLayout.createSequentialGroup()
                        .addComponent(cmdSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panInicioLayout.createSequentialGroup()
                        .addGroup(panInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmdProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(71, 294, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panInicioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(280, 280, 280))
        );
        panInicioLayout.setVerticalGroup(
            panInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panInicioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(cmdSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(panInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        System.exit(0); // Cerrar el sistema
    }//GEN-LAST:event_cmdSalirActionPerformed

    private void cmdVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdVentasActionPerformed
        VentasPage ventasPage = new VentasPage();
        ventasPage.setVisible(true);
        this.dispose(); // liberar recursos en vez de setVisible(false)
    }//GEN-LAST:event_cmdVentasActionPerformed

    private void cmdComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdComprasActionPerformed
        CompraAProveedoresPage comprasPage = new CompraAProveedoresPage();
        comprasPage.setVisible(true);       
        this.dispose(); // liberar recursos
    }//GEN-LAST:event_cmdComprasActionPerformed

    private void cmdProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdProveedoresActionPerformed
        AdminProPage administrarPage = new AdminProPage();
        administrarPage.setVisible(true);   
        this.dispose(); // liberar recursos
    }//GEN-LAST:event_cmdProveedoresActionPerformed

    private void cmdInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdInventarioActionPerformed
        AdminInvPage administrarInvPage = new AdminInvPage();
        administrarInvPage.setVisible(true);   
        this.dispose(); // liberar recursos
    }//GEN-LAST:event_cmdInventarioActionPerformed

    private void cmdReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdReportesActionPerformed
        ReportesPage AdminReportesPage = new ReportesPage();
        AdminReportesPage.setVisible(true);   
        this.dispose(); // liberar recursos
    }//GEN-LAST:event_cmdReportesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> {
            Connection conex = posglagerman.ConexionDB.getConexionOrShowDialog();
            if (conex != null) {
                try {
                    conex.close();
                } catch (SQLException ex) {
                    logger.log(java.util.logging.Level.WARNING, "Error cerrando conexión de verificación al iniciar", ex);
                }
                // Mostrar mensaje de éxito UNA sola vez al inicio de la app.
                JOptionPane.showMessageDialog(null,
                    "Sistema abierto correctamente.",
                    "Conexión exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            new InicioPage().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdCompras;
    private javax.swing.JButton cmdInventario;
    private javax.swing.JButton cmdProveedores;
    private javax.swing.JButton cmdReportes;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JButton cmdVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panInicio;
    // End of variables declaration//GEN-END:variables
}
