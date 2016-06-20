from octopus.plugins.plugin import OctopusPlugin


class RadareImporter(OctopusPlugin):
    def __init__(self, executor):
        super().__init__(executor)
        self._pluginname = "radareimporter.jar"
        self._classname = "bjoern.plugins.radareimporter.RadareImporterPlugin"

    def __setattr__(self, key, value):
        if key == "project":
            self._settings["projectName"] = value
        else:
            super().__setattr__(key, value)
