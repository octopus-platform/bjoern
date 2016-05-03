import os

from bjoern.plugins.cli.plugin_cli import PluginCLI


class FunctionExporterCLI(PluginCLI):
    def __init__(self):
        super().__init__(
            "functionexport.jar",
            "bjoern.plugins.functionexporter.FunctionExportPlugin")

    def _setup_argparser(self):
        super()._setup_argparser()
        self.argparser.add_argument(
            "database",
            help="the name of the database"
        )
        self.argparser.add_argument(
            "-f", "--format",
            type=str,
            default="graphml",
            choices=["graphml", "dot", "gml"],
            help="the format of the exported graphs"
        )
        self.argparser.add_argument(
            "-d", "--destination",
            type=str,
            default=os.getcwd(),
            help="the output directory"
        )
        self.argparser.add_argument(
            "-n", "--nodes",
            type=str,
            nargs="+",
            help="the node types"
        )
        self.argparser.add_argument(
            "-e", "--edges",
            type=str,
            nargs="+",
            help="the edges types"
        )

    def _plugin_configuration(self):
        # get parent configuration
        config = super()._plugin_configuration()
        # add own configuration
        if not "settings" in config:
            config["settings"] = {}
        config["settings"]["database"] = self.args.database
        config["settings"]["format"] = self.args.format
        config["settings"]["destination"] = self.args.destination
        config["settings"]["nodes"] = self.args.nodes
        config["settings"]["edges"] = self.args.edges
        config["settings"]["threads"] = 4
        # return plugin configuration
        return config
