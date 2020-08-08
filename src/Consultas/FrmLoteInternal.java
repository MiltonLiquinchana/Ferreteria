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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

    public FrmLoteInternal() {
        initComponents();
        /*con esto hasociamos el reder de colores que tendra la tabla*/
        this.tablalote.setDefaultRenderer(Object.class, new Entidad.RenderModelColor());
        llenartabla(tablalote);
    }

    /*metodo que no sirve para llenar la tabla desde la base de datos, este metodo recive la tabla a llenar*/
    void llenartabla(JTable tabla) {
        /*creamos un nuevo modelo de para la tabla*/
        DefaultTableModel modelo = new DefaultTableModel();
        /*en un arreglo de tipo string definimos los encabezados de cada columna*/
        String titulos[] = {"CODIGO PRODUCTO", "NOMBRE PRODUCTO", "LOTE", "FECHA COMPRA", "FECHA CADUCIDAD", "CANTIDAD", "ESTADO"};
        /*agregamos los encabezados al modelo de la tabla*/
        modelo.setColumnIdentifiers(titulos);
        /*aqui le decimos que si la ejecucion del metodo nos debuelve veradero*/
        if (CRUD.listarlotes(producto, lote, compra)) {
            /*primero creamos cuatro listas las cuales reciviran los valores que tienen las listas de la clase ClsDetalleVenta*/
            ArrayList<ClsEntidadProducto> listaproducto;
            ArrayList<ClsEntidadLote> listalote;
            ArrayList<ClsEntidadCompra> listacompra;
            /*igualamos los valores de las listas*/
            listaproducto = CRUD.listaproducto;
            listalote = CRUD.listalote;
            listacompra = CRUD.listacompra;

            /*creamos una vaiable de tipo entera para almacenar el numero de registros que tenga cualquiera de las lista, esto con el proposito
            de que se pueda rrecorer entre los datos de la lista y se vayan agregando al modelo*/
            int tamanio = CRUD.listalote.size();
            int recorrer = 0;/*esta variable aumenta dependientdo de cuantas veces recorra la lista*/
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
                    Logger.getLogger(FrmLote.class.getName()).log(Level.SEVERE, null, ex);
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
                String datos[] = {
                    listaproducto.get(recorrer).getStrCodigoProducto(),
                    listaproducto.get(recorrer).getStrNombreProducto(),
                    listalote.get(recorrer).getCodigo(),
                    String.valueOf(listacompra.get(recorrer).getStrFechaCompra()),
                    String.valueOf(listalote.get(recorrer).getFecha_caducidad()),
                    String.valueOf(listalote.get(recorrer).getCantidadcompra()),
                    stadolote
                };
                /*agregamos los datos al modelo de la tabla*/
                modelo.addRow(datos);

                recorrer++;
            }
            /*le asignamos el modelo creado a la tabla*/
            tabla.setModel(modelo);
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
            Logger.getLogger(FrmLote.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fechadevolver;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablalote = new javax.swing.JTable();

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablalote;
    // End of variables declaration//GEN-END:variables
}
