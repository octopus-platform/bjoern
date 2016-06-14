#!/usr/bin/env python3

import sys, os
import base64
import http.client
import urllib

SERVER_HOST = 'localhost'
SERVER_PORT = '2480'

importerPluginJSON ="""{
    "plugin": "radareimporter.jar",
    "class": "bjoern.plugins.radareimporter.RadareImporterPlugin",
    "settings": {
        "projectName": "%s",
    }
}
"""

class BjoernRadareImporter:
    def __init__(self):
        pass

    def importBinary(self, filename):
        self.filename = filename
        self.createProject()
        self.uploadBinary()
        self.executeImporterPlugin()

    def createProject(self):
        self.projectName = os.path.split(self.filename)[-1]
        print('Creating project: %s' % (self.projectName))

        conn = self._getConnectionToServer()
        conn.request("GET", "/manageprojects/create/%s" % (self.projectName))

    def _getConnectionToServer(self):
        return http.client.HTTPConnection(SERVER_HOST + ":" + SERVER_PORT)

    def uploadBinary(self):
        print('Uploading binary: %s' % (self.filename))

        with open(self.filename, mode='rb') as file:
            fileContent = file.read()

        base64Content = base64.b64encode(fileContent)

        headers = {"Content-type": "text/plain;charset=us/ascii"}
        conn = self._getConnectionToServer()
        conn.request("POST", "/uploadfile/%s/binary" % (self.projectName), base64Content, headers)
        response = conn.getresponse()

    def executeImporterPlugin(self):
        print('Executing importer plugin')
        conn = self._getConnectionToServer()
        conn.request("POST", "/executeplugin/", importerPluginJSON % (self.projectName))
        response = conn.getresponse()

def main(filename):
    importer = BjoernRadareImporter()
    importer.importBinary(filename)

def usage():
    print('%s <filename>' % (sys.argv[0]))

if __name__ == '__main__':

    if len(sys.argv) != 2:
        usage()
        exit()

    main(sys.argv[1])
