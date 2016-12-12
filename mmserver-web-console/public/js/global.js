/**
 * 添加扩展
 */
String.prototype.endsWith = function(str) {
	if (typeof(this) != "string" )
		return;

	var reg = new RegExp(str + "$");
	return reg.test(this);
};

String.prototype.longToDate = function(pattern){
	var date = new Date(parseInt(this));
	return date.format(pattern);
};

String.prototype.toDate = function(separator) {
	if (typeof(this) != "string" )
		return;
	if (!separator) {
		separator = "-";
	}
	var dateArr = this.split(separator);
	var year = parseInt(dateArr[0]);
	var month;

	// 处理月份为04这样的情况
	if (dateArr[1].indexOf("0") == 0) {
		month = parseInt(dateArr[1].substring(1));
	} else {
		month = parseInt(dateArr[1]);
	}
	var day = parseInt(dateArr[2]);
	var date = new Date(year, month - 1, day);
	return date;
};

String.prototype.delHtmlTag = function() {
	if (typeof(this) != "string" )
		return;
	return this.replace(/<[^>]+>/g, "");// 去掉所有的html标记
};

String.prototype.delHtmlTag = function() {
	if (typeof(this) != "string" )
		return;
	return this.replace(/<[^>]+>/g, "");// 去掉所有的html标记
};

String.prototype.startWith = function(str) {
	if (typeof(this) != "string" )
		return;
	
	if (str == null || str == "" || this.length == 0 || str.length > this.length)
		return false;
	if (this.substr(0, str.length) == str)
		return true;
	else
		return false;
	return true;
};

String.prototype.toJson = function() {
	if (typeof(this) != "string" )
		return;
	
	return JSON.parse(this);
};

Date.prototype.format = function(fmt){
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}

/**
 * 显示错误信息对话框
 */
function showErrorDialog(message){
	var topwin = window.jQuery;
	topwin.modal({
		title: '出错了',
		content: message,
		buttons:{
		    '关闭' : function(modal) { modal.closeModal(); }
		}
	});
}

function showConfirmDialog(message,okFunction, cancelFunction){
	var html_ = '<div class="modal fade bs-example-modal-sm" id="confirmDialog" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">' + 
		    '<div class="modal-dialog modal-sm">' + 
		    '  <div class="modal-content">' + 
		    '    <div class="modal-header">' + 
		    '      <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span>' + 
		    '      </button>' + 
		    '      <h4 class="modal-title" id="myModalLabel2">操作确认</h4>' + 
		    '    </div>' + 
		    '    <div class="modal-body">' + 
		    '      <p>' + message + '</p>' + 
		    '    </div>' + 
		    '    <div class="modal-footer">' + 
		    '      <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>' + 
		    '      <button type="button" class="btn btn-primary btn-ok">确定</button>' + 
		    '    </div>' + 
		    '  </div>' + 
		    '</div>' + 
		'</div>';
	$(html_).appendTo($(document.body));
	$(html_).modal();
}

/*******************************************************************************
 * * 工具类 * 作者 胡晓光 * *
 ******************************************************************************/
