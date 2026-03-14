package com.syc.gestion.servlet;

import java.util.Base64;

public interface GestionInterface {

    // Commands para PRM_CMD
    public static final int CMD_MAIN = 0;

    public static final int CMD_OPEN_INBOX = 1;

    public static final int CMD_INIT_CASE = 2;

    public static final int CMD_EXEC_CASE = 3;

    public static final int CMD_ADVANCE_CASE = 4;

    public static final int CMD_XML_SAVE = 5;

    public static final int CMD_MSG_COMPOSE = 6;

    public static final int CMD_SEND_MSG = 7;

    public static final int CMD_MSG_SENDED = 8;

    public static final int CMD_MSG_RECIVED = 9;

    public static final int CMD_MSG_READED = 10;

    public static final int CMD_SEND_CHNG_PWD = 11;

    public static final int CMD_CHNG_PWD = 12;

    public static final int CMD_GET_USRES_GROUPS = 13;

    public static final int CMD_GET_BODY_MSG = 14;

    public static final int CMD_DEL_MSG = 15;

    public static final int CMD_GET_USRES_GROUPS_OPER = 16;

    public static final int CMD_GENERIC_INTERFACE = 17;

    public static final int CMD_GET_DOCUMENT_FOR_NAME = 18;

    public static final int CMD_CREATE_KEYS = 19;

    public static final int CMD_DOWNLOAD_KEYS = 20;

    public static final int CMD_DELETE_CASE = 21;

    public static final int CMD_USER_LEAVE_CASE = 22;

    public static final int CMD_OPEN_INBOX2 = 23;

    public static final int CMD_EXIT = 99;

    public static final String SAVE = "SAVE";

    // Session's Attributes
    public static final String ATT_USER = "usuario";

    public static final String ATT_LOGIN = "uLogin";

    public static final String ATT_INBOX = "inbox";

    public static final String ATT_PAG_INBOX = "pag.inbox";

    public static final String ATT_OBJ_PAG_INBOX = "obj.pag.inbox";

    public static final String ATT_CASE = "caso";

    public static final String ATT_CASE_OPER = "casoOper";

    public static final String ATT_MSG = "msg";

    public static final String ATT_NTA = "nta";

    public static final String ATT_TREE = "tree.model";

    public static final String ATT_DOCUMENT = "documento";

    public static final String ATT_TREE_MSG = "tree.model.msg";

    public static final String ATT_CASE_MSG = "msg.caso";

    public static final String ATT_EXPORT_TO = "export.to";

    public static final String ATT_EXP_HEADER = "export.header";

    public static final String ATT_EXP_BODY = "export.body";

    // Esteban
    public static final String ATT_EXP_BODY_DETALLE = "export.body.detalle";

    // Badillo.
    // Fecha:
    // 25/Febrero/2010.
    // Se
    // agrega
    // nueva
    // constante.
    public static final String ATT_SQL_RPT = "rpt.sql";

    public static final String ATT_CMD_AUT = "INGRESO_AUT";

    // REQUISICIONES
    public static final String ATT_ReqEjercicio = "ReqEjercicio";

    public static final String ATT_ReqTipoSolicitud = "ReqTipoSolicitud";

    public static final String ATT_ReqUnidadEjec = "ReqUnidadEjec";

    public static final String ATT_ReqConsecutivo = "ReqConsecutivo";

    // CONSOLIDADO
    public static final String ATT_ConEjercicio = "ConEjercicio";

    public static final String ATT_ConTipoConsolidado = "ConTipoConsolidado";

    public static final String ATT_ConUnidadEjec = "ConUnidadEjec";

    public static final String ATT_ConConsecutivo = "ConConsecutivo";

    // PROCEDIMIENTO
    public static final String ATT_ProEjercicio = "ProEjercicio";

    public static final String ATT_ProTipoProcedimiento = "ProTipoProcedimiento";

    public static final String ATT_ProUnidadEjecutora = "ProUnidadEjecutora";

    public static final String ATT_ProConsecutivo = "ProConsecutivo";

    // PEDIDO
    public static final String ATT_PedidoEjercicio = "PedidoEjercicio";

    public static final String ATT_PedidoTipoPedido = "PedidoTipoPedido";

    public static final String ATT_PedidoUnidadEjec = "PedidoUnidadEjec";

