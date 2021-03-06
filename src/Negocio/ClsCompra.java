/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Negocio;

import Conexion.*;
import Entidad.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;

public class ClsCompra {

    private Connection connection = new ClsConexion().getConection();
    /*variable de tipo boleana para saber si se guardo o no los datos*/
    boolean trueandfalse, trueandfalselote;
//--------------------------------------------------------------------------------------------------
//-----------------------------------------METODOS--------------------------------------------------
//-------------------------------------------------------------------------------------------------- 

    public boolean agregarCompra(ClsEntidadCompra Compra) {
        try {
            CallableStatement statement = connection.prepareCall("{call SP_I_Compra(?,?,?,?,?,?,?,?,?)}");
            statement.setString("pidtipodocumento", Compra.getStrIdTipoDocumento());
            statement.setString("pidproveedor", Compra.getStrIdProveedor());
            statement.setString("pidempleado", Compra.getStrIdEmpleado());
            statement.setString("pnumero", Compra.getStrNumeroCompra());
            statement.setDate("pfecha", new java.sql.Date(Compra.getStrFechaCompra().getTime()));
            statement.setString("psubtotal", Compra.getStrSubTotalCompra());
            statement.setString("pigv", Compra.getStrIgvCompra());
            statement.setString("ptotal", Compra.getStrTotalCompra());
            statement.setString("pestado", Compra.getStrEstadoCompra());
            trueandfalse = statement.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return trueandfalse;
    }

    public void modificarCompra(String codigo, ClsEntidadCompra Compra) {
        try {
            CallableStatement statement = connection.prepareCall("{call SP_U_Compra(?,?,?,?,?,?,?,?,?,?)}");
            statement.setString("pidcompra", codigo);
            statement.setString("pidtipodocumento", Compra.getStrIdTipoDocumento());
            statement.setString("pidproveedor", Compra.getStrIdProveedor());
            statement.setString("pidempleado", Compra.getStrIdEmpleado());
            statement.setString("pnumero", Compra.getStrNumeroCompra());
            statement.setDate("pfecha", new java.sql.Date(Compra.getStrFechaCompra().getTime()));
            statement.setString("psubtotal", Compra.getStrSubTotalCompra());
            statement.setString("pigv", Compra.getStrIgvCompra());
            statement.setString("ptotal", Compra.getStrTotalCompra());
            statement.setString("pestado", Compra.getStrEstadoCompra());
            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "¡Compra Actualizada!", "Mensaje del Sistema", 1);
    }

    public ArrayList<ClsEntidadCompra> listarCompra() {
        ArrayList<ClsEntidadCompra> compras = new ArrayList<ClsEntidadCompra>();
        try {
            CallableStatement statement = connection.prepareCall("{call SP_S_Compra}");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ClsEntidadCompra compra = new ClsEntidadCompra();
                compra.setStrIdCompra(resultSet.getString("IdCompra"));
                compra.setStrTipoDocumento(resultSet.getString("TipoDocumento"));
                compra.setStrProveedor(resultSet.getString("Proveedor"));
                compra.setStrEmpleado(resultSet.getString("Empleado"));
                compra.setStrNumeroCompra(resultSet.getString("Numero"));
                compra.setStrFechaCompra(resultSet.getDate("Fecha"));
                compra.setStrSubTotalCompra(resultSet.getString("SubTotal"));
                compra.setStrIgvCompra(resultSet.getString("Igv"));
                compra.setStrTotalCompra(resultSet.getString("Total"));
                compra.setStrEstadoCompra(resultSet.getString("Estado"));

                compras.add(compra);
            }
            return compras;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ResultSet listarCompraPorParametro(String criterio, String busqueda) throws Exception {
        ResultSet rs = null;
        try {
            CallableStatement statement = connection.prepareCall("{call SP_S_CompraPorParametro(?,?)}");
            statement.setString("pcriterio", criterio);
            statement.setString("pbusqueda", busqueda);
            rs = statement.executeQuery();
            return rs;
        } catch (SQLException SQLex) {
            throw SQLex;
        }
    }

    public ResultSet obtenerUltimoIdCompra() throws Exception {
        ResultSet rs = null;
        try {
            CallableStatement statement = connection.prepareCall("{call SP_S_UltimoIdCompra()}");
            rs = statement.executeQuery();
            return rs;
        } catch (SQLException SQLex) {
            throw SQLex;
        }
    }

    public ResultSet listarCompraPorFecha(String criterio, Date fechaini, Date fechafin, String doc) throws Exception {
        ResultSet rs = null;
        try {
            CallableStatement statement = connection.prepareCall("{call SP_S_CompraPorFecha(?,?,?,?)}");
            statement.setString("pcriterio", criterio);
            statement.setDate("pfechaini", new java.sql.Date(fechaini.getTime()));
            statement.setDate("pfechafin", new java.sql.Date(fechafin.getTime()));
            statement.setString("pdocumento", doc);
            rs = statement.executeQuery();
            return rs;
        } catch (SQLException SQLex) {
            throw SQLex;
        }
    }

    public void actualizarCompraEstado(String codigo, ClsEntidadCompra Compra) {
        try {
            CallableStatement statement = connection.prepareCall("{call SP_U_ActualizarCompraEstado(?,?)}");
            statement.setString("pidcompra", codigo);
            statement.setString("pestado", Compra.getStrEstadoCompra());
            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "¡Compra Anulada!", "Mensaje del Sistema", 1);
    }

    public ResultSet listarCompraPorDetalle(String criterio, Date fechaini, Date fechafin) throws Exception {
        ResultSet rs = null;
        try {
            CallableStatement statement = connection.prepareCall("{call SP_S_CompraPorDetalle(?,?,?)}");
            statement.setString("pcriterio", criterio);
            statement.setDate("pfechaini", new java.sql.Date(fechaini.getTime()));
            statement.setDate("pfechafin", new java.sql.Date(fechafin.getTime()));
            rs = statement.executeQuery();
            return rs;
        } catch (SQLException SQLex) {
            throw SQLex;
        }
    }

    public boolean agregarlote(String codigo, String fechacaducidad, int cantidad, int Idproducto) {
        CallableStatement call = null;
        try {
            call = connection.prepareCall("{call guardarlote(?,?,?,?)}");
            call.setString("codig", codigo);
            call.setString("fecha_Caducidad", fechacaducidad);
            call.setInt("Idproduct", Idproducto);
            call.setInt("cantidadcomprado", cantidad);   
            trueandfalselote = call.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se a podido guardar el lote");
        }
        return trueandfalselote;
    }

    public boolean listarultimoidlote(EntidadLote lote) {
        CallableStatement call = null;
        ResultSet result = null;
        try {
            call = connection.prepareCall("{call maxidlote}");
            trueandfalselote = call.execute();
            result = call.getResultSet();
            if (result.next()) {
                lote.setIdLote(result.getInt("IdLote"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se a podido listar el lote");
        }
        return trueandfalselote;

    }
}
