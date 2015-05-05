AJS.$(document).ready(
		function($) {
			getPageID();

			function getPageID() {
				if (checkElement(AJS.$("#main-content"))
						&& checkElement(AJS.params.pageId)) {
					var timeSpend = $.now() - getCookie("Request-Time");
					accessRecorde(AJS.params.pageId + "-"+timeSpend);
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
			
			function getCookie(cname) {
			    var name = cname + "=";
			    var ca = document.cookie.split(';');
			    for(var i=0; i<ca.length; i++) {
			        var c = ca[i];
			        while (c.charAt(0)==' ') c = c.substring(1);
			        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
			    }
			    return "";
			}
		});