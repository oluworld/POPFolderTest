from poplib import POP3
import string

class DummyServer:
	def stat(self):
		return 3, 5

	def retr(self, num):
		m = open('h:/email/dummy').readlines()

		l = 0
		for each in m:
			l = l + len(each)

		return "+OK", m, l

	def dele(self, num):
		pass

class MMSPOPSource:
	def setFlags(self, flags):
		try:
			self.APOP = flags['apop'].value
		except:
			self.APOP = 0
			
		try:
			self.USER = flags['user'].value
		except:
			self.USER = 'dummy'

		try:
			self.PASS = flags['pass'].value
		except:
			self.PASS = ''

		try:
			self.SERVER = flags['name'].value
		except:
			self.SERVER = 'pop'

	def login(self, ui, getting):
		if not getting:
			self.SERV = DummyServer()
			return self.SERV

		if not self.APOP:
			ui.tell('<<< CONNECT %s\n<<< USER %s\n' % (self.SERVER, self.USER))
			
			self.SERV = POP3(self.SERVER)
			r = self.SERV.user(self.USER)
			ui.tell('>>> %s\n<<< PASS *****' % r)
			r = self.SERV.pass_(self.PASS)
			ui.tell('>>> %s\n\n' % r)
		else:
			import md5
	
			ui.tell('<<< CONNECT %s\n' % self.SERVER)
			self.SERV = POP3(self.SERVER)
			u = self.SERV.getwelcome()
	
			ddg = string.split(u)
			ddg.reverse()
	
			md5obj = md5.md5(string.join((ddg[0], self.PASS), ''))
			digest = md5obj.digest()
			str_digest = ''
			for i in digest:
				str_digest = string.join( (str_digest, "%x" % ord(i)),'')
	
			buffer = self.SERV.apop(self.USER, str_digest)
			ui.tell('<<< APOP %s %s\n>>> %s' % (self.USER, str_digest, buffer))
	
		return self.SERV

	def getMessageCount(self):
		b, n = self.SERV.stat()
		return (n, n)

	def getMsg(self, msgnum, deleting):
		(resp, msg, octets) = self.SERV.retr(msgnum)

		self.RESP   = resp
		self.OCTETS = octets # ??

		if deleting:
			self.SERV.dele(msgnum)

		return msg
#eof
