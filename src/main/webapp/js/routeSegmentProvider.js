dynaFormsApp.config(function($routeSegmentProvider, $routeProvider) {

    $routeSegmentProvider.options.autoLoadTemplates = true;

    $routeSegmentProvider.
    	when('/home', 'home').
    	segment('home', {
	        templateUrl : 'home/tmpl.html',
	    });

    $routeSegmentProvider.
	    within('home').
		    segment('default', {
		    	'default': true,
		        templateUrl : 'home/default.html'
		    });
    
    $routeProvider.otherwise({redirectTo: '/home'}); 
});