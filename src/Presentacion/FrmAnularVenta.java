/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Presentacion;

import Entidad.*;
import Negocio.*;
import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author DAYPER PERU
 */
public class FrmAnularVenta extends javax.swing.JInternalFrame {

    static ResultSet rs = null;
    DefaultTableModel dtm = new DefaultTableModel();
    DefaultTableModel dtm1 = new DefaultTableModel();
    String id[] = new String[50];
    static int intContador;
    Date fecha_ini, fecha_fin;
    String documento, criterio, busqueda, Total;
    boolean valor = true;
    int n = 0;
    String nomcliente;
    /*enviar cantidad de esta consulta a la otra ventana*/
    double cant = 0, ncant, stock;
    /*instancia de form frmventa*/
    Presentacion.FrmVenta venta = new Presentacion.FrmVenta();
    /*Array list tipo string publica*/
    ArrayList cantidades = new ArrayList();

    public FrmAnularVenta() {
        initComponents();
        //lblIdVenta.setVisible(false);
        cargarComboTipoDocumento();
        //---------------------ANCHO Y ALTO DEL FORM----------------------
        this.setSize(769, 338);

        //---------------------FECHA ACTUAL-------------------------------
        Date date = new Date();
        String format = new String("dd/MM/yyyy");
        SimpleDateFormat formato = new SimpleDateFormat(format);
        dcFechaini.setDate(date);
        dcFechafin.setDate(date);
        lblidvendedor.setVisible(false);
        lblvendedor.setVisible(false);
        BuscarVenta();
        CrearTabla();
        CantidadTotal();
    }
//-----------------------------------------------------------------------------------------------
//--------------------------------------METODOS--------------------------------------------------
//-----------------------------------------------------------------------------------------------

