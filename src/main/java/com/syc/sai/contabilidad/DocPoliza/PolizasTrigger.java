package com.syc.sai.contabilidad.DocPoliza;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.servlet.GestionServlet;
import java.util.Base64;

public class PolizasTrigger {

    @SuppressWarnings("finally")
    public HashMap<String, String> creaPolizaRefas(String FolioDocumento, Connection conn) {
        PreparedStatement prepareEsteMen = null;
        Statement estateMen = null;
        ResultSet rs = null;
        HashMap<String, String> returnVal = null;
        try {
            ArrayList<String[]> detalle = new ArrayList<String[]>();
            String nFolioDocPoliza = "";
            String cConceptoPoliza = "";
            BigDecimal importeTotal = new BigDecimal("0");
            String uCaptura = "";
            String uRevision = "";
            String uAutorizacion = "";
            String consultaGetDetalle = "";
            String cPartida = "";
            String fAplicacion = "";
            String nMesAplicacion = "";
            String cCentroContable = "";
            //uCaptura,uRevision,uAutorizacion,cConceptoPoliza,detalleRenglon,aEjercicioFiscal,fCarga,mImporte,subcuenta
            consultaGetDetalle = " select uCaptura, uRevision, uAutorizacion,'REINTEGRO DE AÑOS ANTERIORES CLC '+convert(varchar,noCLC)+' " + " EJERCICIO FISCAL '+CONVERT(VARCHAR,ejercicioRefas)+'  '+observaciones+'  REFA-'+convert(varchar,RE.cUnidadResponsable)+'-'+convert(varchar,RE.nFolioRefas) cConcepto,' ' detalle," + " fAplicacion,mImporte,null subcuenta, substring(EP,32,5) cPartida, RE.cCentroContable, datepart(Month,fAcredit) nMes from CTRL_DOC..tRefasEncabezado RE with(nolock) inner join CTRL_DOC..tRefasDetalle RD with(nolock) on RE.nFolioRefas=RD.nFolioRefas and RE.nFolioRefas=" + FolioDocumento;
            estateMen = conn.createStatement();
            estateMen.execute(consultaGetDetalle);
            rs = estateMen.getResultSet();
            int numEvent = 1;
            int nDocRenglon = 1;
            boolean first = true;
            while (rs.next()) {
                if (first) {
                    uCaptura = rs.getString("uCaptura");
                    uRevision = rs.getString("uRevision");
                    uAutorizacion = rs.getString("uAutorizacion");
                    cConceptoPoliza = rs.getString("cConcepto");
                    cPartida = rs.getString("cPartida");
                    fAplicacion = rs.getString("fAplicacion");
                    nMesAplicacion = rs.getString("nMes");
                    cCentroContable = rs.getString("cCentroContable");
                    nMesAplicacion = rs.getString("nMes");
                    first = !first;
                }
                importeTotal = importeTotal.add(new BigDecimal(rs.getString(7)));
                //nDocRenglon,nCuenta,nSubCuenta,cConcepto,cEvento,mimporte,cCABMS,cCUCOP,cPartida,nNumeroEvento
                detalle.add(new String[] { "" + (nDocRenglon++), "11191-00002-00000-00000", rs.getString("subcuenta"), rs.getString("detalle"), "CARGO", rs.getString("mImporte"), null, null, cPartida, numEvent + "" });
                detalle.add(new String[] { "" + (nDocRenglon++), "11393-00000-00000-00000", rs.getString("subcuenta"), rs.getString("detalle"), "ABONO", rs.getString("mImporte"), null, null, cPartida, numEvent + "" });
                numEvent++;
            }
            Usuario usuario = new Usuario();
            usuario.setLogin(uCaptura);
            usuario = UsuarioManager.select(conn, usuario);
            usuario = UsuarioManager.getRamoUR(conn, usuario);
            GestionServlet gs = new GestionServlet();
            Caso c = gs.iniciaCasoContable("13", usuario, cCentroContable);
            new OperacionManager().endOperacion(c);
            HashMap<String, String> fechas = getFechasPoliza(conn, cCentroContable);
            nFolioDocPoliza = c.getFolio().split("-")[2];
            String cTipoPoliza = "PD";
            StringBuffer QueryDocPolizaSB = new StringBuffer();
            QueryDocPolizaSB.append("set dateformat dmy; insert into tDocPolizaEncabezado(nFolioDocPoliza, cCentroContable, cRamo, cUnidadResponsable,   cTipoPoliza," + "aEjercicioFiscal, cUnidadResponsableContable,cDescripcionPoliza,cConcepto,	cIdUsuarioCaptura, cIdUsuarioRevision, cIdUsuarioAprobacion," + "mTotalCargos,mTotalAbonos,cTipoDocumento, nIdCasoOrigen,Periodo13,ADEFAS,nTipoAjuste,nFormatoPoliza,fCarga,fAplicacion,nMes," + "GrupoEvento,SubGrupoEvento,Evento) values( ");
            QueryDocPolizaSB.append(nFolioDocPoliza + ", ");
            QueryDocPolizaSB.append("'" + cCentroContable + "', ");
            QueryDocPolizaSB.append("'" + usuario.getU_Ramo() + "', ");
            QueryDocPolizaSB.append("'" + usuario.getU_UR() + "', ");
            QueryDocPolizaSB.append("'" + cTipoPoliza + "', ");
            QueryDocPolizaSB.append(fechas.get("aEjercicioFiscal") + ", ");
            QueryDocPolizaSB.append("'B00', ");
            QueryDocPolizaSB.append("'" + cConceptoPoliza + "', ");
            QueryDocPolizaSB.append("'" + cConceptoPoliza + "', ");
            QueryDocPolizaSB.append("'" + uCaptura + "', ");
            QueryDocPolizaSB.append("'" + uRevision + "', ");
            QueryDocPolizaSB.append("'" + uAutorizacion + "', ");
            QueryDocPolizaSB.append(importeTotal.toString() + ", ");
            QueryDocPolizaSB.append(importeTotal.toString() + ", ");
            QueryDocPolizaSB.append("'DOCPOLIZA'" + ", ");
            QueryDocPolizaSB.append("'" + c.getIdCaso() + "', ");
            QueryDocPolizaSB.append("'N', ");
            QueryDocPolizaSB.append("'N', ");
            QueryDocPolizaSB.append(0 + ", ");
            QueryDocPolizaSB.append(1 + ", ");
            QueryDocPolizaSB.append("convert(date,'" + fechas.get("fCarga") + "'), ");
            QueryDocPolizaSB.append("convert(date,'" + fAplicacion + "'), ");
            QueryDocPolizaSB.append(nMesAplicacion + ",");
            QueryDocPolizaSB.append("null,");
            QueryDocPolizaSB.append("null,");
            QueryDocPolizaSB.append("null");
            QueryDocPolizaSB.append(" ) ");
            estateMen.execute(QueryDocPolizaSB.toString());
            estateMen.execute("update CTRL_DOC..tRefasEncabezado set nFolioDocPoliza=" + nFolioDocPoliza + " where nFolioRefas=" + FolioDocumento);
            prepareEsteMen = conn.prepareStatement("set dateformat dmy; insert into tDocPolizaDetalle (nFoliodocpoliza,nDocRenglon,nCuenta,nSubCuenta, cConcepto,  parcial, cEvento, mimporte,  cCABMS, " + "cCUCOP, cPartida,nNumeroEvento,ccentrocontable,aejerciciofiscal,ctipopoliza,Periodo13,nIdGrupoEvento, nIdSubGrupoEvento,cIdEventoManual" + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            //nDocRenglon,nCuenta,nSubCuenta,cConcepto,cEvento,mimporte,cCABMS,cCUCOP,cPartida,nNumeroEvento
            String[] renglon = null;
            for (int i = 0; i < detalle.size(); i++) {
                renglon = detalle.get(i);
                prepareEsteMen.setString(1, nFolioDocPoliza);
                prepareEsteMen.setString(2, renglon[0]);
                prepareEsteMen.setString(3, renglon[1]);
                prepareEsteMen.setString(4, renglon[2]);
                prepareEsteMen.setString(5, renglon[3]);
                prepareEsteMen.setString(6, "N");
                prepareEsteMen.setString(7, renglon[4]);
                prepareEsteMen.setString(8, renglon[5]);
                prepareEsteMen.setString(9, renglon[6]);
                prepareEsteMen.setString(10, renglon[7]);
                prepareEsteMen.setString(11, renglon[8]);
                prepareEsteMen.setString(12, renglon[9]);
                prepareEsteMen.setString(13, cCentroContable);
                prepareEsteMen.setString(14, fechas.get("aEjercicioFiscal"));
                prepareEsteMen.setString(15, cTipoPoliza);
                prepareEsteMen.setString(16, fechas.get("periodo13"));
                prepareEsteMen.setString(17, null);
                prepareEsteMen.setString(18, null);
                prepareEsteMen.setString(19, null);
                prepareEsteMen.addBatch();
                prepareEsteMen.clearParameters();
            }
            prepareEsteMen.executeBatch();
            returnVal = callMotorContable(nFolioDocPoliza, conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            prepareEsteMen = null;
            estateMen = null;
            rs = null;
            return returnVal;
        }
    }

    /*Aplica todos los extemporaneos que falten con la fecha de aplicación del encabezado*/
    public void aplicaExtemporaneo() {
        Statement pstmnt = null;
        ResultSet rs = null;
        Connection conn = null;
        Connection connEFS = null;
        String aEjercicioFiscalSiguiente = "";
        try {
            conn = DataSourceManager.getConnection("jdbc/gestion");
            pstmnt = conn.createStatement();
            pstmnt.execute("SELECT aEjercicioFiscal+1 aEjercicioFiscal FROM TEJERCICIOFISCAL where cActivo=1");
            rs = pstmnt.getResultSet();
            rs.next();
            aEjercicioFiscalSiguiente = rs.getString("aEjercicioFiscal");
            conn.commit();
            pstmnt.execute("SELECT aEjercicioFiscal,cActivo,cNombreBD,cUserBD,cPassBD,cDireccionServer,cPuertoBD FROM sai_" + aEjercicioFiscalSiguiente + "..TEJERCICIOFISCAL where cActivo=1");
            rs = pstmnt.getResultSet();
            rs.next();
            connEFS = getConectionCatalogo(rs.getString("cDireccionServer"), rs.getString("cPuertoBD"), rs.getString("cNombreBD"), rs.getString("cUserBD"), rs.getString("cPassBD"));
            /*Se queda esto por si el usuario llega a pedir que se aplique con otra fecha.*/
            pstmnt.execute("select distinct faplicacion, datepart(MONTH,faplicacion)nMes, RE.nFolioReintegroaut,aEjercicioFiscal+1 aEjercicioFiscal from tReintegroAutEncabezado RE with(nolock) inner join tReintegroAutDetalle RD with(nolock) on RE.nFolioReintegroaut=RD.nFolioReintegroaut and RE.cDocumentoHaplicado='S' and RD.cEvento='SINSALDO' and cTipoReintegro is null and nFolioPolizaCancelacion is null");
            rs = pstmnt.getResultSet();
            PolizasTrigger pt = new PolizasTrigger();
            while (rs.next()) {
                pt.creaReintegroExtemporaneo(rs.getString("faplicacion"), rs.getString("nMes"), rs.getString("nFolioReintegroaut"), rs.getString("aEjercicioFiscal"), conn, connEFS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (pstmnt != null)
                try {
                    pstmnt.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            rs = null;
            pstmnt = null;
            conn = null;
        }
    }

    public void aplicaExtemporaneo(String nFolioReintegroAut) {
        Statement pstmnt = null;
        ResultSet rs = null;
        Connection conn = null;
        Connection connEFS = null;
        String aEjercicioFiscalSiguiente = "";
        String fAplicacion = null;
        String nMes = null;
        try {
            conn = DataSourceManager.getConnection("jdbc/gestion");
            pstmnt = conn.createStatement();
            pstmnt.execute("SELECT aEjercicioFiscal+1 aEjercicioFiscal, convert(date,GETDATE())fAplicacion, datepart(MONTH,GETDATE()) nMes FROM TEJERCICIOFISCAL where cActivo=1");
            rs = pstmnt.getResultSet();
            rs.next();
            aEjercicioFiscalSiguiente = rs.getString("aEjercicioFiscal");
            fAplicacion = rs.getString("faplicacion");
            nMes = rs.getString("nMes");
            conn.commit();
            pstmnt.execute("SELECT aEjercicioFiscal,cActivo,cNombreBD,cUserBD,cPassBD,cDireccionServer,cPuertoBD FROM sai_" + aEjercicioFiscalSiguiente + "..TEJERCICIOFISCAL where cActivo=1");
            rs = pstmnt.getResultSet();
            rs.next();
            connEFS = getConectionCatalogo(rs.getString("cDireccionServer"), rs.getString("cPuertoBD"), rs.getString("cNombreBD"), rs.getString("cUserBD"), rs.getString("cPassBD"));
            PolizasTrigger pt = new PolizasTrigger();
            pt.creaReintegroExtemporaneo(fAplicacion, nMes, nFolioReintegroAut, aEjercicioFiscalSiguiente, conn, connEFS);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmnt.close();
                if (conn != null)
                    conn.close();
                if (connEFS != null)
                    connEFS.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
            pstmnt = null;
            conn = null;
            connEFS = null;
        }
    }

    private Connection getConectionCatalogo(String server, String port, String bd, String user, String pass) {
        Connection conn = null;
        String url = null;
        try {
            url = "jdbc:jtds:sqlserver://" + server + ":" + port + "/" + bd;
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    @SuppressWarnings("finally")
    public HashMap<String, String> creaReintegroExtemporaneo(String fAplicacion, String nMesAplicacion, String FolioDocumento, String aEjercicioFiscalSiguiente, Connection conn, Connection connEFS) {
        PreparedStatement prepareEsteMen = null;
        Statement estateMen = null;
        Statement stPago = null;
        ResultSet rs = null;
        ResultSet rsPago = null;
        HashMap<String, String> returnVal = null;
        /*Se pone Query completo para no armarlo*/
        HashMap<String, String> cConceptoPagos = getConceptosPagos();
        try {
            /*		
			ArrayList <String[]> detalle= new ArrayList<String[]>();
			String nFolioDocPoliza="";			
			String cConceptoPoliza="";			
			BigDecimal importeTotal= new BigDecimal("0");
			String uCaptura="";
			String uRevision="";
			String uAutorizacion="";				
			String consultaGetDetalle="";
			String cPartida="";			
			String cCentroContable="";
			String detalleMovimiento="";
			String renglonReintegro="";
			String cUnidadResponsable="";
			
			//uCaptura,uRevision,uAutorizacion,cConceptoPoliza,detalleRenglon,aEjercicioFiscal,fCarga,mImporte,subcuenta			
			consultaGetDetalle="select RE.nFolioReintegroaut, RE.U_LOGIN uCaptura, RE.U_LOGIN uRevision, RE.U_LOGIN uAutorizacion,'REINTEGRO DE AÑOS ANTERIORES EJERCICIO FISCAL '+" +
					"CONVERT(VARCHAR,aEjercicioFiscal)+' - '+concepto+'  AVI-'+convert(varchar,RE.cUnidadResponsable)+'-'+convert(varchar,RE.nFolioReintegroaut)" +
					"cConcepto, mImporte,null subcuenta, substring(EP,32,5) cPartida, RD.cCentroContable, EE.cTipoPago, EE.nFolioSIAFF,RD.nDocRenglon,RE.cUnidadResponsable from tReintegroAutEncabezado RE with(nolock) inner join tReintegroAutDetalle RD with(nolock) " +
					"on RE.nFolioReintegroaut=RD.nFolioReintegroaut inner join tEjercidoEncabezado EE with(nolock) on RD.noCLC=EE.nFolioSIAFF where RE.nFolioReintegroaut="+FolioDocumento;											
			estateMen=conn.createStatement();
			stPago=conn.createStatement();
			estateMen.execute(consultaGetDetalle);
			rs=estateMen.getResultSet();
			int numEvent=1;
			int nDocRenglon=1;						
			boolean first=true;
			
			while(rs.next())
			{				
				if(first)
				{								
					uCaptura=rs.getString("uCaptura");
					uRevision=rs.getString("uRevision");
					uAutorizacion=rs.getString("uAutorizacion");
					cConceptoPoliza=rs.getString("cConcepto");					
					cPartida=rs.getString("cPartida");			
					cCentroContable=rs.getString("cCentroContable");		
					cUnidadResponsable=rs.getString("cUnidadResponsable");	
					first=!first;
				}
				*/
            /*Renglon del reintegro que se está aplicando*/
            //			    renglonReintegro=rs.getString("nDocRenglon");
            /*Se va al documento del pago para armar la descripción del detalle */
            /*
				stPago.execute(cConceptoPagos.get(rs.getString("cTipoPago"))+rs.getString("nFolioSIAFF"));
				rsPago=stPago.getResultSet();
				rsPago.next();
				detalleMovimiento=rsPago.getString(1);
				
				importeTotal=importeTotal.add(new BigDecimal(rs.getString("mImporte")));
				//nDocRenglon,nCuenta,nSubCuenta,cConcepto,cEvento,mimporte,cCABMS,cCUCOP,cPartida,nNumeroEvento 
				detalle.add(new String[]{""+(nDocRenglon++),"11191-00002-00000-00000",rs.getString("subcuenta"),detalleMovimiento,"CARGO",rs.getString("mImporte"),null,null,cPartida,numEvent+""});
				detalle.add(new String[]{""+(nDocRenglon++),"11393-00000-00000-00000",rs.getString("subcuenta"),detalleMovimiento,"ABONO",rs.getString("mImporte"),null,null,cPartida,numEvent+""});
				
				numEvent++;					
			}	
			
			stPago.close();
			rsPago.close();
			
			Usuario usuario= new Usuario();			
			usuario.setLogin(uCaptura);											
			usuario=UsuarioManager.select(conn, usuario);
			usuario=UsuarioManager.getRamoUR(connEFS, usuario);
			
			Caso c=com.syc.sai.contabilidad.CasoContable.nuevoCaso(connEFS, usuario,cCentroContable, 13);
			com.syc.sai.contabilidad.CasoContable.finalizaPoliza(c,usuario,connEFS);
			
			nFolioDocPoliza=c.getFolio().split("-")[2];
			String cTipoPoliza="PD";
			StringBuffer QueryDocPolizaSB= new StringBuffer();
			QueryDocPolizaSB.append("set dateformat dmy; insert into sai_"+aEjercicioFiscalSiguiente+"..tDocPolizaEncabezado(nFolioDocPoliza, cCentroContable, cRamo, cUnidadResponsable,   cTipoPoliza,"
										  +"aEjercicioFiscal, cUnidadResponsableContable,cDescripcionPoliza,cConcepto,	cIdUsuarioCaptura, cIdUsuarioRevision, cIdUsuarioAprobacion,"
										  +"mTotalCargos,mTotalAbonos,cTipoDocumento, nIdCasoOrigen,Periodo13,ADEFAS,nTipoAjuste,nFormatoPoliza,fCarga,fAplicacion,nMes,"
										  +"GrupoEvento,SubGrupoEvento,Evento) values( ");
		
			QueryDocPolizaSB.append(nFolioDocPoliza+", ");
			QueryDocPolizaSB.append("'"+cCentroContable+"', ");
			QueryDocPolizaSB.append("'16', ");				
			QueryDocPolizaSB.append("'"+cUnidadResponsable+"', ");
			QueryDocPolizaSB.append("'"+cTipoPoliza+"', ");
			QueryDocPolizaSB.append(aEjercicioFiscalSiguiente+", ");
			QueryDocPolizaSB.append("'B00', ");
			QueryDocPolizaSB.append("'"+cConceptoPoliza+"', ");
			QueryDocPolizaSB.append("'"+cConceptoPoliza+"', ");
			QueryDocPolizaSB.append("'"+uCaptura+"', ");
			QueryDocPolizaSB.append("'"+uRevision+"', ");
			QueryDocPolizaSB.append("'"+uAutorizacion+"', ");
			QueryDocPolizaSB.append(importeTotal.toString()+", ");
			QueryDocPolizaSB.append(importeTotal.toString()+", ");
			QueryDocPolizaSB.append("'DOCPOLIZA'"+", ");
			QueryDocPolizaSB.append("'"+c.getIdCaso()+"', ");
			QueryDocPolizaSB.append("'N', "); 
			QueryDocPolizaSB.append("'N', ");
			QueryDocPolizaSB.append("1, ");
			QueryDocPolizaSB.append(1+", ");
			QueryDocPolizaSB.append("convert(date, GETDATE()), ");
			QueryDocPolizaSB.append("convert(date,'"+fAplicacion+"'), ");
			QueryDocPolizaSB.append(nMesAplicacion+",");
			QueryDocPolizaSB.append("null,");
			QueryDocPolizaSB.append("null,");
			QueryDocPolizaSB.append("null");
			QueryDocPolizaSB.append(" ) ");
									
			estateMen.execute(QueryDocPolizaSB.toString());			
			estateMen.execute("update tReintegroAutEncabezado set cTipoReintegro='EXTEMP', nFolioPolizaCancelacion="+nFolioDocPoliza+" where nFolioReintegroaut="+FolioDocumento);
			prepareEsteMen=conn.prepareStatement("set dateformat dmy; insert into sai_"+aEjercicioFiscalSiguiente+"..tDocPolizaDetalle (nFoliodocpoliza,nDocRenglon,nCuenta,nSubCuenta, cConcepto,  parcial, cEvento, mimporte,  cCABMS, "
				+"cCUCOP, cPartida,nNumeroEvento,ccentrocontable,aejerciciofiscal,ctipopoliza,Periodo13,nIdGrupoEvento, nIdSubGrupoEvento,cIdEventoManual,nTipoAjuste"
				+") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
   						
			//nDocRenglon,nCuenta,nSubCuenta,cConcepto,cEvento,mimporte,cCABMS,cCUCOP,cPartida,nNumeroEvento 
			String [] renglon=null;
			for(int i=0;i<detalle.size();i++)
			{
				renglon=detalle.get(i);
				
				prepareEsteMen.setString(1,nFolioDocPoliza);
				prepareEsteMen.setString(2,renglon[0]);
				prepareEsteMen.setString(3,renglon[1]);
				prepareEsteMen.setString(4,renglon[2]);
				prepareEsteMen.setString(5,renglon[3]);
				prepareEsteMen.setString(6,"N");
				prepareEsteMen.setString(7,renglon[4]);
				prepareEsteMen.setString(8,renglon[5]);
				prepareEsteMen.setString(9,renglon[6]);
				prepareEsteMen.setString(10,renglon[7]);
				prepareEsteMen.setString(11,renglon[8]);
				prepareEsteMen.setString(12,renglon[9]);
				prepareEsteMen.setString(13,cCentroContable);
				prepareEsteMen.setString(14,aEjercicioFiscalSiguiente);
				prepareEsteMen.setString(15,cTipoPoliza);
				prepareEsteMen.setString(16,"N");
				prepareEsteMen.setString(17,null);
				prepareEsteMen.setString(18,null);
				prepareEsteMen.setString(19,null);
				prepareEsteMen.setString(20,"0");
				
				prepareEsteMen.addBatch();
				prepareEsteMen.clearParameters();
			}
			
			prepareEsteMen.executeBatch();				
			conn.commit();			
			returnVal=callMotorContable( nFolioDocPoliza, connEFS);		
		*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            prepareEsteMen = null;
            estateMen = null;
            stPago = null;
            rsPago = null;
            rs = null;
            return returnVal;
        }
    }

    public HashMap<String, String> getConceptosPagos() {
        HashMap<String, String> cConceptoPagos = new HashMap<String, String>();
        cConceptoPagos.put("PAGODIRECTO", "select 'CxP: '+rtrim(Pa.caNoContrarrecibo)+'  -Documento: '+ cIdDocumento from tPagoDirectoEncabezado PA with(Nolock) " + "inner join tEjercidoEncabezado EE with(nolock) on Pa.nFolioPagoDirecto=EE.nFolioPAGO and nFolioSIAFF=");
        cConceptoPagos.put("PAGODIVERSO", "select 'CxP: '+rtrim(Pa.caNoContrarrecibo)+'  -Folio:'+rtrim(cFolioPAGODIVERSO)+'   Factura: '+rtrim(cNoFactura)+'  " + "-SIAFF: '+convert(varchar,nFolioSIAFF)+' -SICOP: '+convert(varchar,nFolioSICOP) from tPagoDiversoEncabezado PA with(Nolock) " + "inner join tEjercidoEncabezado EE with(nolock) on Pa.nFolioPAGODIVERSO=EE.nFolioPAGO and nFolioSIAFF=");
        cConceptoPagos.put("RELACIONGASTOS", "select 'CxP: '+rtrim(Pa.caNoContrarrecibo)+'  -Relación:'+cIdRelacion from tRELACIONGASTOSEncabezado PA with(Nolock) " + "inner join tEjercidoEncabezado EE with(nolock) on Pa.nFolioRELACIONGASTOS=EE.nFolioPAGO and nFolioSIAFF=");
        cConceptoPagos.put("FEDERALIZADO", "select 'CxP: '+rtrim(Pa.caNoContrarrecibo)+'  -Folio: '+rtrim(cFolioContratoObra)+'  -Estimación: '+rtrim(ltrim(cNoEstimacion))+'" + "  -Factura: '+rtrim(ltrim(cNoFactura))+'  -SIAFF: '+convert(varchar,nFolioSIAFF)+' -SICOP: '+convert(varchar,nFolioSICOP) from" + " tPAGOFEDERALIZADOEncabezado PA with(nolock) inner join tEjercidoEncabezado EE with(nolock) on " + "Pa.nfolioPagoFederalizado=EE.nFolioPAGO and nFolioSIAFF=");
        cConceptoPagos.put("PAGOOBRA", "select 'CxP: '+rtrim(Pa.caNoContrarrecibo)+'  -Folio: '+cFolioContratoObra+'  -Factura: '+rtrim(ltrim(cNoFactura)) + '  -Estimación: " + "'+rtrim(ltrim(cNoEstimacion))+'  -SIAFF: '+convert(varchar,nFolioSIAFF)+' -SICOP: '+convert(varchar,nFolioSICOP)  from " + "tPAGOOBRAEncabezado PA with(Nolock) inner join tEjercidoEncabezado EE with(nolock) on Pa.nFolioPAGOOBRA=EE.nFolioPAGO and nFolioSIAFF=");
        cConceptoPagos.put("AJENAS", "select 'CxP: '+rtrim(Pa.caNoContrarrecibo)+' -Documento: '+cNombre +'  -SIAFF: '+convert(varchar,nFolioSIAFF)+' -SICOP: '" + "+convert(varchar,EE.nFolioSICOP)  from tOperAjenasEncabezado PA with(nolock)inner join tEjercidoEncabezado EE with(nolock) on " + "Pa.nFolioOperAjenas=EE.nFolioPAGO and nFolioSIAFF=");
        return cConceptoPagos;
    }

    public String getConceptoByTipoPago(Connection conn, String tipoPago, String nFolioSIAFF) throws SQLException {
        ResultSet rsPago = null;
        HashMap<String, String> cConceptoPagos = getConceptosPagos();
        Statement stPago = conn.createStatement();
        stPago.execute(cConceptoPagos.get(tipoPago) + nFolioSIAFF);
        rsPago = stPago.getResultSet();
        rsPago.next();
        return rsPago.getString(1);
    }

    @SuppressWarnings("finally")
    public HashMap<String, String> getFechasPoliza(Connection conn, String cCentroContable) {
        HashMap<String, String> fechas = new HashMap<String, String>();
        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar c1 = Calendar.getInstance();
        fechas.put("fCarga", sdf.format(c1.getTime()));
        Statement estateMen = null;
        ResultSet rs = null;
        try {
            estateMen = conn.createStatement();
            estateMen.execute("select nMes,aEjercicioFiscal from tMesesContables with(nolock) where mesAbierto='S' and cCentroContable='" + cCentroContable + "'");
            rs = estateMen.getResultSet();
            rs.next();
            int mesAbierto = rs.getInt(1);
            int aEjercicioFiscal = rs.getInt(2);
            int mesActual = c1.get(Calendar.MONTH) + 1;
            fechas.put("periodo13", mesAbierto == 13 ? "S" : "N");
            if (mesAbierto != mesActual) {
                if (mesAbierto != mesActual) {
                    if (mesAbierto == 13) {
                        c1.set(Calendar.YEAR, aEjercicioFiscal);
                        c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_YEAR));
                        c1.set(Calendar.MONTH, 12);
                    } else {
                        c1.set(Calendar.YEAR, aEjercicioFiscal);
                        c1.set(Calendar.DAY_OF_MONTH, 1);
                        c1.set(Calendar.MONTH, mesAbierto - 1);
                        c1.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }
                }
            }
            fechas.put("fechaAplicacion", sdf.format(c1.getTime()));
            fechas.put("nMes", mesAbierto == 13 ? "12" : mesAbierto + "");
            fechas.put("aEjercicioFiscal", aEjercicioFiscal + "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (estateMen != null)
                    estateMen.close();
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            estateMen = null;
            rs = null;
            return fechas;
        }
    }

    @SuppressWarnings("finally")
    public HashMap<String, String> callMotorContable(String nFolioDocPoliza, Connection conn) {
        HashMap<String, String> ResultadoOperacion = new HashMap<String, String>();
        /*AccountingEngineNew accEng = new AccountingEngineNew();
		Statement estateMen=null;
		ResultSet rs=null;
		*/
        try {
            /*
		if(accEng.makeAccountingApplicationWithoutEvent(conn,"DocPoliza",nFolioDocPoliza,"tDocPolizaEncabezado","tDocPolizaDetalle","nFolioDocPoliza"))
			conn.commit();
		else
			throw new Exception();
		
		ResultadoOperacion.put("estatus", "true");
		estateMen=conn.createStatement();
		estateMen.execute("select nFolioPoliza, cTipoPoliza, cCentroContable from tDocPolizaEncabezado with(nolock) where nFolioDocPoliza="+nFolioDocPoliza);
		rs=estateMen.getResultSet();
		
		if(rs.next())
			{
				ResultadoOperacion.put("nFolioPoliza", rs.getString(1));
				ResultadoOperacion.put("cTipoPoliza", rs.getString(2));
				ResultadoOperacion.put("cCentroContable", rs.getString(3));
			}
			*/
        } catch (Exception e) {
            ResultadoOperacion.put("estatus", "false");
            e.printStackTrace();
        } finally {
            /*try
			{
				if(estateMen!=null)
					estateMen.close();
				if(rs!=null)
					rs.close();
			}
			catch(Exception e){}
			
			estateMen=null;
			rs=null;
			*/
            return ResultadoOperacion;
        }
    }
}
