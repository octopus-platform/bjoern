#!/bin/bash



base64 $1 | curl --header 'Content-Type:us/ascii' -d @- http://localhost:2480/uploadfile/`python -c "import urllib, sys; print urllib.quote(sys.argv[1], safe='')" $2`
