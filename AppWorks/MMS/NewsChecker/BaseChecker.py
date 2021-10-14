from AppWorks.Util.AwxDBi import *
from MMS.Transfer import *
from MMS.UI import *
from AppWorks.Services.AwxDBiServer import *
from MMS import * #__Eall__

class BaseChecker:
	def _int_checkmail(self, dbiroot, getting):
		def strsplit(instr, splitby):
			pos = instr.rfind(splitby)
			return instr[:pos], instr[pos+1:]
		# --
		Sources = self.myDBi.enum(dbiroot, AwxDBi.EnumFlat)
		# --
		for each in Sources:
			root, acct_name = strsplit(each.path, '/')
			self.myDBi.setRoot(each.path)
			self.check_account(acct_name, getting)
		# --
		self.ui.tell('Finished checking all sources')

	def __init__(self, deleting = 1, ui = None):
		self.deleting = deleting
		self.myDBi = AwxDBi()
#		self.myDBi = DBiServer
		if ui == None:
			self.ui = NullUI() #ConsoleUI()
		else:
			self.ui = ui
			
	def check_account(self, acct_name, getting):
		"""
			
			
		"""
		#
		# Stage 1: Attempt to get an MMSSource for the current server [each]
		#
		source_name = self.myDBi.getStr('name')
		clazz = ("MMS%sSource" % self.myDBi.getStr( 'type' ))
		src = MMSSourceFactory(clazz)

		if not src:
			self.ui.abort("No such SourceType: " + clazz)
			return

		#
		# Stage 2: Inform this generic source with the server's info
		#          and skip if the source is marked as not active.
		#          Otherwise, we will attempt login
		#
		try:
			# enum the current folder (./ is not impl yet, so use blank)
			src.setFlags(self.myDBi.enum('', AwxDBi.EnumRecursive))
			if not src.ACTIVE in ['false', 0, false]:
				uimsg = 'skipping ' + source_name + '(not active)'
				self.ui.tell(uimsg)
				return 
			uimsg = 'getting mail from ' + source_name
			self.ui.tell(uimsg)
			src.login(self.ui, getting)
		except MMSLoginError, e:
			self.ui.abort(e.what())
			return

		#
		# Stage 3: Tell the user how many messages are available
		#          Skip server if there are no messages.
		#
		newmsgs, totmsgs = src.getMessageCount()

		if newmsgs == 0:
			self.ui.tell('No new messages. %d old messages' % totmsgs)
			return
		elif newmsgs == 1:
			self.ui.tell('1 new message')
		else:
			self.ui.tell('%d new messages' % newmsgs)

		#
		# Stage 4: Retrieve the messages, letting the user know the status 
		#          after each retrieval
		#
		if getting:
			T = MMSTransfer(acct_name, self.myDBi) ## who knows if this is right?

			for msgs in xrange(1, newmsgs + 1):
				msgid = src.getMsgId(msgs)
				if not T.findMsgByServerId(msgid):
					self.ui.tell("Retrieving %d of %d" % (msgs, newmsgs) )
					msg = src.getMsg(msgs, self.deleting)
					self.onGetMsg(T, msg)
				else:
					pass
					#TODO:
##					self.ui.tell("Removing %d of %d" % (msgs, newmsgs) )
##					src.removeMsg(msgs)
				
		#
		# Stage 5: Tell the user we are finished and logoff the server by
		#          ``finalizing" the source
		#
		self.ui.tell('Finished ' + uimsg)
		src.finalize()

	def onGetMsg(self, T, msg):
		xfolder, verb = T.getActionForMessage(msg, self.ui)
		if verb == None:
			T.writeMsg(msg)
		else:
			folder = T.getFolder(xfolder)
			T.Do( (verb, xfolder), msg)

#eof
