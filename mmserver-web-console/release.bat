@echo off
set "serverDir=D:\WS_Node\icloud-taxagency\fdp-dev-server"
mkdir "%serverDir%"
start /max "" "%serverDir%"
fis3 release development -wcd "%serverDir%"