/*
.origfile '/local/src/lang/python/MMS/__init__.py'
.origlang python

.transdate ...
.author ...

.namespace /MMS
.modtype static-initialization
*/

/* ----------------------- */
/* this goes in __init__.h */
/* ----------------------- */
MMS_Source_MMSSource *MMS_MMSSourceFactory(AgxObject *clazzname)

int REMOVE_FROM_SERVER  = 1;
int LEAVE_ON_SERVER     = 0;

int CONNECT_MODE        = 1;
int TEST_MODE           = 0;
/* ----------------------- */

#include <TOP/MMS/Source.h>
#include <TOP/MMS/Error.h>
#include <TOP/MMS/NNTPSource.h>
#include <TOP/MMS/POPSource.h>

MMS_Source_MMSSource *MMS_MMSSourceFactory(AgxObject *clazzname)
{
	void *topstack = NULL;
	AgxString *_tmp_001 = AgxString_fromCStr("MMSPOPSource");
	
	AgxCmp _cmpres_001 = AgxCmpObj(clazzname, _tmp_001);
	if(_cmpres_001 == AgxEqual)
	{
		topstack = MMS_PopSource_MMSPopSource___init__();
	}
	else
	{
		{
			AgxRelase(_tmp_001);
			_tmp_001    = AgxString_fromCStr("MMSNNTPSource");			
			_cmpres_001 = AgxCmpObj(clazzname, _tmp_001);
			if(_cmpres_001 == AgxEqual)
			{
				topstack = MMS_NNTPSource_MMSNNTPSource___init__();
			}
			else
			{
				AGX_RUNTIME_FALLOFF
			}
		}
	}
cleanup:
	AgxRelease_ifNULL(_tmp_001);
	return (MMS_Source_MMSource*)topstack;
}
