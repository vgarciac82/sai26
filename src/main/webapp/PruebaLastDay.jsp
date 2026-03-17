<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
	function  lastDayOfMonth(year,month){
		return  new Date(year, month, 0).getDate();
	}
	
	function diasDelMes(){
		for( i = 1; i <= 12; i++){
		    var ultimoDia = lastDayOfMonth(2018, parseInt( i, 10) ); 
			alert( i + " Ultimo dia: " + ultimoDia );
			i = parseInt( i, 10);
		}
	}
</script>
</head>
<body onload="diasDelMes()">
	Prueba de dias:
</body>
</html>