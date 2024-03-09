from whrandom import randint as make_random
import SocketServer
import string
import sys
import socket
import mimetools
import time

class WereOver(Exception):	pass
class AbortData(Exception):	pass

class SMTPServer(SocketServer.TCPServer):
	allow_reuse_address = 1	# Seems to make sense in testing environment

	def server_bind(self):
		"""Override server_bind to store the server name."""
		SocketServer.TCPServer.server_bind(self)
		host, port = self.socket.getsockname()
		self.server_name = socket.getfqdn(host)
		self.server_port = port

class SMTPRequestHandler(SocketServer.StreamRequestHandler):

	def put(self, text):
		self.wfile.write("%s\r\n" % text)

	def get(self):
		def __get(self=self):
			return self.rfile.readline()
		F = __get()
		ofile = open('smtpd.log', 'a+')
		ofile.write("%s" % F[:-1])
		ofile.flush()
		return F
		
	def parse_request(self):
		requestline = self.raw_requestline
		if requestline[-2:] == '\r\n':
			requestline = requestline[:-2]
		elif requestline[-1:] == '\n':
			requestline = requestline[:-1]
		self.requestline = requestline
		words = string.split(requestline)

		if len(words) == 0:
#			words = ['NOOP']
			return 0
#		print 'words', words
		self.command = string.upper(words[0])
		self.rest = words[1:]
		
		return 1

	def IDENTIFY(self):
		self.put("220 Hello, OluWorld! SMTPServer")

	def handle(self):
		print '--'
		self.IDENTIFY()
		self.raw_requestline = self.get()

		self.MSG = ''

		try:
			while self.parse_request():
				mname = 'do_' + self.command
				if not hasattr(self, mname):
					self.send_error(501, "Unsupported method (%s)" % `self.command`)
				else:
					method = getattr(self, mname)
					method()

				self.raw_requestline = self.get()
		except WereOver:
			pass

	def do_HELO(self):
		self.do_RSET()
		try:
			server_name = socket.gethostname()
			self.HELO_NAME = self.client_address[0]
			self.HELO_IP = socket.getfqdn(self.HELO_NAME)
#			port_num = self.client_address[1]			
			self.put("250 %s ESMTP" % server_name)
#			self.put("250 Well howdy, %s" % self.rest[0])
		except IndexError:
			self.put("xxx What's your name pretty?")
			self.were_done=1
	def message_line(self):
		hostnam_ = socket.gethostname()
		hostname = socket.getfqdn(hostnam_)
		the_time = time.strftime('%d %b %Y %H:%M:%S ', time.gmtime(time.time())) #missing -0500
		self.make_SMTP_ID()
		
		rv = 'Recieved: from %s ([%s]) \n\tby %s (%s) with smtp \n\tid %s; %s' \
			% (self.HELO_NAME, self.HELO_IP, hostname, self.ID, self.SMTP_ID, the_time)
		return rv
		
	def do_DBUG(self):
		O=''
		try:
			O = "FROM\t%s\nTO\t%s" % (self.FROM, self.TO)
		except AttributeError:
			O = "Unspecified attributes"
		self.put(O)
	def do_MAIL(self):
		if len(self.rest[0]) > 5:
			self.FROM = self.rest[0][5:]
		else:
			self.FROM = self.rest[1]
		self.MSG = []
		self.put("250 OK")
	def do_RCPT(self):
###		print 'rest =',self.rest
		print 'self.requestline =', self.requestline
		if len(self.rest[0]) > 5:
			TO = self.rest[0][3:]
		else:
			TO = self.rest[1]
		self.TO.append(TO)
###		print "Message out to ", TO
		self.put("250 OK")

	def validate_sender(self):
		try:
			self.FROM
		except AttributeError:
			self.send_error(503, "But I don't even know you")
			raise AbortData()

	def validate_recipients(self):
		if len(self.TO) == 0:
			self.send_error(503, "Right, but where's this thing going?")
			raise AbortData()
	
	def do_NOOP(self):
		print 'NOOP'

	def do_DATA(self):
		try:
			self.validate_sender()
			self.validate_recipients()

#			self.put("354 Let's Rock")
			self.put("354 Start mail input; end with <CRLF>.<CRLF>")
		
			done = 0
		
			self.MSG.append(self.message_line()+'\n')
			while 1:
				line = self.get()
				if not line:
					raise WereOver()
				if line == '.\r\n':
					done = 1
					break
				if line[:2] == '..':
					line = line[1:]
				self.MSG.append(line[:-1])
		
			if done == 0:
				self.put("550 Why don't you ever follow rules?")
				return
			# -- 
			open('smtpd.out/'+self.SMTP_ID, 'w').writelines(self.MSG)
			# -- 
			self.put("250 OK")
		except AbortData:
			pass

	def do_RSET(self):
		self.TO        = []
		self.MSG       = []
		self.ID        = 'OIX MMS SMTPServer v1.0 (MMS-SMTP-1.0)'
		self.HELO_NAME = 'INVALID-HELO-NAME'
		
		
	def make_SMTP_ID(self):
		the_date = time.strftime('%d%b%Y', time.localtime(time.time()))
		the_time = time.strftime('%H%M', time.localtime(time.time()))
		self.SMTP_ID = '%s_MMS-SMTP-1.0_%s_%s' % (the_date,the_time,`make_random(1, 999999)`)
		return self.SMTP_ID
	
	def do_QUIT(self):
		self.put("221 I didn't like you anyway")
		raise WereOver() #(1)

	def send_error(self, code, message=None):
		e = `code` + " "
		if message:
			e = e + message

		self.put(e)

def test(HandlerClass = SMTPRequestHandler, ServerClass = SMTPServer):
	if sys.argv[1:]:
		port = string.atoi(sys.argv[1])
	else:
		port = 25

	server_address = ('', port)

	smtpd = ServerClass(server_address, HandlerClass)

	print "Serving on port", port, "..."
	smtpd.serve_forever()


if __name__ == '__main__':
	test()

