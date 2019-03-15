<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<title>Oracle导入</title>
<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
<script src="bootstrap/js/jquery-1.10.2.min.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
	<%@include file="header.jsp" %>
	<div class="col-md-12">
		
		<div class="form-horizontal" >
			<p style="color:red;">Excel文件从第二行开始导入，请在导入的Excel文件中附带标题！</p>
			
			<div class="form-group">
				<label class="col-sm-2 control-label" >导入到表：</label>
				<div class="col-sm-4">
					<input class="form-control" id="table_name" type="text" />
				</div>
				<div class="col-sm-2">
					<button class="btn btn-primary" onclick="get_table_columns()" id="chaxun_tbn">查询</button>
				</div>	
			</div>
			<div class="form-group">
				<label class="col-sm-2 control-label">选择导入文件：</label>
				<div class="col-sm-4">
					<input type="file" name="uploadFile" id="file">
				</div>
				<div class="col-sm-2">
					<p><button class="btn btn-primary" onclick="upload1()" id="sc_btn1" disabled="disabled">点击上传</button></p>
					<span id="shangchuan_wait" style="color:red;display:none;">正在解析excel,请稍候...</span>
				</div>
				<div class="col-sm-4 pull-left">
					<span class="glyphicon glyphicon-ok" style="font-size:20px;color:green;display:none;" id="sc_flag1"></span>
				</div>
			</div>
		</div>
	</div>
	<!-- style="border:1px dashed #000" -->
	<div class="table-responsive col-sm-12" >
		<table id="table_column" class="table table-bordered table-condensed "></table>
		<span id="file_tishi" style="display:none;"></span>
	</div>
	<div class="row" id="div_btn" style="display:none;">
		<div class="col-sm-6 col-xs-6"><button class="btn btn-danger pull-right" onclick="reset()">重置</button></div>
		<div class="col-sm-6 col-xs-6"><button class="btn btn-primary" onclick="tijiao()" id="tijiao_btn" disabled="disabled">确认无误提交</button></div>
	</div>
	<div class="row" id="span_info" style="display:none;">
		<div class="col-sm-12 col-xs-12"><span style="color:red;font-size:24px;font-weight:bold;">后台正在导入数据表，请勿刷新当前页面！</span></div>
	</div>
	<input style="display:none;" id="filePath">
	<input style="display:none;" id="columns_num">
</div>
<script>
//导航条激活
var table_columns_num = 0;
$("#index").attr("class", "active");
$(function (){
	//get_upload_list();
	//setInterval(get_upload_list,5000);
});

function upload1() {
	var columns_num = $("#columns_num").val();
	if (columns_num == null || columns_num == ""){
		alert("请先查询表结构！");
		return false;
	}
	var formData = new FormData();
	formData.append("myfile",document.getElementById("file").files[0]);
	$("#shangchuan_wait").show();
	$("#sc_btn1").addClass('disabled');
	$.ajax({  
		type: "POST",  
        url:"/BigExcelInsert/DownloadServlet?action=excel_upload", 
        dataType: "json",
        data:formData,
        async: true,
        cache: false,
        contentType: false,
        processData: false,
        error: function(request) {  
           alert("Connection error:"+request.error);
           $("#sc_flag1").show();
        },  
        success: function(data) {
        	$("#shangchuan_wait").hide();
        	$("#sc_btn1").removeClass('disabled');
			if(data.code == -1){
				alert("文件格式不匹配");
			}else if(data.code == 0) {
				alert("Excel读取失败")
			}else if(data.code == 1) {
				alert("表字段数和Excel列数不一样")
			}else if(data.code == 200) {
				//alert("1111");
				$("#sc_flag1").show();
				var rowcount = data["rowCount"];
				$("#file_tishi").html("上传的Excel文件共计"+rowcount+"行数据，请根据前10行核对是否正确。正确请“点击确认无误”提交");
				$("#file_tishi").show();
				var objs = eval(data);
				var obj_list = objs["list"];
				$("#filePath").val(objs["filePath"]);
	            var table = $("#table_column");
	            var str = "";
				for (var i = 0; i < obj_list.length; i++) {
					//alert(JSON.stringify(obj_list[i]));
					str += "<tr align=center>";
					var j = 1;
					for(var key in obj_list[i]){
						var col = "A"+j;
						str += "<td>"+obj_list[i][col]+"</td>";
						j++;						
					}
					str += "</tr>";					
				}
				table.append(str);
				$("#tijiao_btn").removeAttr("disabled");
	     	}
        }  
    });
}

function get_table_columns() {
	var table_name = $("#table_name").val();
	$("#table_column").empty();
	//$("#chaxun_btn").
	$.ajax({
		type : "post",
		url : "/BigExcelInsert/QueryServlet",
		dataType : "json",
		data : {
			action : "get_table_columns",
			table_name : table_name
		},
		success : function(json){
			var code = json.code;
			if (code != 200) {
				alert("未查询到该表信息！");
			}else{
				var objs = eval(json);
				var obj_list = objs["list"];
				var table = $("#table_column");
				table.empty();
				var str_td = "";
				var str_td2 = "";
				//alert(obj_list.length);
				table_columns_num = obj_list.length;
				$("#columns_num").val(obj_list.length);
				//alert(table_columns_num);
				for (var i = 0; i < obj_list.length; i++) {
					str_td += "<td>"+obj_list[i].column_name+"</td>";
					str_td2 += "<td>"+(i+1)+"</td>";
				}
				table.append("<thead><tr align=\"center\">" + str_td2 +"</tr>");
				table.append("<tr align=\"center\">" + str_td +"</tr></thead>");
				$("#div_btn").show();
				$("#sc_btn1").removeAttr("disabled");
			}
		}
	});
}

function reset() {
	location.reload();
}

function tijiao() {
	var filePath = $("#filePath").val();
	var table_name = $("#table_name").val();
	var columns_num = $("#columns_num").val();
	if (filePath == null || filePath == ""){
		alert("请先上传Excel文件！");
		return false;
	}
	$("#span_info").show();
	$("#tijiao_btn").addClass('disabled');
	//$("#tijiao_btn").prop('disabled', true);
	$.ajax({
		type : "post",
		url : "/BigExcelInsert/QueryServlet",
		dataType : "json",
		data : {
			action : "update_table",
			filePath : filePath,
			table_name : table_name,
			columns_num : columns_num
		}, 
		success : function(json){ 
			var code = json.code;
			if (code != 200) {
				alert("插入数据出错，错误提示："+json.message);
			}else{
				alert("插入成功!"+json.message);
				location.reload();
			}
		}
	});
}
</script>
</body>
</html>