    public static final String ATT_PedidoConsecutivo = "PedidoConsecutivo";

    public static final String ATT_PedidoAbierto = "PedidoAbierto";

    public static final String ATT_EstadoPedido = "EstadoPedido";

    // CONTRATO
    public static final String ATT_ContratoDefinitivo = "ContratoDefinitivo";

    public static final String ATT_ContratoEjercicio = "ContratoEjercicio";

    public static final String ATT_ContratoTipoContrato = "ContratoTipoContrato";

    public static final String ATT_ContratoUnidadEjec = "ContratoUnidadEjec";

    public static final String ATT_ContratoConsecutivo = "ContratoConsecutivo";

    public static final String ATT_ContratoAbierto = "ContratoAbierto";

    public static final String ATT_EstadoContrato = "EstadoContrato";

    public static final int ATT_NID_FECHA_FORMALIZACION = 12;

    public static final int ATT_NID_FECHA_ENTREGA = 13;

    // Contrato Plurianual
    public static final String ATT_ContratoEjercicioPlurianual = "ContratoEjercicioPlurianual";

    public static final String ATT_ContratoUnidadEjecPlurianual = "ContratoUnidadEjecPlurianual";

    public static final String ATT_ContratoPlurianual = "ContratoPlurianual";

    public static final String ATT_ContratoPlurianualDefinitivo = "ContratoPlurianualDefinitivo";

    public static final String ATT_tipoCambioPlurianual = "tipoCambioPlurianual";

    public static final String ATT_tipoContratoPlurianual = "tipoContratoPlurianual";

    public static final String ATT_ContratoPluAbierto = "ContratoPluAbierto";

    // Contrato Plurianual pedido
    public static final String ATT_PedidoEjercicioPlurianual = "PedidoEjercicioPlurianual";

    public static final String ATT_PedidoUnidadEjecPlurianual = "PedidoUnidadEjecPlurianual";

    public static final String ATT_PedidoPlurianual = "PedidoPlurianual";

    public static final String ATT_PedidoPlurianualDefinitivo = "PedidoPlurianualDefinitivo";

    public static final String ATT_tipoCambioPlurianualPedido = "tipoCambioPlurianualPedido";

    public static final String ATT_tipoContratoPlurianualPedido = "tipoContratoPlurianualPedido";

    // CONTRATO pasivo
    public static final String ATT_ContratoEjercicioPasivo = "ContratoEjercicioPasivo";

    public static final String ATT_ContratoUnidadEjecPasivo = "ContratoUnidadEjecPasivo";

    public static final String ATT_ContratoPasivo = "ContratoPasivo";

    public static final String ATT_ContratoPasivoDefinitivo = "ContratoPasivoDefinitivo";

    public static final String ATT_tipoCambioPasivo = "tipoCambioPasivo";

    public static final String ATT_tipoContratoPasivo = "tipoContratoPasivo";

    public static final String ATT_SubpartidaPasivo = "SubpartidaPasivo";

    // Pedido pasivo
    public static final String ATT_PedidoEjercicioPasivo = "PedidoEjercicioPasivo";

    public static final String ATT_PedidoUnidadEjecPasivo = "PedidoUnidadEjecPasivo";

    public static final String ATT_PedidoPasivo = "PedidoPasivo";

    public static final String ATT_PedidoPasivoDefinitivo = "PedidoPasivoDefinitivo";

    public static final String ATT_tipoCambioPasivoPedido = "tipoCambioPasivoPedido";

    public static final String ATT_tipoPedidoPasivo = "tipoPedidoPasivo";

    public static final String ATT_SubpartidaPasivoPedido = "SubpartidaPasivoPedido";

    public static final String ATT_tipoContratoPasivoPedido = "tipoContratoPasivoPedido";

    // Mantenimiento de Usuarios
    public static final int OPER_INS = 1;

    public static final int OPER_UPD = 2;

    public static final int OPER_DEL = 3;

    public static final int OPER_AVI = 5;

    // Pedido Modificatorio
    public static final String ATT_PedidoModificatorioEjercicio = "PedidoModificatorioEjercicio";

    public static final String ATT_PedidoModificatorioId = "PedidoModificatorioId";

    public static final String ATT_PedidoModificatorioDefinitivo = "PedidoModificatorioDefinitivo";

    public static final String ATT_PedidoModificatorioConsecutivo = "PedidoModificatorioConsecutivo";

