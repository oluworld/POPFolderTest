from BaseChecker import *
	
class NewsChecker(BaseChecker):
	def checkmail(self, getting = 1):
		self._int_checkmail('~/MailSystem/News/Sources', getting)
		
# eof