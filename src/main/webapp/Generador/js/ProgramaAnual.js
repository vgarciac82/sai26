var oTable1;
var oTable2;
var oTable4;

function resizeDt(){
	var tab = parseInt($("#tbs").val());
	//alert (tab)
	switch (tab){
		case 0:
			if($('#tblSeleccionaCucop >tbody >tr').length>0){
				oTable1.fnAdjustColumnSizing();
			}
			if($('#tblCucops >tbody >tr').length>0){
				oTable2.fnAdjustColumnSizing();
			}
			
		break;
		case 1:
			if($('#tblDisponible >tbody >tr').length>0){
				oTable4.fnAdjustColumnSizing();
			}
			if($('#tblcucopPeriodo >tbody >tr').length>0){
				oTable3.fnAdjustColumnSizing();
			}
			
		break;
		
	}
}