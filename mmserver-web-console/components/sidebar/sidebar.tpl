<ul>
<!-- {{#functions}}
	<li>
		<a href="{{EchoDataMap ../sysMap sysSystemId 'url'}}{{functionUrl}}"  data-function-key="{{functionKey}}" data-url="{{functionUrl}}">
			<i class="iconfont {{functionIcon}}"></i>{{name}}{{EchoDirective ../childMap id}}
		</a>
		{{EchoChildFunctionList ../childMap ../sysMap id}}
	</li>
{{/functions}} -->
<!-- <li>
	<a href="/apps/platform/customer/customer.html" data-url="customer/customer.html">
		<i class="iconfont"></i>客户管理
	</a>
</li>
<li>
	<a href="/apps/platform/customer/member.html" data-url="customer/member.html">
		<i class="iconfont"></i>客户成员管理
	</a>
</li>
<li>
	<a href="/apps/platform/customer/recylce.html" data-url="customer/recylce.html">
		<i class="iconfont"></i>回收站
	</a>
</li>
<li>
	<a href="/apps/bill/electronic/index.html" data-url="electronic/index.html">
		<i class="iconfont"></i>电子资料
	</a>
</li> -->
	<li class="active">
		<a href="/apps/platform/index.html" data-url="#">
			<i class="iconfont icon-homepage"></i><p>首页</p>
		</a>
	</li>
	<li>
		<a href="#" data-url="#">
			<i class="iconfont icon-bill"></i><p>票据</p>
		</a>
		<ul>
			<li><a href="/apps/bill/papery/index.html">收票</a></li>
			<li><a href="/apps/bill/salesIvc/index.html">采集</a></li>
			<li><a href="/apps/bill/voucherList/index.html">凭证清单</a></li>
		</ul>
	</li>
	<li>
		<a href="#" data-url="#">
			<i class="iconfont icon-customer-management"></i><p>客戶</p>
		</a>
		<ul>
			<li><a href="/apps/platform/customer/customer.html">客户管理</a></li>
			<li><a href="/apps/platform/customer/member.html">客户成员管理</a></li>
			<li><a href="/apps/platform/customer/recylce.html">回收站</a></li>
		</ul>
	</li>
	<li>
		<a href="#" data-url="#">
			<i class="iconfont icon-payments-account"></i><p>记账</p>
		</a>
		<ul>
			<li><a href="/apps/account/voucher/index.html">凭证</a></li>
			<li><a href="/apps/account/fixedAssets/index.html">固定资产</a></li>
			<li><a href="/apps/account/transfer/index.html">期末结转</a></li>
			<li><a href="/apps/account/books/index.html">账簿</a></li>
			<li><a href="/apps/account/report/index.html">报表</a></li>
		</ul>
	</li>
	<li>
		<a href="#" data-url="#">
			<i class="iconfont icon-newspaper-online-tax"></i><p>报税</p>
		</a>
	</li>
	<li>
		<a href="#" data-url="#">
			<i class="iconfont icon-set-01"></i><p>设置</p>
		</a>
	</li>
</ul>