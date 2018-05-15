runQueryApp.directive('customautocomplete', ['$compile','$timeout','$localStorage','$filter','RunQueryService',
	 function($compile,$timeout,$localStorage,$filter,RunQueryService) {
	var searchTextTimer;
	var debounceTime=700;
	function link ($scope,$index) {
		$scope.localIndex=0;
		$scope.searchparameter = 'serviceId';
		$scope.selectFromSuggestion = function(bean,$event) {
	    	 //$event.stopPropagation();
	    	if(bean) {
	    		$scope.querybean.serviceId = bean.serviceId;
	            $scope.querybean.hostname = bean.hostname;
	            $scope.querybean.username = bean.username;
	            $scope.querybean.password = bean.password;
	            $scope.querybean.env = bean.env;
	    	} 
	    	removeAll($scope);
	     };
	     
	     $scope.searchOnInput = function(input,searchparam) {
	    	 $scope.searchparameter = searchparam;
	    	 $timeout.cancel(searchTextTimer);
	    	   if($scope.searchparameter==='env' && !$scope.showSuggestionForEnv){
	    		   $scope.showSuggestionForEnv=true;
	    	   }else if($scope.searchparameter==='serviceId' && !$scope.showSuggestionForServId){
	    		   $scope.showSuggestionForServId=true;
	    	   }
        	 if(input) {
        		 searchTextTimer = $timeout(function(){
        			 fetchBeanFromLocalStorageOrService(input,$localStorage,$filter,$scope,true);
        		 },debounceTime);
        	 }else{
        		 removeAll($scope);
        	 }
         };
         
         
         $scope.keySelectFromSuggestion=function($event){
        	 if($scope.suggestions) {
        		 angular.element("ul li").removeClass('selected');
        		 if($event.keyCode==38 && $scope.localIndex>0){
        			 $scope.localIndex=$scope.localIndex-1;
        			 var bean = $scope.suggestions[$scope.localIndex];
        			 if($scope.searchparameter === 'serviceId'){
        				 $scope.querybean.serviceId = bean.serviceId;
        			 }else{
        				 $scope.querybean.env = bean.env;
        			 }
        			 
        		 }else if($event.keyCode==40 && $scope.suggestions.length-1>$scope.localIndex){
        			 $scope.localIndex=$scope.localIndex+1;
        			 var bean = $scope.suggestions[$scope.localIndex];
        			 if($scope.searchparameter === 'serviceId'){
        				 $scope.querybean.serviceId = bean.serviceId;
        			 }else{
        				 $scope.querybean.env = bean.env;
        			 }
        		 }else if($event.keyCode==13) {
        			 var bean = $scope.suggestions[$scope.localIndex];
        			 if(bean) {
        				 $scope.querybean.serviceId = bean.serviceId;
        		            $scope.querybean.hostname = bean.hostname;
        		            $scope.querybean.username = bean.username;
        		            $scope.querybean.password = bean.password;
        		            $scope.querybean.env = bean.env;
        			 }
        			 removeAll($scope);
        		 }
        	 }	 
         }
	     
         $scope.handleMouseOver = function($event,$index){
        	 $scope.localIndex=$index;
        	 angular.element("ul li").removeClass('selected');
        	 var bean = $scope.suggestions[$scope.localIndex];
			 if($scope.searchparameter === 'serviceId'){
				 $scope.querybean.serviceId = bean.serviceId;
			 }else{
				 $scope.querybean.env = bean.env;
			 }
         }
         
         function fetchBeanFromLocalStorageOrService(key,$localStorage,$filter,$scope,fromService) {
        	 
        	if(fromService) {
        		RunQueryService.sendSuggestions(key).then(function(data){        		
        			$scope.suggestions = JSON.parse(JSON.stringify(data));
        		});
        	}
        	else{
			  		   if($localStorage.localRunQueryResults) {
			  			   var list =$localStorage.localRunQueryResults;
			  			   var searchPattern=new RegExp('^'+key,'i');
			  			   if('serviceId'===$scope.searchparameter){
			  				$scope.suggestions=$filter('filter')(list,ele=>searchPattern.test(ele.serviceId));  				 
			  			   }else if('env'===$scope.searchparameter){
			  				$scope.suggestions=$filter('filter')(list,ele=>searchPattern.test(ele.env));  				 
			  			   }			   
			  		   }
         }
  	   }
        
         function removeAll($scope) {
        	 angular.element("ul li").removeClass('selected');
        	 $scope.suggestions.length=0;
        	 $scope.localIndex=0;
         }
         
        
         	 
	}
	
	return {
		templateUrl: '../directivetemplates/autocompletetemplate.html',
        link:link,    
	};
}]);
