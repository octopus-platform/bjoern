from bjoern.plugins.cli.plugin_cli import PluginCLI

DESCRIPTION = "Abstract location (aloc) plugin. The aloc plugin adds a node for each abstract location within a " \
              "function and connects it to the instructions reading from or writing to it. Indirect reads or writes (" \
              "due to indirect addressing) are not resolved."


class AlocCLI(PluginCLI):
    def __init__(self):
        super().__init__(
            "alocs.jar",
            "bjoern.plugins.alocs.AlocPlugin")

    def _description(self):
        return DESCRIPTION

    def _setup_argparser(self):
        super()._setup_argparser()
        self.argparser.add_argument(
            "project",
            help="the name of the project"
        )

    def _plugin_configuration(self):
        # get parent configuration
        config = super()._plugin_configuration()
        # add own configuration
        if not "settings" in config:
            config["settings"] = {}
        config["settings"]["projectName"] = self.args.project
        # return plugin configuration
        return config
