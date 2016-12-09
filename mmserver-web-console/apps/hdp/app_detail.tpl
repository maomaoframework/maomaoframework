<div id="singlepage">
	<div class="row tile_count">
		<!-- 访问人数 -->
		<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
			<span class="count_top"><i class="fa fa-user"></i> 每秒请求数(TPS)</span>
			<div class="count">0</div>
			<span class="count_bottom"><i class="green">0% </i> qidong自今</span>
		</div>
		<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
			<span class="count_top"><i class="fa fa-clock-o"></i> 请求平均响应时间</span>
			<div class="count">0</div>
			<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>3% </i> From last Week</span>
		</div>
		<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
			<span class="count_top"><i class="fa fa-user"></i> 总并发数</span>
			<div class="count green">0</div>
			<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>34% </i> From last Week</span>
		</div>
		<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
			<span class="count_top"><i class="fa fa-user"></i> 超时响应次数</span>
			<div class="count">0</div>
			<span class="count_bottom"><i class="red"><i class="fa fa-sort-desc"></i>12% </i> From last Week</span>
		</div>
		<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
			<span class="count_top"><i class="fa fa-user"></i> 超时响应接口数</span>
			<div class="count">0</div>
			<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>34% </i> From last Week</span>
		</div>
		<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
			<span class="count_top"><i class="fa fa-user"></i> 失效应用</span>
			<div class="count">0</div>
			<span class="count_bottom"><i class="green"><i class="fa fa-sort-asc"></i>34% </i> From last Week</span>
		</div>
	</div>

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
							<button type="button" class="btn btn-primary col-xs-12" @click="removeApp">
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
						<li>
							<button type="button" class="btn btn-primary col-xs-12" @click="addInstance">
								<i class="fa fa-plus"></i> 创建实例
							</button>
						</li>
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
								<tr v-for="instance in hdpserver.instances" data="\{{instance.ip}}:\{{instance.port}}">
									<td scope="row">1</td>
									<td>\{{instance.ip}}:\{{instance.port}}</td>
									<td><span v-if="instance.runningStatus == 0">已停止</span> <span v-if="instance.runningStatus == 1">运行中</span></td>
									<td>
										<ul class="nav navbar-right panel_toolbox">
											<li><a @click="editInstance(instance)" class="collapse-link" 　title="编辑"><i class="fa fa-pencil blue"></i></a></li>
											<li><a v-if="instance.runningStatus == 0" @click="startup(instance)" 　title="启动" 　class="collapse-link"> <i class="fa fa-play-circle-o blue"></i>
											</a></li>
											<li><a v-if="instance.runningStatus == 1" @click="shutdown(instance)" class="collapse-link" 　 title="停止"> <i class="fa fa-stop blue"></i>
											</a></li>
											<li><a @click="undeploy(instance)" class="collapse-link" title="删除"> <i class="fa fa-trash red"></i>
											</a></li>
										</ul>
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
					<!-- <table style="width: 100%">
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
								</table> -->
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
					<!-- <div class="dashboard-widget-content">
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
					</div> -->
				</div>
			</div>
		</div>
	</div>

	<!-- 添加一个App实例 -->
	<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">扩展一个App实例</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal form-label-left input_mask" name="edit_instance" id="edit_instance">
						<input type="hidden" id="appIndex" v-model="instance.appIndex">
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">IP地址</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="text" class="form-control" name="ip" 　required="required" id="instance_ip" v-model="instance.ip">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">端口号 </label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="number" class="form-control" name="port" required="required" data-validate-minmax="1024,65535" id="instance_port" v-model="instance.port">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">JVM参数</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="text" class="form-control" name="jvm" id="instance_jvm" v-model="instance.jvm">
								<p>示例：-Xms1024m -Xmx2048m -XX:PermSize=256m -XX:MaxPermSize=512m</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">是否远程服务器</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<p>
									是<input type="radio" class="flat" value="true" @change="checkRemoteStyle" v-model="instance.remote" required /> 
									否: <input type="radio" class="flat" value="false" v-model="instance.remote" checked="true" @change="checkRemoteStyle" />
								</p>
								<p>
									<select class="form-control" style="display:none" id="selectServer" v-model="instance.sshServer">
										<option>-- 请选择一个远程服务器 --</option>
										<option v-for="se in servers" value="\{{se.key}}">\{{se.name}}</option>
									</select>
								</p>
							</div>
						</div>
						
						<div class="form-group" id="workingDirectory" style="display:none">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">远程服务器部署目录</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<p>
									<input type="text" class="form-control" name="jvm" id="instance_workingDirectory" v-model="instance.workingDirectory" placeholder="将hdp部署到哪个目录下">
								</p>
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">是否自动启动</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<p>
									是 <input type="radio" class="flat" name="enable" value="true" checked="true" v-model="instance.enable" required /> 
									否 <input type="radio" class="flat" name="enable" value="false" v-model="instance.enable" />
								</p>
							</div>
						</div>
						<div class="ln_solid"></div>
						<div class="form-group">
							<div class="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
								<button type="button" @click="doCommitInstance" class="btn btn-success btn-do-commitInstance">确定</button>
								<button type="button" class="btn btn-primary" @click='closeDialog'>取消</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	var appid = "{{appid}}";
	$(document).ready(function() {
		$(":input").inputmask();
		var app = new Vue({
			el : '#singlepage',
			data : {
				servers : [],
				hdpserver : {},
				instance : {
					appIndex : '',
					ip : '',
					port : '',
					jvm : '',
					enable : 'true',
					remote : 'false',
					sshServer : '',
					workingDirectory : ''
				}
			},
			methods : {
				closeDialog : function(){
					this.instance = {
							appIndex : '',
							ip : '',
							port : '',
							jvm : '',
							enable : 'true',
							remote : 'false',
							sshServer : '',
							workingDirectory : ''
						}
					$(".bs-example-modal-lg").modal('hide');
				},
				
				/**
				 * 检查是否远程服务器，并切换状态
				 */
				checkRemoteStyle : function(){
					if (this.instance.remote == 'true') {
						// 如果当前是远程服务器，则显示远程服务器配置情况
						$("#selectServer").show();
						$("#workingDirectory").show();
					} else {
						$("#selectServer").hide();
						$("#workingDirectory").hide();
					}
				},
				
				// 删除应用
				removeApp : function() {
					
				},

				// 重启应用
				restartApp : function() {

				},

				// 添加一个应用实例
				doCommitInstance : function() {
					if (this.instance.ip.length == 0){
						alert("必须填写IP地址");
						return false;
					}
					
					if (this.instance.port.length == 0){
						alert("必须填写端口号");
						return false;
					}
					
					// 如果指定了远程服务器，则这里必须选择远程服务器
					if (this.instance.remote == true && this.instance.sshServer.length == 0) {
						alert("请从远程服务器列表中选择一个服务器");
						return false;
					}
					if (this.instance.remote == true && this.instance.workingDirectory.length == 0) {
						if (!window.confirm("当前实例将运行在远程服务器上，你没有填写远程服务器目录，系统将默认使用/u01/hdp目录进行文件复制，如果你想使用其他目录，请填写远程目录位置．确定继续吗？")){
							return false;
						} else {
							this.instance.workingDirectory = "/u01/hdp";
						}
					}
					
					// 检查填写的内容
					if (this.hdpserver && this.hdpserver.instances && this.hdpserver.instances.length > 0) {
						var _self = this;
						this.hdpserver.instances.forEach(function(e, index) {
							if (e.port == _self.instance.port && e.ip == _self.instance.ip) {
								alert("IP地址或端口号与现有的应用实例冲突，请修改！");
								return;
							}
						});
					}

					// 提交表单
					this.instance.appid = appid;
					var _self = this;
					Request.post('/apps/hdp/appmanager/data_commitInstance', this.instance, function(result) {
						var ret = JSON.parse(result);
						if (ret.success) {
							alert(ret.message);
							_self.closeDialog();
						} else {
							alert(ret.message);
						}
					});
					return false;
				},

				// 添加一个应用实例
				addInstance : function() {
					$("#selectServer").hide();
					$(".bs-example-modal-lg").modal();
				},

				// 编辑一个应用实例
				editInstance : function(instance) {
					// 弹出编辑对话框，修改实例信息
					this.instance = instance;
					
					// 改变页面显示控件
					checkRemoteStyle();
					
					// 判断，如果当前所选的实例具备这个服务器，则显示这个服务器
					$(".bs-example-modal-lg").modal();
				},

				// 启动一个实例
				startup : function(instance) {
					var insid = instance.ip + ":" + instance.port;
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
					Request.get('/apps/hdp/appmanager/data_instancelist', null, function(result) {
						var jo = JSON.parse(result);
						if (jo != 'undefined' && jo.length > 0) {
							jo.forEach(function(el, index) {
								if (el.appId == appid) {
									// 显示出来
									_self.hdpserver = el;
								}
							});
						}
					});
				},
				
				/**
				 * 取得所有的SSH服务器信息
				 */
				getSSHServers : function(){
					// 获取所有的应用列表
					var _self = this;
					Request.get('/apps/hdp/server_manager/data_loadServers', null, function(result) {
						if (result != 'undefined' && result.length > 0) {
							_self.servers = result;
						}
					});
				}
			},
			ready : function() {
				this.getAppInstanceInfo();
				this.getSSHServers();
			}
		});
	});
</script>
</body>
</html>