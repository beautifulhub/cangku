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
                var n=$(".goods-color").length;
                var addAttr = '<div class="row" style="margin-top:5px">\n' +
                    '<div class="form-group add-goods-detail">\n' +
                    '<label for="" class="col-sm-2 control-label" style="margin-left:95px;margin-top:5px">货物颜色：</label>\n' +
                    '<div class="col-md-2">\n' +
                    '<input class="form-control goods-color" style="margin-left: -55px;" type="text" placeholder="货物颜色" name="goodsColor['+n+']" />\n' +
                    '</div>\n' +
                    '<label for="" class="col-sm-2 control-label" style="margin-left:-50px;margin-top:5px">货物尺码：</label>\n' +
                    '<div class="col-md-2">\n' +
                    '<input class="form-control goods-size" style="margin-left: -55px;" type="text" placeholder="货物尺码" name="goodsSize['+n+']" />\n' +
                    '</div>\n' +
                    '<label for="" class="col-sm-2 control-label" style="margin-left:-50px;margin-top:5px">货物数量：</label>\n' +
                    '<div class="col-md-2">\n' +
                    '<input class="form-control goods-num" style="margin-left: -55px;" type="text" placeholder="货物数量" name="goodsNum['+n+']" />\n' +
                    ' <button type="button" class="btn btn-xs btn-link del_stock_in" style="margin-left:74px;margin-top:-27px">删除</button>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>';
                $("#stock_in_group").before(addAttr);
                $('#stockin_form').bootstrapValidator('addField', 'goodsColor['+n+']', {
                    validators: {
                        notEmpty: {
                            message: '颜色不能为空'
                        }
                    }
                });
                $('#stockin_form').bootstrapValidator('addField', 'goodsSize['+n+']', {
                    validators: {
                        notEmpty: {
                            message: '尺码不能为空'
                        }
                    }
                });
                $('#stockin_form').bootstrapValidator('addField', 'goodsNum['+n+']', {
                    validators: {
                        notEmpty: {
                            message: '数量不能为空'
                        }
                    }
                });
                //动态添加-绑定删除动作
                $('.del_stock_in').click(function() {
                    var roleName1 = $(this).prev().find('input[type]').attr("name");
                    var roleName2 = $(this).parent().prev().prev().children('input[type]').attr("name");
                    var roleName3 = $(this).parent().prev().prev().prev().prev().children('input[type]').attr("name");
                    $("#stockin_form").bootstrapValidator('removeField',roleName1);
                    $("#stockin_form").bootstrapValidator('removeField',roleName2);
                    $("#stockin_form").bootstrapValidator('removeField',roleName3);
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
        debugger
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
                goodsNO : {
					validators : {
						notEmpty : {
							message : '货物编号不能为空'
						}
					}
				},
                goodsName : {
					validators : {
						notEmpty : {
							message : '货物名称不能为空'
						}
					}
				},
			}
		})
	}

	// 货物信息自动匹配
	function goodsAutocomplete(){
		$('#goods_input').autocomplete({
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
						searchType : 'searchByName'
					},
					success : function(data){
						var autoCompleteInfo = new Array();
						$.each(data.rows, function(index,elem){
							goodsCache.push(elem);
							autoCompleteInfo.push({label:elem.name,value:elem.id});
						});
						response(autoCompleteInfo);
					}
				});
			},
			focus : function(event, ui){
				$('#goods_input').val(ui.item.label);
				return false;
			},
			select : function(event, ui){
				$('#goods_input').val(ui.item.label);
				stockin_goods = ui.item.value;
				//goodsInfoSet(stockin_goods);
				loadStorageInfo();
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
				
				
				if(detailInfo.adress==null)
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
				if(detailInfo.id==null)
					$('#info_goods_ID').text('-');
				else
					$('#info_goods_ID').text(detailInfo.id);
				
				if(detailInfo.name==null)
					$('#info_goods_name').text('-');
				else
					$('#info_goods_name').text(detailInfo.name);
				
				if(detailInfo.type==null)
					$('#info_goods_type').text('-');
				else
					$('#info_goods_type').text(detailInfo.type);
				
				if(detailInfo.size==null)
					$('#info_goods_size').text('-');
				else
					$('#info_goods_size').text(detailInfo.size);
				
				if(detailInfo.value==null)
					$('#info_goods_value').text('-');
				else
					$('#info_goods_value').text(detailInfo.value);
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
			url : 'repositoryManage/getRepositoryList',
			dataType : 'json',
			contentType : 'application/json',
			data : {
				searchType : 'searchAll',
				keyWord : '',
				offset : -1,
				limit : -1
			},
			success : function(response){
				$.each(response.rows,function(index,elem){
					$('#repository_selector').append("<option value='" + elem.id + "'>" + elem.id +"号仓库</option>");
				});
			},
			error : function(response){
				$('#repository_selector').append("<option value='-1'>加载失败</option>");
			}
			
		})
	}

	// 获取仓库当前库存量
	function fetchStorage(){
		$('#repository_selector').change(function(){
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
		    debugger
			// data validate
			$('#stockin_form').data('bootstrapValidator').validate();
			if (!$('#stockin_form').data('bootstrapValidator').isValid()) {
				return;
			}
			//获取入库获取的明细
            var addGoodsDetail = "";
            $(".add-goods-detail").each(function(i,item){
                var goodsColor = $(item).find('.goods-color').val().trim()
                var goodsSize = $(item).find('.goods-size').val().trim()
                var goodsNum = $(item).find('.goods-num').val().trim()
                addGoodsDetail += goodsColor + "," + goodsSize + "," + goodsNum +";"
            })
			data = {
				supplierID : stockin_supplier,
                goodsNO : $("#goods_no").val().trim(),
                goodsName : $("#goods_name").val().trim(),
                goodsDetail : addGoodsDetail,
                repositoryID : stockin_repository,
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
		$('#goods_input').val('');
		/*$('#stockin_input').val('');*/
		$('#info_supplier_ID').text('-');
		$('#info_supplier_name').text('-');
		$('#info_supplier_tel').text('-');
		$('#info_supplier_address').text('-');
		$('#info_supplier_email').text('-');
		$('#info_supplier_person').text('-');
		/*$('#info_goods_ID').text('-');
		$('#info_goods_name').text('-');
		$('#info_goods_size').text('-');
		$('#info_goods_type').text('-');
		$('#info_goods_value').text('-');*/
		$('#info_storage').text('-');
		$('#stockin_form').bootstrapValidator("resetForm",true); 
	}

</script>


<div class="panel panel-default">
	<ol class="breadcrumb">
		<li>货物入库</li>
	</ol>
	<div class="panel-body">
        <form class="form-horizontal" role="form" id="stockin_form">
		<div class="row">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<form action="" class="form-inline">
							<div class="form-group">
								<label for="" class="form-label">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;供应商：</label>
								<input type="text" class="form-control" id="supplier_input" placeholder="请输入供应商名称">
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div class="row visible-md visible-lg">
			<div class="col-md-12 col-sm-12">
				<div class='pull-right' style="cursor:pointer" id="info-show">
					<span>显示详细信息</span>
					<span class="glyphicon glyphicon-chevron-down"></span>
				</div>
				<div class='pull-right hide' style="cursor:pointer" id="info-hidden">
					<span>隐藏详细信息</span>
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
                <div class="row">
                    <div class="col-md-1 col-sm-1"></div>
                    <div class="col-md-10 col-sm-11">
                        <form action="" class="form-inline">
                            <div class="form-group">
                                <label for="" class="form-label">货物编号：</label>
								<input type="text" class="form-control" placeholder="请输入货物编号" id="goods_no" name="goodsNO">
                            </div>
                            <div class="form-group">
                                <label for="" class="form-label">货物名称：</label>
								<input type="text" class="form-control" placeholder="请输入货物名称" id="goods_name" name="goodsName">
                            </div>
                        </form>
                    </div>
                </div>
				<div class="row visible-md visible-lg">
					<div class="col-md-12 col-sm-12">
						<div class='pull-right' style="cursor:pointer" id="addDetail-show">
							<span>显示添加详情</span>
							<span class="glyphicon glyphicon-chevron-down"></span>
						</div>
						<div class='pull-right hide' style="cursor:pointer" id="addDetail-hidden">
							<span>隐藏添加详情</span>
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

						<div class="row" style="margin-top:5px">
							<div class="form-group add-goods-detail">
								<label for="" class="col-sm-2 control-label" style="margin-left:95px;margin-top:5px">货物颜色：</label>
								<div class="col-md-2">
									<input class="form-control goods-color" style="margin-left: -55px;" type="text" placeholder="货物颜色" name="goodsColor" />
								</div>
								<label for="" class="col-sm-2 control-label" style="margin-left:-50px;margin-top:5px">货物尺码：</label>
								<div class="col-md-2">
									<input class="form-control goods-size" style="margin-left: -55px;" type="text" placeholder="货物尺码" name="goodsSize" />
								</div>
								<label for="" class="col-sm-2 control-label" style="margin-left:-50px;margin-top:5px">货物数量：</label>
								<div class="col-md-2">
									<input class="form-control goods-num" style="margin-left: -55px;" type="text" placeholder="货物数量" name="goodsNum" />
								</div>
							</div>
                        </div>
                        <div style="margin-left:720px" id="stock_in_group">
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
                        <form action="" class="form-inline">
                            <div class="form-group">
                                <label for="" class="form-label">入库仓库：</label>
                                <select name="" id="repository_selector" class="form-control">
									<option value='-1'>请选择仓库</option>
                                </select>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <%--<div class="row" style="margin-top:20px">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<form action="" class="form-inline" id="stockin_form">
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