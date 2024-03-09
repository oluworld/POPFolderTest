from etoffiutils import true, false

class Flags:
	def __init__(self):
		self.raw     = false #deref ::so files
		self.excl    = false #exclusive access
		self.trunc   = false #kill file contents on open
		self.rcreat  = false #create for write if non-existant upon open
		#
		self.inherit = true  #inherited by forked/sub- processes.
							 #at this point, there are no subprocesses and
							 #no forking. threads are not affected by this
		self.binary  = true  #crlf crap
		self.wcreat  = true  #create for write if non-existant upon open

Blank_Flags = Flags ()
