from Error import *

REMOVE_FROM_SERVER  = 1
LEAVE_ON_SERVER     = 0

CONNECT_MODE        = 1
TEST_MODE           = 0

def MMSSourceFactory(clazzname):
	if clazzname == 'MMSPOPSource':
		from MMS.POPSource import * #MMSPOPSource
		return MMSPOPSource()
	if clazzname == 'MMSNNTPSource':
		from MMS.NNTPSource import * #MMSNNTPSource
		return MMSNNTPSource()

__Eall__ = (REMOVE_FROM_SERVER, LEAVE_ON_SERVER, CONNECT_MODE, TEST_MODE, MMSSourceFactory)

#eof
