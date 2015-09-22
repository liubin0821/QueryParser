#!/bin/bash
export LANG=zh_CN.UTF-8

function usage(){
    echo "Usage: $0 dicts|dicts_conf|data|stock|index|indexgroup|keywordgroup|synt|semantic"
    echo "    dicts        : copy dicts & restart dict"
	echo "                 : 拷贝词典 & 重启分词服务"
	echo "    dicts_conf   : copy dicts' conf & restart dict"
	echo "                 : 拷贝词典配置文件 & 重启分词服务"
    echo "    data         : copy data & restart service"
	echo "                 : 拷贝data配置文件 & 重启服务"
    echo "    stock        : copy stock's data & restart service"
	echo "                 : 拷贝stock配置文件 & 重启服务"
	echo " back_stage_conf : copy back_stage's data & restart service"
	echo "                 : 拷贝后台配置文件 & 重启服务"
    echo "    index        : copy index's data & restart service"
    echo "                 : 拷贝指标配置文件 & 重启服务"
	echo "    indexgroup   : copy indexgroup's data & restart service"
	echo "                 : 拷贝指标词组配置文件 & 重启服务"
	echo "    keywordgroup : copy keywordgroup's data & restart service"
	echo "                 : 拷贝关键词组配置文件 & 重启服务"
	echo "    synt         : copy syntactic's data & restart service"
	echo "                 : 拷贝句式配置文件 & 重启服务"
	echo "    semantic     : copy semantic's data & restart service"
	echo "                 : 拷贝语义配置文件 & 重启服务"
	echo "    file         : copy file & restart service"
	echo "                 : 拷贝配置文件 & 重启服务"
	echo "    dict         : copy dict & restart service"
	echo "                 : 拷贝词典 & 重启分词服务"
    exit 0
}

function dicts() {
    echo "starting copy dicts & restart dict... "
	echo "开始拷贝词典 & 重启分词服务... "
	fab update_dicts
	fab restart_ltp
}

function dicts_conf() {
    echo "starting copy dicts'conf & restart dict... "
	echo "开始拷贝词典配置文件 & 重启分词服务... "
	fab update_dict_conf
	fab restart_ltp
}
function data() {
	echo "starting copy dicts & restart dict... "
	echo "开始拷贝词典配置文件 & 重启分词服务... "
	fab update_dicts
	fab restart_ltp
    echo "starting copy data & restart service... "
	echo "开始拷贝data配置文件 & 重启服务... "
	fab update_data
	reload_lightparser
}

function stock() {
	echo "starting copy dicts & restart dict... "
	echo "开始拷贝词典配置文件 & 重启分词服务... "
	fab update_dicts
	fab restart_ltp
    echo "starting copy stock's data & restart service... "
	echo "开始拷贝stock配置文件 & 重启服务... "
	fab update_data_stock
	reload_lightparser
}

function back_stage_conf() {
	echo "starting copy dicts stock_index.dict stock_index_trans.dict stock_keyword.dict"
	echo "开始拷贝指标及关键字相关词典 & 重启分词服务"
	fab update_dicts_back_stage_conf
	fab restart_ltp
    echo "starting copy syntactic's data & restart service... "
	echo "开始拷贝句式配置文件 & 重启服务... "
	fab update_data_stock_back_stage_conf
	reload_lightparser
}

function index() {
	echo "starting copy dicts stock_index.dict stock_index_trans.dict"
	echo "开始拷贝指标相关词典 & 重启分词服务"
	fab update_dicts_index
	fab restart_ltp
    echo "starting copy index's data & restart service... "
	echo "开始拷贝指标配置文件 & 重启服务... "
	fab update_data_stock_index
	reload_lightparser
}

function indexgroup() {
    echo "starting copy indexgroup's data & restart service... "
	echo "开始拷贝指标词组配置文件 & 重启服务... "
	fab update_data_stock_indexgroup
	reload_lightparser
}

function keywordgroup() {
	echo "starting copy dicts stock_keyword.dict"
	echo "开始拷贝关键字相关词典 & 重启分词服务"
	fab update_dicts_keyword
	fab restart_ltp
    echo "starting copy keywordgroup's data & restart service... "
	echo "开始拷贝关键词组配置文件 & 重启服务... "
	fab update_data_stock_keywordgroup
	reload_lightparser
}

function synt() {
	echo "starting copy dicts stock_keyword.dict"
	echo "开始拷贝关键字相关词典 & 重启分词服务"
	fab update_dicts_keyword
	fab restart_ltp
    echo "starting copy syntactic's data & restart service... "
	echo "开始拷贝句式配置文件 & 重启服务... "
	fab update_data_stock_synt
	reload_lightparser
}

function semantic() {
    echo "starting copy semantic's data & restart service... "
	echo "开始拷贝语义配置文件 & 重启服务... "
	fab update_data_stock_semantic
	reload_lightparser
}

function update_data_stock_file() {
    echo "starting copy data & restart service... "
	echo "开始拷贝配置文件$1 & 重启服务... "
	fab update_data_stock_file:file="$1"
	reload_lightparser
}

function update_dict_file() {
    echo "starting copy dict & restart service... "
	echo "开始拷贝词典$1 & 重启服务... "
	fab update_dict_file:file="$1"
	fab restart_ltp
}

function reload_lightparser() {
	curl -i http://192.168.201.167:9100/parser/?reload=1
}

if [ "$1" = "dicts" ]; then
    dicts
elif [ "$1" = "data" ]; then
    data
elif [ "$1" = "stock" ]; then
    stock
elif [ "$1" = "back_stage_conf" ]; then
    back_stage_conf
elif [ "$1" = "index" ]; then
    index
elif [ "$1" = "indexgroup" ]; then
    indexgroup
elif [ "$1" = "keywordgroup" ]; then
    keywordgroup
elif [ "$1" = "synt" ]; then
    synt
elif [ "$1" = "semantic" ]; then
    semantic
elif [ "$1" = "file" ]; then
    update_data_stock_file "$2"
elif [ "$1" = "dict" ]; then
    update_dict_file "$2"
else
    usage
fi
