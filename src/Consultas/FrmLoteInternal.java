/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultas;

import Entidad.ClsEntidadCompra;
import Entidad.ClsEntidadDetalleCompra;
import Entidad.ClsEntidadLote;
import Entidad.ClsEntidadProducto;
import Negocio.ClsDetalleVenta;
import java.awt.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author liqui
 */
public class FrmLoteInternal extends javax.swing.JInternalFrame {

    /*cremos los objetos a las clases necesarias para la consulta*/
    ClsEntidadProducto producto;
    ClsEntidadLote lote;
    ClsEntidadDetalleCompra detallecompra;
    ClsEntidadCompra compra;
    /*objeto para poder ejecutar el metodo en la cual se encuentra la consulta que queremos*/
    ClsDetalleVenta CRUD = new ClsDetalleVenta();
    /*no permite saber si el lote esta caducado o no*/
    String stadolote = null;
    /*formato de fehcha para la comprovacion de fechas*/
    String format = new String("dd/MM/yyyy");
    SimpleDateFormat formato = new SimpleDateFormat(format);
    /*esto lo agregamos para poder hacer busquedas dentro de la tabla*/
    TableRowSorter rowsorter;
    int indice = 6;
    /*creamos un nuevo modelo de para la tabla*/
    DefaultTableModel modelo;

    public FrmLoteInternal() {
        initComponents();
        /*con esto hasociamos el reder de colores que tendra la tabla*/
        this.tablalote.setDefaultRenderer(Object.class, new Entidad.RenderModelColor());
        llenartabla(tablalote, "listar");
    }

