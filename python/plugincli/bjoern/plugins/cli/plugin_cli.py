import http.client
import json
import argparse


class PluginCLI(object):
    def __init__(self, pluginname, pluginclass):
        self.pluginname = pluginname
        self.pluginclass = pluginclass
        self.argparser = argparse.ArgumentParser(description=self._description())
        self.args = None
        self.conn = None

    def _parse_commandline(self):
        self.args = self.argparser.parse_args()

    def _open_connection(self):
        self.conn = http.client.HTTPConnection("{}:{}".format(self.args.server, self.args.port))

    def _close_connection(self):
        self.conn.close()

    def _execute_plugin(self):
        config = self._plugin_configuration()
        self.conn.request("POST", "/executeplugin/", json.dumps(config))
        response = self.conn.getresponse().read().decode().strip()
        print(response)

    def run(self):
        self._setup_argparser()
        self._parse_commandline()
        self._open_connection()
        self._execute_plugin()
        self._close_connection()

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

    def _description(self):
        return None
