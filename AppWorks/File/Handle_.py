import StreamState 
import Perms
from Flags_ import Flags
from etoffiutils import true, false

class Handle:
	def __init__(self):
		self.myState = StreamState.NeverOpened
		self.mode    = Perms.Read
		self.myFlags = Flags()
		self.offset  = 0 #
		self.host    = None #[ABSTRACT]iACDirHandler()
		self.myDesc  = None #Desc()
		self.extra   = None
	def set(self, desc, handler, extra, state = StreamState.NeverOpened, \
					mode=Perms.Read, flags=Flags(), offset=0):
		self.myState = state
		self.mode    = mode
		self.myFlags = flags
		self.offset  = offset
		self.host    = handler
		self.myDesc  = desc
		self.extra   = extra
	def read(self, len): # add wait:BOOLEAN??
		self.host.read(self, len)
	def write(self, data, size):
		self.host.write(self, data, size)
	def write_string(self, str):
		self.host.write(self, str, len(str))
	writeString = write_string
	def close(self):
		self.host.close(self)

