from octopus.plugins.plugin import OctopusPlugin


class Alocs(OctopusPlugin):
    def __init__(self, executor):
        super().__init__(executor)
        self._pluginname = "alocs.jar"
        self._classname = "bjoern.plugins.alocs.AlocPlugin"

    def __setattr__(self, key, value):
        if key == "project":
            self._settings["projectName"] = value
        else:
            super().__setattr__(key, value)
