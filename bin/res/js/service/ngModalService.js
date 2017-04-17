
/*
	Creates a modal dialog and provides events for manipulating the modal.
  Simple usage: 
  ngModalService({
     controller:ModalTestController,
     template:"<p>You have recived a new messsage!: {{message}}</p>",
     showFooter:true,
   });
   function ModalTestController($scope, $modalReference){
      $scope.message = "Hi! Im a alien!";
   }
   
  made with love by @einersantanar
*/
var ngModalServiceModule = angular.module("ngModalServiceModule",[]);

ngModalServiceModule.service("ngModalService",["$document", "$rootScope","$controller", "$compile", "$http", function($document, $rootScope, $controller, $compile,$http){
  
  var defaultOptions = {
    maximize : false,
    showFooter : true,
    title : "Content",
    size: 'normal'
  };

  var NORMAL_SIZE = "normal", MAX_SIZE = "large", MINIMUN_SIZE = "mini";
      
  var utilities = {
  	/*
		centers the given view in the middle of the screen
  	*/
    centerHtml:function (html) {
        html.css("margin-left", ((html.width() * -1) / 2).toString() + "px");
    }
  };

  /*
	* Bind the scope to the modal and controller if set and append the modal to the DOM
  */
  function bind($template, bodyTemplate, scope, controller) {

          if(controller){
              $controller(controller, { $scope: scope, modalReference: $template});
          }

          $template.find(".ng-modal-body").append(bodyTemplate);
       
          $compile($template)(scope);
          $document.find("body").append($template);
          utilities.centerHtml($template);

          if(scope.modal.destroy.toString().indexOf("non-overrididable") == -1)
          {
          	throw new Error("You cannot override the method modal.destroy");
          }

     }
      

  return function ngModalService(options){
    options = angular.extend(defaultOptions, options);

    var modalTemplateString = '<div class="ng-modal">' +
                              '<div class="ng-modal-dialog">' +
                                '<div class="ng-modal-content">' +
                                  '<div class="ng-modal-header">' +
                                    '<button class="ng-modal-close" ng-click="modal.cancelPressed()" type="button" >&times;</button>' +
                                    '<span class="ng-modal-icon" id="ng-modal-icon"></span>' +
                                    '<h4 class="ng-modal-title"></h4>' +
                                  '</div>' +
                                  '<div id="body" class="ng-modal-body">' +
                                    // content here
                                   '</div>' +
                                    '<div class="ng-modal-footer">' +
                                     '<div class="ng-modal-footer-wrapper">' +
                                      '<button type="button" ng-click="modal.cancelPressed()">Cancel</button>' +
                                      '<button type="button" ng-click="modal.acceptPressed()">Continue</button>' +
                                    '</div>' +
                                    '</div>' +
                                   '</div>' +
                                '</div>' +
                            '</div>';


     var $template = angular.element(modalTemplateString);

     var scope = $rootScope.$new();
     scope.$$template = $template;

     // appling the configuration to the modal
     $template.find(".ng-modal-title").text(options.title);

     if(!options.showFooter)
        $template.find(".ng-modal-footer").hide();


     scope.modal = {};
     
    // default events  implementation    
    scope.modal.cancelPressed = function () {
      $template.fadeOut(function  () {
        $template.remove();
      });
    };

    
    scope.modal.acceptPressed = function () {
      $template.fadeOut(function  () {
        $template.remove();
      });
    };
     
     // destroy the modal from the DOM
     scope.modal.destroy = function(){
     	  // non-overrididable
        $template.fadeOut(function  () {
          $template.remove();
        });
     };


     switch(options.size)
     {
       	case MINIMUN_SIZE:
       		$template.css("width","400px");
       	break;
       	case NORMAL_SIZE:
  			 // the css already contains the normal size :)
       	break;
       	case MAX_SIZE:
       		$template.css("width","1020px");
       	break;
     }
     
     if(options.templateUrl)
     {

     	  var templateUrl = options.templateUrl;
        if(options.templateUrl.indexOf("~")){
          // append the site base url
          var basePath = location.host + location.pathname;
          templateUrl = options.templateUrl.replace("~", basePath);
        }

        var useHttpPostMethod = templateUrl.indexOf("post:") !== -1;

        if(useHttpPostMethod){
          templateUrl = templateUrl.replace("post:",'');
        }else{
          templateUrl = templateUrl.replace("get:",'');
        }

        var httpMethod = useHttpPostMethod ? $http.post : $http.get;

        httpMethod(templateUrl).then(function (response) {
            var $bodyTemplate = angular.element(response.data);
            bind($template, $bodyTemplate, scope, options.controller);
        });

     }

     else if (options.template) {
        var $bodyTemplate = angular.element(options.template);
      	$template.find("ng-modal-body").html($bodyTemplate);
        bind($template, $bodyTemplate, scope, options.controller);
     }

     if(options.getView) 
        options.getView($template, scope);
  };

}]);