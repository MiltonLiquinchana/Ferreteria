/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultas;

import Conexion.ClsConexion;
import Entidad.ClsEntidadCompra;
import Entidad.ClsEntidadDetalleCompra;
import Entidad.ClsEntidadProducto;
import Entidad.EntidadLote;
import Negocio.ClsDetalleVenta;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/*librerias necesarias para implimir*/
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;
import java.io.File;
import java.sql.Connection;

/**
 *
 * @author liqui
 */
public class FrmLoteInternal extends javax.swing.JInternalFrame {

    /*esta conexion la utilizaremos para poder usarla con jasper*/
    private Connection connection = new ClsConexion().getConection();
    /*cremos los objetos a las clases necesarias para la consulta*/
    ClsEntidadProducto producto;
    EntidadLote lote;
    ClsEntidadDetalleCompra detallecompra;
    ClsEntidadCompra compra;
    /*objeto para poder ejecutar el metodo en la cual se encuentra la consulta que queremos*/
    ClsDetalleVenta CRUD = new ClsDetalleVenta();
    /*no permite saber si el lote esta caducado o no*/
    String stadolote = null;
    /*formato de fehcha para la comprovacion de fechas*/
    String format = new String("dd/MM/yyyy");
    SimpleDateFormat formato = new SimpleDateFormat(format);
    String formatdos = new String("yyyy-MM-dd");
    SimpleDateFormat formatodos = new SimpleDateFormat(formatdos);
    /*esto lo agregamos para poder hacer busquedas dentro de la tabla*/
    TableRowSorter rowsorter;
    int indice = 6;
    /*creamos un nuevo modelo de para la tabla*/
    DefaultTableModel modelo;
    String criterio = "listar";
    //converion de fecha obtenida a string con formato yyyy-MM-dd
    SimpleDateFormat formatotres = new SimpleDateFormat("yyyy-MM-dd");
    String fechaini, fechafin;

    public FrmLoteInternal() {
        initComponents();

        /*inicialisamos los valores de los calendarios*/
        Date datefecha = new Date();
        dcFechaini.setDate(datefecha);
        dcFechafin.setDate(datefecha);
        /*con esto hasociamos el reder de colores que tendra la tabla*/
        this.tablalote.setDefaultRenderer(Object.class, new Entidad.RenderModelColor());
        /*llenamos la tabla*/
        llenartabla(tablalote);
        /*agregamos el metodo que nos detenca los eventos del calendario*/
        calendario();

    }

