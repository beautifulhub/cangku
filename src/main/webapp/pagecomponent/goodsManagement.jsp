<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<script>
	var search_type_goods = "searchAll";
	var search_keyWord = "";
	var selectID;

    layui.use('upload', function () {
        var upload = layui.upload;
        var reloadHtml = '<span style="color: #FF5722; margin-left:150px">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>';
        var goodsPicUpload = upload.render({
            elem: ".goods_pic_button",
            accept: 'images',
            exts: 'jpg|png|jpeg',
            data: {
                /*folderPath: focusBucketName,
                width: titlePicWidth,
                height: titlePicHeight,*/
                maxFileSize: 1024//Kb
            },
            url: "${pageContext.request.contextPath}/upload/uploadImage.do",
            before: function (obj) {
                //预读本地文件示例，不支持ie8
                /*obj.preview(function(index, file, result){
                    $('#goods_pic').attr('src', result); //图片链接（base64）
                });*/
                layer.load(1, {
                    shade: [0.1,'#fff'] //0.1透明度的白色背景
                });
            },
            //如果上传失败
        done: function (res) {
            layer.closeAll('loading');
            var item = this.item;
                if(res.code != 1){
                    $(item).next().next().children(":first").after(reloadHtml);
                    $('.demo-reload').on('click', function(){
                        goodsPicUpload.upload();
                    });
                }else{
                    $(item).next().next().children(":first").attr('src', res.data.url);
					return layer.msg('上传成功');
                }
            },
            error: function () {
                layer.closeAll('loading');
                var item = this.item;
                $(item).next().next().children(":first").after(reloadHtml);
                $('.demo-reload').on('click', function(){
                    goodsPicUpload.upload();
                });
            }
        });
    });

    //定义个性化命名空间
	var goodsManage = {

        //初始化货物类型选择下拉框
	   showSelectType : function(typeDealType){
           $.ajax({
               type : "GET",
               url : "goodsManage/getGoodsType",
               dataType : "json",
               success : function(response) {
                   if (response.result == "success") {
                       var menuList = "";
                       if(typeDealType == 'edit'){
                           for(var i = 0; i < response.data.length; i++){
                               menuList += '<li><a href="javascript:void(0)" class="typeOptionEdit" data-id="'+response.data[i].id+'">'+response.data[i].name+'</a></li>';
                           }
                           $("#type_select_value_edit").html(menuList);
                           $(".typeOptionEdit").click(function() {
                               var goodsType = $(this).text();
                               $("#type_select_edit").text(goodsType);
                               $("#goods_type_edit").val($(this).attr("data-id")).change();
                           })
					   }else if(typeDealType == 'add'){
                           for(var i = 0; i < response.data.length; i++){
                               menuList += '<li><a href="javascript:void(0)" class="typeOption" data-id="'+response.data[i].id+'">'+response.data[i].name+'</a></li>';
                           }
                           $("#type_select_value").html(menuList);
                           $(".typeOption").click(function() {
                               var goodsType = $(this).text();
                               $("#type_select").text(goodsType);
                               $("#goods_type").val($(this).attr("data-id")).change();
                           })
                       }
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

		//处理货物尺码
		dealGoodsSize : function(sizeDealType){
			/**
			 * 单独处理子尺码
			 */
			$('#'+sizeDealType).click(function() {
				//初始化尺码
				$('#goods_sizes_form').bootstrapValidator("resetForm", true);
				var goodssizes = $("#"+sizeDealType).val().replace(/，/ig,',');
                goodssizes = goodssizes.replace(/，/ig,',');
                var sizesArr = goodssizes.split(",");
				$(".goods_size:eq(0)").val(sizesArr[0]);
				for(var i=1 ; i<sizesArr.length; i++){
					if($(".goods_size:eq("+i+")").length == 0){
						var n=$(".goods_size").length;
						var addAttr = '<div class="form-group">\n' +
							'<div class="col-md-8 col-sm-8">\n' +
							'<input type="text" class="form-control goods_size"\n' +
							'name="goods_size['+n+']" placeholder="请输入尺码">\n' +
							'<div class="alert alert-danger hide">不能为空</div>' +
							'</div>\n' +
							'<button type="button" class="btn btn-xs btn-link del_size" style="margin-top:7px">删除</button>\n' +
							'</div>';
						$("#goods_size_group").before(addAttr);
						$('#goods_sizes_form').bootstrapValidator('addField', 'goods_size['+n+']', {
							validators: {
								notEmpty: {
									message: '尺码不能为空'
								}
							}
						});
						//动态添加-绑定删除动作
						$('.del_size').click(function() {
							var roleName = $(this).prev().find('input[type]').attr("name");
							$("#goods_sizes_form").bootstrapValidator('removeField',roleName);
							$(this).parent("div").remove();
						});
					}
					$(".goods_size:eq("+i+")").val(sizesArr[i]);
				}
				$(".goods_size:gt(0)").each(function(i,item){
					if($(item).val() == ''){
						var roleName = $(item).attr("name");
						$("#goods_sizes_form").bootstrapValidator('removeField',roleName);
						$(this).parent("div").parent("div").remove();
					}
				})
				$('#goods_sizes_modal').modal("show");
			});

			//绑定添加子尺码
			$('#add_size').unbind("click").click(function(){
				var n=$(".goods_size").length;
				var addAttr = '<div class="form-group">\n' +
					'<div class="col-md-8 col-sm-8">\n' +
					'<input type="text" class="form-control goods_size"\n' +
					'name="goods_size['+n+']" placeholder="请输入尺码">\n' +
					'<div class="alert alert-danger hide">不能为空</div>' +
					'</div>\n' +
					'<button type="button" class="btn btn-xs btn-link del_size" style="margin-top:7px">删除</button>\n' +
					'</div>';
				$("#goods_size_group").before(addAttr);
				$('#goods_sizes_form').bootstrapValidator('addField', 'goods_size['+n+']', {
					validators: {
						notEmpty: {
							message: '尺码不能为空'
						}
					}
				});
				//动态添加-绑定删除动作
				$('.del_size').click(function() {
					var roleName = $(this).prev().find('input[type]').attr("name");
					$("#goods_sizes_form").bootstrapValidator('removeField',roleName);
					$(this).parent("div").remove();
				});
			});
        /**
         * 处理子尺码提交到总表单
         */
        $('#goods_size_submit').unbind("click").click(function() {
                $('#goods_sizes_form').data('bootstrapValidator').validate();
                if (!$('#goods_sizes_form').data('bootstrapValidator').isValid()) {
                    return;
                }
                var goodssizes = $(".goods_size");
                var goodssize = '';
                for(var i=0 ;i<goodssizes.length-1; i++){
                    goodssize += goodssizes[i].value.trim().toUpperCase() + "，";
                }
                goodssize += goodssizes[goodssizes.length-1].value.trim().toUpperCase();
                $("#"+sizeDealType).val(goodssize).change();
                $('#goods_sizes_modal').modal("hide");
            });
	    },
		//单独处理颜色
		dealGoodsColor : function(colorDealType) {
            /**
             * 单独添加子颜色
             */
            $("#"+colorDealType).click(function() {
                //初始化颜色
                $('#goods_colors_form').bootstrapValidator("resetForm", true);
                var goodsColors = $("#"+colorDealType).val();
                goodsColors = goodsColors.replace(/，/ig,','); //统一将中英文逗号处理成英文
                var colorsArr = goodsColors.split(",");
                $(".goods_color:eq(0)").val(colorsArr[0]);
                for(var i=1 ; i<colorsArr.length; i++){
                    if($(".goods_color:eq("+i+")").length == 0){
                        var n=$(".goods_color").length;
                        var addAttr = '<div class="form-group">\n' +
                            '<div class="col-md-8 col-sm-8">\n' +
                            '<input type="text" class="form-control goods_color"\n' +
                            'name="goods_color['+n+']" placeholder="请输入颜色">\n' +
                            '<div class="alert alert-danger hide">不能为空</div>' +
                            '</div>\n' +
                            '<button type="button" class="btn btn-xs btn-link del_color" style="margin-top:7px">删除</button>\n' +
                            '</div>';
                        $("#goods_color_group").before(addAttr);
                        $('#goods_colors_form').bootstrapValidator('addField', 'goods_color['+n+']', {
                            validators: {
                                notEmpty: {
                                    message: '颜色不能为空'
                                }
                            }
                        });
                        //动态添加-绑定删除动作
                        $('.del_color').click(function() {
                            var roleName = $(this).prev().find('input[type]').attr("name");
                            $("#goods_colors_form").bootstrapValidator('removeField',roleName);
                            $(this).parent("div").remove();
                        });
                    }
                    $(".goods_color:eq("+i+")").val(colorsArr[i]);
                }
                $(".goods_color:gt(0)").each(function(i,item){
                    if($(item).val() == ''){
                        var roleName = $(item).attr("name");
                        $("#goods_colors_form").bootstrapValidator('removeField',roleName);
                        $(this).parent("div").parent("div").remove();
                    }
                })
                $('#goods_colors_modal').modal("show");
            });
            /**
             * 绑定添加子颜色
             */
            $('#add_color').unbind("click").click(function(){
                var n=$(".goods_color").length;
                var addAttr = '<div class="form-group">\n' +
                    '<div class="col-md-8 col-sm-8">\n' +
                    '<input type="text" class="form-control goods_color"\n' +
                    'name="goods_color['+n+']" placeholder="请输入颜色">\n' +
                    '<div class="alert alert-danger hide">不能为空</div>' +
                    '</div>\n' +
                    '<button type="button" class="btn btn-xs btn-link del_color" style="margin-top:7px">删除</button>\n' +
                    '</div>';
                $("#goods_color_group").before(addAttr);
                $('#goods_colors_form').bootstrapValidator('addField', 'goods_color['+n+']', {
                    validators: {
                        notEmpty: {
                            message: '颜色不能为空'
                        }
                    }
                });
                //动态添加-绑定删除动作
                $('.del_color').click(function() {
                    var roleName = $(this).prev().find('input[type]').attr("name");
                    $("#goods_colors_form").bootstrapValidator('removeField',roleName);
                    $(this).parent("div").remove();
                });
            });
            /**
             * 子颜色提交到总表单
             */
            $('#goods_color_submit').unbind("click").click(function() {
                    $('#goods_colors_form').data('bootstrapValidator').validate();
                    if (!$('#goods_colors_form').data('bootstrapValidator').isValid()) {
                        return;
                    }
                    var goodsColors = $(".goods_color");
                    var goodsColor = '';
                    for(var i=0 ;i<goodsColors.length-1; i++){
                        goodsColor += goodsColors[i].value.trim() + "，";
                    }
                    goodsColor += goodsColors[goodsColors.length-1].value.trim();
                    $("#"+colorDealType).val(goodsColor).change();
                    $('#goods_colors_modal').modal("hide");
                });
		}

	}

	$(function() {
		optionAction();
		searchAction();
		goodsListInit();
		bootstrapValidatorInit();

		addGoodsAction();
		editGoodsAction();
		deleteGoodsAction();
		importGoodsAction();
		exportGoodsAction();

	})

	// 下拉框選擇動作
	function optionAction() {
		$(".dropOption").click(function() {
			var type = $(this).text();
			$("#search_input").val("");
			if (type == "所有") {
				$("#search_input").attr("readOnly", "true");
				search_type_goods = "searchAll";
			} else if (type == "货物编号") {
				$("#search_input").removeAttr("readOnly");
				search_type_goods = "searchByNO";
			} else if (type == "货物名称") {
				$("#search_input").removeAttr("readOnly");
				search_type_goods = "searchByName";
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
            $('#goodsList').bootstrapTable('selectPage',1);
		})
	}

	// 分页查询参数
	function queryParams(params) {
		var temp = {
			limit : params.limit,
			offset : params.offset,
			searchType : search_type_goods,
			keyWord : search_keyWord
		}
		return temp;
	}

	// 表格初始化
	function goodsListInit() {
		$('#goodsList')
				.bootstrapTable(
						{
							columns : [
									{
										field : 'id',
										title : '货物ID',
										visible : false
									},
									{
										field : 'no',
										title : '编号'
									//sortable: true
									},
									{
										field : 'name',
										title : '名称'
									},
									{
										field : 'type',
										title : '类型'
									},
									{
										field : 'sizes',
										title : '尺码'
									},
									{
										field : 'colors',
										title : '颜色'
									},
									{
										field : 'pic',
										title : '图片',
										visible : false
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
							url : 'goodsManage/getGoodsList',
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
		$('#goodsList').bootstrapTable('refresh', {
			query : {}
		});
	}


	// 行编辑操作
	function rowEditOperation(row) {
		$('#edit_modal').modal("show");
		// load info
		$('#goods_form_edit').bootstrapValidator("resetForm", true);
		$('#goods_no_edit').val(row.no);
		$('#goods_name_edit').val(row.name);
        $("#type_select_edit").text(row.type);
		$('#goods_sizes_edit').val(row.sizes);
		$('#goods_colors_edit').val(row.colors);
		if(row.pic != ""){
            $('#goods_pic_edit').attr("src",row.pic);
        }
		goodsManage.showSelectType("edit");
        goodsManage.dealGoodsSize("goods_sizes_edit");
        goodsManage.dealGoodsColor("goods_colors_edit");

    }

	// 添加产品模态框数据校验
	function bootstrapValidatorInit() {
		$("#goods_form").bootstrapValidator({
			message : 'This is not valid',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			excluded : [ ':disabled' ],
			fields : {
                goods_no : {
					validators : {
						notEmpty : {
							message : '货物编号不能为空'
						}
					}
				},
                goods_name : {
					validators : {
						notEmpty : {
							message : '货物名称不能为空'
						}
					}
				},
                goods_type : {
                    trigger:"change",
                    validators : {
                        notEmpty : {
                            message : '货物类型不能为空'
                        }
                    }
                },
                goods_sizes : {
                    trigger:"change",
                    validators : {
                        notEmpty : {
                            message : '货物尺码不能为空'
                        }
                    }
                },
                goods_colors : {
                    trigger:"change",
					validators : {
						notEmpty : {
							message : '货物颜色不能为空'
						}
					}
				}/*,
				goods_pic : {
					validators : {
						notEmpty : {
							message : '货物实物图不能为空'
						}
					}
				}*/
			}
		})
		$("#goods_form_edit").bootstrapValidator({
			message : 'This is not valid',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			excluded : [ ':disabled' ],
			fields : {
                goods_no : {
					validators : {
						notEmpty : {
							message : '货物编号不能为空'
						}
					}
				},
                goods_name : {
					validators : {
						notEmpty : {
							message : '货物名称不能为空'
						}
					}
				}
			}
		})
        $('#goods_colors_form').bootstrapValidator({
            message : 'This is not valid',
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            excluded : [ ':disabled' ],
            fields : {
                'goods_color[0]': {
                    validators : {
                        notEmpty : {
                            message : '颜色不能为空'
                        }
                    }
                },
            }
        })
        $('#goods_sizes_form').bootstrapValidator({
            message : 'This is not valid',
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            excluded : [ ':disabled' ],
            fields : {
                'goods_size[0]': {
                    validators : {
                        notEmpty : {
                            message : '尺码不能为空'
                        }
                    }
                },
            }
        })
	}

	// 编辑货物信息
	function editGoodsAction() {
        $('#edit_modal_submit').click(
            function() {
                $('#goods_form_edit').data('bootstrapValidator').validate();
                if (!$('#goods_form_edit').data('bootstrapValidator').isValid()) {
                    return;
                }
                var data = {
                    id : selectID,
                    no : $('#goods_no_edit').val(),
                    name : $('#goods_name_edit').val(),
                    type : $('#goods_type_edit').val(),
                    sizes : $('#goods_sizes_edit').val(),
                    colors : $('#goods_colors_edit').val(),
                    pic : $('#goods_pic_edit').attr("src") == undefined ? "" : $('#goods_pic_edit').attr("src").trim(),
                }

                // ajax
                $.ajax({
                    type : "POST",
                    url : 'goodsManage/updateGoods',
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
                            msg = "货物信息更新成功";
                        } else if (resposne == "error") {
                            type = "error";
                            msg = "货物信息更新失败"
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

	// 刪除货物信息
	function deleteGoodsAction(){
		$('#delete_confirm').click(function(){
			var data = {
				"goodsID" : selectID
			}
			
			// ajax
			$.ajax({
				type : "GET",
				url : "goodsManage/deleteGoods",
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
						msg = "货物信息删除成功";
					}else{
						type = "error";
						msg = "货物信息删除失败";
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

	// 添加货物信息
	function addGoodsAction() {
		$('#add_goods').click(function() {
			$('#add_modal').modal("show");
			goodsManage.showSelectType("add");
		});

		$('#add_modal_submit').click(function() {
		    //由于提交的type不是submit,所以需要手动校验
            $('#goods_form').data('bootstrapValidator').validate();
            if (!$('#goods_form').data('bootstrapValidator').isValid()) {
                return;
            }
			var data = {
				no : $('#goods_no').val(),
				name : $('#goods_name').val(),
				type : $('#goods_type').val(),
				sizes : $('#goods_sizes').val(),
                colors : $('#goods_colors').val(),
                pic : $('#goods_pic').attr("src") == undefined ? "" : $('#goods_pic').attr("src").trim(),
			}
			// ajax
			$.ajax({
				type : "POST",
				url : "goodsManage/addGoods",
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
						msg = "货物添加成功";
					} else if (response.result == "error") {
						type = "error";
						msg = "货物添加失败";
					}
					showMsg(type, msg, append);
					tableRefresh();
					// reset
					$('#goods_no').val("");
					$('#goods_name').val("");
					$('#goods_type').val("");
                    $("#type_select").text("类型选择");
					$('#goods_sizes').val("");
					$('#goods_colors').val("");
					$('#goods_pic').val("");
					$('#goods_form').bootstrapValidator("resetForm", true);
				},
				error : function(xhr, textStatus, errorThrow) {
					$('#add_modal').modal("hide");
					// handler error
					handleAjaxError(xhr.status);
				}
			})
		});
		goodsManage.dealGoodsSize("goods_sizes");
        goodsManage.dealGoodsColor("goods_colors");

	}

	var import_step = 1;
	var import_start = 1;
	var import_end = 3;
	// 导入货物信息
	function importGoodsAction() {
		$('#import_goods').click(function() {
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
				url : "goodsManage/importGoods",
				secureuri: false,
				dataType: 'json',
				fileElementId:"file",
				success : function(data, status){
					var total = 0;
					var available = 0;
					var msg1 = "货物信息导入成功";
					var msg2 = "货物信息导入失败";
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

	// 导出货物信息
	function exportGoodsAction() {
		$('#export_goods').click(function() {
			$('#export_modal').modal("show");
		})

		$('#export_goods_download').click(function(){
			var data = {
				searchType : search_type_goods,
				keyWord : search_keyWord
			}
			var url = "goodsManage/exportGoods?" + $.param(data)
			window.open(url, '_blank');
			$('#export_modal').modal("hide");
		})
	}

	// 导入货物模态框重置
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
		<li>货物信息管理</li>
	</ol>
	<div class="panel-body">
		<div class="row">
			<div class="col-md-1 col-sm-2">
				<div class="btn-group">
					<button class="btn btn-default dropdown-toggle"
						data-toggle="dropdown">
						<span id="search_type">查询方式</span> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu" role="menu">
						<li><a href="javascript:void(0)" class="dropOption">货物编号</a></li>
						<li><a href="javascript:void(0)" class="dropOption">货物名称</a></li>
						<li><a href="javascript:void(0)" class="dropOption">所有</a></li>
					</ul>
				</div>
			</div>
			<div class="col-md-9 col-sm-9">
				<div>
					<div class="col-md-3 col-sm-4">
						<input id="search_input" type="text" class="form-control"
							placeholder="货物信息查询">
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
				<button class="btn btn-sm btn-default" id="add_goods">
					<span class="glyphicon glyphicon-plus"></span> <span>添加货物信息</span>
				</button>
				<button class="btn btn-sm btn-default" id="import_goods">
					<span class="glyphicon glyphicon-import"></span> <span>导入</span>
				</button>
				<button class="btn btn-sm btn-default" id="export_goods">
					<span class="glyphicon glyphicon-export"></span> <span>导出</span>
				</button>
			</div>
			<div class="col-md-5"></div>
		</div>

		<div class="row" style="margin-top: 15px">
			<div class="col-md-12">
				<table id="goodsList" class="table table-striped"></table>
			</div>
		</div>
	</div>
</div>

<!-- 添加货物信息模态框 -->
<div id="add_modal" class="modal fade" table-index="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">添加货物信息</h4>
			</div>
			<div class="modal-body">
				<!-- 模态框的内容 -->
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-8 col-sm-8">
						<form class="form-horizontal" role="form" id="goods_form"
							style="margin-top: 25px">
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物编号：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_no"
										name="goods_no" placeholder="货物编号">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物名称：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_name"
										name="goods_name" placeholder="货物名称">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物类型：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="hidden" name="goods_type" id="goods_type">
									<button class="btn btn-default dropdown-toggle"
                                            data-toggle="dropdown" >
										<span id="type_select">类型选择</span> <span class="caret"></span>
									</button>
                                    <ul class="dropdown-menu" role="menu" id="type_select_value">
                                    </ul>
                                </div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物尺码：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_sizes" readonly
										   name="goods_sizes" placeholder="货物尺码">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物颜色：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_colors" readonly
										name="goods_colors" placeholder="货物颜色">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物实物图：</span>
								</label>
								<div class="layui-upload">
									<button type="button" class="layui-btn goods_pic_button" style="margin-left:16px">上传图片</button>
									<div class="layui-upload-list">
										<img class="layui-upload-img" id="goods_pic" height="200" width="200" style="margin-left:150px">
									</div>
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
				<button class="btn btn-success" type="button" id="add_modal_submit">
					<span>提交</span>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 编辑货物信息模态框 -->
<div id="edit_modal" class="modal fade" table-index="-1" role="dialog"
	 aria-labelledby="myModalLabel" aria-hidden="true"
	 data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">编辑货物信息</h4>
			</div>
			<div class="modal-body">
				<!-- 模态框的内容 -->
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-8 col-sm-8">
						<form class="form-horizontal" role="form" id="goods_form_edit"
							  style="margin-top: 25px">
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物编号：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_no_edit"
										   name="goods_no" placeholder="货物编号">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物名称：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_name_edit"
										   name="goods_name" placeholder="货物名称">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物类型：</span>
								</label>
								<input type="hidden" name="goods_type_edit" id="goods_type_edit">
								<button class="btn btn-default dropdown-toggle"	data-toggle="dropdown" style="margin-left: 15px;">
									<span id="type_select_edit"></span> <span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu" style="margin-top: -385px;margin-left: 135px;" id="type_select_value_edit">
								</ul>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物尺寸：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_sizes_edit" readonly
										   name="goods_sizes" placeholder="货物尺寸">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物颜色：</span>
								</label>
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control" id="goods_colors_edit" readonly
										   name="goods_colors" placeholder="货物颜色">
								</div>
							</div>
							<div class="form-group">
								<label for="" class="control-label col-md-4 col-sm-4"> <span>货物实物图：</span>
								</label>
								<div class="layui-upload">
									<button type="button" class="layui-btn goods_pic_button" style="margin-left:16px">上传图片</button>
									<div class="layui-upload-list">
										<img class="layui-upload-img" id="goods_pic_edit" height="200" width="200" style="margin-left:150px">
									</div>
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

<!--子颜色弹出框-->
<div id="goods_colors_modal" class="modal fade" table-index="-1" role="dialog"
	 aria-labelledby="myModalLabel" aria-hidden="true"
	 data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">产品颜色</h4>
			</div>
			<div class="modal-body">
				<!-- 模态框的内容 -->
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-8 col-sm-8">
						<form class="form-horizontal" role="form" id="goods_colors_form"
							  style="margin-top: 25px">
							<div class="form-group">
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control goods_color"
										   name="goods_color[0]" placeholder="请输入颜色">
									<div class="alert alert-danger hide">不能为空</div>
								</div>
								<%--<button type="button" class="btn btn-xs btn-link del_color" style="margin-top:7px">删除</button>--%>
							</div>
							<div class="form-group" id="goods_color_group">
								<button type="button" class="btn btn-xs btn-link" style="margin-left:267px" id="add_color">添加</button>
							</div>
						</form>
					</div>
					<div class="col-md-1 col-sm-1"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal" id="add_color_cancel">
					<span>取消</span>
				</button>
				<button class="btn btn-success" type="button" id="goods_color_submit">
					<span>确认</span>
				</button>
			</div>
		</div>
	</div>
</div>
<!--子尺码弹出框-->
<div id="goods_sizes_modal" class="modal fade" table-index="-1" role="dialog"
	 aria-labelledby="myModalLabel" aria-hidden="true"
	 data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">产品尺码</h4>
			</div>
			<div class="modal-body">
				<!-- 模态框的内容 -->
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-8 col-sm-8">
						<form class="form-horizontal" role="form" id="goods_sizes_form"
							  style="margin-top: 25px">
							<div class="form-group">
								<div class="col-md-8 col-sm-8">
									<input type="text" class="form-control goods_size"
										   name="goods_size[0]" placeholder="请输入尺码">
									<div class="alert alert-danger hide">不能为空</div>
								</div>
								<%--<button type="button" class="btn btn-xs btn-link del_size" style="margin-top:7px">删除</button>--%>
							</div>
							<div class="form-group" id="goods_size_group">
								<button type="button" class="btn btn-xs btn-link" style="margin-left:267px" id="add_size">添加</button>
							</div>
						</form>
					</div>
					<div class="col-md-1 col-sm-1"></div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal" id="add_size_cancel">
					<span>取消</span>
				</button>
				<button class="btn btn-success" type="button" id="goods_size_submit">
					<span>确认</span>
				</button>
			</div>
		</div>
	</div>
</div>

<!-- 导入货物信息模态框 -->
<div class="modal fade" id="import_modal" table-index="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">导入货物信息</h4>
			</div>
			<div class="modal-body">
				<div id="step1">
					<div class="row" style="margin-top: 15px">
						<div class="col-md-1 col-sm-1"></div>
						<div class="col-md-10 col-sm-10">
							<div>
								<h4>点击下面的下载按钮，下载货物信息电子表格</h4>
							</div>
							<div style="margin-top: 30px; margin-buttom: 15px">
								<a class="btn btn-info"
									href="commons/fileSource/download/goodsInfo.xlsx"
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
								<h4>请按照货物信息电子表格中指定的格式填写需要添加的一个或多个货物信息</h4>
							</div>
							<div class="alert alert-info"
								style="margin-top: 10px; margin-buttom: 30px">
								<p>注意：</p>
								<p>1.表格中各个列均不能为空，否则该条信息将不完整，不利于维护</p>
								<p>2.表格中“货物类型”请填写相应代码1-5，5个数字任写一个(1：文胸，2：小裤，3：吊衣，4：泳装，5：其他)</p>
								<p>3.表格中“货物尺码”与“货物颜色”多个值之间逗号隔开，如尺码：S，M 如颜色：黑色，白色</p>
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
									<h4>请点击下面上传文件按钮，上传填写好的货物信息电子表格</h4>
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

<!-- 导出货物信息模态框 -->
<div class="modal fade" id="export_modal" table-index="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"
	data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" type="button" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">导出货物信息</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-3 col-sm-3" style="text-align: center;">
						<img src="media/icons/warning-icon.png" alt=""
							style="width: 70px; height: 70px; margin-top: 20px;">
					</div>
					<div class="col-md-8 col-sm-8">
						<h3>是否确认导出货物信息</h3>
						<p>(注意：请确定要导出的货物信息，导出的内容为当前列表的搜索结果)</p>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal">
					<span>取消</span>
				</button>
				<button class="btn btn-success" type="button" id="export_goods_download">
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
						<h3>是否确认删除该条货物信息</h3>
						<p>(注意：若该货物已经有仓库进出库记录或有仓存记录，则该货物信息将不能删除成功。如需删除该货物的信息，请先确保该货物没有关联的仓库进出库记录或有仓存记录)</p>
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
