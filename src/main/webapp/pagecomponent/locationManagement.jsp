<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<script>
	var search_type_location = "searchAll";
	var search_keyWord = "";
	var search_repo_id = "";
	var selectID;

    //定义个性化命名空间
	var locationManage = {
        //初始化所属仓库选择下拉框
	   showOwnRepo : function(){
           $.ajax({
               type : "GET",
               url : "repositoryManage/getOwnRepo",
               dataType : "json",
               success : function(response) {
                   if (response.result == "success") {
                       if(response.data.length > 1){
                           var repoList = "";
                           for(var i = 0; i < response.data.length; i++){
                               repoList += '<li><a href="javascript:void(0)" class="repo-option" data-id="'+response.data[i].id+'">'+response.data[i].id+'</a></li>';
                           }
                           $("#own_repo_text").html(response.data[0].id);
                           $("#own_repo_text").parent().after('<ul class="dropdown-menu" role="menu" id="own_repo_select"></ul>');
                           $("#own_repo_select").html(repoList);
                           $(".repo-option").click(function() {
                               $("#own_repo_text").text($(this).text());
                               search_repo_id = $(this).text();
                               tableRefresh()
                           })
                       }else if(response.data.length == 1){
                           $("#own_repo_text").html(response.data[0].id);
					   }
                       search_repo_id = $("#own_repo_text").text().trim();
                       locationListInit();
                   }else{
                       showMsg("error", "查询出现异常", "");
                   }
               },
               error : function(xhr, textStatus, errorThrow) {
                   // handler error
                   handleAjaxError(xhr.status);
               }
           })
	   },
		dealLocationNO : function(){
	       if($('.del-location-no').length > 0 ){
               $("#location_no").parent().parent().nextAll().not("#location_no_group").remove();
		   }
            $('#add_location_no').unbind("click").click(function(){
                var n=$(".location-no").length;
                var addAttr = '<div class="form-group">\n' +
                    '<div class="col-md-8 col-sm-8" style="margin-left:132px">\n' +
                    '<input type="text" class="form-control location-no"\n' +
                    'name="location-no['+n+']" placeholder="货位编号">\n' +
                    '<div class="alert alert-danger hide">不能为空</div>' +
                    '</div>\n' +
                    '<button type="button" class="btn btn-xs btn-link del-location-no" style="margin-top:-25px;margin-left:398px">删除</button>\n' +
                    '</div>';
                $("#location_no_group").before(addAttr);
                $('#location_no_form').bootstrapValidator('addField', 'location-no['+n+']', {
                    validators: {
                        notEmpty: {
                            message: '编号不能为空'
                        }
                    }
                });
                //动态添加-绑定删除动作
                $('.del-location-no').click(function() {
                    var roleName = $(this).prev().find('input[type]').attr("name");
                    $("#location_no_form").bootstrapValidator('removeField',roleName);
                    $(this).parent("div").remove();
                });
            });
		}

	}

	$(function() {
		optionAction();
		searchAction();
		// locationListInit();
		bootstrapValidatorInit();

		addLocationAction();
		editLocationAction();
		deleteLocationAction();
		importLocationAction();
		exportLocationAction();

	})

	// 下拉框選擇動作
	function optionAction() {
        locationManage.showOwnRepo(); //初始化所属仓库
		$(".dropOption").click(function() {
			var type = $(this).text();
			$("#search_input").val("");
            if (type == "所有") {
                $("#search_input").attr("readOnly", "true");
                search_type_location = "searchAll";
            }else if (type == "货位编号") {
				$("#search_input").removeAttr("readOnly");
				search_type_location = "searchByNO";
			} else {
				$("#search_input").removeAttr("readOnly");
			}
			$("#search_type").text(type);
			$("#search_input").attr("placeholder", type);
		})
	}

	// 搜索动作
	function searchAction() {
		$('#search_button').click(function() {
			search_keyWord = $('#search_input').val();
            search_repo_id = $("#own_repo_text").text().trim();
			tableRefresh();
		})
	}

	// 分页查询参数
	function queryParams(params) {
		var temp = {
			limit : params.limit,
			offset : params.offset,
			searchType : search_type_location,
			keyWord : search_keyWord,
            repoID : search_repo_id
		}
		return temp;
	}

	// 表格初始化
	function locationListInit() {
		$('#locationList')
				.bootstrapTable(
						{
							columns : [
									{
										field : 'no',
										title : '编号'
									//sortable: true
									},
									{
										field : 'createTime',
										title : '创建时间',
                                        formatter: function (value, row, index) {
                                            return commonUtil.changeDateFormat(value);
                                        }
									},
									{
										field : 'updateTime',
										title : '修改时间',
                                        formatter: function (value, row, index) {
                                            return commonUtil.changeDateFormat(value);
                                        }
									},
									{
										field : 'person',
										title : '创建人'
									},
									{
										field : 'operation',
										title : '操作',
										formatter : function(value, row, index) {
											var s = '<button class="btn btn-info btn-sm edit"><span>编辑</span></button>';
											var d = '<button class="btn btn-danger btn-sm delete"><span>删除</span></button>';
											var fun = '';
											return s + ' ' + d;
										},
										events : {
											// 操作列中编辑按钮的动作
											'click .edit' : function(e, value,
													row, index) {
												selectID = row.id;
												rowEditOperation(row);
											},
											'click .delete' : function(e,
													value, row, index) {
												selectID = row.id;
												$('#deleteWarning_modal').modal(
														'show');
											}
										}
									} ],
							url : 'locationManage/getLocationList',
							onLoadError:function(status){
								handleAjaxError(status);
							},
							method : 'GET',
							queryParams : queryParams,
							sidePagination : "server",
							dataType : 'json',
							pagination : true,
							pageNumber : 1,
							pageSize : 5,
							pageList : [ 5, 10, 25, 50, 100 ],
							clickToSelect : true
						});
	}

	// 表格刷新
	function tableRefresh() {
		$('#locationList').bootstrapTable('refresh', {
			query : {}
		});
	}


	// 行编辑操作
	function rowEditOperation(row) {
		$('#edit_modal').modal("show");
		// load info
		$('#location_form_edit').bootstrapValidator("resetForm", true);
		$('#location_no_edit').val(row.no);
    }

	// 产品模态框数据校验
	function bootstrapValidatorInit() {
		$("#location_no_form,#location_form_edit").bootstrapValidator({
			message : 'This is not valid',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			excluded : [ ':disabled' ],
			fields : {
                locationNo: {
					validators : {
						notEmpty : {
							message : '编号不能为空'
						}
					}
				}
			}
		})
	}

	// 编辑货位信息
	function editLocationAction() {
        $('#edit_modal_submit').click(
            function() {
                $('#location_form_edit').data('bootstrapValidator').validate();
                if (!$('#location_form_edit').data('bootstrapValidator').isValid()) {
                    return;
                }
                var data = {
                    id : selectID,
                    no : $('#location_no_edit').val()
                }

                // ajax
                $.ajax({
                    type : "POST",
                    url : 'locationManage/updateLocation',
                    dataType : "json",
                    contentType : "application/json",
                    data : JSON.stringify(data),
                    success : function(response) {
                        $('#edit_modal').modal("hide");
                        var type;
                        var msg;
                        var append = '';
                        if (response.result == "success") {
                            type = "success";
                            msg = "货位信息更新成功";
                        } else if (resposne == "error") {
                            type = "error";
                            msg = "货位信息更新失败"
                        }
                        showMsg(type, msg, append);
                        tableRefresh();
                    },
                    error : function(xhr, textStatus, errorThrow) {
                        $('#edit_modal').modal("hide");
                        // handler error
                        handleAjaxError(xhr.status);
                    }
                });
            });
	}

	// 刪除货位信息
	function deleteLocationAction(){
		$('#delete_confirm').click(function(){
			var data = {
				"locationID" : selectID
			}
			
			// ajax
			$.ajax({
				type : "GET",
				url : "locationManage/deleteLocation",
				dataType : "json",
				contentType : "application/json",
				data : data,
				success : function(response){
					$('#deleteWarning_modal').modal("hide");
					var type;
					var msg;
					var append = '';
					if(response.result == "success"){
						type = "success";
						msg = "货位信息删除成功";
					}else{
						type = "error";
						msg = "货位信息删除失败";
					}
					showMsg(type, msg, append);
					tableRefresh();
				},error : function(xhr, textStatus, errorThrow){
					$('#deleteWarning_modal').modal("hide");
					// handler error
					handleAjaxError(xhr.status);
				}
			})
			
			$('#deleteWarning_modal').modal('hide');
		})
	}

	// 添加货位信息
	function addLocationAction() {
		$('#add_location').click(function() {
			$('#add_modal').modal("show");
            locationManage.dealLocationNO();
		});

		$('#add_modal_submit').click(function() {
		    //由于提交的type不是submit,所以需要手动校验
            $('#location_no_form').data('bootstrapValidator').validate();
            if (!$('#location_no_form').data('bootstrapValidator').isValid()) {
                return;
            }
            var locationNO = '';
            $(".location-no").each(function(i,item){
                locationNO += $(item).val().trim() + ",";
			})
			var data = {
				no : locationNO.toUpperCase(),
                repoID : $("#own_repo_text").text()
			}
			// ajax
			$.ajax({
				type : "post",
				url : "locationManage/addLocation",
				dataType : "json",
				contentType : "application/json",
				data : JSON.stringify(data),
				success : function(response) {
					$('#add_modal').modal("hide");
					var msg;
					var type;
					var append = '';
					if (response.result == "success") {
						type = "success";
						msg = "货位添加成功";
					} else if (response.result == "error") {
						type = "error";
						msg = "货位添加失败";
					}
					showMsg(type, msg, append);
					tableRefresh();
					// reset
                    $("#location_no").val("");
					$('#location_no_form').bootstrapValidator("resetForm", true);
				},
				error : function(xhr, textStatus, errorThrow) {
					$('#add_modal').modal("hide");
					// handler error
					handleAjaxError(xhr.status);
				}
			})
		});

	}

	var import_step = 1;
	var import_start = 1;
	var import_end = 3;
	// 导入货位信息
	function importLocationAction() {
		$('#import_location').click(function() {
			$('#import_modal').modal("show");
		});

		$('#previous').click(function() {
			if (import_step > import_start) {
				var preID = "step" + (import_step - 1)
				var nowID = "step" + import_step;

				$('#' + nowID).addClass("hide");
				$('#' + preID).removeClass("hide");
				import_step--;
			}
		})

		$('#next').click(function() {
			if (import_step < import_end) {
				var nowID = "step" + import_step;
				var nextID = "step" + (import_step + 1);

				$('#' + nowID).addClass("hide");
				$('#' + nextID).removeClass("hide");
				import_step++;
			}
		})

		$('#file').on("change", function() {
			$('#previous').addClass("hide");
			$('#next').addClass("hide");
			$('#submit').removeClass("hide");
		})

		$('#submit').click(function() {
			var nowID = "step" + import_end;
			$('#' + nowID).addClass("hide");
			$('#uploading').removeClass("hide");

			// next
			$('#confirm').removeClass("hide");
			$('#submit').addClass("hide");

			// ajax
			$.ajaxFileUpload({
				url : "locationManage/importLocation",
				secureuri: false,
				dataType: 'json',
				fileElementId:"file",
				data:{"repoID" : $("#own_repo_text").text()},
				success : function(data, status){
					var total = 0;
					var available = 0;
					var msg1 = "货位信息导入成功";
					var msg2 = "货位信息导入失败";
					var info;

					$('#import_progress_bar').addClass("hide");
					if(data.result == "success"){
						total = data.total;
						available = data.available;
						info = msg1;
						$('#import_success').removeClass('hide');
					}else{
						info = msg2
						$('#import_error').removeClass('hide');
					}
					info = info + ",总条数：" + total + ",有效条数:" + available;
					$('#import_result').removeClass('hide');
					$('#import_info').text(info);
					$('#confirm').removeClass('disabled');
                    tableRefresh();
				},error : function(data, status){
					// handler error
					handleAjaxError(status);
				}
			})
		})

		$('#confirm').click(function() {
			// modal dissmiss
			importModalReset();
		})
	}

	// 导出货位信息
	function exportLocationAction() {
		$('#export_location').click(function() {
			$('#export_modal').modal("show");
		})

		$('#export_location_download').click(function(){
			var data = {
                searchType : search_type_location,
                keyWord : search_keyWord,
                repoID : search_repo_id
			}
			var url = "locationManage/exportLocation?" + $.param(data)
			window.open(url, '_blank');
			$('#export_modal').modal("hide");
		})
	}

	// 导入货位模态框重置
	function importModalReset(){
		var i;
		for(i = import_start; i <= import_end; i++){
			var step = "step" + i;
			$('#' + step).removeClass("hide")
		}
		for(i = import_start; i <= import_end; i++){
			var step = "step" + i;
			$('#' + step).addClass("hide")
		}
		$('#step' + import_start).removeClass("hide");

		$('#import_progress_bar').removeClass("hide");
		$('#import_result').removeClass("hide");
		$('#import_success').removeClass('hide');
		$('#import_error').removeClass('hide');
		$('#import_progress_bar').addClass("hide");
		$('#import_result').addClass("hide");
		$('#import_success').addClass('hide');
		$('#import_error').addClass('hide');
		$('#import_info').text("");
		$('#file').val("");

		$('#previous').removeClass("hide");
		$('#next').removeClass("hide");
		$('#submit').removeClass("hide");
		$('#confirm').removeClass("hide");
		$('#submit').addClass("hide");
		$('#confirm').addClass("hide");


		$('#file').on("change", function() {
			$('#previous').addClass("hide");
			$('#next').addClass("hide");
			$('#submit').removeClass("hide");
		})
		
		import_step = 1;
	}

