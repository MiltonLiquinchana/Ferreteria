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

public class ClsDetalleVenta {

    private Connection connection = new ClsConexion().getConection();
    //--------------------------------------------------------------------------------------------------
/*array list*/
    public ArrayList<ClsEntidadProducto> listaproducto;//antes de recivir valores en las listas las limpiamos
    public ArrayList<EntidadLote> listalote;
    public ArrayList<ClsEntidadCompra> listacompra;
    /*variable tipo bool*/
    boolean hadresult;

    //-----------------------------------------METODOS--------------------------------------------------
    //-------------------------------------------------------------------------------------------------- 
    public void agregarDetalleVenta(ClsEntidadDetalleVenta DetalleVenta) {
        try {
            CallableStatement statement = connection.prepareCall("{call SP_I_DetalleVenta(?,?,?,?,?,?)}");
            statement.setString("pidventa", DetalleVenta.getStrIdVenta());
            statement.setString("pidproducto", DetalleVenta.getStrIdProducto());
            statement.setString("pcantidad", DetalleVenta.getStrCantidadDet());
            statement.setString("pcosto", DetalleVenta.getStrCostoDet());
            statement.setString("pprecio", DetalleVenta.getStrPrecioDet());
            statement.setString("ptotal", DetalleVenta.getStrTotalDet());
            statement.execute();

            //JOptionPane.showMessageDialog(null,"¡Venta Realizada con éxito!","Mensaje del Sistema",1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void modificarDetalleVenta(String codigo, ClsEntidadDetalleVenta DetalleVenta) {
        try {
            CallableStatement statement = connection.prepareCall("{call SP_U_DetalleVenta(?,?,?,?,?,?)}");
            statement.setString("pidventa", codigo);
            statement.setString("pidproducto", DetalleVenta.getStrIdProducto());
            statement.setString("pcantidad", DetalleVenta.getStrCantidadDet());
            statement.setString("pcosto", DetalleVenta.getStrCostoDet());
            statement.setString("pprecio", DetalleVenta.getStrPrecioDet());
            statement.setString("ptotal", DetalleVenta.getStrTotalDet());
            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //JOptionPane.showMessageDialog(null,"¡Venta Actualizada!","Mensaje del Sistema",1);
    }

    public ResultSet listarDetalleVentaPorParametro(String criterio, String busqueda) throws Exception {
        ResultSet rs = null;
        try {
            CallableStatement statement = connection.prepareCall("{call SP_S_DetalleVentaPorParametro(?,?)}");
            statement.setString("pcriterio", criterio);
            statement.setString("pbusqueda", busqueda);
            rs = statement.executeQuery();
            return rs;
        } catch (SQLException SQLex) {
            throw SQLex;
        }
    }

    public boolean listarlotes(ClsEntidadProducto producto, EntidadLote lote, ClsEntidadCompra compra,String criterio) {
        ResultSet rs = null;
        CallableStatement call = null;
        listaproducto = new ArrayList<>();//antes de recivir valores en las listas las limpiamos
        listalote = new ArrayList<>();
        listacompra = new ArrayList<>();
        try {
            call = connection.prepareCall("{call buscarlotecriterio(?)}");
            call.setString("criteriobusqueda", criterio);
            hadresult = call.execute();
            rs = call.getResultSet();
            while (rs.next()) {
                producto=new ClsEntidadProducto();
                lote=new EntidadLote();
                compra=new ClsEntidadCompra();
                producto.setStrCodigoProducto(rs.getString("Codigo"));
                producto.setStrNombreProducto(rs.getString("Nombre"));
                lote.setCodigo(rs.getString("codigo"));
                compra.setFechacompra(rs.getString("Fecha"));
                lote.setFecha_caducidad(rs.getString("fecha_caducidad"));
                lote.setCantidadcompra(rs.getInt("cantidadcompra"));
                listaproducto.add(producto);
                listalote.add(lote);
                listacompra.add(compra);
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return hadresult;
    }

}
