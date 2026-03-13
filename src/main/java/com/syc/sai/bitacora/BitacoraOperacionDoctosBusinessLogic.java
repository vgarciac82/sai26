package com.syc.sai.bitacora;

import java.sql.Connection;
import org.apache.log4j.Logger;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.bitacora.core.BitacoraOperacionDoctosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class BitacoraOperacionDoctosBusinessLogic extends DataSourceManager {

	private static final Logger	log	= Logger.getLogger(BitacoraOperacionDoctosBusinessLogic.class);

	private String				uLogin;
	private String				modulo;
	private int					idTC;
	private int					nFolio;

	public BitacoraOperacionDoctosBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public BitacoraOperacionDoctosBusinessLogic(String jniName, String uLogin, String modulo, int idTC, int nFolio) {
		super.init(jniName);
		setuLogin(uLogin);
		setModulo(modulo);
		setIdTC(idTC);
		setnFolio(nFolio);
	}

	public String getuLogin() {
		return uLogin;
	}

	public void setuLogin(String uLogin) {
		this.uLogin = uLogin;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public int getIdTC() {
		return idTC;
	}

	public void setIdTC(int idTC) {
		this.idTC = idTC;
	}

	public int getnFolio() {
		return nFolio;
	}

	public void setnFolio(int nFolio) {
		this.nFolio = nFolio;
	}

	public boolean insertaBitacora(int idOpercion, String cLog) throws Exception {
		return insertaBitacora(getuLogin(), getModulo(), getIdTC(), getnFolio(), idOpercion, cLog);
	}

	public boolean insertaBitacora(String uLogin, String cModulo, int idTc, int nFolio, int idOperacion, String cLog) throws Exception {
		boolean insertado = false;
		Connection conn = null;

		try {
			conn = getConnection();
			insertado = BitacoraOperacionDoctosManager.insertaBitacora(conn, uLogin, cModulo, idTc, nFolio, idOperacion, cLog);
			conn.commit();
			return insertado;
		} catch (Exception e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
					log.warn(e2, e2);
				}
			throw e;
		} finally {

			CloseObject.closeObject(conn, false);
		}

	}

}
