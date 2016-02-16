<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>自营下载统计</title>
	<script src="${ctx}/static/js/util.js"></script>
	<script type="text/javascript">
		menu_flag = 'selfappdownload';
		var toolbar = "[{text : '导出EXCEL',iconCls : 'icon-undo',handler : exportExcel}]";
		var __ctxPath = "${ctx}";
		$(function(){
			loadDatagrid();
			$('#searchButton').click(function(){
				datagrid.datagrid('load',{
					"format" : "true",
					"Q_free" : 2,
					"Q_groupBy" : "theDate,appId",
					"Q_appName" : $('#Q_appName').val(),
					"Q_source" : $('#Q_source').val(),
					"Q_startTime" : $('#Q_startTime').val(),
					"Q_endTime" : $('#Q_endTime').val()
				});
			});
			$('#cancelSearchButton').click(function(){
				$('#Q_appName').val("");
				$('#Q_startTime').val('');
				$('#Q_endTime').val('');
				$('#Q_source').val(''),
				$("#Q_endTime").datepicker("option","minDate",null);
				$("#Q_startTime").datepicker("option","maxDate",null);
			});
		});
		
		function loadDatagrid(){
			datagrid = $("#app_detail_datagrid").datagrid({
				width:$("#rightbox").outerWidth(),
				height : $(".z_nav").outerHeight() - 52,
				nowrap : false,
				striped : true,
				collapsible : true,
				url : __ctxPath + '/appdownload/query',
				loadMsg : '数据加载.....',
				sortName : 'theDate',
				sortOrder : 'desc',
				remoteSort : false,
				pagination : true,
				rownumbers : true,
				fitColumns:true,
				showFooter : true,
				idField : 'id',
				queryParams : {
					"limit" : 10,
					"start" : 0,
					"format" : "true",
					"Q_free" : 2,
					"Q_groupBy" : "theDate,appId",
					'Q_appName' : $("#Q_appName").val(),
					'Q_source' : $("#Q_source").val(),
					"Q_startTime" : $('#Q_startTime').val(),
					"Q_endTime" : $('#Q_endTime').val()
				},
				columns : [[
					{field : 'theDate',title : '时间',width : 100,align : 'center'},
					{field : 'appType',title : '应用类型',width : 80,align : 'center',
						formatter:function(appType){
							if(appType == null){
								return "";
							}else if(appType == 2){
								return 'Apps';
							}else if(appType == 3){
								return 'Games';
							}else{
								return "Homes";
							}
						}
					},
					{field : 'appId',title : '应用编号',width : 80,align : 'center'},
					{field : 'appName',title : '应用名称',width : 200,align : 'center'},
					{field : 'categoryName',title : '应用分类',width : 100,align : 'center'},
					{field : 'source',title : '责任人',width : 100,align : 'center'},
					{field : 'downloadNum',title : '下载次数',width : 100,align : 'center'},
					{field : 'openNum',title : '浏览次数',width : 100,align : 'center'},
					{field : 'countryCn',title : '操作',width : 50,align : 'center',
						formatter:function(countryCn,rowData){
							if(null != countryCn && '' != countryCn){
								return "<a href='${ctx}/appdownload/showMoreSelf?theDate=" + rowData.theDate 
								+ "&appId=" + rowData.appId + "'>详情</a>";
							}else{
								return "";//此处为了屏蔽掉底部总计栏的详情url的显示.
							}
							
						}
					}]],
				toolbar : eval(toolbar),
				onLoadSuccess:function(data){
					//alert("load success !");
				}
			});
		}
		
		//导出
		function exportExcel(){
			var total = datagrid.datagrid('getPager').data("pagination").options.total;
			if(total > 100*1000){
				$.messager.alert("温馨提示","对不起，您导出的数据已经超过上限，请修改时间区间重新导出。");
				return false;
			}
			//导出调用
			var xmlName = "selfappdownload_groupby.xml";
			window.document.location = __ctxPath + "/export/export2Excel?xmlName="
					+ xmlName
					+ "&sort=theDate"
					+ "&Q_dataSource=MYSQL2"
					+ "&order=desc"
					+ "&format=true"
					+ "&Q_free=2"
					+ "&Q_groupBy=theDate,appId"
					+ "&Q_appName=" + $("#Q_appName").val()
					+ "&Q_source=" + $("#Q_source").val()
					+ "&Q_startTime=" + $('#Q_startTime').val()
					+ "&Q_endTime=" + $('#Q_endTime').val();
		}
	</script>
</head>
<body>
	
	<div class="easyui-panel" title="搜索" iconCls="icon-search" collapsible="true" doSize="true">
			<div class="panel_search">
			<label>开始日期 : </label><input id="Q_startTime" readonly="readonly" class="Wdate" style="width:130px;">
			<label>--&nbsp;结束日期 : </label><input id="Q_endTime" readonly="readonly" class="Wdate" style="width:130px;">
			<label>应用名称:</label>&nbsp;<input id='Q_appName' name='Q_appName' maxlength="50"/>
			<label>责任人:</label>&nbsp;<input id='Q_source' name='Q_source' maxlength="50"/>
			&nbsp;&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton"
				id="cancelSearchButton" name="cancelSearchButton" iconCls="icon-cancel">重置搜索</a>
			&nbsp;&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton"
				id="searchButton" name="searchButton" iconCls="icon-search">搜索</a>
			</div>
		</div>
	<div style="margin-top: 5px;">
	<table id="app_detail_datagrid" class="easyui-datagrid"></table>
	</div>
</body>
</html>