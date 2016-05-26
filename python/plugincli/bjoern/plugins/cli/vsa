from bjoern.plugins.cli.plugin_cli import PluginCLI

DESCRIPTION = ""


class VSACLI(PluginCLI):
    def __init__(self):
        super().__init__(
            "vsa.jar",
            "bjoern.plugins.vsa.VSAPlugin")

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
        config["settings"]["database"] = self.args.project
        # return plugin configuration
        return config