    public static final String ATT_PedidoModificatorioTipoArchivo = "PedidoModificatorioTipoArchivo";

    // Contrato Modificatorio
    public static final String ATT_ContratoModificatorioDefinitivo = "ContratoModificatorioDefinitivo";

    public static final String ATT_ContratoModificatorioConsecutivo = "ContratoModificatorioConsecutivo";

    public static final String ATT_ContratoModificatorioId = "ContratoModificatorioId";

    public static final String ATT_ContratoModificatorioEjercicio = "ContratoModificatorioEjercicio";

    public static final String ATT_ContratoModificatorioUE = "ContratoModificatorioUE";

    public static final String ATT_ContratoIsModificatorioEjercicioAnt = "ContratoIsModificatorioEjercicioAnt";

    // Contrato cap 4000
    public static final String ATT_ContratCap4Definitivo = "ContratCap4Definitivo";

    public static final String ATT_EstatusContratCap4 = "Estatus";

    public static final String ATT_ContratCap4Abierto = "EsContratoAbierto";

    // Convenios cap 4000
    public static final String ATT_ContractConvCap4Definitivo = "ContratConvCap4Definitivo";

    public static final String ATT_ContractConvCap4Consecutivo = "ConsecutivoConvCap4";

    public static final String ATT_nIdContModCap4 = "nIdContModCap4";

    // Contratos Artc 25
    public static final String ATT_ContratArt25Definitivo = "ContratArt25Definitivo";

    public static final String ATT_EstatusContratArt25 = "Estatus";

    public static final String ATT_ContratArt25Abierto = "EsContratoAbierto";

    // Garantías de Contratos
    public static final String ATT_GarantiasContDefinitivo = "GarantiasContDefinitivo";

    // PRECOMPROMISO MATERIALES
    public static final int IDTC_PRECOMMATERIALES = 35;

    // CASO PAGO DIRECTO
    public static final int IDTC_PAGODIRECTO = 4;

    // CASO OBRA PÚBLICA
    public static final int IDTC_OBRAPUBLICA = 23;

    // CASO RELACION DE GASTOS
    public static final int IDTC_RELACIONGASTO = 11;

    // PRECOMPROMISO
    public static final int IDTC_PRECOMPROMISO = 14;

    public static final int IDTC_COMPROMISO = 7;

    // Contrato diverso
    public static final int IDTC_CONTRATO_DIVERSO = 9;

    public static final int CENTRO_TRABAJO_NAYARIT = 18;

    public static final int TIPO_RETENCION_CEDULAR_NAYARIT = 8;

    public static final int CENTRO_TRABAJO_GUANAJUATO = 11;

    public static final int TIPO_RETENCION_CEDULAR_GUANAJUATO = 9;

    // APARTADO
    public static final int IDTC_APARTADO = 25;

    // clausulas
    public static final String ATT_pDefinitivo = "PedidoDefinitivo";

    // Archivos Procedimiento
    public static final String ATT_ProcTipoArchivo = "ProcTipoArchivo";

    // Graficas
    public static final String ATT_cadenaXML = "cadenaXML";

    // Parameters
    public static final String PRM_CMD = "cmd";

    public static final String PRM_CASE = "caso";

    public static final String PRM_CASE_OPER = "casoOper";

    public static final String PRM_RESP = "responsable";

    public static final String PRM_OPER = "operacion";

    public static final String PRM_USER_MSG = "usrmsg";

    public static final String PRM_PROM_FILTER = "prmfltr";

    public static final String PRM_MSG_TYPE = "type";

    public static final String PRM_OBSERV = "observ";

    public static final String ATT_CONEXION = "jdbc/gestion";

    public static final String ATT_SUPPLANT_USER = "usuarioSuplantando";

    public static final String ATT_EMPLEADO = "empleado";

    public static final String PRM_NIVEL_ADEC = "nivel_adec";

    // Manejo de Cobertura
    public static final int CBO_CAT_AREAS = 13;

    public static final int CBO_PRD_GESTION = 3;

    // Tipos de exportacion de reportes
    public static final int RPT_EXP_HTML = 1;

    public static final int RPT_EXP_EXCEL = 2;

    public static final int RPT_EXP_CHART = 3;

    public static final int RPT_EXP_PDF = 4;

    // esta
    public static final int RPT_CONSOLIDADO = 1;

