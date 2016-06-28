import sys
from distutils.core import setup

if (sys.version_info.major, sys.version_info.minor) < (3, 4):
    sys.exit("Python < 3.4 not supported.")

setup(
    name='bjoern-tools',
    version='0.1',
    packages=['bjoern', 'bjoern.plugins', 'bjoern.shell', 'bjoern.shell.config'],
    package_dir={
        'bjoern.shell': 'bjoern/shell',
        'bjoern.shell.config': 'bjoern/shell/config'
    },
    package_data={
        'bjoern.shell': ['data/bjosh_banner.txt'],
        'bjoern.shell.config': ['data/bjosh.ini']
    },
    url='https://github.com/octopus-platform/bjoern-tools',
    license='LGPLv3',
    scripts=['scripts/bjoern-import', 'scripts/bjoern-instructionlinker', 'scripts/bjoern-functionexport',
             'scripts/bjoern-alocs', 'scripts/bjoern-vsa', 'scripts/bjosh'])
