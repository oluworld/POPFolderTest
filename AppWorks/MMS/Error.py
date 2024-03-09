class MMSLoginError(Exception):
	def __init__(self, reason):
		self.reason = reason
	def what(self):
		return self.reason

