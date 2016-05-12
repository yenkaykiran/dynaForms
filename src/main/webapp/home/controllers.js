var fileselected; 

dynaFormsApp.controller('HomeController', [ '$scope', '$rootScope', 'AjaxService', '$sce', function($scope, $rootScope, AjaxService, $sce) {
    'use strict';
    
    $scope.app = {
        name: "Dynamic Forms"   
    };

    $scope.restUrl = "forms/";
    
    $scope.nodes = {};
    $scope.modXml = '';
    $scope.status = 'Started';
    
    fileselected = function(changeEvent) {
        var files = changeEvent.target.files;
        if (files.length == 1) {
            var r = new FileReader();
            r.onload = function(e) {
                var contents = r.result;
                $scope.xml = contents;
            };
            r.readAsText(files[0]);
        } else {
            alert("Please Select only one XML File to Import");
        }
    };
    
    AjaxService.call($scope.restUrl + 'attributes', 'GET').success(function(data, status, headers, config) {
        $scope.attributes = data;
    });
    
    $scope.xml2Html = function() {
        var request = {
            "xml" : $scope.xml
        };
        $scope.trustedHtml = '';
        AjaxService.call($scope.restUrl + 'nodes', 'POST', request).success(function(data, status, headers, config) {
            $scope.nodes = data;
            $scope.nodesToDOM();
        }).error(function(data, status, headers, config) {
            alert(headers('errorMessage'));
        });
    };
    
    $scope.html2Xml = function() {
        $scope.modXml = '';
        AjaxService.call($scope.restUrl + 'xml', 'POST', $scope.nodes).success(function(data, status, headers, config) {
            $scope.modXml = data;
        }).error(function(data, status, headers, config) {
        	alert(headers('errorMessage'));
        });
    };
    
    $scope.newSubItem = function(scope) {
        var parent = scope.$parent.$parent.$modelValue;
        var nodeData = angular.copy(scope.$modelValue);
        
        try {
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
        } catch (e) {
			alert(e);
		}
        $scope.nodesToDOM();
    };
    
    $scope.removeSubItem = function (scope) {
    	try {
    		if(!scope.$modelValue) {
                var parent = scope.$parent.$parent.$modelValue;
                parent.nodes.splice(scope.$index, 1);
            } else {
                scope.remove();
            }
		} catch (e) {
			alert(e);
		}
        $scope.nodesToDOM();
    };
    
    $scope.nodesToDOM = function(){
        $scope.html = extractHtmlFromXml($scope.nodes);
        $scope.trustedHtml = $sce.trustAsHtml($scope.html);
    };
    
    function extractHtmlFromXml(nodes) {
        $scope.status = 'Started';
        console.time("XML2HTML");
        var main = "";
        main += "<div>";
        main += "<fieldset>";
        main += "<legend>" + nodes.title + "</legend>";
        main += getContent(nodes);
        main += "</fieldset>";
        main += "</div>";
        console.timeEnd("XML2HTML");
        $scope.status = 'Finished';
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
        	alert("HTML Content is Empty, Cannot Launch HTML");
        } else {
            var html = "<html><head><title></title></head><body>" + $scope.html + "</body></html>";
            var winPrint = window.open('', '', '');
            winPrint.document.write(html);
            winPrint.document.close();
            winPrint.focus();
        }
    };
    
    $scope.saveHTML = function($event) {
        if(!$scope.xml || !$scope.xml.trim().length > 0) {
        	alert("XML Content is Empty, Cannot Generate HTML");
        } else {
            var html = "<html><head><title></title></head><body>" + $scope.html + "</body></html>";
            var blob = new Blob([html], { type:"text/html;charset=utf-8;" });           
            var downloadLink = angular.element('<a></a>');
            downloadLink.attr('href',window.URL.createObjectURL(blob));
            downloadLink.attr('download', "New HTML.html");
            downloadLink[0].click();
        }
    };
    
    $scope.exportXml = function($event) {
        var blob = new Blob([$scope.modXml], { type:"text/xml;charset=utf-8;" });           
        var downloadLink = angular.element('<a></a>');
        downloadLink.attr('href',window.URL.createObjectURL(blob));
        downloadLink.attr('download', "New XML.xml");
        downloadLink[0].click();
    };
    
} ]);

dynaFormsApp.factory('AjaxService', [ '$rootScope', '$http', function($rootScope, $http) {
	
	var serverUrl = "/dynaForms/rest/";
	
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