</script>
<div class="panel panel-default">
	<ol class="breadcrumb">
		<li>货位信息管理</li>
	</ol>
	<div class="panel-body">
		<div class="row">
			<div class="col-md-2 col-sm-2">
				<label > <span>所属仓库：</span></label>
				<div class="btn-group">
					<button class="btn btn-default dropdown-toggle"
						data-toggle="dropdown">
						<span id="own_repo_text">无所属仓库</span> <span class="caret"></span>
					</button>
				</div>
			</div>
			<div class="col-md-1 col-sm-2">
				<div class="btn-group">
					<button class="btn btn-default dropdown-toggle"
						data-toggle="dropdown">
						<span id="search_type">查询方式</span> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu" role="menu">
						<li><a href="javascript:void(0)" class="dropOption">货位编号</a></li>
						<li><a href="javascript:void(0)" class="dropOption">所有</a></li>
					</ul>
				</div>
			</div>
			<div class="col-md-9 col-sm-9">
				<div>
					<div class="col-md-3 col-sm-4">
						<input id="search_input" type="text" class="form-control"
							placeholder="货位信息查询">
					</div>
					<div class="col-md-2 col-sm-2">
						<button id="search_button" class="btn btn-success">
							<span class="glyphicon glyphicon-search"></span> <span>查询</span>
						</button>
					</div>
				</div>
			</div>
		</div>

		<div class="row" style="margin-top: 25px">
			<div class="col-md-5">
				<button class="btn btn-sm btn-default" id="add_location">
					<span class="glyphicon glyphicon-plus"></span> <span>添加货位信息</span>
				</button>
				<button class="btn btn-sm btn-default" id="import_location">
					<span class="glyphicon glyphicon-import"></span> <span>导入</span>
				</button>
				<button class="btn btn-sm btn-default" id="export_location">
					<span class="glyphicon glyphicon-export"></span> <span>导出</span>
				</button>
			</div>
			<div class="col-md-5"></div>
		</div>

		<div class="row" style="margin-top: 15px">
			<div class="col-md-12">
				<table id="locationList" class="table table-striped"></table>
			</div>
		</div>
	</div>
