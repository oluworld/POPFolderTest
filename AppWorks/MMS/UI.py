
class NullUI:
	def abort(self, msg):
		pass
	def tell(self, msg):
		pass
	def xtell(self, msg):
		pass
	def xtell_(self, msg, title):
		pass

class ConsoleUI:
	def abort(self, msg):
		print 'ABORTING!!!\n\t', msg 
	def tell(self, msg):
		print '*** ', msg
	def xtell(self, msg): # multiline
		print '***\n%s\n*** '% msg
	def xtell_(self, msg, title): # usu. dialog
		print '***\n%s\n%s\n***' % (title, msg)

