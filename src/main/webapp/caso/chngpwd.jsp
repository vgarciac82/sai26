<%@ page import="com.syc.gestion.servlet.GestionInterface" %>
<%	String[] data = (String[]) session.getAttribute(GestionInterface.ATT_MSG);
	boolean withOldPwd = ((data != null) && (data[0] != null));
	boolean withNewPwd = ((data != null) && (data[1] != null));
	boolean withVerPwd = ((data != null) && (data[2] != null));
	boolean withMsg = ((data != null) && (data[4] != null));
	
	session.removeAttribute(GestionInterface.ATT_MSG);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>Cambiar Contrase&ntilde;a</title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>	
		<link type="text/css" href="../css/gestion.css" rel="stylesheet">
		<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>		
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {

			});

		function onLoad() {<%=withMsg?"window.alert(\""+data[4]+"\");"+data[3]:""%>}

	</script>


	</head>

<body onLoad="onLoad()">
<br/>
<center>
	<div id="container" class="container" style="width: 360px"> 
		<table id="dt_chngpwd" class="display">
			<thead>
				<tr>
					<div class="card-header"> <h3> Cambiar Contrase&ntilde;a </h3> </div>
					<hr class="mt-3"/>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td align="center">
						<form action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_CHNG_PWD%>" method="post">
						
							<div class="row">								
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">										
									<label for="oldpwd" class="form-label"> Contrase&ntilde;a actual: </label>
								</div>
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">
									<input type="password" name="oldpwd" class="form-control form-control-sm" size="32" maxlength="32" value="<%=withOldPwd?data[0]:""%>">									
								</div>
							</div>
							
							<div class="row">								
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
								</div>
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">										
									Mínimo 8 y Máximo 32 caracteres
								</div>
							</div>
							
							<div class="row">								
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">										
									<label for="newpwd" class="form-label"> Contrase&ntilde;a nueva: </label>
								</div>
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">
									<input type="password" name="newpwd" class="form-control form-control-sm" size="32" maxlength="32" value="<%=withNewPwd?data[1]:""%>">									
								</div>
							</div>
							
							<div class="row">								
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">										
									<label for="verpwd" class="form-label"> Repetir contrase&ntilde;a: </label>
								</div>
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">
									<input type="password" name="verpwd" class="form-control form-control-sm" size="32" maxlength="32" value="<%=withVerPwd?data[2]:""%>">									
								</div>
							</div>
							
							<div class="row">								
								<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">																			
								</div>
								<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">
									<input type="submit" name="pb_chngpwd" value="Cambiar" class="btn-secondary btn-sm"/>									
								</div>
							</div>
														
						</form>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</center>
</body>
</html>
