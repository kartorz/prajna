function getErrMessage(id) {
	if (Number.isInteger(id)) {
		var messages = ["请先登录帐号", "你没有相应的操作权限", "请求失败", "数据长度超过限制"];
		return messages[id];
	}
	return "请先登录帐号";
}

function submitSiginForm(urlSignin){
	//console.log("submitSiginForm:" + urlSignin);
	//alert(urlSignin);
	$('#fm-signin').form('submit',{
		url: urlSignin,
		dataType: "text",
		onSubmit:function(){
        	return $(this).form('enableValidation').form('validate');
       	},
		success:function(data) {
			if (data == "success") {
				$('#dlg-signin').dialog('close');
				window.location.href = "";
			} else { 
				$('#fm-signin-info').text(data);
			}
	    }
	});
}

function onSignInClick(e, signin) {
	//document.getElementById("demo").style.color = "red";
	//if ($('#dlg-signin').prop("closed"))
	console.log("onSignInClick");
	$('#dlg-signin').dialog('open');
	$('#dlg-signin').dialog('vcenter');
}

function onUploadDocClick(e) {
	//document.getElementById("demo").style.color = "red";
	//if ($('#dlg-signin').prop("closed"))
	console.log("onUploadDocClick");
	$('#dlg-uploaddoc').dialog('open');
	$('#dlg-uploaddoc').dialog('vcenter');
}

function submitUploadDocForm(url) {
	var t = $('#ct-edoc').combotree('tree');
	var n = t.tree('getSelected');
	var pid = 0;
	if (n != undefined) {
		pid = n.id;
	}
	
	$('#fm-uploaddoc').form({
	    url:url,
	    onSubmit: function(param){
	    	param.pid = pid;
	    },
	    success:function(data){
	    	$('#dlg-uploaddoc').dialog('close');
	    	window.location.href = "";
	    }
	});
	$('#fm-uploaddoc').submit();
}

function setupCommtlistPagination(url) {
	$('#commtlist-pgination').pagination({
		onSelectPage:function(pageNumber, pageSize){
			var opts= $('#commtlist-pgination').pagination('options');
			$.ajax({
	            type: "GET",
	            url: url + "?did=" + opts.did + "&p=" + pageNumber + "&s=" + pageSize + "&sort=cdate,desc",
	            success: function(response) {
	                $("#comment-list").html(response);
	            }
	        });
		}
	});
}

function doCommitCommt(id, url) {
	var postData = {commtText: $("#commt-text").val(), did: id};
  	$.ajax({
  		type: "POST",
        url: url,
        data: postData,
        success: function(response) {
        	if (response.startsWith('error:')) {
        		alert(response.substr(6));
        	} else {
        		$("#comment-list").html(response);
        		$("#commt-text").val('');  	
        		if ($('#commtlist-pgination').length) {
        			var opts= $('#commtlist-pgination').pagination('options');
        			$('#commtlist-pgination').pagination('refresh',{
        				total: opts.total + 1,
        				pageNumber: 1
        			});
        		}
        	}
        },
        error: function(jqXHR, textStatus, errorThrown) {
        	alert(errorThrown);
        }
    });
}

function doDelCommentItem(id, did, url) {
	if (confirm('确认删除该评论吗?')) {
		var pageNumber = 1;
  		var total = 0;
  		if ($('#commtlist-pgination').length) {
  			var opts = $('#commtlist-pgination').pagination('options');
          	pageNumber = opts.pageNumber;
          	total = opts.total;
  		}
  		$.ajax({
  			type: "DELETE",
  	         url: url + '/' + id + "?did=" + did + "&p=" + pageNumber,
  	         success: function(response) {
  	        	if (!response.startsWith('error:')) {
  	        		$("#comment-list").html(response);
  	        		if (total > 0) {
  	        			$('#commtlist-pgination').pagination('refresh',{
  	        				total: total - 1
  	        			});
  	        		}
  	        	} else {
  	         		alert(response.substr(6));
  	        	 }
  	         }
  	    });
  	}
}

function onEditCommentItemClick(event, id) {
    if (event.stopPropagation) {
        event.stopPropagation();
    } else if(window.event) {
        window.event.cancelBubble=true;
    }
    //$(event.target).editable('show');
	$("#dc-" + id).editable('show');
	return false;
}

function onSubmitEditor(frm) {
	if ($('#editor-title').val() == '') {
		$('#qstn-editor').dialog('setTitle', "亲，忘了输入问题了");
		return false;
	}

	if ($('#editor-tags').val() == '') {
		$('#qstn-editor').dialog('setTitle', "标签不能为空");
		return false;
	}
}

/*function jqAlert( message, title ) {
    if ( !title )
        title = 'Alert';
    if ( !message )
        message = 'No Message';
  
    $('<div></div>').html( message ).dialog({
        title: title,
        resizable: false,
        modal: true,
        buttons: {
            'Ok': function()  {
                $( this ).dialog( 'close' );
            }
        }
    });
}*/

/*function popUpWindow(id, title, containerId) {
    var divObj = $('#' + id);
    divObj.dialog('destroy');
    divObj.dialog({
        autoOpen: false,
        bgiframe: true,
        modal: true,
        title: title,
        open: function (event, ui) {
            divObj.append("simple Div Tag");
            divObj.parent().appendTo($("form"));
 
        },
        close: function (event, ui) {
            divObj.parent().appendTo($('#' + containerId));
            $("body").css("overflow", "auto");
        }
    });
    divObj.dialog('open');
    $('#' + id + " input:text:visible:first").focus();
}*/
