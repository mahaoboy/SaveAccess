AJS.$(document).ready(function() {
    AJS.$('#fromdatepicker').datePicker();
    AJS.$('#todatepicker').datePicker();
    
});


AJS.$(function($) {

	var confluenceContextPath = AJS.params.contextPath;
	var spaceAttachmentsError = AJS.I18n.getText("PageAccess.pagination.error");
	var userNotExist = AJS.I18n.getText("PageAccess.pagination.userNotExist");
	$(".pageList").each(function() {
    	var $spaceAttachments = $(this);
    	var spaceAttachmentsData = {};

    	$("fieldset input[class='param']", $spaceAttachments).each(function() {
    		spaceAttachmentsData[this.name] = this.value;
		});
    	$spaceAttachments.data("spaceAttachmentsCacheData", spaceAttachmentsData);


    	$(".pageAccess", $spaceAttachments).live('click', function() {
    		var $spaceAttachmentsPage = $(this);
    		var $clickedPage = $spaceAttachmentsPage.attr("clickedPage");

    		spaceAttachmentsData["pageNumber"] = $clickedPage;

    		updateSpaceAttachments();
    		return false;
    	});

    	$(".spaceAttachmentsSortBy", $spaceAttachments).live('click', function() {
    		var $spaceAttachmentsSortBy = $(this);
    		var $sortBy = $spaceAttachmentsSortBy.attr("sortBy");

    		spaceAttachmentsData["sortBy"] = $sortBy;
    		spaceAttachmentsData["pageNumber"] = 1;

    		updateSpaceAttachments();
    		return false;
    	});

    	$("#removeFilterLink", $spaceAttachments).live('click', function() {

    		$("input[name='fromdatepicker']", $spaceAttachments).val("");
    		$("input[name='todatepicker']", $spaceAttachments).val("");
    		$("select[name='groupName']", $spaceAttachments).val("");
    		$("select[name='spaceName']", $spaceAttachments).val("");
    		$("input[name='userNameField']", $spaceAttachments).val("");
    		$("input[name='pageTitleField']", $spaceAttachments).val("");
    		
    		spaceAttachmentsData["fromDate"] = "";
    		spaceAttachmentsData["toDate"] = "";
    		spaceAttachmentsData["groupNameField"] = "";
    		spaceAttachmentsData["spaceNameField"] = "";
    		spaceAttachmentsData["userNameField"] = "";
    		spaceAttachmentsData["pageTitleField"] = "";
    		spaceAttachmentsData["pageNumber"] = 1;
    		spaceAttachmentsData["totalItem"] = 0;

    		updateSpaceAttachments();
    		return false;
    	});

    	$("#filter-save-button", $spaceAttachments).live('click', function() {
    		var $fromdatepicker = $("input[name='fromdatepicker']", $spaceAttachments).val();
    		var $todatepicker = $("input[name='todatepicker']", $spaceAttachments).val();
    		var $groupName = $("select[name='groupName']", $spaceAttachments).val();
    		var $spaceName = $("select[name='spaceName']", $spaceAttachments).val();
    		var $userNameField = $("input[name='userNameField']", $spaceAttachments).val();
    		var $pageTitleField = $("input[name='pageTitleField']", $spaceAttachments).val();

    		filterSpaceAttachments($fromdatepicker, $todatepicker, $groupName, $spaceName, $userNameField, $pageTitleField);
    	});


    	var filterSpaceAttachments = function(fromdatepicker, todatepicker, groupName, spaceName, userNameField, pageTitleField) {
    		if(checkElement(fromdatepicker)){
        		spaceAttachmentsData["fromDate"] = fromdatepicker;
    		}
    		if(checkElement(todatepicker)){
        		spaceAttachmentsData["toDate"] = todatepicker;
    		}
    		if(checkElement(groupName)){
        		spaceAttachmentsData["groupNameField"] = groupName;
    		}
    		if(checkElement(spaceName)){
        		spaceAttachmentsData["spaceNameField"] = encodeURIComponent(spaceName);
    		}
    		if(checkElement(userNameField)){
        		spaceAttachmentsData["userNameField"] = userNameField;
    		}
    		if(checkElement(pageTitleField)){
        		spaceAttachmentsData["pageTitleField"] = encodeURIComponent(pageTitleField);
    		}
    		spaceAttachmentsData["pageNumber"] = 1;
    		spaceAttachmentsData["totalItem"] = 0;

    		if(userNameField.length > 0){
    			checkUserExist(userNameField);
    		}else{
    			updateSpaceAttachments();
    		}
    		
    		return false;
    	};

    	var updateSpaceAttachments = function() {
    		
    		$.ajax({
    			cache: false,
    			data : $spaceAttachments.data("spaceAttachmentsCacheData"),
    			dataType : "html",
    			success : function(serverGeneratedHtml) {
	    			var $finalOutput = $(serverGeneratedHtml);
	    			if($finalOutput.find(".attachments-container").length > 0){
	    				$(".attachments-container", $spaceAttachments).replaceWith($finalOutput.find(".attachments-container"));
	    			    AJS.$('#fromdatepicker').datePicker();
	    			    AJS.$('#todatepicker').datePicker();

	    			    AJS.Confluence.Binder.autocompletePage($("input[name='pageTitleField']").parent());
	    			    AJS.Confluence.Binder.autocompleteUser($("input[name='userNameField']").parent());
	    			    
	    			    $(document).scrollTop( $("#SearchResult").offset().top );
	    			}else{
	    				location.reload();
	    			}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {

					AJS.log("Error retrieving data: " + errorThrown);
					$(".attachments-container", $spaceAttachments).remove();
					$spaceAttachments.append('<div class="space-attachments-error error">'+ spaceAttachmentsError +'</div>');
				},

	            type : "GET",
	            url : confluenceContextPath + "/plugins/servlet/winagile/pageaccessstate"
	    	});
    	};

    	var checkUserExist = function(username){
        	var userquery = {};
        	userquery["query"] = username;
        	$.ajax({
    			cache: false,
    			data : userquery,
    			dataType : "json",
    			success : function(serverGeneratedHtml) {
        			
        			if(serverGeneratedHtml.totalSize > 0){
        				updateSpaceAttachments();
        				return false;
        			}else{
        				alert(userNotExist);
        				return false;
        			}
        			
    			},
    			error : function(XMLHttpRequest, textStatus, errorThrown) {
    				AJS.log("Error retrieving data: " + errorThrown);
    				alert(spaceAttachmentsError);
    				location.reload();
    				return false;
    			},

                type : "GET",
                url : confluenceContextPath + "/rest/prototype/1/search/user"
        	});
        };

	});
	function checkElement(elem) {
		if (typeof (elem) != undefined && typeof (elem) != null
				&& typeof (elem) != 'undefined') {
			return true;
		} else {
			return false;
		}
	}
	
	
});

