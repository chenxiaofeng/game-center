<%@page import="com.mas.rave.util.RandNum"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>消息推送</title>
<script type="text/javascript">
	var menu_flag = 'push';
	$(function(){
		var mode =${push.mode };
		if(mode==0){
			$("#te").hide();
		}else{
			$("#te").show();
		}
		
		var target =${push.target };
		if(target==0){
			$("#te1").hide();
		}else{
			$("#te1").show();
		}
		
		var action =${push.action };
		if(action==0){
			$("#te2").hide();
		}else{
			$("#te2").show();
		}
	});
	
	$(document).ready(function(){
	     var button1 = $('#picFile'), interval1;
	     new AjaxUpload(button1, {
				action: '${ctx }/push/uploadPic',
				name:'file1',
				//选择后自动开始上传
				autoSubmit:true,
				//返回Text格式数据
				responseType: 'json',
				data:{
					"flag":1
				},
				//上传的时候按钮不可用
				onSubmit : function(file,ext){
				   //设置允许上传的文件格式
				   if (!(ext && /^(jpg|jpeg|png|bmp|JPG|JPEG|PNG|BMP)$/.test(ext))){
					   $.alert('图片不合法,请重新输入');
				    // cancel upload
				    return false;
				    }
				   ajaxLoading();
				   },
				//上传完成后取得文件名filename为本地取得的文件名，msg为服务器返回的信息
			 onComplete:function (file, response) {  
				 ajaxLoadEnd();
				 if (response.flag==0) {  
					 $("#pic1").val(response.msg);
					 $("#pi").empty();
					$("#pi").append("<a href='${path}"+response.msg+"' target='_blank'>预览</a>");
				} else {  
					  $.alert('图片不合法,请重新输入');
				}  
				 fun1(2);
			 }}); 
	     var button2 = $('#icoFile'), interval2;
	     new AjaxUpload(button2, {
				action: '${ctx }/push/uploadPic',
				name:'file1',
				//选择后自动开始上传
				autoSubmit:true,
				//返回Text格式数据
				responseType: 'json',
				data:{
					"flag":0
				},
				//上传的时候按钮不可用
				onSubmit : function(file,ext){
				   //设置允许上传的文件格式
				   if (!(ext && /^(jpg|jpeg|png|bmp|JPG|JPEG|PNG|BMP)$/.test(ext))){
					   $.messager.alert('图片不合法,请重新输入');
				    // cancel upload
				    return false;
				    }
				   ajaxLoading();
				   },
				//上传完成后取得文件名filename为本地取得的文件名，msg为服务器返回的信息
			 onComplete:function (file, response) {  
				 ajaxLoadEnd();
				 if (response.flag==0) {  
					$("#icon1").val(response.msg);
					$("#ici").empty();
					$("#ici").append("<a href='${path}"+response.msg+"' target='_blank'>预览</a>");
				} else {  
					$.alert('图片不合法,请重新输入');
				}  
				 fun1(2);
			 }}); 
	});
	function ajaxLoading(){
	    $("<div class=\"datagrid-mask\" style=\"z-index:10000\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body");
	    $("<div class=\"datagrid-mask-msg\" style=\"z-index:10000\"></div>").html("正在处理，请稍候。。。").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});
	 }
	 function ajaxLoadEnd(){
	     $(".datagrid-mask").remove();
	     $(".datagrid-mask-msg").remove();            
	} 
