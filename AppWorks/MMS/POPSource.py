from poplib import POP3, error_proto as POP3ProtocolError
import string
from etoffiutils import add_to_head, false, true
from BasicMessage import MMSMessage as MMSMessage_
from Error import MMSLoginError

class DummyServer:
	def uidl(self, msgnum):
		import md5
		import time
		
		return "%d %s" % (msgnum, md5.new(`time.ctime(time.time())`).hexdigest())
		
	def stat(self):
		return 3, 5

	def retr(self, num):
		m = open('q:/email/dummy').readlines()

		l = 0
		for each in m:
			l = l + len(each)

		return "+OK", m, l

	def dele(self, num):
		pass

class MMSPOPSource:
	def setFlags(self, flags):
##		print 'xxxxxxxx', flags
		def getflag(flag, default=None, flags=flags):
			rv=default
			for each in flags:
##				print type(each.path)
##				print '%s: %s = %s' % (flag, each.path, each.value)
				if each.path[-len(flag):] == flag:
##					print '/x//'
					rv = each.value
					break
##			print rv
			return rv
		self.APOP = getflag('apop', 0)
		self.USER = getflag('user', 'no_user_specified')
		self.PASS = getflag('pass', '')
		self.SERVER = getflag('name', 'pop')
		self.ACTIVE = getflag('active', false)

	def login(self, ui, getting):
		if not getting:
			self.SERV = DummyServer()
			return self.SERV

		if not self.APOP:
			ui.tell('<<< CONNECT %s' % self.SERVER)
			self.SERV = POP3(self.SERVER)
			ui.tell('>>> %s' % self.SERV.welcome)
			
			ui.tell('<<< USER %s' % self.USER)			
			r = self.SERV.user(self.USER)
			ui.tell('>>> %s<<< PASS *****' % r)
			try:
				r = self.SERV.pass_(self.PASS)
			except POP3ProtocolError, e:
#				ui.xtell_(`e`, 'Error during connect')
				raise MMSLoginError, `e`
			ui.tell('>>> %s\n\n' % r)
		else:
			import md5
	
			ui.tell('<<< CONNECT %s\n' % self.SERVER)
			self.SERV = POP3(self.SERVER)
			u = self.SERV.getwelcome()
	
			ddg = string.split(u)
			ddg.reverse()
	
			md5obj = md5.md5(string.join((ddg[0], self.PASS), ''))
			str_digest = md5obj.hexdigest()
##			str_digest = ''
##			for i in digest:
##				str_digest = string.join( (str_digest, "%x" % ord(i)),'')
	
			buffer = self.SERV.apop(self.USER, str_digest)
			ui.tell('<<< APOP %s %s\n>>> %s' % (self.USER, str_digest, buffer))
	
		self.SERV.set_debuglevel(1)
		return self.SERV

	def getMessageCount(self):
		newMsgs, totSize = self.SERV.stat()
		return (newMsgs, totSize)

	def finalize(self):
		print 'yy'
		self.SERV.quit()

	def getMsgId(self, msgnum):
		U = string.split(self.SERV.uidl(msgnum))[-1]
		return U

	def getMsg(self, msgnum, deleting):
		(resp, msg, octets) = self.SERV.retr(msgnum)

##		self.RESP   = resp
##		self.OCTETS = octets # ??

		U = string.split(self.SERV.uidl(msgnum))[-1]
#		msg = map(lambda e: "%s\012" % e, add_to_head("X-UIDL: %s" % U, msg))

		if deleting:
			self.SERV.dele(msgnum)

		rv = MMSMessage_(msg, U, resp, octets)
		return rv
#eof
