<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<style type="text/css">
    .table tbody tr td{
        overflow: hidden;
        text-overflow:ellipsis;
        white-space: nowrap;
    }
    table{
        table-layout:fixed;
    }
</style>

<script>
    // 上下架记录查询参数
    search_type = 'searchAll'
    search_goods_no = ''
    search_goods_name = ''
    search_goods_color = ''
    search_goods_size = ''
    search_repository_id = ''
    search_person_id = ''
    search_start_date = null
    search_end_date = null

    $(function(){
        repositoryOptionInit();
        datePickerInit();
        //storageListInit();
        searchAction();
        fetchRepoAdmin();
    })

    // 仓库下拉框数据初始化
	function repositoryOptionInit(){
		$.ajax({
			type : 'GET',
			url : 'repositoryManage/getOwnRepo',
			dataType : 'json',
			contentType : 'application/json',
			success : function(response){
				$.each(response.data,function(index,elem){
					$('#search_input_repository').append("<option value='" + elem.id + "'>" + elem.id +"号仓库</option>");
				})
                if(UnRepoAuthTip())return;
                storageListInit();
			},
			error : function(response){
				// do nothing
                $('#search_input_repository').append("<option value='-1'>加载失败</option>");
			}
		});
	}

	// 日期选择器初始化
	function datePickerInit(){
		$('.form_date').datetimepicker({
			format:'yyyy-mm-dd',
			language : 'zh-CN',
			endDate : new Date(),
			weekStart : 1,
			todayBtn : 1,
			autoClose : 1,
			todayHighlight : 1,
			startView : 2,
			forceParse : 0,
			minView:2
		});
	}

	// 表格初始化
	function storageListInit() {
		$('#locationRecords')
				.bootstrapTable(
						{
							columns : [
									{
										field : 'recordID',
										title : '记录ID',
                                        //visible : false
									//sortable: true
									},
									{
										field : 'goodsNO',
										title : '商品编号'
									},
									{
										field : 'goodsName',
										title : '商品名称'
									},
									{
										field : 'goodsColor',
										title : '商品颜色'
									},
									{
										field : 'goodsSize',
										title : '商品尺码'
									},
									{
										field : 'goodsNum',
										title : '数量',
                                        sortable: true
									},
									{
										field : 'locationNO',
										title : '货位'
									},
									{
										field : 'time',
										title : '日期',
                                        width : 150,
                                        formatter: function (value, row, index) {
                                            return commonUtil.changeDateFormat(value);
                                        }
									},
                                    {
                                        field : 'repositoryID',
                                        title : '下/上架仓库ID',
                                        //visible : false
                                    },
									{
										field : 'personInCharge',
										title : '经手人'
									},
									{
										field : 'type',
										title : '记录类型'
									},
									{
										field : 'remark',
										title : '备注说明',
                                        width : 100,
                                        formatter : function (value, row, index) {
                                            return commonUtil.showParamDetail(row.remark)
                                        }
									}
									 ],
							url : 'locationRecordManage/searchLocationRecord',
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
		$('#locationRecords').bootstrapTable('refresh', {
			query : {}
		});
	}

	// 分页查询参数
	function queryParams(params) {
		var temp = {
			limit : params.limit,
			offset : params.offset,
			searchType : search_type,
            goodsNO : search_goods_no,
            goodsName : search_goods_name,
            goodsColor : search_goods_color,
            goodsSize : search_goods_size,
			repositoryID : search_repository_id,
            personID : search_person_id,
            startDate : search_start_date,
			endDate : search_end_date
		}
		return temp;
	}

	// 查询操作
	function searchAction(){
	    $('#search_button').click(function(){
            if(UnRepoAuthTip())return;
	        search_repository_id = $('#search_input_repository').val();
	        search_person_id = $('#search_person_ID').val();
	        search_type = $('#search_type').val();
	        search_start_date = $('#search_start_date').val();
	        search_end_date = $('#search_end_date').val();
	        tableRefresh();
	    })
	}

	//获取仓库负责人
	function fetchRepoAdmin(){
        $('#search_input_repository').change(function(){
            $.ajax({
                type : 'GET',
                url : 'repositoryAdminManage/getRepositoryAdminList',
                dataType : 'json',
                contentType : 'application/json',
                data:{
                    limit : -1,
                    offset : -1,
                    searchType : 'searchByRepositoryID',
                    keyWord : $('#search_input_repository').val()
                },
                success : function(response){
                    $('#search_person_ID').empty();
                    $('#search_person_ID').append('<option value="-1">所有负责人</option>');
                    $.each(response.rows,function(index,elem){
                        $('#search_person_ID').append("<option value='" + elem.id + "'>" + elem.name +"</option>");
                    })
                },
                error : function(response){
                    // do nothing
                }
            });
        });
    }
</script>

<div class="panel panel-default">
    <ol class="breadcrumb">
        <li>上下架记录</li>
    </ol>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-2">
                <form action="" class="form-inline">
                    <div class="form-group">
                        <label class="form-label">仓库编号：</label>
                        <select class="form-control" id="search_input_repository">
                            <shiro:hasRole name="systemAdmin">
                                <option value="-1">所有仓库</option>
                            </shiro:hasRole>
                        </select>
                    </div>
                </form>
            </div>
            <div class="col-md-2">
                <form action="" class="form-inline">
                    <div class="form-group">
                        <label class="form-label">仓库负责人：</label>
                        <select class="form-control" id="search_person_ID">
                            <option value="-1">所有负责人</option>
                        </select>
                    </div>
                </form>
            </div>
                <div class="col-md-2">
                    <form action="" class="form-inline">
                        <label class="form-label">记录过滤：</label>
                        <select name="" id="search_type" class="form-control">
                            <option value="searchAll">显示所有</option>
                            <option value="up">仅显示上架记录</option>
                            <option value="down">仅显示下架记录</option>
                        </select>
                    </form>
                </div>
            <div class="col-md-2">
                <button class="btn btn-success" id="search_button">
                    <span class="glyphicon glyphicon-search"></span> <span>查询</span>
                </button>
            </div>
        </div>
        <div class="row" style="margin-top:20px">
            <div class="col-md-6">
                <form action="" class="form-inline">
                    <label class="form-label">日期范围：</label>
                    <input class="form_date form-control" value="" id="search_start_date" name="" placeholder="开始日期">
                    <label class="form-label">&nbsp;&nbsp;-&nbsp;&nbsp;</label>
                    <input class="form_date form-control" value="" id="search_end_date" name="" placeholder="结束日期">
                </form>
            </div>
        </div>
        <div class="row" style="margin-top:50px">
            <div class="col-md-12">
                <table id="locationRecords" class="table table-striped" ></table>
            </div>
        </div>
    </div>
</div>