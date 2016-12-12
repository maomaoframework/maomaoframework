<div class="col-md-12 col-sm-12 col-xs-12" id="singlepage">
	<div class="x_panel">
		<div class="x_title">
			<h2>
				服务器列表 <small>当前所添加的服务器</small>
			</h2>
			<ul class="nav navbar-right panel_toolbox">
				<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
				<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-wrench"></i></a>
			</ul>
			<div class="clearfix"></div>
		</div>

		<div class="x_content">
			<p>
				<button type="button" class="btn btn-primary" @click="addServer">
					<i class="fa fa-plus"></i> 添加新的服务器
				</button>
				添加服务器后，应用即可部署到这些服务器上． <span class="label label-danger">请注意：目前只能支持Linux操作系统服务器．</span>
			</p>

			<div class="table-responsive">
				<table class="table table-striped jambo_table bulk_action">
					<thead>
						<tr class="headings">
							<th class="column-title">名称</th>
							<th class="column-title">服务器IP地址</th>
							<th class="column-title">服务器端口号</th>
							<th class="column-title">账号</th>
							<th class="column-title">密码</th>
							<th class="column-title">操作</th>
						</tr>
					</thead>

					<tbody>
						<tr v-for="se in servers" class="even pointer">
							<td class="a-center ">\{{se.name}}</td>
							<td class=" ">\{{se.ip}}</td>
							<td class=" ">\{{se.port}}</td>
							<td class=" ">\{{se.account}}</td>
							<td class=" ">\{{se.password}}</td>
							<td class=" "><button type="button" class="btn btn-success" @click="testServer(se)"><i class="fa fa-play"></i> 测试</button><button type="button" class="btn btn-primary" @click="editServer(se)"><i class="fa fa-pencil"></i> 修改</button><button type="button" class="btn btn-danger"  @click="removeServer(se)"><i class="fa fa-trash"></i> 删除</button></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<!-- 添加一个服务器 -->
	<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">服务器信息</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal form-label-left input_mask" name="edit_instance" id="edit_instance">
						<input type="hidden" id="serverName" v-model="server.name">
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">名称</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="text" class="form-control" name="ip" 　required="required" id="server_name" v-model="server.name">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">IP地址</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="text" class="form-control" name="ip" 　required="required" id="server_ip" v-model="server.ip">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">端口号 </label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="number" class="form-control" name="port" required="required" data-validate-minmax="1024,65535" id="server_port" v-model="server.port">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">账号</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="text" class="form-control" name="account" id="server_account" v-model="server.account">
								<p>示例：root</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">密码</label>
							<div class="col-md-9 col-sm-9 col-xs-12">
								<input type="text" class="form-control" name="password" id="server_password" v-model="server.password">
							</div>
						</div>
						<div class="ln_solid"></div>
						<div class="form-group">
							<div class="col-md-9 col-sm-9 col-xs-12 col-md-offset-3">
								<button type="button" class="btn btn-success btn-do-commitInstance" @click="doAddServer">确定</button>
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
	$(document).ready(function() {
		$(":input").inputmask();
		var app = new Vue({
			el : '#singlepage',
			data : {
				servers : [],
				server : {
					key : '',
					name : '',
					ip : '',
					port : '',
					account : '',
					password : ''
				}
			},
			methods : {
				closeDialog : function(){
					this.server = {
							key : '',
							name : '',
							ip : '',
							port : '',
							account : '',
							password : ''
						}
					$(".bs-example-modal-lg").modal('hide');
				},
				
				// 删除服务器
				removeServer : function(server) {
					var _self = this;
					if (window.confirm('确定要删除此服务器吗？')) {
						Request.get('/apps/hdp/server_manager/data_removeServer?key=' + server.key, null, function(result) {
							if (result.success) {
								var index = _self.servers.indexOf(server);
								
								// 删除记录
								_self.servers.splice(index);
							} else {
								alert(result.message);
							}
						});
					}
				},

				// 测试连接
				testServer : function() {

				},

				// 添加一个服务器
				doAddServer : function() {
					if (this.server.name.length == 0){
						alert("必须填写名称");
						return false;
					}
					
					if (this.server.ip.length == 0){
						alert("必须填写IP地址");
						return false;
					}
					
					if (this.server.port.length == 0){
						alert("必须填写端口号");
						return false;
					}
					
					var _self = this;
					// 检查填写的内容
					if (this.servers && this.servers.length > 0) {
						this.servers.forEach(function(e, index) {
							if (e.port == _self.server.port && e.ip == _self.server.ip) {
								alert("IP地址或端口号与现有的服务器冲突，请修改！");
								return;
							}
							if (e.name == _self.server.name) {
								alert("与已有服务器名称冲突，请修改！");
								return;
							}
						});
					}

					// 提交表单
					Request.post('/apps/hdp/server_manager/data_commitServer', this.server, function(result) {
						if (result.success) {
							if (_self.server.key.length == 0) {
								_self.servers.push(result.RtnMsg);	
							} else {
								// 更新现有的对象
								_self.servers.forEach(function(el, index){
									if (el.key == result.key) {
										_self.servers[index] = result.RtnMsg;
									}
								});
							}
							_self.closeDialog();
						} else {
							alert(result.message);
						}
					});
					return false;
				},

				// 添加一个应用实例
				addServer : function() {
					$(".bs-example-modal-lg").modal();
				},

				// 编辑一个应用实例
				editServer : function(server) {
					// 弹出编辑对话框，修改实例信息
					this.server = server;
					$(".bs-example-modal-lg").modal();
				},
				
				/**
				 * 返回所有的服务器
				 */　
				loadServers : function() {
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
				this.loadServers();
			}
		});
	});
</script>
