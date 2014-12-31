AJS.$(document).ready(
		function($) {
			getPageID();

			function getPageID() {
				if (checkElement(AJS.$("#main-content"))
						&& checkElement(AJS.params.pageId)) {
					accessRecorde(AJS.params.pageId);
				}
			}
			function accessRecorde(pageid) {
				var status;
				AJS.$.ajax({
					url : "/rest/addinfo/1.0/message/" + pageid,
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