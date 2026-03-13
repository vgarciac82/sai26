package com.axtel.contratos.penalties.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.axtel.contratos.penalties.core.DeductionItems;
import com.axtel.contratos.penalties.core.PenaltyAndDeduction;
import com.axtel.contratos.penalties.core.PenaltyItems;
import com.axtel.contratos.penalties.exception.PenaltiesExceptions;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PenaltiesManager {

    private static Logger log = LoggerFactory.getLogger(PenaltiesManager.class);

    public boolean addPenaltyAndDeduction(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        ResultSet rs = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mPenaltyDeduction ");
            query.append("(cFolio,cIdContratoDefinitivo,nIdEstate,fCaptureDate,cCaptureUser,lPenalty,lDeduction,cDocumentHAplicado,cConcept,nIdPeriod,cNumberJob) ");
            query.append(" values(?,?,?,GETDATE(),?,?,?,?,?,?,?)");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, penalty.getcFolio());
            ps.setString(2, penalty.getcIdContratoDefinitivo());
            ps.setInt(3, penalty.getnIdEstate());
            ps.setString(4, penalty.getcCaptureUsser());
            ps.setInt(5, penalty.getlPenalty());
            ps.setInt(6, penalty.getlDeduction());
            ps.setString(7, penalty.getcDocumentHAplicado());
            ps.setString(8, penalty.getcConcepto());
            ps.setInt(9, penalty.getnPeriodo());
            ps.setString(10, penalty.getcOficio());
            success = ps.executeUpdate() > 0;
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                penalty.setnIdPenaltyDeduction(rs.getInt(1));
            }
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug insert into mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
            query = null;
        }
    }

    public boolean updatePenaltyAndDeduction(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" update mPenaltyDeduction set nIdEstate=?,lPenalty=?,lDeduction=?,cIdContratoDefinitivo=?,cDocumentHAplicado=?,cConcept=?,cNumberJob=?,nIdPeriod=? where cFolio=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, penalty.getnIdEstate());
            ps.setInt(2, penalty.getlPenalty());
            ps.setInt(3, penalty.getlDeduction());
            ps.setString(4, penalty.getcIdContratoDefinitivo());
            ps.setString(5, penalty.getcDocumentHAplicado());
            ps.setString(6, penalty.getcConcepto());
            ps.setString(7, penalty.getcOficio());
            ps.setInt(8, penalty.getnPeriodo());
            ps.setString(9, penalty.getcFolio());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug update mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public void selectPenaltyAndDeduction(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("select nIdPenaltyDeduction,nIdEstate,convert(varchar,fCaptureDate,103)fCaptureDate,cCaptureUser,lPenalty,lDeduction,cDocumentHAplicado  from mPenaltyDeduction with(Nolock) where cFolio=? and cIdContratoDefinitivo=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, penalty.getcFolio());
            ps.setString(2, penalty.getcIdContratoDefinitivo());
            rs = ps.executeQuery();
            if (rs.next()) {
                penalty.setnIdPenaltyDeduction(rs.getInt("nIdPenaltyDeduction"));
                penalty.setfCaptureDate(rs.getString("fCaptureDate"));
                penalty.setcCaptureUsser(rs.getString("cCaptureUser"));
                penalty.setlPenalty(rs.getInt("lPenalty"));
                penalty.setlDeduction(rs.getInt("lDeduction"));
                penalty.setcDocumentHAplicado(rs.getString("cDocumentHAplicado"));
                penalty.setlExist(1);
            }
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug select mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public void existPenaltyAndDeduction(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("select * from mPenaltyDeduction with(Nolock) where cFolio=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, penalty.getcFolio());
            rs = ps.executeQuery();
            if (rs.next()) {
                penalty.setnIdPenaltyDeduction(rs.getInt("nIdPenaltyDeduction"));
                penalty.setlExist(1);
            }
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug select mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean addPenaltyItems(Connection conn, PenaltyItems item, String cTipoContrato) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            if ("CV".equalsIgnoreCase(cTipoContrato)) {
                query.append("insert into mPenaltyItems (nIdPenaltyDeduction,nIdItemContract,nPiecesElements,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyPenalty,nDailyDays,mTotalAmountPenalty,nComplianceGuaranteePercentage,nPenaltyDays,mAmountPenalty,nConsecutiveItem)  ");
                query.append("values(?,?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, item.getnIdPenaltyDeduction());
                ps.setInt(2, item.getnIdItemContract());
                ps.setInt(3, item.getnPiecesElements());
                ps.setDouble(4, item.getmDelayAmount());
                ps.setFloat(5, item.getnDailyPenaltyPercentage());
                ps.setDouble(6, item.getmAmountDailyPenalty());
                ps.setInt(7, item.getnDailyDays());
                ps.setDouble(8, item.getmTotalAmountPenalty());
                ps.setFloat(9, item.getnComplianceGuaranteePercentage());
                ps.setFloat(10, item.getnPenaltyDays());
                ps.setDouble(11, item.getmAmountPenalty());
                ps.setInt(12, item.getnConsecutiveItem());
            } else {
                query.append("insert into mPenaltyItems (nIdPenaltyDeduction,nIdItemContract,fItemDeliveryDate,nPiecesElements,mItemAmount,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyPenalty,nDailyDays,mTotalAmountPenalty,nComplianceGuaranteePercentage,nPenaltyDays,mAmountPenalty,nConsecutiveItem)  ");
                query.append("values(?,?,convert(date,?),?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, item.getnIdPenaltyDeduction());
                ps.setInt(2, item.getnIdItemContract());
                //
                ps.setString(3, item.getfItemDeliveryDate());
                ps.setInt(4, item.getnPiecesElements());
                //
                ps.setDouble(5, item.getmItemAmount());
                ps.setDouble(6, item.getmDelayAmount());
                ps.setFloat(7, item.getnDailyPenaltyPercentage());
                ps.setDouble(8, item.getmAmountDailyPenalty());
                ps.setInt(9, item.getnDailyDays());
                ps.setDouble(10, item.getmTotalAmountPenalty());
                ps.setFloat(11, item.getnComplianceGuaranteePercentage());
                ps.setFloat(12, item.getnPenaltyDays());
                ps.setDouble(13, item.getmAmountPenalty());
                ps.setInt(14, item.getnConsecutiveItem());
            }
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug insert into mPenaltyItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean deletePenaltyItems(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("Delete mPenaltyItems where nIdPenaltyDeduction=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, penalty.getnIdPenaltyDeduction());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug delete mPenaltyItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public void selectPenaltyItems(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        PenaltyItems item = null;
        ArrayList<PenaltyItems> penaltyItems = null;
        try {
            query = new StringBuilder();
            query.append("select nIdPenaltyDeduction,nIdItemContract,isnull(fItemDeliveryDate,'')fItemDeliveryDate,nPiecesElements,isnull(mItemAmount,0.0)mItemAmount,mDelayAmount");
            query.append(",nDailyPenaltyPercentage,mAmountDailyPenalty,nDailyDays,mTotalAmountPenalty,nComplianceGuaranteePercentage,nPenaltyDays,mAmountPenalty");
            query.append(" from mPenaltyItems with(Nolock) where nIdPenaltyDeduction=?");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, penalty.getnIdPenaltyDeduction());
            rs = ps.executeQuery();
            penaltyItems = new ArrayList<PenaltyItems>();
            while (rs.next()) {
                item = new PenaltyItems();
                item.setnIdPenaltyDeduction(rs.getInt("nIdPenaltyDeduction"));
                item.setnIdItemContract(rs.getInt("nIdItemContract"));
                item.setfItemDeliveryDate(rs.getString("fItemDeliveryDate"));
                item.setnPiecesElements(rs.getInt("nPiecesElements"));
                item.setmItemAmount(rs.getFloat("mItemAmount"));
                item.setmDelayAmount(rs.getDouble("mDelayAmount"));
                item.setnDailyPenaltyPercentage(rs.getFloat("nDailyPenaltyPercentage"));
                item.setmAmountDailyPenalty(rs.getDouble("mAmountDailyPenalty"));
                item.setnDailyDays(rs.getInt("nDailyDays"));
                item.setmTotalAmountPenalty(rs.getDouble("mTotalAmountPenalty"));
                item.setnComplianceGuaranteePercentage(rs.getFloat("nComplianceGuaranteePercentage"));
                item.setnPenaltyDays(rs.getInt("nPenaltyDays"));
                item.setmAmountPenalty(rs.getDouble("mAmountPenalty"));
                penaltyItems.add(item);
                item = null;
            }
            penalty.setPenaltyItems(penaltyItems);
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug select mPenaltyItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean addDeductionItems(Connection conn, DeductionItems item, String cTipoContrato) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            if ("CV".equalsIgnoreCase(cTipoContrato)) {
                query.append("insert into mDeductionItems (nIdPenaltyDeduction,nIdItemContract,nPiecesElements,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyDeduction,nDeficiencyDays,mTotalAmountDeduction,nComplianceGuaranteePercentage,nPenaltyDays,mAmountDeduction,nConsecutiveItem)  ");
                query.append("values(?,?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, item.getnIdPenaltyDeduction());
                ps.setInt(2, item.getnIdItemContract());
                ps.setInt(3, item.getnPiecesElements());
                ps.setDouble(4, item.getmDelayAmount());
                ps.setFloat(5, item.getnDailyPenaltyPercentage());
                ps.setDouble(6, item.getmAmountDailyDeduction());
                ps.setInt(7, item.getnDailyDays());
                ps.setDouble(8, item.getmTotalAmountDeduction());
                ps.setFloat(9, item.getnComplianceGuaranteePercentage());
                ps.setFloat(10, item.getnPenaltyDays());
                ps.setDouble(11, item.getmAmountDeduction());
                ps.setInt(12, item.getnConsecutiveItem());
            } else {
                query.append("insert into mDeductionItems (nIdPenaltyDeduction,nIdItemContract,fItemDeliveryDate,nPiecesElements,mItemAmount,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyDeduction,nDeficiencyDays,mTotalAmountDeduction,nComplianceGuaranteePercentage,nPenaltyDays,mAmountDeduction,nConsecutiveItem)  ");
                query.append("values(?,?,convert(date,?),?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, item.getnIdPenaltyDeduction());
                ps.setInt(2, item.getnIdItemContract());
                //
                ps.setString(3, item.getfItemDeliveryDate());
                ps.setInt(4, item.getnPiecesElements());
                //
                ps.setDouble(5, item.getmItemAmount());
                ps.setDouble(6, item.getmDelayAmount());
                ps.setFloat(7, item.getnDailyPenaltyPercentage());
                ps.setDouble(8, item.getmAmountDailyDeduction());
                ps.setInt(9, item.getnDailyDays());
                ps.setDouble(10, item.getmTotalAmountDeduction());
                ps.setFloat(11, item.getnComplianceGuaranteePercentage());
                ps.setFloat(12, item.getnPenaltyDays());
                ps.setDouble(13, item.getmAmountDeduction());
                ps.setInt(14, item.getnConsecutiveItem());
            }
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug insert into mDeductionItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean deleteDeductionItems(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("Delete mDeductionItems where nIdPenaltyDeduction=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, penalty.getnIdPenaltyDeduction());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug Delete mDeductionItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public Respuesta areThereDocuments(Connection conn, String cFolio) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        Respuesta response = null;
        StringBuilder query = null;
        StringBuilder docs = null;
        String token = "";
        try {
            query = new StringBuilder();
            docs = new StringBuilder();
            query.append("SELECT NOMBRE_DOCUMENTO FROM IMX_DOCUMENTO WITH(NOLOCK) WHERE TITULO_APLICACION='PENACONVENCIONAL' ");
            query.append("AND ID_GABINETE=(SELECT ID_GABINETE FROM IMX_DOCUMENTO WITH(NOLOCK)WHERE TITULO_APLICACION='PENACONVENCIONAL' AND NOMBRE_DOCUMENTO =?) AND ID_CARPETA_PADRE=2 AND NUMERO_PAGINAS<=0 ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cFolio);
            rs = ps.executeQuery();
            response = new Respuesta();
            response.setResp(true);
            while (rs.next()) {
                docs.append(token);
                docs.append(rs.getString("NOMBRE_DOCUMENTO"));
                success = true;
                token = "\n";
            }
            if (success) {
                response.setMsg("Favor de adjuntar los siguientes documentos en la pestaña de \"Adjuntos\":\n" + docs.toString());
                response.setResp(false);
            }
            return response;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug consulting documents: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
            docs = null;
        }
    }

    public boolean updateObservations(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mPenaltyDeduction set cObservations=? where cFolio=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, penalty.getcObservations());
            ps.setString(2, penalty.getcFolio());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug update observations of mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean updateAplicateDocumentAndState(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mPenaltyDeduction set cDocumentHAplicado=?,nIdEstate=? where cFolio=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, penalty.getcDocumentHAplicado());
            ps.setInt(2, penalty.getnIdEstate());
            ps.setString(3, penalty.getcFolio());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug update cDocumentHAplicado of mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean saveValidatePenaltyAndDeductions(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mPenaltyDeduction set cDocumentHAplicado=?,nIdEstate=?,cObservations=?,cValidatingUser=?,fValidateDate=getDate() where cFolio=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, penalty.getcDocumentHAplicado());
            ps.setInt(2, penalty.getnIdEstate());
            ps.setString(3, penalty.getcObservations());
            ps.setString(4, penalty.getcValidatingUser());
            ps.setString(5, penalty.getcFolio());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug saveValidatePenaltyAndDeductions of mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean resetPenaltyAndDeductions(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append("update mPenaltyDeduction set cIdContratoDefinitivo=?,lPenalty=0,lDeduction=0 where cFolio=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, penalty.getcIdContratoDefinitivo());
            ps.setString(2, penalty.getcFolio());
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug saveValidatePenaltyAndDeductions of mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public int getConsecutiveItemDeduction(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        int nConsecutive = 0;
        try {
            query = new StringBuilder();
            query.append("select isnull(MAX(nConsecutiveItem),0)nConsecutiveItem from mDeductionItems with(nolock) where nIdPenaltyDeduction=? and nIdItemContract=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, penalty.getnIdPenaltyDeduction());
            ps.setInt(2, penalty.getDeductionItems().get(0).getnIdItemContract());
            rs = ps.executeQuery();
            if (rs.next()) {
                nConsecutive = rs.getInt("nConsecutiveItem");
            }
            return nConsecutive;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug select mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public int getConsecutiveItemPenalty(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        int nConsecutive = 0;
        try {
            query = new StringBuilder();
            query.append("select isnull(MAX(nConsecutiveItem),0)nConsecutiveItem from mPenaltyItems with(nolock) where nIdPenaltyDeduction=? and nIdItemContract=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, penalty.getnIdPenaltyDeduction());
            ps.setInt(2, penalty.getPenaltyItems().get(0).getnIdItemContract());
            rs = ps.executeQuery();
            if (rs.next()) {
                nConsecutive = rs.getInt("nConsecutiveItem");
            }
            return nConsecutive;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug select mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean addDeductionRowItems(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            if ("CV".equalsIgnoreCase(penalty.getcTipoContrato())) {
                query.append("insert into mDeductionItems (nIdPenaltyDeduction,nIdItemContract,nPiecesElements,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyDeduction,nDeficiencyDays,mTotalAmountDeduction,nComplianceGuaranteePercentage,nPenaltyDays,mAmountDeduction,nConsecutiveItem)  ");
                query.append("values(?,?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, penalty.getnIdPenaltyDeduction());
                ps.setInt(2, penalty.getDeductionItems().get(0).getnIdItemContract());
                ps.setInt(3, 0);
                ps.setDouble(4, 0);
                ps.setFloat(5, 0);
                ps.setDouble(6, 0);
                ps.setInt(7, 0);
                ps.setDouble(8, 0.0);
                ps.setFloat(9, 0);
                ps.setFloat(10, 0);
                ps.setDouble(11, 0);
                ps.setInt(12, penalty.getDeductionItems().get(0).getnConsecutiveItem());
            } else {
                query.append("insert into mDeductionItems (nIdPenaltyDeduction,nIdItemContract,fItemDeliveryDate,nPiecesElements,mItemAmount,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyDeduction,nDeficiencyDays,mTotalAmountDeduction,nComplianceGuaranteePercentage,nPenaltyDays,mAmountDeduction,nConsecutiveItem)  ");
                query.append("values(?,?,convert(date,getDate()),?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, penalty.getnIdPenaltyDeduction());
                ps.setInt(2, penalty.getDeductionItems().get(0).getnIdItemContract());
                ps.setInt(3, 0);
                //
                ps.setDouble(4, 0.0);
                ps.setDouble(5, 0.0);
                ps.setFloat(6, 0);
                ps.setDouble(7, 0.0);
                ps.setInt(8, 0);
                ps.setDouble(9, 0.0);
                ps.setFloat(10, 0);
                ps.setFloat(11, 0);
                ps.setDouble(12, 0.0);
                ps.setInt(13, penalty.getDeductionItems().get(0).getnConsecutiveItem());
            }
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug insert into mDeductionItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean addPenaltyRowItems(Connection conn, PenaltyAndDeduction penalty) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            if ("CV".equalsIgnoreCase(penalty.getcTipoContrato())) {
                query.append("insert into mPenaltyItems (nIdPenaltyDeduction,nIdItemContract,nPiecesElements,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyPenalty,nDailyDays,mTotalAmountPenalty,nComplianceGuaranteePercentage,nPenaltyDays,mAmountPenalty,nConsecutiveItem)  ");
                query.append("values(?,?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, penalty.getnIdPenaltyDeduction());
                ps.setInt(2, penalty.getPenaltyItems().get(0).getnIdItemContract());
                ps.setInt(3, 0);
                ps.setDouble(4, 0.0);
                ps.setFloat(5, 0);
                ps.setDouble(6, 0);
                ps.setInt(7, 0);
                ps.setDouble(8, 0);
                ps.setFloat(9, 0);
                ps.setFloat(10, 0);
                ps.setDouble(11, 0);
                ps.setInt(12, penalty.getPenaltyItems().get(0).getnConsecutiveItem());
            } else {
                query.append("insert into mPenaltyItems (nIdPenaltyDeduction,nIdItemContract,fItemDeliveryDate,nPiecesElements,mItemAmount,mDelayAmount,nDailyPenaltyPercentage");
                query.append(",mAmountDailyPenalty,nDailyDays,mTotalAmountPenalty,nComplianceGuaranteePercentage,nPenaltyDays,mAmountPenalty,nConsecutiveItem)  ");
                query.append("values(?,?,convert(date,getDate()),?,?,?,?,?,?,?,?,?,?,?)");
                log.info(query);
                ps = conn.prepareStatement(query.toString());
                ps.setInt(1, penalty.getnIdPenaltyDeduction());
                ps.setInt(2, penalty.getPenaltyItems().get(0).getnIdItemContract());
                ps.setInt(3, 0);
                //
                ps.setDouble(4, 0);
                ps.setDouble(5, 0);
                ps.setFloat(6, 0);
                ps.setDouble(7, 0);
                ps.setInt(8, 0);
                ps.setDouble(9, 0);
                ps.setFloat(10, 0);
                ps.setFloat(11, 0);
                ps.setDouble(12, 0);
                ps.setInt(13, penalty.getPenaltyItems().get(0).getnConsecutiveItem());
            }
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug insert into mPenaltyItems: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public String getEmailUsser(Connection conn, String cLogin) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = null;
        String cEmail = "";
        try {
            query = new StringBuilder();
            query.append("select *from CG_USUARIO with(Nolock) where U_LOGIN=? ");
            log.info(query);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cLogin);
            rs = ps.executeQuery();
            if (rs.next()) {
                cEmail = rs.getString("U_EMAIL");
            }
            return cEmail;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug select mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
            query = null;
        }
    }

    public boolean saveBinacleSendEmail(Connection conn, int nIdPenaltyDeduction, String cEmails, int isEmailSend) throws PenaltiesExceptions {
        PreparedStatement ps = null;
        boolean success = false;
        StringBuilder query = null;
        try {
            query = new StringBuilder();
            query.append(" insert into mBitacoraSendEmailPenalty ");
            query.append("(nIdPenaltyDeduction,cEmails,isEmailSend,fDateSend) ");
            query.append(" values(?,?,?,GETDATE())");
            log.info(query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nIdPenaltyDeduction);
            ps.setString(2, cEmails);
            ps.setInt(3, isEmailSend);
            success = ps.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            throw new PenaltiesExceptions("Bug insert into mPenaltyDeduction: " + e.toString(), e);
        } finally {
            CloseObject.closeObject(ps);
            query = null;
        }
    }
}
