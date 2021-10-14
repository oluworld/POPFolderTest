import SocketServer
import string
import sys
import socket

"""
	STAT LIST RETR DELE NOOP RSET QUIT
	
	TOP UIDL USER PASS APOP
"""

class WereOver(Exception):
	pass

class POP3Server(SocketServer.TCPServer):
	allow_reuse_address = 1	# Seems to make sense in testing environment

	def server_bind(self):
		"""Override server_bind to store the server name."""
		SocketServer.TCPServer.server_bind(self)
		host, port = self.socket.getsockname()
		self.server_name = socket.getfqdn(host)
		self.server_port = port


class POP3RequestHandler(SocketServer.StreamRequestHandler):
	def put(self, text):
##		self.ofile.write("[Sx] (%d) %s<crlf>\n" % (len(text)+2, text))
		self.ofile.write("[Sx] (%d) %s\n" % (len(text)+2, text))
		self.wfile.write("%s\r\n" % text)
	putline = put
	def get(self):
		def __get(self):
			return self.rfile.readline()
		F = __get(self)
##		self.ofile.write("[cx] (%d) %s<crlf>\n" % (len(F), F[:-2]))
		self.ofile.write("[cx] (%d) %s\n" % (len(F), F[:-2]))
		self.ofile.flush()
		return F
		
	def parse_request(self):		
		requestline = self.raw_requestline
		if requestline[-2:] == '\r\n':
			requestline = requestline[:-2]
		elif requestline[-1:] == '\n':
			requestline = requestline[:-1]
		words = string.split(requestline)

		self.requestline = requestline
		self.command = string.upper(words[0])
		self.rest = words[1:]
		
		return 1

	def put_ok_line(self, msg):
		self.put("+OK "+msg)
	send_response=put_ok_line
	def send_error(self, message):
		e = '-ERR ' + message
		self.put(e)
		self.wfile.close()
		self.rfile.close()

	def IDENTIFY(self):
		self.ofile = open('pop3d.log', 'a+')
		self.put_ok_line("Hello, OluWorld! POP3Server")
		self.set_initial_state()
	def set_initial_state(self):
		self.proxy   = self
		self.state   = 'auth'
