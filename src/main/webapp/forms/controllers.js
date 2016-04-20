dynaFormsApp.controller('FormsController', [ '$scope', '$rootScope', 'AjaxService', '$location', '$controller', function($scope, $rootScope, AjaxService, $location, $controller) {
    'use strict';
    
    $controller('BaseController', {
		$scope : $scope
	});
    
    $rootScope.pageTitle = "Forms";
    
    $scope.restUrl = "forms/";
    
    $scope.load = function() {
    	AjaxService.call($scope.restUrl, 'GET').success(function(data, status, headers, config) {
            $scope.items = data;
        });
    };
    
    $scope.init = function() {
    	$scope.load();
    };
    
    $scope.add = function(data, ev) {
	    if(!data) {
	    	data = {};
	    }
	    $rootScope.temp = {
            item : data
        };
	    $scope.openAsDialog('forms/add.html', ev, function() {
	        $scope.load();
	    });
	};
	
	$scope.fromXml = function(data, ev) {
        if(!data) {
            data = {};
        }
        $rootScope.temp = {
            item : data
        };
        $scope.openAsDialog('forms/fromXml.html', ev, function() {
            $scope.load();
        });
    };
	
	$scope.deleteItem = function(item, $event) {
		$scope.confirmDialog({
			title: 'Are you sure to delete this ?',
			content: 'Form: ' + item.name,
			okLabel: 'Delete',
			cancelLabel: 'Cancel'
		}, $event, function() {
			AjaxService.call($scope.restUrl + item.id, 'DELETE').success(function(data, status, headers, config) {
                $scope.load();
            });
		});
    };
    
} ]);

dynaFormsApp.controller('AddEditFormController', [ '$scope', '$rootScope', 'AjaxService', '$controller', function($scope, $rootScope, AjaxService, $controller) {
    'use strict';
    
    $controller('BaseController', {
		$scope : $scope
	});
    
    $scope.restUrl = "forms/";
    
    $scope.init = function() {
        $scope.searchText = "";
        $scope.attributes = [ {
							"value" : "type",
							"displayText" : "Field Type",
							"attributeValues" : ["text", "password", "date", "email", "number"]
						}, {
							"value" : "value",
							"displayText" : "Default Value",
							"attributeValues" : [ "" ]
						}, {
							"value" : "maxlength",
							"displayText" : "Maximum Length",
							"attributeValues" : "45"
						}, {
							"value" : "placeholder",
							"displayText" : "Placeholder",
							"attributeValues" : []
						} ];
    	
        $scope.item = $rootScope.temp.item;
    };
    
    $scope.addAttribute = function(field) {
		if(!field.attributes) {
			field.attributes = [];
		}
		field.attributes.push({
			name : "",
			value : ""
		});
	};
    
	$scope.addField = function(group) {
		if(!group.fields) {
			group.fields = [];
		}
		group.fields.push({
			name : "",
			attributes : [ {
				name : "",
				value : ""
			} ]
		});
	};
	
	$scope.addGroup = function(form) {
		if(!form.groups) {
			form.groups = [];
		}
		form.groups.push({
			name : "",
			fields : [ {
				name : "",
				attributes : [ {
					name : "",
					value : ""
				} ]
			} ]
		});
	};
    
    $scope.save = function() {
        AjaxService.call($scope.restUrl, 'POST', $scope.item).success(function(data, status, headers, config) {
        	$scope.item = data;
        });
    };
    
} ]);

dynaFormsApp.controller('AddEditXmlFormController', [ '$scope', '$rootScope', 'AjaxService', '$controller', function($scope, $rootScope, AjaxService, $controller) {
    'use strict';
    
    $controller('BaseController', {
        $scope : $scope
    });
    
    $scope.restUrl = "forms/";
    
    $scope.init = function() {        
        $scope.item = $rootScope.temp.item;
    };
    
    $scope.save = function() {
        AjaxService.call($scope.restUrl + '/xml', 'POST', $scope.item).success(function(data, status, headers, config) {
            $scope.item = data;
        });
    };
    
} ]);