AJS.$(document).ready(function() {
    AJS.$('#fromdatepicker').datePicker();
    AJS.$('#todatepicker').datePicker();
    

});


AJS.$(function($) {

	var confluenceContextPath = AJS.params.contextPath;
	var spaceAttachmentsError = AJS.I18n.getText("PageAccess.pagination.error");

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

    		spaceAttachmentsData["fromDate"] = "";
    		spaceAttachmentsData["toDate"] = "";
    		spaceAttachmentsData["groupNameField"] = "";
    		spaceAttachmentsData["pageNumber"] = 1;
    		spaceAttachmentsData["totalItem"] = 0;

    		updateSpaceAttachments();
    		return false;
    	});

    	$("#filter-save-button", $spaceAttachments).live('click', function() {
    		var $fromdatepicker = $("input[name='fromdatepicker']", $spaceAttachments).val();
    		var $todatepicker = $("input[name='todatepicker']", $spaceAttachments).val();
    		var $groupName = $("select[name='groupName']", $spaceAttachments).val();
    		

    		filterSpaceAttachments($fromdatepicker, $todatepicker, $groupName);
    	});


    	var filterSpaceAttachments = function(fromdatepicker, todatepicker, groupName) {
    		if(checkElement(fromdatepicker)){
        		spaceAttachmentsData["fromDate"] = fromdatepicker;
    		}
    		if(checkElement(todatepicker)){
        		spaceAttachmentsData["toDate"] = todatepicker;
    		}
    		if(checkElement(groupName)){
        		spaceAttachmentsData["groupNameField"] = groupName;
    		}
    		spaceAttachmentsData["pageNumber"] = 1;
    		spaceAttachmentsData["totalItem"] = 0;

    		updateSpaceAttachments();
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

