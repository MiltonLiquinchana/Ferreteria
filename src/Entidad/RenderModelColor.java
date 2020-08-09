package Entidad;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author liqui
 */
public class RenderModelColor extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String valor = (String) table.getValueAt(row, 6);
        if (valor.equals("CADUCADO")) {
            this.setBackground(Color.red);
        }else
        if (valor.equals("POR CADUCAR")) {
            this.setBackground(Color.yellow);
        }else
        if (valor.equals("PENDIENTE")) {
            this.setBackground(Color.green);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
