var Rules = {
	'.fac_nombre:keyup': function(e) {
		if (e.value.length === 0) {
			e.parentNode.appendChild(document.createElement("<label class=\"error\">Proporcione el nombre a quien se debe facturar</label>"));
		}
	}
/*  
	'#icons a:mouseover': function(element) {
		var app = element.id;
		new Effect.BlindDown(app + '-content', {queue: 'end', duration: 0.2});
	},
	
	'#icons a:mouseout': function(element) {
		var app = element.id;
		new Effect.BlindUp(app + '-content', {queue: 'end', duration: 0.2});
	},
	
	'#features:mouseover': function(element) {
		//alert('wee mouse');
	},
	
	'#features': function(element) {
		Sortable.create(element);
	},
	
	'#features li:click': function(element, event) {
		new Ajax.Updater('features', 'item.html', {
			asynchronous:true, 
			method: 'get', 
			evalScripts: true, 
			insertion: Insertion.Bottom
		});
	}
*/
}