</div>

<!-- 添加货位信息模态框 -->
<div id="add_modal" class="modal fade" table-index="-1" role="dialog"
	aria-labelledby="addModalLabel" aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addModalLabel">添加货位信息</h4>
			</div>
			<div class="modal-body">
				<!-- 模态框的内容 -->
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-8 col-sm-8">
						<form class="form-horizontal" role="form" id="location_no_form" style="margin-top: 25px">
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货位编号：</span></label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control location-no" id="location_no"
										name="locationNo" placeholder="货位编号">
								</div>
							</div>
							<div class="form-group" id="location_no_group">
								<button type="button" class="btn btn-xs btn-link" style="margin-left:400px" id="add_location_no">添加</button>
							</div>
						</form>
					</div>
					<div class="col-md-1 col-sm-1"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal">
					<span>取消</span>
				</button>
				<button class="btn btn-success" type="button" id="add_modal_submit">
					<span>提交</span>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 编辑货位信息模态框 -->
<div id="edit_modal" class="modal fade" table-index="-1" role="dialog"
	 aria-labelledby="myModalLabel" aria-hidden="true"
	 data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">编辑货位信息</h4>
			</div>
			<div class="modal-body">
				<!-- 模态框的内容 -->
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-8 col-sm-8">
						<form class="form-horizontal" role="form" id="location_form_edit"
							  style="margin-top: 25px">
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货位编号：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="location_no_edit"
										   name="locationNo" placeholder="货位编号">
								</div>
							</div>
						</form>
					</div>
					<div class="col-md-1 col-sm-1"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal">
					<span>取消</span>
				</button>
				<button class="btn btn-success" type="button" id="edit_modal_submit">
					<span>确认更改</span>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 导入货位信息(货位编号)模态框 -->
