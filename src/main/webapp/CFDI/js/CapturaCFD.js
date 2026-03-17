function init() {
	$( "input.AyudaSyC" ).subIniciaDlg();
	$( 'input[readonly]' ).each( function() {
		$( this ).addClass( "readOnly" );
	} );

	$( ".date" ).each( function() {
		$( this ).datepicker( {
		showOn : "button",
		buttonImage : "../images/calendar.gif",
		buttonImageOnly : true,
		changeYear : true,
		changeMonth : true
		} );
		$( this ).val( hoy );
	} );

	$( "#cfdiSerie" ).change( function() {
		changeCFDISerie();
	} );
}

function changeCFDISerie() {
	queryFormPost( "folioCFDI_Read", {
		async : false
	} );
}