#!/usr/bin/env python
# -*- coding: utf8 -*-

import os
from fabric.api import local,run, env, settings,cd,hide
from fabric.contrib.files import exists
from fabric.operations import get 
from fabric.operations import put


def _get_user_home():
    with settings(hide("running","stdout","stderr")):
        home = run('echo $HOME')
    return str(home)

def __backup__(dir_or_file):
    backup_dir = _get_user_home() + '/backup_dir'
    bn = os.path.basename(dir_or_file)
    if not exists(backup_dir):
        run('mkdir -p %s' % backup_dir)
    if exists("%s/%s.bk" % (backup_dir,bn)):
        run('rm -rf %s/%s.bk' % (backup_dir,bn))
    run('mv %s %s/%s.bk' % (dir_or_file,backup_dir,bn))

def installParserUI():
    app_home = _get_user_home() + "/lightparser"
    backup_dir = _get_user_home() + '/backup_dir/lightparser.bk'
    tmp_dir = app_home + "_new"
    if exists(tmp_dir):
        run("rm -rf " + tmp_dir)
    run("mkdir -p " + tmp_dir)
    put("./*",tmp_dir)
    if exists(app_home):
        stopParserUI()
#         __ctl__("serverctl.sh stop")
        __backup__(app_home)
    run("mv %s %s" % (tmp_dir,app_home))
    run("cp -r %s/data %s/data" % (backup_dir, app_home))
    startParserUI()
    #update_ltpconf()
    #__ctl__("serverctl.sh start")

def __ctl__(sc):
    app_home = _get_user_home() + "/lightparser"
    with cd(app_home + "/scripts"):
        run("setsid bash " + sc)

def __put_in_jar__(sc):
    app_home = _get_user_home() + "/lightparser"
    with cd(app_home):
        run("~/jdk/bin/jar uf *.jar " + sc)      

def startParserUI():
    #__put_in_jar__("spring/*")
    __ctl__("start.sh")

def stopParserUI():
    __ctl__("stop.sh")

def update_ltpconf():
    app_home = _get_user_home() + "/lightparser"
    ltp_home = _get_user_home() + "/ltp-server"
    run("cp -f %s/scripts/ltp.conf.* %s/conf" % (app_home,ltp_home))
    run("cp -f %s/scripts/UpdateDicts.py %s/." % (app_home,ltp_home))

