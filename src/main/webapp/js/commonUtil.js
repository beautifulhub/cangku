//实例化layer
var layer = "";
layui.use('layer', function () {
    layer = layui.layer;
});
var commonUtil={
    //转换日期格式(时间戳转换为datetime格式)
    changeDateFormat : function(cellval) {
        var dateVal = cellval + "";
        if (cellval != null) {
            var date = new Date(parseInt(dateVal.replace("/Date(", "").replace(")/", ""), 10));
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var currentDate = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();

            var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
            var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
            var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

            return date.getFullYear() + "-" + month + "-" + currentDate + " " + hours + ":" + minutes + ":" + seconds;
        }
    },
    // 货物信息自动匹配
    goodsAutocomplete :function(searchKey,repoID){
        var _this = this;
        var goodsCache = new Array();
        var searchType = ''
        if(searchKey == 'searchAll'){
            searchType = 'searchAll'
        }else if(searchKey == 'searchByGoodsNO'){
            searchType = 'searchByNO'
        }else if(searchKey == 'searchByGoodsName'){
            searchType = 'searchByName'
        }
        $("#search_input_type").autocomplete({
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
                        searchType : searchType,
                        repoID : repoID
                    },
                    success : function(data){
                        var autoCompleteInfo = new Array();
                        goodsCache = new Array();
                        $.each(data.rows, function(index,elem){
                            goodsCache.push(elem);
                            if(searchType == 'searchByNO'){
                                autoCompleteInfo.push({label:elem.no,value:elem.id});
                            }else if(searchType == 'searchByName'){
                                autoCompleteInfo.push({label:elem.name,value:elem.id});
                            }
                        });
                        response(autoCompleteInfo);
                    }
                });
            },
            focus : function(event, ui){
                $("#search_input_type").val(ui.item.label);
                return false;
            },
            select : function(event, ui){
                $("#search_input_type").val(ui.item.label);
                _this.goodsInfoSet(ui.item.value,goodsCache);
                return false;
            }
        })
    },
    // 填充货物详细信息
    goodsInfoSet : function(goodsID,goodsCache){
        var detailInfo;
        $.each(goodsCache,function(index,elem){
            if(elem.id == goodsID){
                detailInfo = elem;
                if(detailInfo.colors!=null){
                    $("#search_input_color").empty();
                    $('#search_input_color').append("<option value=''>所有颜色</option>");
                    var goodsColors = detailInfo.colors.replace(/，/ig,','); //统一将中英文逗号处理成英文
                    var colorsArr = goodsColors.split(",");
                    for(var i=0 ; i<colorsArr.length; i++){
                        $("#search_input_color").append("<option value='" + colorsArr[i] + "'>" + colorsArr[i] +"</option>");
                    }
                }
                if(detailInfo.sizes!=null){
                    $("#search_input_size").empty();
                    $('#search_input_size').append("<option value=''>所有尺码</option>");
                    var goodsSizes = detailInfo.sizes.replace(/，/ig,','); //统一将中英文逗号处理成英文
                    var sizesArr = goodsSizes.split(",");
                    for(var i=0 ; i<sizesArr.length; i++){
                        $("#search_input_size").append("<option value='" + sizesArr[i] + "'>" + sizesArr[i] +"</option>");
                    }
                }
            }
        })
    },
    //实现鼠标悬停，显示详细信息
    showParamDetail : function (param) {
        var values = param;//获取当前字段的值
        // 替换空格，因为字符串拼接的时候如果遇到空格，会自动将后面的部分截掉，所有这里用html的转义符
        // &nbsp;代替
        if(values){
            values = values.replace(/\s+/g,'&nbsp;');
            return "<span title="+values+">"+param+"</span>"
        }
    },
    //生成列表序号，便于查看
    tableIndexNum : function (index){
        var currentPage=$(".page-number.active").find('a').text();
        return Number(index+1+eval((currentPage-1)*$(".page-size").text()));
    }


}