<div class="modal fade" id="import_modal" table-index="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">导入货位信息</h4>
			</div>
			<div class="modal-body">
				<div id="step1">
					<div class="row" style="margin-top: 15px">
						<div class="col-md-1 col-sm-1"></div>
						<div class="col-md-10 col-sm-10">
							<div>
								<h4>点击下面的下载按钮，下载货位信息电子表格</h4>
							</div>
							<div style="margin-top: 30px; margin-buttom: 15px">
								<a class="btn btn-info"
									href="commons/fileSource/download/locationInfo.xlsx"
									target="_blank"> <span class="glyphicon glyphicon-download"></span>
									<span>下载</span>
								</a>
							</div>
						</div>
					</div>
				</div>
				<div id="step2" class="hide">
					<div class="row" style="margin-top: 15px">
						<div class="col-md-1 col-sm-1"></div>
						<div class="col-md-10 col-sm-10">
							<div>
								<h4>请按照货位信息电子表格中指定的格式填写需要添加的一个或多个货位信息</h4>
							</div>
							<div class="alert alert-info"
								style="margin-top: 10px; margin-buttom: 30px">
								<p>注意：</p>
								<p>1.表格中列所在的单元格均不能为空，否则该条信息将不完整，不利于维护</p>
								<p>2.注意货位编号的命名规则</p>
							</div>
						</div>
					</div>
				</div>
				<div id="step3" class="hide">
					<div class="row" style="margin-top: 15px">
						<div class="col-md-1 col-sm-1"></div>
						<div class="col-md-8 col-sm-10">
							<div>
								<div>
									<h4>请点击下面上传文件按钮，上传填写好的货位信息电子表格</h4>
								</div>
								<div style="margin-top: 30px; margin-buttom: 15px">
									<span class="btn btn-info btn-file"> <span> <span
											class="glyphicon glyphicon-upload"></span> <span>上传文件</span>
									</span> 
									<form id="import_file_upload"><input type="file" id="file" name="file"></form>
									</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="hide" id="uploading">
					<div class="row" style="margin-top: 15px" id="import_progress_bar">
						<div class="col-md-1 col-sm-1"></div>
						<div class="col-md-10 col-sm-10"
							style="margin-top: 30px; margin-bottom: 30px">
							<div class="progress progress-striped active">
								<div class="progress-bar progress-bar-success"
									role="progreessbar" aria-valuenow="60" aria-valuemin="0"
									aria-valuemax="100" style="width: 100%;">
									<span class="sr-only">请稍后...</span>
								</div>
							</div>
							<!-- 
							<div style="text-align: center">
								<h4 id="import_info"></h4>
							</div>
							 -->
						</div>
						<div class="col-md-1 col-sm-1"></div>
					</div>
					<div class="row">
						<div class="col-md-4 col-sm-4"></div>
						<div class="col-md-4 col-sm-4">
							<div id="import_result" class="hide">
								<div id="import_success" class="hide" style="text-align: center;">
									<img src="media/icons/success-icon.png" alt=""
										style="width: 100px; height: 100px;">
								</div>
								<div id="import_error" class="hide" style="text-align: center;">
									<img src="media/icons/error-icon.png" alt=""
										style="width: 100px; height: 100px;">
								</div>
							</div>
						</div>
						<div class="col-md-4 col-sm-4"></div>
					</div>
					<div class="row" style="margin-top: 10px">
						<div class="col-md-3 col-sm-3"></div>
						<div class="col-md-6 col-sm-6" style="text-align: center;">
							<h4 id="import_info"></h4>
						</div>
						<div class="col-md-3 col-sm-3"></div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn ben-default" type="button" id="previous">
					<span>上一步</span>
				</button>
				<button class="btn btn-success" type="button" id="next">
					<span>下一步</span>
				</button>
				<button class="btn btn-success hide" type="button" id="submit">
					<span>&nbsp;&nbsp;&nbsp;提交&nbsp;&nbsp;&nbsp;</span>
				</button>
				<button class="btn btn-success hide disabled" type="button"
					id="confirm" data-dismiss="modal">
					<span>&nbsp;&nbsp;&nbsp;确认&nbsp;&nbsp;&nbsp;</span>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 导出货位信息模态框 -->
