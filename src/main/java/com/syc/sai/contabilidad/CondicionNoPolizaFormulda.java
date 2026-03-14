package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.util.List;
import com.syc.contable.AccountingEngineException;
import com.syc.contable.DocPolizaEncabezado;
import com.syc.contable.PolizaManager;
import java.util.Base64;

public class CondicionNoPolizaFormulda extends CondicionCierreMes {

    public void preEjecucion(Connection con, int nMes, int aEjercicioFiscal, String cCentroContable) throws AccountingEngineException {
    }

    public boolean CumpleCondicion(Connection conn, int nMes, int aEjercicioFiscal, String cCentroContable) throws AccountingEngineException {
        boolean r = false;
        try {
            MesContable m = new MesContable();
            m.setaEjercicioFiscal(aEjercicioFiscal);
            m.setcCentroContable(cCentroContable);
            int mesActualAbierto = MesContableManager.ultimoMesContableAbierto(conn, m);
            if (mesActualAbierto <= nMes) {
                List<DocPolizaEncabezado> l = PolizaManager.getPolizasEnCaptura(conn, cCentroContable, nMes, aEjercicioFiscal, mesActualAbierto == nMes);
                if (l.size() > 0) {
                    StringBuffer msg = new StringBuffer("No puede cerrar el mes ya que existen documentos sin  autorizar:<br><br>");
                    /*
					 * for (Iterator<DocPolizaEncabezado> i = l.iterator(); i
					 * .hasNext();) { DocPolizaEncabezado d = i.next();
					 * msg.append("Centro Contable:" + d.getcCentroContable() +
					 * "; "); msg.append("Documento:" + d.getnFolioDocPoliza() +
					 * "; "); msg.append("Fecha de Captura:" + d.getfCarga());
					 * msg.append("<br>"); }
					 */
                    setMensaje(msg.toString());
                } else
                    r = true;
            } else
                r = true;
            return r;
        } catch (Exception e) {
            throw new AccountingEngineException(e);
        }
    }
}
