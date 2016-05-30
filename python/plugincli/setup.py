from distutils.core import setup

setup(
    name='plugincli',
    packages=['bjoern', 'bjoern.plugins', 'bjoern.plugins.cli'],
    license='GPLv3',
    scripts=[
        'scripts/bjoern-instructionlinker',
        'scripts/bjoern-functionexporter',
        'scripts/bjoern-aloc',
        'scripts/bjoern-vsa']
)
