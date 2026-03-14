package com.syc.sai.firmaElectronica.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic;
import com.axtel.contratos.core.ProcesoEnteraSatisfaccionManager;
import com.axtel.contratos.entities.DatEnteraSatisfaccion;
import com.syc.adquisiciones.core.DatosRecepcionFIEL;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.reportes.FirmaElectronicaReporte;
import com.syc.obrapublica.core.DatosEstimacionObra;
import com.syc.obrapublica.core.EstimacionObraFIEL;
import com.syc.obrapublica.core.manager.FirmaAutorizacionObraManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contratos.RecepcionMaterialManager;
import com.syc.sai.firmaElectronica.exceptions.AutRecepcionMaterialException;
import com.syc.sai.firmaElectronica.exceptions.EstimacionObraException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import java.util.Base64;

public class ReporteFIELBussinessLogic extends DataSourceManager {

    public ReporteFIELBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public String getPathReporte(int folioReporte, String tipoReporte) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (FirmaElectronicaReporte.REPORTE.equals(tipoReporte))
                return ReporteFIELManager.getPathReporte(conn, folioReporte);
            else if (FirmaElectronicaReporte.ACUSE.equals(tipoReporte))
                return ReporteFIELManager.getPathAcuse(conn, folioReporte);
            else
                throw new Exception("No se conoce el tipo de archivo: " + tipoReporte);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getPathConciliacion(int folioReporte, String tipoReporte) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (FirmaElectronicaReporte.REPORTE.equals(tipoReporte))
                return ReporteFIELManager.getPathConciliacion(conn, folioReporte);
            else if (FirmaElectronicaReporte.ACUSE.equals(tipoReporte))
                return ReporteFIELManager.getPathAcuseConciliacion(conn, folioReporte);
            else
                throw new Exception("No se conoce el tipo de archivo: " + tipoReporte);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getPathNotaRM(int folioNota) throws SQLException, AutRecepcionMaterialException, ParseException {
        Connection conn = null;
        String path = null;
        try {
            conn = getConnection();
            DatosRecepcionFIEL drf = RecepcionMaterialManager.read(conn, folioNota);
            RecepcionMaterialFIEL rmfiel = new RecepcionMaterialFIEL();
            rmfiel.setDocument("CONTRATODIVERSO");
            rmfiel.setRecepcionMaterial(drf);
            if (drf.getnIdEntraAlmacen() != SolicitudFirmaElectronica.ID_ALMACEN || !"-1".equalsIgnoreCase(drf.getFolioNota())) {
                int idGabinete = rmfiel.getIDGabineteRM(conn);
                Documento d = ReporteFIELManager.getPathNotaRM(conn, rmfiel, idGabinete);
                if (d == null)
                    throw new AutRecepcionMaterialException("No se encuentra documento para la recepci\u00f3n: " + folioNota);
                return path = d.getFullPathFilesNames()[0];
            } else {
                path = "";
            }
            return path;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getPathAnexo1A(int folioNota) throws SQLException, AutRecepcionMaterialException, ParseException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatEnteraSatisfaccion dat = ProcesoEnteraSatisfaccionManager.read(conn, folioNota);
            ProcesoEnteraSatisfaccionBusinessLogic anexo1AFiel = new ProcesoEnteraSatisfaccionBusinessLogic();
            anexo1AFiel.setDocument("ENTERASATISFACCION");
            anexo1AFiel.setDocName("Anexo1A");
            anexo1AFiel.setDatEnteraSatisfaccion(dat);
            int idGabinete = anexo1AFiel.getIDGabinete(conn);
            Documento d = ReporteFIELManager.getPathCoctosENSA(conn, anexo1AFiel, idGabinete);
            if (d == null)
                throw new AutRecepcionMaterialException("No se encuentra documento para el proceso de entera satisfacci\u00f3n: " + folioNota);
            return d.getFullPathFilesNames()[0];
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getPathActaHechos(int folioNota) throws SQLException, AutRecepcionMaterialException, ParseException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatEnteraSatisfaccion dat = ProcesoEnteraSatisfaccionManager.read(conn, folioNota);
            ProcesoEnteraSatisfaccionBusinessLogic anexo1AFiel = new ProcesoEnteraSatisfaccionBusinessLogic();
            anexo1AFiel.setDocument("ENTERASATISFACCION");
            anexo1AFiel.setDocName("Acta_Hechos");
            anexo1AFiel.setDatEnteraSatisfaccion(dat);
            if (dat.getnServPrestEnteraSatisfaccion() != SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA) {
                return "";
            }
            int idGabinete = anexo1AFiel.getIDGabinete(conn);
            Documento d = ReporteFIELManager.getPathCoctosENSA(conn, anexo1AFiel, idGabinete);
            if (d == null)
                throw new AutRecepcionMaterialException("No se encuentra documento para el proceso de entera satisfacci\u00f3n: " + folioNota);
            return d.getFullPathFilesNames()[0];
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String getPathNotaEstimacion(int folioNota) throws SQLException, EstimacionObraException, ParseException {
        Connection conn = null;
        try {
            conn = getConnection();
            DatosEstimacionObra datosEstimacion = FirmaAutorizacionObraManager.read(conn, folioNota);
            EstimacionObraFIEL estimacionFiel = new EstimacionObraFIEL();
            estimacionFiel.setDocument("OBRAPUBLICA");
            estimacionFiel.setEstimacionObra(datosEstimacion);
            estimacionFiel.setIdField(Integer.parseInt(datosEstimacion.getnFolioObra()));
            int idGabinete = estimacionFiel.getIDGabinete(conn);
            Documento d = ReporteFIELManager.getPathNotaEstimacion(conn, estimacionFiel, idGabinete);
            if (d == null)
                throw new EstimacionObraException("No se encuentra documento para la estimaci\u00f3n: " + folioNota);
            return d.getFullPathFilesNames()[0];
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
