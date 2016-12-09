var fs = require('fs');

module.exports = {
	tasks : {},
	prefix : '',
	suffix : '',
	charset : 'utf-8',
	setPrefix : function(prefix) {
		this.prefix = prefix;
		return this;
	},
	setSuffix : function(suffix) {
		this.suffix = suffix;
		return this;
	},
	setCharset : function(charset){
		this.charset = charset;
		return this;
	},
	set : function(view, staticFile, compileFunction,
			dataFunction, time, data) {
		var prefix = this.prefix;
		var suffix = this.suffix;
		var charset = this.charset;
		var res = {};
		var req = {
			params : {},
			session : {},
		};
		if (data) {
			req.params = data;
		}
		res.render = function(arg1, data) {
			fs.readFile(prefix + view + suffix, charset,
					function(err, str) {
						if (!err) {
							var output = compileFunction(str, data);
							if (typeof staticFile === 'function') {
								staticFile(view, output);
							} else {
								fs.writeFileSync(staticFile, output);
								console
										.log('generated static file : '
												+ staticFile);
							}
						} else {
							console.log(err);
						}
					});
		};
		if (!time) {
			time = 30 * 60;
		}

		if (typeof dataFunction === 'function') {
			var generate = function() {
				dataFunction(req, res, {});
			};
			if (this.tasks[view]) {
				clearInterval(this.tasks[view]);
			}
			this.tasks[view] = setInterval(generate, time * 1000);
			console.log('add generater task { view : ' + view + ' , interval : '
					+ time + ' }');
			generate();
		}
	},
	add : function(view, staticFile, compileFunction,
			dataFunction, time, data){
		this.set(view, staticFile, compileFunction,
				dataFunction, time, data);
	} ,
	remove : function(view) {
		clearInterval(this.tasks[view]);
		delete tasks[view];
		console.log('remove generater task { view : ' + view + ' }');
	},
	clear : function() {
		Object.keys(this.tasks).forEach(function(view) {
			clearInterval(this.tasks[view]);
			delete tasks[view];
		});
		console.log('clear generater tasks');
	}
};
