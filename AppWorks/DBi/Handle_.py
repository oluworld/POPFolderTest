from Structure import EnumFlat, EnumRecursive

class Handle:

	def enum (self, Key, kind = EnumRecursive):
		rv = self.server().enum(Key, self.root, kind)
##		print '%% enum RETURNS -> %s' % rv
		return rv
	def enumStr (self, Key, kind = EnumRecursive):
		rrv = self.server().enum(Key, self.root, kind)
		rv = []
		for each in rrv:
			rv.append (each.getName ())			
		return rv
		
	def getStrWithDefault(self, Key, default):
		try:
			rv = self.getStr(Key)
		except KeyError: ## RCDBiHandle.getValue
			rv = default
		return rv

	def getStr(self, Key):
##		print '%% getStr(%s %s)' % (self.root, Key)
		rv = self.server().getStr(Key, self.root, self.context)
##		print '%% getStr RETURNS -> %s' % rv
		return rv 
		
	def setRoot(self, root):
##		print 'new root is %s' % root
		self.root = root
		
	def __init__(self, ctx, root='/local/DBi/'):
		self.values = {}
		self.setRoot(root)
		self.context = ctx
		#self._server = ctx.get_shared_information_server ()
		#print "set dbiserver", self.server ()
		if self.server () == None:
			raise "hell"
	def server (self):
		#return self._server
		rv=self.context.get_shared_information_server ()
		return rv
		
#eof
