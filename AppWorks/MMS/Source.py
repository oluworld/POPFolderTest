from etoffiutils import true, false

class MMSSource:
	def inspect(self):
		rv = 'MMSSource: flags = \n'
		for each in self.flags:
##			print type(each.path)
			rv = rv + "\t'%s' = '%s'\n" % (each.path, each.value)
		
	def _getflag(self, flag, default=None):
		rv=default
		for each in self.flags:
			if each.path[-len(flag):] == flag:
				rv = each.value
				break
		return rv

	def __getattr__(self, attr):
		return self._getflag(attr)
		
	def setFlags(self, flags):
		self.flags = flags
##		print 'xxxxxxxx', flags
		self.APOP   = self._getflag('apop', 0)
		self.USER   = self._getflag('user', 'no_user_specified')
		self.PASS   = self._getflag('pass', '')
		self.SERVER = self._getflag('name', 'pop')
		self.ACTIVE = self._getflag('active', false)