var Utils = function() {
	return {
		// 判断当前是否来自于飞信
		isWeiXin : function() {
			var ua = navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == "micromessenger") {
				return true;
			} else {
				return false;
			}
		},

		/**
		 * 在登录后执行某个动作
		 */
		doAfterLogin : function(_func_) {
			// 发送同步请求
			$.ajax({
				type : "post",
				cache : false,
				async : true,
				url : '/login/islogin',
				dataType : "json",
				success : function(resultdata, textStatus) {
					if (resultdata.success == true) {
						if (typeof (_func_) != "undefined") {
							_func_();
						}
					} else {
						var expiresDate = new Date();
						expiresDate.setTime(expiresDate.getTime() - 10000);
						$.cookie('TXv8CYuoO0OCREMxPZ', '', {
							expires : expiresDate,
							path : '/'
						});
						$.cookie("_leshui_wx_user_", "", {
							expires : expiresDate,
							path : '/'
						});
						window.location.assign("/login")
					}
				}
			});
		},

		/**
		 * 取得form中所有input元素的值，并组合成json对象
		 */
		getFormJson : function(frm) {
			var o = {};
			var a = jQuery(frm).serializeArray();
			jQuery.each(a, function() {
				if (o[this.name] !== undefined) {
					if (!o[this.name].push) {
						o[this.name] = [ o[this.name] ];
					}
					o[this.name].push(this.value || '');
				} else {
					o[this.name] = this.value || '';
				}
			});
			return o;
		},

		/**
		 * 用于检测flash版本是否适合，是否需要升级
		 */
		isFlashVersionSuitable : function() {
			var hasFlash = 0; // 是否安装了flash
			var flashVersion = 0; // flash版本
			var isIE = 0; // 是否IE浏览器
			if (isIE) {
				try {
					var swf = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
					if (swf) {
						hasFlash = 1;
						flashVersion = swf.GetVariable("$version");
					}
				} catch (exception) {
					console.log(exception);
				}

			} else {
				if (navigator.plugins && navigator.plugins.length > 0) {
					var swf = navigator.plugins["Shockwave Flash"];
					if (swf) {
						hasFlash = 1;
						flashVersion = swf.description.split(" ");
					}
				}
			}

			if (hasFlash == 0) {
				Dialog
						.showErrorDialog(
								'插件错误',
								"非常抱歉，你使用的Microsoft IE浏览器尚未安装Flash Player播放器插件，因此无法观看课程。<br/>你可以点击<a href='/download/Adobe_Flash_Player_for_IE_19.0.0.207.exe' style='color:blue;'>此处</a>安装Flash Player播放器。我们强烈推荐使用<span style='color:red;'>谷歌浏览器</span>以获得更好的播放效果。");
				return false;
			}
		},

		/**
		 * 取得参数
		 */
		getQueryString : function(name) {
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
			var r = window.location.search.substr(1).match(reg);
			if (r != null)
				return unescape(r[2]);
			return "";
		},

		/**
		 * 产生一个随机数
		 */
		generateRandomCode : function() {
			var r = Math.random() * 100000;
			return r;
		},

		getContextPath : function() {
			var localObj = window.location;
			var contextPath = localObj.pathname.split("/")[1];
			if (contextPath == null || contextPath.length == 0)
				return contextPath;

			return contextPath;
		},

		/**
		 * 取得当前时间
		 * 
		 * @returns
		 */
		getCurrentTime : function() {
			var now = new Date();
			var hour = now.getHours();
			var minute = now.getMinutes();
			var second = now.getSeconds();
			return hour + ":" + minute + ":" + second;
		},

		/**
		 * 获取当前日期时间 yyyy-MM-dd HH:mm
		 */
		getCurrentDateTime : function() {
			var mon, day, now, hour, min, ampm, time, str, tz, end, beg, sec;
			day = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
			now = new Date();
			hour = now.getHours();
			min = now.getMinutes();
			sec = now.getSeconds();
			if (hour < 10) {
				hour = "0" + hour;
			}
			if (min < 10) {
				min = "0" + min;
			}
			if (sec < 10) {
				sec = "0" + sec;
			}

			y = now.getFullYear();
			m = now.getMonth() + 1;
			d = now.getDate();
			w = day[now.getDay()];
			return y + "-" + m + "-" + d + " " + hour + ":" + min + ":" + sec;
		},

		showWeixinQRCode : function() {
			Dialog.showDialog("/qrcode", "微信扫码");
		}
	};
}();

/**
 * 位置监听
 */
var UserLocationSniffer = function() {
	return {
		init : function() {
			area = $.cookie("user_area_name");
			area_id = $.cookie("user_area_id");

			// 如果不存在用户的区域信息，则为用户默认地区
			if (area == null || area_id == null) {
				Request.invok('/wbs/locateProvince',
						function(result) {
							if (result.success === true) {
								UserLocationSniffer.changeArea(
										result.region_id, result.region_name);
							}
						});
			}
		},
		changeArea : function(areaId, areaName, func) {
			var expiresDate = new Date();
			expiresDate.setTime(expiresDate.getTime() + 60 * 60 * 1000 * 24
					* 365);
			$.cookie("user_area_id", areaId, {
				expires : expiresDate,
				path : '/'
			});
			$.cookie("user_area_name", areaName, {
				expires : expiresDate,
				path : '/'
			});
			if ($("#_area_")) {
				$("#_area_").html(areaName);
			}
			
			if (typeof(func) == "undefined") {
				// 判断当前是否处于按照地区展示的页面
				window.location.reload();
			} else {
				func();
			}
		}
	};
}();


