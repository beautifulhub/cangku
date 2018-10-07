<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script>
	var stockout_repository = null;
	var stockout_customer = null;
	var stockout_goods = null;
	var stockout_number = null;

	var customerCache = new Array();
	var goodsCache = new Array();

	$(function(){
		dataValidateInit();
		repositorySelectorInit();
		detilInfoToggle();
		stockoutOperation();

		fetchStorage();
		goodsAutocomplete();
		customerAutocomplete();
	})

	function dataValidateInit(){
		$('#stockout_form').bootstrapValidator({
			message : 'This is not valid',
			
			fields : {
				stockout_input : {
					validators : {
						notEmpty : {
							message : '入库数量不能为空'
						},
						greaterThan: {
	                        value: 0,
	                        message: '入库数量不能小于0'
	                    }
					}
				}
			}
		});
	}

	//货物信息自动匹配
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
				stockout_goods = ui.item.value;
				goodsInfoSet(stockout_goods);
				loadStorageInfo();
				return false;
			}
		})
	}

	function customerAutocomplete(){
		$('#customer_input').autocomplete({
			minLength : 0,
			delay : 500,
			source : function(request, response){
				$.ajax({
					type : 'GET',
					url : 'customerManage/getCustomerList',
					dataType : 'json',
					contentType : 'application/json',
					data : {
						offset : -1,
						limit : -1,
						keyWord : request.term,
						searchType : 'searchByName'
					},
					success : function(data){
						var autoCompleteInfo = Array();
						$.each(data.rows,function(index,elem){
							customerCache.push(elem);
							autoCompleteInfo.push({label:elem.name,value:elem.id});
						});
						response(autoCompleteInfo);
					}
				});
			},
			focus : function(event,ui){
				$('#customer_input').val(ui.item.label);
				return false;
			},
			select : function(event,ui){
				$('#customer_input').val(ui.item.label);
				stockout_customer = ui.item.value;
				customerInfoSet(stockout_customer);
				loadStorageInfo();
				return false;
			}
		})
	}

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

	function customerInfoSet(customerID){
		var detailInfo;
		$.each(customerCache,function(index,elem){
			if(elem.id == customerID){
				detailInfo = elem;

				if(detailInfo.id == null)
					$('#info_customer_ID').text('-');
				else
					$('#info_customer_ID').text(detailInfo.id);
				
				if(detailInfo.name == null)
					$('#info_customer_name').text('-');
				else
					$('#info_customer_name').text(detailInfo.name);
				
				if(detailInfo.tel == null)
					$('#info_customer_tel').text('-');
				else
					$('#info_customer_tel').text(detailInfo.tel);
				
				if(detailInfo.address == null)
					$('#info_customer_address').text('-');
				else
					$('#info_customer_address').text(detailInfo.address);
				
				if(detailInfo.email == null)
					$('#info_customer_email').text('-');
				else
					$('#info_customer_email').text(detailInfo.email);
				
				if(detailInfo.personInCharge == null)
					$('#info_customer_person').text('-');
				else
					$('#info_customer_person').text(detailInfo.personInCharge);
					
			}
		})
	}

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

	//仓库下拉列表初始化
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

	function fetchStorage(){
		$('#repository_selector').change(function(){
			stockout_repository = $(this).val();
			loadStorageInfo();
		});
	}

	function loadStorageInfo(){
		if(stockout_repository != null && stockout_goods != null){
			$.ajax({
				type : 'GET',
				url : 'storageManage/getStorageListWithRepository',
				dataType : 'json',
				contentType : 'application/json',
				data : {
					offset : -1,
					limit : -1,
					searchType : 'searchByGoodsID',
					repositoryBelong : stockout_repository,
					keyword : stockout_goods
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

	//执行货物出库操作
	function stockoutOperation(){
		$('#submit').click(function(){
			// data validate
			$('#stockout_form').data('bootstrapValidator').validate();
			if (!$('#stockout_form').data('bootstrapValidator').isValid()) {
				return;
			}

			data = {
				customerID : stockout_customer,
				goodsID : stockout_goods,
				repositoryID : stockout_repository,
				number : $('#stockout_input').val(),
			}

			$.ajax({
				type : 'POST',
				url : 'stockRecordManage/stockOut',
				dataType : 'json',
				content : 'application/json',
				data : data,
				success : function(response){
					var msg;
					var type;
					var append = '';
					if(response.result == "success"){
						type = 'success';
						msg = '货物出库成功';
						inputReset();
					}else{
						type = 'error';
						msg = '货物出库失败'
					}
					showMsg(type, msg, append);
				},
				error : function(xhr, textStatus, errorThrown){
					// handle error
					handleAjaxError(xhr.status);
				}
			})
		});
	}

	function inputReset(){
		$('#customer_input').val('');
		$('#goods_input').val('');
		$('#stockout_input').val('');
		$('#info_customer_ID').text('-');
		$('#info_customer_name').text('-');
		$('#info_customer_tel').text('-');
		$('#info_customer_address').text('-');
		$('#info_customer_email').text('-');
		$('#info_customer_person').text('-');
		$('#info_goods_ID').text('-');
		$('#info_goods_name').text('-');
		$('#info_goods_size').text('-');
		$('#info_goods_type').text('-');
		$('#info_goods_value').text('-');
		$('#info_storage').text('-');
		$('#stockout_form').bootstrapValidator("resetForm",true); 
	}

</script>

<div class="panel panel-default">
	<ol class="breadcrumb">
		<li>货物出库</li>
	</ol>
	<div class="panel-body">
		<div class="row">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<form action="" class="form-inline">
							<div class="form-group">
								<label for="" class="form-label">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;客户：</label>
								<input type="text" class="form-control" id="customer_input" placeholder="请输入客户名称">
							</div>
						</form>
					</div>
				</div>
			</div>
			<%--<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<form action="" class="form-inline">
							<div class="form-group">
								<label for="" class="form-label">出库货物：</label>
								<input type="text" class="form-control" id="goods_input" placeholder="请输入货物名称">
							</div>
						</form>
					</div>
				</div>
			</div>--%>
		</div>
		<div class="row visible-md visible-lg">
			<div class="col-md-12">
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
			<div class="col-md-6  visible-md visible-lg">
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-10">
						<label for="" class="text-info">客户信息</label>
					</div>
				</div>
				<div class="row">
					<div class="col-md-1"></div>
					<div class="col-md-11">
						<div class="col-md-6">
							<div style="margin-top:5px">
								<div class="col-md-6">
									<span for="" class="pull-right">客户ID：</span>
								</div>
								<div class="col-md-6">
									<span id="info_customer_ID">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6">
									<span for="" class="pull-right">负责人：</span>
								</div>
								<div class="col-md-6">
									<span id="info_customer_person">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6">
									<span for="" class="pull-right">电子邮件：</span>
								</div>
								<div class="col-md-6">
									<span id="info_customer_email">-</span>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div style="margin-top:5px">
								<div class="col-md-6">
									<span for="" class="pull-right">客户名：</span>
								</div>
								<div class="col-md-6">
									<span id="info_customer_name">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6">
									<span for="" class="pull-right">联系电话：</span>
								</div>
								<div class="col-md-6">
									<span id="info_customer_tel">-</span>
								</div>
							</div>
							<div style="margin-top:5px">
								<div class="col-md-6">
									<span for="" class="pull-right">联系地址：</span>
								</div>
								<div class="col-md-6">
									<span id="info_customer_address">-</span>
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
                                <label for="" class="text-info">出库货物明细</label>
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
		<div class="row" style="margin-top:10px">
			<div class="col-md-6 col-sm-6">
				<div class="row">
					<div class="col-md-1 col-sm-1"></div>
					<div class="col-md-10 col-sm-11">
						<form action="" class="form-inline">
							<div class="form-group">
								<label for="" class="form-label">出库仓库：</label>
								<select name="" id="repository_selector" class="form-control">
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
						<form action="" class="form-inline" id="stockout_form">
							<div class="form-group">
								<label for="" class="form-label">出库数量：</label>
								<input type="text" class="form-control" placeholder="请输入数量" id="stockout_input" name="stockout_input">
								<span>(当前库存量：</span>
								<span id="info_storage">-</span>
								<span>)</span>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>--%>
		<div class="row" style="margin-top:80px"></div>
	</div>
	<div class="panel-footer">
		<div style="text-align:right">
			<button class="btn btn-success" id="submit">提交出库</button>
		</div>
	</div>
</div>