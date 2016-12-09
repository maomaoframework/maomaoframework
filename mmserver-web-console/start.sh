#! /bin/sh

useage (){
	echo "-----------------------------------------------------"
	echo "注意：当前目录为“开发目录”，不能运行“start.bat”，只有在“运行目录”下方可执行“start.bat文件”"
	echo "-----------------------------------------------------"
	echo "请根据以下步骤进行操作"
	echo "1. 修改release.bat文件中的发布路径“serverDir”"
	echo "2. 运行release.bat文件发布项目，发布完成后请勿关闭dos窗口，以便fis能够监听文件变化，实时发布"
	echo "3. 在发布目录(release.bat文件中的“serverDir”变量指定)下执行install.bat文件，以安装依赖模块"
	echo "4. 安装完成之后执行start.bat文件，以启动服务器"
	echo "-----------------------------------------------------"

}


nodemon -e js,tpl --debug=5860 ./bin/www


