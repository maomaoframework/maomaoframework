@echo off
If exist "%cd%\fis-conf.js" (
  @echo "-----------------------------------------------------"
  @echo "ע�⣺��ǰĿ¼Ϊ������Ŀ¼�����������С�start.bat����ֻ���ڡ�����Ŀ¼���·���ִ�С�start.bat�ļ���"
  @echo "-----------------------------------------------------"
  @echo "�������²�����в���"
  @echo "1. �޸�release.bat�ļ��еķ���·����serverDir��"
  @echo "2. ����release.bat�ļ�������Ŀ��������ɺ�����ر�dos���ڣ��Ա�fis�ܹ������ļ��仯��ʵʱ����"
  @echo "3. �ڷ���Ŀ¼(release.bat�ļ��еġ�serverDir������ָ��)��ִ��install.bat�ļ����԰�װ����ģ��"
  @echo "4. ��װ���֮��ִ��start.bat�ļ���������������"
  @echo "-----------------------------------------------------"
  pause
  goto end
)

start nodemon -e js,tpl --debug=5860 bin/www

:end