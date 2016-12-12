<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- Meta, title, CSS, favicons, etc. -->
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>MMServer Web Console|</title>

<link href="/gentelella/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="/gentelella/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
<link href="/gentelella/vendors/iCheck/skins/flat/green.css" rel="stylesheet">
<link href="/styles/jquery-jvectormap-2.0.3.css" rel="stylesheet" />
<link href="/gentelella/vendors/google-code-prettify/bin/prettify.min.css" rel="stylesheet">
<link href="/gentelella/vendors/select2/dist/css/select2.min.css" rel="stylesheet">
<link href="/gentelella/vendors/switchery/dist/switchery.min.css" rel="stylesheet">
<link href="/gentelella/vendors/starrr/dist/starrr.css" rel="stylesheet">
<link href="/styles/custom.min.css" rel="stylesheet">
</head>

<body class="nav-md">
	<div class="container body" id="_hdppage_">
		<div class="main_container">
			<div class="col-md-3 left_col">
				<div class="left_col scroll-view">
					<div class="navbar nav_title" style="border: 0;">
						<a href="index.html" class="site_title"><i class="fa fa-paw"></i> <span>MMServer</span></a>
					</div>

					<div class="clearfix"></div>

					<div class="profile">
						<div class="profile_pic">
							<img src="images/img.jpg" alt="..." class="img-circle profile_img">
						</div>
						<div class="profile_info">
							<span>Welcome,</span>
							<h2>John Doe</h2>
						</div>
					</div>
					<!-- /menu profile quick info -->

					<br />

					<!-- sidebar menu -->
					<div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
						<div class="menu_section">
							<h3>操作面板</h3>
							<ul class="nav side-menu">
								<li><a><i class="fa fa-home"></i> 应用管理 <span class="fa fa-chevron-down"></span></a>
									<ul class="nav child_menu">
										<li><a href="/console/appmanager/app_install" target="_frmContent_">安装新的应用</a></li>
										<!-- <li v-for="app in apps"><a href="/console/appmanager/app_detail.html?appid={{app.appid}}" target="_frmContent_">{{app.name}}</a></li>-->
										<li v-for="app in apps"><a href="/console/appmanager/app_detail?appid={{app.appid}}">{{app.name}}</a></li>
									</ul></li>
								<li><a href="/console/server_manager/index"><i class="fa fa-edit"></i> 服务器管理 </a></li>
							</ul>
						</div>
					</div>
					<!-- /sidebar menu -->

					<!-- /menu footer buttons -->
					<div class="sidebar-footer hidden-small">
						<a data-toggle="tooltip" data-placement="top" title="Settings"> <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
						</a> <a data-toggle="tooltip" data-placement="top" title="FullScreen"> <span class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span>
						</a> <a data-toggle="tooltip" data-placement="top" title="Lock"> <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
						</a> <a data-toggle="tooltip" data-placement="top" title="Logout"> <span class="glyphicon glyphicon-off" aria-hidden="true"></span>
						</a>
					</div>
					<!-- /menu footer buttons -->
				</div>
			</div>

			<!-- top navigation -->
			<div class="top_nav">
				<div class="nav_menu">
					<nav class="" role="navigation">
						<div class="nav toggle">
							<a id="menu_toggle"><i class="fa fa-bars"></i></a>
						</div>

						<ul class="nav navbar-nav navbar-right">
							<li class=""><a href="javascript:;" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false"> <img src="images/img.jpg"
									alt="">John Doe <span class=" fa fa-angle-down"></span>
							</a>
								<ul class="dropdown-menu dropdown-usermenu pull-right">
									<li><a href="javascript:;"> Profile</a></li>
									<li><a href="javascript:;"> <span class="badge bg-red pull-right">50%</span> <span>Settings</span>
									</a></li>
									<li><a href="javascript:;">Help</a></li>
									<li><a href="login.html"><i class="fa fa-sign-out pull-right"></i> Log Out</a></li>
								</ul></li>

							<li role="presentation" class="dropdown"><a href="javascript:;" class="dropdown-toggle info-number" data-toggle="dropdown" aria-expanded="false">
									<i class="fa fa-envelope-o"></i> <span class="badge bg-green">6</span>
							</a>
								<ul id="menu1" class="dropdown-menu list-unstyled msg_list" role="menu">
									<li><a> <span class="image"><img src="images/img.jpg" alt="Profile Image" /></span> <span> <span>John Smith</span> <span class="time">3
													mins ago</span>
										</span> <span class="message"> Film festivals used to be do-or-die moments for movie makers. They were where... </span>
									</a></li>
									<li><a> <span class="image"><img src="images/img.jpg" alt="Profile Image" /></span> <span> <span>John Smith</span> <span class="time">3
													mins ago</span>
										</span> <span class="message"> Film festivals used to be do-or-die moments for movie makers. They were where... </span>
									</a></li>
									<li><a> <span class="image"><img src="images/img.jpg" alt="Profile Image" /></span> <span> <span>John Smith</span> <span class="time">3
													mins ago</span>
										</span> <span class="message"> Film festivals used to be do-or-die moments for movie makers. They were where... </span>
									</a></li>
									<li><a> <span class="image"><img src="images/img.jpg" alt="Profile Image" /></span> <span> <span>John Smith</span> <span class="time">3
													mins ago</span>
										</span> <span class="message"> Film festivals used to be do-or-die moments for movie makers. They were where... </span>
									</a></li>
									<li>
										<div class="text-center">
											<a> <strong>See All Alerts</strong> <i class="fa fa-angle-right"></i>
											</a>
										</div>
									</li>
								</ul></li>
						</ul>
					</nav>
				</div>
			</div>

			<div id="content_main" class="right_col" role="main"　></div>

			<footer>
				<div class="pull-right">
					Maomaoframework Powered <a href="http://maomaoframework.org">Maomaoframework</a>
				</div>
				<div class="clearfix"></div>
			</footer>
			<!-- /footer content -->
		</div>
	</div>

	<!-- jQuery -->
	<script src="/gentelella/vendors/jquery/dist/jquery.min.js"></script>
	<!-- Bootstrap -->
	<script src="/gentelella/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="/gentelella/vendors/fastclick/lib/fastclick.js"></script>
	<!-- NProgress -->
	<script src="/gentelella/vendors/nprogress/nprogress.js"></script>
	<!-- Chart.js -->
	<script src="/gentelella/vendors/Chart.js/dist/Chart.min.js"></script>
	<!-- gauge.js -->
	<script src="/gentelella/vendors/bernii/gauge.js/dist/gauge.min.js"></script>
	<!-- bootstrap-progressbar -->
	<script src="/gentelella/vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
	<!-- iCheck -->
	<script src="/gentelella/vendors/iCheck/icheck.min.js"></script>
	<!-- Skycons -->
	<script src="/gentelella/vendors/skycons/skycons.js"></script>
	<!-- Flot -->
	<script src="/gentelella/vendors/Flot/jquery.flot.js"></script>
	<script src="/gentelella/vendors/Flot/jquery.flot.pie.js"></script>
	<script src="/gentelella/vendors/Flot/jquery.flot.time.js"></script>
	<script src="/gentelella/vendors/Flot/jquery.flot.stack.js"></script>
	<script src="/gentelella/vendors/Flot/jquery.flot.resize.js"></script>
	<script src="/gentelella/vendors/iCheck/icheck.min.js"></script>
	<!-- Flot plugins -->
	<script src="/js/flot/jquery.flot.orderBars.js"></script>
	<script src="/js/flot/date.js"></script>
	<script src="/js/flot/jquery.flot.spline.js"></script>
	<script src="/js/flot/curvedLines.js"></script>
	<!-- jVectorMap -->
	<script src="/js/maps/jquery-jvectormap-2.0.3.min.js"></script>

	<!-- jquery.inputmask -->
	<script src="/gentelella/vendors/jquery.inputmask/dist/min/jquery.inputmask.bundle.min.js"></script>

	<!-- bootstrap-daterangepicker -->
	<script src="/js/moment/moment.min.js"></script>
	<script src="/js/datepicker/daterangepicker.js"></script>

	<script src="/gentelella/vendors/autosize/dist/autosize.min.js"></script>

	<!-- Custom Theme Scripts -->
	<script src="/js/custom.min.js"></script>

	<!-- Flot -->
	<script>
		$(document).ready(
				function() {
					var data1 = [ [ gd(2012, 1, 1), 17 ], [ gd(2012, 1, 2), 74 ], [ gd(2012, 1, 3), 6 ], [ gd(2012, 1, 4), 39 ], [ gd(2012, 1, 5), 20 ],
							[ gd(2012, 1, 6), 85 ], [ gd(2012, 1, 7), 7 ] ];

					var data2 = [ [ gd(2012, 1, 1), 82 ], [ gd(2012, 1, 2), 23 ], [ gd(2012, 1, 3), 66 ], [ gd(2012, 1, 4), 9 ], [ gd(2012, 1, 5), 119 ],
							[ gd(2012, 1, 6), 6 ], [ gd(2012, 1, 7), 9 ] ];
					$("#canvas_dahs").length && $.plot($("#canvas_dahs"), [ data1, data2 ], {
						series : {
							lines : {
								show : false,
								fill : true
							},
							splines : {
								show : true,
								tension : 0.4,
								lineWidth : 1,
								fill : 0.4
							},
							points : {
								radius : 0,
								show : true
							},
							shadowSize : 2
						},
						grid : {
							verticalLines : true,
							hoverable : true,
							clickable : true,
							tickColor : "#d5d5d5",
							borderWidth : 1,
							color : '#fff'
						},
						colors : [ "rgba(38, 185, 154, 0.38)", "rgba(3, 88, 106, 0.38)" ],
						xaxis : {
							tickColor : "rgba(51, 51, 51, 0.06)",
							mode : "time",
							tickSize : [ 1, "day" ],
							//tickLength: 10,
							axisLabel : "Date",
							axisLabelUseCanvas : true,
							axisLabelFontSizePixels : 12,
							axisLabelFontFamily : 'Verdana, Arial',
							axisLabelPadding : 10
						},
						yaxis : {
							ticks : 8,
							tickColor : "rgba(51, 51, 51, 0.06)",
						},
						tooltip : false
					});

					function gd(year, month, day) {
						return new Date(year, month - 1, day).getTime();
					}
				});
	</script>
	<!-- /Flot -->

	<!-- jVectorMap -->
	<script src="/js/maps/jquery-jvectormap-world-mill-en.js"></script>
	<script src="/js/maps/jquery-jvectormap-us-aea-en.js"></script>
	<script src="/js/maps/gdp-data.js"></script>
	<script src="/js/global.js"></script>
	<script src="/vue/vue/vue.js"></script>
	<script>
		$(document).ready(function() {
			var sid = "{{sid}}";
			if (sid.length > 0) {
				$("#sidebar").show();
				$("#right").addClass("col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2");
			}

			var app = new Vue({
				el : '#_hdppage_',
				data : {
					apps : []
				},
				methods : {

				},
				ready : function() {
					$(".side-menu").on('click', ' a', function() {
						ajax_fill_page($(this).attr("href"), 'content_main');
						return false;
					});

					// 获取所有的应用列表
					var _self = this;
					Request.get('/console/appmanager/data_applist', null, function(result) {
						_self.apps = result;
					});
				}
			});
		});
	</script>
</body>
</html>
