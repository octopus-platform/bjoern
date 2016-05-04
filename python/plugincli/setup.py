from distutils.core import setup

setup(
    name='plugincli',
    packages=['bjoern', 'bjoern.plugins', 'bjoern.plugins.cli'],
    license='GPLv3',
    scripts=[
        'scripts/instructionlinker',
        'scripts/functionexporter',
        'scripts/aloc']
)
