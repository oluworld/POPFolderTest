from BasicMessage import MMSMessage
from etoffiutils import ensure_directory_present, true, false, \
	quickAppend, add_to_head, oixfs_encode
import os
#from BackupFile import *
	
class MMSFolder:
	def makename(self, msg_id):
		rv = self.makename2(self.root, self.name, oixfs_encode(`msg_id`[1:-1]))
		return rv
	def makename2(self, msg_id):
		rv = '%s/%s/%s' % (self.root, self.name, msg_id)
		return rv
			
	def store(self, msg, put_fromline):
		quickAppend('tmpfile', msg.msg, true)
		
		fn = self.makename(msg.uidl)
		k = add_to_head("X-UIDL: %s" % msg.uidl, msg.msg)
		if put_fromline:
			import time
			k = add_to_head("From - %s" % time.ctime(time.time()) , msg.msg)
		quickAppend(fn, k, true)
		quickAppend(self.makename2('index'), ['%s\t%s\t%s' % (msg.uidl, fn, '')], true)
		
	def is_stored(self, id):
		if os.path.isfile(self.makename(id)):
			return true
		else:
			return false
		
	def __init__(self, name, root):
		self.name = name
		self.root = root
		ensure_directory_present( '%s/%s' % (root, name) )

def test():
	print fixname('<hello nurse !!%%@bbui>')
if __name__ == '__main__':
	test()

#eof
