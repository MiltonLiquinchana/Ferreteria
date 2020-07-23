/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidad;

/**
 *
 * @author DAYPER PERU
 */
public class ClsEntidadDetalleCompra {
    private String strIdCompra;
    private String strIdProducto;
    private String strCantidadDet;
    private String strPrecioDet;
    private String strTotalDet;    
    private String strIva;
    public String getStrIdCompra() {
        return strIdCompra;
    }

    public void setStrIdCompra(String strIdCompra) {
        this.strIdCompra = strIdCompra;
    }
    
    public String getStrIva() {
        return strIva;
    }

    public void setStrIva(String strIva) {
        this.strIva = strIva;
    }

    public String getStrIdProducto() {
        return strIdProducto;
    }

    public void setStrIdProducto(String strIdProducto) {
        this.strIdProducto = strIdProducto;
    }

    public String getStrCantidadDet() {
        return strCantidadDet;
    }

    public void setStrCantidadDet(String strCantidadDet) {
        this.strCantidadDet = strCantidadDet;
    }

    public String getStrPrecioDet() {
        return strPrecioDet;
    }

    public void setStrPrecioDet(String strPrecioDet) {
        this.strPrecioDet = strPrecioDet;
    }

    public String getStrTotalDet() {
        return strTotalDet;
    }

    public void setStrTotalDet(String strTotalDet) {
        this.strTotalDet = strTotalDet;
    }

}
