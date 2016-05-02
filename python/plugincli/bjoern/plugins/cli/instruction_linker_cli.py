from bjoern.plugins.cli.plugin_cli import PluginCLI


class InstructionLinkerCLI(PluginCLI):
    def __init__(self):
        super().__init__(
            "instructionlinker.jar",
            "bjoern.plugins.instructionlinker.InstructionLinkerPlugin")

    def _setup_argparser(self):
        super()._setup_argparser()
        self.argparser.add_argument(
            "database",
            help="the name of the database"
        )

    def _plugin_configuration(self):
        # get parent configuration
        config = super()._plugin_configuration()
        # add own configuration
        if not "settings" in config:
            config["settings"] = {}
        config["settings"]["database"] = self.args.database
        # return plugin configuration
        return config