    /*metodo que no sirve para llenar la tabla desde la base de datos, este metodo recive la tabla a llenar*/
    void llenartabla(JTable tabla, String busqueda) {

        modelo = new DefaultTableModel();
        /*en un arreglo de tipo string definimos los encabezados de cada columna*/
        String titulos[] = {"CODIGO PRODUCTO", "NOMBRE PRODUCTO", "LOTE", "FECHA COMPRA", "FECHA CADUCIDAD", "CANTIDAD", "ESTADO"};
        /*agregamos los encabezados al modelo de la tabla*/
        modelo.setColumnIdentifiers(titulos);
        /*variable para verificacion de consulta*/
        boolean verificacionconsulta = false;
        /*codicionales para saber que tipo de busqueda se va a hacer*/
        if (busqueda.equals("CaducadoporCaducar")) {//en caso de ser para caducado y por caducar
            if (CRUD.listarlotescaducadopordacucar(producto, lote, compra)) {
                verificacionconsulta = true;
            } else {
                verificacionconsulta = false;
            }
        } else if (busqueda.equals("CaducadoPendiente")) {//en caso de ser para caducado y pendiente
            if (CRUD.listarlotescaducadopendiente(producto, lote, compra)) {
                verificacionconsulta = true;
            } else {
                verificacionconsulta = false;
            }

        } else if (busqueda.equals("PorCaducarPendiente")) {//en caso de ser por caducar y pendiente
            if (CRUD.listarlotesporcaducarpendiente(producto, lote, compra)) {
                verificacionconsulta = true;
            } else {
                verificacionconsulta = false;
            }
        } else {
            if (CRUD.listarlotes(producto, lote, compra)) {//en caso de que se tenga que listar todo, el filtro se ara dependiedo del parametro rowsorter
                verificacionconsulta = true;
            } else {
                verificacionconsulta = false;
            }
        }

        /*aqui le decimos que si la ejecucion del metodo nos debuelve veradero*/
        if (verificacionconsulta == true) {
            /*primero creamos cuatro listas las cuales reciviran los valores que tienen las listas de la clase ClsDetalleVenta*/
            ArrayList<ClsEntidadProducto> listaproducto = null;//antes de recivir valores en las listas las limpiamos
            ArrayList<ClsEntidadLote> listalote = null;
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
                Date fechaconformatolote = null, fechaconformatoactual = null;
                String fechalote, fechafactual;
                /*primero convertimos la fecha de tipo date a string con el formato*/
                fechalote = formato.format(listalote.get(recorrer).getFecha_caducidad());
                fechafactual = formato.format(fechaactual);

                try {
                    /*aqui convermimos de string a tipo date pero con el formato de fecha de yyyy/mm/dd*/
                    fechaconformatolote = formato.parse(fechalote);
                    fechaconformatoactual = formato.parse(fechafactual);
                } catch (ParseException ex) {
                    Logger.getLogger(FrmLoteInternal.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (fechaconformatoactual.after(fechaconformatolote)) {
                    /*caducado*/
                    stadolote = "CADUCADO";
                } else if (fechaconformatoactual.before(fechaconformatolote)) {
                    /*hacemos otra comprobacion*/
                    if (fechaconformatoactual.after(restardias(fechaconformatolote)) && fechaconformatoactual.before(fechaconformatolote)) {
                        /*por caducar*/
                        stadolote = "POR CADUCAR";
                    } else if (fechaconformatoactual.before(restardias(fechaconformatolote))) {
                        /*pendiente*/
                        stadolote = "PENDIENTE";
                    }
                }
                /*agregamos los datos de las listas a un arreglo de tipo string*/

                String datos[] = new String[7];
                datos[0] = listaproducto.get(recorrer).getStrCodigoProducto();
                datos[1] = listaproducto.get(recorrer).getStrNombreProducto();
                datos[2] = listalote.get(recorrer).getCodigo();
                datos[3] = String.valueOf(listacompra.get(recorrer).getStrFechaCompra());
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(chkbcaducado)
                        .addGap(18, 18, 18)
                        .addComponent(chkbporcaducar)
                        .addGap(18, 18, 18)
                        .addComponent(chkbpendiente)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkbcaducado)
                    .addComponent(chkbporcaducar)
                    .addComponent(chkbpendiente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
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
            llenartabla(tablalote, "listar");
            rowsorter.setRowFilter(RowFilter.regexFilter(caducado.toUpperCase(), indice));
        }
        if (chkbporcaducar.isSelected() && !chkbcaducado.isSelected() && !chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(null, "por caducar");
            llenartabla(tablalote, "listar");
            rowsorter.setRowFilter(RowFilter.regexFilter(porcaducar.toUpperCase(), indice));
        }
        if (chkbpendiente.isSelected() && !chkbcaducado.isSelected() && !chkbporcaducar.isSelected()) {
            //JOptionPane.showMessageDialog(null, "pendiente");
            llenartabla(tablalote, "listar");
            rowsorter.setRowFilter(RowFilter.regexFilter(pendiente.toUpperCase(), indice));
        }
        /*verificaciones dobles*/
        if (chkbcaducado.isSelected() && chkbporcaducar.isSelected() && !chkbpendiente.isSelected()) {
            /*creamos un nuevo modelo de para la tabla*/
            //JOptionPane.showMessageDialog(this, "caducado y por caducar");
            llenartabla(tablalote, caducadoporcaducar);
        }
        if (chkbcaducado.isSelected() && chkbpendiente.isSelected() && !chkbporcaducar.isSelected()) {
            //JOptionPane.showMessageDialog(this, "caducado y pendiente");
            llenartabla(tablalote, caducadopendiente);
        }
        if (chkbporcaducar.isSelected() && chkbpendiente.isSelected() && !chkbcaducado.isSelected()) {
            //JOptionPane.showMessageDialog(this, "por caducar y pendiente");
            llenartabla(tablalote, porCaducarPendiente);
        }
        if (chkbcaducado.isSelected() && chkbporcaducar.isSelected() && chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(this, "caducado, por caducar, y pendiente");
            llenartabla(tablalote, "listar");
        }
        if (!chkbcaducado.isSelected() && !chkbporcaducar.isSelected() && !chkbpendiente.isSelected()) {
            //JOptionPane.showMessageDialog(this, "caducado, por caducar, y pendiente");
            llenartabla(tablalote, "listar");
        }

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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablalote;
    // End of variables declaration//GEN-END:variables
}
