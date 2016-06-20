from octopus.plugins.plugin import OctopusPlugin


class InstructionLinker(OctopusPlugin):
    def __init__(self, executor):
        super().__init__(executor)
        self._pluginname = "instructionlinker.jar"
        self._classname = "bjoern.plugins.instructionlinker.InstructionLinkerPlugin"

    def __setattr__(self, key, value):
        if key == "project":
            self._settings["database"] = value
        else:
            super().__setattr__(key, value)
