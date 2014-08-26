(function() {
	var app = angular.module('store',[ ]);

	app.controller('StoreController', function(){
		this.product = gem;
		this.products = gems;
	});

	var gem = {
		name: 'Dodecahedron',
		price: 2.95,
		description: '. . .',
	}
	
	var gems = [gem];
})();