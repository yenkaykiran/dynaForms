dynaFormsApp.config(function($routeSegmentProvider, $routeProvider) {

    $routeSegmentProvider.options.autoLoadTemplates = true;

    $routeSegmentProvider.
    	when('/home', 'home').
    	when('/home/forms', 'home.forms').
    	segment('home', {
	        templateUrl : 'home/tmpl.html',
	    });

    $routeSegmentProvider.
	    within('home').
		    segment('default', {
		    	'default': true,
		        templateUrl : 'home/default.html'
		    }).
            segment('forms', {
                templateUrl : 'forms/tmpl.html'
            });
    
    $routeProvider.otherwise({redirectTo: '/home'}); 
});