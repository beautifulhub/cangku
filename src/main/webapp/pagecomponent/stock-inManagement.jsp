<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script>
	var stockin_repository = null;// 入库仓库编号
	var stockin_supplier = null;// 入库供应商编号
	var stockin_goods = null;// 入库货物编号
	var stockin_number = null;// 入库数量

	var supplierCache = new Array();// 供应商信息缓存
	var goodsCache = new Array();//货物信息缓存

    //定义个性化命名空间
    var stockInManage = {
        //批量添加入库
        addStockDetail : function(){
            //绑定添加子入库
            $('#add_stock').unbind("click").click(function(){
                var n=$(".goods_color_selector").length;
                var addAttr = '<div class="row" style="margin-top:5px;width:1500px">\n' +
                    '<div class="col-md-12 col-sm-11 add-goods-detail" style="margin-left:60px">\n' +
                    '<div class="col-md-6 form-group" style="width:20%">\n' +
                    '<label for=""  class="col-md-5 control-label" style="margin-top:6px">货物颜色：</label>\n' +
                    '<div class="col-md-7" style="margin-left: -40px;">\n' +
                    '<select name="goodsColor['+n+']" class="form-control goods_color_selector" style="width:100%;">\n' +
                    '</select>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '<div class="col-md-6 form-group" style="width:20%;margin-left:-70px">\n' +
                    '<label for="" class="col-md-5 control-label" style="margin-top:6px">货物尺码：</label>\n' +
                    '<div class="col-md-5" style="margin-left: -40px;">\n' +
                    '<select name="goodsSize['+n+']" class="form-control goods_size_selector" style="width:100%;">\n' +
                    '</select>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '<div class="col-md-6 form-group" style="width:20%;margin-left:-110px">\n' +
                    '<label for="" class="col-md-5 control-label" style="margin-top:6px">货物数量：</label>\n' +
                    '<div class="col-md-6" style="margin-left: -40px;">\n' +
                    '<input class="form-control goods-num" type="text" placeholder="货物数量" name="goodsNum['+n+']" style="width:110%;" />\n' +
                    '<button type="button" class="btn btn-xs btn-link del_stock_in" style="margin-left:150px;margin-top:-27px">删除</button>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>';
                $("#stock_in_group").before(addAttr);
                $('#stockin_form').bootstrapValidator('addField', 'goodsNum['+n+']', {
                    validators : {
                        notEmpty : {
                            message : '数量只能为数字'
                        },
                        greaterThan: {
                            value: 0,
                            message: '数量不能小于0'
                        }
                    }
                });
                //下拉框赋值
                var colorOption = $(".goods_color_selector:eq(0)").html();
                var sizeOption = $(".goods_size_selector:eq(0)").html();
                $("[name='goodsColor["+n+"]']").append(colorOption);
                $("[name='goodsSize["+n+"]']").append(sizeOption);
                //动态添加-绑定删除动作
                $('.del_stock_in').click(function() {
                    var roleName = $(this).parent("div").find('input[type]').attr("name");
                    $("#stockin_form").bootstrapValidator('removeField',roleName);
                    $(this).parent("div").parent("div").parent("div").remove();
                });
            });


        },
    }

	$(function(){
		repositorySelectorInit();
		dataValidateInit();
		detilInfoToggle();
		addDetailToggle();

		stockInOption();
		fetchStorage();
		supplierAutocomplete();
		goodsAutocomplete();
	})

	// 数据校验
	function dataValidateInit(){
		$('#stockin_form').bootstrapValidator({
            message : 'This is not valid',
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            excluded : [ ':disabled' ],
			fields : {
				/*stockin_input : {
					validators : {
						notEmpty : {
							message : '入库数量不能为空'
						},
						greaterThan: {
	                        value: 0,
	                        message: '入库数量不能小于0'
	                    }
					}
				}*/
                supplierInput : {
					validators : {
						notEmpty : {
							message : '请输入对应的供应商'
						}
					}
				},
                goodsNO : {
					validators : {
						notEmpty : {
							message : '货物编号不能为空'
						}
					}
				},
                goodsNum : {
					validators : {
                        notEmpty : {
                            message : '数量只能为数字'
                        },
                        greaterThan: {
                            value: 0,
                            message: '数量不能小于0'
                        }
					}
				}
			}
		})
	}

	// 货物信息自动匹配
	function goodsAutocomplete(){
		$('#goods_no').autocomplete({
			minLength : 0,
			delay : 500,
			source : function(request, response){
				$.ajax({
					type : 'GET',
					url : 'goodsManage/getGoodsList',
					dataType : 'json',
					contentType : 'application/json',
					data : {
						offset : -1,
						limit : -1,
						keyWord : request.term,
						searchType : 'searchByNO'
					},
					success : function(data){
						var autoCompleteInfo = new Array();
                        goodsCache = new Array();
						$.each(data.rows, function(index,elem){
							goodsCache.push(elem);
							autoCompleteInfo.push({label:elem.no,value:elem.id});
						});
						response(autoCompleteInfo);
					}
				});
			},
			focus : function(event, ui){
				$('#goods_no').val(ui.item.label);
				return false;
			},
			select : function(event, ui){
				$('#goods_no').val(ui.item.label);
				stockin_goods = ui.item.value;
				goodsInfoSet(stockin_goods);
				//loadStorageInfo();
				return false;
			}
		})
	}

	// 供应商信息自动匹配
	function supplierAutocomplete(){
		$('#supplier_input').autocomplete({
			minLength : 0,
			delay:500,
			source : function(request, response){
				$.ajax({
					type : "GET",
					url : 'supplierManage/getSupplierList',
					dataType : 'json',
					contentType : 'application/json',
					data : {
						offset : -1,
						limit : -1,
						searchType : 'searchByName',
						keyWord : request.term
					},
					success : function(data){
						var autoCompleteInfo = new Array();
						$.each(data.rows,function(index, elem){
							supplierCache.push(elem);
							autoCompleteInfo.push({label:elem.name,value:elem.id});
						});
						response(autoCompleteInfo);
					}
				});
			},
			focus : function(event, ui){
				$('#supplier_input').val(ui.item.label);
				return false;
			},
			select : function(event, ui){
				$('#supplier_input').val(ui.item.label);
				stockin_supplier = ui.item.value;
				supplierInfoSet(stockin_supplier);
				return false;
			}
		})
	}

	// 填充供应商详细信息
	function supplierInfoSet(supplierID){
		var detailInfo;
		$.each(supplierCache,function(index,elem){
			if(elem.id == supplierID){
				detailInfo = elem;
				if(detailInfo.id==null)
					$('#info_supplier_ID').text('-');
				else
					$('#info_supplier_ID').text(detailInfo.id);
				
				if(detailInfo.name==null)
					$('#info_supplier_name').text('-');
				else
					$('#info_supplier_name').text(detailInfo.name);
				
				if(detailInfo.tel==null)
					$('#info_supplier_tel').text('-');
				else
					$('#info_supplier_tel').text(detailInfo.tel);
				
				if(detailInfo.personInCharge==null)
					$('#info_supplier_person').text('-');
				else
					$('#info_supplier_person').text(detailInfo.personInCharge);
				
				if(detailInfo.email==null)
					$('#info_supplier_email').text('-');
				else
					$('#info_supplier_email').text(detailInfo.email);
				
				
				if(detailInfo.address==null)
					$('#info_supplier_address').text('-');
				else
					$('#info_supplier_address').text(detailInfo.address);
			}
		})

	}

	// 填充货物详细信息
    function goodsInfoSet(goodsID){
        var detailInfo;
        $.each(goodsCache,function(index,elem){
            if(elem.id == goodsID){
                detailInfo = elem;

                if(detailInfo.name==null)
                    $('#goods_name').val('-');
                else
                    $('#goods_name').val(detailInfo.name);

                if(detailInfo.colors==null){
                    $(".goods_color_selector").each(function(i,item){
                        $(item).empty();
                        $(item).append("<option value='-1'>请选择</option>");
                    })
                }else{
                    $(".goods_color_selector").each(function(i,item){
                        $(item).empty();
                        var goodsColors = detailInfo.colors.replace(/，/ig,','); //统一将中英文逗号处理成英文
                        var colorsArr = goodsColors.split(",");
                        for(var i=0 ; i<colorsArr.length; i++){
                            $(item).append("<option value='" + colorsArr[i] + "'>" + colorsArr[i] +"</option>");
                        }
                    })
                }

                if(detailInfo.sizes==null){
                    $(".goods_size_selector").each(function(i,item){
                        $(item).empty();
                        $(item).append("<option value='-1'>请选择</option>");
                    })
                }else{
                    $(".goods_size_selector").each(function(i,item){
                        $(item).empty();
                        var goodsSizes = detailInfo.sizes.replace(/，/ig,','); //统一将中英文逗号处理成英文
                        var sizesArr = goodsSizes.split(",");
                        for(var i=0 ; i<sizesArr.length; i++){
                            $(item).append("<option value='" + sizesArr[i] + "'>" + sizesArr[i] +"</option>");
                        }
                    })
                }

            }
        })
    }

	// 详细信息显示与隐藏
	function detilInfoToggle(){
		$('#info-show').click(function(){
			$('#detailInfo').removeClass('hide');
			$(this).addClass('hide');
			$('#info-hidden').removeClass('hide');
		});

		$('#info-hidden').click(function(){
			$('#detailInfo').removeClass('hide').addClass('hide');
			$(this).addClass('hide');
			$('#info-show').removeClass('hide');
		});
	}

	// 添加货物详情的显示与隐藏
	function addDetailToggle(){
		$('#addDetail-show').click(function(){
			$('#addDetail').removeClass('hide');
			$(this).addClass('hide');
			$('#addDetail-hidden').removeClass('hide');
            stockInManage.addStockDetail();
		});

		$('#addDetail-hidden').click(function(){
			$('#addDetail').removeClass('hide').addClass('hide');
			$(this).addClass('hide');
			$('#addDetail-show').removeClass('hide');
		});
	}

	// 仓库下拉列表初始化
    function repositorySelectorInit(){
        $.ajax({
            type : 'GET',
            url : 'repositoryManage/getOwnRepo',
            dataType : 'json',
            contentType : 'application/json',
            data : {
                searchType : 'searchAll',
                keyWord : '',
                offset : -1,
                limit : -1
            },
            success : function(response){
                $.each(response.data,function(index,elem){
                    $('#search_input_repository').append("<option value='" + elem.id + "'>" + elem.id +"号仓库</option>");
                });
                if(UnRepoAuthTip())return;
                stockin_repository = response.data[0].id
            },
            error : function(response){
                $('#search_input_repository').append("<option value='-1'>加载失败</option>");
            }

        })
    }

	// 获取仓库当前库存量
	function fetchStorage(){
		$('#search_input_repository').change(function(){
			stockin_repository = $(this).val();
			//loadStorageInfo();
		});
	}

	function loadStorageInfo(){
		if(stockin_repository != null && stockin_goods != null){
			$.ajax({
				type : 'GET',
				url : 'storageManage/getStorageListWithRepository',
				dataType : 'json',
				contentType : 'application/json',
				data : {
					offset : -1,
					limit : -1,
					searchType : 'searchByGoodsID',
					repositoryBelong : stockin_repository,
					keyword : stockin_goods
				},
				success : function(response){
					if(response.total > 0){
						data = response.rows[0].number;
						$('#info_storage').text(data);
					}else{
						$('#info_storage').text('0');
					}
				},
				error : function(response){
					// do nothing
				}
			})
		}
	}

	// 执行货物入库操作
	function stockInOption(){
		$('#submit').click(function(){
            if(UnRepoAuthTip())return;
			// data validate
			$('#stockin_form').data('bootstrapValidator').validate();
			if (!$('#stockin_form').data('bootstrapValidator').isValid()) {
				return;
			}
			//获取入库获取的明细
            var addGoodsDetail = "";
            $(".add-goods-detail").each(function(i,item){
                var goodsColor = $(item).find('.goods_color_selector').val()
                var goodsSize = $(item).find('.goods_size_selector').val()
                var goodsNum = $(item).find('.goods-num').val().trim()
                addGoodsDetail += goodsColor + "," + goodsSize + "," + goodsNum +";"
            })
			data = {
				supplierID : stockin_supplier,
                goodsNO : $("#goods_no").val().trim(),
                goodsName : $("#goods_name").val().trim(),
                goodsDetail : addGoodsDetail,
                repositoryID : stockin_repository,
				remark : $("#remark").val().trim()
                // number : $('#stockin_input').val(),
			}

			$.ajax({
				type : 'POST',
				url : 'stockRecordManage/stockIn',
				dataType : 'json',
				content : 'application/json',
				data : data,
				success : function(response){
					var msg;
					var type;
					var append = '';
					if(response.result == "success"){
						type = 'success';
						msg = '货物入库成功';
						inputReset();
					}else{
						type = 'error';
						msg = '货物入库失败'
					}
					showMsg(type, msg, append);
				},
				error : function(xhr, textStatus, errorThrown){
					// handler error
					handleAjaxError(xhr.status);
				}
			})
		});
	}

	// 页面重置
	function inputReset(){
		$('#supplier_input').val('');
		$('#goods_no').val('');
        $('#goods_name').val('');
        $(".goods_color_selector").each(function(i,item){
            $(item).empty();
		})
        $(".goods_size_selector").each(function(i,item){
            $(item).empty();
		})
        $(".goods-num").each(function(i,item){
            $(item).val('');
		})
        $(".add-goods-detail:gt(0)").each(function(i,item){
            var roleName = $(this).find('.goods-num').attr("name");
            $("#stockin_form").bootstrapValidator('removeField',roleName);
            $(item).remove()
        })
        $('#addDetail-hidden').click()
        $('#remark').val('');
        /*$('#stockin_input').val('');*/
        $('#info_supplier_ID').text('-');
        $('#info_supplier_name').text('-');
        $('#info_supplier_tel').text('-');
        $('#info_supplier_address').text('-');
        $('#info_supplier_email').text('-');
        $('#info_supplier_person').text('-');
		$('#stockin_form').bootstrapValidator("resetForm",true);
	}

