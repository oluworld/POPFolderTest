from etoffiutils import false
from BasicHandler import *
from ConfParser import ConfParser as ConfigParser

class IniDBiHandler (BasicHandler):
	INIEXT = '.ini'
	INISIG = None # 'DBiIni'

	def xx__init__(self, fn, ss):
		BasicHandler .__init__ (self, fn, ss)
		self.locked = false
		self.getRoot = fn
		self.addListeners = []
	def _NAME (self):
		return "IniDBiHandler"
	def _set_sps (self, srv):
		self.sps = srv
	def Begin(self, root):
		c = ConfigParser ()
		c.read (root)

		for each in c.options ('DBiIni'):
			if each == '__name__': continue
			name, value = each, c.get ('DBiIni', each)

			try:
				p = self.sps.translatePath (name, '~/', self)
			except AttributeError:
				p = name
#				print 'yy'
#			print 885, p
			newval = DBiValue(self.form_name (root, p), value, self, tuple([]))
			#self._files[root][linenum]=newval
			self._notifyNewValue(newval)
		
		c = None
	def End(self, root):
		print "ending ********************************", root

#eof
