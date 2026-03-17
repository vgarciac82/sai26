<%@ page language="java" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>

	<head>
		<title>Get Next Sequence Value Test</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">

		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="Generador/js/crud.js"></script>
		<script type="text/javascript" charset="utf-8">
		function testNextSequenceVal() {
			getNextSequenceVal("SEQ_ONLY_TEST_BORRAME");
		}
		</script>
	</head>

	<body>
		<button onclick="testNextSequenceVal()">Test getNextSequenceVal</button>
	</body>

</html>
