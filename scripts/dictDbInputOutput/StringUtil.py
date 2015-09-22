#!/usr/bin/env python
# -*- coding:utf-8 -*-

__status__ = 'Development'
__author__ = 'xuxiang <xuxiang@myhexin.com>'

def subString( str, start, end ):
    if start >= end:
        return ''
    else:
        return str[start : end]