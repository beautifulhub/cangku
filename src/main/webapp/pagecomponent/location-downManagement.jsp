<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script>
	var locationdown_repository = null;
	var locationdown_goods = null;
	var locationdown_number = null;
	var goodsCache = new Array();


    //定义个性化命名空间
    var locationDownManage = {
        //批量添加入库
        downLocationDetail : function(){
            var _this = this;
            //绑定添加子入库
            $('#add_out_stock').unbind("click").click(function(){
                //添加前对新增的数据进行校验
                //①验证货物是否已经选好
                if(_this.locationDownGoodsCheck($('#goods_name').val().trim())){
                    return;
				}
				//②校验货物数量输入是否合法
                $('#locationdown_form').data('bootstrapValidator').validate();
                if (!$('#locationdown_form').data('bootstrapValidator').isValid()) {
                    return;
                }
                var n=$(".goods_color_selector").length;
                var checkError = false;
                //③检查该仓库是否有对应的货位
                $.ajax({
                    type : 'GET',
                    url : 'locationRecordManage/checkLocationStorage',
                    dataType : 'json',
                    contentType : 'application/json',
                    async : false,
                    data : {
                        goodsID : locationdown_goods,
                        goodsColor : $("select[name='goodsColor["+(n-1)+"]']").val(),
                        goodsSize : $("select[name='goodsSize["+(n-1)+"]']").val(),
                        goodsNum : $("input[name='goodsNum["+(n-1)+"]']").val().trim(),
                        locationNO : $("select[name='locationNO["+(n-1)+"]']").val(),
                        repositoryID : locationdown_repository
                    },
                    success : function(ret){
                        checkError = _this.locationDownRowCheck(ret.result,n);
                    },
                    error : function(xhr, textStatus, errorThrown){
                        // handle error
                        handleAjaxError(xhr.status);
                    }
                });
                if(checkError)return;
                var addAttr = '<div class="row" style="margin-top:5px;width:1500px">\n' +
                    '<div class="col-md-12 col-sm-11 down-goods-detail" style="margin-left:60px">\n' +
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
                    /*'<button type="button" class="btn btn-xs btn-link del_stock_out" style="margin-left:350px;margin-top:-27px">删除</button>\n' +*/
                    '</div>\n' +
                    '</div>\n' +
                    '<div class="col-md-6 form-group" style="width:20%;margin-left:-80px">\n' +
                    '<label for="" class="col-md-5 control-label" style="margin-top:6px">货位编号：</label>\n' +
                    '<div class="col-md-6" style="margin-left: -40px;">\n' +
                    '<select name="locationNO['+n+']" class="form-control location_no_selector" style="width:120%;">\n' +
                    '</select>\n' +
                    '<button type="button" class="btn btn-xs btn-link del_stock_out" style="margin-left:136px;margin-top:-24px">删除</button>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>';
                $("#stock_out_group").before(addAttr);
                $('#locationdown_form').bootstrapValidator('addField', 'goodsNum['+n+']', {
                    validators : {
                        notEmpty : {
                            message : '数量不能为空'
                        },
                        regexp: {
                            regexp:/^[0-9]*[1-9][0-9]*$/,
                            message:'请输入大于0的数'
                        }
                    }
                });
                //下拉框赋值
				var colorOption = $(".goods_color_selector:eq(0)").html();
                var sizeOption = $(".goods_size_selector:eq(0)").html();
                var locationNOOption = $(".location_no_selector:eq(0)").html();
                $("[name='goodsColor["+n+"]']").append(colorOption);
                $("[name='goodsSize["+n+"]']").append(sizeOption);
                $("[name='locationNO["+n+"]']").append(locationNOOption);
                //动态添加-绑定删除动作
                $('.del_stock_out').click(function() {
                    var roleName = $(this).parent("div").parent("div").prev().find('input[type]').attr("name");
                    $("#locationdown_form").bootstrapValidator('removeField',roleName);
                    $(this).parent("div").parent("div").parent("div").parent("div").remove();
                });
            });
        },
        //货物编号校验提示语
        locationDownGoodsCheck : function(goodsName){
            if(goodsName == ""){
                layer.alert('请输入正确的货物编号，以便获取货物名称、颜色、尺码等!', {
                    icon : 0
                });
                return true;
            }
            return false;
        },
        //条目校验提示语
        locationDownRowCheck : function(ret,line){
            if(ret == "2"){
                layer.alert('第'+line+'条信息,'+'仓库此货位没有该货物', {
                    icon : 0
                });
                return true;
            }else if(ret =="3"){
                layer.alert('第'+line+'条信息,'+'仓库此货位对应的货物数量不足', {
                    icon : 0
                });
                return true;
            }
            return false;
        }

    }

	$(function(){
		repositorySelectorInit();
        dataValidateInit();
        downDetailToggle();
		locationdownOperation();
		fetchStorage();
		goodsAutocomplete();
	})

	function dataValidateInit(){
		$('#locationdown_form').bootstrapValidator({
            message : 'This is not valid',
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            excluded : [ ':disabled' ],
			fields : {
                goodsNO : {
                    validators : {
                        notEmpty : {
                            message : '货物编号不能为空'
                        }
                    }
                },
                'goodsNum[0]' : {
					validators : {
						notEmpty : {
							message : '数量不能为空'
						},
                        regexp: {
                            regexp:/^[0-9]*[1-9][0-9]*$/,
                            message:'请输入大于0的数'
                        }
					}
				}
			}
		});
	}

	//货物信息自动匹配(输入货物编号)
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
						searchType : 'searchByNO',
                        repoID : $('#search_input_repository').val()
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
				locationdown_goods = ui.item.value;
				goodsInfoSet(locationdown_goods);
				//loadStorageInfo();
				return false;
			}
		})
	}

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

    function downDetailToggle(){
        $('#downDetail-show').click(function(){
            $('#downDetail').removeClass('hide');
            $(this).addClass('hide');
            $('#downDetail-hidden').removeClass('hide');
            locationDownManage.downLocationDetail();
        });

        $('#downDetail-hidden').click(function(){
            $('#downDetail').removeClass('hide').addClass('hide');
            $(this).addClass('hide');
            $('#downDetail-show').removeClass('hide');
        });
    }

	//仓库下拉列表初始化
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
                locationdown_repository = response.data[0].id
                locationNoSelectorInit()
            },
            error : function(response){
                $('#search_input_repository').append("<option value='-1'>加载失败</option>");
			}
			
		})
	}

    // 仓库货位下拉列表初始化
    function locationNoSelectorInit(){
        $('.location_no_selector').empty();
        $.ajax({
            type : 'GET',
            url : 'locationManage/getLocationList',
            dataType : 'json',
            contentType : 'application/json',
            data : {
                searchType : 'searchAll',
                keyWord : '',
                repoID : locationdown_repository,
                offset : -1,
                limit : -1
            },
            success : function(response){
                $.each(response.rows,function(index,elem){
                    $('.location_no_selector').append("<option value='" + elem.no + "'>" + elem.no +"号</option>");
                });
            },
            error : function(response){
                $('.location_no_selector').append("<option value='-1'>加载失败</option>");
            }

        })
    }

	function fetchStorage(){
		$('#search_input_repository').change(function(){
			locationdown_repository = $(this).val();
            locationNoSelectorInit()
			//loadStorageInfo();
		});
	}

	function loadStorageInfo(){
		if(locationdown_repository != null && locationdown_goods != null){
			$.ajax({
				type : 'GET',
				url : 'storageManage/getStorageListWithRepository',
				dataType : 'json',
				contentType : 'application/json',
				data : {
					offset : -1,
					limit : -1,
					searchType : 'searchByGoodsID',
					repositoryBelong : locationdown_repository,
					keyword : locationdown_goods
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
	function locationdownOperation(){
		$('#submit').click(function(){
            if(UnRepoAuthTip())return;
			// data validate
            if(locationDownManage.locationDownGoodsCheck($('#goods_name').val().trim())){
                return;
            }
			$('#locationdown_form').data('bootstrapValidator').validate();
			if (!$('#locationdown_form').data('bootstrapValidator').isValid()) {
				return;
			}
            //获取出库获取的明细
            var downGoodsDetail = "";
            $(".down-goods-detail").each(function(i,item){
                var goodsColor = $(item).find('.goods_color_selector').val()
                var goodsSize = $(item).find('.goods_size_selector').val()
                var goodsNum = $(item).find('.goods-num').val().trim()
                var locationNO = $(item).find('.location_no_selector').val()
                downGoodsDetail += goodsColor + "," + goodsSize + "," + goodsNum + "," + locationNO + ";"
            })
            data = {
                //goodsNO : $("#goods_no").val().trim(),
                goodsID : locationdown_goods,
                goodsName : $("#goods_name").val().trim(),
                goodsDetail : downGoodsDetail,
                repositoryID : locationdown_repository,
                remark : $("#remark").val().trim()
                // number : $('#stockin_input').val(),
            }

			$.ajax({
				type : 'POST',
				url : 'locationRecordManage/locationDown',
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
                        showMsg(type, msg, append);
                    }else{
                        if(response.data != ""){
                            var retAndLine = response.data.split(";");
                            var retCheck = retAndLine[0];
                            var line = retAndLine[1];
                            locationDownManage.locationDownRowCheck(retCheck,line);
                        }else{
                            type = 'error';
                            msg = '系统出现异常，货物出库失败'
                            showMsg(type, msg, append);
                        }
                    }
				},
				error : function(xhr, textStatus, errorThrown){
					// handle error
					handleAjaxError(xhr.status);
				}
			})
		});
	}

	function inputReset(){
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
        $(".down-goods-detail:gt(0)").each(function(i,item){
            var roleName = $(this).find('.goods-num').attr("name");
            $("#locationdown_form").bootstrapValidator('removeField',roleName);
            $(item).remove()
        })
        $('#downDetail-hidden').click()
        $('#remark').val('');

		$('#locationdown_form').bootstrapValidator("resetForm",true);
	}

</script>

<div class="panel panel-default">
	<ol class="breadcrumb">
		<li>货物出库</li>
	</ol>
	<div class="panel-body">
		<form class="form-inline" role="form" id="locationdown_form">
        <div class="row" style="margin-top: 10px">
            <div class="col-md-6 col-sm-6">
                <div class="row" style="width:760px">
                    <div class="col-md-1 col-sm-1"></div>
                    <div class="col-md-10 col-sm-11">
						<div class="form-group">
							<label for="" class="form-label">货物编号：</label>
							<input type="text" class="form-control" placeholder="请输入货物编号" id="goods_no" name="goodsNO">
						</div>
						<div class="form-group">
							<label for="" class="form-label">货物名称：</label>
							<input type="text" class="form-control" placeholder="请输入货物名称" id="goods_name" name="goodsName" readonly>
						</div>
					   <%-- <div class="form-group">
							<label for="" class="form-label">库存数量：</label>
							<input type="text" class="form-control" placeholder="请输入货物名称" id="goods_total_num" name="goodsTotalNum" readonly>
						</div>--%>
                    </div>
                </div>
                <div class="row visible-md visible-lg">
                    <div class="col-md-12 col-sm-12">
                        <div class='pull-right' style="cursor:pointer" id="downDetail-show">
                            <span>显示出库详情</span>
                            <span class="glyphicon glyphicon-chevron-down"></span>
                        </div>
                        <div class='pull-right hide' style="cursor:pointer" id="downDetail-hidden">
                            <span>隐藏出库详情</span>
                            <span class="glyphicon glyphicon-chevron-up"></span>
                        </div>
                    </div>
                </div>
                <div class="row hide" id="downDetail" style="margin-bottom:30px">
                    <div class="col-md-12 col-sm-12  visible-md visible-lg">
                        <div class="row">
                            <div class="col-md-1 col-sm-1"></div>
                            <div class="col-md-10 col-sm-10">
                                <label for="" class="text-info">出库货物明细</label>
                            </div>
                        </div>

						<div class="row" style="margin-top:5px;width:1500px">
							<div class="col-md-12 col-sm-11 down-goods-detail" style="margin-left:60px">
								<div class="col-md-6 form-group" style="width:20%">
									<label for=""  class="col-md-5 control-label" style="margin-top:6px">货物颜色：</label>
									<div class="col-md-7" style="margin-left: -40px;">
										<select name="goodsColor[0]" class="form-control goods_color_selector" style="width:100%;">
										</select>
									</div>
								</div>
								<div class="col-md-6 form-group" style="width:20%;margin-left:-70px">
									<label for="" class="col-md-5 control-label" style="margin-top:6px">货物尺码：</label>
									<div class="col-md-5" style="margin-left: -40px;">
										<select name="goodsSize[0]" class="form-control goods_size_selector" style="width:100%;">
										</select>
									</div>
								</div>
								<div class="col-md-6 form-group" style="width:20%;margin-left:-110px">
									<label for="" class="col-md-5 control-label" style="margin-top:6px">货物数量：</label>
									<div class="col-md-6" style="margin-left: -40px;">
										<input class="form-control goods-num" type="text" placeholder="货物数量" name="goodsNum[0]" style="width:110%;" />
									</div>
								</div>
								<div class="col-md-6 form-group" style="width:20%;margin-left:-80px">
									<label for="" class="col-md-5 control-label" style="margin-top:6px">货位编号：</label>
									<div class="col-md-6" style="margin-left: -40px;">
										<select name="locationNO[0]" class="form-control location_no_selector" style="width:120%;">
										</select>
									</div>
								</div>
							</div>
						</div>

                        <div style="margin-left:920px;margin-top:5px" id="stock_out_group">
                            <button type="button" class="btn btn-xs btn-link"  id="add_out_stock">添加</button>
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
						<div class="form-group">
							<label for="" class="form-label">仓库：</label>
							<select name="" id="search_input_repository" class="form-control">
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
							<label for="" class="form-label">备注：</label>
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
								<label for="" class="form-label">出库数量：</label>
								<input type="text" class="form-control" placeholder="请输入数量" id="locationdown_input" name="locationdown_input">
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
			<button class="btn btn-success" id="submit">提交出库</button>
		</div>
	</div>
</div>