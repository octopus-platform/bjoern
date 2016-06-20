from octopus.plugins.plugin import OctopusPlugin


class FunctionExporter(OctopusPlugin):
    def __init__(self, executor):
        super().__init__(executor)
        self._pluginname = "functionexport.jar"
        self._classname = "bjoern.plugins.functionexporter.FunctionExportPlugin"

    def __setattr__(self, key, value):
        if key == "project":
            self._settings["database"] = value
        elif key == "format":
            self._settings["format"] = value
        elif key == "outdir":
            self._settings["outdir"] = value
        elif key == "nodes":
            self._settings["nodes"] = value
        elif key == "edges":
            self._settings["edges"] = value
        elif key == "threads":
            self._settings["threads"] = value
        else:
            super().__setattr__(key, value)