var Request = {
	post : function (url, params, _call_back_){
		return Request._ajax_('post', url, params, _call_back_);
	},

	get : function(url, params, _call_back_){
		return Request._ajax_('get', url,  params,  _call_back_);
	},
	/**
	 * 提交参数
	 * 
	 * @param sv
	 * @param params
	 *            js json对象
	 * @param _callback_
	 */
	_ajax_ : function(method, url, params, _callback_) {
		if (params != null) {
			if (typeof (params) == "string") {
				if (typeof (jQuery("#" + params).validationEngine) != "undefined") {
					if (!FormValidate.validate("validate")) {
						return;
					}
				}
				var _params_ = Utils.getFormJson(jQuery("#" + params));
				_params_["_rd_url_"] = window.location.href;
				var params = _params_;
				jQuery.ajax({
					type : method,
					cache : false,
					url : url,
					data : jQuery.param(params),
					dataType : "json",
					success : function(resultdata, textStatus) {
						if (_callback_) {
							_callback_(resultdata, textStatus);
						}
					}
				});
			} else {
				if (method == 'post') {
					_p_ = jQuery.param(params);
					jQuery.ajax({
						type : method,
						cache : false,
						dataType : 'json',
						data : _p_,
						url : url,
						success : function(result, textStatus) {
							if (typeof (_callback_) != "undefined") {
								_callback_(result);
							}
						}
					});
				} else if (method == 'get') {
					_p_ = jQuery.param(params);
					if (url.indexOf("?") > 0){
						url = url + "&" + _p_;
					} else {
						url = url + "?" + _p_;
					}
					jQuery.ajax({
						type : method,
						cache : false,
						dataType : 'json',
						url : url,
						success : function(result, textStatus) {
							if (typeof (_callback_) != "undefined") {
								_callback_(result);
							}
						}
					});
				}
				
			}
		} else {
			jQuery.ajax({
				type : method,
				cache : false,
				dataType : 'json',
				url : url,
				success : function(result, textStatus) {
					if (typeof (_callback_) != "undefined") {
						_callback_(result);
					}
				}
			});
		}
	}
};


var Buttons = function() {
	return {
		bindButtons : function(buttons) {
			tp = typeof (buttons);
			if (tp == "string") {
				cmd = "Buttons." + buttons;
				if (typeof (eval(cmd)) == "function") {
					fn = eval(cmd + "()");
					fn;
				}
			} else if (tp == "object") {
				if (buttons instanceof Array) {
					for ( var s in buttons) {
						cmd = "Buttons." + buttons[s];
						if (typeof (eval(cmd)) == "function") {
							fn = eval(cmd + "()");
							fn;
						}
					}
				}
			}

		},

		/**
		 * 退出登录
		 */
		btnLogout : function() {
			$(".btnLogout:not(.fnbtn-inited)").click(function() {
				Dialog.showConfirmDialog("确定要退出系统吗？", function() {
					Request.post('/login/logout', null, function(result) {
						var expiresDate = new Date();
						expiresDate.setTime(expiresDate.getTime() - 10000);
						$.cookie(result.asdfsd, result.zqccc, {
							expires : expiresDate,
							path : '/'
						});
						$.cookie("_leshui_wx_user_", "", {
							expires : expiresDate,
							path : '/'
						});
						window.location.assign(result.url);
					});
				});
				return false;
			}).addClass("fnbtn-inited");
		},

		/**
		 * 登录
		 */
		btnLogin : function() {
			$(".btnLogin:not(.fnbtn-inited)").click( function() {
						var act = $("#login-form").attr("action");
						Request.post(act, 'login-form', function(result, textStatus) {
							if (result.success == false){
								return false;
							} else {
//								var expiresDate = new Date();
//								expiresDate.setTime(expiresDate.getTime() + 60 * 60 * 1000 * 24 * 365);
//								$.cookie(returnval.asdfsd, returnval.zqccc, {
//									expires : expiresDate,
//									path : '/'
//								});
//								$.cookie("_leshui_wx_user_", Utils
//										.json2String(returnval.u), {
//									expires : expiresDate,
//									path : '/'
//								});
								window.location.assign(result.rdp);
							}
						});
					}).addClass("fnbtn-inited");

			$("#login-form").keypress(function(e) {
				if (e.which == 13) {
					$(".btnLogin").click();
				}
			});
		},

		/**
		 * 向下翻页按钮事件
		 */
		btnPageDown : function(container) {
			var unbindPageDown = $('#btnPageDown:not(.fnbtn-inited)');
			$.each(unbindPageDown, function(index, btn) {
				DownPageSplit.createPageSplit($(btn).attr("id"));
			});
			unbindPageDown.addClass("fnbtn-inited");
		},

		/**
		 * 刷新验证码
		 */
		authcode_reload : function() {
			$(".authcode_reload:not(.fnbtn-inited)").click(
					function() {
						var verify = document.getElementById('img_data');
						verify.setAttribute('src', '/SCS?v='
								+ Utils.generateRandomCode());
						return false;
					});
		}
	};
}();
//include页面
function ajax_fill_page(url,blockname){
	var random = Math.random() * 100000;;
	if(url.indexOf("?")>0){
		url+= "&v="+random;
	}else{
		url+="?v="+random;
	}
	jQuery.get(url,function(result){
		jQuery("#"+blockname).html(result)
	})
}
//格式化get请求的查询字符串
function ininGetQuery(params){
	var paramstring="";
	for(key in params){
		if(params.hasOwnProperty(key)){
			paramstring+=(key+"="+params[key]+"&");
		}
	}
	return paramstring;
};

