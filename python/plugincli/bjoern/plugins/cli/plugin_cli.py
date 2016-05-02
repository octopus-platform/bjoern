import http.client
import json
import argparse


class PluginCLI(object):
    def __init__(self, pluginname, pluginclass):
        self.pluginname = pluginname
        self.pluginclass = pluginclass
        self.argparser = argparse.ArgumentParser()

    def _parse_commandline(self):
        self.args = self.argparser.parse_args()

    def _start_plugin(self):
        conn = http.client.HTTPConnection("{}:{}".format(self.args.server, self.args.port))
        config = self.plugin_configuration()
        conn.request("POST", "/executeplugin/", json.dumps(config))
        print(conn.getresponse().read().decode().strip())

    def _plugin_configuration(self):
        data = {"plugin": self.pluginname, "class": self.pluginclass}
        return data

    def _setup_argparser(self):
        self.argparser.add_argument(
            "-s", "--server",
            type=str,
            default="localhost",
            help="the host name of the octopus server"
        )
        self.argparser.add_argument(
            "-p", "--port",
            type=int,
            default=2480,
            help="the port number of the octopus server"
        )

    def execute_plugin(self):
        self._setup_argparser()
        self._parse_commandline()
        self._start_plugin()
