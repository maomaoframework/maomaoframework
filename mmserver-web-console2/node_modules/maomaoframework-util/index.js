_ = require("underscore");

module.exports = {
		clone : function(toObj , cloneble){
			 for ( var p in cloneble ){ // 方法 
				  if ( typeof (cloneble [p]) != " function " ) { 
					  toObj[p] = cloneble [p];
				  } 
			 }
			 console.log(toObj);
		},
		
		/**
		 * 取得参数
		 */
		getParam : function (req, paramName){
			var params = {};
			req.query ? this.merge(req.query, params) : params;
			req.body ? this.merge(req.body, params) : params;
			return params[paramName];
		},
		
		/**
		 * 合并对象
		 */
		merge : function (oSource, oTarget){
			for ( var key in oSource) {
				if ((key in oTarget) && util.isObject(oSource[key])) {
					fnMergConf(oSource[key], oTarget[key]);
					continue;
				}
				oTarget[key] = oSource[key];
			}
		},

		/**
		 * 返回错误信息
		 * @param message -- 消息
		 * @param code --错误码
		 * @returns
		 */
		error : function(res, message, code){
			// 反馈错误给前端
			message = _.once(message); 
			code = _.once(code);
			
			var msg = { message : message , code : '-1' , success : false};
			res.json(msg);
		},
		
		isEmpty : function(str){
			return typeof(str) == "undefined" || str == null || str.length == 0 || str.replace(/(^\s*)|(\s*$)/g, "").length == 0;
		}
		
}