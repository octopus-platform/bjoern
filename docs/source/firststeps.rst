First steps (Tutorial)
----------------------

The following tutorial illustrates basic usage of bjoern. You will
learn how to start the server, import code, spawn a bjoern-shell and
run queries against the database.

1. Begin by starting the bjoern-server:

.. code-block:: none

	./bjoern-server.sh


2. In another shell, import some code

.. code-block:: none

	./bjoern-import.sh /bin/ls

This will start a thread inside the server process which performs the
import. You will see an 'Import finished' message in the server log
upon completion.

3. Create a shell thread using bjosh

.. code-block:: none

	bjosh create

4. Connect to the shell process using bjosh

.. code-block:: none

	bjosh connect

5. Get names of all functions (sample query)

.. code-block:: none


	_     _           _
	| |__ (_) ___  ___| |__
	| '_ \| |/ _ \/ __| '_ \
	| |_) | | (_) \__ \ | | |
	|_.__// |\___/|___/_| |_|
	     |__/     bjoern shell


	bjoern> queryNodeIndex('nodeType:Func').repr
