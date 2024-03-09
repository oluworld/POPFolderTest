
import SocketServer
import string
import sys
import socket
import mimetools

class WereOver(Exception):
	pass

class NNGETParse:
	def __init__(self, A):
		self.Acceptor = A
		
	def get_login_info(self, srv):
		return 119, None, None
		
	def parse(self, txt):
		if txt[:5] == 'LOGIN':
			self.save_login_info(txt)	
		l = string.split(txt,'/')
		if l[1]!='nntp':
			print 'wrong protocol in %s' % l[1]
		NN=NNGET()
		po,u,pw=self.get_login_info(l[2])
		hh=NN.obtain(l[2], po,u,pw)
		if NN.query(hh)[0]!=l[3]:
			NN.select(hh, l[3])
		lines,attr=NN.retrieve(l[4])
		self.Acceptor.push(lines,attr,`l[4]`, string.join(('/nntp',s,g,m), '/'))

class PigFat__Server(SocketServer.TCPServer):
	allow_reuse_address = 1	# Seems to make sense in testing environment

	def server_bind(self):
		"""Override server_bind to store the server name."""
		SocketServer.TCPServer.server_bind(self)
		host, port = self.socket.getsockname()
		self.server_name = socket.getfqdn(host)
		self.server_port = port

ofile = open('ofile', 'a+')
class PigFat__RequestHandler(SocketServer.StreamRequestHandler):
#	def __init__(self):
#		SocketServer.StreamRequestHandler.__init__(self)
#		self.put("220 Hello, OluWorld! PigFat__Server")

	def put(self, text):
		self.wfile.write("%s\r\n" % text)

	def _aget(self):
		return self.rfile.readline()
	def get(self):
		F = self._aget()
		ofile.write("%s\n" % F)
		ofile.flush()
		return F
		
	def parse_request(self):
		self.request_version = version = "HTTP/0.9" # Default

		requestline = self.raw_requestline
		if requestline[-2:] == '\r\n':
			requestline = requestline[:-2]
		elif requestline[-1:] == '\n':
			requestline = requestline[:-1]
		self.requestline = requestline
		words = string.split(requestline)

		self.command = words[0]
		self.rest = words[1:]
		
		return 1

##	MessageClass = mimetools.Message

	def IDENTIFY(self):
		self.put("220 Hello, OluWorld! PigFat Server")

	def handle(self):
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

	def do_GET(self):
		print "ACK ", self.rest
		NNGETParse(self.rest)

	def do_QUIT(self):
		self.put("221 Bye bye")
		raise WereOver() #(1)
		
	def do_SETUP(self):
		meth=self.rest[0]
		if meth=='FS':
			self.acceptor = FSAcceptor(self.rest[1])
		elif meth=='MAIL':
			self.acceptor = MailAcceptor(self.rest[1], self.rest[2])
		else:
			self.acceptor = None
			
	def do_HELP(self):
		help_msg = """
		GET <fullname>
		SETUP <acceptor>
		CONNECT <host> (<port>)
		LOGIN <user> (<pass>)
		
		acceptor: FS <root> | MAIL <to> <envelope>
		"""
		self.put(help_msg)
		
		
	def send_error(self, code, message=None):
		e = `code` + " "
		if message:
			e = e + message

		self.put(e)

def test(HandlerClass = PigFat__RequestHandler, ServerClass = PigFat__Server):
	if sys.argv[1:]:
		port = string.atoi(sys.argv[1])
	else:
		port = 2080

	server_address = ('', port)

	PigFat__d = ServerClass(server_address, HandlerClass)

	print "Serving on port", port, "..."
	PigFat__d.serve_forever()


if __name__ == '__main__':
	test()
##	p=NNGETParse(FSAcceptor('f:\\fr0n'))
##	p.parse('/nntp/openbeta.news.uk.uu.net/alt.binaries.erotica.butts/198652')
