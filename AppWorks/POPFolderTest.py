#from AppWorks.File.POPFolder import POPFolder
#from AppWorks.File import Perms, Flags
from AppWorks.DBi.Server import AwxDBiServer
from AppWorks.Util.AwxDBi import AwxDBi
from File.POPFolder import POPFolder
from File import Perms, Flags
from etoffiutils import quickWrite, ensure_directory_present, true, false

#from File.System_ import oixfs
from Base.App_ import App
from File.System_ import System
from File.Desc_ import Desc

class F(App):
	def __init__(self):
		self.oixfs = System(self)
		# self._my_info_server = AwxDBi()
		# self._setBasicInformation(self.oixfs, self._my_info_server)
	def get_shared_information_server(self):
		return AwxDBiServer()
	def get_shared_file_server(self):
		return None

oixfs = F().oixfs

server, user, passwd = 'localhost', 'user', 'secret'

def readdef ():
	f = open('def')
	global server, user, passwd
	server = f.readline().strip()
	user   = f.readline().rstrip()
	passwd = f.readline().rstrip()
	f.close()

def xx_do_login ():
	handler = POPFolder()

	#######################################
	# login to the server
	dd = _Desc('/pop/'+server+'/login/'+user+'/'+passwd)
	ff = Flags()
	login_handle = handler.open(dd, Perms.Read, ff)
	return handler, dd, ff, login_handle 
	
def xx_write_out (n, lines):
	quickWrite('out/%d'%n, lines)

def do_login ():
	login_path = '/pop/' + server + '/login/' + user + '/' + passwd
	dd = oixfs.enumerateFirstByName (login_path)
	handler = dd.host
	ff = Flags()
	login_handle = handler.open(dd, Perms.Read, ff)
	return handler, dd, ff, login_handle 

def write_out (n, lines):
	hh = Desc ('out/%d'%n)
	rv = false
	if hh:
		for each in lines:
			hh.write (each, len(each))
		rv = true
		hh = None
	return rv
		
#######################################
# initialize
readdef()
#######################################
# login to the server
# obtain handler (usu would be transparent)
handler, dd, ff, login_handle = do_login ()

ensure_directory_present('out')
for n in range(1,10):
	dd     = Desc('/pop/%s-%s/msgnum/%d' % (server, user, n))
	handle = handler.open(dd, Perms.Read, ff)
	try:
		# dont delete until we know it written
		write_out (n, handle.readlines())
		if handler.unlink(handle.myDesc) == true:
			print '-- file removed'
		else:
			print '-- file not removed'
	except:
		pass
##	print "vvcls"
	handle.close()
	
login_handle.close()
