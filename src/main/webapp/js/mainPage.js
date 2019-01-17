$(function() {
	submitPasswordModify();
	menuClickAction();
	welcomePageInit();
	passwordModifyInit();
	signOut();
	homePage();
});

// 加载欢迎界面
function welcomePageInit(){
	$('#panel').load('pagecomponent/welcomePage.jsp');
}

// 跳回首页
function homePage(){
	$('.home').click(function(){
		$('#panel').load('pagecomponent/welcomePage.jsp');
	})
}


// 动作延时
var delay = (function(){
		var timer = 0;
		return function(callback, ms){
		clearTimeout (timer);
		timer = setTimeout(callback, ms);
		};
	})();


// 侧边栏连接点击动作
function menuClickAction() {
	$(".menu_item").click(function() {
		var url = $(this).attr("name");
		$('#panel .panel').mLoading('show');
		delay(function(){
			$('#panel').load(url);
		}, 500);
	})
}

// 注销登陆
function signOut() {
	$("#signOut").click(function() {
		$.ajax({
			type : "GET",
			url : "account/logout",
			dataType : "json",
			contentType : "application/json",
			success:function(response){
				window.location.reload(true);
			},error:function(response){
				
			}
		})
	})
}

// 显示操作结果提示模态框
function showMsg(type, msg, append) {
	$('#info_success').removeClass("hide");
	$('#info_error').removeClass("hide");
	if (type == "success") {
		$('#info_error').addClass("hide");
	} else if (type == "error") {
		$('#info_success').addClass("hide");
	}
	$('#info_summary').text(msg);
	$('#info_content').text(append);
	$('#global_info_modal').modal("show");
}

// 处理 Ajax 错误响应
function handleAjaxError(responseStatus){
	/*var type = 'error';
	var msg  = '';
	var append = '';
    if (responseStatus == 401) {
        msg = '权限提示';
        append = '没有对应的权限，请联系管理员！';
        showMsg(type, msg, append);
    } else if (responseStatus == 403) {
		msg = '停留时间过长';
		append = '对不起，由于您长时间未操作，请重新登陆';
		showMsg(type, msg, append);
		// 刷新重新登陆
		delay(function(){
			window.location.reload(true);
		}, 5000);
	} else if (responseStatus == 404) {
		msg = '不存在的操作';
		showMsg(type, msg, append);
	} else if (responseStatus == 430){
		msg = '您的账号在其他地方登陆';
		append = '请确认是否为您本人的操作。若否请及时更换密码';
		showMsg(type, msg, append);
		// 刷新重新登陆
		delay(function(){
			window.location.reload(true);
		}, 5000);
	}else if (responseStatus == 500) {
		msg = '服务器错误';
		append = '对不起，服务器发生了错误，我们将尽快解决，请稍候重试';
		showMsg(type, msg, append);
	} else {
		msg = '遇到未知的错误';
		showMsg(type, msg, append);
	};*/
}

// 初始密码修改
function passwordModifyInit(){
	bootstrapValidatorInit();

	// 是否弹出密码修改模态框
	isPopPasswordModal = $('#isFirstLogin').text();
	if (isPopPasswordModal == 'true') {
		$('#init_password_modify').modal('show');
	}
}

// 输入校验初始化
function bootstrapValidatorInit(){
	$('#form').bootstrapValidator({
		message:'This value is not valid',
		feedbackIcons:{
			valid:'glyphicon glyphicon-ok',
			invalid:'glyphicon glyphicon-remove',
			validating:'glyphicon glyphicon-refresh'
		},
		excluded: [':disabled'],
		fields:{// 字段验证
			oldPassword:{// 原密码
				validators:{
					notEmpty:{
						message:'输入不能为空'
					},
					callback:{}
				}
			},
			newPassword:{// 新密码
				validators:{
					notEmpty:{
						message:'输入不能为空'
					},
					stringLength:{
						min:6,
						max:16,
						message:'密码长度为6~16位'
					},
					callback:{}
				}
			},
			newPassword_re:{// 重复新密码
				validators:{
					notEmpty:{
						message:'输入不能为空'
					},
					identical:{
						field:'newPassword',
						message:'两次密码不一致'
					},
                    callback:{}
				}
			}
		}
	})
}

// 密码加密模块
function passwordEncrying(userID,password){
	var str1 = $.md5(password);
	//var str2 = $.md5(str1 + userID);
	return str1;
}

