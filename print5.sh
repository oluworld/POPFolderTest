#!/bin/sh
find ../python -type f | grep .py$ > list
cat list | xargs sha1sum >> POPFolderTest-`date +%b%d-%H%M`.txt
echo =========*=========*=========*=========*=========*=========*=========*=========* >> POPFolderTest-`date +%b%d-%H%M`.txt
cat list | xargs pr -n -f -l 84 >> POPFolderTest-`date +%b%d-%H%M`.txt
rm list

