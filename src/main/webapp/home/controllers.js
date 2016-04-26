dynaFormsApp.controller('HomeController', [ '$scope', '$rootScope', '$timeout', '$mdSidenav', 'AjaxService', '$controller', '$sce', function($scope, $rootScope, $timeout, $mdSidenav, AjaxService, $controller, $sce) {
    'use strict';
    
    $scope.app = {
    	name: "Dynamic Forms"	
    };

    $controller('BaseController', {
		$scope : $scope
	});
    
    $scope.restUrl = "forms/";
    
    $scope.nodes = {};
    $scope.modXml = '';
    
    $scope.xml2Html = function() {
    	var request = {
    		"xml" : $scope.xml
    	};
    	$scope.trustedHtml = '';
        AjaxService.call($scope.restUrl + 'nodes', 'POST', request).success(function(data, status, headers, config) {
        	$scope.nodes = data;
        	$scope.nodesToDOM();
        });
    };
    
    $scope.html2Xml = function() {
    	$scope.modXml = '';
        AjaxService.call($scope.restUrl + 'xml', 'POST', $scope.nodes).success(function(data, status, headers, config) {
        	$scope.modXml = data;
        });
    };
    
    $scope.newSubItem = function(scope) {
		var parent = scope.$parent.$parent.$modelValue;
		var nodeData = angular.copy(scope.$modelValue);
		if(!scope.$modelValue) {
			nodeData = angular.copy(parent.nodes[scope.$index]);
			parent.nodes.splice(scope.$index, 0, {
				title : nodeData.title,
				value: nodeData.value,
				container: nodeData.container, 
				attribute: nodeData.attribute,
				nodes : nodeData.nodes
			});
		} else {
			parent.splice(parent.indexOf(scope.$modelValue), 0, {
				title : nodeData.title,
				value: nodeData.value,
				container: nodeData.container, 
				attribute: nodeData.attribute,
				nodes : nodeData.nodes
			});
		}
		$scope.nodesToDOM();
	};
	
	$scope.removeSubItem = function (scope) {
		if(!scope.$modelValue) {
			var parent = scope.$parent.$parent.$modelValue;
			parent.nodes.splice(scope.$index, 1);
		} else {
			scope.remove();
		}
		$scope.nodesToDOM();
	};
    
    $scope.nodesToDOM = function(){
    	$scope.html = extractHtmlFromXml($scope.nodes);
    	$scope.trustedHtml = $sce.trustAsHtml($scope.html);
    };
    
    function extractHtmlFromXml(nodes) {
        var main = "";
        main += "<div>";
        main += "<fieldset>";
        main += "<legend>" + nodes.title + "</legend>";
        main += getContent(nodes);
        main += "</fieldset>";
        main += "</div>";
        return main;
    }

    function getContent(nodes) {
        var content = "";
        var nodeCount = nodes.nodes.length;
        for (var i = 0; i < nodeCount; i++) {
        	var node = nodes.nodes[i];
            if (node) {
                if (node.container == true) {
                    content += "<div>";
                    content += "<fieldset>";
                    content += "<legend>" + node.title + "</legend>";
                    content += getContent(node);
                    content += "</fieldset>";
                    content += "</div>";
                } else {
                	content += "<span>";
                    content += "<label>" + node.title + ": </label>";
                    content += "<input type='text' value='" + node.value + "' name='" + node.title + "' /><br/>";
                    content += getAttributesContent(node);
                    content += "</span><br/>";
                }
            }
        }
        return content;
    }
    
    function getAttributesContent(node) {
        var attributeContent = "";
        var attributes = node.nodes;
        for (var i = 0; i < attributes.length; i++) {
        	var att = attributes[i];
        	if(att.attribute == true) {
	            attributeContent += "<span>";
	            attributeContent += "<label>" + att.title + ": </label>";
	            attributeContent += "<input type='text' value='" + att.value + "' name='" + att.title + "' />";
	            attributeContent += "</span><br/>";
        	}
        }
        return attributeContent;
    }

    $scope.launch = function() {
    	if(!$scope.html || !$scope.html.trim().length > 0) {
    	    $scope.confirmDialog({
                title: 'Error',
                content: "HTML Content is Empty, Cannot Launch HTML",
                okLabel: 'OK'
            }, $event, function() { });
    	} else {
	        var html = "<html><head><title></title></head><body>" + $scope.html + "</body></html>";
			var winPrint = window.open('', '', '');
			winPrint.document.write(html);
			winPrint.document.close();
			winPrint.focus();
			$scope.cancel();
    	}
    };
    
    $scope.saveHTML = function($event) {
    	if(!$scope.xml || !$scope.xml.trim().length > 0) {
    	    $scope.confirmDialog({
                title: 'Error',
                content: "XML Content is Empty, Cannot Generate HTML",
                okLabel: 'OK'
            }, $event, function() { });
    	} else {
    		var html = "<html><head><title></title></head><body>" + $scope.html + "</body></html>";
        	var blob = new Blob([html], { type:"text/html;charset=utf-8;" });			
    		var downloadLink = angular.element('<a></a>');
    		downloadLink.attr('href',window.URL.createObjectURL(blob));
            downloadLink.attr('download', "New HTML.html");
    		downloadLink[0].click();
    	}
    };
    
} ]);

dynaFormsApp.controller('LeftController', [ '$scope', '$timeout', '$mdSidenav', function($scope, $timeout, $mdSidenav) {
	'use strict';

	$scope.close = function() {
		$mdSidenav('left').close();
	};

} ]);

dynaFormsApp.controller('DefaultController', [ '$scope', '$timeout', '$mdSidenav', 'AjaxService', '$rootScope', '$mdDialog', function($scope, $timeout, $mdSidenav, AjaxService, $rootScope, $mdDialog) {
	'use strict';

    $scope.init = function() {
    	
    };
    
    $scope.load = function() {
    	
    };
	
} ]);
