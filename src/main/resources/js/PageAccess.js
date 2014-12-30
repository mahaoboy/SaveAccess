AJS.$(document).ready(
		function($) {
			getPageID();

			function getPageID() {
				if (checkElement(AJS.$("#main-content"))
						&& checkElement(AJS.params.pageId)) {
					alert(AJS.params.pageId);
				}
			}
			function getCurrentUserName(key) {
				var status;
				AJS.$.ajax({
					url : "/rest/editperm/1.0/message/" + key,
					type : 'get',
					dataType : 'json',
					async : false,
					success : function(data) {
						status = data.value;
					}
				});
				return status;
			}

			function checkElement(elem) {
				if (typeof (elem) != undefined && typeof (elem) != null
						&& typeof (elem) != 'undefined') {
					return true;
				} else {
					return false;
				}
			}
		});