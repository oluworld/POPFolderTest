from AppWorks.File import Flags
from DirHandler import iDirHandler
from poplib import POP3
from etoffiutils import *
#from AppWorks.File import *
from MMS.BasicMessage import MMSMessage
from string import split as string_split

from File.Desc_ import Desc
from File.Handle_ import Handle
from File import Perms
from File import StreamState

"""
implement a pop folder. will be used in AwxMail.

"""

class POPFolderError(Exception):
	def __init__(self, str):
		self.gg=str
	def __str__(self):
		return str
class POPFolderData:
	def __init__(self, folder, server):
		self.folder = folder
		self.server = server
class POPFolderWritableData(POPFolderData):
	pass

class POPFolderLoginHandle(Handle):
	def close(self):
		print "$$ quitting server"
		self.getServer().quit()
	def getServer(self):
		return self.extra.server[0]
	def getServerRefCnt(self):
		return self.extra.server[1]
	def write_string (self, str):
		rv = None
		if str[:5].lower () == 'user ':
			self.__username = str [5:]
			self.getServer ().user (str [5:])
			rv = 1
		elif str[:5].lower () == 'pass ':
			self.getServer ().pass_ (str [5:])
			srvname = self.getServer ().host
			#print self.host
			self.host.SRVLIST[self.__username+'@'+srvname] = \
				(110, (self.__username, str[5:], 'writepass'), 1, self.getServer ())
			#print 'adding server', self.host.SRVLIST 
			rv = POPFolderHandle ()
			#print 'extra:', self.extra
			rv.set(self.myDesc, self.host, self.extra, StreamState.Acceptable, Perms.None_, self.myFlags)
		else:
			raise "what the hell are you doing?"
		return rv
class POPFolderHandle(Handle):
	def readlines(self):
		return self.extra.msg.msg
	def getServer(self):
		return self.extra.server[0]
	def incServerRefCnt(self):
		cnt = self.extra.server[1]
		self.extra.server = (self.extra.server[0], cnt+1)
		print '@@ increment', self.extra.server[0], cnt+1
	inc_srv_cnt = incServerRefCnt 
	def close(self):
		cnt = self.extra.server[1]
		self.extra.server = (self.extra.server[0], cnt-1)
		print '@@ decrement', self.extra.server[0], cnt-1
		if self.extra.server[1] == 0:
			print "$$ quitting server"
			self.extra.server[0].quit()
	def open (self, desc, perms, flags, extra, msgnum):
		self.set(desc, self, None, StreamState.Acceptable, perms, flags)
		self.extra = extra

		(resp, msg, octets) = self.getServer().retr(msgnum)
		U = string.split(self.getServer().uidl(msgnum))[-1]
		self.inc_srv_cnt()
#		msg = map(lambda e: "%s\012" % e, add_to_head("X-UIDL: %s" % U, msg))

		self.extra.msg = MMSMessage(msg, U, resp, octets)
		print 'curHandle.extra:', self.extra


g_POPFolder_SRVLIST = {}


class ServerLoginData(object):
	def __init__(self, server, port=110, username=None, password=None):
		self.password = password
		self.username = username
		self.port = port
		self.server = server

	__slots__ = ('server', 'port', 'username', 'password')


class POPFolder(iDirHandler):
	def __init__(self):
		self.SRVLIST = g_POPFolder_SRVLIST
		print 'xxyyzz'
	def canOpen(self, path, perm):
		""" path:File.Path, perm:File.Perms -> bool
			/pop/$server/$msgnum
		"""
		if string_split (path, '/') > 3:
#		if path[:5] == '/pop/' and len(path)>5:
			return true
		else:
			return false

	def enumerate(self, spec):
		""" spec:String -> List<File.Desc> """
		pass

	def enumerateFirstByName(self, spec):
		""" spec:File.Name -> File.Desc """
		s = string_split (spec, '/')
		print 'stat %s (%s)' % (spec, s[3])
		rv = Desc (spec)
		rv.host = self
		return rv
	
	def insert(self, desc):
		""" -> bool """
		if getParam('forward-smtp'):
			h = oixfs.getHandlerForPathOrNil(getParam('forward-server'))
			if h:
				return h.insert(desc)
		else:
			return false
		
	def unlink(self, desc):
		""" -> bool """
		Result = false
		f = Flags()
		f.excl = true
		print "** unlink", desc.getFullName ()
		h = self.open(desc, Perms.Read, f)
		if h:
			#print "ok"
			ll = string.split(desc.getFullName (), '/')
			if ll[3] == 'msgnum':
				msgnum = ll[4]
