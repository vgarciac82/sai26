<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>

<%  
    
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();
	Integer UR_Int = new Integer(UR.substring(1));
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			"jdbc/gestion");
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	
	String CentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor() ;
%>
<%!private Logger log = Logger.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \""
						+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
					+ jniName + "\"");
		}
	}

	public String getValor(String data) {
		return (data == null ? "" : data);
	}%>
	

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

	<style type="text/css" title="currentStyle">
		@import "css/demo_page.css";
		@import "css/demo_table_jui.css";
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>

	<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css">
	




<script type="text/javascript" charset="utf-8">
	
	    var oTable, oCurrentFocus;
		var asInitVals = new Array();
		var arrRet = null;
  	    var aSelected = [0];

		$(document).ready(function() {

				for (var i = 1; i < 5; i++)
				{
				$("#col"+i+"_filter" ).keyup( function(evt) 
				  {
				  oCurrentFocus = this; fnFilterColumn(evt);
				  } 
				);
				}

			$("#dt_saldos tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					$(this).css("visibility","visible");
				});

			oTable = createDataTable("");

		
    		

		});

		function formSubmited() {
                alert("Enviada");
            }


		function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();

				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}

			function fnFilterColumn (evt)
			{
				var value, myWhere, token;
				var column_name = [
					"dNombre",
					"dRFC",
					"dCuenta",
					"dDetalleCuenta"
					];
				myWhere = "";
				token = "&qw=";

				var charCode = evt.which ? evt.which : window.event.keyCode;
				if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
					if (charCode <= 13) return true;

					if (charCode < 96 || charCode > 106) { // Es teclado numerico?
						var keyChar = String.fromCharCode(charCode);
						var re = /[a-zA-Z0-9.]/;
						if (!re.test(keyChar)) return true;
					}
				}

				for (var i = 1; i < 5; i++) {
					value = $("#col"+i+"_filter" ).val();
					if (value !== "") {
						myWhere += token + column_name[i - 1] + " LIKE '%25" + value + "%25'";
						token = " AND ";
					}
				}
				
				oTable = createDataTable(myWhere);
				return true;
			}

		function windowStatus( texto )
		{
			window.status=texto
		}

		function createDataTable(w) {

		return $("#dt_saldos").dataTable({
				"bDestroy": true,
				fnDrawCallback: function() {
					$(oCurrentFocus).focus(function() {
						if (this.createTextRange) {
							var r = this.createTextRange();
							r.collapse(false);
							r.select();
						}
						this.focus();
					});
					$(oCurrentFocus).focus();
				},
				bAutoWidth : false,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Filtrar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
						}
				},
				bServerSide: true,
       			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_saldoBeneficiario" + w,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				//aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "dnombre"   },
					{ sName: "dRFC" },
					{ sName: "nCuenta"	},
					{ sName: "dCuenta" },
					{ sName: "saldoInicial"},
					{ sName: "saldoFinal"}
					
				]
				
        	});
		}
		

									
function imprimir()
{



var numColumns = oTable.fnGetData(0).length;;  
var temp="";

for(var r=0;r<oTable.fnGetData().length;r++)
    {
        for(var c=0;c < numColumns;c++)
        {
          temp+=oTable.fnGetData(r,c)+"|";   
        }
        
    }

document.getElementById("tabla0").value = temp;



temp="";


var nRow =  $('#dt_saldos thead tr')[0];


for(var i=0;i<oTable.fnGetData(0).length;i++)
{
temp+=nRow.cells[i].innerText+"|";
}

document.getElementById("colsxTabla").value=oTable.fnGetData(0).length;
document.getElementById("encabezadosTabla0").value = temp;
document.getElementById("titulosxTabla").value = "Saldos";
document.getElementById("formatosTabla0").value = "String|String|String|String|Double|Double|";
document.getElementById("tTablas").value = "1";

document.formulario.submit();


}




</script>

  <title>Saldos por RFC </title>
</head>



	<body id="dt_example">
	

<div  id="container" class="container SyCData">



<form action="../reports/GeneraExcelTable" method="post" id="formulario" name="formulario">

	<h1>Saldos por RFC</h1>
	<input type="hidden" name="tabla0" id="tabla0" />
	<input type="hidden" name="colsxTabla" id="colsxTabla" />
	<input type="hidden" name="titulosxTabla" id="titulosxTabla" />
	<input type="hidden" name="encabezadosTabla0" id="encabezadosTabla0" />
	<input type="hidden" name="formatosTabla0" id="formatosTabla0" />
	<input type="hidden" name="tTablas" id="tTablas" />
	
	<input type="button" value="Imprimir" onclick="imprimir();";  />
	<table id="dt_saldos" border="0" >
	
	<thead>
	<tr>
	<th>Nombre</th>
	<th>RFC</th>
	<th>Cuenta</th>
	<th>Detalle Cuenta </th>
	<th>Saldo Inicial</th>
	<th>Saldo Final</th>
	</tr>
	</thead>
	<tfoot>
	<tr>
	<th><input type="text" name="col1_filter" id="col1_filter"  onMouseOver="windowStatus('dnombre');" /> </th>
	<th><input type="text" name="col2_filter" id="col2_filter"  onMouseOver="windowStatus('dRFC');" /></th>
	<th><input type="text" name="col3_filter" id="col3_filter"  onMouseOver="windowStatus('nCuenta');" /></th>
	<th><input type="text" name="col4_filter" id="col4_filter"  onMouseOver="windowStatus('dCuenta');" /></th>
	<th></th>
	<th></th>
	</tr>
	</tfoot>
	
	
	
	</table>
			

</form>
		
	
	
	
	
</div>



</body>

</html>