</script>
</head>
<body>

	<div class="sitemap" id="sitemap">
		当前位置: <a href="list"><span id="childTitle"></span></a> -&gt; 修改消息推送
	</div>

	<div class="mainoutside mt18">
		<div id="secmainoutside">
			<div class="title">修改消息推送</div>
			<form id="editForm" action="" method="post" enctype="multipart/form-data">
				<div class="border">
					<table class="mainadd">
						<tbody>
							<tr>
								<td class="name">推送方式</td>
								<td class="content" >
									<input type="radio" id="mode" name="mode" value="0" onchange="ch(0)" <c:if test="${push.mode==0 }">checked="checked"</c:if>>文字&nbsp;&nbsp;
									<input type="radio" id="mode" name="mode" value="1" onchange="ch(1)" <c:if test="${push.mode==1 }">checked="checked"</c:if>>图片</td>
									<td class="content_info"></td>
							</tr>
							<tr id="te">
								<td class="name"></td>
								<td class="content">
									<input type="file" class="text" id="picFile" name="picFile" maxlength='150' /> 
									<span class="red">*</span>
									<input type="hidden" id="pic" name="pic" value="${push.pic }">
									<input type="hidden" id="pic1" name="pic1" value=""><br><br>
									${push.pic }
									<div id="pi"></div>
								</td>
								<td class="content_info"><span id="span_picFile"><a href='${path}${push.pic }' target='_blank'>查看原图</a>
								&nbsp;&nbsp;(仅支持.png、.jpg、.bmp格式。分辨率不大于760*312。不大于60KB)</span></td>
							</tr>
							<tr>
								<td class="name">通知标题</td>
								<td class="content">
									<input type="text" class="text" id="title" name="title" maxlength='20' value="${push.title }">
									<span class="red">*</span>
									</td>
								<td class="content_info">
									<span id="span_title">20字符</span>
								</td>
							</tr>
							<tr>
								<td class="name">通知内容</td>
								<td class="content">
									<textarea rows="10" cols="50" id="content" name="content">${push.content }</textarea>
									<span class="red">*</span></td>
								<td class="content_info">
									<span id="span_content">50字符</span>
								</td>
							</tr>
							<tr>
								<td class="name">推送版本</td>
								<td class="content">
									<input type="radio" id="target" name="target" value="0" onchange="ch1(0)" <c:if test="${push.target==0 }">checked="checked"</c:if>>所有版本 &nbsp;&nbsp;
									<input type="radio" id="target" name="target" value="1" onchange="ch1(1)" <c:if test="${push.target==1 }">checked="checked"</c:if>>指定版本
								</td>
								
							</tr>
							<tr id="te1">
								<td class="name"></td>
								<td class="content">
									<input type="text" class="text" id="versionName" name="versionName" maxlength="99" onkeyup="value=value.replace(/[^\w\.\,]/ig,'')" value="${push.versionName }" />
									<span class="red">*</span>
								</td>
								<td class="content_info">
									<span id="span_content">多个版本时用逗号进行分隔</span>
								</td>
							</tr>
							<tr >
								<td class="name">后续动作</td>
								<td class="content" colspan="2">
									<input type="radio" id="action" name="action" value="0" onchange="ch2(0)" <c:if test="${push.action==0 }">checked="checked"</c:if>>启动应用&nbsp;&nbsp;
									<input type="radio" id="action" name="action" value="1" onchange="ch2(1)" <c:if test="${push.action==1 }">checked="checked"</c:if>>打开链接
								</td>
							</tr>
							<tr id="te2">
								<td class="name">链接地址</td>
								<td class="content" >
									<input type="text" class="text" id="url" name="url" maxlength='99' value="${push.url }"/>
									<span class="red">*</span></td>
								<td class="content_info">
									请输入有效链接地址
								</td>
							</tr>
							<tr>
								<td class="name">推送时间</td>
								<td class="content">
									From:&nbsp;<input id='startString' name='startString' readonly="readonly" type="text" value="${push.startString }"
									 class="Wdate" style="width:150px" onClick="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss',minDate:'1999-01-01',maxDate:'#F{$dp.$D(\'endString\')||\'{%y+1}\'}'})" maxlength="30"/>
									To:&nbsp;
									 <input id='endString' name='endString' readonly="readonly" type="text" class="Wdate" style="width:150px" value="${push.endString }"
									 onClick="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'startString\')||\'1999-01-01\'}',maxDate:'{%y+1}'})" maxlength="30"/>	
								</td>
								<td class="content_info">
									<span id="span_startString">起始时间为必填</span>
									<span id="span_endString"></span>
								</td>
							</tr>
							<tr>
								<td class="name">上传图标</td>
								<td class="content"><input type="file" class="text" id="icoFile"
									name="icoFile" maxlength='150' /> <span class="red">*</span>
									<div id="ici"></div>
									</td>
								<td class="content_info"><span id="span_icoFile"></span></td>
							</tr>
							<tr>
								<td class="name">原图标地址</td>
								<td class="content">
									${push.icon }
									<input type="hidden" id="icon" name="icon" value="${push.icon }">
									<input type="hidden" id="icon1" name="icon1" value="">
								</td>
								<td class="content_info"><span id="span_picFile"><a href='${path}${push.icon }' target='_blank'>查看原图</a>
								&nbsp;&nbsp;(仅支持.png、.jpg、.bmp格式。分辨率不大于36*36。不大于5KB)</span></td>
							</tr>
							<tr>
								<td class="name">是否生效</td>
								<td class="content" colspan="2">
									<input type="radio" id="status" name="status" value="1" <c:if test="${push.status==1 }">checked="checked"</c:if>>是&nbsp;&nbsp;
									<input type="radio" id="status" name="status" value="0" <c:if test="${push.status==0 }">checked="checked"</c:if>>否
								</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td class="name"></td>
								<td class="btn"><input type="submit" class="bigbutsubmit"
									value="提交" id="butsubmit_id" /> <!--<input type="submit" class="bigbutsubmit" value="提交"/> -->
									<input type="button" class="bigbutsubmit"
									onclick="javascript:window.location.href='list';" value="返回" />
								</td>
							</tr>
						</tfoot>
					</table>
				</div>
			</form>
		</div>
	</div>
	<script type="text/javascript">
	function ch(va){
		if(va==0){
			$("#te").hide();
		}else{
			$("#te").show();
		}
	}
	function ch1(va){
		if(va==0){
			$("#te1").hide();
		}else{
			$("#te1").show();
		}
	}
	function ch2(va){
		if(va==0){
			$("#te2").hide();
		}else{
			$("#te2").show();
		}
	}
	$(document).ready(function(){
		$("#editForm").validate({
			rules: {
				"title": {
                    required: true,
                    maxlength: 20
          		},
          		"content": {
                    required: true,
                    maxlength: 50
          		},
          		"startString": {
                    required: true
          		},
          		"endString" :{
         			 required: true
         		}
          		
		},
		messages: {
				"title": {
					required: "通知标题不能为空",
	                maxlength: "名称最长不得超过20个字符"
				},
				"content": {
                    required: "通知内容不能为空",
                    maxlength: "通知内容最长不得超过50个字符"
          		},
				"startString": {
                    required: "超始时间不能为空"
          		},
          		"endString" :{
          			required: "结束时间不能为空"
         		}
			},
			errorPlacement: function(error, element){
				$("#span_" + element.attr("id")).html(error).attr("class", "error").addClass('red').next().hide();
			},
			submitHandler: function(form) {
				$("#butsubmit_id").attr("disabled","true");
				setDisabled('input[type="submit"]', window.document);
				$(form).ajaxSubmit({
					type : "POST",
					dataType : "json",
					 success: function(response){
		        		if(response.flag==0){
	            			$.alert('更新成功', function(){window.location.href = "list?currentPage=${page}";});
	            		}else if(response.flag==1){
	            			$.alert('图片不合法,请重新输入');
	                		$("#butsubmit_id").attr("disabled","false");
	            		}else if(response.flag==2){
	            			$.alert(response.msg);
	                		$("#butsubmit_id").attr("disabled","false");
	            		}else{
	                		$.alert('更新失败,请重新输入');
	                		$("#butsubmit_id").attr("disabled","false");
	            		}
		        		setabled('input[type="submit"]', window.document);
		            }
	            });
			},
			success: function(label){
				var labelparent = label.parent();
				label.parent().html(labelparent.next().html()).attr("class", "valid");
			},
			onkeyup:false
		});
	});
	</script>
</body>
</html>