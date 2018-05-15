var runQueryApp = angular.module("RunQueryApp",['ngStorage']);

runQueryApp.controller("RunQueryController",['$localStorage','$scope','$filter','$http','$timeout','RunQueryService',
	                       function($localStorage,$scope,$filter,$http,$timeout,RunQueryService){
	$scope.listresult = [];
	var spanShowTimer,animateEmptyTimer;
    $scope.spanShow=false;
    $scope.showPassword=false;
    $scope.showPasswordClass="fa fa-eye";
    $scope.helpText="The result will be shown in Query Results below .Meanwhile you can run more queries";
    $scope.suggestions = [];
    RunQueryService.openConn();
    $scope.showSuggestionForEnv=$scope.showSuggestionForServId =false;
    //emptyLocaLStorage($scope,$localStorage);
    //addBeans($scope);
	                 $scope.runQuery =  function($event) {
	                	 var querybean = $scope.querybean;
	                	 if(checkIfEmpty(querybean)) {
	                		 if(querybean.query.slice(-1) !== ';') {
	                			 querybean.query = querybean.query+';';
	                		 }
	                		 showSpanText($scope);
	                		 RunQueryService.send(querybean);
	                		 addRemoveBorderAnim('add','.loading','animateBorderTop');
	                	 }
	                	 
	                 };
	                 
	                 $scope.toggleShowHidePassword = function() {
	                	 $scope.showPassword = !$scope.showPassword;
	                	 if("fa fa-eye" === $scope.showPasswordClass) {
	                		 $scope.showPasswordClass = "fa fa-eye-slash";
	                	 }else{
	                		 $scope.showPasswordClass="fa fa-eye"; 
	                	 }
	                 };
	                 
	                 
	                
	                 $scope.closeAutoComplete=function($event,param){
	                		$event.stopPropagation();
		         	    	if($event.target.className==='list-group-item' || $event.target.id==='environment') {
		         	    		if('keyboard'===param && ($event.keyCode===37 || $event.keyCode===38 || $event.keyCode===39 || $event.keyCode===40)){
		         	    			
		         	    		}else{
		         	    			$scope.suggestions.length=0;
		         	    		}
		         	    	}else{
		         	    		$scope.suggestions.length=0;
		         	    	}        	    	
	         	    };
	                 
	      $scope.$on("queryresult", function(event,data) {
	    	 
	    	  var bean = JSON.parse(data);
	    	  $scope.listresult.push(bean);
	    	  $scope.listresult.sort(compare);
	    	  $scope.$apply();
	    	  addRemoveBorderAnim('remove','.loading','animateBorderTop');
	    	 // addDataToLocalStorage($scope,$localStorage,$filter,bean);
	    	  $(window).scrollTop($(document).height());
	      });           
	                 
	   function showSpanText($scope){
		   $timeout.cancel(spanShowTimer);
		   $scope.spanShow=true;
		   spanShowTimer= $timeout(function(){
			   $scope.spanShow=false;
		   },10000);
	   }
	   
	   /*function addDataToLocalStorage($scope,$localStorage,$filter,bean) {
		  if(bean && bean.resultDesc.indexOf("Error")<0){ 
		   if ($localStorage.localRunQueryResults){
			   var list = $localStorage.localRunQueryResults;
			   if(!$filter('filter')(list,{'serviceId':bean.serviceId},true)){
				   list.push(bean);
				   $localStorage.localRunQueryResults = list;
			   }
		   }else{
			  var list =[];
			   list.push(bean);
			   $localStorage.localRunQueryResults = list;
		   }
		  }
	   }*/
	   
	   	   function compare(a,b) {
		   if(a.resultTime> b.resultTime) {
			   return -1;
		   }
		   else if(a.resultTime< b.resultTime){
			   return 1;
		   }else{
			   return 0;
		   }
	   }
	   	   
	   	   function addRemoveBorderAnim(param,elem,className) {
	   		   if(param==='add' && !$(elem).hasClass(className)){ 
	   			   $(elem).addClass(className);
	   		   }else if(param==='remove' && $(elem).hasClass(className)){
	   			$(elem).removeClass(className);
	   		   }
	   	   }
	   	   
	   	   function checkIfEmpty(querybean){
	   		   if(!querybean || !querybean.env){
	   			   //$scope.helpText = "Environment is empty. Enter a value";
	   			   addAnimateEmptyClass("#environment");
	   			   return false;
	   		   }else if(!querybean.hostname){
	   			   //$scope.helpText = "Hostname is empty. Enter a value";
	   			   addAnimateEmptyClass("#hostname");
	   			   return false;
	   		   }else if(!querybean.serviceId){
	   			   //$scope.helpText = "ServiceId is empty. Enter a value";
	   			   addAnimateEmptyClass("#serviceId");
	   			   return false;
	   		   }else if(!querybean.username){
	   			   //$scope.helpText = "Username is empty. Enter a value";
	   			   addAnimateEmptyClass("#username");
	   			   return false;
	   		   }else if(!querybean.password){
	   			   //$scope.helpText = "Password is empty. Enter a value";
	   			   addAnimateEmptyClass("#pass");
	   			   return false;
	   		   }else if(!querybean.query){
	   			   //$scope.helpText = "Query Field is empty. Enter a value";
	   			   addAnimateEmptyClass("#query");
	   			   return false;
	   		   }
	   		   return true;	   		   
	   	   }
	   	   
	   	   function addAnimateEmptyClass(elem) {
	   		   $timeout.cancel(animateEmptyTimer);
	   		   $(elem).parent().addClass("animateEmpty");
	   		   animateEmptyTimer=$timeout(function(){
	   		      $(elem).parent().removeClass("animateEmpty");  
	   		   },1030);
	   	   }
	   	   
	   	   /*function emptyLocaLStorage($scope,$localStorage) {
	   		   if($localStorage.localRunQueryResults) {
	   			   var list =  $localStorage.localRunQueryResults;
	   			   list.length=0;
	   			   $localStorage.localRunQueryResults=list;
	   		   }
	   	   }*/
	   	   
	   	 /*function addBeans($scope) {
        	 var templist =[]
	   		   for(i=7;i<12;i++) {
	   			   var bean ={
	   					   serviceId: "NOF"+i,
	   			           hostname: "host"+i,
	   			           username: "user"+i,
	   			           password: "pass"+i,
	   			           env: "env"+i,
	   			   }
	   			templist.push(bean);
	   		   }
        	 if($localStorage.localRunQueryResults){
        		 $localStorage.localRunQueryResults= $localStorage.localRunQueryResults.concat(templist);
        	 }else{
        		 $localStorage.localRunQueryResults = templist;
        	 }
	   	   }*/
	   	   
 }]);