    /*metodo que no sirve para llenar la tabla desde la base de datos, este metodo recive la tabla a llenar*/
    void llenartabla(JTable tabla) {

        modelo = new DefaultTableModel();
        /*en un arreglo de tipo string definimos los encabezados de cada columna*/
        String titulos[] = {"CODIGO PRODUCTO", "NOMBRE PRODUCTO", "LOTE", "FECHA COMPRA", "FECHA CADUCIDAD", "CANTIDAD", "ESTADO"};
        /*agregamos los encabezados al modelo de la tabla*/
        modelo.setColumnIdentifiers(titulos);
        /*variable para verificacion de consulta*/
        boolean verificacionconsulta = false;
        fechaini = formatotres.format(dcFechaini.getDate());
        fechafin = formatotres.format(dcFechafin.getDate());
        /*codicionales para saber que tipo de busqueda se va a hacer*/
        if (CRUD.listarlotes(producto, lote, compra, criterio, fechaini, fechafin)) {//en caso de que se tenga que listar todo, el filtro se ara dependiedo del parametro rowsorter
            verificacionconsulta = true;
        } else {
            verificacionconsulta = false;
        }

        /*aqui le decimos que si la ejecucion del metodo nos debuelve veradero*/
        if (verificacionconsulta == true) {
            /*primero creamos cuatro listas las cuales reciviran los valores que tienen las listas de la clase ClsDetalleVenta*/
            ArrayList<ClsEntidadProducto> listaproducto = null;//antes de recivir valores en las listas las limpiamos
            ArrayList<EntidadLote> listalote = null;
            ArrayList<ClsEntidadCompra> listacompra = null;
            /*igualamos los valores de las listas*/
            listaproducto = CRUD.listaproducto;
            listalote = CRUD.listalote;
            listacompra = CRUD.listacompra;
            /*creamos una vaiable de tipo entera para almacenar el numero de registros que tenga cualquiera de las lista, esto con el proposito
            de que se pueda rrecorer entre los datos de la lista y se vayan agregando al modelo*/
            int tamanio = CRUD.listalote.size();
            int recorrer = 0;/*esta variable aumenta dependientdo de cuantas veces recorra la lista*/
            limpiartabla();//Limpiamos la tabla antes de rellenar los datos
            /*con el tamanio de la lista recorremos entre los datos con la ayudad de un while o un for*/
            while (recorrer < tamanio) {
                /*primero hacemos una comprovacion de fechas para saber si esta caducado,por caducar, pendiente*/
 /*variable necesarias para la comprovacion*/
                Date fechaactual = new Date();
                /*damos el formato de fecha a utilizar*/
                Date fechaconformatolote = null;
                String fechalote = null;
                /*primero convertimos la fecha de tipo date a string con el formato*/
                fechalote = listalote.get(recorrer).getFecha_caducidad();;

                try {
                    /*aqui convermimos de string a tipo date pero con el formato de fecha de yyyy/mm/dd*/
                    fechaconformatolote = formatodos.parse(fechalote);
                } catch (ParseException ex) {
                    Logger.getLogger(FrmLoteInternal.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (fechaactual.after(fechaconformatolote)) {
                    /*caducado*/
                    stadolote = "CADUCADO";
                } else if (fechaactual.before(fechaconformatolote)) {
                    /*hacemos otra comprobacion*/
                    if (fechaactual.after(restardias(fechaconformatolote)) && fechaactual.before(fechaconformatolote)) {
                        /*por caducar*/
                        stadolote = "POR CADUCAR";
                    } else if (fechaactual.before(restardias(fechaconformatolote))) {
                        /*pendiente*/
                        stadolote = "PENDIENTE";
                    }
                }
                /*agregamos los datos de las listas a un arreglo de tipo string*/

                String datos[] = new String[7];
                datos[0] = listaproducto.get(recorrer).getStrCodigoProducto();
                datos[1] = listaproducto.get(recorrer).getStrNombreProducto();
                datos[2] = listalote.get(recorrer).getCodigo();
                datos[3] = String.valueOf(listacompra.get(recorrer).getFechacompra());
                datos[4] = String.valueOf(listalote.get(recorrer).getFecha_caducidad());
                datos[5] = String.valueOf(listalote.get(recorrer).getCantidadcompra());
                datos[6] = stadolote;

                /*agregamos los datos al modelo de la tabla*/
                modelo.addRow(datos);

                recorrer++;
            }
            rowsorter = new TableRowSorter(modelo);
            tabla.setRowSorter(rowsorter);
            /*le asignamos el modelo creado a la tabla*/
            tabla.setModel(modelo);
        } else {
            JOptionPane.showMessageDialog(null, "No se a podido obtener registros desde la base de datos");

        }

    }

    public Date restardias(Date fechaloteformateado) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaloteformateado);
        calendar.add(Calendar.DAY_OF_YEAR, -10);
        /*fecha a devolver*/
        Date fechadevolver = null;
        String fechaformato;
        fechaformato = formato.format(calendar.getTime());
        try {
            fechadevolver = formato.parse(fechaformato);
        } catch (ParseException ex) {
            Logger.getLogger(FrmLoteInternal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fechadevolver;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablalote = new javax.swing.JTable();
        chkbcaducado = new javax.swing.JCheckBox();
        chkbporcaducar = new javax.swing.JCheckBox();
        chkbpendiente = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        dcFechaini = new com.toedter.calendar.JDateChooser();
        dcFechafin = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);

        tablalote.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablalote);

        chkbcaducado.setText("Caducados");
        chkbcaducado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkbcaducadoActionPerformed(evt);
            }
        });

        chkbporcaducar.setText("Por caducar");
        chkbporcaducar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkbporcaducarActionPerformed(evt);
            }
        });

        chkbpendiente.setText("Pendiente");
        chkbpendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkbpendienteActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/printer.png"))); // NOI18N
        jButton1.setText("Imprimir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("DESDE:");

        dcFechaini.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        dcFechafin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("HASTA:");

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(dcFechaini, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(dcFechafin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(chkbcaducado)
                                .addGap(18, 18, 18)
                                .addComponent(chkbporcaducar)
                                .addGap(18, 18, 18)
                                .addComponent(chkbpendiente)
                                .addGap(18, 18, 18))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jButton1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkbcaducado)
                            .addComponent(chkbporcaducar)
                            .addComponent(chkbpendiente)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechaini, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechafin, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkbcaducadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkbcaducadoActionPerformed
        filtrarlotes();
    }//GEN-LAST:event_chkbcaducadoActionPerformed

    private void chkbpendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkbpendienteActionPerformed
        filtrarlotes();
    }//GEN-LAST:event_chkbpendienteActionPerformed

    private void chkbporcaducarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkbporcaducarActionPerformed
        filtrarlotes();
    }//GEN-LAST:event_chkbporcaducarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        imprimir();
    }//GEN-LAST:event_jButton1ActionPerformed
    private void imprimir() {
        Map p = new HashMap();
        p.put("criteriobusqueda", criterio);
        p.put("fechaini", fechaini);
        p.put("fechafin", fechafin);
        JasperReport report;
        JasperPrint print;

        try {
            report = JasperCompileManager.compileReport(new File("").getAbsolutePath() + "/src/Reportes/Lotes.jrxml");
            print = JasperFillManager.fillReport(report, p, connection);
            JasperViewer view = new JasperViewer(print, false);
            view.setTitle("Reporte de Lotes");
            view.setVisible(true);
        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    private void filtrarlotes() {
        /*variables que nos sirven para saber que tipo de consulta se ejecutara*/
        String caducado, porcaducar, pendiente, caducadoporcaducar, caducadopendiente, porCaducarPendiente;
        caducado = "CADUCADO";
        porcaducar = "POR CADUCAR";
        pendiente = "PENDIENTE";
        caducadoporcaducar = "CaducadoporCaducar";
        caducadopendiente = "CaducadoPendiente";
        porCaducarPendiente = "PorCaducarPendiente";
        if (chkbcaducado.isSelected() && !chkbporcaducar.isSelected() && !chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(null, "caducado");  
            criterio = caducado;
            llenartabla(tablalote);
            rowsorter.setRowFilter(RowFilter.regexFilter(caducado.toUpperCase(), indice));
        }
        if (chkbporcaducar.isSelected() && !chkbcaducado.isSelected() && !chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(null, "por caducar");
            criterio = porcaducar;
            llenartabla(tablalote);
            rowsorter.setRowFilter(RowFilter.regexFilter(porcaducar.toUpperCase(), indice));
        }
        if (chkbpendiente.isSelected() && !chkbcaducado.isSelected() && !chkbporcaducar.isSelected()) {
            //JOptionPane.showMessageDialog(null, "pendiente");
            criterio = pendiente;
            llenartabla(tablalote);
            rowsorter.setRowFilter(RowFilter.regexFilter(pendiente.toUpperCase(), indice));
        }
        /*verificaciones dobles*/
        if (chkbcaducado.isSelected() && chkbporcaducar.isSelected() && !chkbpendiente.isSelected()) {
            /*creamos un nuevo modelo de para la tabla*/
            //JOptionPane.showMessageDialog(this, "caducado y por caducar");
            criterio = caducadoporcaducar;
            llenartabla(tablalote);
        }
        if (chkbcaducado.isSelected() && chkbpendiente.isSelected() && !chkbporcaducar.isSelected()) {
            //JOptionPane.showMessageDialog(this, "caducado y pendiente");
            criterio = caducadopendiente;
            llenartabla(tablalote);
        }
        if (chkbporcaducar.isSelected() && chkbpendiente.isSelected() && !chkbcaducado.isSelected()) {
            //JOptionPane.showMessageDialog(this, "por caducar y pendiente");
            criterio = porCaducarPendiente;
            llenartabla(tablalote);
        }
        if (chkbcaducado.isSelected() && chkbporcaducar.isSelected() && chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(this, "caducado, por caducar, y pendiente");
            criterio = "listar";
            llenartabla(tablalote);
        }
        if (!chkbcaducado.isSelected() && !chkbporcaducar.isSelected() && !chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(this, "caducado, por caducar, y pendiente");
            criterio = "listar";
            llenartabla(tablalote);
        }

    }

    /*metodo que sirve para bucar dentro de la fechaseleccionada*/
    public void calendario() {

        dcFechaini.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                llenartabla(tablalote);
            }
        });
        dcFechafin.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                llenartabla(tablalote);
            }
        });

    }

    /*este metodo nos sirve para limpiar la tabla*/
    public void limpiartabla() {
        /*declaramos variables e inicialisamos*/
        int contador = 0;
        int numfilas;
        /*capturamos el numero de filas que tiene el modelo de la tabla*/
        numfilas = modelo.getRowCount();
        /*decimos que mientras el numero de filas sea mayor al contador*/
        while (numfilas > contador) {
            /*entonces nos remueva lo que esta en la pocicion0*/
            modelo.removeRow(0);
            /*sumamos mas uno al contador*/
            contador++;
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkbcaducado;
    private javax.swing.JCheckBox chkbpendiente;
    private javax.swing.JCheckBox chkbporcaducar;
    private com.toedter.calendar.JDateChooser dcFechafin;
    private com.toedter.calendar.JDateChooser dcFechaini;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablalote;
    // End of variables declaration//GEN-END:variables
}
