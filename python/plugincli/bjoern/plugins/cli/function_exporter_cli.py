import os

from bjoern.plugins.cli.plugin_cli import PluginCLI

DESCRIPTION = "Function export plugin. The function export plugin exports database content at function level, " \
              "i.e. each function is exported to a seperate file. The nodes and edges that are exported must be " \
              "specified with the respective options."


class FunctionExporterCLI(PluginCLI):
    def __init__(self):
        super().__init__(
            "functionexport.jar",
            "bjoern.plugins.functionexporter.FunctionExportPlugin")

    def _description(self):
        return DESCRIPTION

    def _setup_argparser(self):
        super()._setup_argparser()
        self.argparser.add_argument(
            "project",
            help="the name of the project"
        )
        self.argparser.add_argument(
            "-f", "--format",
            type=str,
            default="graphml",
            choices=["graphml", "dot", "gml"],
            help="the format of the exported graphs"
        )
        self.argparser.add_argument(
            "-o", "--outdir",
            type=str,
            default=os.getcwd(),
            help="the output directory of the exported graphs"
        )
        self.argparser.add_argument(
            "-n", "--nodes",
            type=str,
            nargs="+",
            help="the node types of nodes that are exported"
        )
        self.argparser.add_argument(
            "-e", "--edges",
            type=str,
            nargs="+",
            help="the edges types of nodes that are exported"
        )

    def _plugin_configuration(self):
        # get parent configuration
        config = super()._plugin_configuration()
        # add own configuration
        if not "settings" in config:
            config["settings"] = {}
        config["settings"]["database"] = self.args.project
        config["settings"]["format"] = self.args.format
        config["settings"]["outdir"] = os.path.abspath(self.args.outdir)
        config["settings"]["nodes"] = self.args.nodes
        config["settings"]["edges"] = self.args.edges
        config["settings"]["threads"] = 4
        # return plugin configuration
        return config