    // en
    // Bd
    // pero
    // no
    // en
    // menu
    public static final int RPT_GENERAL = 2;

    public static final int RPT_POR_EMPLEADO = 3;

    public static final int RPT_POR_AREA = 4;

    // esta
    public static final int RPT_DETALLADOVIEJO = 5;

    // en
    // Bd
    // pero
    // no
    // en
    // menu
    // NO
    public static final int RPT_POR_EMPLEADO_DETALLE = 6;

    // esta
    // en
    // Bd
    // NI
    // en
    // menu,
    // no
    // se
    // por
    // que
    // s
    // elo
    // brico
    // Ethiel,
    public static final int RPT_AUDITORIA = 7;

    // fue
    // el
    // id
    // que
    // seguia
    // en
    // BD
    // NO
    public static final int RPT_POR_AREA_DETALLE = 9;

    // estaba
    // en
    // BD
    // ni
    // en
    // menu,
    // tenia
    // 8
    // pero
    // tendra
    // que
    // cambiar
    // cuando
    // lo
    // hagan
    // al
    // id
    // que
    // asigne
    // la
    // BD,
    // por
    // el
    // momento
    // le
    // dejo
    // el
    // 8
    public static final int RPT_HOMOLOGACION_VIATICOS = 8;

    // REAE.-
    public static final int RPT_RESPUESTA = 11;

    // CREADO
    // PARA
    // NUEVO
    // REPORTES
    // REAE.-
    public static final int RPT_DETALLADO = 12;

    // CREADO
    // PARA
    // NUEVO
    // REPORTES
    // public static final int RPT_COMPLETA = 13; //REAE.- CREADO PARA NUEVO
    // REPORTES
    // CREADO
    public static final int RPT_DETALLADODETALLE = 14;

    // PARA
    // NUEVO
    // REPORTES
    // CREADO
    public static final int RPT_DETALLETRAZA = 16;

    // PARA
    // NUEVO
    // REPORTES
    // CREADO
    public static final int RPT_DETALLADODETALLETRAZA = 117;

    // PARA
    // NUEVO
    // REPORTES
    public static final int RPT_POLIZAS = 18;

    public static final int RPT_BALANZA = 13;

    public static final int RPT_PAOP = 15;

    /// TABLA
    public static final int RPT_COMPARATIVA = 100;

    /// COMPARATIVA
    // Constantes de reportes
    public static final int RPT_CNS_VENCIDOS = 1;

    public static final int RPT_CNS_NOVENCIDOS = 2;

    public static final int RPT_CNS_PENDIENTES = 3;

    public static final int RPT_CNS_CONCLUIDOS = 4;

    public static final int RPT_CNS_CASOS = 5;

    // VARIABLES POLIZAS CONTABLES
    public static final String ATT_FOLIOPOLIZA = "FOLIO_POLIZA";

    // TODO Reportes
    public static final int RPT_REPORTE1 = 31;

    public static final int RPT_REPORTE2 = 32;

    public static final int RPT_REPORTE3 = 33;

    public static final int RPT_REPORTE4 = 34;

    public static final int RPT_HOMOVIATI = 35;

    public static final int CMD_SEND_RESET_PWDS = 36;

    public static final int RPT_MOVIMIENTOS_RECT = 37;

    public static final int RPT_MOVIMIENTOS_REINT = 38;

    public static final int RPT_MOVIMIENTOS_RECTMIL = 39;

    public static final int RPT_MOVIMIENTOS_REINMIL = 40;

    public static final int CMD_FILTRA_INBOX = 41;

    // Pagos Directos Materiales
    public static final String ATT_PagoDirectoEstado = "PagoDirectoEstado";

    public static final String ATT_PagoDirectoFolio = "PagoDirectoFolio";

    public static final String ATT_PagoDirectoEjercicio = "PagoDirectoEjercicio";

    public static final String ATT_PagoDirectoUE = "PagoDirectoUE";

    // Relacion de Gastos
    public static final String ATT_RelacionGastosEjercicio = "RelacionGastosEjercicio";

    public static final String ATT_RelacionGastosFolio = "RelacionGastosFolio";

    public static final String ATT_RelacionGastosEstado = "RelacionGastosEstado";

    public static final String ATT_RelacionGastosDESTINO_GASTO = "RelacionGastosDestinoGasto";

