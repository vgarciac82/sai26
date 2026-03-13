package com.syc.crud.request.processors;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Map;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import com.syc.crud.CRUDException;
import com.syc.crud.CRUDRequestProcessor;
import com.syc.gestion.core.CFSequenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceCRUDRequestProcessor implements CRUDRequestProcessor {

    public static final String REQUEST_PROCESOR_TYPE = "seq";

    public static final String CONTENT_TYPE = "application/json";

    private static final Logger log = LoggerFactory.getLogger(SequenceCRUDRequestProcessor.class);

    private JsonFactory jsonFactory;

    private JsonGenerator jsonGenerator;

    private String jniName;

    public SequenceCRUDRequestProcessor() {
        jsonFactory = new JsonFactory();
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public String getRequestProcessorType() {
        return REQUEST_PROCESOR_TYPE;
    }

    public void processData(String pathname, Map<String, String[]> params, OutputStream out) throws CRUDException {
        boolean success = false;
        try {
            String seqName = params.get("n")[0];
            if ((seqName == null) || (seqName.trim().isEmpty()))
                throw new CRUDException("El parametro nombre de secuencia no debe ser nulo o vacio");
            jsonGenerator = jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
            jsonGenerator.writeStartObject();
            String nextVal = String.valueOf(CFSequenceManager.getInstance().nextVal(seqName));
            jsonGenerator.writeStringField("nextVal", nextVal);
            success = true;
        } catch (Exception exc) {
            success = false;
            try {
                jsonGenerator.writeStringField("message", exc.getLocalizedMessage());
            } catch (Exception e) {
                log.warn("Enviando JSON message", e);
            }
            if (exc instanceof CRUDException)
                throw (CRUDException) exc;
            else
                throw new CRUDException(exc);
        } finally {
            if (jsonGenerator != null)
                try {
                    jsonGenerator.writeStringField("success", String.valueOf(success));
                    jsonGenerator.writeEndObject();
                    jsonGenerator.close();
                } catch (Exception exc) {
                    log.warn("Cerrando JSON", exc);
                }
        }
    }

    @Override
    public void setJniName(String jniName) {
        this.jniName = jniName;
    }

    @Override
    public String getJniName() {
        return this.jniName;
    }

    public void reloadSQL(String arg0) throws FileNotFoundException, CRUDException {
        // TODO Auto-generated method stub
    }
}
