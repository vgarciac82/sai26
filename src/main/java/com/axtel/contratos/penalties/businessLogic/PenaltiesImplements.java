package com.axtel.contratos.penalties.businessLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.penalties.PenaltiesInterface;
import com.axtel.contratos.penalties.core.DeductionItems;
import com.axtel.contratos.penalties.core.PenaltyAndDeduction;
import com.axtel.contratos.penalties.core.PenaltyItems;
import com.axtel.contratos.penalties.exception.PenaltiesExceptions;
import com.axtel.contratos.penalties.manager.PenaltiesManager;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.manager.ProcessAgreementManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PenaltiesImplements extends DataSourceManager implements PenaltiesInterface {

    private static Logger log = LoggerFactory.getLogger(PenaltiesImplements.class);

    @Override
    public JSONObject queryPenalties(PenaltyAndDeduction penalty) throws PenaltiesExceptions, JSONException {
        // Falta agregar bitacora de movimientos
        boolean error = true;
        Connection conn = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            jsonObj = new JSONObject();
            conn = getConnection(GestionInterface.ATT_CONEXION);
            query.append("Select penaltyDeduction.nIdPenaltyDeduction,nIdEstate,cCaptureUser,lPenalty checkPenaConv,lDeduction checkDeduccion  ");
            query.append(",isnull(penalty.mAmountPenaltyTotal,0)mAmountPenaltyTotal,isnull(deduction.mAmountDeductionTotal,0)mAmountDeductionTotal  ");
            query.append(",penaltyDeduction.cIdContratoDefinitivo pedidoContratoCompromiso,cont.cIdTipoContrato cIdTipoContrato ,penaltyDeduction.cDocumentHAplicado ");
            query.append(",penaltyDeduction.cObservations,penaltyDeduction.nIdPeriod cPeriodo,penaltyDeduction.cConcept cConcepto,penaltyDeduction.cNumberJob cOficio ");
            query.append(",cont.cNoContratoCNET cNumCNET,cont.proveedor cProveedor ");
            query.append("from mPenaltyDeduction penaltyDeduction with(Nolock) ");
            query.append("inner join( ");
            query.append("	select '[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as proveedor,cNoContratoCNET,cIdContratoDefinitivo,cIdTipoContrato  ");
            query.append("	from mContrato as cont with(Nolock) ");
            query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append("	on prov.cIdRFC=cont.cIdRFC ");
            query.append("	where nIdEstado=4 ");
            query.append("	union ");
            query.append("	select '[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as proveedor,cNoContratoCNET,cIdContratoDefinitivo,cIdTipoContrato  ");
            query.append("	from mPlurianualidadContrato as cont with(Nolock) ");
            query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append("	on prov.cIdRFC=cont.cIdRFC ");
            query.append("	where nIdEstado=4 ");
            query.append("	union ");
            query.append("	select '[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as proveedor, cNoConvenio as cNoContratoCNET,cIdContratoDefinitivo,SUBSTRING(cont.cIdContrato,1,2) cIdTipoContrato ");
            query.append("	from mContratoModificado as cont with(Nolock)");
            query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append("	on prov.cIdRFC=cont.cIdRFC ");
            query.append("	where nEstado=4 and isConvEjercicioAnt=1 ");
            query.append("	union ");
            query.append("	select '[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as proveedor,cNoContratoCNET,cIdContratoDefinitivo,SUBSTRING(cont.cIdContratoDefinitivo,1,2)  cIdTipoContrato  ");
            query.append("	from mContratoRemanenteEjercicioAnterior as cont with(Nolock) ");
            query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append("	on replace(prov.cIdRFC,'-','')=cont.cIdRFC ");
            query.append("	where nEstado=4 ");
            query.append("	union ");
            query.append("	select '[ '+cont.cIdRFC+' ] '+prov.cRazonSocial as proveedor,cNoContratoCNET,cIdContratoDefinitivo,cIdTipoContrato  ");
            query.append("	from mContratoArt25 as cont with(Nolock) ");
            query.append("	inner join mCatalogoProveedor as prov with(Nolock) ");
            query.append("	on prov.cIdRFC=cont.cIdRFC ");
            query.append("	where nIdEstado=4 ");
            query.append(")cont on cont.cIdContratoDefinitivo=penaltyDeduction.cIdContratoDefinitivo ");
            query.append("left join( ");
            query.append("	select nIdPenaltyDeduction,sum(mAmountPenalty)mAmountPenaltyTotal  ");
            query.append("	from mPenaltyItems with(Nolock) group by nIdPenaltyDeduction  ");
            query.append(")penalty on penalty.nIdPenaltyDeduction=penaltyDeduction.nIdPenaltyDeduction  ");
            query.append("left join( ");
            query.append("	select nIdPenaltyDeduction,sum(mAmountDeduction)mAmountDeductionTotal  ");
            query.append("	from mDeductionItems with(Nolock)group by nIdPenaltyDeduction  ");
            query.append(")deduction on deduction.nIdPenaltyDeduction=penaltyDeduction.nIdPenaltyDeduction ");
            query.append("where cFolio='" + penalty.getcFolio() + "' ");
            log.info(query);
            arrayObj = Util.datGuardados(conn, query.toString());
            jsonObj.put("penaltyDeductionSave", arrayObj);
            arrayObj = null;
            query = null;
            query = new StringBuilder();
            // Catalogo periodo
            query.append("select 'Favor de seleccionar el per\u00edodo en el que se originan las penas o deducciones'cPeriodo,0 nIdPeriodo union select cPeriodo,nIdPeriodo from mCatalogoPeriodo with(Nolock)");
            arrayObj = Util.obtieneDatQuery(conn, query.toString());
            jsonObj.put("catPeriodo", arrayObj);
            arrayObj = null;
            query = null;
            query = new StringBuilder();
            // Partidas de contrato
            query.append(" select	convert(varchar,part.nIdLineaConsolidado)+'.- '+part.cDescripcionAdicional as descrip ");
            query.append(" ,part.nIdLineaConsolidado ");
            query.append(" from mContrato cont with(Nolock) ");
            query.append(" inner join mProcedimientoAdjudicacionPartidas as part with(Nolock) ");
            query.append(" on cont.cIdTipoProcedimiento=part.cIdTipoProcedimiento ");
            query.append(" and cont.cIdUnidadEjecutora=part.cIdUnidadEjecutora ");
            query.append(" and cont.nIdConsecutivoProcedimiento=part.nIdConsecutivo ");
            query.append(" and cont.cIdRFC=part.cIdRFC ");
            query.append(" and cont.cIdProcedimiento=part.cIdProcedimiento ");
            query.append(" and cont.nIdconsecutivoAdj=part.nIdconsecutivoAdj ");
            query.append(" inner join mPenaltyDeduction penalty with(Nolock) ");
            query.append(" on penalty.cIdContratoDefinitivo=cont.cIdContratoDefinitivo ");
            query.append(" where penalty.cFolio='" + penalty.getcFolio() + "' ");
            query.append(" union ");
            query.append(" select  ");
            query.append(" convert(varchar,part.nIdLineaConsolidado)+'.- '+part.cDescripcionAdicional descrip ");
            query.append(" ,part.nIdLineaConsolidado ");
            query.append(" from mPlurianualidadContrato as contPlu with(Nolock) ");
            query.append(" inner join mPartidasContratoPlurianual as part with(Nolock) ");
            query.append(" on contPlu.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append(" inner join mPenaltyDeduction penalty with(Nolock) ");
            query.append(" on penalty.cIdContratoDefinitivo=contPlu.cIdContratoDefinitivo ");
            query.append(" where penalty.cFolio='" + penalty.getcFolio() + "' ");
            query.append(" union ");
            query.append(" select convert(varchar,part.nIdLineaConsolidado)+'.- '+part.cDescripcion descrip  ");
            query.append(" ,part.nIdLineaConsolidado ");
            query.append(" from mContratoModificado as cont with(Nolock) ");
            query.append(" inner join mContratoModificadoPartida as part with(Nolock)  ");
            query.append(" on cont.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append(" inner join mPenaltyDeduction penalty with(Nolock) ");
            query.append(" on penalty.cIdContratoDefinitivo=cont.cIdContratoDefinitivo  ");
            query.append(" where penalty.cFolio='" + penalty.getcFolio() + "' ");
            query.append(" union ");
            query.append(" select convert(varchar,part.nIdLineaConsolidado)+'.- '+part.cDescripcion descrip  ");
            query.append(" ,part.nIdLineaConsolidado ");
            query.append(" from mContratoRemanenteEjercicioAnterior as cont with(Nolock) ");
            query.append(" inner join mContratoRemanenteEjercicioAnteriorPartida as part with(Nolock)  ");
            query.append(" on cont.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append(" inner join mPenaltyDeduction penalty with(Nolock) ");
            query.append(" on penalty.cIdContratoDefinitivo=cont.cIdContratoDefinitivo  ");
            query.append(" where penalty.cFolio='" + penalty.getcFolio() + "' ");
            query.append(" union ");
            query.append(" select convert(varchar,part.nIdLineaConsolidado)+'.- '+part.cDescripcionAdicional descrip  ");
            query.append(" ,part.nIdLineaConsolidado ");
            query.append(" from mContratoArt25 as cont with(Nolock) ");
            query.append(" inner join mContratoArt25Partidas as part with(Nolock)  ");
            query.append(" on cont.cIdContratoDefinitivo=part.cIdContratoDefinitivo ");
            query.append(" inner join mPenaltyDeduction penalty with(Nolock) ");
            query.append(" on penalty.cIdContratoDefinitivo=cont.cIdContratoDefinitivo  ");
            query.append(" where penalty.cFolio='" + penalty.getcFolio() + "' ");
            arrayObj = Util.obtieneDatQuery(conn, query.toString());
            jsonObj.put("cPartidaPenalty", arrayObj);
            jsonObj.put("cPartidaDeduction", arrayObj);
            arrayObj = null;
            query = null;
            conn.commit();
            error = false;
            return jsonObj;
        } catch (SQLException e) {
            log.error("Bug, consulting penalities and deductions: " + e);
            throw new PenaltiesExceptions("Bug, consulting penalities and deductions: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            arrayObj = null;
        }
    }

    @Override
    public void savePenalties(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        PenaltiesManager manager = null;
        ArrayList<PenaltyItems> penaltiesItems = null;
        ArrayList<DeductionItems> deductionsItems = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            validateData(penalty);
            manager.existPenaltyAndDeduction(conn, penalty);
            if (penalty.getlExist() == 1) {
                manager.updatePenaltyAndDeduction(conn, penalty);
            } else {
                manager.addPenaltyAndDeduction(conn, penalty);
            }
            manager.deletePenaltyItems(conn, penalty);
            manager.deleteDeductionItems(conn, penalty);
            if (penalty.getlPenalty() == 1) {
                // Add items penalties
                penaltiesItems = penalty.getPenaltyItems();
                for (PenaltyItems item : penaltiesItems) {
                    item.setnIdPenaltyDeduction(penalty.getnIdPenaltyDeduction());
                    manager.addPenaltyItems(conn, item, penalty.getcTipoContrato());
                }
            }
            if (penalty.getlDeduction() == 1) {
                // Add items deductions
                deductionsItems = penalty.getDeductionItems();
                for (DeductionItems item : deductionsItems) {
                    item.setnIdPenaltyDeduction(penalty.getnIdPenaltyDeduction());
                    manager.addDeductionItems(conn, item, penalty.getcTipoContrato());
                }
            }
            if ((penaltiesItems == null || penaltiesItems.size() == 0) && (deductionsItems == null || deductionsItems.size() == 0)) {
                throw new PenaltiesExceptions("Favor de capturar los datos de la penalizaci\u00f3n o deducci\u00f3n de cada partida de contrato.");
            }
            conn.commit();
            error = false;
        } catch (SQLException e) {
            log.error("Bug, savePenalties: " + e);
            throw new PenaltiesExceptions("Bug, savePenalties: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback savePenalties: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback savePenalties: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    @Override
    public void deletePenalties(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        PenaltiesManager manager = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            manager.deleteDeductionItems(conn, penalty);
            manager.deletePenaltyItems(conn, penalty);
            manager.resetPenaltyAndDeductions(conn, penalty);
            conn.commit();
            error = false;
        } catch (SQLException e) {
            log.error("Bug, deleting penalities and deductions: " + e);
            throw new PenaltiesExceptions("Bug, deleting penalities and deductions: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback deletePenalties: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback deletePenalties: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    @Override
    public Respuesta areThereDocuments(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        boolean error = true;
        Connection conn = null;
        PenaltiesManager manager = null;
        Respuesta response = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            response = manager.areThereDocuments(conn, penalty.getcFolio());
            conn.commit();
            error = false;
            return response;
        } catch (SQLException e) {
            log.error("Bug, : " + e);
            throw new PenaltiesExceptions("Bug, deleting penalities and deductions: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback deletePenalties: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback deletePenalties: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    @Override
    public void sendPenalty(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        PenaltiesManager manager = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            manager.updateAplicateDocumentAndState(conn, penalty);
            manager.updateObservations(conn, penalty);
            penalty.setcEmailCaptureUsser(manager.getEmailUsser(conn, penalty.getcCaptureUsser()));
            conn.commit();
        } catch (SQLException e) {
            log.error("Bug, : " + e);
            throw new PenaltiesExceptions("Bug, sendPenalty: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback sendPenalty: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback sendPenalty: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    @Override
    public void validatePenalty(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        PenaltiesManager manager = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            manager.saveValidatePenaltyAndDeductions(conn, penalty);
            conn.commit();
        } catch (SQLException e) {
            log.error("Bug, : " + e);
            throw new PenaltiesExceptions("Bug, sendPenalty: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback sendPenalty: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback sendPenalty: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    private void validateData(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        StringBuilder msg = new StringBuilder();
        String token = "";
        try {
            if ("".equalsIgnoreCase(penalty.getcConcepto())) {
                msg.append("Favor de capturar el concepto por el cual se generan las penas o deducciones.");
                token = "\n";
            }
            if ("".equalsIgnoreCase(penalty.getcOficio())) {
                msg.append(token);
                msg.append("Favor de capturar el n\u00famero de oficio.");
                token = "\n";
            }
            if (penalty.getnPeriodo() == 0) {
                msg.append(token);
                msg.append("Favor de seleccionar el per\u00edodo en el que se originan las penas o deducciones.");
            }
            if (!"".equalsIgnoreCase(msg.toString())) {
                throw new PenaltiesExceptions(msg.toString());
            }
        } finally {
            msg.delete(0, msg.length());
            msg = null;
            token = null;
        }
    }

    @Override
    public void addRowPenaltyItem(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        PenaltiesManager manager = null;
        int nConsecutive = 0;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            // get Max consecutive
            nConsecutive = manager.getConsecutiveItemPenalty(conn, penalty);
            if (0 == nConsecutive) {
                throw new PenaltiesExceptions("Favor de primero capturar los montos de los registros que ya hay agregados y luego agregar los que se necesiten.");
            }
            penalty.getPenaltyItems().get(0).setnConsecutiveItem(nConsecutive + 1);
            manager.addPenaltyRowItems(conn, penalty);
            conn.commit();
        } catch (SQLException e) {
            log.error("Bug, : " + e);
            throw new PenaltiesExceptions("Bug, addRowPenaltyItem: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback addRowPenaltyItem: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback addRowPenaltyItem: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    @Override
    public void addRowDeductionItem(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        PenaltiesManager manager = null;
        int nConsecutive = 1;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            manager = new PenaltiesManager();
            // get Max consecutive
            nConsecutive = manager.getConsecutiveItemDeduction(conn, penalty);
            if (0 == nConsecutive) {
                throw new PenaltiesExceptions("Favor de primero capturar los montos de los registros que ya hay agregados y luego agregar los que se necesiten.");
            }
            penalty.getDeductionItems().get(0).setnConsecutiveItem(nConsecutive + 1);
            manager.addDeductionRowItems(conn, penalty);
            conn.commit();
        } catch (SQLException e) {
            log.error("Bug, : " + e);
            throw new PenaltiesExceptions("Bug, addRowDeductionItem: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback addRowDeductionItem: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback addRowDeductionItem: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
            manager = null;
        }
    }

    @Override
    public void sendEmail(PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        Connection conn = null;
        boolean error = true;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            sendEmail(penalty, conn);
            conn.commit();
        } catch (Exception e) {
            log.error("Bug, : " + e);
            throw new PenaltiesExceptions("Bug, sendEmail: " + e.toString(), e);
        } finally {
            if (error) {
                if (conn != null)
                    try {
                        conn.rollback();
                    } catch (Exception e) {
                        log.error("Bug, Rollback sendEmail: " + e);
                        throw new PenaltiesExceptions("Bug, Rollback sendEmail: " + e.toString(), e);
                    }
            }
            CloseObject.closeObject(conn);
        }
    }

    private void sendEmail(PenaltyAndDeduction penalty, Connection conn) throws Exception {
        String cEmairCoordinator = "";
        String cEmairManager = "";
        String cEmairPurchasingDepartment = "";
        String cEmairAdquisition = "cromo@conafor.gob.mx;";
        boolean isDebuger = false;
        StringBuilder subject = null;
        StringBuilder bodymail = null;
        String tipo = "CAPTURADO";
        ProcessAgreementManager manager = null;
        PenaltiesManager managerPenalty = null;
        String cEjercicioAct = "2022";
        String cUECoordination;
        try {
            subject = new StringBuilder();
            manager = new ProcessAgreementManager();
            managerPenalty = new PenaltiesManager();
            cEjercicioAct = Util.obtieneEjercicioFiscalActivo(conn);
            if (penalty.getnIdOper() == 2) {
                // send email to adquisitions
                tipo = "CAPTURADO";
                subject.append("VALIDAR EL FOLIO " + penalty.getcFolio() + " DE PENAS Y DEDUCCIONES");
                bodymail = bodyEmail(penalty.getcNumContratoCNET(), penalty.getcProveedor(), penalty.getcOficio(), tipo);
            } else if (penalty.getnIdOper() == 1) {
                // send email to capture
                // area
                tipo = "RECHAZADO";
                bodymail = bodyEmailReject(penalty.getcNumContratoCNET(), penalty.getcProveedor(), penalty.getcOficio());
                managerPenalty.selectPenaltyAndDeduction(conn, penalty);
                penalty.setcEmailCaptureUsser(managerPenalty.getEmailUsser(conn, penalty.getcCaptureUsser()));
                subject.append("EL FOLIO " + penalty.getcFolio() + " DE PENAS Y DEDUCCIONES FUE RECHAZADO.");
            } else if (penalty.getnIdOper() == 3) {
                // send email to capture
                // area, requeried area
                // and responsible area
                tipo = "VALIDADO";
                managerPenalty.selectPenaltyAndDeduction(conn, penalty);
                penalty.setcEmailCaptureUsser(managerPenalty.getEmailUsser(conn, penalty.getcCaptureUsser()));
                subject.append("HA SIDO VALIDADO EL PROCESO DE CÁLCULO DE LAS PENAS CONVENCIONALES Y/O DEDUCCIONES");
                bodymail = bodyEmail(penalty.getcNumContratoCNET(), penalty.getcProveedor(), penalty.getcOficio(), tipo);
            }
            // Send email
            cEmairAdquisition = manager.getEmailSubGerenteAdquisiciones(conn, cEjercicioAct);
            if (isDebuger) {
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject.toString(), "", "", "", bodymail.toString(), true);
                managerPenalty.saveBinacleSendEmail(conn, penalty.getnIdPenaltyDeduction(), "", 1);
            } else {
                if (penalty.getnIdOper() == 2) {
                    // send email to adquisitions
                    cEmairPurchasingDepartment = manager.getEmailPurchasingDepartment(conn, cEjercicioAct);
                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject.toString(), cEmairPurchasingDepartment + ";", cEmairAdquisition, "", bodymail.toString(), false);
                    managerPenalty.saveBinacleSendEmail(conn, penalty.getnIdPenaltyDeduction(), cEmairPurchasingDepartment + ";" + cEmairAdquisition, 1);
                } else if (penalty.getnIdOper() == 1) {
                    // send email to
                    // capture area
                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject.toString(), penalty.getcEmailCaptureUsser() + ";", cEmairAdquisition, "", bodymail.toString(), false);
                    managerPenalty.saveBinacleSendEmail(conn, penalty.getnIdPenaltyDeduction(), penalty.getcEmailCaptureUsser() + ";" + cEmairAdquisition, 1);
                } else if (penalty.getnIdOper() == 3) {
                    // send email to
                    // capture area,
                    // requeried area
                    // and responsible
                    // area
                    cUECoordination = manager.getCoordination(conn, penalty.getcFolio().substring(5, 8));
                    cEmairCoordinator = manager.getEmailCoordinator(conn, cUECoordination, cEjercicioAct);
                    cEmairManager = manager.getEmailManager(conn, penalty.getcFolio().substring(5, 8), cEjercicioAct);
                    AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject.toString(), cEmairManager + ";", cEmairCoordinator.equalsIgnoreCase("") ? penalty.getcEmailCaptureUsser() + ";" : cEmairCoordinator + ";" + penalty.getcEmailCaptureUsser() + ";" + cEmairAdquisition, "", bodymail.toString(), false);
                    managerPenalty.saveBinacleSendEmail(conn, penalty.getnIdPenaltyDeduction(), cEmairManager + ";" + cEmairCoordinator + ";" + penalty.getcEmailCaptureUsser() + ";" + cEmairAdquisition, 1);
                }
            }
        } finally {
            cEmairCoordinator = null;
            cEmairManager = null;
            cEmairPurchasingDepartment = null;
            manager = null;
            if (bodymail != null && bodymail.length() > 0)
                bodymail.delete(0, bodymail.length());
            if (subject != null && subject.length() > 0)
                subject.delete(0, subject.length());
            subject = null;
            bodymail = null;
            managerPenalty = null;
        }
    }

    private StringBuilder bodyEmailReject(String cNumContratoCNET, String cProveedor, String cOficio) throws Exception {
        StringBuilder bodymail = new StringBuilder();
        bodymail.append("<html>");
        bodymail.append("<body>");
        bodymail.append("<p style=\"text-align: justify;\">");
        bodymail.append("<b>AREA REQUIRENTE Y ADMINISTRADOR DEL CONTRATO<br>");
        bodymail.append("EN MATERIA DE ADQUISICIONES, ARRENDAMIENTOS Y SERVICIOS </b>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("CON FUNDAMENTO EN LO ESTABLECIDO EN EL NUMERAL 11 \"ADMINISTRACIÓN Y VERIFICACIÓN DEL CUMPLIMIENTO DE LOS CONTRATOS\", APARTADO 11.3 \"PENAS CONVENCIONALES Y DEDUCCIONES AL PAGO\"  ");
        bodymail.append("DE LAS POLÍTICAS, BASES Y LINEAMIENTOS EN MATERIA DE ADQUISICIONES, ARRENDAMIENTOS Y SERVICIOS DE LA COMISIÓN NACIONAL FORESTAL, SE LE INFORMA QUE HA SIDO RECHAZADO EL CÁLCULO ");
        bodymail.append("DE LAS PENAS CONVENCIONALES Y/O DEDUCCIONES AL PAGO CORRESPONDIENTES AL CONTRATO ");
        bodymail.append(cNumContratoCNET);
        bodymail.append(" DEL PROVEEDOR ");
        bodymail.append(cProveedor);
        bodymail.append(" SOLICITADAS POR MEDIO DEL OFICIO ");
        bodymail.append(cOficio);
        bodymail.append(",  POR LAS CAUSAS INDICADAS EN EL REGISTRO RECHAZADO.");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("SIN OTRO EN PARTICULAR RECIBAN UN CORDIAL SALUDO.");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("Notificaciones SAI \"Sistema de Administración Integral\"");
        bodymail.append("<br>");
        bodymail.append("</body>");
        bodymail.append("</html>");
        return bodymail;
    }

    public static StringBuilder bodyEmail(String cNumContratoCNET, String cProveedor, String cOficio, String tipo) throws Exception {
        StringBuilder bodymail = new StringBuilder();
        bodymail.append("<html>");
        bodymail.append("<meta charset=\"UTF-8\">");
        bodymail.append("<body>");
        bodymail.append("<p style=\"text-align: justify;\">");
        bodymail.append("<b>AREA REQUIRENTE Y ADMINISTRADOR DEL CONTRATO<br>");
        bodymail.append("EN MATERIA DE ADQUISICIONES, ARRENDAMIENTOS Y SERVICIOS </b>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("CON FUNDAMENTO EN LO ESTABLECIDO EN EL NUMERAL 11 \"ADMINISTRACIÓN Y VERIFICACIÓN DEL CUMPLIMIENTO DE LOS CONTRATOS\", APARTADO 11.3 \"PENAS CONVENCIONALES Y DEDUCCIONES AL PAGO\"  ");
        bodymail.append("DE LAS POLÍTICAS, BASES Y LINEAMIENTOS EN MATERIA DE ADQUISICIONES, ARRENDAMIENTOS Y SERVICIOS DE LA COMISIÓN NACIONAL FORESTAL, SE LE INFORMA QUE HA SIDO " + tipo + " ÚNICAMNTE EL CÁLCULO ");
        bodymail.append("DE LAS PENAS CONVENCIONALES Y/O DEDUCCIONES AL PAGO CORRESPONDIENTES AL CONTRATO ");
        bodymail.append(cNumContratoCNET);
        bodymail.append(" DEL PROVEEDOR ");
        bodymail.append(cProveedor);
        bodymail.append(" SOLICITADAS POR MEDIO DEL OFICIO ");
        bodymail.append(cOficio);
        bodymail.append(". LO ANTERIOR A EFECTO DE QUE SE REALICE LA NOTIFICACIÓN CORRESPONDIENTE AL PROVEEDOR Y SE HAGAN EFECTIVAS LAS MISMAS AL MOMENTO DEL PAGO.");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("ES IMPORTANTE ACLARAR QUE LA INFORMACIÓN VERTIDA PARA LA REALIZACIÓN DEL CÁLCULO CORRESPONDIENTE AL MONTO DE LOS BIENES O SERVICIOS ENTREGADOS O PRESTADOS CON ATRASO O DE MANERA  ");
        bodymail.append("PARCIAL O DEFICIENTE, PORCENTAJE DE LA PENALIDAD O DEDUCCIÓN ESTABLECIDA EN EL CONTRATO, DIAS DE ATRASO O DE CUMPLIMIENTO PARCIAL O DEFICIENTE Y PORCENTAJE DE LA GARANTÍA DE ");
        bodymail.append("CUMPLIMIENTO Y DEMÁS DATOS DERIVADOS DEL CONTRATO CONSIDERADOS PARA EL CÁLCULO, ES RESPONSABILIDAD DEL ÁREA REQUIRENTE EN CONJUNTO CON EL ADMINISTRADOR DEL CONTRATO  ");
        bodymail.append("(ANTERIORMENTE ÁREA RESPONSABLE DE ADMINISTRAR Y VERIFICAR EL CUMPLIMIENTO DEL CONTRATO).");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("SIN OTRO EN PARTICULAR RECIBAN UN CORDIAL SALUDO.");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("<br>");
        bodymail.append("Notificaciones SAI \"Sistema de Administración Integral\"");
        bodymail.append("<br>");
        bodymail.append("</body>");
        bodymail.append("</html>");
        return bodymail;
    }
}