    public void CrearTabla() {
        //--------------------PRESENTACION DE JTABLE----------------------

        TableCellRenderer render = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                //aqui obtengo el render de la calse superior 
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //Determinar Alineaciones   
                if (column == 0 || column == 2 || column == 4 || column == 5 || column == 6 || column == 7 || column == 8) {
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    l.setHorizontalAlignment(SwingConstants.LEFT);
                }

                //Colores en Jtable        
                if (isSelected) {
                    l.setBackground(new Color(51, 152, 255));
                    //l.setBackground(new Color(168, 198, 238));
                    l.setForeground(Color.WHITE);
                } else {
                    l.setForeground(Color.BLACK);
                    if (row % 2 == 0) {
                        l.setBackground(Color.WHITE);
                    } else {
                        //l.setBackground(new Color(232, 232, 232));
                        l.setBackground(new Color(229, 246, 245));
                    }
                }
                return l;
            }
        };

        //Agregar Render
        for (int i = 0; i < tblVenta.getColumnCount(); i++) {
            tblVenta.getColumnModel().getColumn(i).setCellRenderer(render);
        }

        //Activar ScrollBar
        tblVenta.setAutoResizeMode(tblVenta.AUTO_RESIZE_OFF);

        //Anchos de cada columna
        int[] anchos = {50, 160, 70, 120, 80, 40, 60, 60, 80};
        for (int i = 0; i < tblVenta.getColumnCount(); i++) {
            tblVenta.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

    }

    public void BuscarVenta() {
        String titulos[] = {"ID", "Cliente", "Fecha", "Empleado", "Documento", "Serie", "Número", "Estado", "Total"};
        dtm.setColumnIdentifiers(titulos);

        ClsVenta venta = new ClsVenta();

        fecha_ini = dcFechaini.getDate();
        fecha_fin = dcFechafin.getDate();
        documento = cboTipoDocumento.getSelectedItem().toString();
        try {
            rs = venta.listarVentaPorFecha("anular", fecha_ini, fecha_fin, documento);
            boolean encuentra = false;
            String Datos[] = new String[9];
            int f, i;
            f = dtm.getRowCount();
            if (f > 0) {
                for (i = 0; i < f; i++) {
                    dtm.removeRow(0);
                }
            }
            while (rs.next()) {
                Datos[0] = (String) rs.getString(1);
                Datos[1] = (String) rs.getString(2);
                Datos[2] = (String) rs.getString(3);
                Datos[3] = (String) rs.getString(4);
                Datos[4] = (String) rs.getString(5);
                Datos[5] = (String) rs.getString(6);
                Datos[6] = (String) rs.getString(7);
                Datos[7] = (String) rs.getString(8);
                Datos[8] = (String) rs.getString(9);

                dtm.addRow(Datos);
                encuentra = true;

            }
            if (encuentra = false) {
                JOptionPane.showMessageDialog(null, "¡No se encuentra!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tblVenta.setModel(dtm);
    }

    void cargarComboTipoDocumento() {
        ClsTipoDocumento documentos = new ClsTipoDocumento();
        DefaultComboBoxModel DefaultComboBoxModel = new DefaultComboBoxModel();
        DefaultComboBoxModel.removeAllElements();

        try {
            rs = documentos.listarTipoDocumentoPorParametro("descripcion", "");
            boolean encuentra = false;
            String Datos[] = new String[2];

            while (rs.next()) {
                id[intContador] = (String) rs.getString(1);
                Datos[0] = (String) rs.getString(1);
                Datos[1] = (String) rs.getString(2);
                DefaultComboBoxModel.addElement((String) rs.getString(2));
                encuentra = true;
                intContador++;
            }
            cboTipoDocumento.setModel(DefaultComboBoxModel);

            if (encuentra = false) {
                JOptionPane.showMessageDialog(null, "No se encuentra");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void CrearTablaDetalle() {
        //--------------------PRESENTACION DE JTABLE DETALLE VENTA----------------------

        TableCellRenderer render = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                //aqui obtengo el render de la calse superior 
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //Determinar Alineaciones   
                if (column == 0 || column == 1 || column == 2 || column == 5 || column == 6) {
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    l.setHorizontalAlignment(SwingConstants.LEFT);
                }

                //Colores en Jtable        
                if (isSelected) {
                    l.setBackground(new Color(51, 152, 255));
                    //l.setBackground(new Color(168, 198, 238));
                    l.setForeground(Color.WHITE);
                } else {
                    l.setForeground(Color.BLACK);
                    if (row % 2 == 0) {
                        l.setBackground(Color.WHITE);
                    } else {
                        //l.setBackground(new Color(232, 232, 232));
                        l.setBackground(new Color(229, 246, 245));
                    }
                }
                return l;
            }
        };

        //Agregar Render
        for (int i = 0; i < tblDetalleVenta.getColumnCount(); i++) {
            tblDetalleVenta.getColumnModel().getColumn(i).setCellRenderer(render);
        }

        //Activar ScrollBar
        tblDetalleVenta.setAutoResizeMode(tblVenta.AUTO_RESIZE_OFF);

        //Anchos de cada columna
        int[] anchos = {50, 60, 80, 200, 200, 60, 60, 60};
        for (int i = 0; i < tblDetalleVenta.getColumnCount(); i++) {
            tblDetalleVenta.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        //Ocultar Columnas
        ocultarColumnas(tblDetalleVenta, new int[]{1});
    }

    void BuscarVentaDetalle() {
        String titulos[] = {"ID", "ID Prod.", "Cód Producto", "Nombre", "Descripción", "Cantidad", "Precio", "Total"};
        dtm1.setColumnIdentifiers(titulos);
        ClsDetalleVenta detalleventa = new ClsDetalleVenta();
        busqueda = lblIdVenta.getText();

        try {
            rs = detalleventa.listarDetalleVentaPorParametro("id", busqueda);
            boolean encuentra = false;
            String Datos[] = new String[8];
            int f, i;
            f = dtm1.getRowCount();
            if (f > 0) {
                for (i = 0; i < f; i++) {
                    dtm1.removeRow(0);
                }
            }
            while (rs.next()) {
                Datos[0] = (String) rs.getString(1);
                Datos[1] = (String) rs.getString(2);
                Datos[2] = (String) rs.getString(3);
                Datos[3] = (String) rs.getString(4);
                Datos[4] = (String) rs.getString(5);
                Datos[5] = (String) rs.getString(6);
                Datos[6] = (String) rs.getString(7);
                Datos[7] = (String) rs.getString(8);
                dtm1.addRow(Datos);
                encuentra = true;

            }
            if (encuentra = false) {
                JOptionPane.showMessageDialog(null, "¡No se encuentra!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tblDetalleVenta.setModel(dtm1);
    }

    void CantidadTotal() {
        Total = String.valueOf(tblVenta.getRowCount());
        lblEstado.setText("Se cargaron " + Total + " registros");
    }

    void restablecerCantidades() {
        String strId;
        ClsProducto productos = new ClsProducto();
        ClsEntidadProducto producto = new ClsEntidadProducto();
        int fila = 0;

        fila = tblDetalleVenta.getRowCount();
        for (int f = 0; f < fila; f++) {
            try {
                ClsProducto oProducto = new ClsProducto();

                rs = oProducto.listarProductoActivoPorParametro("id", ((String) tblDetalleVenta.getValueAt(f, 1)));
                while (rs.next()) {
                    cant = Double.parseDouble(rs.getString(5));
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                System.out.println(ex.getMessage());
            }

            strId = ((String) tblDetalleVenta.getValueAt(f, 1));
            ncant = Double.parseDouble(String.valueOf(tblDetalleVenta.getModel().getValueAt(f, 5)));
            stock = cant + ncant;
            producto.setStrStockProducto(String.valueOf(stock));
            productos.actualizarProductoStock(strId, producto);

        }
    }

    private void ocultarColumnas(JTable tbl, int columna[]) {
        for (int i = 0; i < columna.length; i++) {
            tbl.getColumnModel().getColumn(columna[i]).setMaxWidth(0);
            tbl.getColumnModel().getColumn(columna[i]).setMinWidth(0);
            tbl.getTableHeader().getColumnModel().getColumn(columna[i]).setMaxWidth(0);
            tbl.getTableHeader().getColumnModel().getColumn(columna[i]).setMinWidth(0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane5 = new javax.swing.JScrollPane();
        tblVenta = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        dcFechaini = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        dcFechafin = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboTipoDocumento = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        btnAnularVenta = new javax.swing.JButton();
        lblIdVenta = new javax.swing.JLabel();
        lblvendedor = new javax.swing.JLabel();
        lblidvendedor = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblDetalleVenta = new javax.swing.JTable();
        btnVerDetalle = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        btneditar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setTitle("Anular Ventas");
        getContentPane().setLayout(null);

        tblVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblVenta.setRowHeight(22);
        tblVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVentaMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblVenta);

        getContentPane().add(jScrollPane5);
        jScrollPane5.setBounds(10, 110, 730, 140);

        jPanel1.setBackground(new java.awt.Color(255, 204, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Opciones de busqueda y anulación"));
        jPanel1.setLayout(null);

        dcFechaini.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(dcFechaini);
        dcFechaini.setBounds(20, 40, 100, 25);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("DESDE:");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(20, 20, 70, 20);

        dcFechafin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(dcFechafin);
        dcFechafin.setBounds(130, 40, 100, 25);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("DOCUMENTO:");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(240, 20, 100, 20);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("HASTA:");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(130, 20, 70, 20);

        cboTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cboTipoDocumento);
        cboTipoDocumento.setBounds(240, 40, 140, 25);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/Buscar_32.png"))); // NOI18N
        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(390, 22, 110, 50);

        btnAnularVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/anular.png"))); // NOI18N
        btnAnularVenta.setText("Anular");
        btnAnularVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnularVentaActionPerformed(evt);
            }
        });
        jPanel1.add(btnAnularVenta);
        btnAnularVenta.setBounds(510, 22, 110, 50);
        jPanel1.add(lblIdVenta);
        lblIdVenta.setBounds(320, 20, 40, 20);
        jPanel1.add(lblvendedor);
        lblvendedor.setBounds(640, 50, 70, 20);
        jPanel1.add(lblidvendedor);
        lblidvendedor.setBounds(640, 20, 70, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(10, 10, 730, 90);

        tblDetalleVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblDetalleVenta.setRowHeight(22);
        tblDetalleVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleVentaMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tblDetalleVenta);

        getContentPane().add(jScrollPane6);
        jScrollPane6.setBounds(10, 320, 730, 140);

        btnVerDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/busqueda_detallada.png"))); // NOI18N
        btnVerDetalle.setText("Ver Detalle");
        btnVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetalleActionPerformed(evt);
            }
        });
        getContentPane().add(btnVerDetalle);
        btnVerDetalle.setBounds(590, 260, 140, 40);
        getContentPane().add(lblEstado);
        lblEstado.setBounds(10, 250, 230, 20);

        btneditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/busqueda_detallada.png"))); // NOI18N
        btneditar.setText("Editar");
        btneditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneditarActionPerformed(evt);
            }
        });
        getContentPane().add(btneditar);
        btneditar.setBounds(430, 260, 140, 40);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblVentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVentaMouseClicked
        int fila;
        fila = tblVenta.getSelectedRow();
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Se debe seleccionar un registro");
        } else {
            defaultTableModel = (DefaultTableModel) tblVenta.getModel();
            //strCodigo =  ((String) defaultTableModel.getValueAt(fila, 0));
            lblIdVenta.setText((String) defaultTableModel.getValueAt(fila, 0));
        }
        BuscarVentaDetalle();
        CrearTablaDetalle();
    }//GEN-LAST:event_tblVentaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        BuscarVenta();
        CrearTabla();
        CantidadTotal();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblDetalleVentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleVentaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblDetalleVentaMouseClicked

    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalleActionPerformed
        if (tblVenta.getSelectedRows().length > 0) {

            if (n == 0) {
                this.setSize(769, 507);
                n = 1;
                btnVerDetalle.setText("Ocultar Detalle");
            } else if (n == 1) {
                this.setSize(769, 338);
                n = 0;
                btnVerDetalle.setText("Ver Detalle");
            }
            BuscarVentaDetalle();
            CrearTablaDetalle();
        } else {
            JOptionPane.showMessageDialog(null, "¡Se debe seleccionar un registro de venta!");
        }


    }//GEN-LAST:event_btnVerDetalleActionPerformed

    private void btnAnularVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnularVentaActionPerformed
        int fila_s;
        String est_venta;
        fila_s = tblVenta.getSelectedRow();
        est_venta = String.valueOf(tblVenta.getModel().getValueAt(fila_s, 7));
        if (!est_venta.equals("ANULADO")) {
            if (tblVenta.getSelectedRows().length > 0) {
                int result = JOptionPane.showConfirmDialog(this, "¿Desea anular la venta?", "Mensaje del Sistema", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    ClsVenta ventas = new ClsVenta();
                    ClsEntidadVenta venta = new ClsEntidadVenta();
                    venta.setStrEstadoVenta("ANULADO");
                    ventas.actualizarVentaEstado(lblIdVenta.getText(), venta);

                    BuscarVenta();
                    CrearTabla();
                    restablecerCantidades();
                }
                if (result == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Anulación Cancelada!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "¡Se debe seleccionar un registro de venta!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Esta venta ya ha sido ANULADA!");
        }

    }//GEN-LAST:event_btnAnularVentaActionPerformed

    private void btneditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneditarActionPerformed
        if (tblVenta.getSelectedRows().length > 0) {
            actualizarventa();
        } else {
            JOptionPane.showMessageDialog(null, "¡Se debe seleccionar un registro de venta!");
        }


    }//GEN-LAST:event_btneditarActionPerformed
    /*este metod sirve para mostrar la ventana venta*/
    void mostrarventanaventa() {
        /*en esta seccion solo estamos mostrarndo la vista de venta*/

        Presentacion.FrmPrincipal principal = null;
        principal.Escritorio.add(venta);
        venta.IdEmpleado = lblidvendedor.getText();
        venta.NombreEmpleado = lblvendedor.getText();
        venta.CalcularValor_Venta();
        venta.CalcularSubTotal();
        venta.CalcularIGV();
        venta.descuentototal();
        /*el valor del label venta sera anular*/
        venta.vandera.setText("anular");
        venta.idventaanular.setText(lblIdVenta.getText());
        venta.txtNombreCliente.setText(nomcliente);
        /*agregar datos a el array cantidades*/
        int f, i = 0;
        f = tblDetalleVenta.getRowCount();
        while (i < f) {
            cantidades.add(String.valueOf(tblDetalleVenta.getValueAt(i, 5)));
            i++;
        }
        venta.agregardatosarraylist(cantidades);
        //cantidades.clear();
        /*instancia de objeto para poder acceder a la tabla de ventas*/
        venta.show();
    }

    /*este metodo va a servir para capturar el lblIdVenta realizada y deacuerdo a eso hacer la consulta de los productos v
    endidos para luego agregarlos
    a la tabla de la ventana de venta*/
    void capturecodproducto() {
        /*aqui se va a consultar los productos que se vendieron deacuerdo al numero del documento*/
        ClsVenta ventas = new ClsVenta();
        String Datos[] = new String[12];
        try {
            rs = ventas.listarProductosporId(lblIdVenta.getText());
            boolean encuentra = false;
            
            while (rs.next()) {
                nomcliente = (String) rs.getString(1);
                Datos[0] = (String) rs.getString(2);
                Datos[1] = (String) rs.getString(3);
                Datos[2] = (String) rs.getString(4);
                Datos[3] = (String) rs.getString(5);
                Datos[4] = (String) rs.getString(6);
                Datos[5] = (String) rs.getString(7);
                Datos[6] = (String) rs.getString(8);
                Datos[7] = (String) rs.getString(9);
                Datos[8] = (String) rs.getString(10);
                Datos[9] = (String)rs.getString(11);
                Datos[10] = (String)rs.getString(12);
                Datos[11] =(String) rs.getString(13);
                venta.agregardatos(Integer.parseInt(Datos[0]), Datos[1], Datos[2], Datos[3], Double.parseDouble(Datos[4]),
                        Datos[5], Datos[6], Datos[7], Datos[8], Datos[9], Datos[10], Datos[11]);
                encuentra = true;
            }
            if (encuentra = false) {
                JOptionPane.showMessageDialog(null, "¡No se encuentra!");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /*Este metodo nos sirve para actualizar regresar la cantidad de productos vendidos antes de una compra*/
    void actualizarventa() {
        /*mensaje que avisa si desea actualizar la venta y en la cual se dice que se anulara la venta para luego proceder a la venta nuevamente*/
        int indice = JOptionPane.showConfirmDialog(this, "Para esta accion primero se anulara la venta \n ¿esta seguro?");/*al poner this se muestra en el centro del formulario y al poner null en medio de la pantalla*/
 /*para obtener la respuesta de que boton se preciono se lo hace al obtener el indice empesando desde cero este indice se guardara en una
    variable entera*/
        //el indice 0 equivale a SI
        if (indice == 0) {
            //si preciono si primero se ejecuta el metodo que anula la venta

            int fila_s;
            String est_venta;
            fila_s = tblVenta.getSelectedRow();
            est_venta = String.valueOf(tblVenta.getModel().getValueAt(fila_s, 7));
            if (!est_venta.equals("ANULADO")) {
                if (tblVenta.getSelectedRows().length > 0) {
                    ClsVenta ventas = new ClsVenta();
                    ClsEntidadVenta venta = new ClsEntidadVenta();
                    venta.setStrEstadoVenta("ANULADO");
                    ventas.actualizarVentaEstado(lblIdVenta.getText(), venta);
                    BuscarVenta();
                    CrearTabla();
                    restablecerCantidades();
                    /*ejecutamos los metodos*/
                    capturecodproducto();
                    mostrarventanaventa();

                } else {
                    JOptionPane.showMessageDialog(null, "¡Se debe seleccionar un registro de venta!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "¡Esta venta ya ha sido ANULADA!");
            }

        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnularVenta;
    private javax.swing.JButton btnVerDetalle;
    private javax.swing.JButton btneditar;
    private javax.swing.JComboBox cboTipoDocumento;
    private com.toedter.calendar.JDateChooser dcFechafin;
    private com.toedter.calendar.JDateChooser dcFechaini;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblIdVenta;
    public static javax.swing.JLabel lblidvendedor;
    public static javax.swing.JLabel lblvendedor;
    private javax.swing.JTable tblDetalleVenta;
    private javax.swing.JTable tblVenta;
    // End of variables declaration//GEN-END:variables
}
