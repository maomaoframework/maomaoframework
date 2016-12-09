<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- Meta, title, CSS, favicons, etc. -->
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>易泰应用管理平台 |</title>
<!-- Bootstrap -->
<link href="/static/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Font Awesome -->
<link href="/static/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
<!-- iCheck -->
<link href="/static/gentelella/vendors/iCheck/skins/flat/green.css" rel="stylesheet">
<!-- bootstrap-progressbar -->
<link href="/static/gentelella/vendors/bootstrap-progressbar/css/bootstrap-progressbar-3.3.4.min.css" rel="stylesheet">
<!-- jVectorMap -->
<link href="/static/biz/styles/jquery-jvectormap-2.0.3.css" rel="stylesheet" />

<!-- Custom Theme Style -->
<link href="/static/biz/styles/custom.min.css" rel="stylesheet">
</head>
<body class="nav-md" style="background: #F7F7F7">
	<div class="container body">
		<div id="content_main" role="main"　>
			<!-- top tiles -->
			<div id="singlepage">
				<div class="row tile_count">
					<!-- 访问人数 -->
					<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
						<span class="count_top"><i class="fa fa-user"></i> 每秒请求数(TPS)</span>
						<div class="count">2500</div>
						<span class="count_bottom"><i class="green">4% </i> qidong自今</span>
					</div>

					<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
						<span class="count_top"><i class="fa fa-clock-o"></i> 请求平均响应时间</span>
						<div class="count">123.50</div>
						<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>3% </i> From last Week</span>
					</div>
					<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
						<span class="count_top"><i class="fa fa-user"></i> 总并发数</span>
						<div class="count green">2,500</div>
						<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>34% </i> From last Week</span>
					</div>
					<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
						<span class="count_top"><i class="fa fa-user"></i> 超时响应次数</span>
						<div class="count">4,567</div>
						<span class="count_bottom"><i class="red"><i class="fa fa-sort-desc"></i>12% </i> From last Week</span>
					</div>
					<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
						<span class="count_top"><i class="fa fa-user"></i> 超时响应接口数</span>
						<div class="count">2,315</div>
						<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>34% </i> From last Week</span>
					</div>
					<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
						<span class="count_top"><i class="fa fa-user"></i> 失效应用</span>
						<div class="count">7,325</div>
						<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>34% </i> From last Week</span>
					</div>
				</div>
				<!-- /top tiles -->

				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="dashboard_graph">
							<!-- 曲线图 -->
							<div class="row x_title">
								<div class="col-md-6">
									<h3>
										应用TPS <small>曲线图</small>
									</h3>
								</div>
								<div class="col-md-6">
									<div id="reportrange" class="pull-right" style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc">
										<i class="glyphicon glyphicon-calendar fa fa-calendar"></i> <span>December 30, 2014 - January 28, 2015</span> <b class="caret"></b>
									</div>
								</div>
							</div>

							<div class="col-md-9 col-sm-9 col-xs-12">
								<div id="placeholder33" style="height: 260px; display: none" class="demo-placeholder"></div>
								<div style="width: 100%;">
									<div id="canvas_dahs" class="demo-placeholder" style="width: 100%; height: 270px;"></div>
								</div>
							</div>
							
							<div class="col-md-3 col-sm-3 col-xs-12 bg-white">
								<div class="x_title">
								  <h2>控制面板</h2>
								  <div class="clearfix"></div>
								</div>
								<div class="col-md-12 col-sm-12 col-xs-6">
									<div>
										<button type="button" class="btn btn-danger col-xs-12" @click="removeApp">
											<i class="fa fa-trash"></i> 卸载应用
										</button>
									</div>
									<div>
										<button type="button" class="btn btn-warning col-xs-12" @click="restartApp">
											<i class="fa fa-play"></i> 重启实例
										</button>
									</div>
									<div>
										<button type="button" class="btn btn-danger col-xs-12" @click="removeApp">
											<i class="fa fa-trash"></i> 更新应用
										</button>
									</div>
								</div>
				            </div>
							<div class="clearfix"></div>
						</div>
					</div>

				</div>
				<br />

				<div class="row">
					<div class="col-md-4 col-sm-4 col-xs-12">
						<div class="x_panel tile fixed_height_320">
							<div class="x_title">
								<h2>App 实例</h2>
								<ul class="nav navbar-right panel_toolbox">
									<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
									<li><a class="link" href="#" data-toggle="modal" data-target=".bs-example-modal-lg"><i class="fa fa-plus"></i></a></li>
								</ul>
								<div class="clearfix"></div>
							</div>
							<div class="x_content">
								<h4>当前部署的App实例</h4>
								<div class="widget_summary">
									<table class="table">
										<thead>
											<tr>
												<th>#</th>
												<th>实例</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<tr v-for="instance in hdpserver.instances" data="{{instance.ip}}:{{instance.port}}">
												<td scope="row">1</td>
												<td>{{instance.ip}}:{{instance.port}}</td>
												<td><span v-if="instance.runningStatus == 0">已停止</span> <span v-if="instance.runningStatus == 1">运行中</span></td>
												<td>
													<button type="button" class="btn btn-primary" title="编辑" @click="editInstance(instance)">
														<i class="fa fa-pencil"></i>
													</button>
													<button type="button" class="btn btn-primary" v-if="instance.runningStatus == 0" @click="startup(instance)" 　title="启动">
														<i class="fa fa-play-circle-o"></i>
													</button>
													<button type="button" class="btn btn-primary" v-if="instance.runningStatus == 1" @click="shutdown(instance)" title="停止" >
														<i class="fa fa-stop"></i>
													</button>
													<button type="button" class="btn btn-primary" @click="undeploy(instance)" title="删除" >
														<i class="fa fa-trash"></i>
													</button>
												</td>
											</tr>

										</tbody>
									</table>

									<div class="clearfix"></div>
								</div>
							</div>
						</div>
					</div>

					<!-- 数据库使用情况 -->
					<div class="col-md-4 col-sm-4 col-xs-12">
						<div class="x_panel tile fixed_height_320 overflow_hidden">
							<div class="x_title">
								<h2>数据库使用情况</h2>
								<ul class="nav navbar-right panel_toolbox">
									<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
									<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
										<ul class="dropdown-menu" role="menu">
											<li><a href="#">Settings 1</a></li>
											<li><a href="#">Settings 2</a></li>
										</ul></li>
								</ul>
								<div class="clearfix"></div>
							</div>
							<div class="x_content">
								<table style="width: 100%">
									<tr>
										<th style="width: 37%;">
											<p>Top 5</p>
										</th>
										<th>
											<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7">
												<p class="">Device</p>
											</div>
											<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">
												<p class="">Progress</p>
											</div>
										</th>
									</tr>
									<tr>
										<td>
											<canvas id="canvas1" height="140" width="140" style="margin: 15px 10px 10px 0"></canvas>
										</td>
										<td>
											<table class="tile_info">
												<tr>
													<td>
														<p>
															<i class="fa fa-square blue"></i>IOS
														</p>
													</td>
													<td>30%</td>
												</tr>
												<tr>
													<td>
														<p>
															<i class="fa fa-square green"></i>Android
														</p>
													</td>
													<td>10%</td>
												</tr>
												<tr>
													<td>
														<p>
															<i class="fa fa-square purple"></i>Blackberry
														</p>
													</td>
													<td>20%</td>
												</tr>
												<tr>
													<td>
														<p>
															<i class="fa fa-square aero"></i>Symbian
														</p>
													</td>
													<td>15%</td>
												</tr>
												<tr>
													<td>
														<p>
															<i class="fa fa-square red"></i>Others
														</p>
													</td>
													<td>30%</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>


					<!-- Redis使用情况 -->
					<div class="col-md-4 col-sm-4 col-xs-12">
						<div class="x_panel tile fixed_height_320">
							<div class="x_title">
								<h2>Redis使用情况</h2>
								<ul class="nav navbar-right panel_toolbox">
									<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
									<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
										<ul class="dropdown-menu" role="menu">
											<li><a href="#">Settings 1</a></li>
											<li><a href="#">Settings 2</a></li>
										</ul></li>
								</ul>
								<div class="clearfix"></div>
							</div>
							<div class="x_content">
								<div class="dashboard-widget-content">
									<ul class="quick-list">
										<li><i class="fa fa-calendar-o"></i><a href="#">Settings</a></li>
										<li><i class="fa fa-bars"></i><a href="#">Subscription</a></li>
										<li><i class="fa fa-bar-chart"></i><a href="#">Auto Renewal</a></li>
										<li><i class="fa fa-line-chart"></i><a href="#">Achievements</a></li>
										<li><i class="fa fa-bar-chart"></i><a href="#">Auto Renewal</a></li>
										<li><i class="fa fa-line-chart"></i><a href="#">Achievements</a></li>
										<li><i class="fa fa-area-chart"></i><a href="#">Logout</a></li>
									</ul>

									<div class="sidebar-widget">
										<h4>Profile Completion</h4>
										<canvas width="150" height="80" id="foo" class="" style="width: 160px; height: 100px;"></canvas>
										<div class="goal-wrapper">
											<span class="gauge-value pull-left">$</span> <span id="gauge-text" class="gauge-value pull-left">3,200</span> <span id="goal-text"
												class="goal-value pull-right">$5,000</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">应用实例</h4>
				</div>
				<div class="modal-body">
					<h4>Text in a modal</h4>
					<p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor.</p>
					<p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Donec sed odio dui. Donec
						ullamcorper nulla non metus auctor fringilla.</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary">Save changes</button>
				</div>

			</div>
		</div>
	</div>

	<!-- jQuery -->
	<script src="/static/gentelella/vendors/jquery/dist/jquery.min.js"></script>
	<!-- Bootstrap -->
	<script src="/static/gentelella/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="/static/gentelella/vendors/fastclick/lib/fastclick.js"></script>
	<!-- NProgress -->
	<script src="/static/gentelella/vendors/nprogress/nprogress.js"></script>
	<!-- Chart.js -->
	<script src="/static/gentelella/vendors/Chart.js/dist/Chart.min.js"></script>
	<!-- gauge.js -->
	<script src="/static/gentelella/vendors/bernii/gauge.js/dist/gauge.min.js"></script>
	<!-- bootstrap-progressbar -->
	<script src="/static/gentelella/vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
	<!-- iCheck -->
	<script src="/static/gentelella/vendors/iCheck/icheck.min.js"></script>
	<!-- Skycons -->
	<script src="/static/gentelella/vendors/skycons/skycons.js"></script>
	<!-- Flot -->
	<script src="/static/gentelella/vendors/Flot/jquery.flot.js"></script>
	<script src="/static/gentelella/vendors/Flot/jquery.flot.pie.js"></script>
	<script src="/static/gentelella/vendors/Flot/jquery.flot.time.js"></script>
	<script src="/static/gentelella/vendors/Flot/jquery.flot.stack.js"></script>
	<script src="/static/gentelella/vendors/Flot/jquery.flot.resize.js"></script>
	<!-- Flot plugins -->
	<script src="/static/biz/js/flot/jquery.flot.orderBars.js"></script>
	<script src="/static/biz/js/flot/date.js"></script>
	<script src="/static/biz/js/flot/jquery.flot.spline.js"></script>
	<script src="/static/biz/js/flot/curvedLines.js"></script>
	<!-- jVectorMap -->
	<script src="/static/biz/js/maps/jquery-jvectormap-2.0.3.min.js"></script>
	<!-- bootstrap-daterangepicker -->
	<script src="/static/biz/js/moment/moment.min.js"></script>
	<script src="/static/biz/js/datepicker/daterangepicker.js"></script>

	<!-- Custom Theme Scripts -->
	<script src="/static/biz/js/custom.min.js"></script>
	<!-- jVectorMap -->
	<script src="/static/biz/js/maps/jquery-jvectormap-world-mill-en.js"></script>
	<script src="/static/biz/js/maps/jquery-jvectormap-us-aea-en.js"></script>
	<script src="/static/gentelella/vendors/bootstrap/js/modal.js"></script>

	<script src="/static/biz/js/maps/gdp-data.js"></script>
	<script src="/static/biz/js/global.js"></script>
	<script src="/static/vue/vue/vue.js"></script>

	<script>
		$(document).ready(function() {
			var app = new Vue({
				el : '#singlepage',
				data : {
					hdpserver : {}
				},
				methods : {
					// 删除应用
					removeApp : function (){
						
					},
					
					// 重启应用
					restartApp : function (){
						
					},
					
					// 添加一个应用实例
					addInstance : function(instance) {
						return false;
					},

					// 编辑一个应用实例
					editInstance : function(event) {
					// 弹出编辑对话框，修改实例信息
					},

					// 启动一个实例
					startup : function(instance) {
						var insid = instance.ip + ":" + instance.port;
						var appid = Utils.getQueryString("appid");
						Request.get('/apps/hdp/appmanager/data_startup?insid=' + insid + "&appid=" + appid, null, function(result) {
							var ret = JSON.parse(result);
							if (ret.success) {
								alert("操作已完成，服务器将在５秒后自动停止");
							} else {
								alert(ret.message);
							}
						});
					},

					// 停止一个实例
					shutdown : function(instance) {
						if (window.confirm("你确定要关闭该实例吗？")) {
							var insid = instance.ip + ":" + instance.port;
							var appid = Utils.getQueryString("appid");
							Request.get('/apps/hdp/appmanager/data_shutdown?insid=' + insid + "&appid=" + appid, null, function(result) {
								var ret = JSON.parse(result);
								if (ret.success) {
									alert("操作已完成");
								} else {
									alert(ret.message);
								}
							});
						}
					},

					// 删除一个实例
					undeploy : function(instance) {
						if (window.confirm('你确定要删除该实例吗？')) {
							var insid = instance.ip + ":" + instance.port;
							var appid = Utils.getQueryString("appid");
							Request.get('/apps/hdp/appmanager/data_undeploy?insid=' + insid + "&appid=" + appid, null, function(result) {
								var ret = JSON.parse(result);
								if (ret.success) {
									alert("操作已完成");
								} else {
									alert(ret.message);
								}
							});
						}
					},

					getAppInstanceInfo : function() {
						// 获取所有的应用列表
						var _self = this;
						var currentAppId = Utils.getQueryString("appid");
						Request.get('/apps/hdp/appmanager/data_instancelist', null, function(result) {
							var jo = JSON.parse(result);
							if (jo != 'undefined' && jo.length > 0) {
								jo.forEach(function(el, index) {
									if (el.appId == currentAppId) {
										// 显示出来
										_self.hdpserver = el;
									}
								});
							}
						});
					}
				},
				ready : function() {
					this.getAppInstanceInfo();
				}
			});
		});
	</script>
</body>
</html>