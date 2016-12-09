<ul>
{{#functions}}
	<li>
		<a href="{{EchoDataMap ../sysMap sysSystemId 'url'}}{{functionUrl}}"  data-function-key="{{functionKey}}" data-url="{{functionUrl}}">
			<i class="iconfont {{functionIcon}}"></i>{{name}}{{EchoDirective ../childMap id}}
		</a>
		{{EchoChildFunctionList ../childMap ../sysMap id}}
	</li>
{{/functions}}
</ul>