import SocketServer
import string
import sys
import socket

class WereOver(Exception):
	pass

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
		
		return 1

	def IDENTIFY(self):
		self.put("220 Hello, OluWorld! PigFat Server")

	def handle(self):
		self.IDENTIFY()
		self.raw_requestline = self.get()

		try:
			while self.parse_request():
				self.put('1')
		except WereOver:
			pass

	def do_GET(self):
		print self.requestline

	def do_QUIT(self):
		self.put("221 Bye bye")
		raise WereOver() #(1)

	def send_error(self, code, message=None):
		e = `code` + " "
		if message:
			e = e + message

		self.put(e)

def test(HandlerClass = PigFat__RequestHandler, ServerClass = PigFat__Server):
	if sys.argv[1:]:
		port = string.atoi(sys.argv[1])
	else:
		port = 8080

	server_address = ('', port)

	PigFat__d = ServerClass(server_address, HandlerClass)

	print "Serving on port", port, "..."
	PigFat__d.serve_forever()


if __name__ == '__main__':
	test()

