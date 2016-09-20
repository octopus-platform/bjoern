from octopus.plugins.plugin import OctopusPlugin


class UseDefAnalyser(OctopusPlugin):
    def __init__(self, executor):
        super().__init__(executor)
        self._pluginname = 'usedefanalyser.jar'
        self._classname = 'bjoern.plugins.usedefanalyser.UseDefAnalyserPlugin'

    def __setattr__(self, key, value):
        if key == "project":
            self._settings["database"] = value
        else:
            super().__setattr__(key, value)
