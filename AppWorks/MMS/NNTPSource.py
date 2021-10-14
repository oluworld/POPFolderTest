from GetNews_.NNGET import *
from Source import MMSSource
from Error import *

##from poplib import POP3, error_proto as POP3ProtocolError
##import string
##from etoffiutils import add_to_head, false, true
##from BasicMessage import MMSMessage as MMSMessage_
##from Error import MMSLoginError

class MMSNNTPSource(MMSSource):

	def login(self, ui, getting):
		if not getting:
			self.SERV = None #DummyServer()
			return self.SERV
			
		HOST = self._getflag('name', 'nntp')
		PORT = self._getflag('port', 119)
		USER = self._getflag('user', None)
		PASS = self._getflag('passwd', None)

		self.nn = NNGET()
		self.SERV = self.nn.obtain(HOST, PORT, USER, PASS)

		if self.SERV:
			self.SERV.set_debuglevel(1)
		else:
			raise MMSLoginError(self.nn.last_error.reason) 
		return self.SERV

	def getMessageCount(self):
		rv = self.SERV.msg_count
		return (rv, -1)

	def getMsgId(self, msgnum):
		rv = self.SERV.Handle.xhdr('Message-Id', msgnum)
		return rv

	def getMsg(self, msgnum, deleting):
		lines, attr = self.nn.retrieve(self.SERV, msgnum)

##		if deleting:
##			self.SERV.dele(msgnum)

		rv = MMSMessage_(lines, attr['NNTP-Message-Id'], attr['NNTP-Response'], -1)
		return rv

	def finalize(self):
		print 'yy'
##		self.SERV.release()

#eof
