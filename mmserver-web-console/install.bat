@echo off
If exist "%cd%\fis-conf.js" (
  @echo "-----------------------------------------------------"
  @echo "ע�⣺��ǰĿ¼Ϊ������Ŀ¼�����������С�install.bat����ֻ���ڡ�����Ŀ¼���·���ִ�С�install.bat�ļ���"
  @echo "-----------------------------------------------------"
  @echo "��������²�����в���"
  @echo "1. �޸�release.bat�ļ��еķ���·����serverDir��"
  @echo "2. ����release.bat�ļ�������Ŀ��������ɺ�����ر�dos���ڣ��Ա�fis�ܹ������ļ��仯��ʵʱ����"
  @echo "3. �ڷ���Ŀ¼(release.bat�ļ��еġ�serverDir������ָ��)��ִ��install.bat�ļ����԰�װ����ģ��"
  @echo "4. ��װ���֮��ִ��start.bat�ļ���������������"
  @echo "-----------------------------------------------------"
  pause
  goto end
)
start npm install --registry=https://registry.npm.taobao.org
mkdir node_modules
cd node_modules
set work_dir=%cd%
set command = npm install --registry=https://registry.npm.taobao.org
echo %work_dir%
for /f "delims=" %%i in ('dir /ad/b "%work_dir%" fdp-*') do (
  cd %%i
  echo npm install --registry=https://registry.npm.taobao.org>install.bat
  install.bat
  cd %work_dir%
)
pause;