<%@ page import="com.syc.gestion.core.Usuario,com.syc.gestion.servlet.GestionInterface"%>
<%Usuario u = (Usuario) session
					.getAttribute(GestionInterface.ATT_USER);
 %>
<script type="text/javascript">
<!--
var monthNames = new Array('Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre');
var dayNames = new Array('Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sabado');

Number.prototype.zf = function(n) {
	var s = "" + this, r = "";
	if (n < s.length)
		return s;
	for (var i = 0; i < (n - s.length); i++)
		r += "0";
	return r + this;
}

Date.prototype.format = function(f) {
	if (!this.valueOf())
		return '&nbsp;';

	var d = this;

	return f.replace(/(yyyy|mmmm|mmm|mm|dddd|ddd|dd|hh|mi|ss|a\/p)/gi,
        function($1) {
            switch ($1.toLowerCase()) {
				case 'yyyy': return d.getFullYear();
				case 'mmmm': return monthNames[d.getMonth()];
				case 'mmm':  return monthNames[d.getMonth()].substr(0, 3);
				case 'mm':   return (d.getMonth() + 1).zf(2);
				case 'dddd': return dayNames[d.getDay()];
				case 'ddd':  return dayNames[d.getDay()].substr(0, 3);
				case 'dd':   return d.getDate().zf(2);
				case 'hh':   return ((h = d.getHours() % 12) ? h : 12).zf(2);
				case 'mi':   return d.getMinutes().zf(2);
				case 'ss':   return d.getSeconds().zf(2);
				case 'a/p':  return d.getHours() < 12 ? 'AM' : 'PM';
			}
		}
	);
}
setInterval("document.getElementById('fecha').innerText = (new Date()).format('dddd dd de mmmm de yyyy hh:mi:ss a/p ');",1000);
//-->
</script>
  <table width="100%"  background="../imagenes/fondo1.png">
    <tr>
      <td valign="top"><img src="../imagenes/logoi-afirme.png" alt="Afirme" width="200"></td>
      <td valign="top"><span style="color:#FF0000">Operaciones&nbsp;de:&nbsp;</span><strong><%=u.getNombre()%></strong></td>
      <td align="right" valign="top" id="fecha"></td>
    </tr>
  </table>