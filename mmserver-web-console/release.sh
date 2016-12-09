#! /bin/sh

#--------------------------------------------
#
#  Release the project to directory.
#  Author :huxg
#--------------------------------------------

this_dir=`pwd`
dirname $0|grep "^/" >/dev/null
if [ $? -eq 0 ];then
    this_dir=`dirname $0`
else
    dirname $0|grep "^\." >/dev/null
    retval=$?
    if [ $retval -eq 0 ];then
        this_dir=`dirname $0|sed "s#^.#$this_dir#"`
    else
        this_dir=`dirname $0|sed "s#^#$this_dir/#"`
    fi
fi

SERVER_DIR="${this_dir%/*}/node_deploy"

fis3 release development -wcd "$SERVER_DIR"