$(document).ready(function() {
	$(window).scroll(function() {
		var scrollTop = $(window).scrollTop();
		if (scrollTop > 200) {
			$('.to-top').fadeIn();
		} else {
			$('.to-top').fadeOut();
		}
	});
	
	$("#_frmContent_").load(function(){
		var mainheight = jQuery(this).contents().find("body").height() + 0;
		jQuery(this).height(mainheight);
	}); 
	
	$(window.parent.document).find("#_frmContent_").load(function(){
		if (null != window.top) {
			var main = jQuery(window.top.document).find("#_frmContent_");
			var thisheight = jQuery(document).height()+0;
			main.height(thisheight);
		}
	});
	var headerLinks = jQuery(".section_title").children();
	$.each(headerLinks,function(s,val){
		jQuery(this).attr("target","_frmContent_");
		jQuery(this).click(function(){
			jQuery("#_hdp_navigator_").html(jQuery(this).attr("title"));
		});
		//showPage();
	});

	// 加载必要的js
	var context = Utils.getContextPath();
	if (context == null || context.length == 0 || "index" == context) {
		context = "index";
	}

	// 定位用户所在地
	//UserLocationSniffer.init();

	// 对页面上控制页面按钮的处理
	fnbtns = $(".fnbtn");
	if (fnbtns.length > 0) {
		$.each(fnbtns, function(index, ele) {
			allclass = $(ele).attr("class");
			classarray = allclass.split(" ");
			if (classarray.length > 0) {
				$.each(classarray, function(index, el) {
					fnname = "Buttons." + el;
					try {
						fn = eval(fnname);
						if (typeof (fn) == "function") {
							fn();
						}
					} catch (e) {

					}
				});
			}
		});
	}
	
//	// 自动加载必要的js
//	$.ajax({
//		url : "/static/biz/ls.wx.platform" + context + ".js",
//		dataType : "script",
//		cache : true
//	}).done(function() {
//		fnname = context + "." + action;
//		isError = true;
//		try {
//			fn = eval(fnname);
//			if (typeof (fn) == "function") {
//				isError = false;
//				fn();
//			}
//		} catch (e) {
//			console.error(e);
//		}
//		
//		if (isError == true){
//			fname = context + ".getfunc";
//			try {
//				fn2 = eval(context + ".getfunc");
//				if (typeof (fn2) == "function") {
//					handl = fn2();
//					if (handl != null)
//						handl();
//				}
//			} catch (e) {
//				console.error(e);
//			}
//		}
//	}).fail(function(jqXHR, textStatus) {
//	});
});