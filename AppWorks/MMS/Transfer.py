import string
from etoffiutils import quickReadFunc, true
from BasicMessage import MMSMessage
from Folder import MMSFolder

# filterspec: <header> <rule> <action>
# header: 'Subject' ...
# rule: ['not'] 'contains'|'beginswith'|'endswith'
# action: 'moveto' <foldername>|'delete'

class Categorize:
	pass

class MMSTransfer(Categorize):
	def __init__(self, src, dbi=None):
		self._reset(src, dbi)
	
	def _reset(self, src, dbi):
		if dbi==None:
			from AwxDBi import *
			self.myDBi = AwxDBi()
		else:
			self.myDBi = dbi
	
		self._init() # initialize member variables
		
		dbi.setRoot('/local/DBi/.config/email/sources/'+src)
		self.fn = dbi.getStrWithDefault('filterfile', '/dev/null')
		self.fr = dbi.getStr('folderroot')
		#dbi.close() 
		self._readfilters()
		self._makeFolders()
	
	def _init(self):
		self.testing = false	# set testmode to off
		self.filters = []
		self.folders = {}
	def _readfilters(self):
		try:
			self.filters = filter(lambda e: len(e) and e[0][0]!='#', \
				quickReadFunc(self.fn, string.split, true))
		except IOError, e:
			if e.errno == 2:
				print "!!!!: no filters because no filterfile"
			else:
				print e
	
	def _makeFolders(self):
		""" % ... 1.0 01-Feb-05 (0620) delegator,accessor """
		for each in self.filters:
			folder_name = each[len(each)-1]
			print 'loading folder %s' % folder_name 
			self.getFolder(folder_name)

	def transfer(self, complaintbox, msg, Folder=None, put_fromline=true):
		if Folder == None:
			self.ApplyFilters(self.filters, msg, complaintbox, put_fromline)
		else:
			self.writeMsg(msg, Folder, put_fromline)

	def getActionForMessage(self, msg, ui, put_fromline=true, filters=None):
		""" go through each in $filters and see if we can pull up a 
			matching header for $msg. will report success to $ui
		"""
		ChangeLog = """ split ApplyFilters and created this for use outside """
		# Subject contains litestep moveto litestep
		if filters==None:
			filters=self.filters
		for each in filters:
			h = msg.GetHeader(each[0])

			if h and self.GetMatch(h, each[1:], msg):
##				ui.xtell('-- YES YES YES\n%s \n %s %s %s\n--' % (h, each[0], each[1], each[2]))
				ui.tell('-> %s' % each[4])
				return each[4], each[3]
		return None, None

	def ApplyFilters(self, filters, msg, ui, put_fromline=true):
		folder, verb = self.getActionForMessage(filters, msg, ui, put_fromline)
		if verb == None:
			self.writeMsg(msg, put_fromline=put_fromline)
		else:
			self.Do( (verb, action) , msg, put_fromline)
	
	def GetMatch(self, header, rule, msg):
		# header -> '[Litestep] listsplitter 2000'
		# rule	 -> 'contains' '[Litestep]' 'moveto' 'litestep'
		# msg    -> ['From: ...', '...']
		
		cur = 0
		Negate = false
		if rule[1] == 'not':
			Negate = true
			cur = cur + 1
		
		if string.lower(rule[cur]) == 'contains':
			cur = cur + 1
			if string.find(header, rule[cur]) == -1:
				return false
			else:
				return true
		
		if rule[cur] == 'beginswith':
			cur = cur + 1
			if nequals(header, rule[cur]):
				return true
			else:
				return false
		
		if rule[cur] == 'endswith':
			cur = cur + 1
			if header[:len(rule[cur])] == rule[cur]:
				return true
			else:
				return false

		return false
	
	def test(self):
		self.testing = true
	
	def writeMsg(self, msg, folder_name='root', put_fromline=true):
		""" #writeMsg accepts a list of lines and dumps them in the 
			folder $folder.	$put_fromline controls the possibility 
			to add a mbox-separator	to the top of the written file.
			a debug message is output in test mode.
		"""
		ChangeLog = """
			% ... 2.0 01-Jan-13 (1925) actor
			% ... 3.0 01-Feb-05 (0720) delegator
				now only uses `MMSFolder's
		"""
		if self.testing == true:
##			print 'would have written message to folder %s' % folder
			return
		
		folder = self.getFolder(folder_name)
		folder.store(msg, put_fromline=put_fromline)

	def findMsgByServerId(self, msgid):
		""" % ... 1.0 01-Feb-05 (0550) accessor """
		for each in self.folders.items():
			if each[1].is_stored(msgid):
				return each[0]
		return None
	def getFolder(self, folder_name):
		""" 01-Jan-13 (1920) """
		if self.folders.has_key(folder_name):
			return self.folders[folder_name]
		else:
			rv = MMSFolder(folder_name, self.fr)
			self.folders[folder_name] = rv
			return rv

	def Do(self, Action, msg, put_fromline=true):
		""" #Do calls #writeMsg based on understood actions 
			% delegator
		"""
		if Action[0] == 'moveto':
			self.writeMsg(msg, Action[1], put_fromline=put_fromline)

		if Action[0] == 'copyto':
			self.writeMsg(msg, put_fromline=put_fromline)
			self.writeMsg(msg, Action[1], put_fromline=put_fromline)

		if Action[0] == 'delete':
			pass

##def test():
##	fldr = 'y:/data/email/TEST'
##	gpd  = GetPopDeliver(fldr)
##
##	m=0
##	for i in xrange(1452, 1552):
##		gpd.transfer(None, dumptextfile('y:/data/email/softhome/inbox-%d'%1552, true))
##		if m == 16:
##			os.system('command /c @rmm')
##			m=0
##		m = m+1
		
if __name__ == '__main__':
	test()
# eof
