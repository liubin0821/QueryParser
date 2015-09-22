#!/usr/bin/python2.6  
# -*- coding: utf-8 -*-
import cgi
import os
import setting
import urllib2
import AutomaticSynchronizationFrame
import sys

reload(sys)  
sys.setdefaultencoding('utf8')  

sys.path.append(os.getcwd()+'/../dictDbInputOutput')
import InheritDictExport

from os import curdir, sep
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

class RequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        try:
            if self.path.endswith('/readme.txt'):
                #f = open(curdir + sep + self.path)
                self.send_response(200)
                self.send_header('Content-type','text/html; charset=utf-8')
                self.end_headers()
                self.wfile.write("Hello! This is Ltp Dicts Service.")
                #f.close()
                
            elif self.path.endswith('/ltp_reload'):
                self.send_response(200)
                self.send_header('Content-type','text/html; charset=utf-8')
                self.end_headers()
                self.wfile.write("Reload success")

            elif self.path.startswith('/UpdateDicts'):
                if '?' in self.path:#如果带有参数 
                    values = self.path.split('?')[-1]
                    key_values = {}
                    for key_value in values.split('&'):
                        key = key_value.split('=')[0]
                        value = key_value.split('=')[1]
                        key_values[key] = value
                    os.system("python UpdateDicts.py "+key_values["domain"])
                self.send_response(200)
                self.send_header('Content-type','text/html; charset=utf-8')
                self.end_headers()
                self.wfile.write("update success!")
            
            elif self.path.startswith('/SiteNameSync'):
                if '?' in self.path:#如果带有参数 
                    values = self.path.split('?')[-1]
                    key_values = {}
                    for key_value in values.split('&'):
                        key = key_value.split('=')[0]
                        value = key_value.split('=')[1]
                        key_values[key] = value
                    os.system("python read_from_mysql.py "+key_values["type"])
                self.send_response(200)
                self.send_header('Content-type','text/html; charset=utf-8')
                self.end_headers()
                self.wfile.write("update success!")
            
            elif self.path.startswith('/BusinessNameSync'):
                if '?' in self.path:#如果带有参数 
                    values = self.path.split('?')[-1]
                    key_values = {}
                    for key_value in values.split('&'):
                        key = key_value.split('=')[0]
                        value = key_value.split('=')[1]
                        key_values[key] = value
                    os.system("python read_from_ifind.py "+key_values["type"])
                self.send_response(200)
                self.send_header('Content-type','text/html; charset=utf-8')
                self.end_headers()
                self.wfile.write("update success!")
            
            elif self.path.startswith('/ConfSync'):
                if '?' in self.path:#如果带有参数 
                    values = self.path.split('?')[-1]
                    key_values = {}
                    for key_value in values.split('&'):
                        key = key_value.split('=')[0]
                        value = key_value.split('=')[1]
                        key_values[key] = value
                    print "bash deploy.sh "+key_values["type"]
                    os.system("bash deploy.sh "+key_values["type"])
                self.send_response(200)
                self.send_header('Content-type','text/html; charset=utf-8')
                self.end_headers()
                self.wfile.write("update success!")
            elif self.path.startswith('/GetLtpDicts'):
                self.send_response(200)
                self.send_header('Content-type','text/plain; charset=utf-8')
                self.end_headers()
                self.get_dict_from_db()
            elif self.path.startswith('/GetMSLtpDicts'):
                self.send_response(200)
                self.send_header('Content-type','text/plain; charset=utf-8')
                self.end_headers()
                self.get_ms_dict_from_db()
            elif self.path.startswith('/GetMongoDicts'):
                self.send_response(200)
                self.send_header('Content-type','text/plain; charset=utf-8')
                self.end_headers()
                self.get_dict_from_mongo()                
            elif self.path.startswith('/UpdateLtp'):   
                if '?' in self.path:#如果带有参数 
                    values = self.path.split('?')[-1]
                    key_values = {}
                    for key_value in values.split('&'):
                        key = key_value.split('=')[0]
                        value = key_value.split('=')[1]
                        key_values[key] = value
                    ENV='164'
                    if(key_values.has_key("ENV") and key_values["ENV"].lower()=="prod"):
                        ENV='167 168 180 205 206 116 117 12'
                    elif(key_values.has_key("ENV") and key_values["ENV"].lower()=="prerelease"): 
                        ENV='179'
                    if key_values.has_key("MODEL") :
                        ENV = ENV + " " +  key_values["MODEL"];  
                    print '~/ltp-server/ltp-server.sh restart ' + ENV
                    output = os.popen('~/ltp-server/ltp-server.sh restart ' + ENV)
                    
                    self.send_response(200)
                    self.send_header('Content-type','text/plain; charset=utf-8')
                    self.wfile.write(output.read()) 
            else:
                self.send_error(404, 'File Not Found %s' % self.path)  
                
            

        except IOError:
            self.send_error(404,'File Not Found: %s' % self.path)

    def do_POST(self):
        global rootnode
        try:
            ctype, pdict = cgi.parse_header(self.headers.getheader('content-type')) #('multipart/form-data', {'boundary': '303161840321948'})
            if ctype == 'multipart/form-data':
                query = cgi.parse_multipart(self.rfile, pdict)
            self.send_response(301)
            self.end_headers()

            upfilecontent = query.get('upfile')
            print "filecontent", upfilecontent[0]
            self.wfile.write('POST OK!')
            self.wfile.write(upfilecontent[0])

        except :
            pass    
    def get_dict_from_db(self):
        export = InheritDictExport.InheritDictExport(setting.ONTOLOGYDB_CONFIG)
        for line in export.queryAllDicts():
            self.wfile.write(line)
        del export
    
    def get_ms_dict_from_db(self):
        export = InheritDictExport.InheritDictExport(setting.ONTOLOGYDB_CONFIG)
        for line in export.queryMSDicts():
            self.wfile.write(line)
        del export
            
    def get_dict_from_mongo(self):
        for line in AutomaticSynchronizationFrame.getMongoDbDicts():
            self.wfile.write(line)
                        
       

def main():
    try:
        server = HTTPServer(('', setting.SERVER_PORT), RequestHandler)
        print 'started httpserver...'
        server.serve_forever()

    except KeyboardInterrupt:
        print '^C received, shutting down server'
        server.socket.close()

if __name__ == '__main__':
    main()