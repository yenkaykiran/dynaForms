dynaFormsApp.controller('HomeController', [ '$scope', '$rootScope', '$timeout', '$mdSidenav', 'AjaxService', '$controller', '$sce', function($scope, $rootScope, $timeout, $mdSidenav, AjaxService, $controller, $sce) {
    'use strict';
    
    $scope.app = {
    	name: "Dynamic Forms"	
    };

    $controller('BaseController', {
		$scope : $scope
	});
    
    $scope.restUrl = "forms/";
    
    $scope.xml2Html = function() {
    	var request = {
    		"xml" : $scope.xml
    	};
    	$scope.trustedHtml = '';
//        AjaxService.call($scope.restUrl + 'html/', 'POST', request).success(function(data, status, headers, config) {
//        	$scope.html = data;
//        	$scope.trustedHtml = $sce.trustAsHtml(data);
//        });
        AjaxService.call($scope.restUrl + 'nodes', 'POST', request).success(function(data, status, headers, config) {
        	$scope.nodes = data;
        	$scope.html = extractHtmlFromXml($scope.nodes);
        	$scope.trustedHtml = $sce.trustAsHtml($scope.html);
        });
        
        $scope.getAtts = function(node) {
        	var atts = [];
        	if(node && node.nodes) {
	        	for(var i=0;i<node.nodes.length;i++) {
	        		if(node.nodes[i].container==false) {
	        			atts.push(node.nodes[i]);
	        		}
	        	}
        	}
        	return atts;
        };
        
        $scope.getValue = function(node) {
        	console.log(JSON.toString(node));
        	return node.value;
        };
        
        function extractHtmlFromXml(node) {
            var main = "";
            main += "<div>";
            main += "<fieldset>";
            main += "<legend>" + node.title + "</legend>";
            main += getContent(node.nodes);
            main += "</fieldset>";
            main += "</div>";
            return main;
        }

        function getContent(nodes) {
            var content = "";
            var nodeCount = nodes.length;
            for (var i = 0; i < nodeCount; i++) {
                var node = nodes[i];
                if (node && node.nodes.length > 0) {
                    if (node.container == true ) {
                        content += "<div>";
                        content += "<fieldset>";
                        content += "<legend>" + node.title + "</legend>";
                    }
                    content += getContent(node.nodes);
                    if (node.container== true) {
                        content += "</fieldset>";
                        content += "</div>";
                    }
                } else {
                	if(node.title) {
                        content += "<span>";
                        content += "<label>" + node.title + ": </label>";
                        content += "<input type='text' value='" + node.value + "' name='" + node.title + "' />";
                        content += "</span>";
                	}
                }
            }
            return content;
        }

    };
    
    $scope.add = function() {
    	console.log('Add');
    };
    
    $scope.remove = function() {
    	console.log('Remove');
    };
    
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

angular.module('dynaFormsApp').directive('xmlReader', function() {
    return {
        scope : {
            xmlReader : "=",
            postread : "&"
        },
        link : function(scope, element) {
            angular.element(element).on('change', function(changeEvent) {
                var files = changeEvent.target.files;
                if (files.length == 1) {
                    var r = new FileReader();
                    r.onload = function(e) {
                        var contents = e.target.result;
                        scope.$apply(function() {
                            scope.xmlReader = contents;
                            files[0]='';
                        });
                    };
                    r.readAsText(files[0]);
                } else {
                    alert("Please Select only one XML File to Import");
                }
            });
        }
    };
});