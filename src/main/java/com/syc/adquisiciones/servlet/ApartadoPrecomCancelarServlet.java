package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.documental.CatalogosManager;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ApartadoPrecomCancelarServlet", urlPatterns = { "/servlet/ApartadoPrecomCancelarServlet" })
public class ApartadoPrecomCancelarServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ApartadoPrecomCancelarServlet.class);

    private Connection conn = null;

    private Statement stm;

    private ResultSet rs;

    private String folioGenerator = null;

    private JSONArray jsonArray;

    private JSONObject jsonObj;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("Object: {}", "folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/x-json; charset=ISO-8859-1");
        PrintWriter out = response.getWriter();
        String[] param = request.getParameter("Param").toString().split(",");
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String cEjercicio = param[7];
        String mensaje = "";
        String[][] val = null;
        if (param[3].toUpperCase().equals("APARTADO")) {
            if (param[2].toUpperCase().equals("CONSULTA")) {
                if (param[0].equals("")) {
                    val = getApartados(param);
                } else {
                    if (param[0].indexOf("RC") == 0 || param[0].indexOf("RO") == 0 || param[0].indexOf("RS") == 0)
                        val = getSolicitudesConApartado(param, "'RC','RO','RS'");
                    else if (param[0].indexOf("RM") == 0)
                        val = getSolicitudesConApartado(param, "'RM'");
                }
                out.print(convertMatrizToJSONArray(val).toString());
            } else if (param[2].toUpperCase().equals("CANCELAR")) {
                jsonObj = new JSONObject();
                jsonArray = new JSONArray();
                try {
                    mensaje = cancelarApartado(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (GestionException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            } else if (param[2].toUpperCase().equals("LIBERAR")) {
                jsonObj = new JSONObject();
                jsonArray = new JSONArray();
                try {
                    mensaje = cancelarTodoApartado(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (GestionException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            }
        } else if (param[3].toUpperCase().equals("LINEAS")) {
            if (param[2].toUpperCase().equals("CONSULTA"))
                out.print(convertMatrizToJSONArray(getLineasConApartado(param[0])).toString());
            else if (param[2].toUpperCase().equals("CANCELAR")) {
                try {
                    mensaje = cancelarApartado(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (GestionException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            }
        } else if (param[3].toUpperCase().equals("PRECOMPROMISO")) {
            if (param[2].toUpperCase().equals("CONSULTA"))
                out.print(convertMatrizToJSONArray(getPrecompromisos(param)).toString());
            else if (param[2].toUpperCase().equals("CANCELAR")) {
                jsonObj = new JSONObject();
                jsonArray = new JSONArray();
                try {
                    mensaje = cancelarPrecompromiso(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            } else if (param[2].toUpperCase().equals("LIBERAR")) {
                jsonObj = new JSONObject();
                jsonArray = new JSONArray();
                try {
                    mensaje = cancelarTodoPrecompromisos(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (GestionException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            }
        } else if (param[3].toUpperCase().equals("EP")) {
            if (param[2].toUpperCase().equals("CONSULTA"))
                out.print(convertMatrizToJSONArray(getEP_Precom(param[8])).toString());
        } else if (param[3].toUpperCase().equals("CANCELACIONDEDOCUMENTOS")) {
            jsonObj = new JSONObject();
            jsonArray = new JSONArray();
            try {
                mensaje = cancelacionDeDocumentos(param, usuario, request, response);
            } catch (Exception e) {
                // TODO: handle exception
                log.error("Error occurred", "Error en la cancelación de documentos " + e);
                e.printStackTrace();
                mensaje = "Error en la Cancelación del documento.";
            } finally {
                try {
                    jsonObj.put("mensaje", mensaje);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                out.print(jsonArray.put(jsonObj).toString());
            }
        } else //para consultar los precompromisos esto es pantalla para financiero
        if (param[3].toUpperCase().equals("CONSULTACANCELADOPRECOMPROMISO")) {
            if (param[2].toUpperCase().equals("CONSULTA")) {
                if (param[0].equals("")) {
                    val = getPrecompromisosCompletos(param);
                }
                out.print(convertMatrizToJSONArray(val).toString());
            } else if (param[2].toUpperCase().equals("CANCELAR")) {
                jsonObj = new JSONObject();
                jsonArray = new JSONArray();
                try {
                    //mensaje = cancelarPrecompromisoCompletoFinanciero(param,request,response, usuario, cEjercicio);
                    mensaje = cancelarPrecompromisoAndApartadoCompletoFinanciero(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (GestionException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            }
        } else if (param[3].toUpperCase().equals("CONSULTACANCELAAPARTADO")) {
            ///Cancela apartados
            if (param[2].toUpperCase().equals("CONSULTA")) {
                if (param[0].equals("")) {
                    val = getApartadosCompletos(param);
                }
                //				else{
                //					if(param[0].indexOf("RC") == 0 || param[0].indexOf("RO") == 0 || param[0].indexOf("RS") == 0)
                //						val = getSolicitudesConApartado(param, "'RC','RO','RS'");
                //					else if(param[0].indexOf("RM") == 0)
                //						val = getSolicitudesConApartado(param, "'RM'");
                //				}
                out.print(convertMatrizToJSONArray(val).toString());
            } else if (param[2].toUpperCase().equals("CANCELAR")) {
                jsonObj = new JSONObject();
                jsonArray = new JSONArray();
                try {
                    mensaje = cancelarApartadoCompleto(param, request, response, usuario, cEjercicio);
                } catch (AccountingEngineException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } catch (GestionException e) {
                    mensaje = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        jsonObj.put("mensaje", mensaje);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    out.print(jsonArray.put(jsonObj).toString());
                }
            }
        } else {
            out.print(convertMatrizToJSONArray(val).toString());
        }
    }

    private String[][] getPrecompromisosCompletos(String[] params) throws ServletException {
        String[][] valCom = null;
        String[][] val = null;
        int cont = 0;
        valCom = getPrecompromisosCancelar(params);
        for (int i = 0; i < valCom.length; i++) {
            if (i == 0 && val == null)
                val = new String[valCom.length][valCom[i].length];
            val[cont] = valCom[i];
            cont++;
        }
        return val;
    }

    /////////////////////////////////////////////////////////////
    private String[][] getPrecompromisosCancelar(String[] param) throws ServletException {
        String[][] retVal = null;
        String where = "", query = "", and = "";
        if (!param[4].equals("*") && !param[4].equals("") && !param[4].equals(null))
            where += " and K.cIdUnidadEjecutora = '" + param[4] + "' ";
        if (!param[0].equals(""))
            where += " and K.cIdConsolidado = '" + param[0] + "'";
        if (!param[8].equals("0") && !param[9].equals("0"))
            //where += " and ad.cMes between '"+ param[8]+"' and '"+ param[9]+"'";
            and += " and cMes between '" + param[8] + "' and '" + param[9] + "'";
        //else
        //	where += " and ad.cMes between '01' and '"+ param[8]+"'";
        query = "select DISTINCT '<input type=''checkbox'' id=''folioPrecompromiso_'+CONVERT(varchar,K.ConsecutivoPRECOMP)+''' " + " name=''folioPrecompromiso_'' value='''+CONVERT(varchar,K.cIdConsolidado)+'''/>', k.importe,k.cIdConsolidado,k.C_FOLIO_PRE,K.ConsecutivoPRECOMP,K.cIdUnidadEjecutora,K.cDescripcion            from ( " + " select distinct mc.cIdConsolidado,mc.C_FOLIO_PRE,mc.ConsecutivoPRECOMP,SUM(md.mImporte)as importe,mc.cIdUnidadEjecutora,mc.cDescripcion from mConsolidado mc with(nolock),tPrecomMaterialesEncabezado me with(nolock),tPrecomMaterialesDetalle md with(nolock) " + " where mc.ConsecutivoPRECOMP=me.nFolioPrecomMateriales " + " and md.nFolioPrecomMateriales=me.nFolioPrecomMateriales " + " and me.cDocumentoHaplicado='S' " + " and mc.nIdEstado=2 " + and + " and mc.cIdConsolidado not in( " + " select distinct mc.cIdConsolidado from mConsolidado mc with(nolock) " + " inner join mProcedimiento mp with(nolock) " + " on mc.cIdConsolidado=mp.cIdConsolidado " + " inner join mPedido p with(nolock) " + " on p.cIdProcedimiento=mp.cIdProcedimiento " + " and p.nIdEstado in(4,7,6) " + " union " + " select distinct mc.cIdConsolidado from mConsolidado mc with(nolock) " + " inner join mProcedimiento mp with(nolock) " + " on mc.cIdConsolidado=mp.cIdConsolidado " + " inner join mContrato p with(nolock) " + " on p.cIdProcedimiento=mp.cIdProcedimiento " + " and p.nIdEstado in(4,7,6)) " + " group by  mc.cIdConsolidado,mc.C_FOLIO_PRE,mc.ConsecutivoPRECOMP,mc.cIdUnidadEjecutora,mc.cDescripcion " + " union  " + " select distinct mp.cIdProcedimiento,mp.C_FOLIO_PRE,mp.ConsecutivoPRECOMP,SUM(md.mImporte)as importe,c.cIdUnidadEjecutora,c.cDescripcion from mConsolidado c with(nolock),tPrecomMaterialesEncabezado pme with(nolock),mProcedimiento mp with(nolock),tPrecomMaterialesDetalle md with(nolock) " + " where c.cIdConsolidado=mp.cIdConsolidado " + " and mp.cIdProcedimiento=pme.cIdConsolidado " + " and pme.cDocumentoHaplicado='S' " + " and pme.nFolioPrecomMateriales=md.nFolioPrecomMateriales " + " and pme.nFolioPrecomMateriales=mp.ConsecutivoPRECOMP " + " and mp.nIdEstado in(2) " + " and c.nIdEstado in(2) " + and + " and mp.cIdProcedimiento not in( " + " select distinct mp.cIdProcedimiento from mConsolidado mc with(nolock) " + " inner join mProcedimiento mp with(nolock) " + " on mc.cIdConsolidado=mp.cIdConsolidado " + " inner join mPedido p with(nolock) " + " on p.cIdProcedimiento=mp.cIdProcedimiento " + " and p.nIdEstado in(4,7,6) " + " union  " + " select distinct mp.cIdProcedimiento from mConsolidado mc with(nolock) " + " inner join mProcedimiento mp with(nolock) " + " on mc.cIdConsolidado=mp.cIdConsolidado " + " inner join mContrato p with(nolock) " + " on p.cIdProcedimiento=mp.cIdProcedimiento " + " and p.nIdEstado in(4,7,6)) " + " group by  mp.cIdProcedimiento,mp.C_FOLIO_PRE,mp.ConsecutivoPRECOMP,c.cIdUnidadEjecutora,c.cDescripcion " + " union " + " select distinct cIdPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,p.cConceptoPedido from mPedido p with(nolock),tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) " + " where p.nIdEstado not in(4,7,6) " + " and p.cIdTipoProcedimiento in('PR','PL','PN','PC','PS') " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " and pd.cEvento='PRECOM'" + " group by  p.cIdPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cConceptoPedido " + " union " + " select distinct cIdContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,p.cConceptoContrato from mContrato p with(nolock),tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) " + " where p.nIdEstado not in(4,7,6) " + " and p.cIdTipoProcedimiento in('PR','PL','PN','PC','PS')" + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " and pd.cEvento='PRECOM'" + " group by  p.cIdContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cConceptoContrato " + " union " + " select distinct cIdContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,p.cConceptoContrato from mPasivoContrato p with(nolock) ,tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) " + " where p.nIdEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " group by  p.cIdContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cConceptoContrato " + " union " + " select distinct cIdPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,p.cConceptoPedido from mPasivoPedido p with(nolock),tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) " + " where p.nIdEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " group by  p.cIdPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cConceptoPedido " + " union " + " select distinct cIdContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,p.cConceptoContrato from mPlurianualidadContrato p with(nolock) ,tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) " + " where p.nIdEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " group by  p.cIdContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cConceptoContrato " + " union " + " select distinct cIdPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,p.cConceptoPedido from mPlurianualidadPedido p with(nolock) ,tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) " + " where p.nIdEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " group by  p.cIdPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cConceptoPedido " + " union " + " select distinct c.cIdContratoDefinitivo+'-'+'AMP-'+convert(varchar(30),p.nIdConsecutivoAmpliacion),p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,'AMPLIACION CONTRATO '+ p.cIdContrato as cConceptoContrato  from mContratoAmpliacion p with(nolock),tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) ,mContrato c with(nolock) " + " where p.nIdEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " and c.cIdTipoContrato=p.cIdTipoContrato " + " and c.cIdUnidadEjecutora=p.cIdUnidadEjecutora " + " and c.nIdConsecutivo=p.nIdConsecutivo " + " and c.nIdEstado=4 " + " group by  p.cIdContrato,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora,p.cIdContratoDefinitivo ,c.cIdContratoDefinitivo,p.nIdConsecutivoAmpliacion " + " union " + " select distinct c.cIdPedidoDefinitivo+'-'+'AMP-'+convert(varchar(30),p.nIdConsecutivoAmpliacion),p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,p.cIdUnidadEjecutora,'AMPLIACION PEDIDO '+ p.cIdPedido as cConceptoPedido  from mPedidoAmpliacion p with(nolock),tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock) , mPedido c with(nolock)" + " where p.nIdEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " and c.cIdTipoPedido=p.cIdTipoPedido " + " and c.cIdUnidadEjecutora=p.cIdUnidadEjecutora " + " and c.nIdConsecutivo=p.nIdConsecutivo " + " and c.nIdEstado=4 " + " group by  p.cIdPedido,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,p.cIdUnidadEjecutora ,c.cIdPedidoDefinitivo,p.nIdConsecutivoAmpliacion " + " union " + " select distinct p.cContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,c.cIdUnidadEjecutora,c.cConceptoContrato from mContratoModificado p with(nolock) ,tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock),mContrato c with(nolock)" + " where p.nEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " and c.cIdContrato=p.cIdContrato " + " and c.cIdContratoDefinitivo=p.cIdContratoDefinitivo " + " group by  p.cContratoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,c.cIdUnidadEjecutora,c.cConceptoContrato " + " union " + " select distinct p.cPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,SUM(pd.mImporte)as importe,mp.cIdUnidadEjecutora,mp.cConceptoPedido from mPedidoModificado p with(nolock),tPreCompromisoEncabezado pe with(nolock),tPreCompromisoDetalle pd with(nolock),mpedido mp with(nolock) " + " where p.nEstado=3 " + " and pe.nFolioPreCompromiso=pd.nFolioPreCompromiso " + " and pe.nFolioPreCompromiso=p.ConsecutivoPRECOMP " + " and pe.cDocumentoHaplicado='S' " + and + " and p.cIdPedidoDefinitivo=mp.cIdPedidoDefinitivo " + " and p.cIdPedido=mp.cIdPedido " + " group by  p.cPedidoDefinitivo,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP,mp.cIdUnidadEjecutora,mp.cConceptoPedido)k " + " inner join tMovimiento tm with(nolock) " + " on tm.cFolioDocumentoMovimiento=k.ConsecutivoPRECOMP " + " and tm.cTipoDocumento in('PRECOMPROMISO','PRECOMMATERIALES')" + " where 1=1 " + where + " order by k.cIdConsolidado";
        log.info("Object: {}", "getSolicitudesConApartado:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return retVal;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * SYC 19-03-2015
	 * */
    protected String cancelarPrecompromisoAndApartadoCompletoFinanciero(String[] params, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, SQLException, GestionException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        int errores = 0;
        String resp = "";
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            String tipoPrecom = "PRECOMPROMISO";
            for (int y = 8; y < params.length; y++) {
                rs = stm.executeQuery("select ae.nFolioPrecompromiso,'PRECOM' AS tipoPrecompromiso,ae.cIdContrato documento from tPrecompromisoEncabezado ae with(nolock) " + " where ae.cIdContrato = '" + params[y] + "' and cDocumentoHaplicado = 'S'" + " union " + " select pa.nFolioPrecomMateriales,'PRECOMMAT' AS tipoPrecompromiso,pa.cIdConsolidado documento from tPrecomMaterialesEncabezado pa with(nolock) " + " where pa.cIdConsolidado='" + params[y] + "'" + " and pa.cDocumentoHaplicado='S' ");
                if (rs.next()) {
                    //para que cancele los pagos directos y relaciones de gastos
                    String documento = rs.getString("documento");
                    if ("PRECOMMAT".equalsIgnoreCase(rs.getString("tipoPrecompromiso"))) {
                        tipoPrecom = "PRECOMMATERIALES";
                        if (accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES", rs.getString("nFolioPrecompromiso"), "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales")) {
                            log.info("Busca si el documento ya esta en un pedido o contrato");
                            if (!tienePedCont(documento, conn, tipoPrecom)) {
                                errores++;
                                break;
                            }
                            log.info("Object: {}", "Empieza a cancelar la(s) solicitudes del documento: " + documento);
                            if (!cancelarApartadoCompleto(documento, request, response, usuario, cEjercicio, conn)) {
                                errores++;
                                break;
                            }
                        } else {
                            errores++;
                            break;
                        }
                    } else if (!accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", rs.getString("nFolioPrecompromiso"), "tPrecompromisoEncabezado", "tPrecompromisoDetalle", "nFolioPrecompromiso") || !tienePedCont(documento, conn, tipoPrecom)) {
                        errores++;
                        break;
                    }
                } else
                    resp = "OK:EL PRECOMPROMISO  YA HABIA SIDO CANCELADO.";
            }
            if (errores == 0) {
                resp = "OK:EL PRECOMPROMISO   SE CANCELO CORRECTAMENTE CON SU APARTADO(S).";
                conn.commit();
            } else {
                resp = "ERROR AL CANCELAR EL PRECOMPROMISO Y SU APARTADO";
                conn.rollback();
            }
        } catch (Exception e) {
            // TODO: handle exception
            resp = "ERROR AL CANCELAR EL PRECOMPROMISO Y SU APARTADO";
            conn.rollback();
            log.error("Error occurred", "Error Al liberar el recurso de precompromiso y apartado.\n" + e);
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (stm != null)
                stm.close();
            if (conn != null)
                conn.close();
            stm = null;
            rs = null;
            conn = null;
        }
        return resp;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 31-03-2015
	 * */
    protected boolean tienePedCont(String documento, Connection conn, String tipoPrecom) throws AccountingEngineException, SQLException {
        String query = "";
        ResultSet rs = null;
        Statement stm = null;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        boolean reultado = true;
        String pedCont = "";
        int nFolioPrecom;
        try {
            query = "select p.cIdPedidoDefinitivo, pro.cIdConsolidado,p.cIdProcedimiento,c_folio,ConsecutivoCDIV as idCaso,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP " + "from mPedido AS p with(Nolock) " + "inner join mProcedimiento as pro with(Nolock) on pro.cIdProcedimiento=p.cIdProcedimiento and pro.nIdEstado=2 " + "and '" + documento + "' in(p.cIdProcedimiento,pro.cIdConsolidado,p.cIdPedidoDefinitivo) and p.nIdEstado=3 " + " group by p.cIdPedidoDefinitivo,pro.cIdConsolidado,p.cIdProcedimiento,c_folio,ConsecutivoCDIV,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP  " + "union " + "select p.cIdContratoDefinitivo cIdPedidoDefinitivo,pro.cIdConsolidado,p.cIdProcedimiento,c_folio,ConsecutivoCDIV as idCaso,p.C_FOLIO_PRE,p.ConsecutivoPRECOMP " + "from mContrato AS p with(Nolock) " + "inner join mProcedimiento as pro with(Nolock) on pro.cIdProcedimiento=p.cIdProcedimiento and pro.nIdEstado=2 " + "and '" + documento + "' in(p.cIdProcedimiento,pro.cIdConsolidado,p.cIdContratoDefinitivo) and p.nIdEstado=3 " + " group by p.cIdContratoDefinitivo,pro.cIdConsolidado,p.cIdProcedimiento,c_folio,ConsecutivoCDIV, p.C_FOLIO_PRE,p.ConsecutivoPRECOMP ";
            log.info("Object: {}", query.toString());
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            while (rs.next()) {
                pedCont = rs.getString("cIdPedidoDefinitivo");
                nFolioPrecom = rs.getInt("ConsecutivoPRECOMP");
                if ("PRECOMPROMISO".equals(tipoPrecom)) {
                    reultado = devuelveEstatusPedCont(pedCont, nFolioPrecom, conn);
                    if (!reultado) {
                        log.warn("Object: {}", "No se devolvio el pedCont " + pedCont + " con folio de precompromiso " + nFolioPrecom);
                        return reultado;
                    }
                } else {
                    if (!accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", "" + nFolioPrecom, "tPrecompromisoEncabezado", "tPrecompromisoDetalle", "nFolioPrecompromiso")) {
                        log.warn("Object: {}", "No se cancelo la aplicación contable del documento " + documento + " con folio de precompromiso " + nFolioPrecom);
                        return reultado;
                    }
                    reultado = devuelveEstatusPedCont(pedCont, nFolioPrecom, conn);
                    if (!reultado) {
                        log.warn("Object: {}", "No se devolvio el pedCont " + pedCont + " con folio de precompromiso " + nFolioPrecom);
                        return reultado;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Error occurred", "Error al  buscar el pedido o contrato del documento " + documento);
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (stm != null)
                stm.close();
            rs = null;
            stm = null;
        }
        return reultado;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 06/04/2015
	 * */
    protected boolean devuelveEstatusPedCont(String documento, int nFolioPrecom, Connection conn) throws SQLException {
        boolean result = true;
        CallableStatement csmt = null;
        int outputVal = 0;
        try {
            csmt = conn.prepareCall("{ call pa_mDevuelvePedContrato(?,?,?)}");
            csmt.setString(1, documento);
            csmt.setInt(2, nFolioPrecom);
            csmt.registerOutParameter(3, Types.INTEGER);
            csmt.execute();
            outputVal = csmt.getInt(3);
            log.debug("Object: {}", outputVal);
            if (outputVal == 0) {
                log.info("Object: {}", " EL PEDIDO O CONTRATO " + documento + " SE DEVOLVIO EL ESTATUS CON FOLIO " + nFolioPrecom + "  ");
                result = true;
            } else {
                log.info("Object: {}", " EL PEDIDO O CONTRATO " + documento + " SE NO DEVOLVIO EL ESTATUS CON FOLIO " + nFolioPrecom + "  ");
                result = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            result = false;
            log.error("Error occurred", "Error al devolver el estatus del documento " + documento + "\n " + e);
            e.printStackTrace();
        } finally {
            if (csmt != null)
                csmt.close();
            csmt = null;
        }
        return result;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 20-03-2015
	 * */
    protected boolean cancelarApartadoCompleto(String consProced, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio, Connection conn) throws AccountingEngineException, ServletException, SQLException, GestionException, IOException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        boolean respuesta = true;
        CallableStatement csmt = null;
        try {
            stm = conn.createStatement();
            String folioApartado = "";
            String query = "select distinct ap.nFolioApartado from tApartadoEncabezado  as ap with(Nolock) " + " inner join( select cIdSolicitud from mConsolidadoSolicitud with(Nolock) " + " where cIdConsolidado in('" + consProced + "',(select cIdConsolidado from mProcedimiento with(Nolock) where cIdProcedimiento='" + consProced + "')) " + " ) as sol on sol.cIdSolicitud=ap.cIdSolicitud and ap.cDocumentoHaplicado='S' ";
            rs = stm.executeQuery(query);
            while (rs.next()) {
                folioApartado = rs.getString("nFolioApartado");
                log.info("Object: {}", "Liberando el apartado de la solicitud con folio " + folioApartado);
                if (!accEng.cancelAccountingApplication(conn, "APARTADO", folioApartado, "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado")) {
                    respuesta = false;
                    log.warn("Object: {}", "No se Libera el apartado de la solicitud con folio " + folioApartado);
                }
                csmt = conn.prepareCall("{ call pa_actualizaRequisicionLiberaApartadoFinanciero(?,?)}");
                csmt.setString(1, folioApartado);
                csmt.setString(2, usuario.getLogin());
                csmt.execute();
                log.info("Object: {}", "EL FOLIO" + folioApartado + " DEL APARTADO DE LA SOLICITUD  SE CANCELO CORRECTAMENTE ");
            }
        } catch (SQLException e) {
            log.error("Error occurred", "ERROR AL CANCELAR EL APARTADO DE LA SOLICITUD.\n" + e);
            respuesta = false;
            e.printStackTrace();
        } finally {
            if (csmt != null)
                csmt.close();
            if (rs != null)
                rs.close();
            if (stm != null)
                stm.close();
            csmt = null;
            rs = null;
            stm = null;
        }
        return respuesta;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 26-05-2015
	 * */
    protected boolean cancelaApartado(String folioApartado, Connection conn) {
        boolean respuesta = true;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        try {
            log.info("Object: {}", "Liberando el apartado de la solicitud con folio de apartado " + folioApartado);
            if (!accEng.cancelAccountingApplication(conn, "APARTADO", folioApartado, "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado")) {
                respuesta = false;
                log.warn("Object: {}", "No se Libera el apartado de la solicitud con folio " + folioApartado);
            }
        } catch (Exception e) {
            // TODO: handle exception
            respuesta = false;
            log.error("Error occurred", "Error en la cancelación de Apartado." + e);
            e.printStackTrace();
        }
        return respuesta;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 26-05-2015
	 * */
    protected boolean cancelaPrecomMat(String folioPrecomMat, Connection conn) {
        boolean respuesta = true;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        try {
            log.info("Object: {}", "Liberando el precompromiso materiales con folio " + folioPrecomMat);
            if (!accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES", folioPrecomMat, "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales")) {
                respuesta = false;
                log.warn("Object: {}", "No se Libera el Precompromiso del consolidado con folio " + folioPrecomMat);
            }
        } catch (Exception e) {
            // TODO: handle exception
            respuesta = false;
            log.error("Error occurred", "Error en la cancelación de Precompromiso  materiales." + e);
            e.printStackTrace();
        }
        return respuesta;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 04-06-2015
	 * */
    protected boolean cancelaPrecomMatParcial(String folioPrecomMat, Connection conn) {
        boolean respuesta = true;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        try {
            log.info("Object: {}", "Liberando el precompromiso materiales con folio " + folioPrecomMat);
            if (!accEng.makeAccountingApplication(conn, "PRECOMMATERIALES", folioPrecomMat, "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales")) {
                respuesta = false;
                log.warn("Object: {}", "No se Libera el PrecompromisoMat con folio " + folioPrecomMat);
            }
        } catch (Exception e) {
            // TODO: handle exception
            respuesta = false;
            log.error("Error occurred", "Error en la cancelación de Precompromiso  materiales." + e);
            e.printStackTrace();
        }
        return respuesta;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 01-06-2015
	 * */
    protected boolean cancelaPrecomPedCont(String folioPrecompromiso, Connection conn) {
        boolean respuesta = true;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        try {
            log.info("Object: {}", "Liberando el precompromiso materiales con folio " + folioPrecompromiso);
            if (!accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", "" + folioPrecompromiso, "tPrecompromisoEncabezado", "tPrecompromisoDetalle", "nFolioPrecompromiso")) {
                respuesta = false;
                log.warn("Object: {}", "No se Libera el Precompromiso del PedCont con folio " + folioPrecompromiso);
            }
        } catch (Exception e) {
            // TODO: handle exception
            respuesta = false;
            log.error("Error occurred", "Error en la cancelación de Precompromiso  materiales." + e);
            e.printStackTrace();
        }
        return respuesta;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 28-05-2015
	 * */
    protected boolean cancelaPrecomConsolidado(String[] params, Usuario usuario, Connection conn) throws SQLException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        boolean resp = true;
        String consolidado = "";
        String cadRequisiciones = "";
        String[] requis;
        CallableStatement csmt = null;
        try {
            //Busca si hay precompromiso
            stm = conn.createStatement();
            String folioPrecomMat = "";
            consolidado = params[0];
            cadRequisiciones = params[2].replace(" ", "");
            cadRequisiciones = cadRequisiciones + ";";
            requis = cadRequisiciones.toString().split(";");
            String query = "select nFolioPrecomMateriales from tPrecomMaterialesEncabezado with(Nolock) where cIdConsolidado='" + consolidado + "' and cDocumentoHaplicado='S'";
            log.info("Object: {}", "Query para buscar si hay precomMateriales: " + query);
            rs = stm.executeQuery(query);
            //Cancela precom si tiene
            if (rs.next()) {
                folioPrecomMat = rs.getString("nFolioPrecomMateriales");
                resp = cancelaPrecomMat(folioPrecomMat, conn);
            }
            //Anula el consolidado
            String queryCons = "{ call pa_cancelaLineasConsolidado(?,?,?)}";
            int outputVal = 0;
            csmt = conn.prepareCall(queryCons);
            csmt.setString(1, consolidado);
            csmt.setString(2, usuario.getLogin());
            csmt.registerOutParameter(3, Types.INTEGER);
            csmt.execute();
            outputVal = csmt.getInt(3);
            log.info("Object: {}", "Querys para la anulación del consolidado. " + queryCons);
            if (outputVal != 0) {
                resp = false;
            }
            //cancelado de requis del consolidado
            if (resp && Integer.parseInt(params[9]) == 0) {
                for (int i = 0; i < requis.length; i++) {
                    if (!"".equals(requis[i])) {
                        resp = cancelaApartadoAndDocumento(requis[i].replace(" ", ""), usuario, conn);
                        if (!resp) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            resp = false;
            log.error("Error occurred", "Error al cancelar el precompromiso del consolidado. " + e);
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (csmt != null) {
                csmt.close();
            }
            if (stm != null) {
                stm.close();
            }
            rs = null;
            stm = null;
            csmt = null;
        }
        return resp;
    }

    protected boolean cancelaPedContAprobado(String[] params, Usuario usuario, Connection conn, HttpServletRequest request, HttpServletResponse response) throws SQLException, GestionException, IOException, ServletException, AccountingEngineException {
        boolean resp = false;
        String queryEnc = "", queryL = "", queryPedCont = "";
        Statement stmEnc = null, stmL = null, stmPedCont = null;
        ResultSet rsEnc = null;
        String ueOriginal = usuario.getU_UR();
        String cCentroContableOrig = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        CambiaPropiedadesUsuario cpu = new CambiaPropiedadesUsuario();
        String tipoPedCont = params[0].substring(0, 2);
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(Boolean.TRUE);
        try {
            //cancelar compromiso
            queryEnc = "select det.cCentroContable,enc.cRadicado,enc.cIdContrato,enc.cUnidadResponsable,enc.aEjercicioFiscal " + " from tCompromisoEncabezado as enc with(Nolock) inner join tCompromisoDetalle  as det with(nolock) on det.nFolioCompromiso=enc.nFolioCompromiso and enc.cDocumentoHaplicado='S' " + " where cIdContrato='" + params[0] + "' group by det.cCentroContable,enc.cRadicado,enc.cIdContrato,enc.cUnidadResponsable,enc.aEjercicioFiscal having SUM(mImporte)>0";
            stmEnc = conn.createStatement();
            log.info("Object: {}", queryEnc.toString());
            rsEnc = stmEnc.executeQuery(queryEnc);
            int retval;
            int folio;
            Caso caso = null;
            while (rsEnc.next()) {
                cpu.cambiaCentroContableUE(rsEnc.getString("cUnidadResponsable"), rsEnc.getString("cCentroContable"), usuario);
                //Obtiene un nuevo caso.
                caso = generaGuardaCaso(usuario, request, rsEnc.getString("aEjercicioFiscal"), GestionInterface.IDTC_COMPROMISO);
                folio = Integer.parseInt(caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1));
                //Crea encabezado y detalle
                retval = creaEncDetCompromiso(usuario, conn, rsEnc.getString("cIdContrato"), rsEnc.getString("cRadicado"), rsEnc.getString("aEjercicioFiscal"), folio, request);
                //Aplicación contable
                if (retval == 0) {
                    if (!accEng.makeAccountingApplication(conn, "COMPROMISO", "" + folio, "tCompromisoEncabezado", "tCompromisoDetalle", "nFolioCompromiso")) {
                        log.info("Object: {}", "No se aplico contablemente el compromiso:" + folio);
                        return false;
                    }
                }
                //Avanza caso
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                avanzaCaso(request, caso, usuario, prefixPath, new String[] { "CONSULTA_COMPROMISO" }, new String[] { "consulta_compromiso" });
            }
            //Liberar las partidas.
            if ("PE".equalsIgnoreCase(tipoPedCont)) {
                queryL = "update mSolicitudLineas set cIdEstadoLinea='L' WHERE cIdSolicitud in(select cIdSolicitud from v_lineasPartidaPedido with(Nolock) " + " where cIdPedido=SUBSTRING('" + params[0] + "',1,(LEN('" + params[0] + "')-5)))" + "and nIdLineaSolicitud in(select nIdLineaSolicitud from v_lineasPartidaPedido with(Nolock) where cIdPedido=SUBSTRING('" + params[0] + "',1,(LEN('" + params[0] + "')-5))) ";
                queryPedCont = "update mPedido set nIdEstado=6,cMotivoCancelacion='" + new String(params[8].getBytes("ISO-8859-1"), "UTF-8") + "' where cIdPedidoDefinitivo='" + params[0] + "'";
            } else {
                queryL = "update mSolicitudLineas set cIdEstadoLinea='L' WHERE cIdSolicitud in(select cIdSolicitud from v_lineasPartidaContrato with(Nolock) " + "where cIdContrato=SUBSTRING('" + params[0] + "',1,(LEN('" + params[0] + "')-5))) " + " and nIdLineaSolicitud in(select nIdLineaSolicitud from v_lineasPartidaContrato with(Nolock) " + " where cIdContrato=SUBSTRING('" + params[0] + "',1,(LEN('" + params[0] + "')-5)))";
                queryPedCont = "update mContrato set nIdEstado=6,cMotivoCancelacion='" + new String(params[8].getBytes("ISO-8859-1"), "UTF-8") + "' where cIdContratoDefinitivo='" + params[0] + "'";
            }
            log.info("Object: {}", queryL.toString());
            if (Integer.parseInt(params[9]) == 0) {
                stmL = conn.createStatement();
                stmL.executeUpdate(queryL);
            }
            //Cambiar el estatus del contrato
            log.info("Object: {}", queryPedCont.toString());
            stmPedCont = conn.createStatement();
            stmPedCont.executeUpdate(queryPedCont);
            //guardar en bitacora
            Util.bitacoraMovimientos(params[0], "CANCELACIÓN_DOCUMENTOS_APROBADOS.", usuario.getLogin(), conn);
            resp = true;
        } finally {
            cpu.cambiaCentroContableUE(ueOriginal, cCentroContableOrig, usuario);
            if (rsEnc != null) {
                rsEnc.close();
            }
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmL != null) {
                stmL.close();
            }
            if (stmPedCont != null) {
                stmPedCont.close();
            }
            rsEnc = null;
            stmEnc = null;
            stmL = null;
            stmPedCont = null;
        }
        return resp;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 01-06-2015
	 * */
    protected boolean cancelaPedCont(String[] params, Usuario usuario, Connection conn, HttpServletRequest request, HttpServletResponse response) throws SQLException {
        boolean resp = true;
        String folioPrecompromiso = "";
        CallableStatement csmt = null;
        Statement stmPro = null;
        ResultSet rsPro = null;
        try {
            //revisar si hay precompromiso de ped/cont y liberarlo
            stm = conn.createStatement();
            String query = "select nFolioPreCompromiso from tPreCompromisoEncabezado with(Nolock) where cIdContrato='" + params[0] + "' and cDocumentoHaplicado='S'";
            log.info("Object: {}", "Query para buscar si hay precomPedCont: " + query);
            rs = stm.executeQuery(query);
            //Cancela precomPromiso del pedCont si tiene
            while (rs.next()) {
                folioPrecompromiso = rs.getString("nFolioPreCompromiso");
                resp = cancelaPrecomPedCont(folioPrecompromiso, conn);
                if (!resp) {
                    return resp;
                }
            }
            //anulas el ped o cont ANULADO POR ADMINISTRADOR 6
            if (resp) {
                String queryCons = "{ call pa_cancelaPedCont(?,?,?)}";
                int outputVal = 0;
                csmt = conn.prepareCall(queryCons);
                csmt.setString(1, params[0]);
                csmt.setString(2, usuario.getLogin());
                csmt.registerOutParameter(3, Types.INTEGER);
                csmt.execute();
                outputVal = csmt.getInt(3);
                log.info("Object: {}", "Querys para la anulación del consolidado. " + queryCons);
                if (outputVal != 0) {
                    return resp = false;
                }
            }
            //validar si el procedimiento solo genero un ped o contrato( uano a uno)
            if (Integer.parseInt(params[6]) == 0 || Integer.parseInt(params[6]) == 1) {
                //Cancelar el procedimiento
                params[0] = params[5];
                resp = cancelaProcedimiento(params, usuario, conn);
                if (!resp) {
                    return resp;
                }
            } else {
                //Está es otra historia
                resp = false;
                //Buscar si hay precompromiso de procedimiento
                String queryPrecomMat = "select nFolioPrecomMateriales from tPrecomMaterialesEncabezado with(Nolock) where cIdConsolidado='" + params[5] + "' and cDocumentoHaplicado='S'";
                String folioPrecomMat = "";
                stmPro = conn.createStatement();
                log.info("Object: {}", "Query para buscar si hay precomProcedimiento: " + queryPrecomMat);
                rsPro = stmPro.executeQuery(queryPrecomMat);
                if (rsPro.next()) {
                    folioPrecomMat = rsPro.getString("nFolioPrecomMateriales");
                    //Crear encabezado y detalle del precompromiso a cancelar
                    creaEncDetPrecompromiso(usuario, conn, params, request, response);
                    //Cancelar la parte de precompromiso que le corresponde
                    resp = cancelaPrecomMatParcial(folioPrecomMat, conn);
                    if (!resp) {
                        return resp;
                    }
                }
                //Buscar si hay precompromiso de consolidado
                //Cancelar la parte de precompromiso que le corresponde
                //Cancelar las lineas de consolidado que le corresponden
                //Cancelar las lineas de la requi que corresponden
            }
        } catch (Exception e) {
            // TODO: handle exception
            resp = false;
            log.error("Error occurred", "Error al cancelar el pedido o contrato. " + e);
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (csmt != null) {
                csmt.close();
            }
            rs = null;
            stm = null;
        }
        return resp;
    }

    /*
 * Creado por Ing. Humberto Farias Rojas
 * 08-06-2015
 * */
    protected int creaEncDetPrecompromiso(Usuario usuario, Connection conn, String[] params, HttpServletRequest request, HttpServletResponse response) {
        int folio = 0;
        int folioOriginal = 0;
        String cEjercicio = "";
        try {
            //Obtiene un nuevo caso.
            Caso caso = generaGuardaCaso(usuario, request, cEjercicio, GestionInterface.IDTC_PRECOMPROMISO);
            folio = Integer.parseInt(caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1));
            stm = conn.createStatement();
            //Crea el encabezado de la liberacion del precompromiso.
            //params[8] es el canoPrecompromiso
            stm.executeUpdate("insert into tPrecomMaterialesEncabezado " + " values (" + folio + ",GETDATE(),'LIBPRCP-" + usuario.getU_UR() + "-" + folio + "','DI',GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL, NULL, '" + params[8] + "' , 0 , 'PR' , " + "DATEPART(MONTH,GETDATE()), NULL , '" + cEjercicio + "', 'RHQ' , NULL , NULL , 'LIBERACION PARCIAL DE PRECOMPROMISO. PRECOMPROMISO ORIGINAL '" + folioOriginal + " , 0 , GETDATE() , 'ORIGINAL '" + folioOriginal + " , NULL)");
            //Crea el detalle de la liberacion del precompromiso.
            stm.executeUpdate("detalle");
        } catch (Exception e) {
            // TODO: handle exception
            folio = 0;
            log.error("Error occurred", "Error al crear el encabezado y detalle del precompromiso a liberar. " + e);
            e.printStackTrace();
        }
        return folio;
    }

    protected int creaEncDetCompromiso(Usuario usuario, Connection conn, String cidContrato, String esRadicado, String cEjercicio, int folio, HttpServletRequest request) throws SQLException {
        String queryEnc = "", queryDet = "";
        Statement stmEnc = null, stmDet = null;
        String cEevento = "CMP001";
        int retval = -1;
        int seqFolio;
        String seqValue;
        String caNoContrarrecibo;
        try {
            CFSequenceManager sequence = CFSequenceManager.getInstance();
            if ("S".equals(esRadicado)) {
                cEevento = "R_CMP001";
            }
            stmEnc = conn.createStatement();
            //Crea el encabezado de la liberacion del compromiso.
            seqFolio = sequence.nextVal("CO-" + usuario.getPropiedad("CCENTROCONTABLE").getValor());
            seqValue = (100000 + seqFolio) + "";
            //seqValue = seqValue.substring(seqValue.length() - 6);
            //seqValue = "1" + seqValue.substring(seqValue.length() - 5);
            seqValue = usuario.getPropiedad("CCENTROCONTABLE").getValor() + "CO" + cEjercicio + seqValue;
            caNoContrarrecibo = seqValue;
            queryEnc = "INSERT INTO tCompromisoEncabezado (nFolioCompromiso,fCarga,cIdContrato,cTipoContrato,fAplicacion,cCentroContable " + "	,cRamo,cUnidadResponsable,cDocumentoHaplicado,nFolioPoliza,caNoCompromiso,nEnviadoSICOP " + "	,cTipoPoliza,nMes,cRevisado,aEjercicioFiscal,cUnidadResponsableContable,nFolioPolizaCancelacion " + "	,fCancelacion,cDescripcionPoliza,usuario,cRadicado,nFolioAutSICOP) " + " values (" + folio + ",GETDATE(),'" + cidContrato + "','DI',GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL, NULL, '" + caNoContrarrecibo + "' , 0 , 'CO' , " + "DATEPART(MONTH,GETDATE()), NULL , '" + cEjercicio + "', 'RHQ' , NULL , NULL , 'LIBERACION TOTAL DE COMPROMISO POR EL MODULO DE CANCELACIÓN DE PEDIDOS O CONTRATOS APROBADOS.', '" + usuario.getLogin() + "','" + esRadicado + "',null)";
            log.info("Object: {}", queryEnc.toString());
            stmEnc.executeUpdate(queryEnc);
            //Crea el detalle de la liberacion del precompromiso.
            stmDet = conn.createStatement();
            queryDet = "INSERT INTO tCompromisoDetalle (nFolioCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) select " + folio + ",(ROW_NUMBER() OVER (ORDER BY det.EP,det.cMes) )  nDocRenglon,det.EP,'" + cEevento + "'cEvento " + ",(SUM(mImporte)*-1)mImporte,SUM(mImporte) mImporteNegtivo,det.cMes,det.cCentroContable " + " from tCompromisoEncabezado as enc with(Nolock) " + " inner join tCompromisoDetalle  as det with(nolock) on det.nFolioCompromiso=enc.nFolioCompromiso and enc.cDocumentoHaplicado='S' " + " where cIdContrato='" + cidContrato + "' and det.cCentroContable='" + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "' and enc.cRadicado='" + esRadicado + "' " + " group by det.EP,det.cMes,det.cCentroContable";
            log.info("Object: {}", queryDet.toString());
            stmDet.executeUpdate(queryDet);
            retval = 0;
        } finally {
            if (stmEnc != null) {
                stmEnc.close();
            }
            if (stmDet != null) {
                stmDet.close();
            }
            stmEnc = null;
            stmDet = null;
        }
        return retval;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 29-05-2015
	 * */
    protected boolean cancelaProcedimiento(String[] params, Usuario usuario, Connection conn) throws SQLException {
        boolean resp = true;
        Statement stmB = null;
        Statement stmPro = null, stmPAP = null;
        ResultSet rsPro = null;
        try {
            //revisar si hay precompromiso de procedimiento y liberarlo
            String queryPrecomMat = "select nFolioPrecomMateriales from tPrecomMaterialesEncabezado with(Nolock) where cIdConsolidado='" + params[0] + "' and cDocumentoHaplicado='S'";
            String folioPrecomMat = "";
            stmPro = conn.createStatement();
            log.info("Object: {}", "Query para buscar si hay precomProcedimiento: " + queryPrecomMat);
            rsPro = stmPro.executeQuery(queryPrecomMat);
            if (rsPro.next()) {
                folioPrecomMat = rsPro.getString("nFolioPrecomMateriales");
                resp = cancelaPrecomMat(folioPrecomMat, conn);
                if (!resp) {
                    return resp;
                }
            }
            //Cancelar Procedimiento
            stmPAP = conn.createStatement();
            String queryPAP = "update mProcedimientoAdjudicacionPartidas set nIdEstadoPartida=2 where cIdProcedimiento='" + params[0] + "'";
            log.info("Object: {}", "Query para Liberar las lineas del procedimiento: " + queryPAP);
            stmPAP.executeUpdate(queryPAP);
            stm = conn.createStatement();
            String query = "update mProcedimiento set nIdEstado=3 where cIdProcedimiento='" + params[0] + "'";
            log.info("Object: {}", "Query para declarar desierto el procedimiento: " + query);
            stm.executeUpdate(query);
            //Guardar en Bitacora
            String queryB = "insert into mBitacoraMovimientos (cIdDocumento,cAccion,cIdUsuario,fRegistro) values('" + params[0] + "','CANCELACION_DOCUMENTO','" + usuario.getLogin() + "',GETDATE())";
            log.info("Object: {}", "Bitacora : " + queryB);
            stmB = conn.createStatement();
            stmB.executeUpdate(queryB);
            params[0] = params[4];
            //Cancelar el consolidado y requisición
            resp = cancelaPrecomConsolidado(params, usuario, conn);
        } catch (Exception e) {
            // TODO: handle exception
            resp = false;
            log.error("Error al cancelar el Procedimiento.");
            e.printStackTrace();
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (stmPro != null) {
                stmPro.close();
            }
            if (stmB != null) {
                stmB.close();
            }
            if (rsPro != null) {
                rsPro.close();
            }
            if (stmPAP != null) {
                stmPAP.close();
            }
            stm = null;
            stmB = null;
            rsPro = null;
            stmPAP = null;
        }
        return resp;
    }

    /*
	 * Creado por Ing. Humberto Farias Rojas
	 * 22-05-2015
	 * */
    protected boolean cancelaApartadoAndDocumento(String requi, Usuario usuario, Connection conn) throws AccountingEngineException, ServletException, SQLException, GestionException, IOException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        boolean resp = true;
        CallableStatement csmt = null;
        try {
            //Busca si hay apartado
            stm = conn.createStatement();
            String folioApartado = "";
            String query = "select nFolioApartado from tApartadoEncabezado with(Nolock) where cDocumentoHaplicado='S' and cIdSolicitud='" + requi + "'";
            log.info("Object: {}", "Query para buscar si la requi tiene apartado: " + query);
            rs = stm.executeQuery(query);
            //Cancela apartado si tiene
            if (rs.next()) {
                folioApartado = rs.getString("nFolioApartado");
                resp = cancelaApartado(folioApartado, conn);
            }
            //Libera las lineas de la requi
            if (resp) {
                //[pa_cancelaLineasRequisicion]
                int outputVal = 0;
                csmt = conn.prepareCall("{ call pa_cancelaLineasRequisicion(?,?,?)}");
                csmt.setString(1, requi);
                csmt.setString(2, usuario.getLogin());
                csmt.registerOutParameter(3, Types.INTEGER);
                csmt.execute();
                outputVal = csmt.getInt(3);
                if (outputVal != 0) {
                    resp = false;
                }
            } else {
                log.warn("Object: {}", "Como no se libero el apartado de la requisición " + requi + " no se liberan sus lineas.");
            }
        } catch (Exception e) {
            // TODO: handle exception
            resp = false;
            log.error("Error occurred", "Error en la cancelación de documento con su apartado." + e);
            e.printStackTrace();
        } finally {
            if (csmt != null) {
                csmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            rs = null;
            stm = null;
            csmt = null;
        }
        return resp;
    }

    private String cancelarPrecompromisoCompletoFinanciero(String[] params, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, SQLException, GestionException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        /////////////////////////////////////////////////////////////////////////////////
        int errores = 0;
        String resp = "";
        //String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        //CallableStatement csmt=null;
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            for (int y = 8; y < params.length; y++) {
                rs = stm.executeQuery("select ae.nFolioPrecompromiso,'PRECOM' AS tipoPrecompromiso from tPrecompromisoEncabezado ae with(nolock) " + " where ae.cIdContrato = '" + params[y] + "' and cDocumentoHaplicado = 'S'" + " union " + " select pa.nFolioPrecomMateriales,'PRECOMMAT' AS tipoPrecompromiso from tPrecomMaterialesEncabezado pa" + " where pa.cIdConsolidado='" + params[y] + "'" + " and pa.cDocumentoHaplicado='S' ");
                if (rs.next()) {
                    //para que cancele los pagos directos y relaciones de gastos
                    if ("PRECOMMAT".equalsIgnoreCase(rs.getString("tipoPrecompromiso"))) {
                        if (!accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES", rs.getString("nFolioPrecompromiso"), "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales")) {
                            errores++;
                        }
                    } else if (!accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", rs.getString("nFolioPrecompromiso"), "tPrecompromisoEncabezado", "tPrecompromisoDetalle", "nFolioPrecompromiso")) {
                        errores++;
                    }
                    /*
							csmt = conn.prepareCall("{ call pa_actualizaPrecompromisoLiberaFinanciero(?,?,?)}");
							csmt.setString(1, params[y]);
							csmt.setString(2,usuario.getLogin());
							csmt.setString(3,rs.getString("tipoPrecompromiso"));
							csmt.execute();	
							*/
                } else
                    resp = "OK:EL APARTADO DE LA SOLICITUD YA HABIA SIDO CANCELADO.";
            }
            if (errores == 0) {
                resp = "OK:EL PRECOMPROMISO   SE CANCELO CORRECTAMENTE.";
                conn.commit();
            } else {
                resp = "ERROR AL CANCELAR EL PRECOMPROMISO ";
                conn.rollback();
            }
        } catch (SQLException e) {
            resp = "ERROR AL CANCELAR EL PRECOMPROMISO";
            conn.rollback();
            e.printStackTrace();
        } finally {
            //closeConnection();
            if (conn != null)
                conn.close();
            if (stm != null)
                stm.close();
            //if(csmt!=null)
            //	csmt.close();
            if (rs != null)
                rs.close();
            stm = null;
            //csmt=null;
            rs = null;
            conn = null;
        }
        return resp;
    }

    private String cancelacionDeDocumentos(String[] params, Usuario usuario, HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String resp = "";
        boolean val = false;
        String doc = "";
        int tipoDoc = 0;
        try {
            tipoDoc = Integer.parseInt(params[1]);
            conn = DataSourceManager.getConnection(jndiName);
            doc = params[0];
            switch(tipoDoc) {
                case 1:
                    log.info("Opción de requisiciones");
                    val = cancelaApartadoAndDocumento(doc, usuario, conn);
                    break;
                case 2:
                    log.info("Opción de Consolidado");
                    val = cancelaPrecomConsolidado(params, usuario, conn);
                    break;
                case 3:
                    log.info("Opción de Procedimiento");
                    val = cancelaProcedimiento(params, usuario, conn);
                    break;
                case 4:
                    log.info("Opción de Ped/cont");
                    val = cancelaPedCont(params, usuario, conn, request, response);
                    break;
                case 5:
                    log.info("Opción de Ped/cont aprobados. ");
                    val = cancelaPedContAprobado(params, usuario, conn, request, response);
                    break;
                default:
                    log.info("Tipo de opción no valida");
                    val = false;
                    break;
            }
            if (val) {
                conn.commit();
                resp = "El documento " + doc + " se cancelo correctamente";
            } else {
                conn.rollback();
                resp = "El documento " + doc + " no se cancelo";
            }
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
            resp = "Error al cancelar el documento " + doc;
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return resp;
    }

    private String cancelarApartadoCompleto(String[] params, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, ServletException, SQLException, GestionException, IOException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        int errores = 0;
        String resp = "";
        CallableStatement csmt = null;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            for (int y = 8; y < params.length; y++) {
                rs = stm.executeQuery("select ae.nFolioApartado from tApartadoEncabezado ae with(nolock) " + "where ae.nFolioApartado = " + params[y] + " and cDocumentoHaplicado = 'S'");
                if (rs.next()) {
                    if (!accEng.cancelAccountingApplication(conn, "APARTADO", params[y], "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado")) {
                        errores++;
                    }
                    csmt = conn.prepareCall("{ call pa_actualizaRequisicionLiberaApartadoFinanciero(?,?)}");
                    csmt.setString(1, params[y]);
                    csmt.setString(2, usuario.getLogin());
                    csmt.execute();
                } else
                    resp = "OK:EL APARTADO DE LA SOLICITUD YA HABIA SIDO CANCELADO.";
            }
            if (errores == 0) {
                resp = "OK:EL APARTADO DE LA SOLICITUD  SE CANCELO CORRECTAMENTE.";
                conn.commit();
            } else {
                resp = "ERROR AL CANCELAR EL APARTADO DE LA SOLICITUD ";
                conn.rollback();
            }
        } catch (SQLException e) {
            resp = "ERROR AL CANCELAR EL APARTADO DE LA SOLICITUD";
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
        } finally {
            closeConnection();
            if (stm != null)
                stm.close();
            if (csmt != null)
                csmt.close();
            stm = null;
            csmt = null;
        }
        return resp;
    }

    private String[][] getApartadosCompletos(String[] params) throws ServletException {
        String[][] valCom = null;
        String[][] val = null;
        int cont = 0;
        valCom = getSolicitudesCancelar(params);
        for (int i = 0; i < valCom.length; i++) {
            if (i == 0 && val == null)
                val = new String[valCom.length][valCom[i].length];
            val[cont] = valCom[i];
            cont++;
        }
        return val;
    }

    private String[][] getSolicitudesCancelar(String[] param) throws ServletException {
        String[][] retVal = null;
        String where = "", query = "";
        if (!param[4].equals("*") && !param[4].equals("") && !param[4].equals(null))
            where += " and s.cIdUnidadEjecutora = '" + param[4] + "' ";
        if (!param[0].equals(""))
            where += " and s.cIdSolicitud = '" + param[0] + "'";
        if (param[8].equals("0"))
            where += " and ad.cMes between '01' and '12'";
        else
            where += " and ad.cMes between '" + param[8] + "' and '" + param[9] + "'";
        query = " select distinct '<input type=''checkbox'' id=''folioApartado_'+CONVERT(varchar,s.ConsecutivoAPARTADO)+''' " + "name=''folioApartado_'' value='''+CONVERT(varchar,s.ConsecutivoAPARTADO)+'''/>'," + "SUM(ad.mImporte)as importeDetalle,s.cIdSolicitud,s.C_FOLIO_APA,s.ConsecutivoAPARTADO,s.cIdUnidadEjecutora,s.cDescripcion " + " from mSolicitud s with(nolock),tApartadoEncabezado ae with(nolock),tApartadoDetalle ad with(nolock) where ae.nFolioApartado=S.ConsecutivoAPARTADO AND S.cIdSolicitud NOT IN(" + "	Select distinct cs.cidsolicitud from mConsolidadoSolicitud cs with(nolock),mConsolidado c with(nolock),tPrecomMaterialesEncabezado pme with(nolock) where c.cIdConsolidado=cs.cIdConsolidado" + " and c.cIdConsolidado=pme.cIdConsolidado and pme.cDocumentoHaplicado='S' and c.nIdEstado in(2) " + " UNION " + " select distinct cs.cidsolicitud from mConsolidadoSolicitud cs with(nolock),mConsolidado c with(nolock),tPrecomMaterialesEncabezado pme with(nolock),mProcedimiento mp with(nolock) " + " where c.cIdConsolidado=mp.cIdConsolidado and mp.cIdProcedimiento=pme.cIdConsolidado and pme.cDocumentoHaplicado='S' and mp.nIdEstado in(2) " + " and cs.cIdConsolidado=c.cIdConsolidado and c.nIdEstado in(2) ) and ae.cDocumentoHaplicado='S' AND S.nIdEstado=3 AND S.nIdEstadoPrecomprometido=3 " + " and ad.nFolioApartado=ae.nFolioApartado " + where + " group by s.cIdSolicitud,s.C_FOLIO_APA,s.ConsecutivoAPARTADO,s.cIdUnidadEjecutora,s.cIdEntidadContable,s.cDescripcion order by s.cIdUnidadEjecutora";
        log.info("Object: {}", "getSolicitudesConApartado:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return retVal;
    }

    /**
     * Convierte matriz en JSONArray
     * @param valores
     * @return
     */
    private JSONArray convertMatrizToJSONArray(String[][] valores) {
        jsonArray = new JSONArray();
        jsonObj = new JSONObject();
        String datos = "";
        try {
            //Crear el JSON
            if (valores != null) {
                for (int i = 0; i < valores.length; i++) {
                    jsonObj = new JSONObject();
                    for (int j = 0; j < valores[i].length; j++) {
                        datos = valores[i][j];
                        jsonObj.put("Col" + j, datos);
                    }
                    // Add to the array
                    jsonArray.put(jsonObj);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * Obtiene los importes de los apartados con la EP´S
     * @param cIdSolicitud
     * @return
     * @throws ServletException
     */
    private String[][] getSolicitudesConApartado(String[] param, String tipoRequisicion) throws ServletException {
        String[][] retVal = null;
        String where = "", where2 = "", query = "";
        if (!param[4].equals("*") && !param[4].equals("") && !param[4].equals(null))
            where += " and s.cIdUnidadEjecutora = '" + param[4] + "' ";
        if (!param[0].equals(""))
            where += " and s.cIdSolicitud = '" + param[0] + "'";
        if (param[5].equals("PARCIALES")) {
            where2 += " and totalLineasSolicitud >= totalLineasActivas and totalLineasSolicitud > (totalLineasApartado-totalLineasPrecomprometidas) ";
            //" and (totalLineasApartado-totalLineasPrecomprometidas) > totalLineasPrecomprometidas";
        }
        if (param[5].equals("COMPLETO")) {
            where2 += " and totalLineasSolicitud = totalLineasActivas" + " and totalLineasActivas = (totalLineasApartado-totalLineasPrecomprometidas)" + " and (totalLineasPrecomprometidas is null or totalLineasPrecomprometidas = 0 ) ";
        }
        if (param[5].equals("TODOS")) {
            where2 += " and ((totalLineasSolicitud > totalLineasActivas or totalLineasSolicitud > (totalLineasApartado-totalLineasPrecomprometidas)) " + " and (totalLineasApartado-totalLineasPrecomprometidas) > totalLineasPrecomprometidas) " + "or " + "(totalLineasSolicitud = totalLineasActivas" + " and totalLineasActivas = (totalLineasApartado-totalLineasPrecomprometidas)" + " and (totalLineasPrecomprometidas is null or totalLineasPrecomprometidas = 0 ))";
        }
        if (tipoRequisicion.indexOf("RC") >= 0 || tipoRequisicion.indexOf("RO") >= 0 || tipoRequisicion.indexOf("RS") >= 0) {
            query = "select tabF.folioApartado, tabF.cIdSolicitud, tabF.cDescripcion, tabF.totalLineasSolicitud, tabF.totalLineasActivas, tabF.totalLineasApartado-tabF.totalLineasPrecomprometidas as totalLineasApartado, tabF.totalLineasPrecomprometidas " + "from (" + "select tabL.folioApartado, tabL.cIdSolicitud, tabL.cDescripcion, sum(tabL.totalLineasSolicitud) as totalLineasSolicitud, " + "sum(tabL.totalLineasActivas) as totalLineasActivas, sum(tabL.totalLineasApartado) as totalLineasApartado, " + "case when tabP.totalLineasPrecomprometidas is null then 0 else tabP.totalLineasPrecomprometidas end as totalLineasPrecomprometidas " + "from (" + "select 'APTD-'+rtrim(ltrim(ae.cUnidadResponsable))+'-'+cast(ae.nFolioApartado as varchar) as folioApartado, " + "s.cDescripcion, s.cIdSolicitud, 1 as totalLineasSolicitud, case when sln.cIdEstadoLinea  in( 'C','L' ) then 0 else 1 end as totalLineasActivas," + "case when (sum(case when sla.nIdLineaSolicitud is null then 0 else " + "case when ad.nFolioApartado is null then 0 else " + "case when (sla.mes01+sla.mes02+sla.mes03+sla.mes04+sla.mes05+sla.mes06+sla.mes07+sla.mes08+sla.mes09+sla.mes10+sla.mes11+sla.mes12)>0 then 1 else 0 end " + "end " + "end)) > 0 then 1 else 0 end as totalLineasApartado " + "from " + "mSolicitud s with(nolock) " + "inner join mSolicitudLineas sln with(nolock) " + "on s.cIdSolicitud = sln.cIdSolicitud " + "left join mSolicitudLineasApartado sla with(nolock) " + "on sln.cIdSolicitud = sla.cIdSolicitud and sln.nIdLineaSolicitud = sla.nIdLineaSolicitud " + "left join tApartadoEncabezado ae with(nolock) " + "on s.cIdSolicitud = ae.cIdSolicitud and ae.cDocumentoHaplicado = 'S' " + "left join tApartadoDetalle ad with(nolock) " + "on ae.nFolioApartado = ad.nFolioApartado " + "where s.cIdTipoSolicitud in (" + tipoRequisicion + ") and s.nIdEstado in (2,3) and ad.cEvento='APARTADO' " + where + "group by ae.cUnidadResponsable, ae.nFolioApartado, s.cDescripcion, s.cIdSolicitud, sln.nIdLineaSolicitud, sln.cIdEstadoLinea " + ") as tabL " + "left join " + "(" + "select tabS.cIdSolicitud, sum(precomMat + precomPedido + precomContrato) as totalLineasPrecomprometidas from " + "(" + "select distinct " + "sl.cIdSolicitud, sl.nIdLineaSolicitud " + ",case when pen.nFolioPrecomMateriales is null then 0 else 1 end as precomMat " + ",case when pe.cIdPedido is not null then case when pe.nIdEstado < 3 then 0 else 1 end else 0 end as precomPedido " + ",case when co.cIdContrato is not null then case when co.nIdEstado < 3 then 0 else 1 end else 0 end as precomContrato  " + "from mSolicitud s with(nolock) " + "inner join mSolicitudLineas sl with(nolock) " + "on s.cIdSolicitud = sl.cIdSolicitud " + "inner join mConsolidadoSolicitud cs with(nolock) " + "on sl.cIdSolicitud = cs.cIdSolicitud and sl.nIdLineaSolicitud = cs.nIdLineaSolicitud " + "inner join mConsolidado c with(nolock) " + "on cs.cIdConsolidado = c.cIdConsolidado " + "left join tPrecomMaterialesEncabezado pen with(nolock) " + "on c.ConsecutivoPRECOMP = pen.nFolioPrecomMateriales and pen.cDocumentoHaplicado='S'  " + "left join mProcedimientoAdjudicacionPartidas pap with(nolock) " + "on cs.cIdConsolidado = pap.cIdConsolidado and pap.nIdLineaConsolidado = cs.nIdLineaConsolidado " + "left join mPedido pe with(nolock) " + "on pe.cIdProcedimiento = pap.cIdProcedimiento and pe.cIdRFC = pe.cIdRFC and pe.nIdconsecutivoAdj = pe.nIdconsecutivoAdj " + "left join mContrato co with(nolock) " + "on pap.cIdProcedimiento = co.cIdProcedimiento and pap.cIdRFC = co.cIdRFC and pap.nIdconsecutivoAdj = co.nIdconsecutivoAdj " + "where " + "s.cIdTipoSolicitud in (" + tipoRequisicion + ") " + "and s.nIdEstado in (2,3) " + "and c.nIdEstado in (1,2) " + where + ") as tabS " + "group by tabS.cIdSolicitud" + ") as tabP " + "on tabL.cIdSolicitud = tabP.cIdSolicitud " + "group by tabL.folioApartado, tabL.cDescripcion, tabL.cIdSolicitud, tabP.totalLineasPrecomprometidas " + ") as tabF " + "where (tabF.totalLineasApartado-tabF.totalLineasPrecomprometidas) > 0 " + where2;
        } else if (tipoRequisicion.indexOf("RM") >= 0) {
            query = "select tabF.folioApartado, tabF.cIdSolicitud, tabF.cDescripcion, tabF.totalLineasSolicitud, tabF.totalLineasActivas, tabF.totalLineasApartado-tabF.totalLineasPrecomprometidas as totalLineasApartado, tabF.totalLineasPrecomprometidas " + "from (" + "select " + "tabL.folioApartado, tabL.cIdSolicitud, tabL.cDescripcion, sum(tabL.totalLineasSolicitud) as totalLineasSolicitud, " + "sum(tabL.totalLineasActivas) as totalLineasActivas, sum(tabL.totalLineasApartado) as totalLineasApartado, " + "case when tab.totalLineasPrecomprometidas is null then 0 else tab.totalLineasPrecomprometidas end as totalLineasPrecomprometidas " + "from ( " + "select 'APTD-'+rtrim(ltrim(ae.cUnidadResponsable))+'-'+cast(ae.nFolioApartado as varchar) as folioApartado, " + "s.cDescripcion, s.cIdSolicitud, 1 as totalLineasSolicitud, case when sln.cIdEstadoLinea  in( 'C','L') then 0 else 1 end as totalLineasActivas," + "case when (sum(case when sla.nIdLineaSolicitud is null then 0 else " + "case when ad.nFolioApartado is null then 0 else " + "case when (sla.mes01+sla.mes02+sla.mes03+sla.mes04+sla.mes05+sla.mes06+sla.mes07+sla.mes08+sla.mes09+sla.mes10+sla.mes11+sla.mes12)>0 then 1 else 0 end " + "end " + "end)) > 0 then 1 else 0 end as totalLineasApartado " + "from " + "mSolicitud s with(nolock) " + "inner join mSolicitudLineas sln with(nolock) " + "on s.cIdSolicitud = sln.cIdSolicitud " + "left join mSolicitudLineasApartado sla with(nolock) " + "on sln.cIdSolicitud = sla.cIdSolicitud and sln.nIdLineaSolicitud = sla.nIdLineaSolicitud " + "left join tApartadoEncabezado ae with(nolock) " + "on s.cIdSolicitud = ae.cIdSolicitud and ae.cDocumentoHaplicado = 'S' " + "left join tApartadoDetalle ad with(nolock) " + "on ae.nFolioApartado = ad.nFolioApartado " + "where s.cIdTipoSolicitud in (" + tipoRequisicion + ") and s.nIdEstado in (2,3) and ad.cEvento='APARTADO' " + where + "group by ae.cUnidadResponsable, ae.nFolioApartado, s.cDescripcion, s.cIdSolicitud, sln.nIdLineaSolicitud, sln.cIdEstadoLinea " + ")as tabL " + "left join ( " + "select tabS.cIdSolicitud, count(*) as totalLineasPrecomprometidas " + "from " + "(" + "select modi.cIdSolicitud, modi.cIdLineaSolicitud " + "from (" + "select cmp.cIdSolicitud, cmp.cIdLineaSolicitud, cm.cContratoDefinitivo as cDocumentoDefinitivo " + "from mContratoModificadoPartida cmp  with(nolock) " + "inner join mContratoModificado cm with(nolock) " + "on cmp.cIdContratoDefinitivo = cm.cIdContratoDefinitivo and cmp.nConsecutivoModificacion = cm.nConsecutivoModificacion " + "inner join mSolicitud s with(nolock) " + "on cmp.cIdSolicitud = s.cIdSolicitud " + "where cm.tipoMod = 0 and s.cIdTipoSolicitud in (" + tipoRequisicion + ") and s.nIdEstado in (2,3) " + where + "union " + "select pmp.cIdSolicitud, pmp.cIdLineaSolicitud, pm.cPedidoDefinitivo as cDocumentoDefinitivo " + "from mPedidoModificadoPartida pmp with(nolock) " + "inner join mPedidoModificado pm with(nolock) " + "on pmp.cIdPedidoDefinitivo = pm.cIdPedidoDefinitivo and pmp.nConsecutivoModificacion = pm.nConsecutivoModificacion " + "inner join mSolicitud s with(nolock) " + "on pmp.cIdSolicitud = s.cIdSolicitud " + "where pm.tipoMod = 0 and s.cIdTipoSolicitud in (" + tipoRequisicion + ") and s.nIdEstado in (2,3) " + where + ") as modi " + "left join tPreCompromisoEncabezado pen with(nolock) " + "on modi.cDocumentoDefinitivo = pen.cIdContrato " + "where pen.cDocumentoHaplicado='S' " + "group by modi.cIdSolicitud, modi.cIdLineaSolicitud" + ") as tabS " + "group by tabS.cIdSolicitud " + ") as tab " + "on tabL.cIdSolicitud = tab.cIdSolicitud " + "group by tabL.folioApartado, tabL.cIdSolicitud, tabL.cDescripcion, tab.totalLineasPrecomprometidas" + ") as tabF " + "where (tabF.totalLineasApartado-tabF.totalLineasPrecomprometidas) > 0 " + where2;
        }
        log.info("Object: {}", "getSolicitudesConApartado:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return retVal;
    }

    /**
     * Obtiene las de una requisicion con apartado
     * @param cIdSolicitud
     * @return
     * @throws ServletException
     */
    private String[][] getLineasConApartado(String cIdSolicitud) throws ServletException {
        String[][] retVal = null;
        String query = "select " + "sl.nIdLineaSolicitud, sl.cIdCABM, sl.cDescripcion " + ",'$ '+CONVERT(VARCHAR,CAST(sum(sla.mes01+sla.mes02+sla.mes03+sla.mes04+sla.mes05+sla.mes06+ " + "sla.mes07+sla.mes08+sla.mes09+sla.mes10+sla.mes11+sla.mes12) AS money),1) as total " + "from " + "mSolicitudLineas sl with(nolock) " + "inner join mSolicitudLineasApartado sla with(nolock) " + "on sl.cIdSolicitud = sla.cIdSolicitud and sl.nIdLineaSolicitud = sla.nIdLineaSolicitud " + "where sl.cIdSolicitud='" + cIdSolicitud + "' " + "and sl.nIdLineaSolicitud not in (" + getLineasPrecomprometidas(cIdSolicitud) + ") " + "group by " + "sl.nIdLineaSolicitud, sl.cIdCABM, sl.cDescripcion " + "having sum(sla.mes01+sla.mes02+sla.mes03+sla.mes04+sla.mes05+sla.mes06+ " + "sla.mes07+sla.mes08+sla.mes09+sla.mes10+sla.mes11+sla.mes12)>0 " + "order by sl.nIdLineaSolicitud";
        log.info("Object: {}", "getLineasConApartado:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return retVal;
    }

    /**
     * Obtiene los ID´S de las lineas de una requisicion que ya fueron precomprometidas.
     * @param cIdSolicitud
     * @return
     * @throws ServletException
     */
    private String getLineasPrecomprometidas(String cIdSolicitud) throws ServletException {
        String[][] retVal = null;
        String lineasPrecom = "0,";
        String query = "";
        log.info("Object: {}", "cIdSolicitud: " + cIdSolicitud);
        if (cIdSolicitud.indexOf("RC") == 0 || cIdSolicitud.indexOf("RO") == 0 || cIdSolicitud.indexOf("RS") == 0) {
            query = "select sl.nIdLineaSolicitud " + "from " + "mSolicitud s with(nolock) " + "inner join mSolicitudLineas sl with(nolock) " + "on s.cIdSolicitud = sl.cIdSolicitud " + "inner join mConsolidadoSolicitud cs with(nolock) " + "on sl.cIdSolicitud = cs.cIdSolicitud and sl.nIdLineaSolicitud = cs.nIdLineaSolicitud " + "inner join mConsolidado c " + "on cs.cIdConsolidado = c.cIdConsolidado " + "inner join tPrecomMaterialesEncabezado pen with(nolock) " + "on c.ConsecutivoPRECOMP = pen.nFolioPrecomMateriales and pen.cDocumentoHaplicado='S' " + "where " + "s.nIdEstado in (2,3) " + "and s.cIdSolicitud='" + cIdSolicitud + "' " + "order by sl.nIdLineaSolicitud ";
        } else if (cIdSolicitud.indexOf("RM") == 0) {
            query = "select sl.nIdLineaSolicitud " + "from mSolicitud s with(nolock) " + "inner join mSolicitudLineas sl with(nolock) " + "on s.cIdSolicitud = sl.cIdSolicitud " + "left join mContratoModificadoPartida cmp with(nolock) " + "on cmp.cIdSolicitud = sl.cIdSolicitud and sl.nIdLineaSolicitud = cmp.cIdLineaSolicitud " + "left join mContratoModificado cm with(nolock) " + "on cm.cIdContratoDefinitivo = cmp.cIdContratoDefinitivo and cm.nConsecutivoModificacion = cmp.nConsecutivoModificacion " + "left join tPrecompromisoEncabezado pen with(nolock) " + "on cm.cContratoDefinitivo = pen.cIdContrato and pen.cDocumentoHaplicado='S' " + "where s.nIdEstado in (2,3) and s.cIdSolicitud='" + cIdSolicitud + "' and pen.nFolioPreCompromiso is not null " + "union " + "select sl.nIdLineaSolicitud " + "from mSolicitud s with(nolock) " + "inner join mSolicitudLineas sl with(nolock) " + "on s.cIdSolicitud = sl.cIdSolicitud " + "left join mPedidoModificadoPartida pmp with(nolock) " + "on pmp.cIdSolicitud = sl.cIdSolicitud and sl.nIdLineaSolicitud = pmp.cIdLineaSolicitud " + "left join mPedidoModificado pm with(nolock) " + "on pm.cIdPedidoDefinitivo = pmp.cIdPedidoDefinitivo and pm.nConsecutivoModificacion = pmp.nConsecutivoModificacion " + "left join tPrecompromisoEncabezado pen with(nolock) " + "on pm.cPedidoDefinitivo = pen.cIdContrato and pen.cDocumentoHaplicado='S' " + "where s.nIdEstado in (2,3) and s.cIdSolicitud='" + cIdSolicitud + "' and pen.nFolioPreCompromiso is not null ";
        }
        log.info("Object: {}", "getLineasPrecomprometidas:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
            for (int i = 0; i < retVal.length; i++) {
                for (int l = 0; l < retVal[i].length; l++) lineasPrecom += retVal[i][l] + ",";
            }
            if (lineasPrecom.length() > 0)
                lineasPrecom = lineasPrecom.substring(0, lineasPrecom.length() - 1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return lineasPrecom;
    }

    /**
     * Obtienes los precompromisos de cancelacion parcial y completa
     * @param params
     * @return
     * @throws ServletException
     */
    private String[][] getApartados(String[] params) throws ServletException {
        String[][] valCom = null;
        String[][] valModificacion = null;
        String[][] val = null;
        int cont = 0;
        valCom = getSolicitudesConApartado(params, "'RC','RO','RS'");
        valModificacion = getSolicitudesConApartado(params, "'RM'");
        for (int i = 0; i < valCom.length; i++) {
            if (i == 0 && val == null)
                val = new String[valCom.length + valModificacion.length][valCom[i].length];
            val[cont] = valCom[i];
            cont++;
        }
        for (int i = 0; i < valModificacion.length; i++) {
            if (i == 0 && val == null)
                val = new String[valCom.length + valModificacion.length][valModificacion[i].length];
            val[cont] = valModificacion[i];
            cont++;
        }
        return val;
    }

    /**
     * Obtiene todos los precompromisos activos
     * @param params
     * @return
     * @throws ServletException
     */
    private String[][] getPrecompromisos(String[] params) throws ServletException {
        String[][] retVal = null;
        String query = "", documento = "", pedido = "", contrato = "", consolidado_procedimiento = "", ur = "";
        if (!params[0].equals("")) {
            //documento += " and pe.cIdContrato='"+params[0]+"' ";
            pedido += " and doc.cIdPedido = '" + params[0] + "' ";
            contrato += " and doc.cIdContrato = '" + params[0] + "' ";
            consolidado_procedimiento += "and pe.cIdConsolidado = '" + params[0] + "' ";
        }
        if (!params[4].equals("*") && !params[4].equals("") && !params[4].equals(null)) {
            ur += " and doc.cIdUnidadEjecutora ='" + params[4] + "' ";
            pedido += " and substring(doc.cIdPedido,4,3) = '" + params[4] + "' ";
            contrato += " and substring(doc.cIdContrato,4,3) = '" + params[4] + "' ";
        }
        query = "select 'PRMT-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPrecomMateriales as varchar) as folioPrecompromiso, doc.cIdConsolidado, " + "'CONSOLIDADO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mConsolidado doc with(nolock) inner join tPrecomMaterialesEncabezado pe with(nolock) " + "on doc.cIdConsolidado = pe.cIdConsolidado inner join tPrecomMaterialesDetalle pd with(nolock) " + "on pd.nFolioPrecomMateriales = pe.nFolioPrecomMateriales " + "left join mProcedimientoAdjudicacion pa with(nolock) " + "on pa.cIdTipoConsolidado+'-'+ltrim(rtrim(pa.cIdUnidadEjecutora))+'-'+cast(pa.nIdConsecutivo as varchar) = doc.cIdConsolidado " + "left join mPedido ped with(nolock) on ped.cIdProcedimiento = pa.cIdProcedimiento and rtrim(ltrim(ped.cIdRFC)) = rtrim(ltrim(pa.cIdRFC)) and ped.nIdconsecutivoAdj = pa.nIdconsecutivoAdj " + "left join mContrato cr with(nolock) on cr.cIdProcedimiento = pa.cIdProcedimiento and rtrim(ltrim(cr.cIdRFC)) = rtrim(ltrim(pa.cIdRFC)) and cr.nIdconsecutivoAdj = pa.nIdconsecutivoAdj " + "where pe.cDocumentoHaplicado = 'S' and (ped.nIdEstado < 3 or (ped.nIdEstado is null and (cr.nIdEstado < 3 or cr.nIdEstado is null))) " + "and (select max(nFolioPreCompromiso) from tPreCompromisoEncabezado with(nolock) where cIdContrato = ped.cIdPedido or cIdContrato = ped.cIdPedidoDefinitivo and cDocumentoHaplicado = 'S') is null " + "and (select max(nFolioPreCompromiso) from tPreCompromisoEncabezado with(nolock) where cIdContrato = cr.cIdContrato or cIdContrato = cr.cIdContratoDefinitivo and cDocumentoHaplicado = 'S') is null " + ur + " " + consolidado_procedimiento + "group by doc.cIdConsolidado, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPrecomMateriales " + "union " + "select 'PRMT-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPrecomMateriales as varchar) as folioPrecompromiso, doc.cIdProcedimiento, " + "'PROCEDIMIENTO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mProcedimiento doc with(nolock) inner join tPrecomMaterialesEncabezado pe with(nolock) " + "on doc.cIdProcedimiento = pe.cIdConsolidado inner join tPrecomMaterialesDetalle pd with(nolock) " + "on pd.nFolioPrecomMateriales = pe.nFolioPrecomMateriales " + "left join mProcedimientoAdjudicacion pa with(nolock) " + "on pa.cIdProcedimiento = doc.cIdProcedimiento " + "left join mPedido ped with(nolock) on ped.cIdProcedimiento = pa.cIdProcedimiento and ped.cIdRFC = pa.cIdRFC and ped.nIdconsecutivoAdj = pa.nIdconsecutivoAdj " + "left join mContrato cr with(nolock) on cr.cIdProcedimiento = pa.cIdProcedimiento and rtrim(ltrim(cr.cIdRFC)) = rtrim(ltrim(pa.cIdRFC)) and cr.nIdconsecutivoAdj = pa.nIdconsecutivoAdj " + "where pe.cDocumentoHaplicado = 'S' and (ped.nIdEstado < 3 or (ped.nIdEstado is null and (cr.nIdEstado < 3 or cr.nIdEstado is null))) " + " and (select max(nFolioPreCompromiso) from tPreCompromisoEncabezado with(nolock) where cIdContrato = ped.cIdPedido or cIdContrato = ped.cIdPedidoDefinitivo and cDocumentoHaplicado = 'S') is null " + " and (select max(nFolioPreCompromiso) from tPreCompromisoEncabezado with(nolock) where cIdContrato = cr.cIdContrato or cIdContrato = cr.cIdContratoDefinitivo and cDocumentoHaplicado = 'S') is null " + ur + " " + consolidado_procedimiento + "group by doc.cIdProcedimiento, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPrecomMateriales " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, pe.cIdContrato, " + "'PEDIDO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPedido doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on (doc.cIdPedidoDefinitivo = pe.cIdContrato or doc.cIdPedido = pe.cIdContrato) inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null and pd.cEvento in ('PRECOM','COMP_MAT') " + pedido + " " + ur + "group by pe.cIdContrato, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, pe.cIdContrato, " + "'CONTRATO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mContrato doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on (doc.cIdContratoDefinitivo = pe.cIdContrato or doc.cIdContrato = pe.cIdContrato) inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null and pd.cEvento in ('PRECOM','COMP_MAT') " + contrato + " " + ur + "group by pe.cIdContrato, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, pe.cIdContrato, " + "'AMPLIACION PEDIDO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPedidoAmpliacion doc with(nolock) inner join mPedido p with(nolock) " + "on doc.cIdTipoPedido+'-'+doc.cIdUnidadEjecutora+'-'+cast(doc.nIdConsecutivo as varchar) = p.cIdPedido " + "inner join tPreCompromisoEncabezado pe with(nolock) " + "on p.cIdPedidoDefinitivo+'-AMP-'+cast(doc.nIdConsecutivoAmpliacion as varchar) = pe.cIdContrato " + "inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null " + documento + " " + ur + "group by pe.cIdContrato, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, doc.cIdPedidoDefinitivo, " + "'PASIVO PEDIDO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPasivoPedido doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on doc.cIdPedidoDefinitivo = pe.cIdContrato inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null " + documento + " " + ur + "group by doc.cIdPedidoDefinitivo, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, doc.cIdPedidoDefinitivo, " + "'PLURIANUALIDAD PEDIDO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPlurianualidadPedido doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on doc.cIdPedidoDefinitivo = pe.cIdContrato inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null " + documento + " " + ur + "group by doc.cIdPedidoDefinitivo, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, doc.cPedidoDefinitivo, " + "'MODIFICACION PEDIDO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPedidoModificado doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on doc.cPedidoDefinitivo = pe.cIdContrato inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null and doc.tipoMod = 0 " + pedido + "group by doc.cPedidoDefinitivo, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, pe.cIdContrato, " + "'AMPLIACION CONTRATO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mContratoAmpliacion doc with(nolock) inner join mPedido p with(nolock) " + "on doc.cIdTipoContrato+'-'+doc.cIdUnidadEjecutora+'-'+cast(doc.nIdConsecutivo as varchar) = p.cIdPedido " + "inner join tPreCompromisoEncabezado pe with(nolock) " + "on p.cIdPedidoDefinitivo+'-AMP-'+cast(doc.nIdConsecutivoAmpliacion as varchar) = pe.cIdContrato " + "inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null " + documento + " " + ur + "group by pe.cIdContrato, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, doc.cIdContratoDefinitivo, " + "'PASIVO CONTRATO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPasivoContrato doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on doc.cIdContratoDefinitivo = pe.cIdContrato inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null " + documento + " " + ur + "group by doc.cIdContratoDefinitivo, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, doc.cIdContratoDefinitivo, " + "'PLURIANUALIDAD CONTRATO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mPlurianualidadContrato doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on doc.cIdContratoDefinitivo = pe.cIdContrato inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null " + documento + " " + ur + "group by doc.cIdContratoDefinitivo, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso " + "union " + "select 'PRCP-'+rtrim(ltrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) as folioPrecompromiso, doc.cContratoDefinitivo, " + "'MODIFICACION CONTRATO' as tipo, pe.cDescripcionPoliza, '$ '+convert(varchar,sum(pd.mImporte),1) as total " + "from mContratoModificado doc with(nolock) inner join tPreCompromisoEncabezado pe with(nolock) " + "on doc.cContratoDefinitivo = pe.cIdContrato inner join tPreCompromisoDetalle pd with(nolock) " + "on pd.nFolioPreCompromiso = pe.nFolioPreCompromiso " + "where pe.cDocumentoHaplicado = 'S' and pe.C_FOLIO_COMP is null and doc.tipoMod = 0 " + contrato + "group by doc.cContratoDefinitivo, pe.cDescripcionPoliza, pe.cUnidadResponsable, pe.nFolioPreCompromiso";
        log.info("Object: {}", "getPrecompromisos:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return retVal;
    }

    /**
     * Obtiene los precompromisos del folio del documento indicado
     * (consolidado, procedimiento, pedido o contrato)
     * @param cIdDocumento
     * @return
     * @throws ServletException
     */
    private String[][] getEP_Precom(String folioCasoPrecompromiso) throws ServletException {
        String[] folioCasoPrecomSeparado = folioCasoPrecompromiso.split("-");
        String[][] retVal = null;
        String query = "";
        if (folioCasoPrecomSeparado[0].equals("PRCP")) {
            query = "select " + "EP, " + "case when [1] is null then 0 else [1] end as Enero," + "case when [2] is null then 0 else [2] end as Febrero," + "case when [3] is null then 0 else [3] end as Marzo, " + "case when [4] is null then 0 else [4] end as Abril, " + "case when [5] is null then 0 else [5] end as Mayo, " + "case when [6] is null then 0 else [6] end as Junio, " + "case when [7] is null then 0 else [7] end as Julio, " + "case when [8] is null then 0 else [8] end as Agosto, " + "case when [9] is null then 0 else [9] end as Septiembre," + "case when [10] is null then 0 else [10] end as Octubre, " + "case when [11] is null then 0 else [11] end as Noviembre, " + "case when [12] is null then 0 else [12] end as Diciembre " + "from " + "(select pd.EP, pd.cMes, sum(pd.mImporte) as mImporte " + "from tPreCompromisoEncabezado pe inner join tPreCompromisoDetalle pd " + "on pe.nFolioPreCompromiso = pd.nFolioPreCompromiso " + "where " + "pe.cDocumentoHaplicado = 'S' " + "and pe.nFolioPrecompromiso='" + folioCasoPrecomSeparado[2] + "' " + "and pd.cEvento in ('PRECOM','COMP_MAT') " + "and pd.mImporte > 0 " + "and pe.C_FOLIO_COMP is null " + "and pe.ConsecutivoCOMP is null " + "group by pd.EP, pd.cMes) as tab " + "pivot ( " + "sum(mImporte) " + "for cMes in ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12]) " + ") as pvt ";
        }
        if (folioCasoPrecomSeparado[0].equals("PRMT")) {
            query = "select " + "EP, " + "case when [1] is null then 0 else [1] end as Enero," + "case when [2] is null then 0 else [2] end as Febrero," + "case when [3] is null then 0 else [3] end as Marzo, " + "case when [4] is null then 0 else [4] end as Abril, " + "case when [5] is null then 0 else [5] end as Mayo, " + "case when [6] is null then 0 else [6] end as Junio, " + "case when [7] is null then 0 else [7] end as Julio, " + "case when [8] is null then 0 else [8] end as Agosto, " + "case when [9] is null then 0 else [9] end as Septiembre," + "case when [10] is null then 0 else [10] end as Octubre, " + "case when [11] is null then 0 else [11] end as Noviembre, " + "case when [12] is null then 0 else [12] end as Diciembre " + "from " + "(select pd.EP, pd.cMes, sum(pd.mImporte) as mImporte " + "from tPrecomMaterialesEncabezado pe inner join tPrecomMaterialesDetalle pd " + "on pe.nFolioPrecomMateriales = pd.nFolioPrecomMateriales " + "where " + "pe.cDocumentoHaplicado = 'S' " + "and pe.nFolioPrecomMateriales = " + folioCasoPrecomSeparado[2] + " " + "and pd.cEvento in ('PRECOM','PRECOM_MAT','DISP_PRECOMMAT') " + "and pd.mImporte > 0 " + "and (select MAX(CONSECUTIVO_MAT) from mPrecomMaterialesPrecomFinanciero pm with(nolock) where CONSECUTIVO_MAT = " + folioCasoPrecomSeparado[2] + ") is null " + "group by pd.EP, pd.cMes) as tab " + "pivot ( " + "sum(mImporte) " + "for cMes in ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12]) " + ") as pvt ";
        }
        log.info("Object: {}", "getEP_Precom:" + query);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            retVal = CatalogosManager.getSelectQuery(conn, query);
            //conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            closeConnection();
        }
        return retVal;
    }

    /**
     * Obtiene los folios de precompromiso que se deben cancelar del mismo documento.
     * @param cIdDocumento
     * @return
     * @throws ServletException
     */
    private String[][] getFoliosPrecomCancelar(String cIdDocumento, Connection conn) throws ServletException {
        String[][] retVal = null;
        String query = "select distinct nFolioPreCompromiso from tPreCompromisoEncabezado with(nolock) " + "where cIdContrato='" + cIdDocumento + "' and cDocumentoHaplicado='S' and C_FOLIO_COMP is null and ConsecutivoCOMP is null " + "union " + "select distinct nFolioPrecomMateriales from tPrecomMaterialesEncabezado pme with(nolock) " + "left join mPrecomMaterialesPrecomFinanciero pm with(nolock) " + "on pme.nFolioPrecomMateriales = pm.CONSECUTIVO_MAT " + "where pme.cIdConsolidado='" + cIdDocumento + "' and cDocumentoHaplicado='S' and pm.CONSECUTIVO_MAT is null ";
        log.info("Object: {}", "getFoliosPrecomCancelar:" + query);
        try {
            retVal = CatalogosManager.getSelectQuery(conn, query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        return retVal;
    }

    /**
     * Realiza la cancelacion del apartado por documento o por linea de requsicion
     * @param cIdSolicitud
     * @param lineas
     * @param request
     * @param response
     * @param usuario
     * @param cEjercicio
     * @return
     * @throws AccountingEngineException
     * @throws ServletException
     * @throws SQLException
     * @throws IOException
     * @throws GestionException
     */
    private String cancelarApartado(String[] params, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, ServletException, SQLException, GestionException, IOException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        int errores = 0;
        String resp = "";
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            if (params[6].toUpperCase().equals("ALL")) {
                rs = stm.executeQuery("select ae.nFolioApartado from tApartadoEncabezado ae with(nolock) " + "where ae.nFolioApartado = " + params[1] + " and cDocumentoHaplicado = 'S'");
                if (rs.next()) {
                    if (!accEng.cancelAccountingApplication(conn, "APARTADO", params[1], "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado")) {
                        errores++;
                        resp = "ERROR AL CANCELAR EL APARTADO CON FOLIO " + params[1] + " DE LA SOLICITUD " + params[0] + ".";
                    }
                    if (errores == 0) {
                        resp = "OK:EL APARTADO DE LA SOLICITUD " + params[0] + " SE CANCELO CORRECTAMENTE.";
                        conn.commit();
                    } else {
                        resp = "ERROR AL CANCELAR EL APARTADO DE LA SOLICITUD " + params[0] + ".";
                        conn.rollback();
                    }
                } else
                    resp = "OK:EL APARTADO DE LA SOLICITUD YA HABIA SIDO CANCELADO.";
            } else {
                rs = stm.executeQuery("select sla.nIdLineaSolicitud from mSolicitudLineasApartado sla with(nolock) " + "where sla.cIdSolicitud = '" + params[0] + "' and sla.nIdLineaSolicitud " + "in (" + params[6].replace("|", ",") + ") and sla.mes01+sla.mes02+sla.mes03+sla.mes04+sla.mes05+sla.mes06+sla.mes07+sla.mes08+sla.mes09+sla.mes10+sla.mes11+sla.mes12 > 0");
                if (rs.next()) {
                    //Obtiene un nuevo caso.
                    Caso caso = generaGuardaCaso(usuario, request, cEjercicio, GestionInterface.IDTC_APARTADO);
                    Integer folio = Integer.parseInt(caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1));
                    stm = conn.createStatement();
                    ContableInterface conInt = new AplicacionContable();
                    AplicarContableReturn acr = null;
                    //Crea el encabezado de la liberacion del apartado.
                    stm.executeUpdate("insert into tApartadoEncabezado " + "select top 1 " + folio + ",ae.fCarga, ae.fAplicacion, ae.cCentroContable, ae.cRamo, ae.cUnidadResponsable, NULL, NULL, '', cTipoPoliza, nMes, NULL, ae.aEjercicioFiscal," + "ae.cUnidadResponsableContable, NULL, NULL, NULL, 0, ae.fVigencia, NULL, 0, ae.cIdSolicitud " + "from tApartadoEncabezado ae with(nolock) inner join tApartadoDetalle ad with(nolock) on ae.nFolioApartado = ad.nFolioApartado " + "where ae.nFolioApartado = '" + params[1] + "' and ae.cDocumentoHaplicado = 'S' and ad.cEvento = 'APARTADO' ");
                    //Crea el detalle de la liberacion del apartado.
                    stm.executeUpdate("insert into tApartadoDetalle (nFolioApartado,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) " + "select " + folio + ",ROW_NUMBER() OVER (ORDER BY tab.EP),tab.EP,'LIBAPAR', sum(tab.monto)," + "sum(tab.monto)*-1,tab.mes, tab.cCentroContable from " + "(select EP, convert(int, right(mes,2)) as mes, monto, cCentroContable " + "from (select sla.nIdClaveEgresos+'.'+sla.ClaveInterna as EP, mes01,mes02," + "mes03,mes04,mes05,mes06,mes07,mes08,mes09,mes10,mes11,mes12,(select cCentroContable from tApartadoEncabezado with(nolock) where nFolioApartado=" + params[1] + ") as cCentroContable " + "from mSolicitudLineasApartado sla with(nolock) " + "where sla.cIdSolicitud = '" + params[0] + "' and sla.nIdLineaSolicitud " + "in (" + params[6].replace("|", ",") + ")) pvt " + "unpivot " + "(monto for mes in (mes01,mes02,mes03 " + ",mes04,mes05,mes06,mes07,mes08,mes09,mes10,mes11,mes12)) " + "as unpvt where monto > 0 ) tab " + "group by tab.EP, tab.mes, tab.cCentroContable");
                    //Elimina las lineas de la tabla mSolicitudLineasApartado
                    stm.executeUpdate("update mSolicitudLineasApartado set mes01=0,mes02=0,mes03=0,mes04=0,mes05=0,mes06=0,mes07=0,mes08=0,mes09=0,mes10=0,mes11=0,mes12=0 where cIdSolicitud='" + params[0] + "' and nIdLineaSolicitud in (" + params[6].replace("|", ",") + ")");
                    //Realiza la aplicacion contable
                    Map m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
                    acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
                    if (acr.isSuccess()) {
                        resp = "OK:EL APARTADO DE LA SOLICITUD " + params[0] + " SE CANCELO CORRECTAMENTE.";
                        conn.commit();
                    } else {
                        resp = acr.getMessageList().get(0);
                        conn.rollback();
                    }
                } else
                    resp = "OK:EL APARTADO DE LAS LINEAS YA HABIA SIDO CANCELADO.";
            }
        } catch (SQLException e) {
            resp = "ERROR AL CANCELAR EL APARTADO DE LA SOLICITUD " + params[0] + ".";
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
        } finally {
            closeConnection();
            if (stm != null) {
                stm.close();
            }
            stm = null;
        }
        return resp;
    }

    /**
     * Realiza la cancelacion de todos los apartados.
     * @param params
     * @param request
     * @param response
     * @param usuario
     * @param cEjercicio
     * @return
     * @throws AccountingEngineException
     * @throws ServletException
     * @throws SQLException
     * @throws GestionException
     * @throws IOException
     */
    private String cancelarTodoApartado(String[] params, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, ServletException, SQLException, GestionException, IOException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        String resp = "", apartadosStr = "", requisicionesStr = "", apartadosFolios = "", casosStr = "", clearApartados = "";
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        params[5] = "TODOS";
        String[][] apartados = getApartados(params);
        Connection conn = DataSourceManager.getConnection(jndiName);
        try {
            //Obtiene un nuevo caso.
            Caso caso = generaGuardaCaso(usuario, request, cEjercicio, GestionInterface.IDTC_APARTADO);
            Integer folio = Integer.parseInt(caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1));
            stm = conn.createStatement();
            ContableInterface conInt = new AplicacionContable();
            AplicarContableReturn acr = null;
            //Crea el encabezado de la liberacion del apartado.
            stm.executeUpdate("insert into tApartadoEncabezado " + "VALUES (" + folio + ", GETDATE(), GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL," + "NULL,'','PR',DATEPART(MONTH,GETDATE()),NULL,'" + cEjercicio + "','RHQ',NULL,NULL,'LIBERACION DE APARTADO',0,GETDATE(),NULL,0,NULL )");
            //Crea el detalle de la liberacion del apartado.
            stm.executeUpdate("insert into tApartadoDetalle (nFolioApartado,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) " + "select " + folio + ",ROW_NUMBER() OVER (order by cSubCuenta,mes) nDocRenglon, x.cSubCuenta as EP,'LIBAPAR' as cEvento,x.montoImporte,x.montoImporteNegativo,x.mes AS cMes, " + usuario.getPropiedad("CCENTROCONTABLE").getValor() + " from(" + "select nCuenta, cSubCuenta, SUM(msaldoarrastre) as montoImporte,SUM(msaldoarrastre)*-1 as montoImporteNegativo, Substring(cSubCuenta, 32, 1)as capitulo,SUBSTRING(nCuenta,10,2) as mes " + "from tSaldos " + "where nCuenta like '82101%' and mSaldoArrastre > 0 and Substring(cSubCuenta, 32, 1) not in ('6', '4') " + "group by nCuenta, cSubCuenta) x");
            //Realiza la aplicacion contable
            Map m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            if (acr.isSuccess()) {
                for (int i = 0; i < apartados.length; i++) {
                    apartadosStr += apartados[i][0] + ",";
                    requisicionesStr += apartados[i][1] + ",";
                    apartadosFolios += apartados[i][0].split("-")[2] + ",";
                    //Valida si existen lineas precomprometidas
                    if (Integer.parseInt(apartados[i][6]) == 0)
                        clearApartados += apartados[i][1] + ",";
                    else
                        stm.executeUpdate("update mSolicitudLineasApartado set mes01=0,mes02=0,mes03=0,mes04=0,mes05=0,mes06=0,mes07=0,mes08=0,mes09=0,mes10=0,mes11=0,mes12=0 where cIdSolicitud = '" + apartados[i][1] + "' and nIdLineaSolicitud in (" + getLineasPrecomprometidas(apartados[i][1]) + ")");
                }
                if (clearApartados.length() > 0) {
                    clearApartados = clearApartados.substring(0, clearApartados.length() - 1);
                    clearApartados = clearApartados.replace(",", "','");
                    clearApartados = "'" + clearApartados + "'";
                }
                if (apartadosStr.length() > 0) {
                    apartadosStr = apartadosStr.substring(0, apartadosStr.length() - 1);
                    apartadosStr = apartadosStr.replace(",", "','");
                    apartadosStr = "'" + apartadosStr + "'";
                }
                if (requisicionesStr.length() > 0) {
                    requisicionesStr = requisicionesStr.substring(0, requisicionesStr.length() - 1);
                    requisicionesStr = requisicionesStr.replace(",", "','");
                    requisicionesStr = "'" + requisicionesStr + "'";
                }
                if (apartadosFolios.length() > 0) {
                    apartadosFolios = apartadosFolios.substring(0, apartadosFolios.length() - 1);
                }
                rs = stm.executeQuery("select ID_CASO from CG_CASO with(nolock) where C_FOLIO in (" + apartadosStr + ")");
                while (rs.next()) casosStr += rs.getString(1) + ",";
                if (casosStr.length() > 0) {
                    casosStr = casosStr.substring(0, casosStr.length() - 1);
                    stm.executeUpdate("update CG_CASO_OPERACION set ID_OPER = 5, CO_RESPONSABLE = 'CONSULTA_APARTADO' where ID_CASO in (" + casosStr + ") and ID_TC = 25");
                }
                //Cancela los apartados
                stm.executeUpdate("update tApartadoEncabezado set cDocumentoHaplicado = 'C' where nFolioApartado in (" + apartadosFolios + ")");
                //Actualiza el estatus de las requisiciones para habilitar la pestaña de presupuesto y apartado en el modulo de requisicion
                stm.executeUpdate("update mSolicitud set nIdEstado = 5 where cIdSolicitud in (" + requisicionesStr + ")");
                //Limpia los apartados de las requisiciones que no tienen precompromisos
                stm.executeUpdate("update mSolicitudLineasApartado set mes01=0,mes02=0,mes03=0,mes04=0,mes05=0,mes06=0,mes07=0,mes08=0,mes09=0,mes10=0,mes11=0,mes12=0 where cIdSolicitud in  (" + clearApartados + ")");
                resp = "OK:LA CANCELACION SE REALIZO CORRECTAMENTE.";
                conn.commit();
            } else {
                resp = acr.getMessageList().get(0);
                conn.rollback();
            }
        } catch (SQLException e) {
            resp = "ERROR AL CANCELAR LOS APARTADOS.";
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return resp;
    }

    /**
     * Realiza la cancelacion de todos los precompromisos
     * @param params
     * @param request
     * @param response
     * @param usuario
     * @param cEjercicio
     * @return
     * @throws AccountingEngineException
     * @throws ServletException
     * @throws SQLException
     * @throws GestionException
     * @throws IOException
     */
    private String cancelarTodoPrecompromisos(String[] params, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, ServletException, SQLException, GestionException, IOException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        String resp = "", precompromisosStr = "", precompromisosFolios = "", casosStr = "";
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String[][] precompromisos = getPrecompromisos(params);
        try {
            conn = DataSourceManager.getConnection(jndiName);
            //Obtiene un nuevo caso.
            Caso caso = generaGuardaCaso(usuario, request, cEjercicio, GestionInterface.IDTC_PRECOMPROMISO);
            Integer folio = Integer.parseInt(caso.getFolio().substring(caso.getFolio().lastIndexOf("-") + 1));
            stm = conn.createStatement();
            ContableInterface conInt = new AplicacionContable();
            AplicarContableReturn acr = null;
            //Crea el encabezado de la liberacion del precompromiso.
            stm.executeUpdate("insert into tPrecompromisoEncabezado " + " values (" + folio + ",GETDATE(),'LIBPRCP-" + usuario.getU_UR() + "-" + folio + "','DI',GETDATE()," + usuario.getPropiedad("CCENTROCONTABLE").getValor() + "," + usuario.getU_Ramo() + ",'" + usuario.getU_UR() + "',NULL, NULL, '" + params[8] + "' , 0 , 'CO' , " + "DATEPART(MONTH,GETDATE()), NULL , '" + cEjercicio + "', 'RHQ' , NULL , NULL , 'LIBERACION DE PRECOMPROMISOS' , 2 , GETDATE() , NULL , NULL)");
            //Crea el detalle de la liberacion del precompromiso.
            stm.executeUpdate("insert into tPreCompromisoDetalle (nFolioPreCompromiso,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable) " + "select " + folio + ", ROW_NUMBER() OVER (order by cSubCuenta,mes) nDocRenglon, x.cSubCuenta as EP,'LIBPREC'as cEvento,x.montoImporte,x.montoImporteNegativo,x.mes AS cMes, " + usuario.getPropiedad("CCENTROCONTABLE").getValor() + " from(" + "select nCuenta, cSubCuenta, SUM(msaldoarrastre) as montoImporte,SUM(msaldoarrastre)*-1 as montoImporteNegativo, Substring(cSubCuenta, 32, 1)as capitulo,SUBSTRING(nCuenta,10,2)as mes " + "from tSaldos " + "where nCuenta like '82102%' and mSaldoArrastre > 0 and Substring(cSubCuenta, 32, 1) not in ('6', '4') " + "group by nCuenta, cSubCuenta) x");
            //Realiza la aplicacion contable
            Map m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
            acr = conInt.aplicarContableNuevo(conn, caso, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "");
            if (acr.isSuccess()) {
                for (int i = 0; i < precompromisos.length; i++) {
                    precompromisosStr += precompromisos[i][0] + ",";
                    precompromisosFolios += precompromisos[i][0].split("-")[2] + ",";
                }
                if (precompromisosStr.length() > 0) {
                    precompromisosStr = precompromisosStr.substring(0, precompromisosStr.length() - 1);
                    precompromisosStr = precompromisosStr.replace(",", "','");
                    precompromisosStr = "'" + precompromisosStr + "'";
                    precompromisosFolios = precompromisosFolios.substring(0, precompromisosFolios.length() - 1);
                }
                rs = stm.executeQuery("select ID_CASO from CG_CASO with(nolock) where C_FOLIO in (" + precompromisosStr + ")");
                while (rs.next()) casosStr += rs.getString(1) + ",";
                if (casosStr.length() > 0) {
                    casosStr = casosStr.substring(0, casosStr.length() - 1);
                    stm.executeUpdate("update CG_CASO_OPERACION set ID_OPER = 3, CO_RESPONSABLE = 'CONSULTA_PRECOMPROMISO' where ID_CASO in (" + casosStr + ") and ID_TC=14");
                }
                stm.executeUpdate("update tPreCompromisoEncabezado set cDocumentoHaplicado = 'C' where nFolioPreCompromiso in (" + precompromisosFolios + ")");
                resp = "OK:LA CANCELACION SE REALIZO CORRECTAMENTE.";
                conn.commit();
            } else {
                resp = acr.getMessageList().get(0);
                conn.rollback();
            }
        } catch (SQLException e) {
            resp = "ERROR AL CANCELAR LOS PRECOMPROMISOS.";
            conn.rollback();
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return resp;
    }

    /**
     * Realiza la cancelacion de los pre-compromisos
     * @param param
     * @param request
     * @param response
     * @param usuario
     * @param cEjercicio
     * @return respuesta de la aplicacion contable
     * @throws SQLException
     */
    private String cancelarPrecompromiso(String[] param, HttpServletRequest request, HttpServletResponse response, Usuario usuario, String cEjercicio) throws AccountingEngineException, SQLException {
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(true);
        String resp = "OK:EL PRECOMPROMISO DEL DOCUMENTO " + param[0] + " SE CANCELO CORRECTAMENTE.";
        ;
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            rs = stm.executeQuery("select pe.nFolioPrecompromiso from tPrecompromisoEncabezado pe with(nolock) " + "where pe.nFolioPreCompromiso = " + param[1] + " and cDocumentoHaplicado = 'S'");
            if (rs.next()) {
                int precomPendientes = getFoliosPrecomCancelar(param[0], conn).length;
                if (!accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", param[1], "tPreCompromisoEncabezado", "tPreCompromisoDetalle", "nFolioPreCompromiso")) {
                    resp = "ERROR AL CANCELAR EL PRECOMPROMISO CON FOLIO " + param[1] + " DEL DOCUMENTO " + param[0] + ".";
                    conn.rollback();
                } else {
                    //Avanza el caso
                    stm.executeUpdate("update CG_CASO_OPERACION SET ID_CASO_OPER=3, ID_OPER=3, CO_RESPONSABLE='CONSULTA_PRECOMPROMISO' " + "where ID_CASO = (select c.ID_CASO from CG_CASO c with(nolock) " + "inner join tPreCompromisoEncabezado pe with(nolock) on c.C_FOLIO='PRCP-'+ltrim(rtrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPreCompromiso as varchar) " + "where pe.nFolioPreCompromiso=" + param[1] + ")");
                    //Actualiza el datos del documento
                    if (precomPendientes == 1)
                        actualizarDatosDoctoCancelado(stm, param[0]);
                    conn.commit();
                }
            } else {
                rs = stm.executeQuery("select pe.nFolioPrecomMateriales from tPrecomMaterialesEncabezado pe with(nolock) " + "where pe.cIdConsolidado = '" + param[0] + "' and cDocumentoHaplicado = 'S'");
                if (rs.next()) {
                    if (!accEng.cancelAccountingApplication(conn, "PRECOMPROMISO", param[1], "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales")) {
                        resp = "ERROR AL CANCELAR EL PRECOMPROMISO CON FOLIO " + param[1] + " DEL DOCUMENTO " + param[0] + ".";
                        conn.rollback();
                    } else {
                        //Avanza el caso
                        stm.executeUpdate("update CG_CASO_OPERACION SET ID_CASO_OPER=3, ID_OPER=3, CO_RESPONSABLE='CONSULTA_PRECOMPROMISO' " + "where ID_CASO = (select c.ID_CASO from CG_CASO c with(nolock) " + "inner join tPrecomMaterialesEncabezado pe with(nolock) on c.C_FOLIO='PRCP-'+ltrim(rtrim(pe.cUnidadResponsable))+'-'+cast(pe.nFolioPrecomMateriales as varchar) " + "where pe.nFolioPrecomMateriales=" + param[1] + ")");
                        //Actualiza el datos del documento
                        actualizarDatosDoctoCancelado(stm, param[0]);
                        conn.commit();
                    }
                } else {
                    resp = "OK:EL PRECOMPROMISO YA HABIA SIDO CANCELADO.";
                }
            }
        } catch (Exception e) {
            resp = "ERROR AL CANCELAR EL PRECOMPROMISO DEL DOCUMENTO " + param[0] + ".";
            conn.rollback();
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return resp;
    }

    public void actualizarDatosDoctoCancelado(Statement stm, String docto) throws SQLException {
        int resp = 0;
        if (resp == 0) {
            resp = stm.executeUpdate("update mPedido set nIdEstado = 2 where cIdPedidoDefinitivo='" + docto + "' and nIdEstado = 3 ");
            resp += stm.executeUpdate("update mPedido set C_FOLIO_PRE = NULL, ConsecutivoPRECOMP = NULL where cIdPedidoDefinitivo='" + docto + "'");
        }
        if (resp == 0)
            resp = stm.executeUpdate("update mPedidoAmpliacion set nIdEstado = 2 where cIdPedido = (select pe.cIdTipoPedido+'-'+pe.cIdUnidadEjecutora+'-'+cast(pe.nIdConsecutivo as varchar)+'-AMP-'+cast(pa.nIdConsecutivoAmpliacion as varchar) " + "from mPedidoAmpliacion pa with(nolock) inner join mPedido pe with(nolock) " + "on pa.cIdTipoPedido+'-'+pa.cIdUnidadEjecutora+'-'+cast(pa.nIdConsecutivo as varchar)=pe.cIdPedido " + "where pe.cIdPedidoDefinitivo+'-AMP-'+cast(pa.nIdConsecutivoAmpliacion as varchar) = '" + docto + "')");
        if (resp == 0)
            resp = stm.executeUpdate("update mPasivoPedido set nIdEstado = 2 where cIdPedidoDefinitivo = '" + docto + "'");
        if (resp == 0)
            resp = stm.executeUpdate("update mPlurianualidadPedido set nIdEstado = 2 where cIdPedidoDefinitivo = '" + docto + "'");
        if (resp == 0)
            resp = stm.executeUpdate("update mPedidoModificado set nEstado = 2 where cPedidoDefinitivo = '" + docto + "'");
        if (resp == 0) {
            resp = stm.executeUpdate("update mContrato set nIdEstado = 2, C_FOLIO_PRE = NULL, ConsecutivoPRECOMP = NULL where cIdContratoDefinitivo ='" + docto + "' and nIdEstado = 3 ");
            resp += stm.executeUpdate("update mContrato set C_FOLIO_PRE = NULL, ConsecutivoPRECOMP = NULL where cIdContratoDefinitivo ='" + docto + "' ");
        }
        if (resp == 0)
            resp = stm.executeUpdate("update mContratoAmpliacion set nIdEstado = 2 where cIdContrato = (select co.cIdTipoContrato+'-'+co.cIdUnidadEjecutora+'-'+cast(co.nIdConsecutivo as varchar)+'-AMP-'+cast(ca.nIdConsecutivoAmpliacion as varchar) " + "from mContratoAmpliacion ca with(nolock) inner join mContrato co with(nolock) " + "on ca.cIdTipoContrato+'-'+ca.cIdUnidadEjecutora+'-'+cast(ca.nIdConsecutivo as varchar)=co.cIdContrato " + "where co.cIdContratoDefinitivo+'-AMP-'+cast(ca.nIdConsecutivoAmpliacion as varchar) = '" + docto + "')");
        if (resp == 0)
            resp = stm.executeUpdate("update mPasivoContrato set nIdEstado = 2 where cIdContratoDefinitivo = '" + docto + "'");
        if (resp == 0)
            resp = stm.executeUpdate("update mPlurianualidadContrato set nIdEstado = 2 where cIdContratoDefinitivo = '" + docto + "'");
        if (resp == 0)
            resp = stm.executeUpdate("update mContratoModificado set nEstado = 2 where cContratoDefinitivo = '" + docto + "'");
    }

    /**
     * Inicia caso para cuando se requiera cancelar un pedazo de un documento
     * @param caso
     * @param conn
     * @param usuario
     * @param request
     * @param cEjercicio
     * @param today
     * @throws SQLException
     * @throws GestionException
     * @throws IOException
     */
    private Caso generaGuardaCaso(Usuario usuario, HttpServletRequest request, String cEjercicio, Integer tipoCaso) throws SQLException, GestionException, IOException {
        String folioCaso = null;
        Caso caso = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        Connection conn = null;
        try {
            caso = iniciaCaso(request, tipoCaso + "", usuario);
            conn = DataSourceManager.getConnection(jndiName);
            folioCaso = caso.getFolio();
            Map<String, String> datos = new HashMap<String, String>();
            datos.put("FOLIO", folioCaso);
            datos.put("FECHA_DOCUMENTO", today);
            datos.put("EJERCICIO_FISCAL", cEjercicio);
            datos.put("OPERADOR", usuario.getNombre());
            datos.put("CONCEPTO_MOV", "Liberacion Apartado");
            datos.put("MONEDA", "MXP");
            datos.put("APLICADO_CONT", "false");
            //Actualiza el caso en BD
            CasoDatoManager.update(conn, caso.getIdTC(), caso.getIdCaso(), datos);
            Caso sc = new Caso();
            sc.setIdCaso(caso.getIdCaso());
            caso = CasoManager.select(conn, sc);
            conn.commit();
        } catch (Exception exc) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(exc.getMessage(), exc);
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return caso;
    }

    /**
     * Inicia el caso para realizar cancelacion de apartado de las lineas de requisicion
     * @param req
     * @param tCaso
     * @param usuario
     * @return
     * @throws GestionException
     */
    private synchronized Caso iniciaCaso(HttpServletRequest req, String tCaso, Usuario usuario) throws GestionException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jndiName);
        if (tCaso == null) {
            log.error("Identificador de Tipo de Caso, vacio");
            throw new GestionException("Identificador de Tipo de Caso, vacio");
        }
        int idTC = Integer.parseInt(tCaso);
        if (idTC <= 0) {
            log.error("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
            throw new GestionException("Identificador de Tipo de Caso, menor o igual a cero (<= 0)");
        }
        Caso c = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new GestionException(exc);
        } catch (Exception exc) {
            log.error("algo raro paso", exc);
            exc.printStackTrace();
            throw new GestionException(exc);
        }
        c = casoTx.IniciaCaso(usuario, idTC, fg);
        log.error("Object: {}", usuario.getLogin() + "_" + idTC + "_" + fg);
        if (c == null) {
            log.error("No se logro crear el caso");
            throw new GestionException("No se logró crear el caso");
        }
        return c;
    }

    /**
     * Cierra la CONEXION
     */
    private void closeConnection() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        conn = null;
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.flush();
        out.close();
    }

    private void avanzaCaso(HttpServletRequest req, Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre) throws GestionException, ServletException, IOException {
        //Método para avanzar el caso. Se utliza tanto para pre-compromiso como para compromiso
        if (c == null) {
            log.error("Llamada invalida, sin Caso seleccionado");
            throw new GestionException("Llamada inválida, sin Caso seleccionado");
        }
        if (c.getIdCaso() <= 0) {
            log.error("Llamada invalida, identificador de caso menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, identificador de caso menor o igual a cero (<= 0)");
        }
        if (c.getCasoOperacion(0).getIdOperacion() <= 0) {
            log.error("Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)");
            throw new GestionException("Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)");
        }
        Map m = CasoDatoManager.readValuesCasoDato(req, c.getCasoDato(), true);
        CasoBusinessLogic cbl = new CasoBusinessLogic(jndiName);
        cbl.avanzaCaso(c, u.getLogin(), "", responsable, nombre, m, prefixPath);
    }
}
