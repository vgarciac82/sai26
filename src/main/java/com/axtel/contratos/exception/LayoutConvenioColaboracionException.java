package com.axtel.contratos.exception;

import java.util.List;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class LayoutConvenioColaboracionException extends Exception {

    /**
     */
    private static final long serialVersionUID = 2637520268214318187L;

    /**
     */
    public LayoutConvenioColaboracionException(List<String> errores) {
        super(Util.join(errores, '\n'));
    }

    /**
     * @param message
     */
    public LayoutConvenioColaboracionException(String message) {
        super(message);
    }

    public LayoutConvenioColaboracionException(Exception e) {
        super(e);
    }

    public LayoutConvenioColaboracionException(String msg, Exception e) {
        super(msg, e);
    }
}
