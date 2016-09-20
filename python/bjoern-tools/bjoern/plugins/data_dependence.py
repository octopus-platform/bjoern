from octopus.plugins.plugin import OctopusPlugin


class DataDependenceCreator(OctopusPlugin):
    def __init__(self, executor):
        super().__init__(executor)
        self._pluginname = 'datadependence.jar'
        self._classname = 'bjoern.plugins.datadependence.DataDependencePlugin'

    def __setattr__(self, key, value):
        if key == "project":
            self._settings["database"] = value
        else:
            super().__setattr__(key, value)