##				print "^^ msgnum", msgnum
				r = h.getServer().dele(msgnum)
				h.close()
				if r[:1] == '+':
					Result = true
			else:
				raise POPFolderError("Invalid AccessMethod: "+ll[3])
		print "%% unlink"
		return Result
		
	def __validate_method(self, meth):
		#if meth != 'pop': raise InvalidMethodException(self, meth)
		pass
	def _extract(self, ll):
		sl = string_split (ll, '@')
		#print '555 _extract:',sl

		def get_host_port(s):
			hp = string_split(s, ":")
			if len(hp) == 2:
				return hp[0], int(hp[1])
			elif len(hp) == 1:
				return hp[0], 110
			else:
				raise "Malformed host/port combo", s

		if len(sl) > 1:
			host, port = get_host_port(sl[1])
			rv = ServerLoginData(host, port, username=sl[0])
		else:
			host, port = get_host_port(sl[0])
			rv = ServerLoginData(host, port)
		return rv
	
	def _x_extract(self, ll):
		import re
		print 'matching:',ll
		m = re.match (ll, '.*?@.*') 
		print 'm',m
		if m != None:
			print 'patern',format.pattern
			print 'groups',m.groups ()
			print m.group (1)
		else:
			print 'not matching'
			rv = ll, None, None
		return rv
	def __int_open(self, desc, perms, flags, extra): #private, please
		ll = desc.getFullName ().split('/')[1:]
		self.__validate_method(ll[0])
		server_login_data = self._extract(ll[1])
		srvname = server_login_data.server
		
		print "** __int_open ", desc.getFullName()

		# ---------------------------------------------------
		accval = None
		acctype = None
		# ---------------------------------------------------
		if len(ll) > 2:
			acctype = ll[2]
		else:
			raise POPFolderError("No AccessType Specified")
		# ---------------------------------------------------
		if len(ll) > 3:
			accval = ll[3:]
		else:
			#raise POPFolderError("No AccessValue Specified")
			pass
		# ---------------------------------------------------

		if acctype == 'msgnum':
			curHandle = POPFolderHandle()
			print '*************************'
			curHandle.open (desc, perms, flags, apply(extra, (curHandle,self.__int_getserver(ll[1]))), accval[0])
			#curHandle.open (desc, perms, flags, apply(extra, (curHandle, srvname)), accval[0])
			return curHandle
		elif acctype == 'login':
			if accval is not None:
				# warn the user of the dangers of using the password in the filename
				print "** Using Insecure Login Method"
				method_ = "userpass"
			else:
				accval = (None, None)
				method_ = "writeable"
			curHandle = POPFolderLoginHandle()
			curHandle.set(desc, self, None, StreamState.Opening, perms, flags)
			curHandle.extra = apply(extra, (curHandle,self.__int_getserver(server_login_data, (accval[0],accval[1],method_))))
			curHandle.myState = StreamState.Closed
			return curHandle
		else:
			raise POPFolderError("Invalid AccessType Specified")

	def __int_getserver(self, sld, authinfo=None):
		srvname = sld.server
		srvport = sld.port
		host_port_user_triple = (srvname, srvport, sld.username)
		
		print "** getserver", srvname, srvport
#		print "---------"
#		print self.SRVLIST
#		print "---------"
		if self.SRVLIST.has_key(host_port_user_triple):
			port, authinfo, refcnt, inst = self.SRVLIST[host_port_user_triple]
#			self.SRVLIST[srvname] = (port, authinfo, refcnt, inst)
			return inst, refcnt
		else:
			#/pop/[user[:passwd]]@server[:port]/
			#srvname = self._extract (srvname)[0]
			s = POP3(srvname, srvport) # vv port authinfo refcnt instance
			u = '(DEF)'
			flag = 0
			if authinfo != None:
				if authinfo[2] == 'userpass':
					u = authinfo[0]
					s.user(authinfo[0])
					s.pass_(authinfo[1])
				elif authinfo[2] == 'apop':
					## APOP foofery
					pass
				elif authinfo[2] == 'writeable':
					flag=1
			if flag==0:
				# self.SRVLIST[srvname+'-'+u] = (srvport, authinfo, 1, s)
				self.SRVLIST[host_port_user_triple] = (srvport, authinfo, 1, s)
			return s, 1
	
	def open(self, desc, perms, flags):
		""" -> File.Handle """
		if perms == Perms.Read:
			return self.__int_open(desc, perms, flags, POPFolderData)
		elif perms == Perms.Write:
			return self.__int_open(desc, perms, flags, POPFolderWriteableData)
		else:
			return None
		
	def reopen(self, handle, perms, flags):
		""" -> File.Handle """
		if perms == handle.perms and flags == handle.flags:
			return handle
		
		ret = open(handle.desc, perms, flags) # what if
		self.close(handle) # what kind of exception handling here?
		return ret
	
	def close(self, handle):
		""" -> bool """
		print "closing"
		if handle.server == None:
#			raise err("already closed")
			return false
		port, authinfo, refcnt, inst = handle.server
		if refcnt == 1:
			del port, authinfo, inst, refcnt, handle, server
##			handle.server = None
		return true
		
	def write(self, handle, data, size):
		""" File.Handle ByteStream int -> int """
		pass
		
	def read(self, handle, size):
		""" File.Handle int -> ByteStream """
		pass
		
	def seek(self, handle, amt, dir, pos):
		""" handle:File.Handle amt:uint dir:(*forward*, backward) pos:(beg, end, *cur*) """
		pass
		
##	def setParams(self, handle, params):
##		""" handle:File.Handle params:?a """
##		pass
##		
##	def handle(self, evt):
##		pass
	
