/************************************
 *  admin的JS文件 
 *  Author:huxg
 *  Date:2016-10-06
 ***********************************/
$(document).ready(function(){
	$("#selChooseWixin").change(function(){ 
		 var sid = $(this).children('option:selected').val();
		 if (sid != "")
			 window.location.href = "/admin/index.do?sid=" + sid ;
	 });
});