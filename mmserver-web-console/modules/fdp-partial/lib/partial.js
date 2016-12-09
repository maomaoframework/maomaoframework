var hbs = require('hbs');

partial = module.exports = {
	prefix : '',
	partials : {},
	put : function(name, path,callback) {
		this.partials[path] = name;
		hbs.registerPartial(partial.prefix	+ name, '');
		callback(path, function(view, result) {
			hbs.registerPartial(partial.prefix + partial.partials[view], result);
		});
		return this;
	},
	setPrefix : function(prefix) {
		this.prefix = prefix;
		return this;
	}
};