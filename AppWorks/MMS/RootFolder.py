from Folder import MMSFolder

class RootFolder(MMSFolder):
	def __init__(self, name, root):
		MMSFolder.__init__(name, root)
		self.transfer = MMSTransfer(name, AwxDBi())

	def store(self, msg, put_fromline):

		for msgs in xrange(1, newmsgs + 1):
			msgid = src.getMsgId(msgs)
			if not self.transfer.findMsgByServerId(msgid):
				self.ui.tell("Retrieving %d of %d" % (msgs, newmsgs) )
				msg = src.getMsg(msgs, self.deleting)

#				if not msg:
#					raise errEmptyMessage(self, src) # "MMSMailChecker::checkmail"

				self.onGetMsg(msg)	
	def onGetMsg(self, msg):
		xfolder, verb = self.transfer.getActionForMessage(msg, self.ui)
		if verb == None:
			self.transfer.writeMsg(msg)
		else:
			folder = self.transfer.getFolder(xfolder)
			self.transfer.Do( (verb, xfolder), msg)
	
#eof
