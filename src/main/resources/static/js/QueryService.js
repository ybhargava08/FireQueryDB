runQueryApp.service('RunQueryService',['$rootScope','$http',function($rootScope,$http){
	
	var ws;
	this.openConn = function() {
		ws = new WebSocket("ws://"+window.location.hostname+":"+window.location.port+"/queryconn");
		
		ws.onopen = function(){}
		
		ws.onmessage = function(response) {
			$rootScope.$broadcast("queryresult",response.data);
		}
	};
	
	this.send = function(querybean) {
		if (ws && ws.readyState === WebSocket.OPEN) {
			  ws.send(JSON.stringify(querybean));
		}
	};
	
	
	this.sendSuggestions = function(input){
		//var url = "/getSuggestions/"+input;
		//var config = "application/stream+json";
		return $http({
			method: "GET",
			url : "/getSuggestions/"+input,
		}).then(function(response){
					return response.data;
		}).catch(function(response){
			alert("error");
		});
	};
}]);
