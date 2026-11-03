package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class PasarelaPagoDialog extends JDialog {

    private final long totalVenta;
    private boolean pagoConfirmado = false;
    private String resumenPagoTicket = "";

    // Variables para guardar en la Base de Datos SQLite
    private String metodoPagoBD = "EFECTIVO";
    private long montoPagadoBD = 0;
    private long vueltoBD = 0;
    private String dniClienteBD = "-";
    private String celularClienteBD = "-";
    private String detalleBilletesBD = "-";

    // Denominaciones de billetes peruanos
    private final int[] denominaciones = {10, 20, 50, 100, 200};
    private final String[] archivosImagen = {
        "billete_10s.jpg", "billete_20s.jpg", "billete_50s.jpg", "billete_100s.jpg", "billete_200s.jpg"
    };
    private final Map<Integer, Integer> conteoBilletes = new LinkedHashMap<>();
    private final Map<Integer, JLabel> etiquetasContador = new LinkedHashMap<>();

    // Componentes Efectivo
    private JTextField txtMonedasExtra;
    private JLabel lblMontoRecibido;
    private JLabel lblVuelto;
    private JLabel lblDetalleBilletes;

    // Componentes Yape / Plin
    private JRadioButton rbEfectivo;
    private JRadioButton rbYape;
    private JRadioButton rbPlin;
    private JPanel panelContenedorModos;
    private CardLayout cardLayout;
    private JTextField txtDni;
    private JTextField txtCelular;
    private JTextField txtMontoDigital;

    public PasarelaPagoDialog(Frame parent, long totalVenta) {
        super(parent, "Pasarela de Pago - Verdulería Glagerman", true);
        this.totalVenta = totalVenta;
        for (int den : denominaciones) {
            conteoBilletes.put(den, 0);
        }
        construirInterfaz();
        actualizarCalculoEfectivo();
    }

    private void construirInterfaz() {
        // Permite redimensionar y maximizar la ventana libremente
        setSize(940, 660);
        setMinimumSize(new Dimension(820, 580));
        setLocationRelativeTo(getParent());
        setResizable(true);
        getContentPane().setBackground(new Color(250, 250, 250));
        setLayout(new BorderLayout(10, 10));

        // 1. CABECERA EN DOS FILAS SEPARADAS (Evita que las letras choquen)
        JPanel panelNorte = new JPanel(new GridLayout(2, 1, 5, 8));
        panelNorte.setBackground(new Color(230, 235, 240));
        panelNorte.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTituloTotal = new JLabel("TOTAL A COBRAR: S/ " + totalVenta, SwingConstants.CENTER);
        lblTituloTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloTotal.setForeground(new Color(22, 54, 92));

        JPanel panelMetodos = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panelMetodos.setOpaque(false);

        JLabel lblMetodo = new JLabel("Seleccione Método de Pago:");
        lblMetodo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        rbEfectivo = new JRadioButton("Efectivo (Billetes)", true);
        rbYape = new JRadioButton("Yape");
        rbPlin = new JRadioButton("Plin");

        Font fontRadio = new Font("Segoe UI", Font.BOLD, 15);
        rbEfectivo.setFont(fontRadio);
        rbYape.setFont(fontRadio);
        rbPlin.setFont(fontRadio);
        rbEfectivo.setOpaque(false);
        rbYape.setOpaque(false);
        rbPlin.setOpaque(false);
        rbEfectivo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rbYape.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rbPlin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ButtonGroup grupoMetodo = new ButtonGroup();
        grupoMetodo.add(rbEfectivo);
        grupoMetodo.add(rbYape);
        grupoMetodo.add(rbPlin);

        panelMetodos.add(lblMetodo);
        panelMetodos.add(rbEfectivo);
        panelMetodos.add(rbYape);
        panelMetodos.add(rbPlin);

        panelNorte.add(lblTituloTotal);
        panelNorte.add(panelMetodos);
        add(panelNorte, BorderLayout.NORTH);

        // 2. CONTENEDOR CENTRAL ADAPTABLE
        cardLayout = new CardLayout();
        panelContenedorModos = new JPanel(cardLayout);
        panelContenedorModos.setBorder(new EmptyBorder(5, 15, 5, 15));
        panelContenedorModos.setBackground(new Color(250, 250, 250));

        panelContenedorModos.add(crearPanelEfectivo(), "EFECTIVO");
        panelContenedorModos.add(crearPanelDigital(), "DIGITAL");

        rbEfectivo.addActionListener(e -> cardLayout.show(panelContenedorModos, "EFECTIVO"));
        rbYape.addActionListener(e -> cardLayout.show(panelContenedorModos, "DIGITAL"));
        rbPlin.addActionListener(e -> cardLayout.show(panelContenedorModos, "DIGITAL"));

        add(panelContenedorModos, BorderLayout.CENTER);

        // 3. PIE DE PÁGINA: Botones Confirmar y Cancelar
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panelSur.setBackground(new Color(230, 235, 240));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(244, 168, 168));
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnConfirmar = new JButton("Confirmar Pago y Generar Ticket");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setPreferredSize(new Dimension(280, 45));
        btnConfirmar.addActionListener(e -> validarYConfirmarPago());

        panelSur.add(btnCancelar);
        panelSur.add(btnConfirmar);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelEfectivo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(250, 250, 250));

        JLabel lblInstruccion = new JLabel(
            "Haz clic en cada billete para sumarlo al pago. Usa el botón '[-] Quitar' (o clic derecho) si te equivocaste:"
        );
        lblInstruccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblInstruccion, BorderLayout.NORTH);

        // Grilla responsiva de los 5 billetes
        JPanel panelBilletes = new JPanel(new GridLayout(1, 5, 12, 12));
        panelBilletes.setBackground(new Color(250, 250, 250));

        for (int i = 0; i < denominaciones.length; i++) {
            final int valor = denominaciones[i];
            final String nombreImg = archivosImagen[i];

            JPanel tarjetaBillete = new JPanel(new BorderLayout(5, 5));
            tarjetaBillete.setBackground(Color.WHITE);
            tarjetaBillete.setBorder(new LineBorder(new Color(180, 180, 180), 1, true));

            JButton btnBillete = new JButton();
            btnBillete.setLayout(new BorderLayout(0, 5));
            btnBillete.setBackground(new Color(245, 250, 255));
            btnBillete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBillete.setFocusPainted(false);
            btnBillete.setToolTipText("Clic izquierdo: Sumar S/ " + valor + " | Clic derecho: Quitar 1 billete");

            ImageIcon icono = cargarImagenBillete(nombreImg, 145, 80);
            JLabel lblImg = new JLabel();
            lblImg.setHorizontalAlignment(SwingConstants.CENTER);
            if (icono != null) {
                lblImg.setIcon(icono);
            } else {
                lblImg.setText("S/ " + valor);
                lblImg.setFont(new Font("Segoe UI", Font.BOLD, 22));
            }

            JLabel lblValorTitulo = new JLabel("+ S/ " + valor + " Soles", SwingConstants.CENTER);
            lblValorTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblValorTitulo.setForeground(new Color(22, 54, 92));
            lblValorTitulo.setBorder(new EmptyBorder(5, 0, 0, 0));

            btnBillete.add(lblValorTitulo, BorderLayout.NORTH);
            btnBillete.add(lblImg, BorderLayout.CENTER);

            btnBillete.addActionListener(e -> modificarBillete(valor, 1));
            btnBillete.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        modificarBillete(valor, -1);
                    }
                }
            });

            JPanel panelControlBillete = new JPanel(new BorderLayout(4, 4));
            panelControlBillete.setBackground(Color.WHITE);
            panelControlBillete.setBorder(new EmptyBorder(6, 6, 6, 6));

            JLabel lblCantidad = new JLabel("Recibidos: 0", SwingConstants.CENTER);
            lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 13));
            etiquetasContador.put(valor, lblCantidad);

            JButton btnRestar = new JButton("[-] Quitar S/ " + valor);
            btnRestar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnRestar.setBackground(new Color(255, 220, 220));
            btnRestar.setForeground(new Color(180, 0, 0));
            btnRestar.setFocusPainted(false);
            btnRestar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnRestar.addActionListener(e -> modificarBillete(valor, -1));

            panelControlBillete.add(lblCantidad, BorderLayout.NORTH);
            panelControlBillete.add(btnRestar, BorderLayout.SOUTH);

            tarjetaBillete.add(btnBillete, BorderLayout.CENTER);
            tarjetaBillete.add(panelControlBillete, BorderLayout.SOUTH);
            panelBilletes.add(tarjetaBillete);
        }

        panel.add(panelBilletes, BorderLayout.CENTER);

        // Panel inferior de resumen de Efectivo y Vuelto
        JPanel panelResumen = new JPanel(new GridLayout(4, 1, 6, 6));
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(168, 197, 227), 2),
            new EmptyBorder(12, 18, 12, 18)
        ));

        JPanel filaMonedas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaMonedas.setOpaque(false);
        JLabel lblMonedas = new JLabel("Sencillo / Monedas adicionales (S/):");
        lblMonedas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filaMonedas.add(lblMonedas);

        txtMonedasExtra = new JTextField("0", 6);
        txtMonedasExtra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtMonedasExtra.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                actualizarCalculoEfectivo();
            }
        });

        JButton btnReiniciarBilletes = new JButton("Reiniciar todos los billetes a 0");
        btnReiniciarBilletes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReiniciarBilletes.setBackground(new Color(230, 230, 230));
        btnReiniciarBilletes.addActionListener(e -> {
            for (int den : denominaciones) conteoBilletes.put(den, 0);
            for (int den : denominaciones) etiquetasContador.get(den).setText("Recibidos: 0");
            txtMonedasExtra.setText("0");
            actualizarCalculoEfectivo();
        });
        filaMonedas.add(txtMonedasExtra);
        filaMonedas.add(btnReiniciarBilletes);

        lblDetalleBilletes = new JLabel("Billetes recibidos: Ninguno");
        lblDetalleBilletes.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        lblMontoRecibido = new JLabel("EFECTIVO TOTAL RECIBIDO: S/ 0");
        lblMontoRecibido.setFont(new Font("Segoe UI", Font.BOLD, 18));

        lblVuelto = new JLabel("VUELTO A ENTREGAR (SACAR DE CAJA): S/ 0");
        lblVuelto.setFont(new Font("Segoe UI", Font.BOLD, 20));

        panelResumen.add(filaMonedas);
        panelResumen.add(lblDetalleBilletes);
        panelResumen.add(lblMontoRecibido);
        panelResumen.add(lblVuelto);

        panel.add(panelResumen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelDigital() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(168, 197, 227), 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblInfo = new JLabel("Registro de Pago por Billetera Digital (Yape / Plin)");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblInfo.setForeground(new Color(114, 28, 142));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblInfo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        JLabel lbl1 = new JLabel("DNI del Cliente (8 dígitos):");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(lbl1, gbc);

        txtDni = new JTextField(15);
        txtDni.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtDni.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) || txtDni.getText().length() >= 8) {
                    e.consume();
                }
            }
        });
        gbc.gridx = 1;
        panel.add(txtDni, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lbl2 = new JLabel("Número de Celular (9 dígitos):");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(lbl2, gbc);

        txtCelular = new JTextField(15);
        txtCelular.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtCelular.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) || txtCelular.getText().length() >= 9) {
                    e.consume();
                }
            }
        });
        gbc.gridx = 1;
        panel.add(txtCelular, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lbl3 = new JLabel("Monto Pagado (S/):");
        lbl3.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(lbl3, gbc);

        txtMontoDigital = new JTextField(String.valueOf(totalVenta), 15);
        txtMontoDigital.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtMontoDigital.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });
        gbc.gridx = 1;
        panel.add(txtMontoDigital, gbc);

        return panel;
    }

    private void modificarBillete(int denominacion, int delta) {
        int actual = conteoBilletes.getOrDefault(denominacion, 0);
        int nuevo = Math.max(0, actual + delta);
        conteoBilletes.put(denominacion, nuevo);
        etiquetasContador.get(denominacion).setText("Recibidos: " + nuevo);
        actualizarCalculoEfectivo();
    }

    private long obtenerTotalEfectivoRecibido() {
        long suma = 0;
        for (Map.Entry<Integer, Integer> entry : conteoBilletes.entrySet()) {
            suma += (long) entry.getKey() * entry.getValue();
        }
        String extraStr = txtMonedasExtra.getText().trim();
        if (!extraStr.isEmpty()) {
            try {
                suma += Long.parseLong(extraStr);
            } catch (NumberFormatException ignored) {}
        }
        return suma;
    }

    private String construirDesgloseBilletes() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : conteoBilletes.entrySet()) {
            if (entry.getValue() > 0) {
                if (sb.length() > 0) sb.append(" + ");
                sb.append("S/").append(entry.getKey()).append("(x").append(entry.getValue()).append(")");
            }
        }
        String extraStr = txtMonedasExtra.getText().trim();
        if (!extraStr.isEmpty() && !extraStr.equals("0")) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append("Monedas(S/").append(extraStr).append(")");
        }
        return sb.length() == 0 ? "Ninguno" : sb.toString();
    }

    private void actualizarCalculoEfectivo() {
        long recibido = obtenerTotalEfectivoRecibido();
        long diferencia = recibido - totalVenta;

        lblDetalleBilletes.setText("Billetes recibidos: " + construirDesgloseBilletes());
        lblMontoRecibido.setText("EFECTIVO TOTAL RECIBIDO: S/ " + recibido);

        if (diferencia >= 0) {
            lblVuelto.setText("VUELTO A ENTREGAR (SACAR DE CAJA): S/ " + diferencia);
            lblVuelto.setForeground(new Color(34, 139, 34));
        } else {
            lblVuelto.setText("FALTA CUBRIR: S/ " + Math.abs(diferencia));
            lblVuelto.setForeground(Color.RED);
        }
    }

    private void validarYConfirmarPago() {
        if (rbEfectivo.isSelected()) {
            long recibido = obtenerTotalEfectivoRecibido();
            if (recibido < totalVenta) {
                JOptionPane.showMessageDialog(this,
                    "El monto recibido en efectivo (S/ " + recibido + ") es menor al total a cobrar (S/ " + totalVenta + ").\n" +
                    "Selecciona los billetes entregados por el cliente.",
                    "Pago Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long vuelto = recibido - totalVenta;

            // Guardar datos para la Base de Datos
            this.metodoPagoBD = "EFECTIVO";
            this.montoPagadoBD = recibido;
            this.vueltoBD = vuelto;
            this.dniClienteBD = "-";
            this.celularClienteBD = "-";
            this.detalleBilletesBD = construirDesgloseBilletes();

            resumenPagoTicket =
                "Método de Pago: EFECTIVO\n" +
                "Billetes:       " + this.detalleBilletesBD + "\n" +
                "Monto Recibido: S/ " + recibido + "\n" +
                "Vuelto Caja:    S/ " + vuelto + "\n";
            pagoConfirmado = true;
            dispose();
        } else {
            String metodo = rbYape.isSelected() ? "YAPE" : "PLIN";
            String dni = txtDni.getText().trim();
            String celular = txtCelular.getText().trim();
            String montoStr = txtMontoDigital.getText().trim();

            if (dni.length() != 8) {
                JOptionPane.showMessageDialog(this, "Ingresa un DNI válido de 8 dígitos.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (celular.length() != 9) {
                JOptionPane.showMessageDialog(this, "Ingresa un número de celular válido de 9 dígitos.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long montoPagado;
            try {
                montoPagado = Long.parseLong(montoStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingresa un monto pagado válido.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (montoPagado < totalVenta) {
                JOptionPane.showMessageDialog(this, "El monto pagado por " + metodo + " no puede ser menor al total (S/ " + totalVenta + ").", "Monto insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            long vuelto = montoPagado - totalVenta;

            // Guardar datos para la Base de Datos
            this.metodoPagoBD = metodo;
            this.montoPagadoBD = montoPagado;
            this.vueltoBD = vuelto;
            this.dniClienteBD = dni;
            this.celularClienteBD = celular;
            this.detalleBilletesBD = "Transferencia " + metodo;

            resumenPagoTicket =
                "Método de Pago: " + metodo + "\n" +
                "DNI Cliente:    " + dni + "\n" +
                "Nro. Celular:   " + celular + "\n" +
                "Monto Pagado:   S/ " + montoPagado + "\n";
            pagoConfirmado = true;
            dispose();
        }
    }

    private ImageIcon cargarImagenBillete(String nombreArchivo, int ancho, int alto) {
        try {
            URL recurso = getClass().getResource("/images/" + nombreArchivo);
            if (recurso == null) recurso = getClass().getResource("/views/" + nombreArchivo);
            Image img = null;
            if (recurso != null) {
                img = new ImageIcon(recurso).getImage();
            } else {
                File f1 = new File("src/images/" + nombreArchivo);
                File f2 = new File("images/" + nombreArchivo);
                if (f1.exists()) img = new ImageIcon(f1.getAbsolutePath()).getImage();
                else if (f2.exists()) img = new ImageIcon(f2.getAbsolutePath()).getImage();
            }
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isPagoConfirmado() { return pagoConfirmado; }
    public String getResumenPagoTicket() { return resumenPagoTicket; }
    public String getMetodoPagoBD() { return metodoPagoBD; }
    public long getMontoPagadoBD() { return montoPagadoBD; }
    public long getVueltoBD() { return vueltoBD; }
    public String getDniClienteBD() { return dniClienteBD; }
    public String getCelularClienteBD() { return celularClienteBD; }
    public String getDetalleBilletesBD() { return detalleBilletesBD; }
}