<div class="modal fade" id="export_modal" table-index="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">导出货位信息</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-3 col-sm-3" style="text-align: center;">
						<img src="media/icons/warning-icon.png" alt=""
							style="width: 70px; height: 70px; margin-top: 20px;">
					</div>
					<div class="col-md-8 col-sm-8">
						<h3>是否确认导出货位信息</h3>
						<p>(注意：请确定要导出的货位信息，导出的内容为当前列表的搜索结果)</p>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal">
					<span>取消</span>
				</button>
				<button class="btn btn-success" type="button" id="export_location_download">
					<span>确认下载</span>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 删除提示模态框 -->
<div class="modal fade" id="deleteWarning_modal" table-index="-1"
	role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">警告</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-3 col-sm-3" style="text-align: center;">
						<img src="media/icons/warning-icon.png" alt=""
							style="width: 70px; height: 70px; margin-top: 20px;">
					</div>
					<div class="col-md-8 col-sm-8">
						<h3>是否确认删除该条货位信息</h3>
						<p>(注意：若该货位已经有仓库进出库记录或有仓存记录，则该货位信息将不能删除成功。如需删除该货位的信息，请先确保该货位没有关联的仓库进出库记录或有仓存记录)</p>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal">
					<span>取消</span>
				</button>
				<button class="btn btn-danger" type="button" id="delete_confirm">
					<span>确认删除</span>
				</button>
			</div>
		</div>
	</div>
</div>
