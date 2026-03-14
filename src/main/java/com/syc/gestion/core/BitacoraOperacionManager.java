package com.syc.gestion.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import com.syc.gestion.documental.Documental;
import java.util.Base64;

public class BitacoraOperacionManager {

    public static BitacoraOperacion select(Connection conn, BitacoraOperacion bo) throws SQLException {
        BitacoraOperacion retVal = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (bo.getIdBitacora() > 0) {
                where.append(token + "id_bitacora = ?");
                token = " AND ";
            }
            if (bo.getIdCaso() > 0) {
                where.append(token + "id_caso = ?");
                token = " AND ";
            }
            if (bo.getIdOperacion() > 0) {
                where.append(token + "id_operacion = ?");
                token = " AND ";
            }
            // Corresponde a CG_OPERACION.ID_CASO_OPER
            if (bo.getSecuencialOperacion() > 0) {
                where.append(token + "secuencial_operacion = ?");
                token = " AND ";
            }
            if (bo.getStatus() > 0) {
                where.append(token + "status = ?");
                token = " AND ";
            }
            if (bo.getFechaInicio() != null) {
                where.append(token + "fecha_inicio = ?");
                token = " AND ";
            }
            if (bo.getFechaCompromiso() != null) {
                where.append(token + "fecha_compromiso = ?");
                token = " AND ";
            }
            if (bo.getFechaTermino() != null) {
                where.append(token + "fecha_termino = ?");
                token = " AND ";
            }
            if (bo.getResponsableId() != null) {
                where.append(token + "responsable_id = ?");
                token = " AND ";
            }
            if (bo.getResponsableArea() != null) {
                where.append(token + "responsable_area = ?");
                token = " AND ";
            }
            if (bo.getRemitenteId() != null) {
                where.append(token + "remitente_id = ?");
                token = " AND ";
            }
            if (bo.getRemitenteArea() != null) {
                where.append(token + "remitente_area = ?");
                token = " AND ";
            }
            if (bo.getTurnadoId() != null) {
                where.append(token + "turnado_id = ?");
                token = " AND ";
            }
            if (bo.getTurnadoArea() != null) {
                where.append(token + "turnado_area = ?");
                token = " AND ";
            }
            if (bo.getInstruccion() != null) {
                where.append(token + "instruccion = ?");
                token = " AND ";
            }
            if (bo.getOprStack() >= Documental.OPR_STACK_POP && bo.getOprStack() <= Documental.OPR_STACK_PUSH) {
                where.append(token + "opr_stack = ?");
                token = " AND ";
            }
            String query = "SELECT * FROM cg_bitacora_operacion " + where.toString();
            //System.out.println("query=["+query+"]");
            pstmnt = conn.prepareStatement(query);
            int i = 1;
            if (bo.getIdBitacora() > 0)
                pstmnt.setInt(i++, bo.getIdBitacora());
            if (bo.getIdCaso() > 0)
                pstmnt.setInt(i++, bo.getIdCaso());
            if (bo.getIdOperacion() > 0)
                pstmnt.setInt(i++, bo.getIdOperacion());
            if (bo.getSecuencialOperacion() > 0)
                pstmnt.setInt(i++, bo.getSecuencialOperacion());
            if (bo.getStatus() > 0)
                pstmnt.setInt(i++, bo.getStatus());
            if (bo.getFechaInicio() != null)
                pstmnt.setTimestamp(i++, bo.getFechaInicio());
            if (bo.getFechaCompromiso() != null)
                pstmnt.setTimestamp(i++, bo.getFechaCompromiso());
            if (bo.getFechaTermino() != null)
                pstmnt.setTimestamp(i++, bo.getFechaTermino());
            if (bo.getResponsableId() != null)
                pstmnt.setString(i++, bo.getResponsableId());
            if (bo.getResponsableArea() != null)
                pstmnt.setString(i++, bo.getResponsableArea());
            if (bo.getRemitenteId() != null)
                pstmnt.setString(i++, bo.getRemitenteId());
            if (bo.getRemitenteArea() != null)
                pstmnt.setString(i++, bo.getRemitenteArea());
            if (bo.getTurnadoId() != null)
                pstmnt.setString(i++, bo.getTurnadoId());
            if (bo.getTurnadoArea() != null)
                pstmnt.setString(i++, bo.getTurnadoArea());
            if (bo.getInstruccion() != null)
                pstmnt.setString(i++, bo.getInstruccion());
            if (bo.getOprStack() >= Documental.OPR_STACK_POP && bo.getOprStack() <= Documental.OPR_STACK_PUSH) {
                pstmnt.setInt(i++, bo.getOprStack());
            }
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retVal = new BitacoraOperacion();
                retVal.setIdBitacora(rs.getInt("id_bitacora"));
                retVal.setIdCaso(rs.getInt("id_caso"));
                retVal.setIdTipoCaso(rs.getInt("id_tipo_caso"));
                retVal.setIdOperacion(rs.getInt("id_operacion"));
                retVal.setSecuencialAnterior(rs.getInt("secuencial_anterior"));
                retVal.setSecuencialOperacion(rs.getInt("secuencial_operacion"));
                retVal.setSecuencialSiguiente(rs.getInt("secuencial_siguiente"));
                retVal.setStatus(rs.getInt("status"));
                retVal.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                //System.out.println("BitacoraOperacionManager.select-->fecha_compromiso");
                //System.out.println("BitacoraOperacionManager.select-->fecha_compromiso=["+rs.getTimestamp("fecha_compromiso")+"]");
                retVal.setFechaCompromiso(rs.getTimestamp("fecha_compromiso"));
                retVal.setFechaTermino(rs.getTimestamp("fecha_termino"));
                retVal.setResponsableId(rs.getString("responsable_id"));
                retVal.setResponsableArea(rs.getString("responsable_area"));
                retVal.setRemitenteId(rs.getString("remitente_id"));
                retVal.setRemitenteArea(rs.getString("remitente_area"));
                retVal.setTurnadoId(rs.getString("turnado_id"));
                retVal.setTurnadoArea(rs.getString("turnado_area"));
                retVal.setInstruccion(rs.getString("instruccion"));
                retVal.setOprStack(rs.getInt("opr_stack"));
                boolean isTerminada = false;
                try {
                    isTerminada = rs.getString("terminada").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                retVal.setTerminada(isTerminada);
                boolean isLeida = false;
                try {
                    isLeida = rs.getString("leida").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                retVal.setLeida(isLeida);
                retVal.setIdGabinete(rs.getInt("id_gabinete"));
                retVal.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                retVal.setOperacion(getOperacion(conn, retVal.getIdTipoCaso(), retVal.getIdOperacion()));
                retVal.setIdGabinete(rs.getInt("id_gabinete"));
                retVal.setFolio(rs.getString("folio"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }

    public static BitacoraOperacion[] selectAll(Connection conn, BitacoraOperacion bo) throws SQLException {
        BitacoraOperacion[] retVal = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (bo.getIdBitacora() > 0) {
                where.append(token + "id_bitacora = ?");
                token = " AND ";
            }
            if (bo.getIdCaso() > 0) {
                where.append(token + "id_caso = ?");
                token = " AND ";
            }
            if (bo.getIdOperacion() > 0) {
                where.append(token + "id_operacion = ?");
                token = " AND ";
            }
            if (bo.getSecuencialOperacion() > 0) {
                where.append(token + "secuencial_operacion = ?");
                token = " AND ";
            }
            if (bo.getStatus() > 0) {
                where.append(token + "status = ?");
                token = " AND ";
            }
            if (bo.getFechaInicio() != null) {
                where.append(token + "fecha_inicio = ?");
                token = " AND ";
            }
            if (bo.getFechaCompromiso() != null) {
                where.append(token + "fecha_compromiso = ?");
                token = " AND ";
            }
            if (bo.getFechaTermino() != null) {
                where.append(token + "fecha_termino = ?");
                token = " AND ";
            }
            if (bo.getResponsableId() != null) {
                where.append(token + "responsable_id = ?");
                token = " AND ";
            }
            if (bo.getResponsableArea() != null) {
                where.append(token + "responsable_area = ?");
                token = " AND ";
            }
            if (bo.getRemitenteId() != null) {
                where.append(token + "remitente_id = ?");
                token = " AND ";
            }
            if (bo.getRemitenteArea() != null) {
                where.append(token + "remitente_area = ?");
                token = " AND ";
            }
            if (bo.getTurnadoId() != null) {
                where.append(token + "turnado_id = ?");
                token = " AND ";
            }
            if (bo.getTurnadoArea() != null) {
                where.append(token + "turnado_area = ?");
                token = " AND ";
            }
            if (bo.getInstruccion() != null) {
                where.append(token + "instruccion = ?");
                token = " AND ";
            }
            if (bo.getOprStack() >= Documental.OPR_STACK_POP && bo.getOprStack() <= Documental.OPR_STACK_PUSH) {
                where.append(token + "opr_stack = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_bitacora_operacion " + where.toString() + " ORDER BY id_caso, secuencial_operacion");
            int i = 1;
            if (bo.getIdBitacora() > 0)
                pstmnt.setInt(i++, bo.getIdBitacora());
            if (bo.getIdCaso() > 0)
                pstmnt.setInt(i++, bo.getIdCaso());
            if (bo.getIdOperacion() > 0)
                pstmnt.setInt(i++, bo.getIdOperacion());
            if (bo.getSecuencialOperacion() > 0)
                pstmnt.setInt(i++, bo.getSecuencialOperacion());
            if (bo.getStatus() > 0)
                pstmnt.setInt(i++, bo.getStatus());
            if (bo.getFechaInicio() != null)
                pstmnt.setTimestamp(i++, bo.getFechaInicio());
            if (bo.getFechaCompromiso() != null)
                pstmnt.setTimestamp(i++, bo.getFechaCompromiso());
            if (bo.getFechaTermino() != null)
                pstmnt.setTimestamp(i++, bo.getFechaTermino());
            if (bo.getResponsableId() != null)
                pstmnt.setString(i++, bo.getResponsableId());
            if (bo.getResponsableArea() != null)
                pstmnt.setString(i++, bo.getResponsableArea());
            if (bo.getRemitenteId() != null)
                pstmnt.setString(i++, bo.getRemitenteId());
            if (bo.getRemitenteArea() != null)
                pstmnt.setString(i++, bo.getRemitenteArea());
            if (bo.getTurnadoId() != null)
                pstmnt.setString(i++, bo.getTurnadoId());
            if (bo.getTurnadoArea() != null)
                pstmnt.setString(i++, bo.getTurnadoArea());
            if (bo.getInstruccion() != null)
                pstmnt.setString(i++, bo.getInstruccion());
            if (bo.getOprStack() >= Documental.OPR_STACK_POP && bo.getOprStack() <= Documental.OPR_STACK_PUSH) {
                pstmnt.setInt(i++, bo.getOprStack());
            }
            rs = pstmnt.executeQuery();
            Vector v = new Vector();
            while (rs.next()) {
                BitacoraOperacion tmpBo = new BitacoraOperacion();
                tmpBo.setIdBitacora(rs.getInt("id_bitacora"));
                tmpBo.setIdCaso(rs.getInt("id_caso"));
                tmpBo.setIdTipoCaso(rs.getInt("id_tipo_caso"));
                tmpBo.setIdOperacion(rs.getInt("id_operacion"));
                tmpBo.setSecuencialAnterior(rs.getInt("secuencial_anterior"));
                tmpBo.setSecuencialOperacion(rs.getInt("secuencial_operacion"));
                tmpBo.setSecuencialSiguiente(rs.getInt("secuencial_siguiente"));
                tmpBo.setStatus(rs.getInt("status"));
                tmpBo.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                tmpBo.setFechaCompromiso(rs.getTimestamp("fecha_compromiso"));
                tmpBo.setFechaTermino(rs.getTimestamp("fecha_termino"));
                tmpBo.setResponsableId(rs.getString("responsable_id"));
                tmpBo.setResponsableArea(rs.getString("responsable_area"));
                tmpBo.setRemitenteId(rs.getString("remitente_id"));
                tmpBo.setRemitenteArea(rs.getString("remitente_area"));
                tmpBo.setTurnadoId(rs.getString("turnado_id"));
                tmpBo.setTurnadoArea(rs.getString("turnado_area"));
                tmpBo.setInstruccion(rs.getString("instruccion"));
                tmpBo.setOprStack(rs.getInt("opr_stack"));
                boolean isTerminada = false;
                try {
                    isTerminada = rs.getString("terminada").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                tmpBo.setTerminada(isTerminada);
                boolean isLeida = false;
                try {
                    isLeida = rs.getString("leida").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                tmpBo.setLeida(isLeida);
                tmpBo.setIdGabinete(rs.getInt("id_gabinete"));
                tmpBo.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                tmpBo.setIdGabinete(rs.getInt("id_gabinete"));
                tmpBo.setFolio(rs.getString("folio"));
                //obtiene la operacion
                tmpBo.setOperacion(getOperacion(conn, tmpBo.getIdTipoCaso(), tmpBo.getIdOperacion()));
                v.add(tmpBo);
            }
            if (v.size() > 0) {
                retVal = new BitacoraOperacion[v.size()];
                v.toArray(retVal);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }

    public static int insert(Connection conn, BitacoraOperacion bo) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_bitacora_operacion " + "(id_bitacora, id_caso, id_tipo_caso, id_operacion, " + " secuencial_anterior, secuencial_operacion, secuencial_siguiente," + " status, fecha_inicio, fecha_compromiso, fecha_termino, " + " remitente_id, remitente_area, " + " responsable_id, responsable_area, " + " turnado_id, turnado_area, " + " terminada, instruccion, leida, titulo_aplicacion," + " id_gabinete, folio, opr_stack) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?)");
            pstmnt.setInt(1, getNextIdBitacora(conn));
            pstmnt.setInt(2, bo.getIdCaso());
            pstmnt.setInt(3, bo.getIdTipoCaso());
            pstmnt.setInt(4, bo.getIdOperacion());
            pstmnt.setInt(5, bo.getSecuencialAnterior());
            pstmnt.setInt(6, bo.getSecuencialOperacion());
            pstmnt.setInt(7, bo.getSecuencialSiguiente());
            pstmnt.setInt(8, bo.getStatus());
            pstmnt.setTimestamp(9, bo.getFechaInicio());
            pstmnt.setTimestamp(10, bo.getFechaCompromiso());
            pstmnt.setTimestamp(11, bo.getFechaTermino());
            pstmnt.setString(12, bo.getRemitenteId());
            pstmnt.setString(13, bo.getRemitenteArea());
            pstmnt.setString(14, bo.getResponsableId());
            pstmnt.setString(15, bo.getResponsableArea());
            pstmnt.setString(16, bo.getTurnadoId());
            pstmnt.setString(17, bo.getTurnadoArea());
            String strTerminada = (bo.isTerminada()) ? "S" : "N";
            pstmnt.setString(18, strTerminada);
            pstmnt.setString(19, bo.getInstruccion());
            String strLeida = (bo.isLeida()) ? "S" : "N";
            pstmnt.setString(20, strLeida);
            pstmnt.setString(21, bo.getTituloAplicacion());
            pstmnt.setInt(22, bo.getIdGabinete());
            pstmnt.setString(23, bo.getFolio());
            pstmnt.setInt(24, bo.getOprStack());
            retval = pstmnt.executeUpdate();
        } catch (Exception e) {
            //System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int update(Connection conn, BitacoraOperacion bo) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            String query = "UPDATE cg_bitacora_operacion " + " SET status=?, fecha_termino=?, " + " secuencial_anterior=?, secuencial_siguiente=?, " + " turnado_id=?, turnado_area=?, " + " terminada=?, leida=?, instruccion=?, " + " remitente_id=?, remitente_area=?, " + " responsable_id=?, responsable_area=? " + " WHERE id_bitacora=?";
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, bo.getStatus());
            pstmnt.setTimestamp(2, bo.getFechaTermino());
            pstmnt.setInt(3, bo.getSecuencialAnterior());
            pstmnt.setInt(4, bo.getSecuencialSiguiente());
            pstmnt.setString(5, bo.getTurnadoId());
            pstmnt.setString(6, bo.getTurnadoArea());
            String strTerminada = (bo.isTerminada()) ? "S" : "N";
            pstmnt.setString(7, strTerminada);
            String strLeida = (bo.isLeida()) ? "S" : "N";
            pstmnt.setString(8, strLeida);
            pstmnt.setString(9, bo.getInstruccion());
            pstmnt.setString(10, bo.getRemitenteId());
            pstmnt.setString(11, bo.getRemitenteArea());
            pstmnt.setString(12, bo.getResponsableId());
            pstmnt.setString(13, bo.getResponsableArea());
            pstmnt.setInt(14, bo.getIdBitacora());
            retval = pstmnt.executeUpdate();
        } catch (Exception e) {
            //System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    /*--------------------------------------------------------------------------------------------------------------------------------
        Elaboró: MFR
          Fecha: 04Jun09
       Objetivo: Actualiza la tabla cg_bitacora_operacion cuando se lleva a cabo una Transferencia de Asuntos.
Relacionado Con: SeguridadBusinessLogic.copiaUsuario(), CasoDatoManager.updateTrans(), CasoOperacionManager.update()
--------------------------------------------------------------------------------------------------------------------------------*/
    public static int transfiereBitacoraOperacion(Connection conn, CasoOperacion co, String u_login) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion " + " SET remitente_id=? " + " WHERE id_caso =?" + " AND remitente_id=?");
            pstmnt.setString(1, co.getResponsable());
            pstmnt.setInt(2, co.getIdCaso());
            pstmnt.setString(3, u_login);
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion " + " SET responsable_id=? " + " WHERE id_caso=?" + " AND responsable_id=?");
            pstmnt.setString(1, co.getResponsable());
            pstmnt.setInt(2, co.getIdCaso());
            pstmnt.setString(3, u_login);
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int updateUser(Connection conn, String oldUser, String newUser) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        //Actualiza el remitente
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion " + " SET remitente_id=? " + " WHERE remitente_id=?");
            pstmnt.setString(1, newUser);
            pstmnt.setString(2, oldUser);
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        //Actualiza el responsable
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion " + " SET responsable_id=? " + " WHERE responsable_id=?");
            pstmnt.setString(1, newUser);
            pstmnt.setString(2, oldUser);
            retval = pstmnt.executeUpdate();
            //GAF 2010-04-16
            //En los manager no debe haber commits!
            //solamente en los businesslogic o servlets
            //conn.commit();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        //Actualiza el turnado
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion " + " SET turnado_id=? " + " WHERE turnado_id=?");
            pstmnt.setString(1, newUser);
            pstmnt.setString(2, oldUser);
            retval = pstmnt.executeUpdate();
        } catch (Exception e) {
            //System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    private static int getNextIdBitacora(Connection conn) throws SQLException {
        /*
		int retval = -1;
		Statement stmnt = null;
		ResultSet rs = null;

		try {
			stmnt = conn.createStatement();
			rs = stmnt.executeQuery("SELECT MAX(id_bitacora) FROM cg_bitacora_operacion");

			if (rs.next())
				retval = rs.getInt(1) + 1;
		} finally {
			if (rs != null)
				rs.close();

			if (stmnt != null)
				stmnt.close();

			rs = null;
			stmnt = null;
		}
		*/
        //GAF - sequence en lugar de MAX
        //SequenceManager sm = SequenceManager.getInstance();
        //SequenceManager sm = new SequenceManager();
        //return sm.nextVal("CG_BITACORA_OPERACION.ID_BITACORA", new String[]{});
        CFSequenceManager sm = CFSequenceManager.getInstance();
        return sm.nextVal("CG_BITACORA_OPERACION");
    }

    public static BitacoraOperacion nuevaBitacoraOperacion(Connection conn, String remId, String instruccion, String turnado, Caso c, CasoOperacion co, int oprStack) throws SQLException {
        if (c == null)
            throw new NullPointerException("El caso no debe ser nulo");
        if (co == null)
            throw new NullPointerException("La operacion no debe ser nula");
        BitacoraOperacion bo = new BitacoraOperacion();
        bo.setFechaInicio(co.getFechaInicio());
        //para la fecha compromiso
        //hay que sumar la fecha inicio +
        //horas de la operacion y si son -1
        //se guarda la fecha compromiso del caso
        //GAF 2010-12-03
        //Se corrige el error de fecha compromiso.
        //Se estaba utilizando el campo co_tiempo_limite
        //Y debe utilizar el USER03
        bo.setFechaCompromiso(c.getFechaCompromiso());
        if (co.getUser03() != null && co.getUser03().trim().length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Timestamp t1 = null;
            try {
                String strFechaCompromiso = co.getUser03();
                t1 = new Timestamp(sdf.parse(strFechaCompromiso).getTime());
                bo.setFechaCompromiso(t1);
            } catch (ParseException pe) {
                pe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //la fecha termino se actualiza con un update
        //bo.setFechaTermino(fechaTermino);
        //el IdBitacora no se inicializa
        //dejamos que el insert lo calcule
        //bo.setIdBitacora(idBitacora);
        bo.setIdCaso(c.getIdCaso());
        bo.setIdTipoCaso(c.getIdTC());
        bo.setIdOperacion(co.getIdOperacion());
        bo.setSecuencialOperacion(co.getIdCasoOper());
        bo.setTituloAplicacion(c.getTipoCaso().getGavetaAsociada());
        bo.setIdGabinete(c.getIdGabinete());
        bo.setFolio(c.getFolio());
        bo.setOprStack(oprStack);
        //Se va a actualizar con un update
        //bo.setSecuencialSiguiente(secuencialSiguiente);
        bo.setInstruccion(instruccion);
        bo.setTurnadoId(turnado);
        Empleado eRemitente = new Empleado();
        eRemitente.setClaveUsuario(remId);
        eRemitente = EmpleadoManager.select(conn, eRemitente);
        if (eRemitente != null) {
            bo.setRemitenteId(eRemitente.getClaveUsuario());
            bo.setRemitenteArea(eRemitente.getClaveArea());
        }
        bo.setResponsableArea(co.getResponsable());
        Empleado eResponsable = new Empleado();
        eResponsable.setClaveUsuario(co.getResponsable());
        eResponsable = EmpleadoManager.select(conn, eResponsable);
        if (eResponsable != null) {
            bo.setResponsableId(eResponsable.getClaveUsuario());
            bo.setResponsableArea(eResponsable.getClaveArea());
        }
        bo.setStatus(co.getStatus());
        // Se le quita que el terminado tenga que ver con la operacion queda sin terminar
        // bo.setTerminada(co.getOperacion().isSinRegreso());
        boolean isTerminada = false;
        //boolean isTerminada = () ? true : false;
        bo.setTerminada(isTerminada);
        //El turnado se actualiza en el update
        //bo.setTurnadoArea(turnadoArea);
        //bo.setTurnadoId(turnadoId);
        return bo;
    }

    public static BitacoraOperacion findPreviousOperation(Connection conn, BitacoraOperacion bo) throws SQLException {
        BitacoraOperacion retVal = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (bo.getIdCaso() > 0) {
                where.append(token + "id_caso = ?");
                token = " AND ";
            }
            if (bo.getSecuencialOperacion() > 0) {
                where.append(token + "secuencial_operacion < ?");
                token = " AND ";
            }
            //aqui el remitente es el responsable
            //en la operacion anterior !!!
            if (bo.getRemitenteId() != null) {
                where.append(token + "responsable_id = ? OR (responsable_id is null and responsable_area=?) ");
                token = " AND ";
            }
            where.append(token + "terminada = 'N'");
            String strQuery = "SELECT * FROM cg_bitacora_operacion " + where.toString() + " order by secuencial_operacion desc";
            /*
			System.out.println("BitacoraOperacionManager.findPreviousOperation strQuery=["
							   +strQuery
							   +"] prm1=["
							   +bo.getIdCaso()
							   +"], prm2=["
							   +bo.getSecuencialOperacion()
							   +"], prm3=["
							   +bo.getRemitenteId()
							   +"], prmArea=["
							   +bo.getRemitenteArea()
							   +"]");
			*/
            pstmnt = conn.prepareStatement(strQuery);
            int i = 1;
            if (bo.getIdCaso() > 0)
                pstmnt.setInt(i++, bo.getIdCaso());
            if (bo.getSecuencialOperacion() > 0)
                pstmnt.setInt(i++, bo.getSecuencialOperacion());
            //aqui el remitente es el responsable
            //en la operacion anterior !!!
            if (bo.getRemitenteId() != null) {
                pstmnt.setString(i++, bo.getRemitenteId());
                pstmnt.setString(i++, bo.getRemitenteArea());
            }
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                retVal = new BitacoraOperacion();
                retVal.setIdBitacora(rs.getInt("id_bitacora"));
                retVal.setIdCaso(rs.getInt("id_caso"));
                retVal.setIdOperacion(rs.getInt("id_operacion"));
                retVal.setSecuencialAnterior(rs.getInt("secuencial_anterior"));
                retVal.setSecuencialOperacion(rs.getInt("secuencial_operacion"));
                retVal.setSecuencialSiguiente(rs.getInt("secuencial_siguiente"));
                retVal.setStatus(rs.getInt("status"));
                retVal.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                retVal.setFechaCompromiso(rs.getTimestamp("fecha_compromiso"));
                retVal.setFechaTermino(rs.getTimestamp("fecha_termino"));
                retVal.setResponsableId(rs.getString("responsable_id"));
                retVal.setResponsableArea(rs.getString("responsable_area"));
                retVal.setRemitenteId(rs.getString("remitente_id"));
                retVal.setRemitenteArea(rs.getString("remitente_area"));
                retVal.setTurnadoId(rs.getString("turnado_id"));
                retVal.setTurnadoArea(rs.getString("turnado_area"));
                retVal.setInstruccion(rs.getString("instruccion"));
                retVal.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                retVal.setOprStack(rs.getInt("opr_stack"));
                boolean isTerminada = false;
                try {
                    isTerminada = rs.getString("terminada").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                retVal.setTerminada(isTerminada);
                boolean isLeida = false;
                try {
                    isLeida = rs.getString("leida").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                retVal.setLeida(isLeida);
                retVal.setIdTipoCaso(rs.getInt("id_tipo_caso"));
                retVal.setIdGabinete(rs.getInt("id_gabinete"));
                retVal.setFolio(rs.getString("folio"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retVal;
    }

    public static List selectBitacoraOperacion(Connection conn, String u_login, int id_tc, int id_oper) throws SQLException {
        List l = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String query = "select * from cg_bitacora_caso bc, cg_bitacora_operacion bo " + "where bc.id_caso = bo.id_caso " + "and (bo.remitente_id = ? or bo.responsable_id = ?) " + "and bc.tipo_caso = ? " + "and bo.id_operacion = ? " + "order by bo.id_operacion, bc.folio";
            //System.out.println("query=["+query+"], u_login=["+u_login+"], id_tc=["+id_tc+"], id_oper=["+id_oper+"]");
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, u_login);
            pstmnt.setString(2, u_login);
            pstmnt.setInt(3, id_tc);
            pstmnt.setInt(4, id_oper);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                BitacoraCaso bc = new BitacoraCaso();
                bc.setIdCaso(rs.getInt("id_caso"));
                bc.setFolio(rs.getString("folio"));
                bc.setTipoCaso(rs.getInt("tipo_caso"));
                bc.setStatus(rs.getInt("status"));
                bc.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                bc.setFechaCompromiso(rs.getTimestamp("fecha_compromiso"));
                bc.setFechaUltimaOperacion(rs.getTimestamp("fecha_ultima_operacion"));
                bc.setIdGabinete(rs.getInt("id_gabinete"));
                bc.setTituloAplicacion(rs.getString("titulo_aplicacion"));
                bc.setResponsableId(rs.getString("responsable_id"));
                bc.setResponsableArea(rs.getString("responsable_area"));
                bc.setRemitenteId(rs.getString("remitente_id"));
                bc.setRemitenteArea(rs.getString("remitente_area"));
                boolean isCerrado = false;
                try {
                    isCerrado = rs.getString("cerrado").toUpperCase().equals("S");
                } catch (Exception e) {
                    //ignore
                }
                bc.setCerrado(isCerrado);
                BitacoraOperacion bo = new BitacoraOperacion();
                bo.setFechaCompromiso(rs.getTimestamp("fecha_compromiso"));
                bo.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                bo.setFechaTermino(rs.getTimestamp("fecha_termino"));
                bo.setIdBitacora(rs.getInt("id_bitacora"));
                bo.setIdCaso(rs.getInt("id_caso"));
                bo.setIdTipoCaso(rs.getInt("tipo_caso"));
                bo.setIdOperacion(rs.getInt("id_operacion"));
                bo.setInstruccion(rs.getString("instruccion"));
                bo.setOprStack(rs.getInt("opr_stack"));
                boolean boolLeida = false;
                try {
                    boolLeida = rs.getString("leida").equals("S");
                } catch (Exception e) {
                    //ignore
                }
                bo.setLeida(boolLeida);
                bo.setRemitenteArea(rs.getString("remitente_area"));
                bo.setRemitenteId(rs.getString("remitente_id"));
                bo.setResponsableArea(rs.getString("remitente_area"));
                bo.setResponsableId(rs.getString("remitente_id"));
                bo.setSecuencialAnterior(rs.getInt("secuencial_anterior"));
                bo.setSecuencialOperacion(rs.getInt("secuencial_operacion"));
                bo.setSecuencialSiguiente(rs.getInt("secuencial_siguiente"));
                bo.setStatus(rs.getInt("status"));
                boolean boolTerminado = false;
                try {
                    boolTerminado = rs.getString("terminado").equals("S");
                } catch (Exception e) {
                    //ignore
                }
                bo.setTerminada(boolTerminado);
                bo.setTurnadoArea(rs.getString("turnado_area"));
                bo.setTurnadoId(rs.getString("turnado_id"));
                bo.setStatus(rs.getInt("status"));
                bo.setOperacion(getOperacion(conn, bo.getIdTipoCaso(), bo.getIdOperacion()));
                Vector myVector = new Vector();
                myVector.add(bc);
                myVector.add(bo);
                l.add(myVector);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return l;
    }

    private static Operacion getOperacion(Connection conn, int idTipoCaso, int idOperacion) throws SQLException {
        // obtiene la operacion
        Operacion o = new Operacion();
        o.setIdTC(idTipoCaso);
        o.setIdOperacion(idOperacion);
        return OperacionManager.select(conn, o);
    }

    public static String getGestionXml(Connection conn, String tituloAplicacion, String folio, String realPath) throws SQLException {
        String retVal = null;
        String query1 = "SELECT distinct * FROM vimx_arbol WHERE " + "typerow = 2 " + "AND typecolumn = 'docto.externo' " + "AND titulo_aplicacion = ? " + "AND namecolumn = ? " + "ORDER BY typerow, id_carpeta_padre, id_carpeta_hija";
        //System.out.println("query=["+query1+"], titulo_aplicacion=["+tituloAplicacion+"], namecolumn=["+folio+"]");
        PreparedStatement pstmnt1 = null;
        PreparedStatement pstmnt2 = null;
        PreparedStatement pstmnt3 = null;
        //Actualiza el remitente
        try {
            pstmnt1 = conn.prepareStatement(query1);
            pstmnt1.setString(1, tituloAplicacion);
            pstmnt1.setString(2, folio);
            ResultSet rs1 = pstmnt1.executeQuery();
            String query2 = "SELECT * FROM imx_pagina WHERE " + " titulo_aplicacion = ? " + " and id_gabinete = ? " + " and id_carpeta_padre = ? " + " and id_documento = 1 ";
            pstmnt2 = conn.prepareStatement(query2);
            if (rs1.next()) {
                pstmnt2.setString(1, tituloAplicacion);
                pstmnt2.setInt(2, rs1.getInt("id_gabinete"));
                pstmnt2.setInt(3, rs1.getInt("id_carpeta_padre"));
                ResultSet rs2 = pstmnt2.executeQuery();
                if (rs2.next()) {
                    String query3 = "SELECT * FROM imx_volumen WITH (NOLOCK) " + "WHERE volumen = ?";
                    pstmnt3 = conn.prepareStatement(query3);
                    pstmnt3.setString(1, rs2.getString("volumen"));
                    ResultSet rs3 = pstmnt3.executeQuery();
                    if (rs3.next()) {
                        retVal = getGestionXml(rs3.getString("UNIDAD_DISCO"), rs3.getString("RUTA_BASE"), rs3.getString("RUTA_DIRECTORIO"), realPath, rs2.getString("NOM_ARCHIVO_VOL"), rs2.getString("NOM_ARCHIVO_ORG"));
                    }
                }
            }
        } catch (Exception sqle) {
            //que hacemos?
            System.out.println("error al copiar el archivo");
            sqle.printStackTrace();
        } finally {
            if (pstmnt1 != null) {
                pstmnt1.close();
                pstmnt1 = null;
            }
            if (pstmnt2 != null) {
                pstmnt2.close();
                pstmnt2 = null;
            }
            if (pstmnt3 != null) {
                pstmnt3.close();
                pstmnt3 = null;
            }
        }
        return retVal;
    }

    public static String getGestionXml(Connection conn, String tituloAplicacion, String folio, String realPath, String contextPath) throws SQLException {
        String retVal = null;
        String query = "SELECT distinct * FROM vimx_arbol " + "where titulo_aplicacion = ? " + "and namecolumn = ? " + "and typecolumn = 'docto.externo' " + "and typerow = 2 " + "ORDER BY typerow, id_carpeta_padre, id_carpeta_hija";
        //System.out.println("query=["+query+"], titulo_aplicacion=["+tituloAplicacion+"], namecolumn=["+folio+"]");
        PreparedStatement pstmnt = null;
        //Actualiza el remitente
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, tituloAplicacion);
            pstmnt.setString(2, folio);
            ResultSet rs = pstmnt.executeQuery();
            String unidad = null;
            String rutaBase = null;
            String rutaDirectorio = null;
            String nombreArchivoVolumen = null;
            String nombreArchivoOriginal = null;
            if (rs.next()) {
                unidad = rs.getString("UNIDAD_DISCO");
                rutaBase = rs.getString("RUTA_BASE");
                rutaDirectorio = rs.getString("RUTA_DIRECTORIO");
                nombreArchivoVolumen = rs.getString("NOM_ARCHIVO_VOL");
                nombreArchivoOriginal = rs.getString("NOM_ARCHIVO_ORG");
            }
            retVal = getGestionXml(unidad, rutaBase, rutaDirectorio, realPath, nombreArchivoVolumen, nombreArchivoOriginal);
        } catch (Exception sqle) {
            //que hacemos?
            System.out.println("error al copiar el archivo");
            sqle.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retVal;
    }

    public static String getGestionXml(String unidad, String rutaBase, String rutaDirectorio, String realPath, String nombreArchivoVolumen, String nombreArchivoOriginal) throws SQLException {
        File archTif = null;
        File archXML = null;
        try {
            archTif = new File(unidad + rutaBase + rutaDirectorio + nombreArchivoVolumen);
            archXML = new File(realPath + nombreArchivoOriginal);
            copyFile(archTif, archXML);
        } catch (Exception sqle) {
            //que hacemos?
            System.out.println("error al copiar el archivo");
            sqle.printStackTrace();
        }
        return archXML.getName();
    }

    public static void copyFile(File in, File out) throws Exception {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[2048];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }

    public static int updateProrroga(Connection conn, BitacoraOperacion bo) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_bitacora_operacion " + " SET status=?, fecha_termino=?, " + " secuencial_anterior=?, secuencial_siguiente=?, " + " turnado_id=?, turnado_area=?, " + " terminada=?, leida=?, instruccion=? " + " WHERE id_bitacora=?");
            pstmnt.setInt(1, bo.getStatus());
            pstmnt.setTimestamp(2, bo.getFechaTermino());
            pstmnt.setInt(3, bo.getSecuencialAnterior());
            pstmnt.setInt(4, bo.getSecuencialSiguiente());
            pstmnt.setString(5, bo.getTurnadoId());
            pstmnt.setString(6, bo.getTurnadoArea());
            String strTerminada = (bo.isTerminada()) ? "S" : "N";
            pstmnt.setString(7, strTerminada);
            String strLeida = (bo.isLeida()) ? "S" : "N";
            pstmnt.setString(8, strLeida);
            pstmnt.setString(9, bo.getInstruccion());
            pstmnt.setInt(10, bo.getIdBitacora());
            retval = pstmnt.executeUpdate();
        } catch (Exception e) {
            //System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
    //END CLASS
}
