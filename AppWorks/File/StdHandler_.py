# native
#import posix
# generic util
from etoffiutils import nequals as starts_with, xstat, true, false, os
# AppWorks
from Desc_ import Desc
from Handle_ import Handle
import Perms

def posix_open (filename, flags):
	#
	# POSIX returns -1
	#
	handle_ = -1
	try:
		handle_ = open (filename, flags)
	except IOError, e:
		if e.errno == 13:
			raise "permission denied"
		if e.errno != 2:
			raise e
	return handle_

def print_bool_result (rv):
	if rv:
		print "\ttrue"
	else:
		print "\tfalse"
class PermissionsError: pass
#
# This class exists to map /local onto a `native' medium
#

class StdHandler:
	def checkPerms (self, ctx, aFileName):
		return true
	def exists (self, aFileName, ctx):
		# --
		#assert ctx.get_shared_file_server () == self
##		print 927, ctx.get_shared_file_server ().hamdlers [0]
##		print 928, self
		# --
		if not self.checkPerms (ctx, aFileName):
			raise PermissionsError (ctx, aFileName, self)
		#rv = self.open (Desc(aFileName), Perms.Exist) != None
		rv = xstat (self.translateName (aFileName))
		#print_bool_result (rv)
		return rv
	def open (self, aFileDesc, flags):
		rv 		= Handle ()
		handle_ = -1
		flags_ 	= 'r'
		fn      = self.translateName (aFileDesc.getFullName ())
		handle_ = posix_open (fn, flags_)
		if handle_ == -1:
			rv = None
		else:
			rv.set (aFileDesc, self, Handle, StreamState.Acceptable, 
					 aMode, flags, 0)
#			rv.setHost  (self)
#			rv.setState ('ok')#ssOK)
#			rv.setExtra (Handle)
##			rv.setDesc  (aFileDesc)
#			rv.setMode  (aMode)
#			rv.setFlags (flags)
			
		return rv
	def translateName (self, xx):
		print 'translating name', xx,
		try:
			rv = self.translateName_ (xx)
		finally:
			if rv != xx:
				print "-->",rv
			else:
				print "!!! no change !!!"
		return rv
	def translateName_ (self, xx):
		#if path references [special] dirs as a file
		if xx=='/local': 		return self.local_root
		if xx=="/local/DBi":	return self.dbi_root
		#
		if starts_with (xx, '/local/DBi/'):
			return self.dbi_root+ xx[11:]
		if starts_with (xx, '/local/'):
			return self.local_root+ xx[7:]
		return xx
	def enumerateFirstByName (self, aFileName):
		rv = Desc (aFileName)
		print 777, os.path.dirname (self.translateName (aFileName))
		rv.host = self
		return rv

	def __init__ (self, ctx=None):
		self.local_root = 'd:/'
		self.dbi_root = 'q:/DBi/'
	sigs = """
	exists.signature = 'AppWorks.File.StdHandler#exists;1/bool/string'
	open.signature = 'AppWorks.File.StdHandler#open;1/AppWorks.File.Handle/string,AppWorks.File.OpenFlags'
	"""
	
#eof