// 密码修改提交
function submitPasswordModify(){
	$('#init_password_modify_submit').click(function(event) {
        //由于提交的type不是submit,所以需要手动校验
        $('#form').data('bootstrapValidator').validate();
        if (!$('#form').data('bootstrapValidator').isValid()) {
            return;
        }
		var userID = $('#userID').html();
		var oldPassword = $('#oldPassword').val();
		var newPassword = $('#newPassword').val();
		var rePassword = $('#newPassword_re').val();

		oldPassword = passwordEncrying(userID, oldPassword);
		newPassword = passwordEncrying(userID, newPassword);
		rePassword = passwordEncrying(userID, rePassword);
		var data = {
				"oldPassword" : oldPassword,
				"newPassword" : newPassword,
				"rePassword" : rePassword
			}

		// 将数据通过 AJAX 发送到后端
		$.ajax({
			type: "POST",
			url:"account/passwordModify",
			dataType:"json",
			contentType:"application/json",
			data:JSON.stringify(data),
			success:function(response){
				// 接收并处理后端返回的响应e'd'
				if(response.result == "error"){
					var errorMessage;
					if(response.msg == "passwordError"){
						errorMessage = "密码错误";
						field = "oldPassword"
					}else if(response.msg == "passwordUnmatched"){
						errorMessage = "两次密码不一致";
						field = "newPassword_re"
					}else if(response.msg == "passwordDiffUsername"){
						errorMessage = "密码不可与用户ID相同";
						field = "newPassword"
					}

					$('#form').data('bootstrapValidator').updateMessage(field,'callback',errorMessage);
					$('#form').data('bootstrapValidator').updateStatus(field,'INVALID','callback');
				}else{
					// 否则更新成功，弹出模态框并清空表单
					$('#init_password_modify').modal('hide');
					$('#reset').trigger("click");
					$('#form').bootstrapValidator("resetForm",true); 
				}
				
			},
			error:function(xhr, textStatus, errorThrown){
				// handler error
				handleAjaxError(xhr.status);
			}
		});
	});
}

//没有仓库管理权限提示
function UnRepoAuthTip(){
	//首先判断response.data有没有数据，如果普通管理员没有，就是没有被分配仓库，要进行提示
    if($('#search_input_repository option').length == 0){
        var type = "error";
        var msg = "权限不足";
        var append = "请联系管理员给你分配仓库管理权限！" ;
        showMsg(type, msg, append);
        return true;
    }else{
    	return false;
	}
}

/**
 * 全局的ajax请求后台已经配置拦截，非ajax请求（如下载请求），后台已做处理
 */
$.ajaxSetup({
    complete:function(XMLHttpRequest,textStatus){
        var type = 'error';
        var msg  = '';
        var append = '';
        if(textStatus=="error"){
            if (XMLHttpRequest.status == 401) {
                msg = '无权限';
                append = '没有对应的权限，请联系管理员！';
                showMsg(type, msg, append);
            } else if (XMLHttpRequest.status == 403) {
                /*msg = '停留时间过长';
                append = '由于您长时间未操作，请重新登陆';
                showMsg(type, msg, append);
                // 刷新重新登陆
                delay(function(){
                    window.location.reload(true);
                }, 5000);*/
                layer.open({
                    content: '请求超时，请重新登录！',
                    btn: ['确定', '取消'],
                    yes: function(index, layero){
                        window.location.href = "/WMS";
                    },
                    btn2: function(index, layero){
                        //按钮【按钮二】的回调 //return false 开启该代码可禁止点击该按钮关闭
                    },
                    cancel: function(){
                        //右上角关闭回调 //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            } else if (XMLHttpRequest.status == 404) {
                msg = '不存在的操作';
                showMsg(type, msg, append);
            } else if (XMLHttpRequest.status == 430){
                /*msg = '您的账号在其他地方登陆';
                append = '请确认是否为您本人的操作，若否请及时更换密码';
                showMsg(type, msg, append);
                // 刷新重新登陆
                delay(function(){
                    window.location.reload(true);
                }, 5000);*/
                layer.open({
                    content: '您的账号在其他地方登陆，请确认是否为您本人操作，若否，请重新登录，及时更换密码',
                    btn: ['确定', '取消'],
                    yes: function(index, layero){
                        window.location.href = "/WMS";
                    },
                    btn2: function(index, layero){
                        //按钮【按钮二】的回调 //return false 开启该代码可禁止点击该按钮关闭
                    },
                    cancel: function(){
                        //右上角关闭回调 //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }else if (XMLHttpRequest.status == 500) {
                msg = '服务器错误';
                append = '服务器发生了错误，我们将尽快解决，请稍候重试';
                showMsg(type, msg, append);
            } else {
                msg = '遇到未知的错误';
                showMsg(type, msg, append);
            };
        }
        /*if(textStatus=="parsererror"){
            /!*$.messager.alert('提示信息', "登陆超时！请重新登陆！", 'info',function(){
                window.location.href = 'login.jsp';
            });*!/
            window.location.href = "/WMS";
        } else if(textStatus=="error"){
            if(XMLHttpRequest.status == 403){
                //layer.alert('没有对应的权限，请联系管理员', {icon: 0});
                layer.open({
                    content: '请求超时，请重新登录！',
                    btn: ['确定', '取消'],
                    yes: function(index, layero){
                        window.location.href = "/WMS";
                    },
                    btn2: function(index, layero){
                        //按钮【按钮二】的回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    },
                    cancel: function(){
                        //右上角关闭回调
                        //return false 开启该代码可禁止点击该按钮关闭
                    }
                });
            }
            //$.messager.alert('提示信息', "请求超时！请稍后再试！", 'info');
        }*/
    }
});