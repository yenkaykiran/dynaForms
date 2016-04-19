dynaFormsApp.factory('AuthenticationService', ['$http', '$cookieStore', '$rootScope', 'AjaxService', function($http, $cookieStore, $rootScope, AjaxService) {
	'use strict';

	var service = {};

	service.Login = function(user, callback) {
		AjaxService.call('users/login', 'POST', user).success(function(data, status, headers, config) {
			callback(status, headers);
		}).error(function(data, status, headers, config) {
			callback(data);
		});
	};

	service.SetCredentials = function(username, authdata, r, fullName, staff) {
	    service.ClearCredentials();
		if (authdata != null && authdata != '' && authdata != 'null' && authdata != undefined) {
			var roles = [];
			var rolesArray = JSON.parse(r);
			for(var i = 0; i < rolesArray.length; i++) {
				roles.push(rolesArray[i].authority);
			}
			$rootScope.globals = {
                currentUser : {
                    username : username,
                    authdata : authdata,
                    roles: roles,
                    fullName: fullName,
                    staff: staff
                }
            };

            $http.defaults.headers.common['SESSION-KEY'] = authdata;
            service.updateCookie();
	        AjaxService.call('about', 'GET').success(function(data, status, headers, config) {
	            $rootScope.globals.owner = data.licenceTo;
	            service.updateCookie();
	        });
		} else{
			$rootScope.errorMessage = "Failed to Login, due to a Server Error, Please contact Administrator!";
		}
	};

	service.ClearCredentials = function() {
		$rootScope.globals = {};
		$cookieStore.remove('globals');
		$http.defaults.headers.common['SESSION-KEY'] = '';
	};
	
	service.hasAccess = function(role) {
	    var allowed = false;
	    var rolesToCheck = $rootScope.globals.currentUser.roles;
	    toBreak: 
	        for(var i = 0; i < rolesToCheck.length; i++) {
    	        for(var j = 0; j < role.length; j++) {
        	        if(rolesToCheck[i] == role[j].trim()) {
        	            allowed = true;
        	            break toBreak;
        	        }
    	        }
    	    }
		return allowed;
	};
	
	service.updateCookie = function() {
	    $cookieStore.put('globals', $rootScope.globals);
	};
	
	return service;
} ]);

dynaFormsApp.factory('AjaxService', [ '$rootScope', '$http', function($rootScope, $http) {
	
	var serverUrl = "/dynaForms-1.0/rest/";
	
    return {
        call : function(url, method, params) {
        	$rootScope.errorMessage = '';
            switch (method) {
            case 'POST':
                return $http.post(serverUrl + url, params);
            case 'GET':
                return $http.get(serverUrl + url, params);
            case 'DELETE':
            	return $http({
					url : serverUrl + url,
					method : 'DELETE',
					data : params,
					headers : {
						"Content-Type" : "application/json"
					}
				});
            default:
                break;
            }
        },
        baseUrl: function() {
        	return serverUrl;
        }
    };
} ]);

dynaFormsApp.directive('access', ['AuthenticationService', function(AuthenticationService) {
	return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var makeVisible = function () {
                    element.removeClass('hidden');
                },
                makeHidden = function () {
                    element.addClass('hidden');
                },
                determineVisibility = function (resetFirst) {
                    var result;
                    if (resetFirst) {
                        makeVisible();
                    }

                    result = AuthenticationService.hasAccess(role);
                    if (result === true) {
                        makeVisible();
                    } else {
                        makeHidden();
                    }
                },
                role = attrs.access.split(",");

            if (role != null && role.length > 0) {
                determineVisibility(true);
            }
        }
      };
} ]);
