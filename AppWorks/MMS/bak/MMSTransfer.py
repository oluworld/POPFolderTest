import string
from etoffiutils import *

# filterspec: <header> <rule> <action>
# header: 'Subject' ...
# rule: ['not'] 'contains'|'beginswith'|'endswith'
# action: 'moveto' <foldername>|'delete'

class GetPopDeliver:
	def __init__(self, src):
#		dbi = AwxDBi()
#		self.fn = dbi.getStr('filterfile', src.path)
#		self.fr = dbi.getStr('folderroot', src.path)
#		dbi.close() ## wrong!! AwxDBi is modeless!!

		self.fn = '%s/Filters.dat' % src
		self.fr = src

		l = dumptextfile(self.fn, true)

		ll = []
		for each in l:
			ll.append(string.split(each))

		self.filters = ll

	def transfer(self, complaintbox, msg):
		self.ApplyFilters(self.filters, msg, complaintbox)

	def ApplyFilters(self, filters, msg, ui):
		# Subject contains litestep moveto litestep
		for each in filters:
			h = self.GetHeader(each[0], msg)
			if h == None:
				self.writeMsg(msg)
				return

			if self.GetMatch(h, each[1:], msg):
				self.Do(each[3:], msg)
				return

	def GetHeader(self, lookfor, msg):
		for each in msg:
			if nequals(string.lower(each), string.lower(lookfor)):
				return each[len(lookfor)+2:]

		return None

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
			if string.find(header, rule[cur]):
				return true
			else:
				return false

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

	def writeMsg(self, msg, folder='root'):
		import time

		fp = '%s/%s' % (self.fr, folder)
		ensure_directory_present(fp)

		nm = self.GetNext('%s/.next' % fp)
		f = open('%s/%d.msg' % (fp, nm), 'a+')
		f.write("From - %s\012%s\012" % (time.ctime(time.time()), string.join(msg, '\012')))
		f.close()
##		f = open('%s/.next' % fp, 'w')
##		f.write('%d\012' % int(nm)+1)
##		f.close()

	def GetNext(self, Folder):
		mn = 0
		try:
			mnf = open(Folder)
			llp = mnf.readline()[:-1]
			if llp:
				mn = int(llp)
			else:
				mn = 0
			
			mnf.close()
		except Exception, e:
			print e
		inc_until_nofile(mn)
		try:
			mnf = open(Folder, 'w')
			mpn = mn+1
			mnf.write('%d\012' % mpn)
			mnf.close()
		except Exception, e:
			print e

		return mn

	def Do(self, Action, msg):
		if Action[0] == 'moveto':
			self.writeMsg(msg, Action[1])

		if Action[0] == 'copyto':
			self.writeMsg(msg)
			self.writeMsg(msg, Action[1])

		if Action[0] == 'delete':
			pass

def test():
	fldr = 'y:/data/email/TEST'
	gpd  = GetPopDeliver(fldr)
	gpd.transfer(None, dumptextfile('y:/data/email/softhome/inbox-1552', true))
		
if __name__ == '__main__':
	test()
# eof