#		self.command = x.command
#		self.rest    = x.rest
		self.markedForDelete = []

	def get_next_command(self):
		self.raw_requestline = self.get()
	def handle(self):
		self.IDENTIFY()
		self.raw_requestline = self.get()

		self.MSG = ''

		try:
			while self.parse_request():
				self.set_initial_state()
				self.handle_()
		except WereOver:
			pass
		except Exception, e:
			print e
			self.send_error('Unexpected Error')

	def req_state(self, sn):
		print 'req_state:', sn, 'curstate ==', self.state
		if self.state == sn:
			return
		if sn == 'trans':
			self.send_error('You would do well to login')
		if sn == 'update':
			self.send_error('You must be in the update state. Whatever that means.')
		if sn == 'auth':
			self.send_error('Already logged in') ##Connect again in 5 minutes')
	def _ch_state(self, newstate, command):
		print 'old state is ', self.state
		self.state = newstate
		print 'state is now ', self.state
			
	def handle_(self):
		mname = 'do_' + self.command
		if not hasattr(self, mname):
			self.proxy.send_error("Unsupported method (%s)" % `self.command`)
		else:
			method = getattr(self, mname)
			method()
		self.proxy.get_next_command()

	def getNumMsgs(self):
		print 'FIxME: nummsgs = 3'
		return 3
	def getTotalMsgSize(self):
		R = 0
		for each in xrange(1, self.getNumMsgs()):
			if each not in self.markedForDelete:
				R = R + self.getMsgSize(each)
		return R
	def getMsgSize(self, num):
		print 'FIxME: msgsize = 3'
		return 3
	def validateMsgNumOrErr(self, num):
		if num > self.getNumMsgs():
			self.send_error('There are only %d message(s) in this drop' % self.getNumMsgs())
		if num < 1:
			self.send_error('Message numbers start at 1')		
		if num in self.markedForDelete:
			self.send_error('Message irretrievable - marked for deletion')
	def	startMultiLine(self):
		print 'FIxME'
		pass
	def getMsg(self, msgNum, putline):
		print 'FIxME: sending default message'
		for each in self.__int_getMsg(msgNum):
			self.putline(each)
		print "Exit getMsg"
	def __int_getMsg(self, msgNum):
		return open('Q:/email/dummy', 'r').readlines()
	def endMultiLine(self):
		print 'FIxME'
		self.put('.')

	def do_HELO(self):
		server_name = socket.gethostname()
		self.put_ok_msg("%s EPOP3, Ready for Action" % server_name)

	def do_DBUG(self):
		O=''
		try:
			O = "FROM\t%s\nTO\t%s" % (self.FROM, self.TO)
		except AttributeError:
			O = "Unspecified attributes"
		self.put(O)
	
	def do_RETR(self):
		self.req_state('trans')
		print '//%s//' % self.rest
		msgNum = int(self.rest[0])
		self.validateMsgNumOrErr(msgNum)
		self.put_ok_line('%d octets' % self.getMsgSize(msgNum))
		self.startMultiLine()
		self.getMsg(msgNum, self.putline)
		self.endMultiLine()
		print "Exit RETR"
	def do_STAT(self):
		self.req_state('trans')
		self.send_response('%d %d' % (self.proxy.getNumMsgs(), self.proxy.getTotalMsgSize()) )
	def do_LIST(self):
		self.req_state('trans')
		print self.rest
		if len(self.rest) == 0:
			self.put_ok_line('%d messages (%s octets)' % (self.getNumMsgs(), self.getTotalMsgSize()) )
			self.startMultiLine()
			for each_num in xrange(1, self.getNumMsgs()+1):
				self.put_ok_line('%d %d' % (each_num, self.getMsgSize(each_num)) )
			self.endMultiLine()
		else:
			msgNum = int(self.rest[0])
			self.validateMsgNumOrErr(msgNum)
	def do_DELE(self):
		self.req_state('trans')
		msgNum = self.validateMsgNumOrErr(msgNum)
		self.markedForDelete.append(msgNum)
		self.put_ok_line('Message deleted')
	def do_NOOP(self):
		self.put_ok_line('')
	def do_RSET(self):
##		self.req_state('trans')
		self.markedForDelete = []
		self.put_ok_line('Maildrop has %d messages (%d octets)' % (self.getNumMsgs(), self.getTotalMsgSize()))
	def do_TOP(self):
		self.req_state('trans')
		pass
	def do_UIDL(self):
		self.req_state('trans')
		pass
	def do_USER(self):
		self.req_state('auth')
##		print 'USER ' + self.rest[0]
		self.proxy.USERNAME = self.rest[0]
		self.proxy.put_ok_line('Send Password')
	def do_PASS(self):
		self.req_state('auth')
		if self.auth_PASS(self.USERNAME, self.rest[0]):
			self.put_ok_line('Login OK')
			self._ch_state('trans', '')
		else:
			self.proxy.send_error('Bad Password')
	def auth_PASS(self, username, passwd):
		return 1
	def do_APOP(self):
		self.req_state('auth')
		pass
	def do_QUIT(self):
##		self.req_state('update')
		# delete all messages
		#self.
		pass
		self.proxy.put_ok_line('So long sucker')

def test(HandlerClass = POP3RequestHandler, ServerClass = POP3Server):
	if sys.argv[1:]:
		port = string.atoi(sys.argv[1])
	else:
		port = 110

	server_address = ('', port)

	pop3d = ServerClass(server_address, HandlerClass)

	print "Serving on port %d ..." % port
	pop3d.serve_forever()

if __name__ == '__main__':
	test()

#eof
	