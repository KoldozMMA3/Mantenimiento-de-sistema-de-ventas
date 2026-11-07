package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CodigoBarrasUtil {

    // Mapeo nativo del estándar Code 128B (ASCII 32 a 127) sin usar librerías externas
    private static final String[] CODE128_PATTERNS = "212222,222122,222221,121223,121322,131222,122213,122312,132212,221213,221312,231212,112232,122132,122231,113222,123122,123221,223211,221132,221231,213212,223112,312131,311222,321122,321221,312212,322112,322211,212123,212321,232121,111323,131123,131321,112313,132113,132311,211313,231113,231311,112133,112331,132131,113123,113321,133121,313121,211331,231131,213113,213311,213131,311123,311321,331121,312113,312311,332111,314111,221411,431111,111224,111422,121124,121421,141122,141221,112214,112412,122114,122411,142112,142211,241211,221114,413111,241112,134111,111242,121142,121241,114212,124112,124211,411212,421112,421211,212141,214121,412121,111143,111341,131141,114113,114311,411113,411311,113141,114131,311141,411131,211412,211214,211232,2331112".split(",");

    public static void generarYMostrar(Component parent, String codigo, String nombre, String precio) {
        try {
            // 1. Calcular Checksum y Estructura del Código 128B
            StringBuilder pattern = new StringBuilder(CODE128_PATTERNS[104]); // Símbolo Start B
            int checksum = 104;
            for (int i = 0; i < codigo.length(); i++) {
                int ascii = codigo.charAt(i);
                int value = ascii - 32;
                if(value < 0 || value > 95) value = 0; // Fallback para caracteres raros
                pattern.append(CODE128_PATTERNS[value]);
                checksum += value * (i + 1);
            }
            checksum = checksum % 103;
            pattern.append(CODE128_PATTERNS[checksum]); // Dígito verificador
            pattern.append(CODE128_PATTERNS[106]); // Símbolo Stop

            // 2. Calcular ancho dinámico según el tamaño del código
            int barWidth = 2;
            int modules = 0;
            for (char c : pattern.toString().toCharArray()) {
                modules += Character.getNumericValue(c);
            }
            int width = Math.max(350, 60 + (modules * barWidth));
            int height = 150;

            // 3. Crear el lienzo gráfico 2D
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            
            // Fondo blanco
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Dibujar Barras negras
            g2d.setColor(Color.BLACK);
            int x = 30;
            boolean isBlack = true;
            for (char c : pattern.toString().toCharArray()) {
                int w = Character.getNumericValue(c) * barWidth;
                if (isBlack) {
                    g2d.fillRect(x, 40, w, 60);
                }
                x += w;
                isBlack = !isBlack;
            }
            
            // 4. Dibujar Textos de la etiqueta (Antialiasing activado para nitidez)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2d.drawString(nombre.length() > 25 ? nombre.substring(0,25) + "..." : nombre, 30, 25); 
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g2d.drawString(codigo, 30, 125); 
            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2d.drawString("S/ " + precio, width - 110, 125); 
            g2d.dispose();

            // 5. Mostrar Diálogo Visual y Opción de Guardado
            ImageIcon icon = new ImageIcon(img);
            int opc = JOptionPane.showConfirmDialog(parent, 
                new JLabel(icon), 
                "Etiqueta Generada Exitosamente\n¿Desea guardarla como PNG para imprimir?", 
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                
            if (opc == JOptionPane.YES_OPTION) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Guardar Código de Barras");
                fc.setSelectedFile(new File("Codigo_" + codigo + ".png"));
                if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if(!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getAbsolutePath() + ".png");
                    }
                    ImageIO.write(img, "png", file);
                    JOptionPane.showMessageDialog(parent, "Etiqueta lista para impresión guardada en:\n" + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error al generar código: " + e.getMessage());
        }
    }
}