    public static final String ATT_RelacionGastosTIPO_CONCEPTO = "RelacionGastosTipoConcepto";

    // TODO Verificar cual es el valor correcto!
    public static final String P7M_DATA = "";

    // Manejo de Consulta de Aplicacion (Expedientes)
    // NOTA: ID_GABINETE,ACTIVO siempre van si no falla
    // 'AplicacionManager.getQueryByExampleAplicacionData'
    // NOTA: Esto solo funciona para cuando se tiene una Gaveta
    // FIXME: APP_LST_CAMPOS, APP_LST_STY_CAMPOS poner como columnas de
    // IMX_DESCRIPCION, y modificar AdminWeb
    public static final String[][] APP_LST_CAMPOS = { { "ID_GABINETE", "" }, { "ACTIVO", "" }, { "FOLIO", "" }, { "REFERENCIA", "" }, { "ASUNTO", "" }, { "RESDDESC", "" }, { "RESUNOMBRE", "AyudaSyC autoCompletaSyC" }, { "DPC_F_LIMITE", "" } };

    // public static final String APP_LST_CAMPOS =
    // "ID_GABINETE,ACTIVO,FOLIO,REFERENCIA,ASUNTO,RESDDESC,RESUNOMBRE,DPC_F_LIMITE";
    // public static final String APP_LST_STY_CAMPOS = ",,,,,,AyudaSyC
    // autoCompletaSyC,";
    public static final String[][] APP_LST_CAMPOS_AVANZADA_I = { { "ID_GABINETE", "" }, { "ACTIVO", "" }, { "FOLIO", "*" }, { "NCONTROL", "*" }, { "REFERENCIA", "*" }, { "ALCANCE", "*" }, { "ANTECEDENTE", "*" }, { "ASUNTO", "*" }, { "TIPOASUNTO", "" }, { "TDDESCRIPCION", "" }, { "PRIORIDAD", "" }, { "DPC_F_RECEPCION", "" }, { "DPC_F_REGISTRO", "" }, { "DPC_F_DOCUMENTO", "" }, { "DPC_F_LIMITE", "" }, { "REMINUNOMBRE", "" }, { "REMINPTONOM", "" }, { "REMINDDESC", "" }, { "RESUNOMBRE", "" }, { "RESPTONOMBRE", "" }, { "RESDDESC", "" } };

    public static final String[][] APP_LST_CAMPOS_AVANZADA_E = { { "ID_GABINETE", "" }, { "ACTIVO", "" }, { "FOLIO", "*" }, { "NCONTROL", "*" }, { "REFERENCIA", "*" }, { "ALCANCE", "*" }, { "ANTECEDENTE", "*" }, { "ASUNTO", "*" }, { "TIPOASUNTO", "" }, { "TDDESCRIPCION", "" }, { "PRIORIDAD", "" }, { "DPC_F_RECEPCION", "" }, { "DPC_F_REGISTRO", "" }, { "DPC_F_DOCUMENTO", "" }, { "DPC_F_LIMITE", "" }, { "RENOMBRE", "" }, { "RECARGO", "" }, { "REPROCEDENCIA", "" }, { "REESTADO", "" }, { "REMUNICIPIO", "" }, { "REDIRECCION", "" }, { "RELOCALIDAD", "" }, { "RESUNOMBRE", "" }, { "RESPTONOMBRE", "" }, { "RESDDESC", "" } };

    // Parametros del sistema
    public static final String SYS_IP_PRODUCCION = "10.0.0.226";

    public static final String PREFIX_TEMP = "000000";

    public static final String FOLIO_GENERATOR = "com.syc.gestion.custom.DefaultFolioGenerator";

    // Parametros momentos contables
    public static final int APARTADO = 82101;

    public static final int PRECOMPROMISO = 82102;

    public static final int COMPROMISO = 82103;

    public static final int DISPONIBLE = 82106;

    public static final String EGRESOS_IMPLEMENTACION_PKG = "com.syc.egresos.core.impl";

    // Garantia
    public static final int GARANTIA = 1;

    public static final int ENDOSO = 2;

    public static final int GARANTIA_ANTICIPO = 1;

    public static final int GARANTIA_CUMPLIMIENTO = 2;

    public static final int GARANTIA_VICIOS_OCULTOS = 3;

    public static final String SYSTEM_NAME = "SAI";
}