</script>


<div class="panel panel-default">
	<ol class="breadcrumb">
		<li>货物入库</li>
	</ol>
	<div class="panel-body">
        <form class="form-inline" role="form" id="stockin_form">
		<div class="row">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<%--<form action="" class="form-inline">--%>
							<div class="form-group">
								<label for="" class="form-label">&nbsp;&nbsp;&nbsp;&nbsp;供应商：</label>
								<input type="text" class="form-control" id="supplier_input" name="supplierInput" placeholder="请输入供应商名称">
							</div>
						<%--</form>--%>
					</div>
				</div>
			</div>
		</div>
		<div class="row visible-md visible-lg">
			<div class="col-md-12 col-sm-12">
				<div class='pull-right' style="cursor:pointer" id="info-show">
					<span>显示供应商详细信息</span>
					<span class="glyphicon glyphicon-chevron-down"></span>
				</div>
				<div class='pull-right hide' style="cursor:pointer" id="info-hidden">
					<span>隐藏供应商详细信息</span>
					<span class="glyphicon glyphicon-chevron-up"></span>
				</div>
			</div>
		</div>
		<div class="row hide" id="detailInfo" style="margin-bottom:30px">
			<div class="col-md-6 col-sm-6  visible-md visible-lg">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-10">
						<label for="" class="text-info">供应商信息</label>
					</div>
				</div>
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-11 col-sm-11">
						<div class="col-md-6 col-sm-6">
							<div style="margin-top:5px">
								<div class="col-md-6 col-sm-6">
									<span for="" class="pull-right">供应商ID：</span>
								</div>
								<div class="col-md-6 col-sm-6">
									<span id="info_supplier_ID">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6 col-sm-6">
									<span for="" class="pull-right">负责人：</span>
								</div>
								<div class="col-md-6 col-sm-6">
									<span id="info_supplier_person">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6 col-sm-6">
									<span for="" class="pull-right">电子邮件：</span>
								</div>
								<div class="col-md-6">
									<span id="info_supplier_email">-</span>
								</div>
							</div>
						</div>
						<div class="col-md-6 col-sm-6  visible-md visible-lg">
							<div style="margin-top:5px">
								<div class="col-md-6 col-sm-6">
									<span for="" class="pull-right">供应商名：</span>
								</div>
								<div class="col-md-6 col-sm-6">
									<span id="info_supplier_name">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6 col-sm-6">
									<span for="" class="pull-right">联系电话：</span>
								</div>
								<div class="col-md-6 col-sm-6">
									<span id="info_supplier_tel">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6 col-sm-6">
									<span for="" class="pull-right">联系地址：</span>
								</div>
								<div class="col-md-6 col-sm-6">
									<span id="info_supplier_address">-</span>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
        <div class="row" style="margin-top: 10px">
            <div class="col-md-6 col-sm-6">
                <div class="row" style="width:760px">
                    <div class="col-md-1 col-sm-1"></div>
                    <div class="col-md-10 col-sm-11">
                        <%--<form action="" class="form-inline">--%>
                            <div class="form-group">
                                <label for="" class="form-label">货物编号：</label>
								<input type="text" class="form-control" placeholder="请输入货物编号" id="goods_no" name="goodsNO">
                            </div>
                            <div class="form-group">
                                <label for="" class="form-label">货物名称：</label>
								<input type="text" class="form-control" placeholder="请输入货物名称" id="goods_name" name="goodsName" readonly>
                            </div>
                        <%--</form>--%>
                    </div>
                </div>
				<div class="row visible-md visible-lg">
					<div class="col-md-12 col-sm-12">
						<div class='pull-right' style="cursor:pointer" id="addDetail-show">
							<span>显示入库详情</span>
							<span class="glyphicon glyphicon-chevron-down"></span>
						</div>
						<div class='pull-right hide' style="cursor:pointer" id="addDetail-hidden">
							<span>隐藏入库详情</span>
							<span class="glyphicon glyphicon-chevron-up"></span>
						</div>
					</div>
				</div>
				<div class="row hide" id="addDetail" style="margin-bottom:30px">
					<div class="col-md-12 col-sm-12  visible-md visible-lg">
						<div class="row">
							<div class="col-md-1 col-sm-1"></div>
							<div class="col-md-10 col-sm-10">
								<label for="" class="text-info">入库货物明细</label>
							</div>
						</div>

						<div class="row" style="margin-top:5px;width:1500px">
							<div class="col-md-12 col-sm-11 add-goods-detail" style="margin-left:60px">
								<div class="col-md-6 form-group" style="width:20%">
									<label for=""  class="col-md-5 control-label" style="margin-top:6px">货物颜色：</label>
									<div class="col-md-7" style="margin-left: -40px;">
										<select name="goodsColor" class="form-control goods_color_selector" style="width:100%;">
										</select>
									</div>
								</div>
								<div class="col-md-6 form-group" style="width:20%;margin-left:-70px">
									<label for="" class="col-md-5 control-label" style="margin-top:6px">货物尺码：</label>
									<div class="col-md-5" style="margin-left: -40px;">
										<select name="goodsSize" class="form-control goods_size_selector" style="width:100%;">
										</select>
									</div>
								</div>
								<div class="col-md-6 form-group" style="width:20%;margin-left:-110px">
									<label for="" class="col-md-5 control-label" style="margin-top:6px">货物数量：</label>
									<div class="col-md-6" style="margin-left: -40px;">
										<input class="form-control goods-num" type="text" placeholder="货物数量" name="goodsNum" style="width:110%;" />
									</div>
								</div>
							</div>
                        </div>
						
                        <div style="margin-left:720px;margin-top:5px" id="stock_in_group">
                            <button type="button" class="btn btn-xs btn-link"  id="add_stock">添加</button>
                        </div>
					</div>
				</div>
            </div>
        </div>
        <div class="row" style="margin-top: 10px">
            <div class="col-md-6 col-sm-6">
                <div class="row">
                    <div class="col-md-1 col-sm-1"></div>
                    <div class="col-md-10 col-sm-11">
						<div class="form-group">
							<label for="" class="form-label">入库仓库：</label>
							<select name="" id="search_input_repository" class="form-control">
								<%--<option value='-1'>请选择仓库</option>--%>
							</select>
						</div>
                    </div>
                </div>
            </div>
        </div>
		<div class="row" style="margin-top:20px">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<div class="form-group">
							<label for="" class="form-label">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备注：</label>
							<input type="text" class="form-control" id="remark" placeholder="备注说明">
						</div>
					</div>
				</div>
			</div>
		</div>
        <%--<div class="row" style="margin-top:20px">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<form action="" class="form-inline" id="">
							<div class="form-group">
								<label for="" class="control-label">入库数量：</label>
								<input type="text" class="form-control" placeholder="请输入数量" id="stockin_input" name="stockin_input">
								<span>(当前库存量：</span>
								<span id="info_storage">-</span>
								<span>)</span>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>--%>
        </form>
	</div>
	<div class="panel-footer">
		<div style="text-align:right">
			<button class="btn btn-success" type="button" id="submit">提交入库</button>
		</div>
	</div>
</div>